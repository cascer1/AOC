package y2015.day07

import readInput
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

val wires: HashMap<String, Wire> = hashMapOf()

@OptIn(ExperimentalTime::class)
fun main() {
    val inputFile = "2015/Day07"
    val input = readInput(inputFile)
    val part1Duration: Duration = measureTime {
        println(part1(input))
    }
    val part2Duration: Duration = measureTime {
        println(part2(input))
    }

    println("Part 1 time: ${part1Duration.toDouble(DurationUnit.MILLISECONDS)} ms")
    println("Part 2 time: ${part2Duration.toDouble(DurationUnit.MILLISECONDS)} ms")
}

private fun part1(input: List<String>): Int {
    parseInput(input)
    return wires["a"]!!.signal()!!.toInt()
}

private fun part2(input: List<String>): Int {
    parseInput(input)
    val a = wires["a"]!!.signal()!!
    wires.forEach { (_, u) -> u.reset() }
    wires["b"]!!.result = a
    return wires["a"]!!.signal()!!.toInt()
}

fun parseInput(input: List<String>) {
    wires.clear()

    input.forEach { instruction ->
        val (before, after) = instruction.split(" -> ")
        val beforeParts = before.split(" ")

        val outputWire = wires.getOrPut(after) { Wire(after) }
        val gate: NewGate
        if (beforeParts.size == 1) {
            gate = NewGate(operation = OperationType.INPUT, outputWire = outputWire)
            try {
                gate.numberOne = beforeParts[0].toUShort()
            } catch (e: NumberFormatException) {
                gate.wireOne = wires.getOrPut(beforeParts[0]) { Wire(beforeParts[0]) }
            }

        } else if (beforeParts[0] == "NOT") {
            gate = NewGate(operation = OperationType.NOT, outputWire = outputWire)
            try {
                gate.numberOne = beforeParts[1].toUShort()
            } catch (e: NumberFormatException) {
                gate.wireOne = wires.getOrPut(beforeParts[1]) { Wire(beforeParts[1]) }
            }
        } else {
            gate = NewGate(operation = OperationType.valueOf(beforeParts[1]), outputWire = outputWire)

            try {
                gate.numberOne = beforeParts[0].toUShort()
            } catch (e: NumberFormatException) {
                gate.wireOne = wires.getOrPut(beforeParts[0]) { Wire(beforeParts[0]) }
            }

            try {
                gate.numberTwo = beforeParts[2].toUShort()
            } catch (e: NumberFormatException) {
                gate.wireTwo = wires.getOrPut(beforeParts[2]) { Wire(beforeParts[2]) }
            }
        }

        if (outputWire.inputGate != null) {
            throw IllegalStateException("Wires must not have two inputs")
        }

        outputWire.inputGate = gate
    }
}

enum class OperationType {
    OR, AND, NOT, LSHIFT, RSHIFT, INPUT
}

data class Wire(val name: String, var inputGate: NewGate? = null) {
    var result: UShort? = null

    fun reset() {
        result = null
    }

    fun signal(): UShort? {
        if (result == null) {
            result = inputGate!!.calculate()
        }
        return result
    }

    override fun toString(): String {
        return "[$name] -> $result"
    }
}

data class NewGate(val operation: OperationType, var wireOne: Wire? = null, var wireTwo: Wire? = null, var numberOne: UShort? = null, var numberTwo: UShort? = null, val outputWire: Wire) {
    fun calculate(): UShort {
        return when(operation) {
            OperationType.INPUT -> firstNumber
            OperationType.OR -> firstNumber.or(secondNumber)
            OperationType.AND -> firstNumber.and(secondNumber)
            OperationType.NOT -> firstNumber.xor(UShort.MAX_VALUE)
            OperationType.LSHIFT -> (firstNumber.toInt() shl secondNumber.toInt()).toUShort()
            OperationType.RSHIFT -> (firstNumber.toInt() shr secondNumber.toInt()).toUShort()
        }
    }

    private val firstNumber: UShort
        get() = wireOne?.signal() ?: numberOne!!

    private val secondNumber: UShort
        get() = wireTwo?.signal() ?: numberTwo!!

    override fun toString(): String {
        val firstWire = wireOne?.name ?: numberOne ?: ""
        val secondWire = wireTwo?.name ?: numberTwo ?: ""

        return "$firstWire $operation $secondWire -> ${outputWire.name}"
    }
}

