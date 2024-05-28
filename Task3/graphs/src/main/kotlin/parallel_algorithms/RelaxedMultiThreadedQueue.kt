package org.example

import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock
import kotlin.random.Random
import org.example.Node
import org.example.Edge

class RelaxedMultiThreadedQueue(private var numberOfQueues: Int) {
    private val queues = Array(numberOfQueues) { PriorityBlockingQueue<Node>() }
    private val locks = Array(numberOfQueues) { ReentrantLock() }
    private val sizes = Array(numberOfQueues) { AtomicInteger(0) }

    fun add(element: Node) {
        val queueIndex = Random.nextInt(numberOfQueues)
        queues[queueIndex].add(element)
        sizes[queueIndex].incrementAndGet()
    }

    fun poll(): Node? {
        var smallestQueueIndex = -1
        var smallestNode: Node? = null
        for (i in 0 until numberOfQueues) {
            locks[i].lock()
            try {
                val node = queues[i].peek()
                if (node != null && (smallestNode == null || node.distance < smallestNode.distance)) {
                    smallestNode = node
                    smallestQueueIndex = i
                }
            } finally {
                locks[i].unlock()
            }
        }
        if (smallestQueueIndex == -1) return null

        locks[smallestQueueIndex].lock()
        return try {
            val node = queues[smallestQueueIndex].poll()
            if (node != null) sizes[smallestQueueIndex].decrementAndGet()
            node
        } finally {
            locks[smallestQueueIndex].unlock()
        }
    }

    fun isEmpty(): Boolean {
        return sizes.all { it.get() == 0 }
    }
}
