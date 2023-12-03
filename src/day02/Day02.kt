package day02

import println
import readInput

fun main() {
    fun parseRound(round: String): List<Pair<String, Int>> {
        val rounds = round.split(",").map { it.trim() }
        return rounds.map { it.split(" ")[1] to it.split(" ")[0].toInt() }
    }

    fun parseGame(game: String): Pair<Int, List<List<Pair<String, Int>>>> {
        // Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green
        val gameId = Regex("Game (\\d+):").find(game)?.groupValues?.get(1)
        val rounds = game.substringAfter(":").trim().split(";")

        val result = rounds.map { parseRound(it) }
        return gameId?.toInt()!! to result
    }

    fun isRoundPossible(round: List<Pair<String, Int>>): Boolean {
        val maxValues = mapOf("red" to 12, "green" to 13, "blue" to 14)
        return round.all { it.second <= maxValues[it.first]!! }
    }

    fun isGamePossible(game: List<List<Pair<String, Int>>>): Boolean {
        return game.all { isRoundPossible(it) }
    }


    fun part1(input: List<String>): Int {
        return input.map { parseGame(it) }.filter { isGamePossible(it.second) }.sumOf { it.first }
    }

    fun requiredCubesForGame(game: List<List<Pair<String, Int>>>): List<Pair<String, Int>> {
        return game.flatten().groupBy({ value -> value.first }, { value -> value.second })
            .map { it.key to it.value.max() }
    }

    fun part2(input: List<String>): Int {
        return input.map { parseGame(it) }
            .sumOf { game -> requiredCubesForGame(game.second).map { it.second }.reduceRight { a, b -> a * b } }
    }


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day02/day02_test")
    check(part1(testInput) == 8)
    check(part2(testInput) == 2286)

    val input = readInput("day02/day02")
    part1(input).println()
    part2(input).println()
}