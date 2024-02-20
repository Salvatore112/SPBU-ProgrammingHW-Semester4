namespace Triber_Stack;

public class EliminationBackoffStack
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

    // 0 - Empty
    // 1 - LoadingIteam
    // 2 - Waiting
    // 3 - Busy

    private class Exchanger
    {
        public int State;
        public int? Item;

        public Exchanger(int state, int? item)
        {
            State = state;
            Item = item;
        }
    }

    List<Exchanger> eliminationArray = new List<Exchanger>();

    private bool tryPushEliminationArray(int x)
    {
        foreach (Exchanger exchanger in eliminationArray)
        {
            if (Interlocked.CompareExchange(ref exchanger.State, 0, 1) == 0)
            {
                exchanger.Item = x;
                exchanger.State = 2;

                Thread.Sleep(1000);

                if (Interlocked.CompareExchange(ref exchanger.State, 2, 3) == 2)
                {
                    exchanger.State = 0;
                    return false;

                } else
                {
                    exchanger.State = 0;
                    return true;
                }
            }
           
        }
        return false;
    }

    /// <summary>
    /// Function that remembers current head and tris to change it to
    /// the next element by CompareExchange method
    /// </summary>
    /// <returns>Current head value</returns>
    public int? Pop()
    {
        while (true)
        {
            StackNode? head = H;
            if (Interlocked.CompareExchange(ref H, head.Next, head) == head)
            {
                return head.Value;
            }
            else
            {
                var pair = tryPopEliminationArray();
                if (pair.Item2)
                {
                    return pair.Item1;
                }
            }
                
        }
    }

    private (int?, bool) tryPopEliminationArray()
    {
        foreach (Exchanger exchanger in eliminationArray)
        {
            var item = exchanger.Item;
            if (item != null && Interlocked.CompareExchange(ref exchanger.State, 2, 3) == 2)
            {
                return (item, true);
            }
        }
        return (0, false);
    }
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
            } else if (tryPushEliminationArray(newValue))
            {
                return;
            }
        }
    }

    /// <returns>Current head value</returns>
    public int Peek() => H.Value;

    public EliminationBackoffStack(int exchengerAmount)
    {
        this.H = new StackNode();

        for (int i = 0; i < exchengerAmount; i++)
        {
            eliminationArray.Add(new Exchanger(1, null));
        }
    }
}
