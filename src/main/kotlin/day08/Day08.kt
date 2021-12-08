package day08

import readInput

// Key = length, Value = number
val knownNumberLengths = setOf(2, 3, 4, 7)

fun main() {
    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day08_test")
    check(part1(testInput) == 26)
    check(part2(testInput) == 61229)

    val input = readInput("Day08")
    println(part1(input))
    println(part2(input))
}

fun part1(input: List<String>): Int {
    return input.sumOf {
        it.split("|").last().split(" ")
                .count { knownNumberLengths.contains(it.length) }
    }
}

fun part2(input: List<String>): Int {
    return input.sumOf { line ->
        val (left, right) = line.split(" | ")
        val wires = left.split(' ').map { it.toSet() }.groupBy { it.size }

        // 1 is the only 2-segment number
        val one = wires.getValue(2).single()

        // 7 is the only 3-segment number
        val seven = wires.getValue(3).single()

        // 4 is the only 4-segment number
        val four = wires.getValue(4).single()

        // 2,3,5 all have 5 segments
        // 2 has 3 segments that don't match 4
        // 3 and 5 have another amount
        // So, the 5-segment input that has 3 differences from 4 must be 2
        val (twos, threeFive) = wires.getValue(5).partition { (it - four).size == 3 }
        val two = twos.single()

        // 3,5 both have 5 segments
        // 3 has 1 different segment compared to 2
        // 5 has 2 different segments compared to 2
        val (threes, fives) = threeFive.partition { (it - two).size == 1 }
        val three = threes.single()
        val five = fives.single()

        // 6,0,9 all have 6 segments
        // 6 has 5 left over after subtracting 1
        // 0 has 4 left over after subtracting 1
        // 9 has 4 left over after subtracting 1
        val (zeroNine, sixes) = wires.getValue(6).partition { (it - one).size == 4 }
        val six = sixes.single()

        // zero has 2 left after subtracting 3
        // nine has 1 left after subtracting 3
        val (zeroes, nines) = zeroNine.partition { (it - three).size == 2 }
        val zero = zeroes.single()
        val nine = nines.single()

        // 8 is the only 7-segment number
        val eight = wires.getValue(7).single()

        val mapping = arrayOf(zero, one, two, three, four, five, six, seven, eight, nine)

        right.split(' ').fold(0) { acc: Int, s ->
            10 * acc + mapping.indexOf(s.toSet())
        }
    }
}