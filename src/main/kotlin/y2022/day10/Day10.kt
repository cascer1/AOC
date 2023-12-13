package y2022.day10

import readInput
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

var x = 1
var cycle = 0
var pixelPosition = 0
var outputs = ArrayList<Int>()

@OptIn(ExperimentalTime::class)
fun main() {
    val inputFile = "2022/Day10"
    val testInput = readInput("${inputFile}_test")
    check(part1(testInput) == 13140)
    println("Part 1 check successful!")

    val input = readInput(inputFile)
    val part1Duration: Duration = measureTime {
        println(part1(input))
    }

    println("Part 1 time: ${part1Duration.toDouble(DurationUnit.MILLISECONDS)} ms")
}

private fun part1(input: List<String>): Int {
    reset()
    input.forEach { instruction ->
        cycle++
        evaluateCycle()

        if (instruction != "noop") {
            val parts = instruction.split(" ")
            val number = parts[1].toInt()

            cycle++
            evaluateCycle()
            x += number
        }
    }


    return outputs.sum()
}


fun evaluateCycle() {
    if (cycle == 20 || (cycle - 20) % 40 == 0) {
        outputs.add(cycle * x)
    }
    printPixel()
}

fun printPixel() {
    pixelPosition++

    if (pixelPosition == 40) {
        pixelPosition = 0
    }

    if (x + 2 == pixelPosition || x + 1 == pixelPosition || x == pixelPosition) {
        print("#")
    } else {
        print(".")
    }

    if (pixelPosition == 0) {
        println()
    }
}

fun reset() {
    x = 1
    cycle = 0
    pixelPosition = 0
    outputs = ArrayList()
}