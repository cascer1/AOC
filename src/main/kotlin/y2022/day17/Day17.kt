package y2022.day17

import allEmpty
import readInput
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

val rocks = arrayOf(
    // # # # #
    setOf(Pair(0, 0), Pair(1, 0), Pair(2, 0), Pair(3, 0)),

    // . # .
    // # # #
    // . # .
    setOf(Pair(1, 0), Pair(0, 1), Pair(1, 1), Pair(2, 1), Pair(1, 2)),

    // . . #
    // . . #
    // # # #
    setOf(Pair(2, 2), Pair(2, 1), Pair(0, 0), Pair(1, 0), Pair(2, 0)),

    // #
    // #
    // #
    // #
    setOf(Pair(0, 0), Pair(0, 1), Pair(0, 2), Pair(0, 3)),

    // # #
    // # #
    setOf(Pair(0, 0), Pair(1, 0), Pair(0, 1), Pair(1, 1))
)

const val LEFT_OFFSET = 2
const val BOTTOM_OFFSET = 3
const val CHAMBER_WIDTH = 7

@OptIn(ExperimentalTime::class)
fun main() {
    val inputFile = "2022/Day17"
    val testInput = readInput("${inputFile}_test")
    check(simulateRocks(testInput, 2022) == 3068L)
    println("Part 1 check successful!")
    check(simulateRocks(testInput, 1_000_000_000_000) == 1514285714288L)
    println("Part 2 check successful!")

    val input = readInput(inputFile)
    val part1Duration: Duration = measureTime {
        println(simulateRocks(input, 2022))
    }
    val part2Duration: Duration = measureTime {
        println(simulateRocks(input, 1_000_000_000_000))
    }

    println("Part 1 time: ${part1Duration.toDouble(DurationUnit.MILLISECONDS)} ms")
    println("Part 2 time: ${part2Duration.toDouble(DurationUnit.MILLISECONDS)} ms")
}

fun simulateRocks(input: List<String>, rockCount: Long): Long {
    val horizontalMoves = parseInput(input)
    var grid = HashSet<Pair<Long, Long>>()
    var highestRock = 0L
    var horizontalStep = 0

    (0 until rockCount).forEach { step ->
        var rock = calculateStartingPoint(step, highestRock)
        var oldRock: Set<Pair<Long, Long>>? = null

        while (rock != oldRock) {
            oldRock = rock
            val horizontalMoveIndex = horizontalStep++ % horizontalMoves.size
            val horizontalMove = horizontalMoves[horizontalMoveIndex]
            rock = simulateHorizontal(horizontalMove, rock, grid)
            val verticalRock = simulateVertical(rock, grid)
            if (rock == verticalRock) {
                break
            }
            rock = verticalRock
        }

        val newHighestRock = rock.maxOf { it.second } + 1
        highestRock = highestRock.coerceAtLeast(newHighestRock)

        grid.addAll(rock)
    }

    return highestRock
}

fun parseInput(input: List<String>): List<Char> {
    return input.first().toList()
}

fun simulateHorizontal(horizontalMove: Char, rock: Set<Pair<Long, Long>>, grid: Set<Pair<Long, Long>>): Set<Pair<Long, Long>> {
    if (horizontalMove == '<') {
        // move left
        val newPosition = rock.map { old -> Pair(old.first - 1, old.second) }.toSet()
        if (newPosition.any { it.first < 0 }) {
            // Couldn't move further left
            return rock
        }

        if (grid.allEmpty(newPosition)) {
            return newPosition
        }

        return rock
    } else if (horizontalMove == '>') {
        // move right
        val newPosition = rock.map { old -> Pair(old.first + 1, old.second) }.toSet()
        if (newPosition.any { it.first >= CHAMBER_WIDTH }) {
            // Couldn't move further right
            return rock
        }

        if (grid.allEmpty(newPosition)) {
            return newPosition
        }

        return rock
    }

    error("Unexpected move: $horizontalMove")
}

fun simulateVertical(rock: Set<Pair<Long, Long>>, grid: HashSet<Pair<Long, Long>>): Set<Pair<Long, Long>> {
    val newPosition = rock.map { old -> Pair(old.first, old.second - 1) }.toSet()

    if (newPosition.any { it.second < 0 }) {
        return rock
    }

    if (grid.allEmpty(newPosition)) {
        return newPosition
    }

    return rock
}

fun calculateStartingPoint(step: Long, highestRock: Long): Set<Pair<Long, Long>> {
    val rockIndex = step % 5
    val rock = rocks[rockIndex.toInt()]

    val yOffset = highestRock + BOTTOM_OFFSET
    val xOffset = LEFT_OFFSET.toLong()

    return rock.map { initial -> Pair(initial.first + xOffset, initial.second + yOffset) }.toSet()
}

fun printGrid(grid: HashSet<Pair<Long, Long>>, highestRock: Long, fallingRock: Set<Pair<Long, Long>>? = null) {
    println("=== highest rock : $highestRock ===")
    (highestRock downTo 0).forEach { y ->
        print('|')
        (0L until 7).forEach { x ->
            if (grid.contains(Pair(x, y))) {
                print('#')
            } else if (fallingRock != null && fallingRock.contains(Pair(x, y))) {
                print('@')
            } else {
                print('.')
            }
        }
        print('|')
        println()
    }
    println("+-------+")
    println()
    println()
}
