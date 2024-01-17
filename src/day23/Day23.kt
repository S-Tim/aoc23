package day23

import Point
import check
import plus
import println
import readInput

fun main() {
    fun getNeighbors(position: Point, map: List<String>): Set<Point> {
        val directions = setOf(Point(1, 0), Point(-1, 0), Point(0, 1), Point(0, -1))

        return directions.map { position + it }.associateWith { map.getOrNull(it.first)?.getOrNull(it.second) }
            .filter { it.value != null && it.value != '#' }.keys
    }

    fun parseInput2(input: List<String>): Map<Point, Set<Point>> {
        val adjacencyMatrix = mutableMapOf<Point, Set<Point>>()

        for (row in input.indices) {
            for (column in input[0].indices) {
                val currentValue = input[row][column]
                val currentPos = Point(row, column)
                if (currentValue != '#') {
                    val neighbors = getNeighbors(currentPos, input)
                    adjacencyMatrix[currentPos] = neighbors
                }
            }
        }

        return adjacencyMatrix
    }

    fun parseInput(input: List<String>): Map<Point, Set<Point>> {
        val adjacencyMatrix = mutableMapOf<Point, Set<Point>>()

        for (row in input.indices) {
            for (column in input[0].indices) {
                val currentValue = input[row][column]
                val currentPos = Point(row, column)
                if (currentValue != '#') {
                    val neighbors = when (input[row][column]) {
                        '>' -> setOf(currentPos + Point(0, 1))
                        '<' -> setOf(currentPos + Point(0, -1))
                        'v' -> setOf(currentPos + Point(1, 0))
                        '^' -> setOf(currentPos + Point(-1, 0))
                        else -> getNeighbors(currentPos, input)
                    }
                    adjacencyMatrix[currentPos] = neighbors
                }
            }
        }

        return adjacencyMatrix
    }

    fun part1(input: List<String>): Int {
        val adjacencyMatrix = parseInput(input)
        val end = Point(input.size - 1, input[0].length - 2)
        val open = ArrayDeque<Pair<Point, Set<Point>>>()
        open.add(Point(0, 1) to setOf())
        val visited = mutableSetOf<Pair<Point, Int>>()

        while (open.isNotEmpty()) {
            val next = open.removeFirst()
            if (next.first to next.second.size in visited) continue
            visited.add(next.first to next.second.size)

            if (next.first == end) {
                println("Found a path with length ${next.second.size}")
            }

            val path = next.second + next.first
            open.addAll(adjacencyMatrix[next.first]!!.filter { it !in path }.map { it to path })
        }

        val pathsToEnd = visited.filter { it.first == end }
        return pathsToEnd.maxOf { it.second }
    }

    fun part2(input: List<String>): Int {
        val adjacencyMatrix = parseInput2(input)
        val end = Point(input.size - 1, input[0].length - 2)
        val open = ArrayDeque<Pair<Point, Set<Point>>>()
        open.add(Point(0, 1) to setOf())
//        val visited = mutableSetOf<Pair<Point, Int>>()
        var maxPathLength = 0

        while (open.isNotEmpty()) {
            val next = open.removeLast()
//            if (next.first to next.second.size in visited) continue
//            visited.add(next.first to next.second.size)

            if (next.first == end) {
                if(next.second.size > maxPathLength){
                    maxPathLength = next.second.size
                    println("Found new max path with length $maxPathLength")
                }
            }

            val path = next.second + next.first
            open.addAll(adjacencyMatrix[next.first]!!.filter { it !in path }.map { it to path })
        }

        return maxPathLength
    }


    val testInput = readInput("day23/day23_test")
    check(part1(testInput), 94)
    check(part2(testInput), 154)

    val input = readInput("day23/day23")
    part1(input).println()
    part2(input).println()
}
