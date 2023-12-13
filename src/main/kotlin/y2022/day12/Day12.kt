package y2022.day12

import Coordinate
import getAt
import readInput
import surroundingMatching
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

var target = Coordinate(0, 0)
var start = Coordinate(0, 0)

private fun Map<Coordinate, Point>.surroundingUnvisited(point: Point): ArrayList<Point> {
    return this.surroundingMatching(point.x, point.y, Point::unvisited)
}

private fun Map<Coordinate, Point>.copy(): Map<Coordinate, Point> {
    val returned = HashMap<Coordinate, Point>()

    this.forEach { (key, value) ->
        returned[key.copy()] = value.copy()
    }

    return returned
}

@OptIn(ExperimentalTime::class)
fun main() {
    val inputFile = "2022/Day12"
    val testInput = readInput("${inputFile}_test")
    check(part1(testInput) == 31)
    println("Part 1 check successful!")
    check(part2(testInput) == 29)
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
    val map = parseInput(input, true)
    return findBestRoute(map, start)
}

private fun part2(input: List<String>): Int {
    val map = parseInput(input, false)

    return map.filterValues { it.height == "1".toByte() }.values.minOf {
        findBestRoute(map.copy(), Coordinate(it.x, it.y))
    }
}

fun findBestRoute(map: Map<Coordinate, Point>, start: Coordinate): Int {
    val pointQueue: ArrayDeque<Point> = ArrayDeque()
    val startPoint = map.getAt(start.x, start.y)!!
    startPoint.distance = 0
    pointQueue.add(startPoint)

    while (pointQueue.isNotEmpty()) {
        val currentPoint = pointQueue.removeFirst()
        currentPoint.visited = true

        map.surroundingUnvisited(currentPoint).forEach { point ->
            if (point.height - 1 <= currentPoint.height) {
                point.distance = point.distance.coerceAtMost(currentPoint.distance + 1)
                if (!pointQueue.contains(point)) {
                    pointQueue.add(point)
                }
            }
        }
    }

    val finish = map.getAt(target.x, target.y)!!
    return finish.distance
}

fun parseInput(input: List<String>, startVisited: Boolean): HashMap<Coordinate, Point> {
    val points = HashMap<Coordinate, Point>()
    input.forEachIndexed { y, row ->
        row.forEachIndexed { x, char ->
            points[Coordinate(x, y)] = toPoint(char, x, y, startVisited)
        }
    }

    return points
}

fun toPoint(char: Char, x: Int, y: Int, startVisited: Boolean): Point {
    if (char == 'E') {
        target = Coordinate(x, y)
        return Point(height = 26, x = x, y = y)
    }

    if (char == 'S') {
        start = Coordinate(x, y)
        return Point(height = 1, visited = startVisited, x = x, y = y)
    }

    return Point(height = (char.code - 96).toByte(), x = x, y = y)
}