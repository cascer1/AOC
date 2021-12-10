package day10

import readInput
import java.util.*

val validPairs = listOf(Pair('[', ']'), Pair('(', ')'), Pair('{', '}'), Pair('<', '>'))
val illegalClosingScores = listOf(Pair(')', 3), Pair(']', 57), Pair('}', 1197), Pair('>', 25137))
val closingCompletionScores = listOf(Pair(')', 1), Pair(']', 2), Pair('}', 3), Pair('>', 4))

fun List<Pair<Char, Char>>.isOpening(character: Char): Boolean {
    return this.any { pair -> pair.first == character }
}

fun List<Pair<Char, Char>>.getClosing(character: Char): Char {
    return this.first { it.first == character }.second
}

fun List<Pair<Char, Int>>.score(character: Char): Int {
    return this.first { it.first == character }.second
}

fun main() {
    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day10_test")
    check(part1(testInput) == 26397)
    check(part2(testInput) == 288957L)

    val input = readInput("Day10")
    println(part1(input))
    println(part2(input))
}

fun findErrorScore(input: String): Int {
    val expectedClosingCharacters = Stack<Char>()

    input.forEach { character ->
        if (validPairs.isOpening(character)) {
            expectedClosingCharacters.push(validPairs.getClosing(character))
        } else {
            val expectedClosingCharacter = expectedClosingCharacters.pop()

            if (expectedClosingCharacter != character) {
                return illegalClosingScores.score(character)
            }
        }
    }

    return 0
}

fun completeLine(input: String): Long {
    val expectedClosingCharacters = Stack<Char>()
    var score = 0L

    input.forEach { character ->
        if (validPairs.isOpening(character)) {
            expectedClosingCharacters.push(validPairs.getClosing(character))
        } else {
            if (expectedClosingCharacters.peek() == character) {
                expectedClosingCharacters.pop()
            }
        }
    }

    expectedClosingCharacters.reversed().forEach { character ->
        score = (score * 5) + closingCompletionScores.score(character)
    }

    return score
}


fun part1(input: List<String>): Int {
    return input.sumOf { findErrorScore(it) }
}

fun part2(input: List<String>): Long {
    val scores = input
        .filter { findErrorScore(it) == 0 }
        .map { completeLine(it) }
        .sorted()

    return scores[scores.size / 2]
}