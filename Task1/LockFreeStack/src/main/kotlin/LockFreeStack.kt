import java.util.concurrent.atomic.AtomicReference

class LockFreeStack<T> {
    private class Node<T> (val value: T, val next: Node<T>?)

    private var h = AtomicReference<Node<T>?>(null)

    fun pop(): T? {
        while (true) { //Cas loop
            val head = h.get() ?: return null
            if (h.compareAndSet(head, head.next)) {
                return head.value
            }
        }
    }

    fun push(x: T) {
        while (true) {
            val head = h.get()
            val newHead = Node (x, head)
            if (h.compareAndSet(head, newHead)) {
                return
            }
        }
    }

    fun peek() : T? {
        return (h.get())?.value
    }
}