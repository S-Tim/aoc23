package day05

import println
import readInput
import splitToLongs

fun main() {
    data class ConversionFunction(
        private val destinationStart: Long, private val sourceStart: Long, private val range: Long
    ) {
        operator fun contains(value: Long): Boolean {
            return value in sourceStart until sourceStart + range
        }

        operator fun invoke(value: Long): Long {
            return destinationStart + (value - sourceStart)
        }
    }

    class ConversionMap(entries: List<List<Long>>) {
        val entries: List<ConversionFunction> = entries.map { ConversionFunction(it[0], it[1], it[2]) }

        operator fun get(value: Long): Long {
            return entries.firstOrNull { value in it }?.let { it(value) } ?: value
        }
    }

    fun parseInput(input: List<String>): Pair<List<Long>, List<ConversionMap>> {
        val seeds: List<Long> = input[0].substringAfter(":").splitToLongs()

        // Drop the seed line then filter out the map headers and split the input into the maps
        val maps = input.drop(2).filter { it.isEmpty() || it[0].isDigit() }.joinToString("\n") { it }.split("\n\n")
        val conversionMaps = maps.map { map -> map.lines().map { it.splitToLongs() } }.map { ConversionMap(it) }

        return seeds to conversionMaps
    }

    fun part1(input: List<String>): Long {
        val (seeds, conversionMaps) = parseInput(input)
        return seeds.minOf { conversionMaps.fold(it) { acc, map -> map[acc] } }
    }

    fun part2(input: List<String>): Long {
        val (seeds, conversionMaps) = parseInput(input)

        var minValue = Long.MAX_VALUE
        for ((start, range) in seeds.windowed(2, 2)) {
            println("Checking seed: $start with range $range")
            for (value in start until start + range) {
                val mappedValue = conversionMaps.fold(value) { acc, map -> map[acc] }
                if (mappedValue < minValue) {
                    minValue = mappedValue
                    println("New min value: $minValue")
                }
            }
        }

        return minValue
    }


    val testInput = readInput("day05/day05_test")
    check(part1(testInput) == 35L)
    check(part2(testInput) == 46L)

    val input = readInput("day05/day05")
    part1(input).println()
    part2(input).println()
}
