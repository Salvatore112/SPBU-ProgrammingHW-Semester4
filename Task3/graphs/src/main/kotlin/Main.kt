package org.example

import kotlin.random.Random
import kotlin.system.measureTimeMillis
import java.io.File
import java.nio.file.Paths
fun generateRandomGraph(n: Int): List<List<Edge>> {
    val graph = List(n) { mutableListOf<Edge>() }
    val totalEdges = n * (n - 1) / 2 // Maximum possible edges for an undirected graph
    val numEdges = Random.nextInt(n - 1, totalEdges) // Choose a random number of edges

    val allEdges = mutableListOf<Pair<Int, Int>>()
    for (u in 0 until n) {
        for (v in u + 1 until n) {
            allEdges.add(Pair(u, v))
        }
    }

    allEdges.shuffle() // Shuffle the list of all possible edges

    for (i in 0 until numEdges) {
        val (u, v) = allEdges[i]
        val weight = Random.nextInt(1, 10) // Random weight between 1 and 9
        graph[u].add(Edge(v, weight))
        graph[v].add(Edge(u, weight))
    }

    return graph
}

// Function to store a graph into a file
fun storeGraphToFile(graph: List<List<Edge>>, filePath: String) {
    val file = File(filePath)
    if (!file.exists()) {
        file.createNewFile()
    }
    file.printWriter().use { out ->
        graph.forEachIndexed { nodeIndex, edges ->
            edges.forEach { edge ->
                out.println("$nodeIndex ${edge.to} ${edge.weight}")
            }
        }
    }
}
// Function to read a graph from a file
fun readGraphFromFile(filePath: String): List<List<Edge>> {
    val graph = mutableListOf<MutableList<Edge>>()

    File(filePath).forEachLine { line ->
        val parts = line.split(" ")
        val from = parts[0].toInt()
        val to = parts[1].toInt()
        val weight = parts[2].toInt()

        // Ensure the graph list is large enough
        while (graph.size <= from) {
            graph.add(mutableListOf())
        }

        graph[from].add(Edge(to, weight))
    }

    return graph
}


fun benchmark(testGraph : List<List<Edge>>) {
    val dj_seq = SequentialDijkstra()
    val dj_par = ParallelDijkstra()
    val fl_seq = SequentialFloydWarshall()
    val fl_par = FloydWarshallParallel()

    val dj_seq_time = measureTimeMillis {
        val paths = dj_seq.run_algorithm(testGraph, 0)
    }
    val dj_par_time = measureTimeMillis {
        val paths = dj_par.runAlgorithm(testGraph, 0, 4)
    }
    val fl_seq_time = measureTimeMillis {
        val paths = fl_seq.run_algorithm(testGraph)
    }
    val fl_par_time = measureTimeMillis {
        val paths = fl_par.run_algorithm(testGraph, 4)
    }
    println("Testing for a graph with ${testGraph.size} vertexes")
    println("Sequential Dijkstra time: $dj_seq_time milliseconds")
    println("Parallel Dijkstra time: $dj_par_time milliseconds")
    println("Sequential Floyd time: $fl_seq_time milliseconds")
    println("Parallel Floyd time: $fl_par_time milliseconds")
    println()
}
fun main() {
    val graph10 = generateRandomGraph(10)
    val graph100 = generateRandomGraph(100)
    val graph1000 = generateRandomGraph(1000)

    storeGraphToFile(graph10, "graph10.txt")
    storeGraphToFile(graph100,"graph100.txt")
    storeGraphToFile(graph1000, "graph1000.txt")

    benchmark(graph10)
    benchmark(graph100)
    benchmark(graph1000)

    val newGraph10 = readGraphFromFile("graph10.txt")
    val newGraph100 = readGraphFromFile("graph100.txt")
    val newGraph1000 = readGraphFromFile("graph1000.txt")

    for (i in 2..10) {
        println("Launch $i")
        benchmark(newGraph10)
        benchmark(newGraph100)
        benchmark(newGraph1000)
    }

}

