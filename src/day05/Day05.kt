package day05

import println
import readInput
import kotlin.math.min

fun main() {
    data class ConversionMapEntry(val destinationStart: Long, val sourceStart: Long, val range: Long) {
        fun isInRange(value: Long): Boolean {
            return value in sourceStart until sourceStart + range
        }

        fun mapValue(value: Long): Long {
            return destinationStart + (value - sourceStart)
        }
    }

    fun mapValue(value: Long, conversions: List<ConversionMapEntry>): Long {
        for (conversion in conversions) {
            if (conversion.isInRange(value)) {
                return conversion.mapValue(value)
            }
        }
        return value
    }

    fun parseInput(input: List<String>): Pair<List<Long>, List<List<ConversionMapEntry>>> {
        var seeds: List<Long> = listOf()
        val conversionMaps: MutableList<List<ConversionMapEntry>> = mutableListOf()

        var currentMap: MutableList<ConversionMapEntry> = mutableListOf()
        for (line in input) {
            if (line.startsWith("seeds:")) {
                seeds = line.substringAfter(": ").split(" ").map { it.toLong() }
            }

            if (line.isEmpty()) {
                if (currentMap.isNotEmpty()) {
                    conversionMaps.add(currentMap)
                    currentMap = mutableListOf()
                }
            } else if (line[0].isLetter()) {
//                println("Parsing $line")
            } else {
                val values = line.split(" ").map { it.toLong() }
                currentMap.add(ConversionMapEntry(values[0], values[1], values[2]))
            }
        }
        if (currentMap.isNotEmpty()) {
            conversionMaps.add(currentMap)
        }

        return seeds to conversionMaps
    }

    fun part1(input: List<String>): Long {
        val (seeds, conversionMaps) = parseInput(input)

        var currentValues = seeds.toMutableList()
        for (conversionMap in conversionMaps) {
            currentValues = currentValues.map { mapValue(it, conversionMap) }.toMutableList()
        }

        return currentValues.min()
    }

    fun part2(input: List<String>): Long {
        val (seeds, conversionMaps) = parseInput(input)

        var minValue = Long.MAX_VALUE
        for (index in seeds.indices step 2) {
            println("Checking seed: ${seeds[index]}")
            val longRange = seeds[index] until seeds[index] + seeds[index + 1]
            for (value in longRange) {
                var currentValue = value
                for (conversionMap in conversionMaps) {
                    currentValue = mapValue(currentValue, conversionMap)
                }
                if (currentValue < minValue) {
                    minValue = currentValue
                    println("New min value: $minValue")
                }
            }
        }

        return minValue
    }


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day05/day05_test")
    check(part1(testInput) == 35L)
    check(part2(testInput) == 46L)

    val input = readInput("day05/day05")
    part1(input).println()
    part2(input).println()
}
