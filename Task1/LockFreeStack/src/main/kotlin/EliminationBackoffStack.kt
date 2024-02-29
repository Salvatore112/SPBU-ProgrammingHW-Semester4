import java.util.concurrent.atomic.AtomicReference
import java.lang.RuntimeException

class EliminationBackoffStack<T> (private val maxCounter : Int = 100, private val eliminationArraySize: Int = 6) {
    private class Node<T> (val value: T?, val next: Node<T>?)

    private var h = AtomicReference<Node<T>?>(null)

    private enum class State {
        Empty, Busy, Waiting, LoadingItem
    }

    private fun randomArrayIndex(arraySize: Int) = (0..<arraySize).random()

    private class Exchanger<T> {
        var state = AtomicReference(State.Empty)
        var value: T? = null
    }

    private val eliminationArray = Array<Exchanger<T>>(eliminationArraySize) {Exchanger()}

    fun push(x: T) {
        while (true) {
            val head = h.get()
            val newHead = Node (x, head)
            if (h.compareAndSet(head, newHead)) {
                return
            } else if (tryPushEliminationArray(x)) {
                return
            }
        }
    }

    private fun tryPushEliminationArray(value: T): Boolean {
        var counter = 0
        while (counter != maxCounter) {
            val randomExchanger = eliminationArray[randomArrayIndex(eliminationArraySize)]

            if (randomExchanger.state.compareAndSet(State.Empty, State.LoadingItem)) {
                randomExchanger.value = value
                randomExchanger.state = AtomicReference(State.Waiting)

                Thread.sleep(1000)

                if (randomExchanger.state.compareAndSet(State.Waiting, State.Busy)) {
                    randomExchanger.state = AtomicReference(State.Empty)
                    return false
                } else {
                    randomExchanger.state = AtomicReference(State.Empty)
                    return true
                }
            }
            counter ++
        }
        return false
    }

    private fun tryPop(): Pair<Node<T>?, Boolean> {
        val head = h.get()
        if (h.compareAndSet(head, head?.next)) {
            return Pair(head, true)
        }
        return Pair(null, false)
    }

    private fun tryPopEliminate(): T? {
        var counter = 0
        while (counter != maxCounter) {
            val randomExchanger = eliminationArray[randomArrayIndex(eliminationArraySize)]

            if (randomExchanger.state.compareAndSet(State.Waiting, State.Busy)) {
                val takenValue = randomExchanger.value
                randomExchanger.value = null
                if (!randomExchanger.state.compareAndSet(State.Busy, State.Empty)) {
                    throw RuntimeException("Couldn't change an exchanger's state")
                }
                return takenValue
            }
            counter++
        }
        return null
    }

    fun pop(): T? {
        var counter = 0

        while (true) {
            val (node, casResult) = tryPop()

            if (casResult) {
                if (node != null) {
                    return node.value
                }
                if (counter == maxCounter) throw RuntimeException("Too many attempts to pop an element")

                counter++
                Thread.sleep(1000)
            }

            val popAttempt = tryPopEliminate()
            if (popAttempt != null) {
                return popAttempt
            }
        }
    }

    fun peek() : T? {
        return (h.get())?.value
    }
}