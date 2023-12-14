package day14

import Point
import check
import plus
import println
import readInput
import kotlin.math.max
import kotlin.math.min

fun main() {
    fun parseInput(input: List<String>): List<MutableList<Char>> {
        return input.map { it.toMutableList() }.toList()
    }

    operator fun List<MutableList<Char>>.get(point: Point): Char? {
        return this.getOrNull(point.first)?.getOrNull(point.second)
    }

    operator fun List<MutableList<Char>>.set(point: Point, value: Char) {
        this[point.first][point.second] = value
    }

    fun rollRock(map: List<MutableList<Char>>, rock: Point, direction: Point) {
        if (map[rock] == 'O') {
            var current = rock
            map[rock] = '.'
            var next = current + direction
            while (map[next] == '.' && min(next.first, next.second) >= 0 && max(next.first, next.second) < map.size) {
                current = next
                next += direction
            }
            map[current] = 'O'
        }
    }

    fun rollNorth(map: List<MutableList<Char>>) {
        for (i in map.indices) {
            for (j in map[0].indices) {
                rollRock(map, Point(i, j), Point(-1, 0))
            }
        }
    }

    fun rollSouth(map: List<MutableList<Char>>) {
        for (i in map.indices) {
            for (j in map[0].indices) {
                rollRock(map, Point(map.size - i - 1, j), Point(1, 0))
            }
        }
    }

    fun rollWest(map: List<MutableList<Char>>) {
        for (i in map.indices) {
            for (j in map[0].indices) {
                rollRock(map, Point(i, j), Point(0, -1))
            }
        }
    }

    fun rollEast(map: List<MutableList<Char>>) {
        for (i in map.indices) {
            for (j in map[0].indices) {
                rollRock(map, Point(i, map.size - j - 1), Point(0, 1))
            }
        }
    }

    fun calculateScore(map: List<MutableList<Char>>): Int {
        return map.mapIndexed { index, chars -> chars.count { it == 'O' } * (map.size - index) }.sum()
    }

    fun printMap(map: List<MutableList<Char>>) {
        map.forEach {
            it.forEach { cell -> print(cell) }
            println()
        }
    }

    fun part1(input: List<String>): Int {
        val map = parseInput(input)
        rollNorth(map)
//        printMap(map)

        return calculateScore(map)
    }

    fun part2(input: List<String>): Int {
        val map = parseInput(input)

        val states =
            mutableListOf(map.indices.flatMap { map[0].indices.map { c -> it to c } }.filter { map[it] == 'O' })
        var firstOccurrence = 0
        var nextOccurrence = 0

        for (i in 1..1000000000) {
            if (i % 1000000 == 0) println("Cycle $i")

            rollNorth(map)
            rollWest(map)
            rollSouth(map)
            rollEast(map)

            val newState = map.indices.flatMap { map[0].indices.map { c -> it to c } }.filter { map[it] == 'O' }
            if (newState in states) {
                firstOccurrence = states.indexOf(newState)
                nextOccurrence = states.size
                break
            } else states.add(newState)

        }

        val index = ((1000000000 - firstOccurrence) % (nextOccurrence - firstOccurrence)) + firstOccurrence
        val rollers = states[index]
        for (i in map.indices) {
            for (j in map[0].indices) {
                if (map[i to j] == 'O') map[i to j] = '.'
                if (i to j in rollers) map[i to j] = 'O'
            }
        }
        return calculateScore(map)
    }


    val testInput = readInput("day14/day14_test")
    check(part1(testInput), 136)
    check(part2(testInput), 64)

    val input = readInput("day14/day14")
    part1(input).println()
    part2(input).println()
}
