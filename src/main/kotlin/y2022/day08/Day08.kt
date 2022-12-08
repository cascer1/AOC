package y2022.day08

import getAt
import getColumn
import readInput
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

var trees: Array<Array<Int>> = emptyArray()

@OptIn(ExperimentalTime::class)
fun main() {
    val inputFile = "2022/Day08"
    val testInput = readInput("${inputFile}_test")
    trees = parseInput(testInput)
    check(part1() == 21)
    println("Part 1 check successful!")
    check(part2() == 8)
    println("Part 2 check successful!")

    val input = readInput(inputFile)
    trees = parseInput(input)
    val part1Duration: Duration = measureTime {
        println(part1())
    }
    val part2Duration: Duration = measureTime {
        println(part2())
    }

    println("Part 1 time: ${part1Duration.toDouble(DurationUnit.MILLISECONDS)} ms")
    println("Part 2 time: ${part2Duration.toDouble(DurationUnit.MILLISECONDS)} ms")
}

fun part1(): Int {
    var highestCount = 0

    trees.forEachIndexed { y, ints ->
        ints.forEachIndexed { x, _ ->
            if (trees.isVisibleAt(x, y)) {
                highestCount++
            }
        }
    }

    return highestCount
}

fun part2(): Int {
    return trees.flatMapIndexed { y, column ->
        column.mapIndexed { x, _ ->
            trees.scenicScoreAt(x, y)
        }
    }.max()
}

fun parseInput(input: List<String>): Array<Array<Int>> {
    return input.map { row ->
        row.map { height ->
            height.toString().toInt()
        }.toTypedArray()
    }.toTypedArray()
}

private fun Array<Array<Int>>.isVisibleAt(x: Int, y: Int): Boolean {
    return this.isVisibleHorizontal(x, y) || this.isVisibleVertical(x, y)
}

private fun Array<Array<Int>>.isVisibleHorizontal(x: Int, y: Int): Boolean {
    val treeHeight = this.getAt(x, y)!!
    val row = this[y]

    val leftVisible = (0 until x).none { row[it] >= treeHeight }
    val rightVisible = (x + 1..row.lastIndex).none { row[it] >= treeHeight }

    return leftVisible || rightVisible
}

private fun Array<Array<Int>>.isVisibleVertical(x: Int, y: Int): Boolean {
    val treeHeight = this.getAt(x, y)!!
    val column = this.getColumn(x)

    val topVisible = (0 until y).none { column[it] >= treeHeight }
    val bottomVisible = (y + 1..column.lastIndex).none { column[it] >= treeHeight }

    return topVisible || bottomVisible
}

private fun Array<Array<Int>>.scenicScoreAt(x: Int, y: Int): Int {
    val row = this[y]
    val column = this.getColumn(x)

    return row.viewingDistanceLeft(x) * row.viewingDistanceRight(x) * column.viewingDistanceLeft(y) * column.viewingDistanceRight(
        y
    )
}

private fun Array<Int>.viewingDistanceLeft(x: Int): Int {
    if (x == 0 || x == this.lastIndex) {
        return 0
    }

    val height = this[x]
    var distance = 0

    (x - 1 downTo 0).forEach { i ->
        if (this[i] < height) {
            distance++
        } else {
            return distance + 1
        }
    }

    return distance
}

private fun Array<Int>.viewingDistanceRight(x: Int): Int {
    if (x == 0 || x == this.lastIndex) {
        return 0
    }

    val height = this[x]
    var distance = 0

    (x + 1..this.lastIndex).forEach { i ->
        if (this[i] < height) {
            distance++
        } else {
            return distance + 1
        }
    }
    return distance
}