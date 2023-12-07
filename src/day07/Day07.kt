package day07

import check
import println
import readInput

fun main() {
    open class Hand(val cards: List<Char>, val bid: Int) : Comparable<Hand> {
        protected open val cardValues = listOf('A', 'K', 'Q', 'J', 'T', '9', '8', '7', '6', '5', '4', '3', '2')

        override fun compareTo(other: Hand): Int {
            val winningOrder = listOf(
                { h: Hand -> h.isNOfAKind(5) },
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

        protected open fun isNOfAKind(value: Int): Boolean {
            return cards.associateWith { card -> cards.count { it == card } }.any { (_, count) -> count == value }
        }

        private fun isHighCard(): Boolean {
            return cards.distinct().size == 5
        }

        protected open fun isFullHouse(): Boolean {
            val cardCounts = cards.associateWith { card -> cards.count { it == card } }
            return cardCounts.containsValue(3) && cardCounts.containsValue(2)
        }

        private fun isTwoPair(): Boolean {
            val cardCounts = cards.associateWith { card -> cards.count { it == card } }
            return cardCounts.values.count { it == 2 } == 2
        }

        private fun tieBreak(other: List<Char>): Int {
            val firstDifference = cards.zip(other).indexOfFirst { it.first != it.second }
            return cardValues.indexOf(other[firstDifference]) - cardValues.indexOf(cards[firstDifference])
        }

        override fun toString(): String {
            return "${cards.joinToString("")} $bid"
        }
    }

    class HandWithJoker(cards: List<Char>, bid: Int) : Hand(cards, bid) {
        // Joker is the least valuable card in part 2
        override val cardValues = listOf('A', 'K', 'Q', 'T', '9', '8', '7', '6', '5', '4', '3', '2', 'J')

        override fun isNOfAKind(value: Int): Boolean {
            return cards.associateWith { card -> cards.count { it == card || it == 'J' } }
                .any { (_, count) -> count == value }
        }

        override fun isFullHouse(): Boolean {
            val cardCounts =
                cards.filter { it != 'J' }.associateWith { card -> cards.count { it == card } }.toMutableMap()

            // Add jokers to the card that occurs most often
            // This only works because the winning order is defined and a better hand would have been evaluated already
            val key = cardCounts.maxBy { it.value }.key
            cardCounts[key] = cardCounts[key]!! + cards.count { it == 'J' }

            return cardCounts.containsValue(3) && cardCounts.containsValue(2)
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
    check(part1(testInput),6440)
    check(part2(testInput) , 5905)

    val input = readInput("day07/day07")
    part1(input).println()
    part2(input).println()
}
