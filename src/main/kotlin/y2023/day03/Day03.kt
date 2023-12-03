package y2023.day03

import readInput
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
fun main() {
    val inputFile = "2023/Day03"
    val testInput = readInput("${inputFile}_test")
    require(part1(testInput) == 4361) { "Part 1 check failed" }


    val input = readInput(inputFile)
    val part1Duration: Duration = measureTime {
        println(part1(input))
    }
    println("Part 1 time: ${part1Duration.toDouble(DurationUnit.MILLISECONDS)} ms")

    require(part2(testInput) == 467835) { "Part 2 check failed" }
    val part2Duration: Duration = measureTime {
        println(part2(input))
    }

    println("Part 2 time: ${part2Duration.toDouble(DurationUnit.MILLISECONDS)} ms")
}

fun part1(input: List<String>): Int {
    val (groups, symbols) = parseInput(input)

    return symbols
        .flatMap { symbol -> getSymbolAdjacentNumbers(symbol, groups) }
        .sum()
}

fun part2(input: List<String>): Int {
    val (groups, symbols) = parseInput(input)

    return symbols
        .filter { it.value == '*' }
        .map { symbol -> getSymbolAdjacentNumbers(symbol, groups) }
        .filter { it.size > 1 }
        .sumOf { it.reduce { a, b -> a * b } }
}

fun parseInput(input: List<String>): Pair<Set<NumberGroup>, Map<Pair<Int, Int>, Char>> {
    val numberGroups = mutableSetOf<NumberGroup>()
    val symbolPositions = mutableMapOf<Pair<Int, Int>, Char>()

    input.mapIndexed { y, row ->
        var inGroup = false
        var numberString = ""
        var minX = 0
        var maxX = 0
        row.mapIndexed { x, character ->
            if (character.isDigit()) {
                if (inGroup) {
                    maxX = x
                    numberString += character
                } else {
                    inGroup = true
                    numberString = character.toString()
                    minX = x
                    maxX = x
                }
            } else {
                if (inGroup) {
                    numberGroups.add(NumberGroup(y, minX..maxX, numberString.toInt()))
                    numberString = ""
                    inGroup = false
                }

                if (character != '.') {
                    symbolPositions[Pair(x, y)] = character
                }
            }
        }
        if (inGroup) {
            numberGroups.add(NumberGroup(y, minX..maxX, numberString.toInt()))
            numberString = ""
            inGroup = false
        }
    }

    return Pair(numberGroups, symbolPositions)
}

fun getSymbolAdjacentNumbers(symbol: Map.Entry<Pair<Int, Int>, Char>, numberGroups: Set<NumberGroup>): List<Int> {
    val x = symbol.key.first
    val y = symbol.key.second

    return numberGroups.filter { it.isAdjacentTo(x, y) }.map { it.number }.toList()
}

class NumberGroup(val y: Int, val x: IntRange, val number: Int) {
    fun isAdjacentTo(x: Int, y: Int): Boolean {
        val yRange = (this.y - 1)..(this.y + 1)
        val xRange = (this.x.min() - 1)..(this.x.max() + 1)

        return (yRange.contains(y) && xRange.contains(x))
    }
}