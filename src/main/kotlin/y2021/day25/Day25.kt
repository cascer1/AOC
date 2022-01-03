@file:OptIn(ExperimentalTime::class)

package y2021.day25

import readInput
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

var cucumbers: HashMap<Pair<Int, Int>, Direction> = hashMapOf()
var xMax = 0
var yMax = 0

fun main() {
    val testInput = readInput("2021/Day25_test")
    check(part1(testInput) == 58)
    println("Part 1 check successful!")

    val input = readInput("2021/Day25")
    val part1Duration: Duration = measureTime {
        println(part1(input))
    }

    println("Part 1 time: ${part1Duration.toDouble(DurationUnit.MILLISECONDS)} ms")
}

fun part1(input: List<String>): Int {
    parseInput(input)

    var oldPositions: Map<Pair<Int, Int>, Direction>
    var steps = 0
    do {
        steps++
        oldPositions = cucumbers.toMap()
        moveCucumbers()
    } while (oldPositions != cucumbers)

    return steps
}

fun moveCucumbers() {
    val newEastPositions = cucumbers.filter { it.value == Direction.EAST }.map { determineNewCoordinates(it.key, it.value) }

    cucumbers.entries.removeIf { it.value == Direction.EAST }
    newEastPositions.forEach { coordinates ->
        cucumbers[coordinates] = Direction.EAST
    }

    val newSouthPositions = cucumbers.filter { it.value == Direction.SOUTH }.map { determineNewCoordinates(it.key, it.value) }

    cucumbers.entries.removeIf { it.value == Direction.SOUTH }
    newSouthPositions.forEach { coordinates ->
        cucumbers[coordinates] = Direction.SOUTH
    }
}

fun determineNewCoordinates(coordinates: Pair<Int, Int>, direction: Direction): Pair<Int, Int> {
    val spaceToCheck = if (direction == Direction.SOUTH) {
        if (coordinates.second == yMax) {
            Pair(coordinates.first, 0)
        } else {
            Pair(coordinates.first, coordinates.second + 1)
        }
    } else {
        if (coordinates.first == xMax) {
            Pair(0, coordinates.second)
        } else {
            Pair(coordinates.first + 1, coordinates.second)
        }
    }

    if (cucumbers.containsKey(spaceToCheck)) {
        return coordinates
    }

    return spaceToCheck
}

fun parseInput(input: List<String>) {
    cucumbers.clear()
    xMax = 0
    yMax = 0
    input.forEachIndexed { y, row ->
        yMax = yMax.coerceAtLeast(y)
        row.forEachIndexed row@{ x, cucumber ->
            xMax = xMax.coerceAtLeast(x)
            if (cucumber == '.') {
                return@row
            } else if (cucumber == 'v') {
                cucumbers[Pair(x, y)] = Direction.SOUTH
            } else if (cucumber == '>') {
                cucumbers[Pair(x, y)] = Direction.EAST
            }
        }
    }
}

enum class Direction {
    SOUTH, EAST
}