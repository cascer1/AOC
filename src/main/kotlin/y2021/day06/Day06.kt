package y2021.day06

import readInput

private fun main() {
    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day06_test")
    check(simulateFishes(testInput, 80) == 5934L)
    check(simulateFishes(testInput, 256) == 26984457539)

    val input = readInput("Day06")
    println(simulateFishes(input, 80))
    println(simulateFishes(input, 256))
}

private fun growFishes(fishes: Map<Int, Long>): Map<Int, Long> {
    val returned = HashMap<Int, Long>()

    fishes.forEach { (age, count) ->
        if (age == 0) {
            returned[8] = count
            returned[6] = count
        } else {
            val additional = returned[age - 1] ?: 0L
            returned[age - 1] = count + additional
        }
    }

    return returned
}

private fun simulateFishes(input: List<String>, days: Int): Long {
    var fishes = input[0].split(",")
            .groupingBy { it }
            .eachCount()
            .mapKeys { it.key.toInt() }
            .mapValues { (_, v) -> v.toLong() }

    repeat(days) {
        fishes = growFishes(fishes)
    }

    return fishes.values.sum()
}