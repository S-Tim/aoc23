package day17

import Point
import check
import plus
import println
import readInput
import java.util.PriorityQueue

enum class Direction(val vector: Point) {
    NORTH(-1 to 0), EAST(0 to 1), SOUTH(1 to 0), WEST(0 to -1)
}

fun main() {
    data class Block(val position: Point, val direction: Direction, val movesInDirection: Int)

    fun getNeighbors(block: Block, rows: Int, cols: Int, maxMovesInDirection: Int): List<Block> {
        val neighbors = mutableListOf<Block>()
        for (direction in Direction.values()) {
            neighbors.add(
                Block(
                    (direction.vector + block.position),
                    direction,
                    if (direction == block.direction) block.movesInDirection + 1 else 1
                )
            )
        }
        return neighbors.filter { it.position.first in 0 until rows && it.position.second in 0 until cols && it.movesInDirection <= maxMovesInDirection }
    }

    fun parseInput(input: List<String>): Map<Point, Int> {
        return input.indices.flatMap { row -> input[0].indices.map { (row to it) to input[row][it].digitToInt() } }
            .toMap()
    }

    fun calculateHeatLoss(
        distances: Map<Block, Int>,
        blockCosts: Map<Point, Int>,
        parents: Map<Block, Block?>,
        start: Point,
        target: Point
    ): Int {
        var score = 0
        var temp = distances.filter { it.key.position == target }.keys.first()
        while (temp.position != start) {
            score += blockCosts[temp.position]!!
            temp = parents[temp]!!
        }
        return score
    }

    fun part1(input: List<String>): Int {
        val mapSize = input.size
        val blockCosts = parseInput(input)
        val start = Block(Point(0, 0), Direction.WEST, 0)
        val seen = mutableSetOf<Block>()
        val distances = mutableMapOf<Block, Int>()
        distances[start] = 0
        val open = PriorityQueue(compareBy<Block> { distances[it] })
        open.add(start)
        val parents = mutableMapOf<Block, Block?>(start to null)

        while (open.isNotEmpty()) {
            val current = open.remove()

            if (current in seen) {
                continue
            }
            seen.add(current)

            val neighbors =
                getNeighbors(current, mapSize, mapSize, 3).filter { it.position != parents[current]?.position }

            for (neighbor in neighbors) {
                if (neighbor in seen) {
                    continue
                }
                val newDistance = distances[current]!! + blockCosts[neighbor.position]!!
                if (newDistance < (distances[neighbor] ?: Int.MAX_VALUE)) {
                    distances[neighbor] = newDistance
                    parents[neighbor] = current
                }
            }
            open.addAll(neighbors)
        }

        return calculateHeatLoss(
            distances,
            blockCosts,
            parents,
            start.position,
            Point(input.size - 1, input[0].length - 1)
        )
    }

    fun part2(input: List<String>): Int {
        val rows = input.size
        val cols = input[0].length
        val blockCosts = parseInput(input)

        val start1 = Block(Point(0, 0), Direction.EAST, 0)
        val start2 = Block(Point(0, 0), Direction.SOUTH, 0)
        val target = Point(rows - 1, cols - 1)

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

            val neighbors =
                getNeighbors(current, rows, cols, 10).filter { it.position != parents[current]?.position }

            for (neighbor in neighbors) {
                if (neighbor in seen) {
                    continue
                }
                val newDistance = distances[current]!! + blockCosts[neighbor.position]!!
                if ((neighbor.direction == current.direction) || current.movesInDirection >= 4) {
                    if (neighbor.position == target && neighbor.movesInDirection < 4) {
                        continue
                    }
                    if (newDistance < (distances[neighbor] ?: Int.MAX_VALUE)) {
                        distances[neighbor] = newDistance
                        parents[neighbor] = current
                    }
                    open.add(neighbor)
                }
            }
        }

        return calculateHeatLoss(distances, blockCosts, parents, start1.position, target)
    }


    val testInput = readInput("day17/day17_test")
    check(part1(testInput), 102)
    check(part2(testInput), 94)

    val input = readInput("day17/day17")
    part1(input).println()
    part2(input).println()
}
