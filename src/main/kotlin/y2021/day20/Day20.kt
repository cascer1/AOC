@file:OptIn(ExperimentalTime::class)

package y2021.day20

import readInput
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

var map: Array<Array<Boolean>> = arrayOf()
var algorithm: Array<Boolean> = arrayOf()

fun Array<Array<Boolean>>.getAt(x: Int, y: Int, unknown: Boolean): Boolean {
    return this.getOrNull(y)?.getOrNull(x) ?: unknown
}

fun Array<Array<Boolean>>.getSurrounding(x: Int, y: Int, unknown: Boolean): Int {
    val replacement = if (algorithm[0]) unknown else false
    return arrayOf(
            this.getAt(x - 1, y - 1, replacement),
            this.getAt(x, y - 1, replacement),
            this.getAt(x + 1, y - 1, replacement),
            this.getAt(x - 1, y, replacement),
            this.getAt(x, y, replacement),
            this.getAt(x + 1, y, replacement),
            this.getAt(x - 1, y + 1, replacement),
            this.getAt(x, y + 1, replacement),
            this.getAt(x + 1, y + 1, replacement)
    ).map { if (it) "1" else "0" }.reduce { acc, s -> acc + s }.toInt(2)
}

fun main() {
    val testInput = readInput("Day20_test")
    parseInput(testInput)
    check(part1() == 35)
    check(part2() == 3351)

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

fun parseInput(input: List<String>) {
    algorithm = input[0].map { it == '#' }.toTypedArray()

    map = input.drop(2).map { row ->
        row.map { it == '#' }.toList().toTypedArray()
    }.toTypedArray()
}

fun applyAlgorithm(unknown: Boolean) {
    val maxY = map.size
    val maxX = map[0].size

    map = (-2..maxY + 1).map { y ->
        (-2..maxX + 1).map { x ->
            val surrounding = map.getSurrounding(x, y, unknown)
            algorithm[surrounding]
        }.toTypedArray()
    }.toTypedArray()
}

fun part1(): Int {
    repeat(2) {
        applyAlgorithm(it % 2 != 0)
    }

    val result = map.sumOf { row ->
        row.count { it }
    }

    return result
}

fun part2(): Int {
    repeat(48) {
        applyAlgorithm(it % 2 != 0)
    }

    val result = map.sumOf { row ->
        row.count { it }
    }

    return result
}