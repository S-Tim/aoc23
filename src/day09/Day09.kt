package day09

import check
import println
import readInput
import splitToInts

fun main() {
    fun extrapolationSequence(reportValue: List<Int>): Sequence<List<Int>> {
        return generateSequence(reportValue) {
            it.windowed(2).map { (a, b) -> b - a }
        }.takeWhile { differences -> differences.any { it != 0 } }
    }

    fun part1(input: List<String>): Int {
        val values = input.map { it.splitToInts() }
        return values.sumOf { value -> extrapolationSequence(value).sumOf { it.last() } }
    }

    fun part2(input: List<String>): Int {
        val values = input.map { it.splitToInts() }
        return values.sumOf { value ->
            extrapolationSequence(value).map { it.first() }.toList().reduceRight { a, b -> a - b }
        }
    }

    val testInput = readInput("day09/day09_test")
    check(part1(testInput), 114)
    check(part2(testInput), 2)

    val input = readInput("day09/day09")
    part1(input).println()
    part2(input).println()
}
