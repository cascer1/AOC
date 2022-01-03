@file:OptIn(ExperimentalTime::class)

package y2021.day20

import getAt
import readInput
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

private var map: Array<Array<Boolean>> = arrayOf()
private var algorithm: Array<Boolean> = arrayOf()

private fun Array<Array<Boolean>>.getSurroundingIndex(x: Int, y: Int, unknown: Boolean): Int {
    val replacement = if (algorithm[0]) unknown else false
    var result = 0

    // This looks stupid, but it's 4x faster than making a list and iterating over it
    if (this.getAt(x - 1, y - 1, replacement)) {
        result += 256
    }
    if (this.getAt(x, y - 1, replacement)) {
        result += 128
    }
    if (this.getAt(x + 1, y - 1, replacement)) {
        result += 64
    }
    if (this.getAt(x - 1, y, replacement)) {
        result += 32
    }
    if (this.getAt(x, y, replacement)) {
        result += 16
    }
    if (this.getAt(x + 1, y, replacement)) {
        result += 8
    }
    if (this.getAt(x - 1, y + 1, replacement)) {
        result += 4
    }
    if (this.getAt(x, y + 1, replacement)) {
        result += 2
    }
    if (this.getAt(x + 1, y + 1, replacement)) {
        result += 1
    }

    return result
}

private fun main() {
    val testInput = readInput("2021/Day20_test")
    parseInput(testInput)
    check(part1() == 35)
    check(part2() == 3351)

    val input = readInput("2021/Day20")
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

private fun part1(): Int {
    repeat(2) {
        applyAlgorithm(it % 2 != 0)
    }

    val result = map.sumOf { row ->
        row.count { it }
    }

    return result
}

private fun part2(): Int {
    repeat(48) {
        applyAlgorithm(it % 2 != 0)
    }

    val result = map.sumOf { row ->
        row.count { it }
    }

    return result
}

private fun parseInput(input: List<String>) {
    algorithm = input[0].map { it == '#' }.toTypedArray()

    map = input.drop(2).map { row ->
        row.map { it == '#' }.toList().toTypedArray()
    }.toTypedArray()
}

private fun applyAlgorithm(unknown: Boolean) {
    val maxY = map.size
    val maxX = map[0].size

    map = (-2..maxY + 1).map { y ->
        (-2..maxX + 1).map { x ->
            val surrounding = map.getSurroundingIndex(x, y, unknown)
            algorithm[surrounding]
        }.toTypedArray()
    }.toTypedArray()
}
