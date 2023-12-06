package y2023.day06

import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

typealias Race = Pair<Int, ULong> // time, distance

@OptIn(ExperimentalTime::class)
fun main() {
    var testInput = setOf(Race(7, 9UL), Race(15, 40UL), Race(30, 200UL))
    var realInput = setOf(Race(59, 543UL), Race(68, 1020UL), Race(82, 1664UL), Race(74, 1022UL))

    require(calculateScore(testInput) == 288UL) { "Part 1 check failed" }

    val part1Duration: Duration = measureTime {
        println(calculateScore(realInput))
    }
    println("Part 1 time: ${part1Duration.toDouble(DurationUnit.MILLISECONDS)} ms")


    testInput = setOf(Race(71530, 940200UL))
    realInput = setOf(Race(59688274, 543102016641022UL))

    require(calculateScore(testInput) == 71503UL) { "Part 2 check failed" }
    val part2Duration: Duration = measureTime {
        println(calculateScore(realInput))
    }

    println("Part 2 time: ${part2Duration.toDouble(DurationUnit.MILLISECONDS)} ms")
}

fun calculateScore(input: Set<Race>): ULong {
    return input.map { calculateNumberOfWaysToBeatRecord(it) }.reduce { a, b -> a * b }
}


fun calculateNumberOfWaysToBeatRecord(race: Race): ULong {
    val timeLimit = race.first.toULong()
    val distanceRecord = race.second
    var shortestTime = 0UL
    var longestTime = 0UL
    var shortestfound = false
    var longestFound = false

    var holdTime = 0UL
    while (!shortestfound) {
        val distance = (++holdTime * (timeLimit - holdTime))
        if (distance > distanceRecord) {
            shortestTime = holdTime
            shortestfound = true
        }
    }

    holdTime = timeLimit

    while (!longestFound) {
        val distance = (--holdTime * (timeLimit - holdTime))
        if (distance > distanceRecord) {
            longestTime = holdTime
            longestFound = true
        }
    }

    return (longestTime - shortestTime + 1UL)
}