package y2023.day08

import readInput
import repeat
import kotlin.math.abs
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

val stepPattern = "(.{3}) = \\((.{3}), (.{3})\\)".toRegex()

@OptIn(ExperimentalTime::class)
fun main() {
    require(part1(readInput("2023/Day08_test1")) == 2L) { "Part 1 check failed" }
    require(part1(readInput("2023/Day08_test2")) == 6L) { "Part 1 check failed" }

    val part1Duration: Duration = measureTime {
        println(part1(readInput("2023/Day08")))
    }
    println("Part 1 time: ${part1Duration.toDouble(DurationUnit.MILLISECONDS)} ms")

    require(part2(readInput("2023/Day08_test3")) == 6L) { "Part 2 check failed" }
    val part2Duration: Duration = measureTime {
        println(part2(readInput("2023/Day08")))
    }

    println("Part 2 time: ${part2Duration.toDouble(DurationUnit.MILLISECONDS)} ms")
}

fun part1(input: List<String>): Long {
    val steps = parseDirections(input.first())
    val nodes: HashMap<String, Node> = hashMapOf()

    input.drop(2)
        .map { Node(it) }
        .forEach { node -> nodes[node.name] = node }

    var currentNode = nodes["AAA"]!!
    var stepCount = 0L

    steps.takeWhile { currentNode.name != "ZZZ" }.forEach { step ->
        stepCount++
        when (step) {
            'L' -> currentNode = nodes[currentNode.left]!!
            'R' -> currentNode = nodes[currentNode.right]!!
        }
    }

    return stepCount
}

fun part2(input: List<String>): Long {
    val steps = parseDirections(input.first())
    val nodes: HashMap<String, Node> = hashMapOf()

    input.drop(2)
        .map { Node(it) }
        .forEach { node -> nodes[node.name] = node }

    val paths = nodes.filter { it.key.endsWith('A') }
        .map { Path(nodes, it.value) }

    steps.takeWhile { !paths.all { path -> path.completedOnce } }.forEach { step ->
       paths.filter { !it.completedOnce }.forEach { path -> path.move(step) }
    }

    return paths.map { it.minSteps }
        .reduce { a, b -> calculateLeastCommonMultiple(a, b) }
}

fun calculateGreatestCommonDivisor(a: Long, b: Long): Long {
    if (b == 0L) return a
    return calculateGreatestCommonDivisor(b, a % b)
}

fun calculateLeastCommonMultiple(a: Long, b: Long): Long {
    return (abs(a * b)) / calculateGreatestCommonDivisor(a, b)
}

fun parseDirections(input: String): Sequence<Char> {
    val inputSteps = input.toCharArray()
    val finiteStepsSequence = inputSteps.asSequence()
    return finiteStepsSequence.repeat()
}


class Node(input: String) {
    val name: String
    val left: String
    val right: String

    init {
        val match = stepPattern.matchEntire(input) ?: throw IllegalArgumentException("Invalid input: $input")
        name = match.groupValues[1]
        left = match.groupValues[2]
        right = match.groupValues[3]
    }

    override fun toString(): String {
        return "$name = ($left, $right)"
    }
}

class Path(private val nodes: Map<String, Node>, private var currentNode: Node) {
    private var steps = 0L
    var minSteps = Long.MAX_VALUE
    var completedOnce = false

    private fun determineMinSteps(){
        val complete = currentNode.name.endsWith('Z')

        if (complete) {
            minSteps = minSteps.coerceAtMost(steps)
            completedOnce = true
        }
    }

    fun move(direction: Char) {
        when (direction) {
            'L' -> currentNode = nodes[currentNode.left]!!
            'R' -> currentNode = nodes[currentNode.right]!!
        }
        steps++
        determineMinSteps()
    }

    override fun toString(): String {
        return "Path(node = ${currentNode.name}, steps=$steps, minSteps=$minSteps, completedOnce=$completedOnce)"
    }
}