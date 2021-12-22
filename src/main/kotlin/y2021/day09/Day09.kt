package y2021.day09

import getAt
import readInput

private var heightMap: Array<Array<Point>> = emptyArray()

private fun main() {
    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day09_test")
    heightMap = parseInput(testInput)
    check(part1() == 15)
    check(part2() == 1134)

    val input = readInput("Day09")
    heightMap = parseInput(input)
    println(part1())
    println(part2())
}

private fun part1(): Int {
    return heightMap.sumOf { row ->
        row.filter { point -> isLowestPoint(point) }.sumOf { it.dangerScore }
    }
}

private fun part2(): Int {
    return heightMap
            .flatMap { it.filter { point -> point.lowest } }
            .asSequence()
            .map { determineBasin(it) }
            .distinct()
            .sortedByDescending { it.size }
            .take(3)
            .map { it.size }
            .reduce { a, b -> a * b }
}

private fun parseInput(input: List<String>): Array<Array<Point>> {
    return input.mapIndexed { rowIndex, row ->
        row.mapIndexed { colIndex, height ->
            Point(colIndex, rowIndex, height.toString().toInt())
        }.toTypedArray()
    }.toTypedArray()
}

private fun Array<Array<Point>>.getSurrounding(point: Point): Set<Point> {
    val above = this.getAt(point.x, point.y + 1)
    val below = this.getAt(point.x, point.y - 1)
    val left = this.getAt(point.x - 1, point.y)
    val right = this.getAt(point.x + 1, point.y)

    return setOfNotNull(above, below, left, right)
}

private fun isLowestPoint(point: Point): Boolean {
    val surrounding = heightMap.getSurrounding(point)

    val lowest = surrounding.all { it.height > point.height }

    if (lowest) {
        point.lowest = true
    }

    heightMap[point.y][point.x] = point

    return lowest
}

private fun determineBasin(point: Point): Set<Point> {
    var currentBasin = setOf(point)
    var newBasin: Set<Point>

    while (true) {
        newBasin = expandBasin(currentBasin)
        if (newBasin.size == currentBasin.size) {
            return newBasin
        }
        currentBasin = newBasin
    }
}

private fun expandBasin(basin: Set<Point>): Set<Point> {
    val newBasin = basin.toMutableSet()

    basin.forEach { point ->
        newBasin.addAll(heightMap.getSurrounding(point).filter { it.height < 9 })
    }

    return newBasin
}

private data class Point(var x: Int, var y: Int, var height: Int, var lowest: Boolean = false) {
    val dangerScore: Int
        get() = height + 1
}