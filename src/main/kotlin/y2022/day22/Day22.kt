package y2022.day22

import readInput
import java.util.*
import kotlin.collections.HashMap
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Coordinate = Pair<Int, Int>
typealias CoordinateMap = HashMap<Coordinate, Boolean>

val directions = arrayOf('>', 'v', '<', '^')

@OptIn(ExperimentalTime::class)
fun main() {
    val inputFile = "2022/Day22"
    val testInput = readInput("${inputFile}_test")
    require(part1(testInput) == 6032) { "Part 1 check failed" }
    println("Part 1 check successful!")
//    require(part2(testInput, 4) == 5031) { "Part 2 check failed" }
//    println("Part 2 check successful!")

    val input = readInput(inputFile)
    val part1Duration: Duration = measureTime {
        println(part1(input))
    }
    val part2Duration: Duration = measureTime {
        println(part2(input, 50))
    }

    println("Part 1 time: ${part1Duration.toDouble(DurationUnit.MILLISECONDS)} ms")
    println("Part 2 time: ${part2Duration.toDouble(DurationUnit.MILLISECONDS)} ms")
}

private fun part1(input: List<String>): Int {
    val map = parseMap(input)
    val instructions = input.last()

    val topRow = map.filter { it.key.second == 1 }
    val minX = topRow.minOf { it.key.first }
    var position = Coordinate(minX, 1)
    var instruction = ""
    var directionIndex = 40

    instructions.forEach { instructionPart ->
        val numericPart = instructionPart.toString().toIntOrNull()

        if (numericPart != null) {
            instruction += numericPart.toString()
        } else {
            position = move(position, map, directions[directionIndex % 4], instruction.toInt())
            instruction = ""
            if (instructionPart == 'R') {
                directionIndex++
            } else {
                directionIndex--
            }
        }
    }

    if (instruction.isNotEmpty()) {
        position = move(position, map, directions[directionIndex % 4], instruction.toInt())
    }

    return 1000 * position.second + 4 * position.first + directionIndex % 4
}

fun move(position: Coordinate, map: CoordinateMap, direction: Char, distance: Int): Coordinate {
    return when (direction) {
        '>' -> moveRight(position, map, distance)
        'v' -> moveDown(position, map, distance)
        '<' -> moveLeft(position, map, distance)
        '^' -> moveUp(position, map, distance)
        else -> error("Unknown direction: $direction")
    }
}

fun moveRight(position: Coordinate, map: CoordinateMap, distance: Int): Coordinate {
    var x = position.first
    val y = position.second
    val row = map.filter { it.key.second == y }
    val minX = row.minOf { it.key.first }
    val maxX = row.maxOf { it.key.first }

    var newPosition = position.copy()
    repeat(distance) {
        if (x + 1 > maxX) {
            x = minX
        } else {
            x++
        }

        val walkable = map[Pair(x, y)]!!

        if (!walkable) {
            return newPosition
        }

        newPosition = newPosition.copy(first = x)
    }

    return newPosition
}

fun moveDown(position: Coordinate, map: CoordinateMap, distance: Int): Coordinate {
    val x = position.first
    var y = position.second
    val column = map.filter { it.key.first == x }
    val minY = column.minOf { it.key.second }
    val maxY = column.maxOf { it.key.second }

    var newPosition = position.copy()
    repeat(distance) {
        if (y + 1 > maxY) {
            y = minY
        } else {
            y++
        }

        val walkable = map[Pair(x, y)]!!

        if (!walkable) {
            return newPosition
        }

        newPosition = newPosition.copy(second = y)
    }

    return newPosition
}

fun moveLeft(position: Coordinate, map: CoordinateMap, distance: Int): Coordinate {
    var x = position.first
    val y = position.second
    val row = map.filter { it.key.second == y }
    val minX = row.minOf { it.key.first }
    val maxX = row.maxOf { it.key.first }

    var newPosition = position.copy()
    repeat(distance) {
        if (x - 1 < minX) {
            x = maxX
        } else {
            x--
        }

        val walkable = map[Pair(x, y)]!!

        if (!walkable) {
            return newPosition
        }

        newPosition = newPosition.copy(first = x)
    }

    return newPosition
}

fun moveUp(position: Coordinate, map: CoordinateMap, distance: Int): Coordinate {
    val x = position.first
    var y = position.second
    val column = map.filter { it.key.first == x }
    val minY = column.minOf { it.key.second }
    val maxY = column.maxOf { it.key.second }

    var newPosition = position.copy()
    repeat(distance) {
        if (y - 1 < minY) {
            y = maxY
        } else {
            y--
        }

        val walkable = map[Pair(x, y)]!!

        if (!walkable) {
            return newPosition
        }

        newPosition = newPosition.copy(second = y)
    }

    return newPosition
}


private fun part2(input: List<String>, segmentSize: Int): Int {
    val map = parseMap(input)
    val instructions = input.last()


    // probably just manually define transformations for walking off the different edges
    // like if (x in (a .. b) and y in (c .. d)) -> transform x + a & y + b, set direction to c

    return 2
}

fun parseMap(input: List<String>): CoordinateMap {
    val returned = CoordinateMap()

    input.forEachIndexed { y, line ->
        line.forEachIndexed { x, char ->
            if (char == '.') {
                returned[Pair(x + 1, y + 1)] = true
            } else if (char == '#') {
                returned[Pair(x + 1, y + 1)] = false
            }
        }
    }

    return returned
}