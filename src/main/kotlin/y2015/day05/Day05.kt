package y2015.day05

import readInput
import java.util.regex.Pattern
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
fun main() {
    val inputFile = "2015/Day05"
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
    val vowels = setOf('a', 'e', 'i', 'o', 'u')
    val forbiddenPairs = setOf("ab", "cd", "pq", "xy")
    val doubleLetters = Pattern.compile(".*(.)\\1.*").asMatchPredicate()
    return input.asSequence()
            .filterNot { forbiddenPairs.any { forbiddenPair -> it.contains(forbiddenPair) }}
            .filter { it.count { char -> vowels.contains(char) } >= 3 }
            .filter { doubleLetters.test(it) }
            .count()
}

private fun part2(input: List<String>): Int {
    val pattern1 = Pattern.compile(".*(..).*\\1.*").asMatchPredicate()
    val pattern2 = Pattern.compile(".*(.).\\1.*").asMatchPredicate()
    return input.asSequence()
            .filter { pattern1.test(it) }
            .filter { pattern2.test(it) }
            .count()
}