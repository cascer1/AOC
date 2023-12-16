package y2023.day13

import readInput
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
fun main() {
    val inputFile = "2023/Day13"
    val testInput = readInput("${inputFile}_test")
    require(part1(testInput) == 405) { "Part 1 check failed" }

    val input = readInput(inputFile)
    val part1Duration: Duration = measureTime {
        println(part1(input))
    }
    println("Part 1 time: ${part1Duration.toDouble(DurationUnit.MILLISECONDS)} ms")

    require(part2(testInput) == 400) { "Part 2 check failed" }
    val part2Duration: Duration = measureTime {
        println(part2(input))
    }

    println("Part 2 time: ${part2Duration.toDouble(DurationUnit.MILLISECONDS)} ms")
}

private fun part1(input: List<String>): Int {
    val patterns = parseInput(input)
    return patterns.sumOf { findPatternReflectionScore(it) }
}

private fun part2(input: List<String>): Int {
    val patterns = parseInput(input)
    return patterns.sumOf { findPatternReflectionScore(it, true) }
}

private fun parseInput(input: List<String>): List<List<String>> {
    return input.joinToString("\n").split("\n\n").map { it.split("\n") }
}

private fun findPatternReflectionScore(pattern: List<String>, smudge: Boolean = false): Int {
    val verticalScore = findVerticalReflectionScore(pattern, smudge)
    val horizontalScore = findHorizontalReflectionScore(pattern, smudge)

    if (verticalScore > horizontalScore) {
        return verticalScore
    }

    return horizontalScore * 100
}

private fun findVerticalReflectionScore(pattern: List<String>, smudge: Boolean = false): Int {
    val length = pattern.first().length
    val rotated = (0 until length).map { columnIndex ->
        pattern.map { it[columnIndex] }.joinToString("")
    }

    return findReflectionLine(rotated, smudge)
}

private fun findHorizontalReflectionScore(pattern: List<String>, smudge: Boolean = false): Int {
    return findReflectionLine(pattern, smudge)

}

private fun findReflectionLine(pattern: List<String>, smudge: Boolean = false): Int {
    val size = pattern.size

    return (pattern.indices).maxBy { i ->
        val bottom = pattern.subList(i, size)
        val top = pattern.subList(0, i).reversed()
        val pairing = top.zip(bottom)

        val errors = pairing.sumOf { it.first.errors(it.second) }

        return@maxBy if (smudge && errors == 1 || !smudge && errors == 0) {
             i
        } else {
            0
        }
    }
}

private fun String.errors(other: String): Int {
    return this.zip(other).count { it.first != it.second }
}