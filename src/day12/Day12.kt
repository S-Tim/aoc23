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

    fun calculateScore(
        springs: String,
        groups: List<Int>,
        currentPos: Int = 0,
        currentGroup: Int = 0,
        currentHashLength: Int = 0,
        memoization: MutableMap<Triple<Int, Int, Int>, Long> = mutableMapOf()
    ): Long {
        val key = Triple(currentPos, currentGroup, currentHashLength)
        if (key in memoization) {
            return memoization[key]!!
        }

        // If we are at the end of the springs
        if (currentPos == springs.length) {
            return if (currentGroup == groups.size && currentHashLength == 0) 1
            else if (currentGroup == groups.size - 1 && groups.last() == currentHashLength) 1
            else 0
        }

        var score = 0L
        if (springs[currentPos] in ".?") {
            if (currentHashLength == 0) {
                score += calculateScore(springs, groups, currentPos + 1, currentGroup, currentHashLength, memoization)
            }
            if (currentHashLength > 0 && currentGroup < groups.size && groups[currentGroup] == currentHashLength) {
                score += calculateScore(springs, groups, currentPos + 1, currentGroup + 1, 0, memoization)
            }
        }
        if (springs[currentPos] in "?#") {
            score += calculateScore(springs, groups, currentPos + 1, currentGroup, currentHashLength + 1, memoization)
        }

        memoization[key] = score
        return score
    }

    fun part1(input: List<String>): Long {
        val records = parseInput(input)
        return records.sumOf { calculateScore(it.first, it.second) }
    }

    fun part2(input: List<String>): Long {
        val records =
            parseInput(input).map { List(5) { _ -> it.first }.joinToString("?") to List(5) { _ -> it.second }.flatten() }
        return records.sumOf { calculateScore(it.first, it.second) }
    }


    val testInput = readInput("day12/day12_test")
    check(part1(testInput), 21)
    check(part2(testInput), 525152)

    val input = readInput("day12/day12")
    part1(input).println()
    part2(input).println()
}
