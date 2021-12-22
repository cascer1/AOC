package y2021.day05

import readInput
import java.lang.Integer.max
import java.util.function.IntPredicate
import kotlin.math.abs
import kotlin.math.min

private var parsedMap = HashMap<Int, HashMap<Int, Int>>()
private var lines: List<Line> = emptyList()
private var maxX = 0
private var maxY = 0

private fun main() {
    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")
    check(part1(testInput) == 5)
    check(part2(testInput) == 12)

    val input = readInput("Day05")
    println(part1(input))
    println(part2(input))
}

private fun drawStraightLine(line: Line) {
    (line.startY..line.endY).forEach { y ->
        if (!parsedMap.containsKey(y)) {
            parsedMap[y] = HashMap()
        }

        (line.startX..line.endX).forEach { x ->
            parsedMap[y]!![x] = parsedMap[y]!![x]!! + 1
        }
    }
}

private fun drawDiagonalLine(line: Line) {
    val increasingHorizontal = line.start.x < line.end.x
    val increasingVertical = line.start.y < line.end.y
    val steps = abs(line.startX - line.endX)

    (0..steps).forEach { step ->
        val x = if (increasingHorizontal) line.start.x + step else line.start.x - step
        val y = if (increasingVertical) line.start.y + step else line.start.y - step

        parsedMap[y]!![x] = parsedMap[y]!![x]!! + 1
    }
}

private fun initializeMap() {
    (0..maxY).forEach { y ->
        parsedMap[y] = HashMap()

        (0..maxX).forEach { x ->
            parsedMap[y]!![x] = 0
        }
    }
}

private fun parseMap(includeDiagnoal: Boolean = false) {
    initializeMap()

    lines.forEach { line ->
        if (!line.diagonal) {
            drawStraightLine(line)
        } else if (includeDiagnoal) {
            drawDiagonalLine(line)
        }
    }
}

private fun drawMap() {
    (0..maxY).forEach { y ->
        (0..maxX).forEach { x ->
            print(parsedMap[y]!![x])
        }
        println()
    }
}

private fun countOccurence(predicate: IntPredicate): Int {
    var result = 0
    (0..maxY).forEach { y ->
        (0..maxX).forEach { x ->
            if (predicate.test(parsedMap[y]!![x]!!)) {
                result++
            }
        }
    }

    return result
}

private fun part1(input: List<String>): Int {
    lines = input.map { Line(it) }
    parseMap(includeDiagnoal = false)
    return countOccurence { it >= 2 }
}

private fun part2(input: List<String>): Int {
    lines = input.map { Line(it) }
    parseMap(includeDiagnoal = true)
    return countOccurence { it >= 2 }
}

private data class Coordinate(var x: Int, var y: Int) {
    init {
        if (x > maxX) {
            maxX = x
        }

        if (y > maxY) {
            maxY = y
        }
    }
}

private data class Line(val description: String) {
    val start: Coordinate
    val end: Coordinate

    val horizontal: Boolean
        get() = start.y == end.y

    val vertical: Boolean
        get() = start.x == end.x

    val diagonal: Boolean
        get() = !horizontal && !vertical

    val startX: Int
        get() = min(start.x, end.x)

    val endX: Int
        get() = max(start.x, end.x)

    val startY: Int
        get() = min(start.y, end.y)

    val endY: Int
        get() = max(start.y, end.y)

    init {
        val parts = description.split(" -> ")
        val startString = parts[0].split(",")
        val endString = parts[1].split(",")

        start = Coordinate(startString[0].toInt(), startString[1].toInt())
        end = Coordinate(endString[0].toInt(), endString[1].toInt())
    }
}