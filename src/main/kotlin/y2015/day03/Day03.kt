package y2015.day03

import TwoDimensionalCoordinates
import readInput
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

var visited: HashSet<TwoDimensionalCoordinates> = hashSetOf()
var currentPosition: TwoDimensionalCoordinates = TwoDimensionalCoordinates(0, 0)
var robotPosition: TwoDimensionalCoordinates = TwoDimensionalCoordinates(0, 0)

@OptIn(ExperimentalTime::class)
fun main() {
    val inputFile = "2015/Day03"
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
    visited.add(currentPosition)
    parseInput(input).forEach { move(it, 0) }
    return visited.size
}

private fun part2(input: List<String>): Int {
    visited.clear()
    currentPosition = TwoDimensionalCoordinates(0,0)
    visited.add(currentPosition)
    parseInput(input).forEachIndexed { index, instruction -> move(instruction, index) }
    return visited.size
}

fun move(instruction: Char, index: Int) {
    val step = parseInstruction(instruction)
    if (index % 2 == 0) {
        currentPosition = TwoDimensionalCoordinates(currentPosition.x + step.first, currentPosition.y + step.second)
        visited.add(currentPosition)
    } else {
        robotPosition = TwoDimensionalCoordinates(robotPosition.x + step.first, robotPosition.y + step.second)
        visited.add(robotPosition)
    }
}

fun parseInstruction(instruction: Char): Pair<Int, Int> {
    return when (instruction) {
        'v' -> Pair(0, -1)
        '<' -> Pair(-1, 0)
        '>' -> Pair(1, 0)
        '^' -> Pair(0, 1)
        else -> throw IllegalStateException("Illegal input")
    }
}

fun parseInput(input: List<String>): List<Char> {
    return input.first().toList()
}