package y2022.day06

import readInput
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
fun main() {
    val inputFile = "2022/Day06"
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
    return findMarker(input.first(), 4)
}

fun part2(input: List<String>): Int {
    return findMarker(input.first(), 14)
}

fun findMarker(input: String, length: Int): Int {
    input.windowed(length, 1).forEachIndexed { index, string ->
        if (string.toSet().size == length) {
            return index + length
        }
    }

    return 0
}