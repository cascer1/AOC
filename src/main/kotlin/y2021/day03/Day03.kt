package y2021.day03

import readInput

private fun String.invert() = this.map { if (it == '0') '1' else '0' }.joinToString("")
private fun List<String>.charactersForColumn(index: Int): Map<Char, Int> = this.groupingBy { it[index] }.eachCount()
private var bitIndices: IntRange = (0..1)

private fun main() {
    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 198)
    check(part2(testInput) == 230)

    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val lineLength = input[0].length
    val oneCount = IntArray(lineLength)

    input.forEach { line ->
        line.forEachIndexed { index, number ->
            if (number == '1') {
                oneCount[index]++
            }
        }
    }

    var gammaString = ""

    (0 until lineLength).forEach { index ->
        gammaString += if (oneCount[index] > (input.size / 2)) {
            "1"
        } else {
            "0"
        }
    }

    val gamma = gammaString.toInt(2)
    val epsilon = gammaString.invert().toInt(2)

    return gamma * epsilon
}

private fun part2(input: List<String>): Int {
    bitIndices = input[0].indices
    val oxyGenRating = input.filterColumnsForCharacter { zeroes, ones ->
        if (zeroes > ones) '0' else '1'
    }

    val co2ScrubberRating = input.filterColumnsForCharacter { zeroes, ones ->
        if (zeroes > ones) '1' else '0'
    }

    return oxyGenRating.toInt(2) * co2ScrubberRating.toInt(2)
}

private fun List<String>.filterColumnsForCharacter(desiredCharacterByFrequency: (zeroes: Int, ones: Int) -> Char): String {
    var candidateList = this
    for (column in bitIndices) {
        val charFrequencyByColumn = candidateList.charactersForColumn(column)
        val zeroes = charFrequencyByColumn['0'] ?: 0
        val ones = charFrequencyByColumn['1'] ?: 0
        candidateList = candidateList.filter { it[column] == desiredCharacterByFrequency(zeroes, ones) }
        if (candidateList.size == 1) break
    }
    return candidateList.single()
}
