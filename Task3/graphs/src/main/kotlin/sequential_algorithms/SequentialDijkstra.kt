package org.example
import org.example.Edge
import org.example.Node
import java.util.PriorityQueue

class SequentialDijkstra {
    fun run_algorithm(graph: List<List<Edge>>, source: Int): Map<Int, Int> {
        val n = graph.size
        val dist = MutableList(n) { Int.MAX_VALUE }
        val visited = BooleanArray(n)
        val pq = PriorityQueue<Node>()

        dist[source] = 0
        pq.offer(Node(source, 0))

        while (pq.isNotEmpty()) {
            val curr = pq.poll()
            val u = curr.id

            if (visited[u]) continue

            visited[u] = true

            for (edge in graph[u]) {
                val v = edge.to
                val weight = edge.weight

                if (dist[v] > dist[u] + weight) {
                    dist[v] = dist[u] + weight
                    pq.offer(Node(v, dist[v]))
                }
            }
        }

        return dist.mapIndexed { index, distance ->
            index to distance
        }.toMap()
    }
}
