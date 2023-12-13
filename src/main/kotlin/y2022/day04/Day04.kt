package y2022.day04

import containsAll
import intersects
import readInput
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
fun main() {
    val inputFile = "2022/Day04"
    val testInput = readInput("${inputFile}_test")
    check(part1(testInput) == 2)
    println("Part 1 check successful!")
    check(part2(testInput) == 4)
    println("Part 2 check successful!")

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
    val pairs = parseInput(input)

    return pairs.count { it[0].containsAll(it[1]) || it[1].containsAll(it[0]) }
}

private fun part2(input: List<String>): Int {
    return parseInput(input).count { it[0].intersects(it[1])}
}

fun parseInput(input: List<String>): List<List<IntRange>> {
    return input.map { it.split(",").map { part -> toIntRange(part) }.toList() }.toList()
}

fun toIntRange(input: String): IntRange {
    val (start, stop) = input.split("-").map { it.toInt() }
    return IntRange(start, stop)
}