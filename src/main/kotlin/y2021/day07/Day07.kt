package y2021.day07

import readInput
import kotlin.math.abs

private fun main() {
    // test if implementation meets criteria from the description, like:
    val testInput = readInput("2021/Day07_test")
    check(part1(testInput) == 37L)
    check(part2(testInput) == 168L)

    val input = readInput("2021/Day07")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Long {
    val crabs = parseCrabs(input)

    val lowestPosition = crabs.keys.minOf { it }
    val highestPosition = crabs.keys.maxOf { it }

    var lowest = Long.MAX_VALUE
    (lowestPosition..highestPosition).forEach { position ->
        val result = crabs.map { (k, v) -> abs(position - k) * v }
                .sum()
        if (result < lowest) {
            lowest = result
        }
    }

    return lowest
}

private fun part2(input: List<String>): Long {
    val crabs = parseCrabs(input)

    val lowestPosition = crabs.keys.minOf { it }
    val highestPosition = crabs.keys.maxOf { it }

    var lowest = Long.MAX_VALUE
    (lowestPosition..highestPosition).forEach { position ->
        val result = crabs.map { (k, v) ->
            val cost = calculateMovementCost(abs(position - k))
            cost * v
        }
                .sum()
        if (result < lowest) {
            lowest = result
        }
    }

    return lowest
}

private fun parseCrabs(input: List<String>): Map<Long, Long> {
    return input[0].split(",")
            .groupingBy { it }
            .eachCount()
            .mapKeys { it.key.toLong() }
            .mapValues { (_, v) -> v.toLong() }
}

private fun calculateMovementCost(distance: Long): Int {
    var step = 1
    var cost = 0

    repeat(distance.toInt()) {
        cost += step
        step++
    }

    return cost
}