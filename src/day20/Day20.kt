package day20

import check
import lcm
import println
import readInput

enum class PulseValue {
    LOW, HIGH
}

fun main() {
    data class Pulse(val source: String, val destination: String, val pulse: PulseValue)

    abstract class Module(val name: String, val outputs: List<String>) {
        abstract fun handlePulse(source: String, pulse: PulseValue): List<Pulse>
    }

    class NoOp(name: String, outputs: List<String>) : Module(name, outputs) {
        override fun handlePulse(source: String, pulse: PulseValue): List<Pulse> {
            println("$name received $pulse from $source")
            return listOf()
        }
    }

    class FlipFlop(name: String, outputs: List<String>, var state: PulseValue = PulseValue.LOW) :
        Module(name, outputs) {
        override fun handlePulse(source: String, pulse: PulseValue): List<Pulse> {
            if (pulse == PulseValue.LOW) {
                state = if (state == PulseValue.LOW) PulseValue.HIGH else PulseValue.LOW
                return outputs.map { Pulse(name, it, state) }
            }
            return listOf()
        }
    }

    class Conjunction(name: String, outputs: List<String>) : Module(name, outputs) {
        var inputPulses: MutableMap<String, PulseValue> = mutableMapOf()

        override fun handlePulse(source: String, pulse: PulseValue): List<Pulse> {
            inputPulses[source] = pulse

            return if (inputPulses.all { it.value == PulseValue.HIGH }) {
                outputs.map { Pulse(name, it, PulseValue.LOW) }
            } else {
                outputs.map { Pulse(name, it, PulseValue.HIGH) }
            }
        }
    }

    class Broadcaster(name: String, outputs: List<String>) : Module(name, outputs) {
        override fun handlePulse(source: String, pulse: PulseValue): List<Pulse> {
            return outputs.map { Pulse(name, it, pulse) }
        }
    }

    fun parseInput(input: List<String>): List<Module> {
        val modulePattern = """([a-z]+) -> ([a-z, ]+)""".toRegex()
        val modules = input.map { moduleString ->
            val (name, outputs) = modulePattern.find(moduleString)!!.groupValues.drop(1)
            val outs = outputs.split(", ")

            when (moduleString[0]) {
                '%' -> FlipFlop(name, outs)
                '&' -> Conjunction(name, outs)
                else -> if (name == "broadcaster") Broadcaster(name, outs) else NoOp(name, outs)
            }
        }

        // Determine inputs of conjunction modules
        modules.forEach { module ->
            if (module is Conjunction) {
                module.inputPulses =
                    modules.filter { it.outputs.contains(module.name) }.map { it.name }.associateWith { PulseValue.LOW }
                        .toMutableMap()
            }
        }

        return modules
    }

    fun part1(input: List<String>): Long {
        val modules = parseInput(input).associateBy { it.name }
        val pulseQueue = ArrayDeque<Pulse>()

        var lowPulseCount = 0L
        var highPulseCount = 0L

        repeat(1000) {
            pulseQueue.add(Pulse("button", "broadcaster", PulseValue.LOW))

            while (pulseQueue.isNotEmpty()) {
                val current = pulseQueue.removeFirst()
//                println(current)
                if (current.pulse == PulseValue.LOW) lowPulseCount += 1 else highPulseCount += 1
                // Some modules have no outputs, so they are not in the map
                modules[current.destination]?.let { pulseQueue.addAll(it.handlePulse(current.source, current.pulse)) }
            }
        }

        return lowPulseCount * highPulseCount
    }

    fun part2(input: List<String>): Long {
        val modules = parseInput(input).associateBy { it.name }
        val pulseQueue = ArrayDeque<Pulse>()

        val rxInput = modules.values.first { it.outputs.contains("rx") }
        val counters = modules.values.filter { it.outputs.contains(rxInput.name) }.map { it.name }
        val cycles = mutableMapOf<String, Long>()

        var buttonPresses = 0L

        while (!cycles.keys.containsAll(counters)) {
            pulseQueue.add(Pulse("button", "broadcaster", PulseValue.LOW))
            buttonPresses += 1

            while (pulseQueue.isNotEmpty()) {
                val current = pulseQueue.removeFirst()

                /*
                rx has exactly one input. This input is a conjunction module connected to four other modules which
                also are conjunction modules. So if all of them output high (receive a low input) then rx receives low.

                So if we calculate when the four connected inputs receive low (and then output high) we can determine the
                cycle of each of them:

                The lcm of these is the first time when all of them are low (giving high output) at the same time and
                therefore the final conjunction delivers low to rx.

                Of course all of this relies heavily on the input being engineered in this way. Otherwise, the problem
                would be much harder to solve.
                */

                if (current.pulse == PulseValue.LOW && current.destination in counters) {
                    cycles[current.destination] = buttonPresses
                }

                // Some modules have no outputs, so they are not in the map
                modules[current.destination]?.let { pulseQueue.addAll(it.handlePulse(current.source, current.pulse)) }
            }
        }

        println(cycles)
        return lcm(cycles.values.toList())
    }


    val testInput = readInput("day20/day20_test")
    check(part1(testInput), 32000000L)

    val input = readInput("day20/day20")
    part1(input).println()
    part2(input).println()
}
