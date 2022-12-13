package y2022.day12

import getAt
import readInput
import surroundingMatching
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

var target = Pair(0, 0)
var start = Pair(0, 0)

private fun Map<Pair<Int, Int>, Point>.surroundingUnvisited(point: Point): ArrayList<Point> {
    return this.surroundingMatching(point.x, point.y, Point::unvisited)
}

private fun Map<Pair<Int, Int>, Point>.copy(): Map<Pair<Int, Int>, Point> {
    val returned = HashMap<Pair<Int, Int>, Point>()

    this.forEach { (key, value) ->
        val newKey = key.copy()
        val newValue = value.copy()
        returned[newKey] = newValue
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

fun part1(input: List<String>): Int {
    val map = parseInput(input, true)
    return findBestRoute(map, start)
}

fun part2(input: List<String>): Int {
    val map = parseInput(input, false)

    return map.filterValues { it.height == "1".toByte() }.values.minOf {
        findBestRoute(map.copy(), Pair(it.x, it.y))
    }
}

fun findBestRoute(map: Map<Pair<Int, Int>, Point>, start: Pair<Int, Int>): Int {
    val pointQueue: ArrayDeque<Point> = ArrayDeque()
    val startPoint = map.getAt(start.first, start.second)!!
    startPoint.distance = 0
    pointQueue.add(startPoint)

    while (!pointQueue.isEmpty()) {
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

    val finish = map.getAt(target.first, target.second)!!
    return finish.distance
}

fun parseInput(input: List<String>, startVisited: Boolean): HashMap<Pair<Int, Int>, Point> {
    val points = HashMap<Pair<Int, Int>, Point>()
    input.forEachIndexed { y, row ->
        row.forEachIndexed { x, char ->
            points[Pair(x, y)] = toPoint(char, x, y, startVisited)
        }
    }

    return points
}

fun toPoint(char: Char, x: Int, y: Int, startVisited: Boolean): Point {
    if (char == 'E') {
        target = Pair(x, y)
        return Point(height = 26, x = x, y = y)
    }

    if (char == 'S') {
        start = Pair(x, y)
        return Point(height = 1, visited = startVisited, x = x, y = y)
    }

    return Point(height = (char.code - 96).toByte(), x = x, y = y)
}