package day13

import Point
import check
import println
import readInput

fun main() {
    fun parseInput(input: List<String>): List<List<List<Char>>> {
        return input.joinToString("\n").split("\n\n").map { it.split("\n").map { line -> line.toList() } }
    }

    fun checkSymmetry(pairs: Sequence<Pair<Char?, Char?>>, expectedErrors: Int = 0): Boolean {
        var errorCount = 0

        for ((a, b) in pairs) {
            // if end of pattern is reached
            if (a == null || b == null) return errorCount == expectedErrors

            // if the characters don't match
            if (a != b) {
                errorCount += 1
                // short circuit
                if (errorCount > expectedErrors) {
                    return false
                }
            }
        }

        return errorCount == expectedErrors
    }

    val getVertical = fun(pattern: List<List<Char>>, candidate: Point): Sequence<Pair<Char?, Char?>> {
        return sequence {
            for (i in 0..candidate.first) {
                for (j in pattern.indices) {
                    val a = pattern.getOrNull(j)?.getOrNull(candidate.first - i)
                    val b = pattern.getOrNull(j)?.getOrNull(candidate.second + i)
                    yield(a to b)
                }
            }
        }
    }

    val getHorizontal = fun(pattern: List<List<Char>>, candidate: Point): Sequence<Pair<Char?, Char?>> {
        return sequence {
            for (i in 0..candidate.first) {
                for (j in pattern[0].indices) {
                    val a = pattern.getOrNull(candidate.first - i)?.getOrNull(j)
                    val b = pattern.getOrNull(candidate.second + i)?.getOrNull(j)
                    yield(a to b)
                }
            }
        }
    }

    fun differenceCount(a: List<Char>, b: List<Char>): Int {
        return a.zip(b).count { it.first != it.second }
    }

    fun symmetryScore(pattern: List<List<Char>>, expectedErrors: Int = 0): Int {
        val horizontalResult = pattern.indices.windowed(2)
            // horizontal candidates
            .filter { (a, b) -> differenceCount(pattern[a], pattern[b]) <= expectedErrors }
            .filter { (a, b) -> checkSymmetry(getHorizontal(pattern, a to b), expectedErrors) }

        val verticalResult = pattern[0].indices.windowed(2)
            .filter { (a, b) ->
                // vertical candidates
                differenceCount(
                    pattern.map { row -> row[a] },
                    pattern.map { row -> row[b] }) <= expectedErrors
            }
            .filter { (a, b) -> checkSymmetry(getVertical(pattern, a to b), expectedErrors) }

        return horizontalResult.sumOf { (it.first() + 1) * 100 } + verticalResult.sumOf { it.first() + 1 }
    }

    fun part1(input: List<String>): Int {
        val patterns = parseInput(input)
        val candidates = patterns.map { pattern -> symmetryScore(pattern) }
        return candidates.sum()
    }

    fun part2(input: List<String>): Int {
        val patterns = parseInput(input)
        val candidates = patterns.map { pattern -> symmetryScore(pattern, 1) }
        return candidates.sum()
    }


    val testInput = readInput("day13/day13_test")
    check(part1(testInput), 405)
    check(part2(testInput), 400)

    val input = readInput("day13/day13")
    part1(input).println()
    part2(input).println()
}
