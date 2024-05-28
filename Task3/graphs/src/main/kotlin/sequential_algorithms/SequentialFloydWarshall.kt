package org.example

import org.example.Node
import org.example.Edge

class SequentialFloydWarshall {
    fun run_algorithm(graph: List<List<Edge>>): Array<IntArray> {
        val n = graph.size
        val dist = Array(n) { IntArray(n) { Int.MAX_VALUE / 2 } }

        // Initialize distances with edge weights
        for (u in 0 until n) {
            for (edge in graph[u]) {
                val v = edge.to
                val weight = edge.weight
                dist[u][v] = weight
            }
            dist[u][u] = 0
        }

        // Floyd-Warshall algorithm
        for (k in 0 until n) {
            for (i in 0 until n) {
                for (j in 0 until n) {
                    if (dist[i][k] != Int.MAX_VALUE / 2 && dist[k][j] != Int.MAX_VALUE / 2 &&
                        dist[i][j] > dist[i][k] + dist[k][j]
                    ) {
                        dist[i][j] = dist[i][k] + dist[k][j]
                    }
                }
            }
        }

        return dist
    }
}