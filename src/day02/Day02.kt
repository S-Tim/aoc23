package day02

import println
import readInput

fun main() {
    data class Cube(val color: String, val amount: Int)

    data class Round(val cubes: List<Cube>) {
        fun isPossible(): Boolean {
            val maxValues = mapOf("red" to 12, "green" to 13, "blue" to 14)
            return cubes.all { it.amount <= maxValues[it.color]!! }
        }
    }

    data class Game(val id: Int, val rounds: List<Round>) {
        fun isPossible(): Boolean = rounds.all { it.isPossible() }

        fun requiredCubes(): List<Cube> {
            return rounds
                .flatMap { it.cubes }
                .groupBy({ it.color }, { it.amount })
                .map { Cube(it.key, it.value.max()) }
        }
    }

    fun parseGames(games: List<String>): List<Game> {
        return games
            .map { it.substringAfter(": ") }
            .map { game ->
                game.split("; ")
                    .map { round ->
                        round.split(", ")
                            .map { cube -> cube.split(" ") }
                            .map { (amount, color) -> Cube(color, amount.toInt()) }
                    }.map { Round(it) }
            }.mapIndexed { index, game -> Game(index + 1, game) }
    }


    fun part1(input: List<String>): Int {
        return parseGames(input).filter { it.isPossible() }.sumOf { it.id }
    }

    fun part2(input: List<String>): Int {
        return parseGames(input).sumOf { game ->
            game.requiredCubes().map { cube -> cube.amount }
                .reduce { product, amount -> product * amount }
        }
    }


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day02/day02_test")
    check(part1(testInput) == 8)
    check(part2(testInput) == 2286)

    val input = readInput("day02/day02")
    part1(input).println()
    part2(input).println()
}
