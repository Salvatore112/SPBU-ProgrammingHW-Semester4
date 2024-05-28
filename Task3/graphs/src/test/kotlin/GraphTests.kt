import org.example.Edge
import org.example.Node
import org.example.FloydWarshallParallel
import org.example.ParallelDijkstra
import org.example.SequentialDijkstra
import org.example.SequentialFloydWarshall
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals



class GraphTests {
    val testGraph = listOf(
        listOf(Edge(1, 1), Edge(2, 7), Edge(3, 2)),
        listOf(Edge(2, 3)),
        listOf(Edge(4, 1), Edge(3, 5)),
        listOf(Edge(4, 7)),
        listOf()
    )
    val numThreads = 4

    val dj_seq = SequentialDijkstra()
    val dj_par = ParallelDijkstra()
    val fl_seq = SequentialFloydWarshall()
    val fl_par = FloydWarshallParallel()
    val expectedPaths = arrayOf(0, 1, 4, 2, 5)

    @Test
    fun sequentialDijkstraTest() {
        val paths = dj_seq.run_algorithm(testGraph, 0)
        for (i in 0 until testGraph.size) {
            assertEquals(paths[i], expectedPaths[i])
        }

    }

    @Test
    fun parallelDijkstraTest() {
        val source = 0
        val numberOfThreads = 4
        val paths = dj_par.runAlgorithm(testGraph, source, numberOfThreads)
        for (i in 0 until testGraph.size) {
            assertEquals(paths[i], expectedPaths[i])
        }
    }

    @Test
    fun parallelFloydTest() {
        val paths = fl_par.run_algorithm(testGraph, numThreads)
        for (i in 0 until testGraph.size) {
            assertEquals(paths[0][i], expectedPaths[i])
        }
    }

    @Test
    fun sequentialFloydTest() {
        val paths = fl_seq.run_algorithm(testGraph)
        for (i in 0 until testGraph.size) {
            assertEquals(paths[0][i], expectedPaths[i])
        }
    }

}