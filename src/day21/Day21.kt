package day21

import Point
import check
import println
import readInput

fun main() {
    fun getNeighbors(position: Point, map: List<String>): Set<Point> {
        val neighbors = listOf(
            Point(-1, 0),
            Point(1, 0),
            Point(0, -1),
            Point(0, 1)
        ).map { it.first + position.first to it.second + position.second }
            .associateWith { map.getOrNull(it.first)?.getOrNull(it.second) }

        return neighbors.filter { it.value != null && it.value != '#' && it.key != position }.keys
    }

    fun printMap(map: List<String>, neighbors: Set<Point>) {
        for (row in map.indices) {
            for (col in map[0].indices) {
                if (row to col in neighbors) print('O') else print(map[row][col])
            }
            println()
        }
    }

    fun part1(input: List<String>, steps: Int = 64): Int {
        val start = input.indices.flatMap { row -> input[0].indices.map { col -> row to col } }
            .first { input[it.first][it.second] == 'S' }

        val visited = mutableSetOf<Pair<Point, Int>>()
        val open = ArrayDeque<Pair<Point, Int>>()
        open.add(start to 0)

        while (open.isNotEmpty()) {
            val current = open.removeFirst()

            if (current in visited) {
                continue
            }
            visited.add(current)

            if (current.second < steps) {
                open.addAll(getNeighbors(current.first, input).map { it to current.second + 1 })
            }
        }

        val lastVisited = visited.filter { it.second == steps }.map { it.first }.toSet()
        printMap(input, lastVisited)
        return lastVisited.size
    }

    fun part2(input: List<String>): Int {
        return input.size
    }


    val testInput = readInput("day21/day21_test")
    check(part1(testInput, 6), 16)
//    check(part2(testInput), 281)

    val input = readInput("day21/day21")
    part1(input).println()
    part2(input).println()
}
