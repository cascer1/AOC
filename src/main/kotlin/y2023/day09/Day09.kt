package y2023.day09

import readInput
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
fun main() {
    val inputFile = "2023/Day09"
    val testInput = readInput("${inputFile}_test")
    require(part1(testInput) == 114) { "Part 1 check failed" }

    val input = readInput(inputFile)
    val part1Duration: Duration = measureTime {
        println(part1(input))
    }
    println("Part 1 time: ${part1Duration.toDouble(DurationUnit.MILLISECONDS)} ms")

    require(part2(testInput) == 2) { "Part 2 check failed" }
    val part2Duration: Duration = measureTime {
        println(part2(input))
    }

    println("Part 2 time: ${part2Duration.toDouble(DurationUnit.MILLISECONDS)} ms")
}

private fun part1(input: List<String>): Int {
    return input.map { line -> History(line.split(" ").map { it.toInt() }) }
            .sumOf { it.getNext() }
}

private fun part2(input: List<String>): Int {
    return input.map { line -> History(line.split(" ").map { it.toInt() }) }
            .sumOf { it.getPrevious() }
}

class History(val numbers: List<Int>) {
    var differences: History? = null

    init {
        if (!isAllZeroes()) {
            differences = calculateDifferences()
        }
    }

    fun calculateDifferences(): History {
        return History(numbers.windowed(2, 1).map { it[1] - it[0] })
    }

    fun isAllZeroes(): Boolean {
        return numbers.all { it == 0 }
    }

    fun getNext(): Int {
        if (isAllZeroes()) {
            return 0
        }
        return differences!!.getNext() + numbers.last()
    }

    fun getPrevious(): Int {
        if (isAllZeroes()) {
            return 0
        }

        return numbers.first() - differences!!.getPrevious()
    }

    override fun toString(): String {
        return numbers.joinToString(" ")
    }
}
