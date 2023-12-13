package day13

import check
import println
import readInput

fun main() {
    fun parseInput(input: List<String>): List<List<String>> {
        return input.joinToString("\n").split("\n\n").map { it.split("\n") }
    }

    fun checkHorizontalSymmetry(pattern: List<String>, candidate: Pair<Int, Int>): Boolean {
        for (i in 0..candidate.first) {
            for (j in pattern[0].indices) {
                val a = pattern.getOrNull(candidate.first - i)?.getOrNull(j)
                val b = pattern.getOrNull(candidate.second + i)?.getOrNull(j)
                if (a == null || b == null) return true
                if (a != b) return false
            }
        }
        return true
    }

    fun checkVerticalSymmetry(pattern: List<String>, candidate: Pair<Int, Int>): Boolean {
        for (i in 0..candidate.first) {
            for (j in pattern.indices) {
                val a = pattern.getOrNull(j)?.getOrNull(candidate.first - i)
                val b = pattern.getOrNull(j)?.getOrNull(candidate.second + i)
                if (a == null || b == null) return true
                if (a != b) return false
            }
        }
        return true
    }

    fun checkVerticalSymmetry2(pattern: List<String>, candidate: Pair<Int, Int>, initialErrors: Int = 0): Boolean {
        var errorCount = initialErrors
        for (i in 0..candidate.first) {
            for (j in pattern.indices) {
                val a = pattern.getOrNull(j)?.getOrNull(candidate.first - i)
                val b = pattern.getOrNull(j)?.getOrNull(candidate.second + i)
                if (a == null || b == null) return errorCount == 1
                if (a != b) {
                    errorCount += 1
                    if (errorCount > 1) {
                        return false
                    }
                }
            }
        }
        return errorCount == 1
    }

    fun checkHorizontalSymmetry2(pattern: List<String>, candidate: Pair<Int, Int>, initialErrors: Int = 0): Boolean {
        var errorCount = initialErrors
        for (i in 0..candidate.first) {
            for (j in pattern[0].indices) {
                val a = pattern.getOrNull(candidate.first - i)?.getOrNull(j)
                val b = pattern.getOrNull(candidate.second + i)?.getOrNull(j)
                if (a == null || b == null) return errorCount == 1
                if (a != b) {
                    errorCount += 1
                    if (errorCount > 1) {
                        return false
                    }
                }
            }
        }
        return errorCount == 1
    }

    fun hasSymmetry(pattern: List<String>): Int {
        val horizontalResult = pattern.indices.windowed(2).filter { pattern[it.first()] == pattern[it.last()] }
            .map { it.first() to it.last() }.filter { checkHorizontalSymmetry(pattern, it) }

        val verticalResult = pattern[0].indices.windowed(2)
            .filter { pattern.map { row -> row[it.first()] } == pattern.map { row -> row[it.last()] } }
            .map { it.first() to it.last() }.filter { checkVerticalSymmetry(pattern, it) }

        return horizontalResult.sumOf { (it.first + 1) * 100 } + verticalResult.sumOf { it.first + 1 }
    }

    fun differenceCount(a: List<Char>, b: List<Char>): Int {
        return a.indices.count { a[it] != b[it] }
    }

    fun hasSymmetry2(pattern: List<String>): Int {
        val horizontalResult = pattern.indices.windowed(2)
            .map { it to differenceCount(pattern[it.first()].toList(), pattern[it.last()].toList()) }
            .filter { it.second <= 1 }
            .filter { checkHorizontalSymmetry2(pattern, it.first.first() to it.first.last(), 0) }

        val verticalResult = pattern[0].indices.windowed(2).map {
            it to differenceCount(pattern.map { row -> row[it.first()] },
                pattern.map { row -> row[it.last()] })
        }
            .filter { it.second <= 1 }
            .filter { checkVerticalSymmetry2(pattern, it.first.first() to it.first.last(), 0) }

        return horizontalResult.sumOf { (it.first.first() + 1) * 100 } + verticalResult.sumOf { it.first.first() + 1 }
    }

    fun part1(input: List<String>): Int {
        val patterns = parseInput(input)

        val candidates = patterns.map { pattern -> hasSymmetry(pattern) }

        return candidates.sum()
    }

    fun part2(input: List<String>): Int {
        val patterns = parseInput(input)

        val candidates = patterns.map { pattern -> hasSymmetry2(pattern) }

        return candidates.sum()
    }


    val testInput = readInput("day13/day13_test")
    check(part1(testInput), 405)
    check(part2(testInput), 400)

    val input = readInput("day13/day13")
    part1(input).println()
    part2(input).println()
}
