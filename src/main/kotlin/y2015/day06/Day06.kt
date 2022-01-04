@file:OptIn(ExperimentalTime::class)

package y2015.day06

import TwoDimensionalCoordinates
import readInput
import toggle
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

var lights: Array<Array<Int>> = emptyArray()

fun main() {
    val inputFile = "2015/Day06"
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

fun part1(input: List<String>): Int {
    lights = Array(1000) { Array(1000) { 0 } }
    val instructions = parseInput(input)
    instructions.forEach { executeInstruction(it) }
    return lights.sumOf { it.count { brightness -> brightness > 0 } }
}

fun part2(input: List<String>): Int {
    lights = Array(1000) { Array(1000) { 0 } }
    val instructions = parseInput(input)
    instructions.forEach { executeInstruction(it, true) }
    return lights.sumOf { it.sum() }
}

fun parseInput(input: List<String>): List<Instruction> {
    return input.map { it.removePrefix("turn ") }
            .map { it.split(' ') }
            .map {
                val type = InstructionType.valueOf(it[0].uppercase())
                val from = TwoDimensionalCoordinates.fromString(it[1])
                val to = TwoDimensionalCoordinates.fromString(it[3])
                Instruction(from, to, type)
            }
}

fun executeInstruction(instruction: Instruction, partTwo: Boolean = false) {
    (instruction.from.x..instruction.to.x).forEach { x ->
        (instruction.from.y..instruction.to.y).forEach { y ->
            if (partTwo) {
                when (instruction.type) {
                    InstructionType.ON -> lights[x][y]++
                    InstructionType.OFF -> lights[x][y] = (lights[x][y] - 1).coerceAtLeast(0)
                    InstructionType.TOGGLE -> lights[x][y] += 2
                }
            } else {
                when (instruction.type) {
                    InstructionType.ON -> lights[x][y] = 1
                    InstructionType.OFF -> lights[x][y] = 0
                    InstructionType.TOGGLE -> lights[x][y] = lights[x][y].toggle()
                }
            }
        }
    }
}

enum class InstructionType {
    ON, OFF, TOGGLE;
}

data class Instruction(val from: TwoDimensionalCoordinates, val to: TwoDimensionalCoordinates, val type: InstructionType)
