namespace Triber_Stack;

public class TriberStack
{
    private StackNode? H;
    
    private class StackNode
    {
        public StackNode? next { get; set; }
        public int value { get; set; }

        public StackNode(int value, StackNode nextNode)
        {
            this.next = nextNode;
            this.value = value;
        }

        public StackNode() { }
    }

    public int Pop() 
    {
        while (true)
        {
            StackNode? head = H;
            if (Interlocked.CompareExchange(ref H, head.next, head) != head.next)
            {
                return head.value;
            }
        }
    }

    public int Peek() => H.value;

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
            if (Interlocked.CompareExchange(ref H, newHead, head) != newHead)
            {
                return;
            }
        }
    }

    public TriberStack()
    {
        this.H = new StackNode();
    }
}
