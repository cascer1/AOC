package y2023.day01

import readInput
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

private val digits = arrayOf(
        Digit("one", "1"),
        Digit("two", "2"),
        Digit("three", "3"),
        Digit("four", "4"),
        Digit("five", "5"),
        Digit("six", "6"),
        Digit("seven", "7"),
        Digit("eight", "8"),
        Digit("nine", "9"),
        Digit("1", "1"),
        Digit("2", "2"),
        Digit("3", "3"),
        Digit("4", "4"),
        Digit("5", "5"),
        Digit("6", "6"),
        Digit("7", "7"),
        Digit("8", "8"),
        Digit("9", "9"))

@OptIn(ExperimentalTime::class)
fun main() {
    val inputFile = "2023/Day01"
    val testInputOne = readInput("${inputFile}_test")
    require(part1(testInputOne) == 142) { "Part 1 check failed" }

    val input = readInput(inputFile)
    val part1Duration: Duration = measureTime {
        println(part1(input))
    }
    println("Part 1 time: ${part1Duration.toDouble(DurationUnit.MILLISECONDS)} ms")

    val testInputTwo = readInput("${inputFile}_test2")
    require(part2(testInputTwo) == 281) { "Part 2 check failed" }

    val part2Duration: Duration = measureTime {
        println(part2(input))
    }
    println("Part 2 time: ${part2Duration.toDouble(DurationUnit.MILLISECONDS)} ms")

}

private fun part1(input: List<String>): Int {
    return input.sumOf { takeFirstAndLastDigits(it) }
}

fun takeFirstAndLastDigits(input: String): Int {
    val digits = input.filter { it.isDigit() }
    return "${digits.first()}${digits.last()}".toInt()
}

private fun part2(input: List<String>): Int {
    return input
            .sumOf { replaceDigitWordsSmart(it) }
}

private fun replaceDigitWordsSmart(input: String): Int {
    val firstDigit: String
    val lastDigit: String

    val wordPositions = digits.flatMap { getDigitIndices(input, it) }
            .filter { it.position != -1 }
            .sortedBy { it.position }

    if (wordPositions.isEmpty()) {
        return 0
    }

    firstDigit = wordPositions.first().digit.value
    lastDigit = wordPositions.last().digit.value

    return (firstDigit + lastDigit).toInt()
}

private fun getDigitIndices(input: String, digit: Digit): Set<DigitPosition> {
    val indices = ArrayList<Int>()
    var index: Int = input.indexOf(digit.word)
    while (index >= 0) {
        indices.add(index)
        index = input.indexOf(digit.word, index + 1)
    }

    return indices.map { DigitPosition(digit, it) }.toSet()
}

private class Digit(val word: String, val value: String)
private class DigitPosition(val digit: Digit, val position: Int) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DigitPosition) return false

        if (position != other.position) return false

        return true
    }
}