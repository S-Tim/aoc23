import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import kotlin.math.max

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("src", "$name.txt")
    .readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

operator fun Pair<Int, Int>.plus(other: Pair<Int, Int>): Pair<Int, Int> {
    return Pair(first + other.first, second + other.second)
}

typealias Point = Pair<Int, Int>

/**
 * Prints a collection of points as a grid.
 *
 * @param points the points that define the grid
 * @param padding extra space to add to the top, bottom, left and right of the grid
 * @param occupiedSymbol symbol used for the grid cells where the points are
 * @param emptySymbol symbol used for grid cells without a point
 * @param printRowNumbers indicates whether line numbers should be printed
 */
fun printAsGrid(
    points: Collection<Point>,
    padding: Int = 0,
    occupiedSymbol: Char = '#',
    emptySymbol: Char = '.',
    printRowNumbers: Boolean = false
) {
    val minX = points.minBy { it.first }.first - padding
    val maxX = points.maxBy { it.first }.first + padding
    val minY = points.minBy { it.second }.second - padding
    val maxY = points.maxBy { it.second }.second + padding

    val rowNumberLength = max(minY.toString().length, maxY.toString().length)
    val columnNumberLength = max(minX.toString().length, maxX.toString().length)

    if (printRowNumbers) {
        val columnNumbers = (minX..maxX).map { it.toString().padStart(columnNumberLength) }
        for (i in 0 until columnNumberLength) {
            print(" ".repeat(rowNumberLength + 1) + columnNumbers.map { it[i] }.joinToString(""))
            println()
        }
    }

    for (y in minY..maxY) {
        if (printRowNumbers) {
            print(y.toString().padStart(rowNumberLength) + " ")
        }
        for (x in minX..maxX) {
            if (Point(x, y) in points) print(occupiedSymbol) else print(emptySymbol)
        }
        println()
    }
    println()
}

/**
 * Splits a string by any number of whitespace between the numbers and then converts them.
 * Also handles whitespace at the beginning and end.
 */
fun String.splitToInts(): List<Int> = this.trim().split("\\s+".toRegex()).map { it.toInt() }

/**
 * Splits a string by any number of whitespace between the numbers and then converts them.
 * Also handles whitespace at the beginning and end.
 */
fun String.splitToLongs(): List<Long> = this.trim().split("\\s+".toRegex()).map { it.toLong() }

fun check(v1: Any, v2: Any) {
    if (v1 != v2) {
        throw IllegalStateException("Expected $v2 but was $v1")
    }
}
