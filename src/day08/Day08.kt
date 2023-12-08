package day08

import check
import println
import readInput

fun main() {
    data class Node(val value: String, val left: String, val right: String)

    fun parseInput(input: List<String>): Pair<List<Char>, List<Node>> {
        val instructions = input[0].toCharArray().toList()

        val nodePattern = """(...) = \((...), (...)\)""".toRegex()
        val nodes = input.drop(2)
            .map { nodePattern.matchEntire(it)!!.groupValues }
            .map { (_, value, left, right) -> Node(value, left, right) }

        return instructions to nodes
    }

    fun mapNode(
        start: String,
        nodeMap: Map<String, Node>,
        instructions: List<Char>,
        endCondition: (Node) -> Boolean
    ): Long {
        var currentNode = nodeMap[start]
        var instructionIndex = 0

        while (currentNode != null && !endCondition(currentNode)) {
            val instruction = instructions[instructionIndex % instructions.size]
            currentNode = nodeMap[if (instruction == 'L') currentNode.left else currentNode.right]
            instructionIndex++
        }

        return instructionIndex.toLong()
    }

    fun part1(input: List<String>): Long {
        val (instructions, nodes) = parseInput(input)
        val nodeMap = nodes.associateBy { it.value }

        return mapNode("AAA", nodeMap, instructions) { it.value == "ZZZ" }
    }

    fun findLCM(a: Long, b: Long): Long {
        val larger = if (a > b) a else b
        val maxLcm = a * b
        var lcm = larger
        while (lcm <= maxLcm) {
            if (lcm % a == 0L && lcm % b == 0L) {
                return lcm
            }
            lcm += larger
        }
        return maxLcm
    }

    fun findLCMOfListOfNumbers(numbers: List<Long>): Long {
        var result = numbers[0]
        for (i in 1 until numbers.size) {
            result = findLCM(result, numbers[i])
        }
        return result
    }

    fun part2(input: List<String>): Long {
        val (instructions, nodes) = parseInput(input)
        val nodeMap = nodes.associateBy { it.value }

        val startingNodes = nodes.filter { it.value.endsWith("A") }

        val stepsToFinish =
            startingNodes.map { node -> mapNode(node.value, nodeMap, instructions) { it.value.endsWith("Z") } }

        return findLCMOfListOfNumbers(stepsToFinish)
    }


    val testInput = readInput("day08/day08_1_test")
    check(part1(testInput), 6L)
    val testInput2 = readInput("day08/day08_2_test")
    check(part2(testInput2), 6L)

    val input = readInput("day08/day08")
    part1(input).println()
    part2(input).println()
}
