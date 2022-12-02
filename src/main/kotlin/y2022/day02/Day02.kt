package y2022.day02

import readInput
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

val scores: HashMap<Pair<String, String>, Int> = HashMap()
val winningMoves: HashMap<String, String> = HashMap()
val drawMoves: HashMap<String, String> = HashMap()
val losingMoves: HashMap<String, String> = HashMap()

@OptIn(ExperimentalTime::class)
fun main() {
    val inputFile = "2022/Day02"
    val testInput = readInput("${inputFile}_test")
    fillScores()
    check(part1(testInput) == 15)
    println("Part 1 check successful!")
    check(part2(testInput) == 12)
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

fun fillScores() {
    scores[Pair("A", "X")] = 4
    scores[Pair("A", "Y")] = 8
    scores[Pair("A", "Z")] = 3
    scores[Pair("B", "X")] = 1
    scores[Pair("B", "Y")] = 5
    scores[Pair("B", "Z")] = 9
    scores[Pair("C", "X")] = 7
    scores[Pair("C", "Y")] = 2
    scores[Pair("C", "Z")] = 6

    winningMoves["A"] = "Y"
    winningMoves["B"] = "Z"
    winningMoves["C"] = "X"

    drawMoves["A"] = "X"
    drawMoves["B"] = "Y"
    drawMoves["C"] = "Z"

    losingMoves["A"] = "Z"
    losingMoves["B"] = "X"
    losingMoves["C"] = "Y"
}

fun part1(input: List<String>): Int {
    var score = 0

    input.forEach { round ->
        val (opponent, me) = round.split(" ")
        score += scores[Pair(opponent, me)]!!
    }

    return score
}

fun part2(input: List<String>): Int {
    var score = 0

    input.forEach { round ->
        val (opponent, outcome) = round.split(" ")

        val move = when (outcome) {
            "X" -> losingMoves[opponent]
            "Y" -> drawMoves[opponent]
            "Z" -> winningMoves[opponent]
            else -> error("Unknown outcome")
        }

        score += scores[Pair(opponent, move)]!!
    }

    return score
}