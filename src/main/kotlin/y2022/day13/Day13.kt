package y2022.day13

import readInput
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
fun main() {
    val inputFile = "2022/Day13"
    val testInput = readInput("${inputFile}_test")
    check(part1(testInput) == 13)
    println("Part 1 check successful!")
    check(part2(testInput) == 140)
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

private fun part1(input: List<String>): Int {
    val parsed = parseInput(input)

    return parsed.chunked(2)
        .withIndex()
        .filter { (_, pair) -> compare(pair[0], pair[1]) == -1 }
        .sumOf { (i, _) -> i + 1 }
}

private fun part2(input: List<String>): Int {
    val parsed = parseInput(input)
    val distress = listOf(listOf(listOf(2)), listOf(listOf(6)))
    return parsed
        .also { it.addAll(distress) }
        .sortedWith(::compare)
        .withIndex()
        .filter { it.value in distress }
        .fold(1) { acc, (i, _) -> (i + 1) * acc }
}

fun parseInput(input: List<String>): MutableList<Any> {
    return input.filter { it.isNotBlank() }
        .map { it.parseLists() }
        .toMutableList()
}

fun String.parseLists(): Any {
    val stack: MutableList<MutableList<Any>> = mutableListOf(mutableListOf())
    val normalized = this.replace("]", ",}").replace("[", "{,").replace(",,", ",").split(",")
    normalized.forEach {
        when (it) {
            "{" -> {
                val m: MutableList<Any> = mutableListOf()
                stack.last().add(m)
                stack.add(m)
            }

            "}" -> stack.removeLast()
            else -> stack.last().add(it.toInt())
        }
    }
    return stack[0][0]
}

fun compare(left: Any, right: Any): Int {
    if (left is Int && right is Int) {
        return left.compareTo(right)
    }

    val leftList = if (left is MutableList<*>) left else mutableListOf(left)
    val rightList = if (right is MutableList<*>) right else mutableListOf(right)

    for ((l, r) in leftList zip rightList) {
        if (compare(l!!, r!!) != 0) {
            return compare(l, r)
        }
    }

    return compare(leftList.size, rightList.size)
}