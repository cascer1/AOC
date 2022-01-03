@file:OptIn(ExperimentalTime::class)

package y2015.day02

import readInput
import kotlin.math.min
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

fun main() {
    val inputFile = "2015/Day02"

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
    return parseInput(input).sumOf {
        val side1 = it.first * it.second
        val side2 = it.first * it.third
        val side3 = it.second * it.third

        2 * side1 + 2 * side2 + 2 * side3 + min(min(side1, side2), side3)
    }
}

fun part2(input: List<String>): Int {
    return parseInput(input).sumOf {
        val sorted = it.toList().sorted()
        sorted[0] + sorted[0] + sorted[1] + sorted[1] + sorted[0] * sorted[1] * sorted[2]
    }
}

fun parseInput(input: List<String>): List<Triple<Int, Int, Int>> {
    return input.map { present ->
        val (length, width, height) = present.split('x').map { it.toInt() }
        Triple(length, width, height)
    }
}