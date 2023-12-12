package day11

import Point
import check
import println
import readInput
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun main() {
    fun parseInput(input: List<String>): Triple<List<Int>, List<Int>, List<Point>> {
        val emptyRows = input.indices.filter { index -> input[index].all { it == '.' } }
        val emptyCols = (input.indices).filter { index -> input.all { it[index] == '.' } }

        val galaxies = input.indices.flatMap { row -> input[0].indices.map { col -> row to col } }
            .filter { input[it.first][it.second] == '#' }

        return Triple(emptyRows, emptyCols, galaxies)
    }

    fun calculateDistances(
        emptyRows: List<Int>,
        emptyCols: List<Int>,
        galaxies: List<Pair<Int, Int>>,
        multiplier: Int
    ): Map<Set<Pair<Int, Int>>, Long> {
        return buildMap {
            galaxies.flatMap { g -> galaxies.map { g to it } }.forEach { (a, b) ->
                if (setOf(a, b) !in this) {
                    val (minRow, maxRow) = listOf(a.first, b.first).sorted()
                    val (minCol, maxCol) = listOf(a.second, b.second).sorted()

                    val emptyRowCount = emptyRows.count { it in minRow until maxRow }
                    val emptyColCount = emptyCols.count { it in minCol until maxCol }

                    val distanceRows = maxRow - minRow + emptyRowCount * multiplier - emptyRowCount
                    val distanceCols = maxCol - minCol + emptyColCount * multiplier - emptyColCount

                    this[setOf(a, b)] = distanceCols.toLong() + distanceRows.toLong()
                }
            }
        }
    }

    fun part1(input: List<String>): Long {
        val (emptyRows, emptyCols, galaxies) = parseInput(input)
        val distances: Map<Set<Point>, Long> = calculateDistances(emptyRows, emptyCols, galaxies, 2)
        return distances.values.sum()
    }

    fun part2(input: List<String>, multiplier: Int = 1000000): Long {
        val (emptyRows, emptyCols, galaxies) = parseInput(input)
        val distances: Map<Set<Point>, Long> = calculateDistances(emptyRows, emptyCols, galaxies, multiplier)
        return distances.values.sum()
    }

    val testInput = readInput("day11/day11_test")
    check(part1(testInput), 374)
    check(part2(testInput, 10), 1030)
    check(part2(testInput, 100), 8410)

    val input = readInput("day11/day11")
    part1(input).println()
    part2(input).println()
}
