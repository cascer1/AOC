package day11

import readInput
import java.util.function.IntPredicate
import java.util.function.Predicate

var octopi: Array<Array<Octopus>> = emptyArray()

fun Array<Array<Octopus>>.getAt(x: Int, y: Int): Octopus? {
    return this.getOrNull(y)?.getOrNull(x)
}

fun Array<Array<Octopus>>.getAdjacent(octopus: Octopus): Set<Octopus> {
    return setOf(
        this.getAt(octopus.x - 1, octopus.y - 1),
        this.getAt(octopus.x, octopus.y - 1),
        this.getAt(octopus.x + 1, octopus.y - 1),
        this.getAt(octopus.x - 1, octopus.y),
        this.getAt(octopus.x + 1, octopus.y),
        this.getAt(octopus.x - 1, octopus.y + 1),
        this.getAt(octopus.x, octopus.y + 1),
        this.getAt(octopus.x + 1, octopus.y + 1)
    ).filterNotNull().toSet()
}

fun Array<Array<Octopus>>.matches(predicate: Predicate<Octopus>): Boolean {
    return this.all { row ->
        row.all { predicate.test(it) }
    }
}

fun Array<Array<Octopus>>.anyWithEnergy(predicate: IntPredicate): Boolean {
    return this.any { row ->
        row.any { predicate.test(it.energy) }
    }
}

fun Array<Array<Octopus>>.getWithEnergy(predicate: IntPredicate): Set<Octopus> {
    return this.flatMap { row ->
        row.filter { predicate.test(it.energy) }
    }.toSet()
}

fun Array<Array<Octopus>>.incrementEnergy(): Array<Array<Octopus>> {
    this.forEach { row ->
        row.forEach { it.energy++ }
    }
    return this
}

fun Array<Array<Octopus>>.resetFlashStates() {
    this.forEach { row ->
        row.filter { it.flashed }.forEach { octopus ->
            octopus.flashed = false
            octopus.energy = 0
        }
    }
}

fun main() {
    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day11_test")
    parseInput(testInput)
    check(simulateOctopi(100) == 1656)
    check(findFirstFullFlashDay(100) == 195)

    val input = readInput("Day11")
    parseInput(input)
    println(simulateOctopi(100))
    println(findFirstFullFlashDay(100))
}

fun step(octopi: Array<Array<Octopus>>): Int {
    octopi.incrementEnergy()
    var flashes = 0

    while (octopi.anyWithEnergy { it > 9 }) {
        val toFlash = octopi.getWithEnergy { it > 9 }
        flashes += toFlash.size

        toFlash.forEach { octopus ->
            octopus.energy = 0
            octopus.flashed = true
            octopi.getAdjacent(octopus)
                .filter { !it.flashed }
                .forEach { surroundingOctopus -> surroundingOctopus.energy++ }
        }
    }

    return flashes
}

fun parseInput(input: List<String>) {
    octopi = input.mapIndexed { y, row ->
        row.mapIndexed { x, energy ->
            Octopus(x, y, energy.digitToInt())
        }.toTypedArray()
    }.toTypedArray()
}

fun simulateOctopi(days: Int): Int {
    var flashCount = 0

    repeat(days) {
        flashCount += step(octopi)
        octopi.resetFlashStates()
    }

    return flashCount
}

fun findFirstFullFlashDay(startDay: Int): Int {
    var day = startDay
    do {
        octopi.resetFlashStates()
        step(octopi)
        day++
    } while (!octopi.matches { it.flashed })

    return day
}

data class Octopus(val x: Int, val y: Int, var energy: Int, var flashed: Boolean = false)