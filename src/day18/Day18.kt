package day18

import Point
import check
import plus
import printAsGrid
import println
import readInput
import times

enum class Direction(val vector: Point) {
    R(1 to 0), L(-1 to 0), U(0 to -1), D(0 to 1)
}

fun main() {
    data class Instruction(val direction: Direction, val distance: Int, val color: String)

    fun parseInput(input: List<String>): List<Instruction> {
        val pattern = """([A-Z]) (\d+) \(#([0-9a-f|]+)\)""".toRegex()

        return input.map { pattern.matchEntire(it)?.groupValues?.drop(1)!! }
            .map { (direction, distance, color) -> Instruction(Direction.valueOf(direction), distance.toInt(), color) }
    }

    fun Point.neighbors(): List<Point> {
        val neighbors = mutableListOf<Point>()
        for (i in -1..1)
            for (j in -1..1)
                if (!(i == 0 && j == 0)) neighbors.add(this.first + i to this.second + j)
        return neighbors
    }

    fun part1(input: List<String>): Int {
        val instructions = parseInput(input)

        val trench = mutableSetOf(Point(0, 0))
        for (instruction in instructions) {
            repeat(instruction.distance) { trench.add(trench.last() + instruction.direction.vector) }
        }

//        printAsGrid(trench)

        // Bottom right tile of the top left corner must always be in inside the trench outline
        val topLeft = trench.filter { point -> point.second == trench.minOfOrNull { it.second } }.minBy { it.first }
        val open = ArrayDeque(listOf(topLeft + (1 to 1)))
        val seen = mutableSetOf<Point>()

        // Flood fill
        while (open.isNotEmpty()) {
            val current = open.removeFirst()

            if (current in seen) continue
            seen.add(current)

            val neighbors = current.neighbors().filter { it !in trench && it !in seen }
            open.addAll(neighbors)
        }

//        printAsGrid(seen)
        return seen.union(trench).size
    }

    fun Char.toDirection(): Direction {
        return when (this) {
            '0' -> Direction.R
            '1' -> Direction.D
            '2' -> Direction.L
            '3' -> Direction.U
            else -> throw IllegalArgumentException()
        }
    }

    fun part2(input: List<String>): Int {
        val instructions = parseInput(input).map { instruction ->
            instruction.color.last().toDirection() to instruction.color.take(5).toInt(16)
        }

        println(instructions.map { it.second }.sum())
        val trench = mutableListOf(Point(0, 0))
        for ((direction, distance) in instructions) {
            repeat(distance) {
                trench.add(trench.last() + direction.vector)
                if(trench.size % 1_000_000 == 0) println(trench.size)
            }
        }
        println("Trench completed")

//        printAsGrid(trench)

        // Bottom right tile of the top left corner must always be in inside the trench outline
        val topLeft = trench.filter { point -> point.second == trench.minOfOrNull { it.second } }.minBy { it.first }
        val open = ArrayDeque(listOf(topLeft + (1 to 1)))
        val seen = mutableSetOf<Point>()

        // Flood fill
        while (open.isNotEmpty()) {
            val current = open.removeFirst()

            if (current in seen) continue
            seen.add(current)

            val neighbors = current.neighbors().filter { it !in trench && it !in seen }
            open.addAll(neighbors)
        }

//        printAsGrid(seen)
        return seen.union(trench).size
    }


    val testInput = readInput("day18/day18_test")
    check(part1(testInput), 62)
    check(part2(testInput), 952408144115)

    val input = readInput("day18/day18")
    part1(input).println()
//    part2(input).println()
}
