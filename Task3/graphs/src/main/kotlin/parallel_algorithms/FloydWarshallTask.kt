package org.example

import java.util.concurrent.Callable

class FloydWarshallTask(
    private val dist: Array<IntArray>,
    private val k: Int,
    private val startRow: Int,
    private val endRow: Int
) : Callable<Void?> {
    override fun call(): Void? {
        for (i in startRow until endRow) {
            for (j in dist.indices) {
                if (dist[i][k] != Int.MAX_VALUE && dist[k][j] != Int.MAX_VALUE) {
                    val newDist = dist[i][k] + dist[k][j]
                    if (newDist < dist[i][j]) {
                        dist[i][j] = newDist
                    }
                }
            }
        }
        return null
    }
}