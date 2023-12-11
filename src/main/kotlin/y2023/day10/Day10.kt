package y2023.day10

import Coordinate
import getAt
import getX
import getY
import readInput
import java.util.PriorityQueue
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
fun main() {
    val inputFile = "2023/Day10"
    val testInput = readInput("${inputFile}_test")
    require(part1(testInput) == 4) { "Part 1 check failed" }

    val input = readInput(inputFile)
    val part1Duration: Duration = measureTime {
        println(part1(input))
    }
    println("Part 1 time: ${part1Duration.toDouble(DurationUnit.MILLISECONDS)} ms")

//    require(part2(readInput("2023/Day10_test2")) == 10) { "Part 2 check failed" }
    val part2Duration: Duration = measureTime {
        println(part2(input))
    }

    println("Part 2 time: ${part2Duration.toDouble(DurationUnit.MILLISECONDS)} ms")
}

fun part1(input: List<String>): Int {
    val (pipes, startPipe) = parseInput(input)

    val distanceQueue = PriorityQueue<Pipe>(compareBy { it.distance })
    startPipe.checked = true

    addStartingPipes(pipes, startPipe, distanceQueue)

    while (distanceQueue.isNotEmpty()) {
        val checkPipe = distanceQueue.poll()
        checkPipe.checked = true

        checkPipe.getAdjacent(pipes).filterNot { it.checked }.forEach { adjacentPipe ->
            adjacentPipe.distance = checkPipe.distance + 1
            distanceQueue.add(adjacentPipe)
        }
    }

    val maxDistance = pipes.maxOf { it.value.distance }

    return maxDistance
}

fun part2(input: List<String>): Int {
    val (pipes, startPipe) = parseInput(input)

    val distanceQueue = PriorityQueue<Pipe>(compareBy { it.distance })
    startPipe.checked = true

    addStartingPipes(pipes, startPipe, distanceQueue)

    while (distanceQueue.isNotEmpty()) {
        val checkPipe = distanceQueue.poll()
        checkPipe.checked = true

        checkPipe.getAdjacent(pipes).filterNot { it.checked }.forEach { adjacentPipe ->
            distanceQueue.add(adjacentPipe)
        }
    }

    val relevantPipes = pipes.filterValues { it.checked }

    val maxX = pipes.maxOf { it.value.x }
    val maxY = pipes.maxOf { it.value.y }

    var enclosedPoints = 0

    (0..maxY).forEach { y ->
        (0..maxX).forEach { x ->
            if (isEnclosed(relevantPipes, x, y, maxX, maxY)) {
                enclosedPoints++
            }
        }
    }

    return enclosedPoints
}

private fun addStartingPipes(pipes: MutableMap<Coordinate, Pipe>, startPipe: Pipe, distanceQueue: PriorityQueue<Pipe>) {
    pipes.getAt(startPipe.x, startPipe.y - 1)?.let {
        if (it.south) {
            startPipe.north = true
            it.checked = true
            it.distance = 1
            distanceQueue.add(it)
        }
    }

    pipes.getAt(startPipe.x, startPipe.y + 1)?.let {
        if (it.north) {
            startPipe.south = true
            it.checked = true
            it.distance = 1
            distanceQueue.add(it)
        }
    }

    pipes.getAt(startPipe.x - 1, startPipe.y)?.let {
        if (it.east) {
            startPipe.west = true
            it.checked = true
            it.distance = 1
            distanceQueue.add(it)
        }
    }

    pipes.getAt(startPipe.x + 1, startPipe.y)?.let {
        if (it.west) {
            startPipe.east = true
            it.checked = true
            it.distance = 1
            distanceQueue.add(it)
        }
    }
}

private fun printMap(pipes: Map<Coordinate, Pipe>) {
    val maxX = pipes.maxOf { it.key.getX() }
    val maxY = pipes.maxOf { it.key.getY() }

    (0..maxY).forEach { y ->
        (0..maxX).forEach { x ->
            val pipe = pipes.getAt(x, y)
            if (pipe != null) {
                print(pipe.printChar())
            } else {
                if (isEnclosed(pipes, x, y, maxX, maxY)) {
                    print("▒")
                } else {
                    print(" ")
                }
            }
        }
        println()
    }
}

private fun isEnclosed(map: Map<Pair<Int, Int>, Pipe>, pointX: Int, pointY: Int, maxX: Int, maxY: Int): Boolean {
    if (!hasPipesBelow(map, pointX, pointY, maxY)) {
        return false
    }

    if (!hasPipesToRight(map, pointX, pointY, maxX)) {
        return false
    }

    if (!hasPipesAbove(map, pointX, pointY)) {
        return false
    }

    if (!hasPipesLeft(map, pointX, pointY)) {
        return false
    }

    val horizontalCrossingNumber = calculateHorizontalCrossingNumber(map, pointX, pointY)
    val verticalCrossingNumber = calculateVerticalCrossingNumber(map, pointX, pointY)
    return horizontalCrossingNumber % 2 != 0 && verticalCrossingNumber % 2 != 0
}

private fun calculateHorizontalCrossingNumber(map: Map<Pair<Int, Int>, Pipe>, pointX: Int, pointY: Int): Int {
    var crossingNumber = 0

    if (map.getAt(pointX, pointY) != null) {
        return 0
    }

    var segmentStart: Char? = null
    var inSegment = false

    (0..pointX).forEach { x ->
        val found = map.getAt(x, pointY)

        if (found != null) {
            //  F - 7
            //  |   |
            //  L - J

            if (found.shape == '|') {
                crossingNumber++
            } else {
                if (inSegment) {
                    if (found.shape != '-') {
                        inSegment = false
                    }
                    if (segmentStart == 'F' && found.shape == 'J') {
                        crossingNumber++
                    } else if (segmentStart == 'L' && found.shape == '7') {
                        crossingNumber++
                    } else if (found.shape == 'S') {
                        crossingNumber++
                    }
                } else {
                    segmentStart = found.shape
                    inSegment = true
                }
            }
        }
    }

    return crossingNumber
}

private fun calculateVerticalCrossingNumber(map: Map<Pair<Int, Int>, Pipe>, pointX: Int, pointY: Int): Int {
    var crossingNumber = 0

    if (map.getAt(pointX, pointY) != null) {
        return 0
    }

    var segmentStart: Char? = null
    var inSegment = false

    (0..pointY).forEach { y ->
        val found = map.getAt(pointX, y)

        if (found != null) {
            //  F - 7
            //  |   |
            //  L - J

            if (found.shape == '-') {
                crossingNumber++
            } else {
                if (inSegment) {
                    if (found.shape != '|') {
                        inSegment = false
                    }
                    if (segmentStart == 'F' && found.shape == 'J') {
                        crossingNumber++
                    } else if (segmentStart == '7' && found.shape == 'L') {
                        crossingNumber++
                    } else if (found.shape == 'S') {
                        crossingNumber++
                    }
                } else {
                    segmentStart = found.shape
                    inSegment = true
                }
            }
        }
    }

    return crossingNumber
}

private fun hasPipesToRight(map: Map<Pair<Int, Int>, Pipe>, pointX: Int, pointY: Int, maxX: Int): Boolean {
    return (pointX..maxX).any { x -> map.getAt(x, pointY) != null }
}

private fun hasPipesLeft(map: Map<Pair<Int, Int>, Pipe>, pointX: Int, pointY: Int): Boolean {
    return (0..pointX).any { x -> map.getAt(x, pointY) != null }
}

private fun hasPipesBelow(map: Map<Pair<Int, Int>, Pipe>, pointX: Int, pointY: Int, maxY: Int): Boolean {
    return (pointY..maxY).any { y -> map.getAt(pointX, y) != null }
}

private fun hasPipesAbove(map: Map<Pair<Int, Int>, Pipe>, pointX: Int, pointY: Int): Boolean {
    return (0..pointY).any { y -> map.getAt(pointX, y) != null }
}

fun parseInput(input: List<String>): Pair<MutableMap<Coordinate, Pipe>, Pipe> {
    var startPipe = Pipe(-1, -1, '.')
    val pipes = mutableMapOf<Coordinate, Pipe>()
    input.forEachIndexed { y, line ->
        line.forEachIndexed { x, char ->
            if (char != '.') {
                val coordinate = Coordinate(x, y)
                val pipe = Pipe(x, y, char)
                if (char == 'S') {
                    pipe.checked = true
                    startPipe = pipe
                }
                pipes[coordinate] = pipe
            }
        }
    }
    return Pair(pipes, startPipe)
}

class Pipe(val x: Int, val y: Int, val shape: Char) {
    var east = false
    var west = false
    var north = false
    var south = false
    var checked = false
    var distance = 0

    private var adjacentPipes = mutableSetOf<Pipe>()

    init {
        when (shape) {
            '|' -> {
                north = true
                south = true
            }

            '-' -> {
                east = true
                west = true
            }

            'J' -> {
                north = true
                west = true
            }

            'L' -> {
                north = true
                east = true
            }

            '7' -> {
                west = true
                south = true
            }

            'F' -> {
                east = true
                south = true
            }

            'S' -> {
                checked = true
            }
        }
    }

    fun getAdjacent(pipes: Map<Coordinate, Pipe>): Set<Pipe> {
        if (adjacentPipes.isNotEmpty()) {
            return adjacentPipes
        }

        if (north) {
            pipes.getAt(x, y - 1)?.let { adjacentPipes.add(it) }
        }

        if (south) {
            pipes.getAt(x, y + 1)?.let { adjacentPipes.add(it) }
        }

        if (west) {
            pipes.getAt(x - 1, y)?.let { adjacentPipes.add(it) }
        }

        if (east) {
            pipes.getAt(x + 1, y)?.let { adjacentPipes.add(it) }
        }

        return adjacentPipes
    }

    fun printChar(): String {
        return when (shape) {
            '|' -> "│"
            '-' -> "─"
            'J' -> "┘"
            'L' -> "└"
            '7' -> "┐"
            'F' -> "┌"
            'S' -> "S"
            else -> "."
        }
    }

    override fun toString(): String {
        var returned = "($x, $y) "

        returned += when (shape) {
            '|' -> "│"
            '-' -> "─"
            'J' -> "┘"
            'L' -> "└"
            '7' -> "┐"
            'F' -> "┌"
            'S' -> "S"
            else -> "."
        }

        returned += " distance = $distance"

        return returned
    }
}

enum class Direction {
    NORTH, SOUTH, EAST, WEST
}