package day22

import Point3d
import check
import plus
import println
import readInput
import kotlin.math.max
import kotlin.math.min

fun main() {
    data class Brick(val pos1: Point3d, val pos2: Point3d, val letter: Char) {
        val coordinates: Set<Point3d> = calculateCoordinates()

        private fun calculateCoordinates(): Set<Point3d> {
            val result = mutableSetOf<Point3d>()
            val (x1, y1, z1) = pos1
            val (x2, y2, z2) = pos2

            // only one dimension can change because all the bricks are straight lines
            (min(x1, x2)..max(x1, x2)).forEach { result.add(Point3d(it, y1, z1)) }
            (min(y1, y2)..max(y1, y2)).forEach { result.add(Point3d(x1, it, z1)) }
            (min(z1, z2)..max(z1, z2)).forEach { result.add(Point3d(x1, y1, it)) }

            return result
        }
    }

    fun String.toPoint3d(): Point3d {
        val (x, y, z) = this.split(",").map { it.toInt() }
        return Point3d(x, y, z)
    }

    fun parseInput(input: List<String>): List<Brick> {
        val sequence = generateSequence('A') { it.plus(1) }
        return input.map { s -> s.split("~") }
            .mapIndexed { index, (pos1, pos2) -> Brick(pos1.toPoint3d(), pos2.toPoint3d(), sequence.elementAt(index)) }
    }

    fun moveDown(bricks: List<Brick>): List<Brick> {
        val map = bricks.flatMap { it.coordinates }.toMutableSet()
        val result = bricks.toMutableList()

        // let all bricks fall as far as they can (in a very inefficient matter)
        var moved = true
        while (moved) {
            moved = false

            for (i in bricks.indices) {
                var brick = result.removeFirst()
                map.removeAll(brick.coordinates)
                val newCoordinates = brick.coordinates.map { Point3d(it.first, it.second, it.third - 1) }
                if (newCoordinates.all { it.third > 0 } && newCoordinates.all { it !in map }) {
                    moved = true
                    brick = Brick(brick.pos1 + Point3d(0, 0, -1), brick.pos2 + Point3d(0, 0, -1), brick.letter)
                }
                map.addAll(brick.coordinates)
                result.add(brick)
            }
        }

        return result
    }

    fun calculateSupportedBy(brick: Brick, bricks: List<Brick>): Set<Brick> {
        // Any brick that has any coordinate directly underneath the brick is a supporter
        val below = brick.coordinates.map { Point3d(it.first, it.second, it.third - 1) }
        return bricks.filter { other -> other != brick && below.any { it in other.coordinates } }.toSet()
    }

    fun calculateSupports(brick: Brick, bricks: List<Brick>): Set<Brick> {
        // Any brick that has any coordinate directly above the brick is supported by it
        val above = brick.coordinates.map { Point3d(it.first, it.second, it.third + 1) }
        return bricks.filter { other -> other != brick && above.any { it in other.coordinates } }.toSet()
    }

    fun part1(input: List<String>): Int {
        val bricks = moveDown(parseInput(input))

        // determine which bricks are supported by which bricks
        val supportedBy = bricks.associateWith { brick -> calculateSupportedBy(brick, bricks) }
        // determine which brick supports which bricks
        val supports = bricks.associateWith { brick -> calculateSupports(brick, bricks) }

        // every brick supported by the specified brick needs to have another supporter
        val canBeDisintegrated = bricks.associateWith { brick ->
            supports[brick]!!.all { other -> supportedBy[other]!!.any { supporter -> supporter != brick } }
        }

        return canBeDisintegrated.count { it.value }
    }

    fun wouldFallWhenDisintegrated(
        brick: Brick,
        bricks: List<Brick>,
        supportedBy: Map<Brick, Set<Brick>>,
        supports: Map<Brick, Set<Brick>>
    ): Set<Brick> {
        // find all by bricks supported only by this brick, then all that are only supported by those etc.
        val wouldFall = mutableSetOf<Brick>()
        val open = bricks.filter { supportedBy[it] == setOf(brick) }.toMutableList()

        while (open.isNotEmpty()) {
            val current = open.removeFirst()
            if (current in wouldFall) continue
            wouldFall.add(current)
            // add all the bricks that are only supported by this brick or other bricks that would fall already
            open.addAll(supports[current]?.filter { supportedBy[it]!!.all { neighbor -> neighbor in wouldFall } }!!)
        }

        return wouldFall
    }

    fun part2(input: List<String>): Int {
        val bricks = moveDown(parseInput(input))

        // determine which bricks are supported by which bricks
        val supportedBy = bricks.associateWith { brick -> calculateSupportedBy(brick, bricks) }
        // determine which brick supports which bricks
        val supports = bricks.associateWith { brick -> calculateSupports(brick, bricks) }

        val fallingBricks =
            bricks.associateWith { brick -> wouldFallWhenDisintegrated(brick, bricks, supportedBy, supports) }
        return fallingBricks.values.sumOf { it.count() }
    }


    val testInput = readInput("day22/day22_test")
    check(part1(testInput), 5)
    check(part2(testInput), 7)

    val input = readInput("day22/day22")
    part1(input).println()
    part2(input).println()
}
