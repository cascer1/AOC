package y2021

import readInput

private val validPairs = listOf(Pair('[', ']'), Pair('(', ')'), Pair('{', '}'), Pair('<', '>'))
private val illegalClosingScores = listOf(Pair(')', 3), Pair(']', 57), Pair('}', 1197), Pair('>', 25137))
private val closingCompletionScores = listOf(Pair(')', 1), Pair(']', 2), Pair('}', 3), Pair('>', 4))

private fun List<Pair<Char, Char>>.isOpening(character: Char): Boolean {
    return this.any { pair -> pair.first == character }
}

private fun List<Pair<Char, Char>>.getClosing(character: Char): Char {
    return this.first { it.first == character }.second
}

private fun List<Pair<Char, Int>>.score(character: Char): Int {
    return this.first { it.first == character }.second
}

private fun main() {
    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day10_test")
    check(part1(testInput) == 26397)
    check(part2(testInput) == 288957L)

    val input = readInput("Day10")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    return input.sumOf { findErrorScore(it) }
}

private fun part2(input: List<String>): Long {
    val scores = input
            .filter { findErrorScore(it) == 0 }
            .map { completeLine(it) }
            .sorted()

    return scores[scores.size / 2]
}

private fun findErrorScore(input: String): Int {
    val expectedClosingCharacters = ArrayDeque<Char>()

    input.forEach { character ->
        if (validPairs.isOpening(character)) {
            expectedClosingCharacters.addFirst(validPairs.getClosing(character))
        } else {
            val expectedClosingCharacter = expectedClosingCharacters.removeFirst()

            if (expectedClosingCharacter != character) {
                return illegalClosingScores.score(character)
            }
        }
    }

    return 0
}

private fun completeLine(input: String): Long {
    val expectedClosingCharacters = ArrayDeque<Char>()
    var score = 0L

    input.forEach { character ->
        if (validPairs.isOpening(character)) {
            expectedClosingCharacters.addFirst(validPairs.getClosing(character))
        } else {
            if (expectedClosingCharacters.first() == character) {
                expectedClosingCharacters.removeFirst()
            }
        }
    }

    expectedClosingCharacters.forEach { character ->
        score = (score * 5) + closingCompletionScores.score(character)
    }

    return score
}

