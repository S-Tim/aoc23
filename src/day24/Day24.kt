package day24

import check
import println
import readInput

fun main() {
    data class Function(val x: Long, val y: Long, val z: Long, val dx: Long, val dy: Long, val dz: Long) {
        // linear regression
        val a = ((y + dy) - y).toDouble() / ((x + dx) - x).toDouble()
        val b = -a * x + y

        fun intersection(other: Function): Pair<Double, Double>? {
            val db = other.b - b
            val da = a - other.a

            // parallel functions
            if (da == 0.0) {
                return if (db == 0.0) Pair(0.0, 0.0) else null
            }

            // intersection
            val x = db / da
            val y = a * x + b
            return Pair(x, y)
        }
    }

    fun parseInput(input: List<String>): List<Function> {
        val pattern = """(\d+), \s*(\d+), \s*(\d+) \s*@ \s*(-?\d+), \s*(-?\d+), \s*(-?\d+)""".toRegex()

        val functions = input.map { pattern.matchEntire(it)!!.groupValues.drop(1).map { value -> value.toLong() } }
        return functions.map { (x, y, z, dx, dy, dz) -> Function(x, y, z, dx, dy, dz) }
    }

    fun part1(
        input: List<String>,
        validRange: ClosedFloatingPointRange<Double> = 200000000000000.0..400000000000000.0
    ): Int {
        val functions = parseInput(input)
        var intersectionInsideTestArea = 0

        for (i in functions.indices) {
            for (j in i + 1 until functions.size) {
                val f1 = functions[i]
                val f2 = functions[j]
                val intersection = f1.intersection(f2)

                println(f1)
                println(f2)

                if (intersection == null) {
                    println("parallel")
                } else if (intersection.first < f1.x && f1.dx > 0 || intersection.first > f1.x && f1.dx < 0) {
                    println("past")
                } else if (intersection.first < f2.x && f2.dx > 0 || intersection.first > f2.x && f2.dx < 0) {
                    println("past")
                } else {
                    if (intersection.first in validRange && intersection.second in validRange) {
                        println("inside")
                        intersectionInsideTestArea += 1
                    } else {
                        println("outside")
                    }
                }
                println(intersection)
                println()
            }
        }

        return intersectionInsideTestArea
    }

    fun part2(input: List<String>): Int {
        return input.size
    }


    val testInput = readInput("day24/day24_test")
    check(part1(testInput, 7.0..27.0), 2)
//    check(part2(testInput), 281)

    val input = readInput("day24/day24")
    part1(input).println()
    part2(input).println()
}

private operator fun <E> List<E>.component6(): E {
    return this[5]
}
