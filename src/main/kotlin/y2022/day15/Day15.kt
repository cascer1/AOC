package y2022.day15

import manhattanDistance
import readInput
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
fun main() {
    val inputFile = "2022/Day15"
    val testInput = readInput("${inputFile}_test")
    check(part1(testInput, 10) == 26)
    println("Part 1 check successful!")
    check(part2(testInput, (0..20), (0..20)) == 56000011L)
    println("Part 2 check successful!")

    val input = readInput(inputFile)
    val part1Duration: Duration = measureTime {
        println(part1(input, 2_000_000))
    }
    val part2Duration: Duration = measureTime {
        println(part2(input, (0..4_000_000), (0..4_000_000)))
    }

    println("Part 1 time: ${part1Duration.toDouble(DurationUnit.MILLISECONDS)} ms")
    println("Part 2 time: ${part2Duration.toDouble(DurationUnit.MILLISECONDS)} ms")
}

private fun part1(input: List<String>, y: Int): Int {
    val relevantSensors = parseInput(input).filter { it.isNearY(y) }.sortedBy { it.x }
    val beaconCoordinates = relevantSensors.map { it.beacon }

    val maxDistance = relevantSensors.maxOf { it.distance }
    val minX = relevantSensors.first().x - maxDistance
    val maxX = relevantSensors.last().x + maxDistance

    val coveredCoordinates = HashSet<Pair<Int, Int>>()

    (minX..maxX).forEach { x ->
        val thisCoordinate = Pair(x, y)

        if (!beaconCoordinates.contains(thisCoordinate)
            && relevantSensors.any { it.isInRange(thisCoordinate) }
        ) {
            coveredCoordinates.add(thisCoordinate)
        }
    }

    return coveredCoordinates.size
}

private fun part2(input: List<String>, xRange: IntRange, yRange: IntRange): Long {
    val relevantSensors = parseInput(input).filter { it.isWithin(xRange, yRange) }
    val beaconCoordinates = relevantSensors.map { it.beacon }

    val missingCoordinate = findMissingCoordinate(xRange, yRange, relevantSensors, beaconCoordinates)

    return missingCoordinate.first.toLong() * 4_000_000L + missingCoordinate.second.toLong()
}

fun findMissingCoordinate(
    xRange: IntRange,
    yRange: IntRange,
    sensors: List<Sensor>,
    beacons: List<Pair<Int, Int>>
): Pair<Int, Int> {
    sensors.flatMap { it.getNearbyCoordinates() }.filter { it.isWithin(xRange, yRange) }.forEach { thisCoordinate ->
        if (!beacons.contains(thisCoordinate)
            && sensors.none { it.isInRange(thisCoordinate) }
        ) {
            return thisCoordinate
        }
    }

    error("No empty space found")
}

fun parseInput(input: List<String>): List<Sensor> {
    return input.map(::parseSensor)
}

fun parseSensor(input: String): Sensor {
    //[Sensor at ][x=2, y=18][: closest beacon is at ][x=-2, y=15]
    val parts = input.split("at ", ":")
    val sensorCoordinates = parts[1].toCoordinates()
    val beaconCoordinates = parts[3].toCoordinates()
    val distance = sensorCoordinates.manhattanDistance(beaconCoordinates)
    return Sensor(sensorCoordinates, beaconCoordinates, distance)
}

fun String.toCoordinates(): Pair<Int, Int> {
    val parts = this.removePrefix("x=").split(", y=").map(String::toInt)
    return Pair(parts[0], parts[1])
}

fun Pair<Int, Int>.isWithin(xRange: IntRange, yRange: IntRange): Boolean {
    return xRange.contains(this.first) && yRange.contains(this.second)
}