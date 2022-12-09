package y2022.day09

import readInput
import kotlin.math.abs
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

private var visitedPoints = HashSet<Pair<Int, Int>>()

private var coordinates = ArrayList<Pair<Int, Int>>()

@OptIn(ExperimentalTime::class)
fun main() {
    val inputFile = "2022/Day09"
    var testInput = readInput("${inputFile}_test")
    check(simulateRope(testInput, 2) == 13)
    println("Part 1 check successful!")
    testInput = readInput("${inputFile}_test2")
    check(simulateRope(testInput, 10) == 36)
    println("Part 2 check successful!")

    val input = readInput(inputFile)
    val part1Duration: Duration = measureTime {
        println(simulateRope(input, 2))
    }
    val part2Duration: Duration = measureTime {
        println(simulateRope(input, 10))
    }

    println("Part 1 time: ${part1Duration.toDouble(DurationUnit.MILLISECONDS)} ms")
    println("Part 2 time: ${part2Duration.toDouble(DurationUnit.MILLISECONDS)} ms")
}

fun simulateRope(input: List<String>, segments: Int): Int {
    reset(segments)
    visitedPoints.add(Pair(0, 0))
    input.forEach { instruction ->
        val parts = instruction.split(" ")
        val direction = parts[0]
        val count = parts[1].toInt()
        move(direction, count)
    }

    return visitedPoints.size
}

fun move(direction: String, count: Int) {
    repeat(count) {
        moveHead(direction)
        moveTail()
    }
}

fun movePoint(stepX: Int, stepY: Int, index: Int) {
    val (oldX, oldY) = coordinates[index]
    coordinates[index] = Pair(oldX + stepX, oldY + stepY)
}

fun moveHead(direction: String) {
    when (direction) {
        "R" -> movePoint(1, 0, 0)
        "L" -> movePoint(-1, 0, 0)
        "U" -> movePoint(0, 1, 0)
        "D" -> movePoint(0, -1, 0)
    }
}

fun moveTail() {
    (1..coordinates.lastIndex).forEach {
        moveSegment(it)
    }

    val (x, y) = coordinates.last()
    visitedPoints.add(Pair(x, y))
}

fun moveSegment(index: Int) {
    val (headX, headY) = coordinates[index - 1]
    val (tailX, tailY) = coordinates[index]

    val xDistance = abs(headX - tailX)
    val yDistance = abs(headY - tailY)

    if (xDistance <= 1 && yDistance <= 1) {
        return
    }

    var stepX = 0
    var stepY = 0

    if (xDistance == 2 && yDistance == 0) {
        stepX = if (headX > tailX) 1 else -1
    } else if (xDistance == 2 && yDistance > 0) {
        stepX = if (headX > tailX) 1 else -1
        stepY = if (headY > tailY) 1 else -1
    } else if (yDistance == 2 && xDistance == 0) {
        stepY = if (headY > tailY) 1 else -1
    } else if (yDistance == 2 && xDistance > 0) {
        stepX = if (headX > tailX) 1 else -1
        stepY = if (headY > tailY) 1 else -1
    }

    movePoint(stepX, stepY, index)
}

fun reset(segments: Int) {
    coordinates = ArrayList()
    visitedPoints = HashSet()

    repeat(segments) {
        coordinates.add(Pair(0, 0))
    }
}
