namespace MyThreadPool;

using System;

/// <summary>
/// Unit of program execution in the context of thread pool
/// </summary>
public interface IMyTask<TResult>
{
    /// <summary>
    /// Returns true is task is completed.
    /// </summary>
    public bool IsCompleted { get; }

    /// <summary>
    /// Returns task result.
    /// </summary>
    public TResult Result { get; }

    /// <summary>
    /// takes an object of type Func<TResult, TNewResult>, which can
    /// be applied to the result of a given task X and returns a new task Y
    /// </summary>
    public IMyTask<TNewResult> ContinueWith<TNewResult>(Func<TResult, TNewResult> func);
}