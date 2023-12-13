package y2023.day11

import Coordinate
import readInput
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
fun main() {
    val inputFile = "2023/Day11"
    val testInput = readInput("${inputFile}_test")
    require(calculateShortestDistance(testInput, 2) == 374L) { "Part 1 check failed" }

    val input = readInput(inputFile)
    val part1Duration: Duration = measureTime {
        println(calculateShortestDistance(input, 2))
    }
    println("Part 1 time: ${part1Duration.toDouble(DurationUnit.MILLISECONDS)} ms")

    require(calculateShortestDistance(testInput, 10) == 1030L) { "Part 2 check failed" }
    require(calculateShortestDistance(testInput, 100) == 8410L) { "Part 2 check failed" }
    val part2Duration: Duration = measureTime {
        println(calculateShortestDistance(input, 1_000_000))
    }

    println("Part 2 time: ${part2Duration.toDouble(DurationUnit.MILLISECONDS)} ms")
}

private fun calculateShortestDistance(input: List<String>, expansion: Int): Long {
    val galaxies = parseInput(input)

    val maxX = galaxies.maxOf { it.x }
    val maxY = galaxies.maxOf { it.y }

    val emptyColumns = (0 .. maxX).filter{x -> galaxies.none { it.x == x}}.toSet()
    val emptyRows = (0 .. maxY).filter{y -> galaxies.none { it.y == y}}.toSet()

    val result = galaxies.flatMapIndexed { index, galaxy ->
        galaxies.subList(index + 1, galaxies.size).map { Pair(galaxy, it) }
    }.sumOf { (from, to) ->
        calculateDistance(from, to, emptyRows, emptyColumns, expansion)
    }

    return result
}

private fun calculateDistance(from: Coordinate, to: Coordinate, emptyRows: Set<Int>, emptyColumns: Set<Int>, expansion: Int): Long {
    val xSteps = minOf(from.x, to.x)..maxOf(from.x, to.x)
    val ySteps = minOf(from.y, to.y)..maxOf(from.y, to.y)

    val emptyXSteps = xSteps.filter { emptyColumns.contains(it) }.size
    val emptyYSteps = ySteps.filter { emptyRows.contains(it) }.size

    val unexpandedDistance = from.manhattanDistance(to).toLong() - emptyXSteps - emptyYSteps
    val expandedHorizontalDistance = emptyXSteps * expansion
    val expandedVerticalDistance = emptyYSteps * expansion

    return unexpandedDistance + expandedHorizontalDistance + expandedVerticalDistance
}

private fun parseInput(input: List<String>): List<Coordinate> {
    return input.flatMapIndexed { y, line ->
        line.mapIndexedNotNull { x, char ->
            if (char == '#') {
                Coordinate(x, y)
            } else {
                null
            }
        }
    }
}