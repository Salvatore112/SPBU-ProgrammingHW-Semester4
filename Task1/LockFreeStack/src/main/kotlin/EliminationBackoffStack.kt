import java.util.concurrent.atomic.AtomicReference
import java.util.EmptyStackException
import kotlin.random.Random
import kotlinx.atomicfu.atomicArrayOfNulls

class EliminationBackoffStack<T> (private val maxCounter : Int = 100, private val eliminationArraySize: Int = 10) {
    private class Node<T> (val value: T, val next: Node<T>?)

    private class Exchanger<T> (var value: T)

    private val head = AtomicReference<Node<T>?>(null)

    private val eliminationArray = atomicArrayOfNulls<Exchanger<T>?>(eliminationArraySize)

    private fun eliminationPush(value: T) {
        val randomExchanger = eliminationArray[Random.nextInt(eliminationArraySize)]
        if (randomExchanger.value == null) {
            if (randomExchanger.compareAndSet(null, Exchanger(value))) {
                if (randomExchanger.getAndSet(null) == null) {
                    return
                }
            }
        }
        push(value) // Trying to push again
    }

    fun push(value: T) {
        val currentHead = head.get()
        val newHead = Node(value, currentHead)
        if (head.compareAndSet(currentHead, newHead)) {
            return
        } // Trying to push once the old way
        eliminationPush(value) // If cas failed we try to push through eliminationArray
    }

    fun pop(): T {
        val currentHead = head.get() ?: throw EmptyStackException()
        if (head.compareAndSet(currentHead, currentHead.next) && currentHead.value != null) {
            return currentHead.value
        } // Trying to pop without elimination once

        val randomEliminationArrayIndex = Random.nextInt(eliminationArraySize)
        var counter = 0
        while (counter < maxCounter) {
            val randomExchanger = eliminationArray[randomEliminationArrayIndex].value
            if (randomExchanger != null) {
                if (eliminationArray[randomEliminationArrayIndex].compareAndSet(randomExchanger, null)) {
                    return randomExchanger.value
                }
            }
            counter++
        } // Trying to pop with elimination up to maxCounter times

        while (true) {
            val currentHead = head.get() ?: throw EmptyStackException()
            if (head.compareAndSet(currentHead, currentHead.next)) {
                return currentHead.value
            }
        } // Trying to pop with cas loop till it's done
    }

    fun peek(): T? {
        return head.get()?.value ?: return null
    }
}