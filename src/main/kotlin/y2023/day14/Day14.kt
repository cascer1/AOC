package y2023.day14

import readInput
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
fun main() {
    val inputFile = "2023/Day14"
    val testInput = readInput("${inputFile}_test")
    require(part1(testInput) == 136) { "Part 1 check failed" }

    val input = readInput(inputFile)
    val part1Duration: Duration = measureTime {
        println(part1(input))
    }
    println("Part 1 time: ${part1Duration.toDouble(DurationUnit.MILLISECONDS)} ms")

    require(part2(testInput, 1000000000) == 64) { "Part 2 check failed" }
    val part2Duration: Duration = measureTime {
        println(part2(input, 1000000000))
    }

    println("Part 2 time: ${part2Duration.toDouble(DurationUnit.MILLISECONDS)} ms")
}

private fun part1(input: List<String>): Int {
    var rocks = parseInput(input)
    val maxY = input.size - 1
    val maxX = rocks.maxOf { it.x }

    rocks = moveRocks(rocks, Direction.NORTH, maxX, maxY)

    return rocks.filter { it.movable }.sumOf { maxY - it.y + 1 }
}

private fun part2(input: List<String>, cycles: Int): Int {
    var rocks: HashSet<Rock> = parseInput(input)
    val maxY = input.size - 1
    val maxX = rocks.maxOf { it.x }
    val cache = HashMap<HashSet<Rock>, HashSet<Rock>>()
    var firstCacheHit = HashSet<Rock>()

    var hits = 0
    var skipSize = 0
    var offset = 0

    for (i in 0..cycles) {
        if (cache.contains(rocks)) {
            if (hits == 0) {
                hits++
                firstCacheHit = rocks
            } else {
                if (rocks == firstCacheHit) {
                    skipSize = hits
                    offset = i
                    break
                }
                hits++
            }
            rocks = cache.getValue(rocks)
        } else {
            hits = 0
            var next = moveRocks(rocks, Direction.NORTH, maxX, maxY)
            next = moveRocks(next, Direction.WEST, maxX, maxY)
            next = moveRocks(next, Direction.SOUTH, maxX, maxY)
            next = moveRocks(next, Direction.EAST, maxX, maxY)
            cache[rocks] = next
            rocks = next
        }
    }

    val remainingCycles = (cycles - offset) % skipSize
    for (i in 0 until remainingCycles) {
        rocks = cache.getValue(rocks)
    }

    return rocks.filter { it.movable }.sumOf { maxY - it.y + 1 }
}

private fun moveRocks(rocks: HashSet<Rock>, direction: Direction, maxX: Int, maxY: Int): HashSet<Rock> {
    var newRocks: HashSet<Rock>
    var oldRocks = rocks

    while (true) {
        newRocks = oldRocks.map { it.move(oldRocks, direction, maxX, maxY) }.toHashSet()
        if (newRocks.all { oldRocks.contains(it) }) {
            break
        }
        oldRocks = newRocks
    }

    return newRocks
}

private fun parseInput(input: List<String>): HashSet<Rock> {
    // Could be greatly optimized by storing rocks in a 2d array, or Map<Coordinate, Rock>
    return input.flatMapIndexed { y, row ->
        row.mapIndexedNotNull { x, char ->
            if (char == 'O') {
                return@mapIndexedNotNull Rock(x, y, true)
            }
            if (char == '#') {
                return@mapIndexedNotNull Rock(x, y, false)
            }
            null
        }
    }.toHashSet()
}

data class Rock(var x: Int, var y: Int, val movable: Boolean) {
    fun canMove(rocks: HashSet<Rock>, direction: Direction, maxX: Int, maxY: Int): Boolean {
        if (!movable) {
            return false
        }

        return when (direction) {
            Direction.NORTH -> rocks.none { it.x == x && it.y == y - 1 } && y > 0
            Direction.SOUTH -> rocks.none { it.x == x && it.y == y + 1 } && y < maxY
            Direction.WEST -> rocks.none { it.x == x - 1 && it.y == y } && x > 0
            Direction.EAST -> rocks.none { it.x == x + 1 && it.y == y } && x < maxX
        }
    }

    fun move(rocks: HashSet<Rock>, direction: Direction, maxX: Int, maxY: Int): Rock {
        if (!canMove(rocks, direction, maxX, maxY)) {
            return this.copy()
        }

        return when (direction) {
            Direction.NORTH -> this.copy(y = y - 1)
            Direction.SOUTH -> this.copy(y = y + 1)
            Direction.WEST -> this.copy(x = x - 1)
            Direction.EAST -> this.copy(x = x + 1)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Rock

        if (x != other.x) return false
        if (y != other.y) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x
        result = 37 * result + y
        return result
    }


}

enum class Direction {
    NORTH, SOUTH, WEST, EAST
}