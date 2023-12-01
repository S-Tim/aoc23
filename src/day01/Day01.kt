package day01

import println
import readInput

fun main() {
    fun firstAndLastDigit(calibrationValue: String, replacementValues: List<Pair<String, Int>>): Int {
        val firstNumberString =
            replacementValues.map { it.second to calibrationValue.indexOf(it.first) }.filter { it.second != -1 }
                .minBy { it.second }
        val lastNumberString =
            replacementValues.map { it.second to calibrationValue.lastIndexOf(it.first) }.filter { it.second != -1 }
                .maxBy { it.second }

        return Integer.parseInt(firstNumberString.first.toString() + lastNumberString.first.toString())
        // 8954bxsqntndjmonenx5
    }

    fun part1(input: List<String>): Int {
        val numbers = listOf(
            "1" to 1,
            "2" to 2,
            "3" to 3,
            "4" to 4,
            "5" to 5,
            "6" to 6,
            "7" to 7,
            "8" to 8,
            "9" to 9,
        )

        return input.sumOf { firstAndLastDigit(it, numbers) }
    }

    fun part2(input: List<String>): Int {
        val numbers = listOf(
            "one" to 1,
            "two" to 2,
            "three" to 3,
            "four" to 4,
            "five" to 5,
            "six" to 6,
            "seven" to 7,
            "eight" to 8,
            "nine" to 9,
            "1" to 1,
            "2" to 2,
            "3" to 3,
            "4" to 4,
            "5" to 5,
            "6" to 6,
            "7" to 7,
            "8" to 8,
            "9" to 9
        )

        return input.sumOf { firstAndLastDigit(it, numbers) }
    }


    // test if implementation meets criteria from the description, like:
    val testInput1 = readInput("day01/day01_1_test")
    check(part2(testInput1) == 142)

    val testInput2 = readInput("day01/day01_test")
    check(part2(testInput2) == 281)

    val input = readInput("day01/day01")
    part1(input).println()
    part2(input).println()
}
