package day15

import check
import println
import readInput

fun main() {
    fun parseInput(input: List<String>): List<String> {
        return input.flatMap { it.split(",") }
    }

    fun hash(value: String): Int {
        return value.fold(0) { acc, next ->
            ((acc + next.code) * 17) % 256
        }
    }

    fun part1(input: List<String>): Int {
        val steps = parseInput(input)
        return steps.sumOf { hash(it) }
    }

    data class BoxEntry(var label: String, var lens: Int)

    fun part2(input: List<String>): Int {
        val steps = parseInput(input)
        // for example: Box 0: [rn 1] [cm 2]
        val boxes = mutableMapOf<Int, MutableList<BoxEntry>>()

        for (step in steps) {
            val pattern = """([a-z]+)([=-])(\d?)""".toRegex()
            val (label, op, lens) = pattern.matchEntire(step)!!.groupValues.drop(1)
            if (op == "=") {
                val boxIndex = hash(label)
                val lensValue = lens.toInt()

                if (boxIndex !in boxes) {
                    boxes[boxIndex] = mutableListOf()
                }

                val box = boxes[boxIndex]!!
                if (label in box.map { it.label }) {
                    box.first { it.label == label }.lens = lensValue
                } else {
                    box.add(BoxEntry(label, lensValue))
                }
            }
            if (op == "-") {
                val boxIndex = hash(label)

                if (boxIndex in boxes) {
                    val box = boxes[boxIndex]!!
                    if (label in box.map { it.label }) {
                        box.remove(box.first { it.label == label })
                    }
                }
            }
        }

        return boxes.map { box ->
            box.value.mapIndexed { index, entry -> (index + 1) * entry.lens }.map { it * (box.key + 1) }.sum()
        }.sum()
    }


    val testInput = readInput("day15/day15_test")
    check(part1(testInput), 1320)
    check(part2(testInput), 145)

    val input = readInput("day15/day15")
    part1(input).println()
    part2(input).println()
}
