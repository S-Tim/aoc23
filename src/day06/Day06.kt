package day06

import println
import readInput
import java.lang.Math.pow
import kotlin.math.*

fun main() {
    fun parseInput(input: List<String>): Pair<List<Int>, List<Int>> {
        val values = input.map { it.substringAfter(": ") }
            .map { list -> list.split(" ").filter { it.isNotEmpty() }.map { it.trim().toInt() } }
        return values[0] to values[1]
    }

    fun calculateWinningConfigurations(time: Long, distance: Long): Int {
        return (1 until time).count { it * (time - it) > distance }
    }

    fun calculateWinningConfigurationsWithMath(time: Long, distance: Long): Int {
        // x * (t - x) = d
        // x * (t - x) - d = 0
        // -X^2 + tx - d = 0
        // x^2 - tx + d = 0
        // PQ-Formula:
        // -t/2 +- sqrt((t/2)^2 -d)
        val doubleTime = time.toDouble()
        val doubleDistance = distance.toDouble() + 0.0001 // you have to beat the distance so add a small delta

        // crop to whole integers
        val x1 = floor(0.5 * doubleTime + sqrt((doubleTime / 2.0).pow(2.0) - doubleDistance))
        val x2 = ceil(0.5 * doubleTime - sqrt((doubleTime / 2.0).pow(2.0) - doubleDistance))

        return (x1 - x2).toInt() + 1
    }

    fun part1(input: List<String>): Int {
        val (times, distances) = parseInput(input)

        return times.zip(distances)
            .map { calculateWinningConfigurationsWithMath(it.first.toLong(), it.second.toLong()) }
            .reduceRight { i, acc -> i * acc }
    }

    fun part2(input: List<String>): Int {
        val (times, distances) = parseInput(input)

        val time = times.joinToString("") { it.toString() }.toLong()
        val distance = distances.joinToString("") { it.toString() }.toLong()

        return calculateWinningConfigurationsWithMath(time, distance)
    }


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day06/day06_test")
    check(part1(testInput) == 288)
    check(part2(testInput) == 71503)

    val input = readInput("day06/day06")
    part1(input).println()
    part2(input).println()
}
