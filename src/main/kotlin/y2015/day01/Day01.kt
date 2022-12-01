@file:OptIn(ExperimentalTime::class)

package y2015.day01

import readInput
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

fun main() {
    val input = readInput("2015/Day01.txt")
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
    return parseInput(input).sum()
}

fun part2(input: List<String>): Int {
    val parsed = parseInput(input)
    var currentPosition = 0

    parsed.forEachIndexed { index, instruction ->
        currentPosition += instruction

        if (currentPosition < 0) {
            return index + 1
        }
    }

    return 0
}

fun parseInput(input: List<String>): List<Int> {
    return input.first().map { if (it == ')') -1 else 1 }
}