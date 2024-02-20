using Triber_Stack;
using static Triber_Stack.TriberStack;

var stack = new TriberStack();

stack.Push(1);
stack.Push(2);
stack.Pop();

Console.WriteLine(stack.Peek());

var stackBackOFf = new EliminationBackoffStack(10);
stackBackOFf.Push(3);
stackBackOFf.Push(2);
stackBackOFf.Pop();
Console.WriteLine(stackBackOFf.Peek());