package y2023.day04

import readInput
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
fun main() {
    val inputFile = "2023/Day04"
    val testInput = readInput("${inputFile}_test")
    require(part1(testInput) == 13) { "Part 1 check failed" }

    val input = readInput(inputFile)
    val part1Duration: Duration = measureTime {
        println(part1(input))
    }
    println("Part 1 time: ${part1Duration.toDouble(DurationUnit.MILLISECONDS)} ms")

    require(part2(testInput) == 30) { "Part 2 check failed" }
    val part2Duration: Duration = measureTime {
        println(part2(input))
    }

    println("Part 2 time: ${part2Duration.toDouble(DurationUnit.MILLISECONDS)} ms")
}

private fun part1(input: List<String>): Int {
    return input.map { Card(it) }.sumOf { it.score }
}

private fun part2(input: List<String>): Int {
    val countMap = HashMap<Int, Int>()
    input.map { Card(it) }.forEach { card ->
        incrementCount(countMap, card.id!!, 1)
        for (newId in card.id!! + 1..card.id!! + card.matches) {
            incrementCount(countMap, newId, countMap[card.id!!]!!)
        }
    }

    return countMap.values.sum()
}

fun incrementCount(countMap: HashMap<Int, Int>, cardId: Int, count: Int) {
    if (countMap.containsKey(cardId)) {
        countMap[cardId] = countMap[cardId]!! + count
    } else {
        countMap[cardId] = count
    }
}

class Card(input: String) {
    var score = 1
    var matches = 0
    var id: Int? = null
    private var winningNumbers: List<Int> = emptyList()
    private var ownNumbers: List<Int> = emptyList()

    init {
        id = input.substring(5, input.indexOf(":")).trim().toInt()
        val numbers = input.substring(input.indexOf(": ") + 1).split("|")
        winningNumbers = numbers[0].split(" ").filter { it.isNotBlank() }.map { it.toInt() }
        ownNumbers = numbers[1].split(" ").filter { it.isNotBlank() }.map { it.toInt() }
        winningNumbers.forEach { winningNumber ->
            if (ownNumbers.contains(winningNumber)) {
                score *= 2
                matches++
            }
        }
        score /= 2
    }
}