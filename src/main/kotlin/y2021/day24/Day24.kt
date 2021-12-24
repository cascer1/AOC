@file:OptIn(ExperimentalTime::class)

package y2021.day24

import readInput
import java.util.*
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

var instructions: List<String> = listOf()

fun main() {
//    val testInput = readInput("Day24_test")
//    parseInput(testInput)
//    check(part1() == 1L)
//    println("Part 1 check successful!")
//    check(part2() == 2)
//    println("Part 2 check successful!")

    instructions = readInput("Day24_modified")
    val part1Duration: Duration = measureTime {
        println(part1())
    }
    val part2Duration: Duration = measureTime {
        println(part2())
    }

    println("Part 1 time: ${part1Duration.toDouble(DurationUnit.MILLISECONDS)} ms")
    println("Part 2 time: ${part2Duration.toDouble(DurationUnit.MILLISECONDS)} ms")
}

fun part1(): Long {
    // Find the largest valid model number
    var highestModelNumber = 0L
    val program = Program(instructions)
    (99_999_999_999_999 downTo 1).forEach { modelNumber ->
        if (modelNumber.toString().contains("0")) {
            // Model numbers must not contain zeroes
            return@forEach
        }
        val result = program.run(modelNumber.toString())
        if (result == 0) {
            return modelNumber
        }
    }

    return highestModelNumber
}

fun part2(): Int {
    return 2
}

data class Program(val instructions: List<String>) {
    val variables: HashMap<Char, Long> = hashMapOf()

    fun runSmart(input: String): Boolean {
        val z: Stack<Int> = Stack()
        var w = 0

        z.push(input[0].digitToInt() + 15)
        z.push(input[1].digitToInt() + 10)
        z.push(input[2].digitToInt() + 2)
        z.push(input[3].digitToInt() + 16)

        w = input[4].digitToInt()

        if (w != z.pop() - 12) {
            z.push(w + 12)
        }

        z.push(input[5].digitToInt() + 11)

        //TODO: numbers 7-14

        return z.isEmpty()
    }

    fun run(input: String): Int {
        variables.clear()
        var thisInput = input

        instructions.forEach { instruction ->
            val instructionParts = instruction.split(' ')

            when (instructionParts[0]) {
                "inp" -> thisInput = input(thisInput, instructionParts[1].first())
                "add" -> add(instructionParts[1].first(), instructionParts[2])
                "mul" -> mul(instructionParts[1].first(), instructionParts[2])
                "div" -> div(instructionParts[1].first(), instructionParts[2])
                "mod" -> mod(instructionParts[1].first(), instructionParts[2])
                "eql" -> eql(instructionParts[1].first(), instructionParts[2])
            }
        }

        return variables['z']!!.toInt()
    }

    private fun input(input: String, variableName: Char): String {
        variables[variableName] = input.first().digitToInt().toLong()
        return input.drop(1)
    }

    private fun add(variableOne: Char, variableTwo: String) {
        val firstNumber = variables[variableOne] ?: 0
        val secondNumber = parseVariable(variableTwo)

        variables[variableOne] = firstNumber + secondNumber
    }

    private fun mul(variableOne: Char, variableTwo: String) {
        val firstNumber = variables[variableOne] ?: 0
        val secondNumber = parseVariable(variableTwo)

        variables[variableOne] = firstNumber * secondNumber
    }

    private fun div(variableOne: Char, variableTwo: String) {
        val firstNumber = variables[variableOne] ?: 0
        val secondNumber = parseVariable(variableTwo)

        if (secondNumber == 0L) {
            return
        }

        variables[variableOne] = firstNumber / secondNumber
    }

    private fun mod(variableOne: Char, variableTwo: String) {
        val firstNumber = variables[variableOne] ?: 0
        val secondNumber = parseVariable(variableTwo)

        if (firstNumber < 0 || secondNumber <= 0) {
            return
        }

        variables[variableOne] = firstNumber % secondNumber
    }

    private fun eql(variableOne: Char, variableTwo: String) {
        val firstNumber = variables[variableOne] ?: 0L
        val secondNumber = parseVariable(variableTwo)

        variables[variableOne] = if (firstNumber == secondNumber) 1 else 0
    }

    private fun parseVariable(variable: String): Long {
        return try {
            variable.toInt().toLong()
        } catch (ex: NumberFormatException) {
            variables[variable.first()] ?: 0
        }
    }
}
