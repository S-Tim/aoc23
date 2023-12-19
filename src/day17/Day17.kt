package day17

import Point
import check
import day17.Direction.EAST
import day17.Direction.SOUTH
import plus
import println
import readInput
import java.util.PriorityQueue

enum class Direction(val vector: Point) {
    NORTH(-1 to 0), EAST(0 to 1), SOUTH(1 to 0), WEST(0 to -1);

    fun opposite(): Direction {
        return when (this) {
            NORTH -> SOUTH
            EAST -> WEST
            SOUTH -> NORTH
            WEST -> EAST
        }
    }
}

fun main() {
    data class Block(val position: Point, val direction: Direction, val movesInDirection: Int) {
        fun neighbors(): List<Block> {
            return Direction.values().mapNotNull { direction ->
                if (direction == this.direction.opposite()) {
                    null
                } else {
                    Block(
                        direction.vector + position,
                        direction,
                        if (direction == this.direction) movesInDirection + 1 else 1
                    )
                }
            }
        }
    }

    fun parseInput(input: List<String>): Map<Point, Int> {
        return input.indices.flatMap { row -> input[0].indices.map { (row to it) to input[row][it].digitToInt() } }
            .toMap()
    }

    fun solve(
        input: List<String>,
        start: Point = Point(0, 0),
        target: Point = Point(input.size - 1, input[0].length - 1),
        minMovesInDirection: Int = 0,
        maxMovesInDirection: Int = 3
    ): Int {
        val rows = input.size
        val cols = input[0].length
        val blockCosts = parseInput(input)

        val start1 = Block(start, EAST, 0)
        val start2 = Block(start, SOUTH, 0)

        val seen = mutableSetOf<Block>()
        val distances = mutableMapOf(start1 to 0, start2 to 0)
        val parents = mutableMapOf<Block, Block?>(start1 to null, start2 to null)

        val open = PriorityQueue(compareBy<Block> { distances[it] })
        open.add(start1)
        open.add(start2)

        while (open.isNotEmpty()) {
            val current = open.remove()

            if (current in seen) {
                continue
            }
            seen.add(current)

            val neighbors = current.neighbors()
                .filter { it.position.first in 0 until rows && it.position.second in 0 until cols && it.movesInDirection <= maxMovesInDirection }

            for (neighbor in neighbors) {
                if (neighbor in seen) {
                    continue
                }
                if ((neighbor.direction == current.direction) || current.movesInDirection >= minMovesInDirection) {
                    if (neighbor.position == target && neighbor.movesInDirection < minMovesInDirection) {
                        continue
                    }
                    val newDistance = distances[current]!! + blockCosts[neighbor.position]!!
                    if (newDistance < (distances[neighbor] ?: Int.MAX_VALUE)) {
                        distances[neighbor] = newDistance
                        parents[neighbor] = current
                    }
                    open.add(neighbor)
                }
            }
        }

        val temp = distances.filter { it.key.position == target }.keys.first()
        return generateSequence(temp) { parents[it]!! }.takeWhile { it.position != start }
            .fold(0) { heatLoss, block -> heatLoss + blockCosts[block.position]!! }
    }

    fun part1(input: List<String>): Int {
        return solve(input)
    }

    fun part2(input: List<String>): Int {
        return solve(input, minMovesInDirection = 4, maxMovesInDirection = 10)
    }


    val testInput = readInput("day17/day17_test")
    check(part1(testInput), 102)
    check(part2(testInput), 94)

    val input = readInput("day17/day17")
    part1(input).println()
    part2(input).println()
}
