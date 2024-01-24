package day10

import Point
import check
import plus
import println
import readInput

fun main() {
    fun parseInput(input: List<String>): Pair<Map<Point, Set<Point>>, Point> {
        val map = mutableMapOf<Point, Set<Point>>()
        val neighborDirections = mapOf(
            '|' to setOf(Point(-1, 0), Point(1, 0)),
            '-' to setOf(Point(0, -1), Point(0, 1)),
            'L' to setOf(Point(-1, 0), Point(0, 1)),
            'J' to setOf(Point(-1, 0), Point(0, -1)),
            '7' to setOf(Point(0, -1), Point(1, 0)),
            'F' to setOf(Point(1, 0), Point(0, 1))
        )

        var start = Point(0, 0)
        for (row in input.indices) {
            for (col in input[0].indices) {
                val position = Point(row, col)

                // Save the start position
                if (input[row][col] == 'S') {
                    start = position
                    continue
                }

                map[position] = neighborDirections.getOrDefault(input[row][col], setOf())
                    .map { Point(it.first + row, it.second + col) }
                    .filter { it.first in input.indices && it.second in input[0].indices }
                    .toSet()
            }
        }

        // Find the vertices connected to the start and add the edges from the start to them
        map[start] = map.filter { start in it.value }.keys.toMutableSet()

        return map to start
    }

    fun calculateMainLoop(map: Map<Point, Set<Point>>, start: Point): Pair<Set<Point>, Map<Point, Int>> {
        val open = ArrayDeque<Pair<Point, Int>>()
        open.add(start to 0)
        val visited = mutableSetOf<Point>()
        val distances = mutableMapOf<Point, Int>()

        while (open.isNotEmpty()) {
            val (current, distance) = open.removeFirst()

            if (current in visited) continue
            visited.add(current)
            distances[current] = distance

            open.addAll(map.getOrDefault(current, setOf()).map { it to distance + 1 })
        }

        return Pair(visited, distances)
    }

    fun part1(input: List<String>): Int {
        val (map, start) = parseInput(input)
        val (_, distances) = calculateMainLoop(map, start)

        return distances.maxBy { it.value }.value
    }

    fun isEdgeInMainLoop(from: Point, to: Point, mainLoop: Set<Point>, map: Map<Point, Set<Point>>): Boolean {
        return from in mainLoop && to in mainLoop && map[from]!!.contains(to)
    }

    /**
     * Gets the neighbors as if the position would be in the middle of the tile.
     * It then checks if there is an edge of the mainloop on each side of the tile.
     */
    fun getNeighbors(tile: Point, mainLoop: Set<Point>, map: Map<Point, Set<Point>>): Set<Point> {
        val neighbors = mutableSetOf<Point>()

        val top = tile + Point(-1, 0)
        val right = tile + Point(0, 1)
        val bottomRight = tile + Point(1, 1)
        val bottom = tile + Point(1, 0)
        val left = tile + Point(0, -1)

        if (!isEdgeInMainLoop(tile, right, mainLoop, map)) neighbors.add(top)
        if (!isEdgeInMainLoop(right, bottomRight, mainLoop, map)) neighbors.add(right)
        if (!isEdgeInMainLoop(bottom, bottomRight, mainLoop, map)) neighbors.add(bottom)
        if (!isEdgeInMainLoop(tile, bottom, mainLoop, map)) neighbors.add(left)

        return neighbors
    }


    fun part2(input: List<String>): Int {
        val (map, start) = parseInput(input)
        val (mainLoop, _) = calculateMainLoop(map, start)

        // All non main loop tiles are candidates
        val candidates = input.indices.flatMap { row -> input[0].indices.map { row to it } }.toMutableSet()
        val adjacency = candidates.associateWith { getNeighbors(it, mainLoop, map) }
        candidates.removeAll(mainLoop)

        val enclosedTiles = mutableSetOf<Point>()
        val notEnclosedTiles = mutableSetOf<Point>()

        val rows = input.size
        val cols = input[0].length

        while (candidates.isNotEmpty()) {
            val open = ArrayDeque<Point>()
            open.add(candidates.first())
            val visited = mutableSetOf<Point>()
            var enclosed = true

            while (open.isNotEmpty()) {
                val current = open.removeFirst()

                if (current in visited) continue
                // If we reach a point outside the map then the points are not enclosed by the loop
                // we still want to continue the search to find the whole cluster
                if (current.first !in (0 until rows) || current.second !in (0 until cols)) {
                    enclosed = false
                    continue
                }
                visited.add(current)

                open.addAll(adjacency[current]!!)
            }

            // If we squeeze between the pipes the path still has the coordinates of one of the sides.
            // We can still traverse in that direction but there is no enclosed tile at that point.
            visited.removeAll(mainLoop)
            candidates.removeAll(visited)
            if (enclosed) {
                enclosedTiles.addAll(visited)
            } else {
                notEnclosedTiles.addAll(visited)
            }
        }

        for (i in (0 until rows)) {
            for (j in (0 until cols)) {
                val current = Point(i, j)
                print(
                    when (current) {
                        in enclosedTiles -> "I"
                        in notEnclosedTiles -> "O"
                        else -> input[i][j]
                    }
                )
            }
            println()
        }

        return enclosedTiles.size
    }


    val testInput1 = readInput("day10/day10_1_test")
    val testInput2 = readInput("day10/day10_2_test")
    check(part1(testInput1), 8)
    check(part2(testInput2), 10)

    val input = readInput("day10/day10")
    part1(input).println()
    part2(input).println()
}
