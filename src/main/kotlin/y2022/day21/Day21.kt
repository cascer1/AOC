package y2022.day21

import readInput
import kotlin.math.roundToLong
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
fun main() {
    val inputFile = "2022/Day21"
    val testInput = readInput("${inputFile}_test")
    check(part1(testInput) == 152L)
    println("Part 1 check successful!")
    check(part2(testInput) == 301L)
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

fun part1(input: List<String>): Long {
    val monkeys = parseInput(input)
    return monkeys["root"]!!.answer.roundToLong()
}

fun part2(input: List<String>): Long {
    val monkeys = parseInput(input)

    monkeys["root"]!!.operator = '='

    val human = monkeys["humn"]!!
    val root = monkeys["root"]!!

    var (low, high) = -1e19 to 1e19
    var swapped = false

    while (root.leftAnswer != root.rightAnswer) {
        val mid = (low + high) / 2
        human.setNumber(mid.roundToLong().toDouble())

        // we've exhausted all options, try going in the different direction next
        if (!swapped && low == high) {
            low = -1e19
            high = 1e19
            swapped = true
            continue
        }

        if (!swapped) {
            if (root.leftAnswer < root.rightAnswer) {
                low = mid
            } else {
                high = mid
            }
        } else {
            if (root.leftAnswer < root.rightAnswer) {
                high = mid
            } else {
                low = mid
            }
        }
    }

    return human.answer.roundToLong()
}

fun parseInput(input: List<String>): HashMap<String, Monkey> {
    val returned = HashMap<String, Monkey>()

    input.map { Monkey(it, returned) }.forEach {
        returned[it.name] = it
    }

    return returned
}