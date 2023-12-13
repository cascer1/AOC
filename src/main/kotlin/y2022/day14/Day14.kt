package y2022.day14

import readInput
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

val startPoint = Pair(500, 0)
var floorHeight: Int? = null

@OptIn(ExperimentalTime::class)
fun main() {
    val inputFile = "2022/Day14"
    val testInput = readInput("${inputFile}_test")
    check(part1(testInput) == 24)
    println("Part 1 check successful!")
    check(part2(testInput) == 93)
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

private fun part1(input: List<String>): Int {
    val grid = parseInput(input)
    floorHeight = null

    var newGrid = grid
    var oldGrid = HashSet<Pair<Int, Int>>()
    var sandCount = -1

    while (newGrid != oldGrid) {
        oldGrid = newGrid
        newGrid = simulate(oldGrid, startPoint)
        sandCount++
    }

    return sandCount
}

private fun part2(input: List<String>): Int {
    val grid = parseInput(input)

    val range = grid.ranges()
    val lowestY = range.second.second
    floorHeight = lowestY + 2

    var newGrid = grid
    var oldGrid = HashSet<Pair<Int, Int>>()
    var sandCount = -1

    while (newGrid != oldGrid) {
        oldGrid = newGrid
        newGrid = simulate(oldGrid, startPoint)
        sandCount++
    }

    return sandCount
}

fun simulate(grid: HashSet<Pair<Int, Int>>, location: Pair<Int, Int>): HashSet<Pair<Int, Int>> {
    var oldLocation = Pair(0, 0)
    var newLocation = location.copy()
    val returned = HashSet(grid)

    while (newLocation != oldLocation) {
        oldLocation = newLocation.copy()
        newLocation = moveDown(grid, oldLocation)

        if (floorHeight == null && isBeyondRanges(grid.ranges().first, newLocation.first)) {
            return returned
        }
    }

    returned.add(newLocation)
    return returned
}

fun moveDown(grid: HashSet<Pair<Int, Int>>, point: Pair<Int, Int>): Pair<Int, Int> {
    if (floorHeight != null) {
        if (point.second == floorHeight!! - 1) {
            return point
        }
    }

    if (!grid.contains(Pair(point.first, point.second + 1))) {
        return Pair(point.first, point.second + 1)
    }

    if (!grid.contains(Pair(point.first - 1, point.second + 1))) {
        return Pair(point.first - 1, point.second + 1)
    }

    if (!grid.contains(Pair(point.first + 1, point.second + 1))) {
        return Pair(point.first + 1, point.second + 1)
    }

    return point
}


fun parseInput(input: List<String>): HashSet<Pair<Int, Int>> {
    val returned = HashSet<Pair<Int, Int>>()

    input.forEach { line ->
        line.split(" -> ")
            .windowed(2, 1)
            .forEach { window ->
                val first = window[0].split(",").map { it.toInt() }
                val second = window[1].split(",").map { it.toInt() }

                val xStart = minOf(first[0], second[0])
                val xEnd = maxOf(first[0], second[0])
                val yStart = minOf(first[1], second[1])
                val yEnd = maxOf(first[1], second[1])

                (xStart..xEnd).forEach { x ->
                    (yStart..yEnd).forEach { y ->
                        returned.add(Pair(x, y))
                    }
                }
            }
    }

    return returned
}

fun isBeyondRanges(range: Pair<Int, Int>, x: Int): Boolean {
    return (x < range.first) || (x > range.second)
}

fun draw(grid: HashSet<Pair<Int, Int>>) {
    val ranges = grid.ranges()

    val minY = ranges.second.first
    val maxY = ranges.second.second
    val minX = ranges.first.first
    val maxX = ranges.first.second

    (minY..maxY).forEach { y ->
        (minX..maxX).forEach { x ->
            if (grid.contains(Pair(x, y))) print("#")
            else print(".")
        }
        println()
    }
}

fun HashSet<Pair<Int, Int>>.ranges(): Pair<Pair<Int, Int>, Pair<Int, Int>> {
    var minX = Int.MAX_VALUE
    var maxX = Int.MIN_VALUE
    var minY = Int.MAX_VALUE
    var maxY = Int.MIN_VALUE

    this.forEach { key ->
        minX = minX.coerceAtMost(key.first)
        maxX = maxX.coerceAtLeast(key.first)
        minY = minY.coerceAtMost(key.second)
        maxY = maxY.coerceAtLeast(key.second)
    }

    return Pair(Pair(minX, maxX), Pair(minY, maxY))
}