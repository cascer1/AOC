package y2022.day05

import insertInDeque
import readInput
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

var stacks = ArrayList<ArrayDeque<Char>>()
var instructions = ArrayList<Instruction>()

/*

 first --> [A]
           [B]
           [C]
           [D]
  last --> [E]

 */

@OptIn(ExperimentalTime::class)
fun main() {
    val inputFile = "2022/Day05"
    val testInput = readInput("${inputFile}_test")
    check(part1(testInput) == "CMZ")
    println("Part 1 check successful!")
    check(part2(testInput) == "MCD")
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

private fun part1(input: List<String>): String {
    parseInput(input)

    instructions.forEach { instruction ->
        repeat(instruction.count) {
            val character = stacks[instruction.from - 1].removeFirst()
            stacks[instruction.to - 1].addFirst(character)
        }
    }

    return stacks.map { it.firstOrNull() }.joinToString(separator = "")
}

private fun part2(input: List<String>): String {
    parseInput(input)

    instructions.forEach { instruction ->
        val characters = ArrayDeque<Char>()
        repeat(instruction.count) {
            val character = stacks[instruction.from - 1].removeFirst()
            characters.addFirst(character)
        }

        characters.forEach { character ->
            stacks[instruction.to - 1].addFirst(character)
        }
    }

    return stacks.map { it.firstOrNull() }.joinToString(separator = "")
}

fun parseInput(input: List<String>) {
    stacks = ArrayList()
    instructions = ArrayList()

    var parsingStacks = true

    input.forEach { line ->
        if (line.startsWith(" 1") || line.isBlank()) {
            parsingStacks = false
        } else if (parsingStacks) {
            parseStack(line)
        } else {
            parseInstruction(line)
        }
    }
}

fun parseStack(line: String) {
    val missingLength = line.length % 4

    val tempLine = line.padEnd(line.length + 4 - missingLength, ' ')

    tempLine.windowed(4, 4).forEachIndexed { index, s ->
        if (s.isNotBlank()) {
            val character = s.substring(1, 2)[0]
            stacks.insertInDeque(index, character)
        }
    }
}

fun parseInstruction(line: String) {
    if (!line.startsWith("move ")) {
        return
    }

    val parts = line.split(" ", ignoreCase = false, limit = 6)
    val count = parts[1].toInt()
    val from = parts[3].toInt()
    val to = parts[5].toInt()

    instructions.add(Instruction(count, from, to))
}