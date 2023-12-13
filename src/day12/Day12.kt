package day12

import check
import println
import readInput

fun main() {
    fun parseInput(input: List<String>): List<Pair<String, List<Int>>> {
        return input.map { it.split(" ") }.map { (springs, groups) ->
            springs to groups.split(",").map { it.toInt() }
        }
    }

    fun isValid(springs: String, groups: List<Int>): Boolean {
        val pattern = "#+".toRegex()
        val matches = pattern.findAll(springs).map { it.value }.toList()

        if (matches.size != groups.size) return false

        for ((l1, l2) in groups.zip(matches.map { it.length })) {
            if (l1 != l2) {
                return false
            }
        }

        return true
    }

    fun expandSprings(springs: String): List<String> {
        if ('?' !in springs) return listOf(springs)
        return expandSprings(springs.replaceFirst('?', '#')) + expandSprings(springs.replaceFirst('?', '.'))
    }

    fun calcScore(springs: String, groups: List<Int>, seen: String, cache: MutableMap<String, Long>): Long {
        if (seen in cache) {
            return cache[seen]!!
        }

        if ('?' !in springs) {
            return if (isValid(springs, groups)) 1 else 0
        }

        val s = springs.take(springs.indexOf('?'))
        val score =
            calcScore(springs.replaceFirst('?', '.'), groups, s, cache) +
                    calcScore(springs.replaceFirst('?', '#'), groups, s, cache)
        cache[s] = score

        return score
    }

    fun part1(input: List<String>): Int {
        val records = parseInput(input)
        val expanded = records.flatMap { (springs, groups) -> expandSprings(springs).map { it to groups } }
        return expanded.count { isValid(it.first, it.second) }
//        return expanded.count { isValid(it.first, it.second) }
//        return calcScore(records[0].first, records[0].second, records[0].first, mutableMapOf())
    }

    fun part2(input: List<String>): Int {
        return input.size
    }


    val testInput = readInput("day12/day12_test")
    check(part1(testInput), 21)

    val input = readInput("day12/day12")
    part1(input).println()
    part2(input).println()
}
