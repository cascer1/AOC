package y2022.day01

import readInput
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
fun main() {
    val inputFile = "2022/Day01"
    val testInput = readInput("${inputFile}_test")
    check(part1(testInput) == 24000)
    println("Part 1 check successful!")
    check(part2(testInput) == 45000)
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

fun part1(input: List<String>): Int {
    return getTopX(input, 1)
}

fun part2(input: List<String>): Int {
    return getTopX(input, 3)
}

fun getTopX(input: List<String>, count: Int): Int {
    var calorieCounts = ArrayList<Int>()

    var thisElf = 0

    input.forEach {
        if (it.isNullOrEmpty()) {
            calorieCounts.add(thisElf)
            thisElf = 0
        } else {
            thisElf += Integer.parseInt(it)
        }
    }

    calorieCounts.add(thisElf)

    return calorieCounts.sortedDescending().take(count).sum()
}

fun parseInput(input: List<String>) {
    println("parsing")
}