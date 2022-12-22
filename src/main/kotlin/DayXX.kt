import readInput
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
fun main() {
    val inputFile = "2000/Day"
    val testInput = readInput("${inputFile}_test")
    require(part1(testInput) == 58) { "Part 1 check failed" }
    println("Part 1 check successful!")
    require(part2(testInput) == 58) { "Part 2 check failed" }
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

fun part1(input: List<String>): Int {
    parseInput(input)

    return 1
}

fun part2(input: List<String>): Int {
    parseInput(input)

    return 2
}

fun parseInput(input: List<String>) {
    println("parsing")
}