package y2021.day15

import readInput
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

var map: HashMap<Pair<Int, Int>, Point> = HashMap()
var pointQueue: ArrayDeque<Point> = ArrayDeque()

fun HashMap<Pair<Int, Int>, Point>.pointAt(x: Int, y: Int): Point? {
    return this[Pair(x, y)]
}

fun HashMap<Pair<Int, Int>, Point>.surroundingUnvisited(x: Int, y: Int): ArrayList<Point> {
    return ArrayList(setOfNotNull(
            this.pointAt(x, y - 1), // above
            this.pointAt(x - 1, y), // left
            this.pointAt(x + 1, y), // right
            this.pointAt(x, y + 1)  // below
    ).filter { !it.visited })
}

fun HashMap<Pair<Int, Int>, Point>.surroundingUnvisited(point: Point): ArrayList<Point> {
    return this.surroundingUnvisited(point.x, point.y)
}

fun HashMap<Pair<Int, Int>, Point>.bounds(): Pair<Int, Int> {
    val x = this.maxOf { it.value.x }
    val y = this.maxOf { it.value.y }
    return Pair(x, y)
}

fun ArrayList<Point>.score(): Int {
    return this.drop(1).sumOf { it.risk }
}

fun parseInput(input: List<String>) {
    map.clear()
    input.forEachIndexed { y, row ->
        row.forEachIndexed { x, risk ->
            map[Pair(x, y)] = Point(x, y, risk.digitToInt())
        }
    }
}

@OptIn(ExperimentalTime::class)
fun main() {
    val testInput = readInput("Day15_test")
    parseInput(testInput)
    check(part1() == 40)
    check(part2() == 315)

    val input = readInput("Day15")
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

fun part1(): Int {
    return findBestRoute()
}

fun part2(): Int {
    expandMap()
    pointQueue.clear()
    map.forEach { (_, u) ->
        u.distance = Int.MAX_VALUE
        u.visited = false
    }
    return findBestRoute()
}

fun expandMap() {
    var originalWidth = map.maxOf { it.value.x }
    val originalHeight = map.maxOf { it.value.y }
    (1 until 5).forEach {
        val offset = it * (originalWidth + 1)
        val original = offset - (originalWidth + 1)
        val difference = offset - original

        (original until offset).forEach { x ->
            (0..originalHeight).forEach { y ->
                val originalPoint = map.pointAt(x, y)!!
                map[Pair(x + difference, y)] = Point(x + difference, y, if (originalPoint.risk < 9) originalPoint.risk + 1 else 1)
            }
        }
    }

    originalWidth = map.maxOf { it.value.x }

    (1 until 5).forEach {
        val offset = it * (originalHeight + 1)
        val original = offset - (originalHeight + 1)
        val difference = offset - original

        (0..originalWidth).forEach { x ->
            (original until offset).forEach { y ->
                val originalPoint = map.pointAt(x, y)!!
                map[Pair(x, y + difference)] = Point(x, y + difference, if (originalPoint.risk < 9) originalPoint.risk + 1 else 1)
            }
        }
    }
}

fun findBestRoute(): Int {
    val start = map.pointAt(0, 0)!!
    start.distance = 0
    pointQueue.add(start)

    while (!pointQueue.isEmpty()) {
        val currentPoint = pointQueue.removeFirst()
        currentPoint.visited = true

        map.surroundingUnvisited(currentPoint).forEach { point ->
            val minDistance = point.distance.coerceAtMost(currentPoint.distance + point.risk)

            if (minDistance != point.distance) {
                point.distance = minDistance

                if (!pointQueue.contains(point)) {
                    pointQueue.add(point)
                }
                pointQueue.sort()
            }
        }
    }

    val bounds = map.bounds()
    val finish = map.pointAt(bounds.first, bounds.second)!!
    return finish.distance
}


data class Point(var x: Int, var y: Int, var risk: Int, var distance: Int = Int.MAX_VALUE, var visited: Boolean = false) : Comparable<Point> {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Point

        return (x == other.x && y == other.y)
    }

    override fun hashCode(): Int {
        return 31 * x + y
    }

    override fun toString(): String {
        return "Point([$x, $y] dst: $distance, risk: $risk)"
    }

    override fun compareTo(other: Point): Int {
        return this.distance.compareTo(other.distance)
    }
}