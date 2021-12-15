package y2021.day14

import readInput

var instructions: HashMap<String, Rule> = HashMap()
var state: HashMap<String, Long> = HashMap()
var startCharacter: Char = 'a'

fun HashMap<String, Long>.addCount(sequence: String, amount: Long) {
    val old = this.getOrDefault(sequence, 0L)
    this[sequence] = old + amount
}

fun parseInput(input: List<String>) {
    instructions.clear()
    state.clear()

    state = HashMap(input.first().windowed(2).groupingBy { it }.eachCount().mapValues { (_, value) -> value.toLong() })
    startCharacter = input.first()[0]

    input.drop(2).forEach {
        val (from, to) = it.split(" -> ")
        instructions[from] = Rule(from, to)
    }
}

fun main() {
    val testInput = readInput("Day14_test")
    parseInput(testInput)
    check(doTheThing(10) == 1588L)
    check(doTheThing(30) == 2188189693529L)

    val input = readInput("Day14")
    parseInput(input)
    println(doTheThing(10))
    println(doTheThing(30))
}


fun doTheThing(count: Int): Long {
    repeat(count) {
        val newState: HashMap<String, Long> = HashMap()
        state.forEach { (sequence, amount) ->
            val (new1, new2) = instructions[sequence]!!.replacements
            newState.addCount(new1, amount)
            newState.addCount(new2, amount)
        }
        state = newState
    }

    val characterCounts: HashMap<Char, Long> = HashMap()
    characterCounts[startCharacter] = 1

    state.forEach { (pair, count) ->
        val letter = pair[1]
        characterCounts[letter] = (characterCounts[letter] ?: 0) + count
    }

    val finalCount = characterCounts.toList().sortedBy { (_, count) -> count }

    return finalCount.last().second - finalCount.first().second
}

data class Rule(val from: String, val to: String) {
    val replacements: List<String>
        get() = listOf("${from[0]}$to", "$to${from[1]}")
}