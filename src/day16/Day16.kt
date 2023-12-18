package day16

import Point
import check
import plus
import println
import readInput

enum class Direction(val vector: Point) {
    NORTH(-1 to 0), EAST(0 to 1), SOUTH(1 to 0), WEST(0 to -1)
}

fun main() {
    data class Beam(val position: Point, val direction: Direction) {
        fun move(direction: Direction): Beam = Beam(position + direction.vector, direction)
    }

    operator fun List<String>.get(point: Point) = this.getOrNull(point.first)?.getOrNull(point.second)

    fun move(beam: Beam, contraption: List<String>): List<Beam> {
        val current = contraption[beam.position] ?: return listOf()
        val beams = mutableListOf<Beam>()

        when (current) {
            '/' -> {
                when (beam.direction) {
                    Direction.NORTH -> beams.add(beam.move(Direction.EAST))
                    Direction.EAST -> beams.add(beam.move(Direction.NORTH))
                    Direction.SOUTH -> beams.add(beam.move(Direction.WEST))
                    Direction.WEST -> beams.add(beam.move(Direction.SOUTH))
                }
            }

            '\\' -> {
                when (beam.direction) {
                    Direction.NORTH -> beams.add(beam.move(Direction.WEST))
                    Direction.EAST -> beams.add(beam.move(Direction.SOUTH))
                    Direction.SOUTH -> beams.add(beam.move(Direction.EAST))
                    Direction.WEST -> beams.add(beam.move(Direction.NORTH))
                }
            }

            '|' -> {
                when (beam.direction) {
                    Direction.NORTH -> beams.add(beam.move(Direction.NORTH))
                    Direction.EAST -> {
                        beams.add(beam.move(Direction.NORTH))
                        beams.add(beam.move(Direction.SOUTH))
                    }

                    Direction.SOUTH -> beams.add(beam.move(Direction.SOUTH))
                    Direction.WEST -> {
                        beams.add(beam.move(Direction.NORTH))
                        beams.add(beam.move(Direction.SOUTH))
                    }
                }
            }

            '-' -> {
                when (beam.direction) {
                    Direction.NORTH -> {
                        beams.add(beam.move(Direction.WEST))
                        beams.add(beam.move(Direction.EAST))
                    }

                    Direction.EAST -> beams.add(beam.move(Direction.EAST))
                    Direction.SOUTH -> {
                        beams.add(beam.move(Direction.WEST))
                        beams.add(beam.move(Direction.EAST))
                    }

                    Direction.WEST -> beams.add(beam.move(Direction.WEST))
                }
            }

            else -> {
                beams.add(beam.move(beam.direction))
            }
        }

        return beams.filter { it.position.first >= 0 && it.position.first < contraption.size && it.position.second >= 0 && it.position.second < contraption.size }
    }

    fun calculateEnergizedTiles(contraption: List<String>, startingBeam: Beam): Int {
        val seen = mutableSetOf<Beam>()
        var beams = mutableListOf(startingBeam)

        while (beams.isNotEmpty()) {
            seen.addAll(beams)
            beams = beams.flatMap { move(it, contraption) }.filter { it !in seen }.toMutableList()
        }

        return seen.map { it.position }.toSet().count()
    }

    fun printMap(contraption: List<String>, energizedTiles: Set<Beam>) {
        for (i in contraption.indices) {
            for (j in contraption.indices) {
                if (i to j in energizedTiles.map { it.position }) print('#') else print(contraption[i][j])
            }
            println()
        }
        println()
    }

    fun part1(input: List<String>): Int {
        return calculateEnergizedTiles(input, Beam(Point(0, 0), Direction.EAST))
    }

    fun part2(input: List<String>): Int {
        val startingBeams = input.indices.flatMap {
            listOf(
                Beam(Point(it, 0), Direction.EAST),
                Beam(Point(it, input.size - 1), Direction.WEST),
                Beam(Point(0, it), Direction.SOUTH),
                Beam(Point(input.size - 1, it), Direction.NORTH)
            )
        }

        return startingBeams.maxOf { calculateEnergizedTiles(input, it) }
    }


    val testInput = readInput("day16/day16_test")
    check(part1(testInput), 46)
    check(part2(testInput), 51)

    val input = readInput("day16/day16")
    part1(input).println()
    part2(input).println()
}
