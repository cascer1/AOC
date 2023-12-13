@file:OptIn(ExperimentalTime::class)

package y2021.day15

import Coordinate
import getAt
import readInput
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

private var map: HashMap<Coordinate, Point> = HashMap()

private var width: Int = 0
private var height: Int = 0

private fun HashMap<Coordinate, Point>.surroundingUnvisited(x: Int, y: Int): ArrayList<Point> {
    return ArrayList(setOfNotNull(
            this.getAt(x, y - 1), // above
            this.getAt(x - 1, y), // left
            this.getAt(x + 1, y), // right
            this.getAt(x, y + 1)  // below
    ).filter { !it.visited })
}

private fun HashMap<Coordinate, Point>.surroundingUnvisited(point: Point): ArrayList<Point> {
    return this.surroundingUnvisited(point.x, point.y)
}

private fun parseInput(input: List<String>) {
    width = input[0].length
    height = input.size

    map.clear()
    input.forEachIndexed { y, row ->
        row.forEachIndexed { x, risk ->
            map[Coordinate(x, y)] = Point(x, y, risk.digitToInt())
        }
    }
}

private fun main() {
    val testInput = readInput("2021/Day15_test")
    parseInput(testInput)
    check(part1() == 40)
    check(part2() == 315)

    val input = readInput("2021/Day15")
    parseInput(input)

    val part1Duration: Duration = measureTime {
        println(part1())
    }
    val part2Duration: Duration = measureTime {
        println(part2())
    }

    println("Part 1 time: ${part1Duration.toDouble(DurationUnit.MILLISECONDS)} ms")
    println("Part 2 time: ${part2Duration.toDouble(DurationUnit.MILLISECONDS)} ms")
}

private fun part1(): Int {
    return findBestRoute()
}

private fun part2(): Int {
    expandMap()
    map.forEach { (_, u) ->
        u.distance = Int.MAX_VALUE
        u.visited = false
    }

    return findBestRoute()
}

private fun findBestRoute(): Int {
    val pointQueue: ArrayDeque<Point> = ArrayDeque()
    val start = map.getAt(0, 0)!!
    start.distance = 0
    pointQueue.add(start)

    while (pointQueue.isNotEmpty()) {
        val currentPoint = pointQueue.removeFirst()
        currentPoint.visited = true

        map.surroundingUnvisited(currentPoint).forEach { point ->
            point.distance = point.distance.coerceAtMost(currentPoint.distance + point.risk)
            if (!pointQueue.contains(point)) {
                pointQueue.add(point)
            }
        }
    }

    val finish: Point = map.getAt(width - 1, height - 1)!!
    return finish.distance
}

private fun expandMap() {
    var originalWidth = width - 1

    (1 until 5).forEach {
        val offset = it * (originalWidth + 1)
        val original = offset - (originalWidth + 1)
        val difference = offset - original

        (original until offset).forEach { x ->
            (0 until height).forEach { y ->
                val originalPoint = map.getAt(x, y)!!
                map[Coordinate(x + difference, y)] = Point(x + difference, y, if (originalPoint.risk < 9) originalPoint.risk + 1 else 1)
            }
        }
    }

    originalWidth = width * 5 - 1

    (1 until 5).forEach {
        val offset = it * height
        val original = offset - height
        val difference = offset - original

        (0..originalWidth).forEach { x ->
            (original until offset).forEach { y ->
                val originalPoint = map.getAt(x, y)!!
                map[Coordinate(x, y + difference)] = Point(x, y + difference, if (originalPoint.risk < 9) originalPoint.risk + 1 else 1)
            }
        }
    }

    width *= 5
    height *= 5
}

private data class Point(var x: Int, var y: Int, var risk: Int, var distance: Int = Int.MAX_VALUE, var visited: Boolean = false)