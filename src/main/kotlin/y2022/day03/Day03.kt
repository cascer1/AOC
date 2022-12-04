package y2022.day03

import readInput
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

lateinit var rucksacks: List<Rucksack>

@OptIn(ExperimentalTime::class)
fun main() {
    val inputFile = "2022/Day03"
    val testInput = readInput("${inputFile}_test")
    check(part1(testInput) == 157)
    println("Part 1 check successful!")
    check(part2() == 70)
    println("Part 2 check successful!")

    val input = readInput(inputFile)
    val part1Duration: Duration = measureTime {
        println(part1(input))
    }
    val part2Duration: Duration = measureTime {
        println(part2())
    }

    println("Part 1 time: ${part1Duration.toDouble(DurationUnit.MILLISECONDS)} ms")
    println("Part 2 time: ${part2Duration.toDouble(DurationUnit.MILLISECONDS)} ms")
}

fun part1(input: List<String>): Int {
    rucksacks = parseInput(input)

    return rucksacks
        .mapNotNull { it.findDuplicate() }
        .sumOf { getCharValue(it) }
}

fun part2(): Int {
    val groups = rucksacks.windowed(3, 3)

    return groups.map { group ->
        group.map { it.getUniqueLetters() }
            .reduce { first, second -> first.intersect(second) }
            .first()
    }.sumOf { getCharValue(it) }
}

fun parseInput(input: List<String>): List<Rucksack> {
    return input.map { Rucksack(it) }.toList()
}

fun getCharValue(character: Char): Int {
    return if (character.isLowerCase()) {
        character.code - 96
    } else {
        character.code - 38
    }
}