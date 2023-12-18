package day17

import Point
import check
import minus
import plus
import println
import readInput

enum class Direction(val vector: Point) {
    NORTH(-1 to 0), EAST(0 to 1), SOUTH(1 to 0), WEST(0 to -1)
}

fun main() {
    data class Block(val position: Point, val direction: Direction, val movesInDirection: Int)

    fun getNeighbors(block: Block, mapSize: Int): List<Block> {
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
        return neighbors.filter { it.position.first in 0 until mapSize && it.position.second in 0 until mapSize && it.movesInDirection <= 3 }
    }

    fun getNeighbors2(block: Block, mapSize: Int, mapSize2: Int): List<Block> {
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
        return neighbors.filter { it.position.first in 0 until mapSize && it.position.second in 0 until mapSize2 && it.movesInDirection <= 10 }
    }

    fun parseInput(input: List<String>): Map<Point, Int> {
        return input.indices.flatMap { row -> input[0].indices.map { (row to it) to input[row][it].digitToInt() } }
            .toMap()
    }

    fun part1(input: List<String>): Int {
        val mapSize = input.size
        val blockCosts = parseInput(input)
        val start = Block(Point(0, 0), Direction.WEST, 0)
        val open = ArrayDeque<Block>()
        val seen = mutableSetOf<Block>()
        open.add(start)
        val distances = mutableMapOf<Block, Int>()
        distances[start] = 0
        val parents = mutableMapOf<Block, Block?>(start to null)

        while (open.isNotEmpty()) {
            val current = open.minBy { distances[it]!! }
            open.remove(current)

            if (current in seen) {
                continue
            }
            seen.add(current)

            val neighbors = getNeighbors(current, mapSize).filter { it.position != parents[current]?.position }
            open.addAll(neighbors)

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
        }

        var score = 0
        val path = mutableListOf<Block>()
        var temp = distances.filter { it.key.position == (mapSize - 1 to mapSize - 1) }.keys.first()
        while (temp.position != start.position) {
            score += blockCosts[temp.position]!!
            path.add(temp)
            temp = parents[temp]!!
        }
        path.add(start)
        println(score)

        println()
        for (i in input.indices) {
            for (j in input.indices) {
                if (i to j in path.map { it.position }) print("#") else print(input[i][j])
            }
            println()
        }

        return score
    }

    fun part2(input: List<String>): Int {
        val mapSize = input.size
        val mapSize2 = input[0].length
        val blockCosts = parseInput(input)
        val start1 = Block(Point(0, 0), Direction.EAST, 0)
        val start2 = Block(Point(0, 0), Direction.SOUTH, 0)
        val open = ArrayDeque<Block>()
        val seen = mutableSetOf<Block>()
        open.add(start1)
        open.add(start2)
        var distances = mutableMapOf<Block, Int>()
        distances[start1] = 0
        distances[start2] = 0

        val parents = mutableMapOf<Block, Block?>(start1 to null)
        parents.put(start2, null)

        while (open.isNotEmpty()) {
            val current = open.minBy { distances[it] ?: Int.MAX_VALUE }
            open.remove(current)

            if (current in seen) {
                continue
            }
            seen.add(current)

            val neighbors =
                getNeighbors2(current, mapSize, mapSize2).filter { it.position != parents[current]?.position }

            for (neighbor in neighbors) {
                if (neighbor in seen) {
                    continue
                }
                val newDistance = (distances[current] ?: (Int.MAX_VALUE / 2 - 2)) + (blockCosts[neighbor.position]
                    ?: (Int.MAX_VALUE / 2 - 2))
                if ((neighbor.direction == current.direction) || current.movesInDirection >= 4) {
                    if (neighbor.position == Point(mapSize - 1, mapSize2 - 1) && neighbor.movesInDirection < 4) {
                        continue
                    }
                    open.add(neighbor)
                    if (newDistance < (distances[neighbor] ?: Int.MAX_VALUE)) {
                        distances[neighbor] = newDistance
                        parents[neighbor] = current
                    }
                }
            }
        }

        var score = 0
        val path = mutableListOf<Block>()
        var temp = distances.filter { it.key.position == (mapSize - 1 to mapSize2 - 1) }.keys.first()
        while (temp.position != start1.position) {
            score += blockCosts[temp.position]!!
            path.add(temp)
            temp =
                parents[temp]!! // ?: Block(temp.position - temp.direction.vector, temp.direction, temp.movesInDirection - 1)
        }
        path.add(start1)
        println(score)

        println()
        for (i in input.indices) {
            for (j in input[0].indices) {
                if (i to j in path.map { it.position }) print("#") else print(input[i][j])
            }
            println()
        }

        return score
    }


    val testInput = readInput("day17/day17_test")
//    check(part1(testInput), 102)
    check(part2(testInput), 94)

    val input = readInput("day17/day17")
//    part1(input).println()
    part2(input).println()
}
