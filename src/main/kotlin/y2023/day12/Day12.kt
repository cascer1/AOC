package y2023.day12

import readInput
import java.util.Objects
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
fun main() {
    val inputFile = "2023/Day12"
    val testInput = readInput("${inputFile}_test")
    require(calculateTotalPermutations(testInput) == 21L) { "Part 1 check failed" }

    val input = readInput(inputFile)
    val part1Duration: Duration = measureTime {
        println(calculateTotalPermutations(input))
    }
    println("Part 1 time: ${part1Duration.toDouble(DurationUnit.MILLISECONDS)} ms")

    require(calculateTotalPermutations(testInput, 5) == 525152L) { "Part 2 check failed" }
    val part2Duration: Duration = measureTime {
        println(calculateTotalPermutations(input, 5))
    }

    println("Part 2 time: ${part2Duration.toDouble(DurationUnit.MILLISECONDS)} ms")
}

private fun calculateTotalPermutations(input: List<String>, fold: Int = 1): Long {
    val cache = HashMap<Input, Long>()
    return input.map { Input(it) }
            .onEach { it.unfold(fold) }
            .parallelStream()
            .mapToLong { countPermutations(it, cache) }
            .sum()
}

private fun countPermutations(condition: String, groups: List<Int>, cache: HashMap<Input, Long>): Long =
        countPermutations(Input(condition, groups), cache)

private fun countPermutationOptions(condition: String, groups: List<Int>, cache: HashMap<Input, Long>): Long {
    return countPermutations("." + condition.substring(1), groups, cache) + countPermutations("#" + condition.substring(1), groups, cache)
}

private fun countPermutations(input: Input, cache: HashMap<Input, Long>): Long {
    cache[input]?.let { return it }

    val condition = input.condition
    val groups = input.brokenSpringGroups


    if (condition.isBlank()) {
        // If the condition is empty, we are done if there are no remaining groups.
        // Otherwise, the result is not valid.
        return if (groups.isEmpty()) 1 else 0
    }

    val firstChar = condition[0]
    val permutations: Long = when {
        firstChar == '.' -> countPermutations(condition.substring(1), groups, cache)
        firstChar == '?' -> countPermutationOptions(condition, groups, cache)
        groups.isNotEmpty() -> calculateNextPermutations(condition, groups, cache)
        else -> 0
    }

    cache[input] = permutations
    return permutations
}

private fun calculateNextPermutations(condition: String, groups: List<Int>, cache: HashMap<Input, Long>): Long {
    val damagedSpringCount = groups.first()
    if (damagedSpringCount > condition.length) {
        // We must add more damaged springs than there are remaining springs.
        // this means we made a mistake before and cannot continue.
        return 0
    }

    if (condition.substring(0, damagedSpringCount).any { it == '.' }) {
        // We were supposed to add $damagedSpringCount damaged springs, but there is a working
        // spring in the way.
        return 0
    }

    val newGroups = groups.subList(1, groups.size)

    if (damagedSpringCount == condition.length) {
        // The remaining length of the condition is equal to the nr of damaged springs
        // in the group, so we are done if there are no remaining groups.
        return (if (newGroups.isEmpty()) 1 else 0).toLong()
    }

    val nextSpring = condition[damagedSpringCount]

    if (nextSpring == '.') {
        // We have just added a group of damaged springs (#) and the next spring is
        // operational, this is valid, so skip over that operational spring.
        return countPermutations(condition.substring(damagedSpringCount + 1), newGroups, cache)
    }

    if (nextSpring == '?') {
        // We have just added a group of damaged springs (#), so the next spring can
        // only be operational (.).
        return countPermutations("." + condition.substring(damagedSpringCount + 1), newGroups, cache)
    }

    // We have just added a group of damaged springs (#), but the next character is
    // also a damaged spring, this is not valid.
    return 0
}

private class Input(var condition: String, var brokenSpringGroups: List<Int>) {
    constructor(input: String) : this(input.split(" ")[0], input.split(" ")[1].split(",").map { it.toInt() }.toMutableList())

    fun unfold(count: Int) {
        val originalCondition = condition
        val originalGroupString = brokenSpringGroups.joinToString(",")
        var groupString = originalGroupString
        repeat(count - 1) {
            condition = "$condition?$originalCondition"
            groupString = "$groupString,$originalGroupString"
        }
        brokenSpringGroups = groupString.split(",").map { it.toInt() }
    }

    override fun toString(): String {
        return "$condition, $brokenSpringGroups"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Input) return false

        if (condition != other.condition) return false
        if (brokenSpringGroups != other.brokenSpringGroups) return false

        return true
    }

    override fun hashCode(): Int {
        return Objects.hash(condition, brokenSpringGroups)
    }

}