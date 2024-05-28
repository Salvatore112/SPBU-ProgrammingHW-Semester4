package org.example

import org.example.Edge
import java.util.concurrent.Callable
import java.util.concurrent.ForkJoinPool

class FloydWarshallParallel {
    fun run_algorithm(graph: List<List<Edge>>, numThreads: Int): Array<IntArray> {
        val n = graph.size
        val dist = Array(n) { IntArray(n) { Int.MAX_VALUE } }

        for (i in 0 until n) {
            dist[i][i] = 0
            for (edge in graph[i]) {
                dist[i][edge.to] = edge.weight
            }
        }

        val pool = ForkJoinPool(numThreads)

        for (k in 0 until n) {
            val tasks = mutableListOf<Callable<Void?>>()
            val chunkSize = (n + numThreads - 1) / numThreads

            for (startRow in 0 until n step chunkSize) {
                val endRow = minOf(startRow + chunkSize, n)
                tasks.add(FloydWarshallTask(dist, k, startRow, endRow))
            }

            pool.invokeAll(tasks)
        }

        pool.shutdown()

        return dist
    }
}