package y2021.day01

import readInput

private fun main() {
    fun part1(input: List<String>): Int {
        var previous = input[0].toInt()
        var increaseCount = 0

        input.forEach{inputString ->
            val measurement = inputString.toInt()
            if (measurement > previous) {
                increaseCount++
            }

            previous = measurement
        }

        return increaseCount
    }

    fun part2(input: List<String>): Int {
        var previous = input[0].toInt() + input[1].toInt() + input[2].toInt()
        var increaseCount = 0

        input.forEachIndexed { index, _ ->
            if (index >= input.size - 2) {
                return@forEachIndexed
            }

            var current = 0

            (index .. index+2).forEach { i -> current += input[i].toInt() }

            if (current > previous) {
                increaseCount++
            }
            previous = current
        }

        return increaseCount
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("2021/Day01_test.txt")
    check(part1(testInput) == 7)
    check(part2(testInput) == 5)

    val input = readInput("2021/Day01.txt")
    println(part1(input))
    println(part2(input))
}
