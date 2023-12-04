package day03

import println
import readInput

fun main() {
    fun parseAsGrid(input: List<String>): List<List<Char>> {
        return input.map { it.toCharArray().toList() }
    }

    fun hasSymbolNeighbor(row: Int, col: Int, grid: List<List<Char>>): Boolean {
        for (i in -1..1) {
            for (j in -1..1) {
                val current = grid.getOrNull(row + i)?.getOrNull(col + j)
                if (current != null && !current.isDigit() && current != '.') {
                    return true
                }
            }
        }
        return false
    }

    fun getGearNeighbor(row: Int, col: Int, grid: List<List<Char>>): Pair<Int, Int>? {
        for (i in -1..1) {
            for (j in -1..1) {
                val current = grid.getOrNull(row + i)?.getOrNull(col + j)
                if (current != null && current == '*') {
                    return row + i to col + j
                }
            }
        }
        return null
    }

    fun part1(input: List<String>): Long {
        val grid = parseAsGrid(input)
        val partNumbers = mutableListOf<Long>()

        for (r in grid.indices) {
            var currentNumber = ""
            var hasSymbolNeighbor = false

            for (c in grid[0].indices) {
                val currentChar = grid[r][c]

                if (currentChar.isDigit()) {
                    currentNumber += currentChar
                    hasSymbolNeighbor = hasSymbolNeighbor || hasSymbolNeighbor(r, c, grid)
                } else {
                    if (currentNumber != "" && hasSymbolNeighbor) {
                        partNumbers.add(currentNumber.toLong())
                    }
                    currentNumber = ""
                    hasSymbolNeighbor = false
                }

                // at the end of a row
                if (c == grid[0].size - 1) {
                    if (currentNumber != "" && hasSymbolNeighbor) {
                        partNumbers.add(currentNumber.toLong())
                    }
                }
            }
        }
        return partNumbers.sum()
    }

    fun part2(input: List<String>): Long {
        val grid = parseAsGrid(input)
        val gears = mutableMapOf<Pair<Int, Int>, MutableList<Long>>()

        for (r in grid.indices) {
            var currentNumber = ""
            var currentGear: Pair<Int, Int>? = null

            for (c in grid[0].indices) {
                val currentChar = grid[r][c]

                if (currentChar.isDigit()) {
                    currentNumber += currentChar
                    getGearNeighbor(r, c, grid)?.let {
                        if (it !in gears) {
                            gears[it] = mutableListOf()
                        }
                        currentGear = it
                    }
                } else {
                    if (currentNumber != "" && currentGear != null) {
                        gears[currentGear]?.add(currentNumber.toLong())
                    }
                    currentNumber = ""
                    currentGear = null
                }

                // at the end of a row
                if (c == grid[0].size - 1) {
                    if (currentNumber != "" && currentGear != null) {
                        gears[currentGear]?.add(currentNumber.toLong())
                    }
                }
            }
        }
        return gears.filter { it.value.size == 2 }.map { it.value[0] * it.value[1] }.sum()
    }


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day03/day03_test")
    check(part1(testInput) == 4361L)
    check(part2(testInput) == 467835L)

    val input = readInput("day03/day03")
    part1(input).println()
    part2(input).println()
}
