@file:OptIn(ExperimentalTime::class)

package y2021

import readInput
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

fun main() {
    val testInput = readInput("Day20_test")
    parseInput(testInput)
    check(part1() == 1)
    check(part2() == 2)

    val input = readInput("Day20")
    parseInput(input)
    val part1Duration: Duration = measureTime {
        println(part1())
    }
    val part2Duration: Duration = measureTime {
        println(part2())
    }

    println("Part 1 time: ${part1Duration.toDouble(DurationUnit.MILLISECONDS)} ms")
    println("Part 2 time: ${part2Duration.toDouble(DurationUnit.MILLISECONDS)} ms")
}

fun part1(): Int {
    return 1
}

fun part2(): Int {
    return 2
}

fun parseInput(input: List<String>) {

}