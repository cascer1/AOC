package y2023.day02

import readInput
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
fun main() {
    val inputFile = "2023/Day02"
    val testInput = readInput("${inputFile}_test")
    val limits = ColorCount(red = 12, green = 13, blue = 14)
    require(part1(testInput, limits) == 8) { "Part 1 check failed" }

    val input = readInput(inputFile)
    val part1Duration: Duration = measureTime {
        println(part1(input, limits))
    }
    println("Part 1 time: ${part1Duration.toDouble(DurationUnit.MILLISECONDS)} ms")

    require(part2(testInput) == 2286) { "Part 2 check failed" }
    val part2Duration: Duration = measureTime {
        println(part2(input))
    }

    println("Part 2 time: ${part2Duration.toDouble(DurationUnit.MILLISECONDS)} ms")
}

private fun part1(input: List<String>, limits: ColorCount): Int {
    return input.map { parseGame(it) }
        .filter { it.isPossible(limits) }
        .sumOf { it.id }
}

private fun part2(input: List<String>): Int {
    return input.map { parseGame(it) }
        .sumOf { it.power }
}

fun parseGame(input: String): Game {
    // Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green
    val (gameIdString, handsString) = input.split(": ")
    val handStrings = handsString.split("; ")

    val gameId = gameIdString.removePrefix("Game ").toInt()

    val colorCounts = handStrings.map { handString ->
        var red = 0
        var green = 0
        var blue = 0

        val colors = handString.split(", ")
        colors.forEach { colorString ->
            val (number, color) = colorString.split(" ")
            when (color) {
                "red" -> red = number.toInt()
                "green" -> green = number.toInt()
                "blue" -> blue = number.toInt()
            }
        }

        ColorCount(red, green, blue)
    }

    return Game(gameId, colorCounts)
}

class Game(val id: Int, private val hands: List<ColorCount>) {
    fun isPossible(colorCount: ColorCount): Boolean {
        return hands.all { it.isPossible(colorCount) }
    }

    private val minColors: ColorCount
        get() {
            var minRed = Int.MIN_VALUE
            var minGreen = Int.MIN_VALUE
            var minBlue = Int.MIN_VALUE

            hands.forEach {
                minRed = maxOf(minRed, it.red)
                minGreen = maxOf(minGreen, it.green)
                minBlue = maxOf(minBlue, it.blue)
            }

            return ColorCount(minRed, minGreen, minBlue)
        }

    val power: Int
        get() {
            val colors = minColors
            return colors.red * colors.green * colors.blue
        }
}

class ColorCount(val red: Int, val green: Int, val blue: Int) {
    fun isPossible(limit: ColorCount): Boolean {
        return (red <= limit.red && green <= limit.green && blue <= limit.blue)
    }
}