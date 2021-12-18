@file:OptIn(ExperimentalTime::class)

package y2021.day17

import kotlin.math.absoluteValue
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

var targetX = (0..0)
var targetY = (0..0)

fun main() {
    targetX = (20..30)
    targetY = (-10..-5)
    check(part1() == 45)
    check(part2() == 112)

    targetX = (281..311)
    targetY = (-74..-54)
    val part1Duration: Duration = measureTime {
        println(part1())
    }
    val part2Duration: Duration = measureTime {
        println(part2())
    }

    println("Part 1 time: ${part1Duration.toDouble(DurationUnit.MILLISECONDS)} ms")
    println("Part 2 time: ${part2Duration.toDouble(DurationUnit.MILLISECONDS)} ms")
}

fun calculateYVelocities(): MutableSet<Int> {
    val result: MutableSet<Int> = mutableSetOf()

    startVelocity@ for (startVelocity in targetY.first..10000) {
        var y = 0
        var velocity = startVelocity
        do {
            y += velocity
            velocity--
            if (y in targetY) {
                result += startVelocity
                continue@startVelocity
            }
        } while (y > targetY.first)
    }

    return result
}

fun calculateXVelocities(): MutableSet<Int> {
    val result: MutableSet<Int> = mutableSetOf()

    startVelocity@ for (startVelocity in 0..targetX.last) {
        var x = 0
        var velocity = startVelocity
        do {
            x += velocity

            if (velocity > 0) velocity--
            if (velocity < 0) velocity++

            if (x in targetX) {
                result += startVelocity
                continue@startVelocity
            }
        } while (velocity > 0 && x < targetX.last)
    }

    return result
}

fun startVelocityIsValid(xSpeed: Int, ySpeed: Int): Boolean {
    var x = 0
    var y = 0
    var xVelocity= xSpeed
    var yVelocity = ySpeed
    do {
        x += xVelocity
        y += yVelocity
        yVelocity--
        if (xVelocity > 0) xVelocity--
        if (xVelocity < 0) xVelocity++

        if (x in targetX && y in targetY) {
            return true
        }
    } while (y >= targetY.first)

    return false
}

fun part1(): Int {

    var count = 0
    (1 until targetY.maxOf { it.absoluteValue }.absoluteValue).forEach { step ->
        count += step
    }

    return count
}

fun part2(): Int {
    val xVelocities = calculateXVelocities()
    val yVelocities = calculateYVelocities()

    return xVelocities.flatMap { x ->
        yVelocities.map { y ->
            x to y
        }
    }.filter { startVelocityIsValid(it.first, it.second) }.size
}
