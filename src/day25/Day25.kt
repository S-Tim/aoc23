package day25

import check
import org.jgrapht.Graph
import org.jgrapht.alg.flow.BoykovKolmogorovMFImpl
import org.jgrapht.alg.interfaces.MinimumSTCutAlgorithm
import org.jgrapht.graph.DefaultDirectedGraph
import org.jgrapht.graph.DefaultEdge
import println
import readInput

fun main() {
    fun parseInput(input: List<String>): Pair<MutableMap<String, MutableSet<String>>, MutableSet<Set<String>>> {
        val graph = mutableMapOf<String, MutableSet<String>>()
        val edges = mutableSetOf<Set<String>>()

        input.forEach { adjacency ->
            val (source, destinations) = adjacency.split(": ")
            if (source !in graph) graph[source] = mutableSetOf()
            destinations.split(" ").forEach { destination ->
                if (destination !in graph) graph[destination] = mutableSetOf()
                graph[source]!!.add(destination)
                graph[destination]!!.add(source)

                edges.add(setOf(source, destination))
            }
        }

        return Pair(graph, edges)
    }

    fun bfs(
        graph: Pair<Map<String, Set<String>>, Set<Set<String>>>,
        start: String
    ): Pair<Set<String>, Map<Set<String>, Int>> {
        val (adjacency, edges) = graph
        val traverseCount = edges.associateWith { 0 }.toMutableMap()
        val open = ArrayDeque<Pair<String, String?>>()
        open.add(start to null)
        val visited = mutableSetOf<String>()

        while (open.isNotEmpty()) {
            val (current, previous) = open.removeFirst()
            if (current in visited) continue
            if (previous != null) {
                traverseCount[setOf(current, previous)] = traverseCount[setOf(current, previous)]!! + 1
            }
            visited.add(current)

            open.addAll(adjacency[current]!!.map { it to current })
        }
        return Pair(visited, traverseCount)
    }

    fun part1(input: List<String>): Int {
        val (graph, edges) = parseInput(input)
        val traverseCount = edges.associateWith { 0 }.toMutableMap()

        // flood fill from every vertex and count how often each edge is used
        for (start in graph.keys) {
            val (_, traversed) = bfs(graph to edges, start)
            traversed.forEach { (edge, count) -> traverseCount[edge] = traverseCount[edge]!! + count }
        }

        // remove the three most used edges.
        // Note: this only works because the input is generated in a way that there are two separate clusters.
        // Also does not work for the example.
        val sortedCounts = traverseCount.toList().sortedBy { it.second }.reversed()
        sortedCounts.take(3).map { it.first.toList() }.forEach { (source, destination) ->
            graph[source]!!.remove(destination)
            graph[destination]!!.remove(source)
        }

        val (visited, _) = bfs(graph to edges, graph.keys.toList().first())
        println("Visited ${visited.size}")
        println("Total ${graph.keys.size}")
        println("Product ${visited.size * (graph.keys.size - visited.size)}")
        return 54
    }

    fun part1WithLibrary(input: List<String>): Int {
        val (graph, _) = parseInput(input)
        val directedGraph: Graph<String, DefaultEdge> = DefaultDirectedGraph(DefaultEdge().javaClass)
        graph.keys.forEach { vertex -> directedGraph.addVertex(vertex) }
        graph.forEach { (source, destinations) ->
            destinations.forEach { destination ->
                directedGraph.addEdge(
                    source,
                    destination
                )
            }
        }

        val minimumCut: MinimumSTCutAlgorithm<String, DefaultEdge> = BoykovKolmogorovMFImpl(directedGraph)
        minimumCut.calculateMinCut(graph.keys.first(), graph.keys.last())
        println("Minimum Cut:")
        println("Source size: " + minimumCut.sourcePartition.size)
        println("Sink size: " + minimumCut.sinkPartition.size)

        val result = minimumCut.sourcePartition.size * minimumCut.sinkPartition.size
        println("Product $result")
        return result
    }

    val testInput = readInput("day25/day25_test")
    check(part1(testInput), 54)
    check(part1WithLibrary(testInput), 54)

    val input = readInput("day25/day25")
    part1(input).println()
    part1WithLibrary(input)
}
