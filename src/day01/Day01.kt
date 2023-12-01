package day01

import println
import readInput

fun main() {
    fun mapToDigit(value: String?): String? {
        return when (value) {
            "one" -> "1"
            "two" -> "2"
            "three" -> "3"
            "four" -> "4"
            "five" -> "5"
            "six" -> "6"
            "seven" -> "7"
            "eight" -> "8"
            "nine" -> "9"
            else -> value
        }
    }

    fun parseCalibrationValue(calibrationValue: String, pattern: String): Int {
        val regex = Regex("(?=($pattern))")
        val matches = regex.findAll(calibrationValue).toList()
        var firstNumber = matches.first().groups[1]?.value
        var lastNumber = matches.last().groups[1]?.value

        firstNumber = mapToDigit(firstNumber)
        lastNumber = mapToDigit(lastNumber)

        return (firstNumber + lastNumber).toInt()
    }


    fun part1(input: List<String>): Int {
        return input.sumOf { parseCalibrationValue(it, "[1-9]") }
    }

    fun part2(input: List<String>): Int {
        return input.sumOf { parseCalibrationValue(it, "[1-9]|one|two|three|four|five|six|seven|eight|nine") }
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