package org.example

import kotlin.concurrent.thread
import org.example.Node
import org.example.Edge

class ParallelDijkstra {
    fun runAlgorithm(graph: List<List<Edge>>, source: Int, numberOfThreads: Int): IntArray {
        val distances = IntArray(graph.size) { Int.MAX_VALUE }
        distances[source] = 0

        val rmq = RelaxedMultiThreadedQueue(numberOfThreads)
        rmq.add(Node(source, 0))

        val threads = Array(numberOfThreads) {
            thread {
                while (!rmq.isEmpty()) {
                    val currentNode = rmq.poll() ?: continue
                    val currentDistance = currentNode.distance
                    if (currentDistance > distances[currentNode.id]) continue

                    for (edge in graph[currentNode.id]) {
                        val newDist = currentDistance + edge.weight
                        if (newDist < distances[edge.to]) {
                            synchronized(distances) {
                                if (newDist < distances[edge.to]) {
                                    distances[edge.to] = newDist
                                    rmq.add(Node(edge.to, newDist))
                                }
                            }
                        }
                    }
                }
            }
        }

        threads.forEach { it.join() }
        return distances
    }
}