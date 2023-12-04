package day04

import println
import readInput
import kotlin.math.pow

fun main() {
    fun parseCard(card: String): Pair<Set<Int>, Set<Int>> {
        val numbers = card.substringAfter(":").split(" | ").map { it.trim() }
        val winning = numbers[0].split(" ").filter { it.isNotEmpty() }.map { it.toInt() }.toSet()
        val actual = numbers[1].split(" ").filter { it.isNotEmpty() }.map { it.toInt() }.toSet()

        return winning to actual
    }

    fun calculateWinningNumbers(winning: Set<Int>, actual: Set<Int>): Int {
        return actual.intersect(winning).count()
    }

    fun part1(input: List<String>): Int {
        return input.map { parseCard(it) }.sumOf { 2.0.pow(calculateWinningNumbers(it.first, it.second) - 1).toInt() }
    }

    fun part2(input: List<String>): Int {
        val numberOfOriginalCards = input.size
        val cards = input.map { parseCard(it) }
        val numberOfCards = (1..numberOfOriginalCards).associateWith { 1 }.toMutableMap()

        cards.forEachIndexed { index, numbers ->
            val winningNumbers = calculateWinningNumbers(numbers.first, numbers.second)
            (index + 1..index + winningNumbers).forEach {
                numberOfCards[it + 1] = numberOfCards[it + 1]!! + 1 * numberOfCards[index + 1]!!
            }
        }

        return numberOfCards.map { it.value }.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day04/day04_test")
    check(part1(testInput) == 13)
    check(part2(testInput) == 30)

    val input = readInput("day04/day04")
    part1(input).println()
    part2(input).println()
}
