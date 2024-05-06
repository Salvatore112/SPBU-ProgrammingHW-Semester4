namespace MyThreadPool;

using System.Collections.Concurrent;
using System.Collections.Generic;
using System.Threading;

/// <summary>
/// Software design pattern for achieving concurrency of execution in a computer program
/// </summary>
public class ThreadPool
{
    readonly private int taskCompletionTimeLimit;
    readonly private ThreadPoolThread[] threads;
    readonly private CancellationTokenSource cancellationTokenSource;
    readonly private ManualResetEvent thereAreTasksInQueue;
    readonly private ManualResetEvent disposalEvent;
    readonly private AutoResetEvent newTaskGotSubmitted;
    private WaitHandle[] waitHandlesForThreadLoop;
    public ConcurrentQueue<Action> TasksQueue { get; private set; }

    /// <summary>
    /// A number of available for work threads
    /// </summary>
    public int NumberOfThreads { get; }

    /// <summary>
    /// Flag that shows whether the thread pool is being disposed of at the moment.
    /// </summary>
    public bool IsBeingDisposedOf { get; private set; }

    /// <summary>
    /// Gets a value indicating whether all tasks given to the ThreadPool are being computed.
    /// </summary>
    public bool NoTasksInQueue => TasksQueue.Count == 0;

    /// <summary>
    /// Property to check how many threads were added during thread pool initialization
    /// </summary>
    public int NumberOfSuccessfullyAddedThreads { get; private set; }

    /// <summary>
    /// Add a new task to the queue
    /// </summary>
    public IMyTask<TResult> Enqueue<TResult>(Func<TResult> function, ManualResetEvent? upperTaskIsFinished = null)
    {

        if (cancellationTokenSource.IsCancellationRequested)
        {
            throw new InvalidOperationException("Thread pool is no longer accepting any new tasks");
        }

        var newTask = upperTaskIsFinished == null ? new MyTask<TResult>(function, this) : new MyTask<TResult>(function, this, upperTaskIsFinished);
        TasksQueue.Enqueue(() => newTask.Evaluate());
        newTaskGotSubmitted.Set();
        return newTask;
    }

    /// <summary>
    /// Method that terminates all threads
    /// </summary>
    public void Dispose()
    {
        IsBeingDisposedOf = true;
        cancellationTokenSource.Cancel();
        disposalEvent.Set();
        foreach (ThreadPoolThread thread in threads)
        {
            thread.ThreadJoin(taskCompletionTimeLimit);
            if (!thread.Idle)
            {
                throw new Exception("Task completion took longer than it was allowed");
            }
        }
    }

    private class ThreadPoolThread
    {
        private readonly Thread threadItself;
        private readonly ThreadPool threadPool;
        private Action? task;

        public bool Idle { get; set; } = true;

        public async void TaskExecutionLoop(CancellationToken cancellationToken)
        {
            while (!cancellationToken.IsCancellationRequested)
            {
                WaitHandle.WaitAny(threadPool.waitHandlesForThreadLoop);
                if (cancellationToken.IsCancellationRequested)
                {
                    return;
                }

                lock (threadPool.thereAreTasksInQueue)
                {
                    if (!threadPool.NoTasksInQueue)
                    {
                        threadPool.thereAreTasksInQueue.Set();
                    }
                    else
                    {
                        threadPool.thereAreTasksInQueue.Reset();
                    }
                }

                if (!threadPool.NoTasksInQueue)
                {
                    var taskWasFetched = threadPool.TasksQueue.TryDequeue(out task);
                    if (taskWasFetched && task != null)
                    {
                        Idle = false;
                        task();
                        Idle = true;
                    }
                }

                lock (threadPool.thereAreTasksInQueue)
                {
                    if (!threadPool.NoTasksInQueue)
                    {
                        threadPool.thereAreTasksInQueue.Set();
                    }
                    else
                    {
                        threadPool.thereAreTasksInQueue.Reset();
                    }
                }
            }
        }

        public void ThreadJoin(int threadJoinTimelimit) => threadItself.Join(threadJoinTimelimit);

        public ThreadPoolThread(ThreadPool threadPool, CancellationToken token)
        {
            this.threadPool = threadPool;
            threadItself = new Thread(() => TaskExecutionLoop(token));
            threadItself.Start();
        }
    }

    /// <inheritdoc cref="IMyTask{TResult}"/>
    private class MyTask<TResult> : IMyTask<TResult>
    {
        readonly private bool taskIsContinuation;
        readonly private Func<TResult> func;
        private TResult? result;

        readonly private List<Action> AllContinuations;

        readonly private ManualResetEvent accessToResult;
        readonly private ManualResetEvent? isUpperTaskCompleted;
        readonly private ManualResetEvent continuationsCanBeEvaluated;

        private Exception? aggregateExceptionDescrtiption;
        readonly private ThreadPool threadPool;

        public MyTask(Func<TResult> function, ThreadPool myThreadPool, ManualResetEvent? upperTaskEvent = null)
        {
            func = function;
            AllContinuations = new List<Action>();
            accessToResult = new ManualResetEvent(false);
            threadPool = myThreadPool;
            taskIsContinuation = upperTaskEvent != null ? true : false;
            isUpperTaskCompleted = upperTaskEvent;
            continuationsCanBeEvaluated = new ManualResetEvent(false);
        }

        /// <inheritdoc cref="IMyTask{TResult}"/>
        public bool IsCompleted { get; private set; }

        /// <inheritdoc cref="IMyTask{TResult}"/>
        public TResult Result
        {
            get
            {
                if (threadPool.IsBeingDisposedOf && !IsCompleted)
                {
                    throw new Exception("Function wasn't able to complete before disposal");
                }

                accessToResult.WaitOne();

                if (aggregateExceptionDescrtiption != null)
                {
                    throw new AggregateException(aggregateExceptionDescrtiption);
                }

                return result!;
            }
        }

        /// <inheritdoc cref="IMyTask{TResult}"/>
        public IMyTask<TNewResult> ContinueWith<TNewResult>(Func<TResult, TNewResult> func)
        {
            lock (AllContinuations)
            {
                if (result != null)
                {
                    return threadPool.Enqueue(() => func(Result), continuationsCanBeEvaluated);
                }

                var continuationTask = new MyTask<TNewResult>(() => func(Result), threadPool, continuationsCanBeEvaluated);

                AllContinuations.Add(() => continuationTask.Evaluate());

                return continuationTask;
            }
        }

        public void Evaluate()
        {
            try
            {
                if (taskIsContinuation)
                {
                    isUpperTaskCompleted!.WaitOne();
                }

                result = func();

                lock (AllContinuations)
                {
                    if (AllContinuations.Count > 0)
                    {
                        foreach (var continuationTaskAction in AllContinuations)
                        {
                            threadPool.Enqueue(() => continuationTaskAction, isUpperTaskCompleted);
                        }
                    }
                }
            }
            catch (Exception exception)
            {
                aggregateExceptionDescrtiption = exception;
            }

            IsCompleted = true;
            accessToResult.Set();
            continuationsCanBeEvaluated.Set();
        }
    }

    public ThreadPool(int numberOfThreads, int threadJoinTime = 3000)
    {
        if (numberOfThreads <= 0)
        {
            throw new ArgumentException();
        }

        NumberOfThreads = numberOfThreads;

        TasksQueue = new ConcurrentQueue<Action>();
        threads = new ThreadPoolThread[numberOfThreads];
        cancellationTokenSource = new CancellationTokenSource();
        thereAreTasksInQueue = new ManualResetEvent(false);
        disposalEvent = new ManualResetEvent(false);
        newTaskGotSubmitted = new AutoResetEvent(false);
        waitHandlesForThreadLoop = new WaitHandle[3]
        {
            newTaskGotSubmitted,
            thereAreTasksInQueue,
            disposalEvent,
        };

        for (var i = 0; i < numberOfThreads; i++)
        {
            threads[i] = new ThreadPoolThread(this, cancellationTokenSource.Token);
            NumberOfSuccessfullyAddedThreads++;
        }

        taskCompletionTimeLimit = threadJoinTime;
    }
}
