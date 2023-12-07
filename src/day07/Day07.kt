package day07

import check
import println
import readInput

fun main() {
    open class Hand(val cards: List<Char>, val bid: Int) : Comparable<Hand> {
        protected open val cardValues = listOf('A', 'K', 'Q', 'J', 'T', '9', '8', '7', '6', '5', '4', '3', '2')

        override fun compareTo(other: Hand): Int {
            val winningOrder = listOf({ h: Hand -> h.isNOfAKind(5) },
                { h: Hand -> h.isNOfAKind(4) },
                { h: Hand -> h.isFullHouse() },
                { h: Hand -> h.isNOfAKind(3) },
                { h: Hand -> h.isTwoPair() },
                { h: Hand -> h.isNOfAKind(2) },
                { h: Hand -> h.isHighCard() })

            val thisWinningIndex = winningOrder.indexOfFirst { it(this) }
            val otherWinningIndex = winningOrder.indexOfFirst { it(other) }

            return if (thisWinningIndex == otherWinningIndex) {
                tieBreak(other.cards)
            } else {
                otherWinningIndex - thisWinningIndex
            }
        }

        private fun isNOfAKind(value: Int): Boolean = getCardCounts().any { (_, count) -> count == value }
        private fun isHighCard(): Boolean = cards.distinct().size == 5

        private fun isFullHouse(): Boolean {
            val cardCounts = getCardCounts()
            return cardCounts.containsValue(3) && cardCounts.containsValue(2)
        }

        private fun isTwoPair(): Boolean = getCardCounts().values.count { it == 2 } == 2

        private fun tieBreak(other: List<Char>): Int {
            val firstDifference = cards.zip(other).indexOfFirst { it.first != it.second }
            return cardValues.indexOf(other[firstDifference]) - cardValues.indexOf(cards[firstDifference])
        }

        protected open fun getCardCounts(): Map<Char, Int> = cards.associateWith { card -> cards.count { it == card } }

        override fun toString(): String {
            return "${cards.joinToString("")} $bid"
        }
    }

    class HandWithJoker(cards: List<Char>, bid: Int) : Hand(cards, bid) {
        // Joker is the least valuable card in part 2
        override val cardValues = listOf('A', 'K', 'Q', 'T', '9', '8', '7', '6', '5', '4', '3', '2', 'J')

        override fun getCardCounts(): Map<Char, Int> {
            if (cards.all { it == 'J' }) {
                return mapOf('A' to 5)
            }

            val jokers = cards.count { it == 'J' }
            val cardCounts =
                cards.filter { it != 'J' }.associateWith { card -> cards.count { it == card } }.toMutableMap()

            // Add jokers to the card that occurs most often
            val key = cardCounts.maxByOrNull { it.value }?.key
            key?.let { cardCounts[it] = (cardCounts[it] ?: 0) + jokers }

            return cardCounts
        }
    }

    fun part1(input: List<String>): Int {
        val hands = input.map { it.split(" ") }.map { (cards, bid) -> Hand(cards.toCharArray().toList(), bid.toInt()) }
        return hands.sorted().mapIndexed { index, hand -> (index + 1) * hand.bid }.sum()
    }

    fun part2(input: List<String>): Int {
        val hands =
            input.map { it.split(" ") }.map { (cards, bid) -> HandWithJoker(cards.toCharArray().toList(), bid.toInt()) }
        return hands.sorted().mapIndexed { index, hand -> (index + 1) * hand.bid }.sum()
    }


    val testInput = readInput("day07/day07_test")
    check(part1(testInput), 6440)
    check(part2(testInput), 5905)

    val input = readInput("day07/day07")
    part1(input).println()
    part2(input).println()
}
