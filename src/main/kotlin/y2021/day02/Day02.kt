package y2021.day02

import readInput

private fun main() {
    // test if implementation meets criteria from the description, like:
    val testInput = readInput("2021/Day02_test")
    check(part1(testInput) == 150)
    check(part2(testInput) == 900)

    val input = readInput("2021/Day02")
    println(part1(input))
    println(part2(input))
}

private fun getOperation(input: String): Pair<SubmarineMovements, Int> {
    val parts = input.split(" ")
    if (parts.size != 2) {
        throw IllegalArgumentException()
    }

    val operation = SubmarineMovements.valueOf(parts[0].uppercase())
    val amount = parts[1].toInt()

    return Pair(operation, amount)
}

private fun part1(input: List<String>): Int {
    var horizontal = 0
    var depth = 0

    input.forEach{inputString ->
        val instruction = getOperation(inputString)

        when(instruction.first) {
            SubmarineMovements.UP -> depth -= instruction.second
            SubmarineMovements.DOWN -> depth += instruction.second
            SubmarineMovements.FORWARD -> horizontal += instruction.second
        }
    }

    return horizontal * depth
}

private fun part2(input: List<String>): Int {
    var horizontal = 0
    var depth = 0
    var aim = 0

    input.forEach{inputString ->
        val instruction = getOperation(inputString)

        when(instruction.first) {
            SubmarineMovements.UP -> aim -= instruction.second
            SubmarineMovements.DOWN -> aim += instruction.second
            SubmarineMovements.FORWARD -> {
                horizontal += instruction.second
                depth += instruction.second * aim
            }
        }
    }

    return horizontal * depth
}

private enum class SubmarineMovements {
    FORWARD,
    DOWN,
    UP
}