package day04

import check
import println
import readInput
import splitToInts
import kotlin.math.pow

fun main() {
    fun String.toCard(): Pair<Set<Int>, Set<Int>> {
        val (winning, actual) = this.substringAfter(": ").split(" | ").map { it.splitToInts().toSet() }
        return winning to actual
    }

    fun calculateWinningNumbers(winning: Set<Int>, actual: Set<Int>): Int {
        return actual.intersect(winning).count()
    }

    fun part1(input: List<String>): Int {
        return input.map { it.toCard() }
            .sumOf { (winning, actual) -> 2.0.pow(calculateWinningNumbers(winning, actual) - 1).toInt() }
    }

    fun part2(input: List<String>): Int {
        val cards = input.map { it.toCard() }
        val numberOfCards = (1..input.size).associateWith { 1 }.toMutableMap()

        cards.forEachIndexed { index, (winning, actual) ->
            val winningNumbers = calculateWinningNumbers(winning, actual)
            (index + 1..index + winningNumbers).forEach {
                numberOfCards[it + 1] = numberOfCards[it + 1]!! + numberOfCards[index + 1]!!
            }
        }

        return numberOfCards.map { it.value }.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day04/day04_test")
    check(part1(testInput), 13)
    check(part2(testInput), 30)

    val input = readInput("day04/day04")
    part1(input).println()
    part2(input).println()
}
