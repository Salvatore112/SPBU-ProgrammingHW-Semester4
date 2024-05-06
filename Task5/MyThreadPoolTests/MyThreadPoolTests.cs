using MyThreadPool;

namespace MyThreadPoolTests.Tests;
public class MyThreadPoolTests
{
    private int threadCount = 8;
    private MyThreadPool.ThreadPool threadPool;

    private int fibonacci(int n, int acc = 0, int prev = 1) => n == 0 ? acc : fibonacci(n - 1, acc + prev, acc);

    private int factorial(int n) => n == 0 ? 1 : factorial(n - 1) * n;

    [SetUp]
    public void Initialize()
    {
        threadPool = new MyThreadPool.ThreadPool(threadCount);
    }

    [Test]
    public void ThreadPoolGotTheSpecifiedNumberOfThreads()
    {
        threadPool.Dispose();
        Assert.That(threadPool.NumberOfSuccessfullyAddedThreads, Is.EqualTo(threadCount));
    }

    [Test]
    public void AddingOneTaskTest()
    {
        var factorialTaskthreadPool = threadPool.Enqueue(() => factorial(7));
        threadPool.Dispose();
        Assert.That(factorialTaskthreadPool.Result, Is.EqualTo(5040));
    }

    [Test]
    public void SimpleCPSTest()
    {
        var factorialTaskthreadPool = threadPool.Enqueue(() => fibonacci(6)).ContinueWith(x => factorial(x));
        threadPool.Dispose();
        Assert.That(factorialTaskthreadPool.Result, Is.EqualTo(40320));
    }

    [Test]
    public void TheNumberOfTasksIsBiggerThanTheNumberOfThreads()
    {
        var tasks = new IMyTask<int>[threadCount * 2];
        for (var i = 0; i < threadCount * 2; i++)
        {
            tasks[i] = threadPool.Enqueue(() => fibonacci(5));
        }

        var expectedResults = new int[threadCount * 2];
        var results = new int[threadCount * 2];
        for (var j = 0; j < threadCount * 2; j++)
        {
            results[j] = tasks[j].Result;
            expectedResults[j] = 5;
        }

        threadPool.Dispose();
        Assert.That(results, Is.EqualTo(expectedResults));
    }

    [Test]
    public void MoreThanOneContinueWithTest()
    {
        int testFunction()
        {
            return 3;
        }

        var testTask = threadPool.Enqueue(() => testFunction()).ContinueWith(x => x * 3).ContinueWith(x => x + 3);
        threadPool.Dispose();
        Assert.That(testTask.Result, Is.EqualTo(12));
    }
}