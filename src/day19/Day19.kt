package day19

import check
import println
import readInput

fun main() {
    data class Part(val x: Int, val m: Int, val a: Int, val s: Int) {
        val rating = x + m + a + s

        operator fun get(variable: String): Int {
            return when (variable) {
                "x" -> this.x
                "m" -> this.m
                "a" -> this.a
                "s" -> this.s
                else -> throw IllegalArgumentException()
            }
        }
    }

    class Rule(variable: String, operator: String, constant: String, val next: String) {
        private val predicate: (Part) -> Boolean = when (operator) {
            "<" -> { part: Part -> part[variable] < constant.toInt() }
            ">" -> { part: Part -> part[variable] > constant.toInt() }
            else -> throw IllegalArgumentException()
        }

        operator fun invoke(part: Part): Boolean {
            return predicate(part)
        }
    }

    data class Workflow(val name: String, val rules: List<Rule>, val default: String)


    fun parseInput(input: List<String>): Pair<List<Workflow>, List<Part>> {
        // example: px{a<2006:qkq,m>2090:A,rfg}
        val rulePattern = """([xmas])([<>])(\d+):([a-zA-Z]+),""".toRegex()
        val workflowPattern = """([a-z]+)\{($rulePattern)*([a-zA-Z]+)}""".toRegex()

        val (workflows, parts) = input.joinToString("\n").split("\n\n").map { it.split("\n") }
        val parsedWorkflows = workflows.map { workflow ->
            val workflowMatch = workflowPattern.matchEntire(workflow)!!
            val ruleMatches = rulePattern.findAll(workflow).toList()

            val rules = ruleMatches.map { ruleMatch ->
                val variable = ruleMatch.groupValues[1]
                val operator = ruleMatch.groupValues[2]
                val constant = ruleMatch.groupValues[3]
                val next = ruleMatch.groupValues[4]

                Rule(variable, operator, constant, next)
            }
            val workflowName = workflowMatch.groupValues[1]
            val workflowDefault = workflowMatch.groupValues.last()
            Workflow(workflowName, rules, workflowDefault)
        }

        val partPattern = """\{x=(\d+),m=(\d+),a=(\d+),s=(\d+)}""".toRegex()
        val parsedParts =
            parts.map { partPattern.matchEntire(it)!!.groupValues.drop(1).map { variable -> variable.toInt() } }
                .map { (x, m, a, s) -> Part(x, m, a, s) }

        return Pair(parsedWorkflows, parsedParts)
    }

    fun isPartAccepted(part: Part, workflows: Map<String, Workflow>): Boolean {
        var next = "in"

        while (next != "A" && next != "R") {
            val current = workflows[next]!!
            next = current.rules.firstOrNull { it(part) }?.next ?: current.default
        }

        return next == "A"
    }

    fun part1(input: List<String>): Int {
        val (workflows, parts) = parseInput(input)
        val workflowMap = workflows.associateBy { it.name }

        return parts.filter { isPartAccepted(it, workflowMap) }.sumOf { it.rating }
    }

    fun part2(input: List<String>): Int {
        return input.size
    }


    val testInput = readInput("day19/day19_test")
    check(part1(testInput), 19114)
//    check(part2(testInput), 281)

    val input = readInput("day19/day19")
    part1(input).println()
    part2(input).println()
}
