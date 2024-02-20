namespace Triber_Stack;

/// <summary>
/// scalable lock-free stack utilizing the fine-grained
/// concurrency primitive compare-and-swap.
/// </summary>
public class TriberStack
{
    private StackNode? H;
    
    private class StackNode
    {
        public StackNode? Next { get; set; }
        public int Value { get; set; }

        public StackNode(int value, StackNode nextNode)
        {
            this.Next = nextNode;
            this.Value = value;
        }
        
        public StackNode() { }
    }

    /// <summary>
    /// Function that remembers current head and tris to change it to
    /// the next element by CompareExchange method
    /// </summary>
    /// <returns>Current head value</returns>
    public int Pop() 
    {
        while (true)
        {
            StackNode? head = H;
            if (Interlocked.CompareExchange(ref H, head.Next, head) == head)
            {
                return head.Value;
            }
        }
    }

    /// <returns>Current head value</returns>
    public int Peek() => H.Value;

    /// <summary>
    /// Function that creates a new node and tries to push it onto
    /// the stack by CompareExchange method
    /// </summary>
    /// <param name="newValue">Value you want to push to the stack</param>
    /// <exception cref="NullReferenceException"></exception>
    public void Push(int newValue)
    {
        while (true) 
        {  
            StackNode? head = H;
            if (head == null)
            {
                throw new NullReferenceException();
            }

            var newHead = new StackNode(newValue, head);
            if (Interlocked.CompareExchange(ref H, newHead, head) == head)
            {
                return;
            }
        }
    }

    public TriberStack()
    {
        this.H = new StackNode();
    }

    public class IntWrapper
    {
        public int test;
        public IntWrapper(int test)
        {
            this.test = test;
        }
    }
}
