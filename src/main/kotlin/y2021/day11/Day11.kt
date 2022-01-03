package y2021.day11

import allMatch
import anyMatch
import get
import getAt
import readInput

private var octopi: Array<Array<Octopus>> = emptyArray()

private fun Array<Array<Octopus>>.getAdjacent(octopus: Octopus): Set<Octopus> {
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

private fun Array<Array<Octopus>>.incrementEnergy(): Array<Array<Octopus>> {
    this.forEach { row ->
        row.forEach { it.energy++ }
    }
    return this
}

private fun Array<Array<Octopus>>.resetFlashStates() {
    this.forEach { row ->
        row.filter { it.flashed }.forEach { octopus ->
            octopus.flashed = false
            octopus.energy = 0
        }
    }
}

private fun main() {
    // test if implementation meets criteria from the description, like:
    val testInput = readInput("2021/Day11_test")
    parseInput(testInput)
    check(part1() == 1656)
    check(part2() == 195)

    val input = readInput("2021/Day11")
    parseInput(input)
    println(part1())
    println(part2())
}

private fun parseInput(input: List<String>) {
    octopi = input.mapIndexed { y, row ->
        row.mapIndexed { x, energy ->
            Octopus(x, y, energy.digitToInt())
        }.toTypedArray()
    }.toTypedArray()
}

private fun part1(): Int {
    var flashCount = 0

    repeat(100) {
        flashCount += step(octopi)
        octopi.resetFlashStates()
    }

    return flashCount
}

private fun part2(): Int {
    var day = 100
    do {
        octopi.resetFlashStates()
        step(octopi)
        day++
    } while (!octopi.allMatch { it.flashed })

    return day
}

private fun step(octopi: Array<Array<Octopus>>): Int {
    octopi.incrementEnergy()
    var flashes = 0

    while (octopi.anyMatch{it.energy > 9}) {
        val toFlash = octopi.get { it.energy > 9 }
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

private data class Octopus(val x: Int, val y: Int, var energy: Int, var flashed: Boolean = false)