package y2022.day11

import readInput
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

var monkeys = ArrayList<Monkey>()


@OptIn(ExperimentalTime::class)
fun main() {
    val inputFile = "2022/Day11"
    val testInput = readInput("${inputFile}_test")
    check(simulateMonkeys(testInput, 20, 3L) == 10605L)
    println("Part 1 check successful!")
    check(simulateMonkeys(testInput, 10000, 1L) == 2713310158L)
    println("Part 2 check successful!")

    val input = readInput(inputFile)
    val part1Duration: Duration = measureTime {
        println(simulateMonkeys(input, 20, 3L))
    }
    val part2Duration: Duration = measureTime {
        println(simulateMonkeys(input, 10000, 1L))
    }

    println("Part 1 time: ${part1Duration.toDouble(DurationUnit.MILLISECONDS)} ms")
    println("Part 2 time: ${part2Duration.toDouble(DurationUnit.MILLISECONDS)} ms")
}

fun simulateMonkeys(input: List<String>, rounds: Int, worryDivisor: Long): Long {
    parseInput(input)

    repeat(rounds) {
        monkeys.forEach { monkey ->
            val handleOutput = monkey.handle(worryDivisor)
            handleOutput.forEach { worryLevel -> giveItem(worryLevel) }
            monkey.throwItems()
        }
    }

    val sortedMonkeys = monkeys.map { it.inspectCount }.sortedDescending()
    return sortedMonkeys[0] * sortedMonkeys[1]
}

fun part2(input: List<String>): Int {
    parseInput(input)

    return 2
}

fun giveItem(item: Pair<Long, Long>) {
    val monkeyId = item.first.toInt()
    val worryLevel = item.second
    monkeys[monkeyId].giveItem(worryLevel)
}

fun parseInput(input: List<String>) {
    monkeys = ArrayList()
    var monkeyId = 0
    var tempDivisor = 0L
    lateinit var tempOperation: (Long, Long) -> Long
    var tempOperationNumber: Long? = null
    var tempItems = ArrayList<Long>()
    var trueDestination = 0L
    var falseDestination = 0L
    Monkey.modulo = 1L

    input.forEach { line ->
        if (line.isBlank()) {
            Monkey.modulo *= tempDivisor
            monkeys.add(
                Monkey(
                    monkeyId,
                    tempOperation,
                    tempOperationNumber,
                    tempDivisor,
                    trueDestination,
                    falseDestination,
                    tempItems
                )
            )
            monkeyId++
            tempItems = ArrayList()
        } else if (line.trim().startsWith("Starting items: ")) {
            tempItems = ArrayList(line.trim().removePrefix("Starting items: ").split(", ").map { it.toLong() }.toList())
        } else if (line.trim().startsWith("Operation: ")) {
            val instructionParts = line.trim().removePrefix("Operation: new = old ").split(" ")
            val operation = instructionParts[0]
            tempOperationNumber = instructionParts[1].toLongOrNull()

            tempOperation = when (operation) {
                "+" -> Long::plus
                "-" -> Long::minus
                "/" -> Long::div
                "*" -> Long::times
                else -> error("Unexpected operation: $operation")
            }

        } else if (line.trim().startsWith("Test: ")) {
            tempDivisor = line.trim().removePrefix("Test: divisible by ").toLong()
        } else if (line.trim().startsWith("If true: ")) {
            trueDestination = line.trim().removePrefix("If true: throw to monkey ").toLong()
        } else if (line.trim().startsWith("If false")) {
            falseDestination = line.trim().removePrefix("If false: throw to monkey ").toLong()
        }
    }
    Monkey.modulo *= tempDivisor
    monkeys.add(
        Monkey(
            monkeyId,
            tempOperation,
            tempOperationNumber,
            tempDivisor,
            trueDestination,
            falseDestination,
            tempItems
        )
    )
}