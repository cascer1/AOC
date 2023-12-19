package y2023.day15

import readInput
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

val OPERATION_REMOVE_PATTERN = "^([a-z]+)-$".toRegex()
val OPERATION_ADD_PATTERN = "(^[a-z]+)=([0-9]+)$".toRegex()

@OptIn(ExperimentalTime::class)
fun main() {
    val inputFile = "2023/Day15"
    val testInput = readInput("${inputFile}_test")
    require(part1(testInput) == 1320) { "Part 1 check failed" }

    val input = readInput(inputFile)
    val part1Duration: Duration = measureTime {
        println(part1(input))
    }
    println("Part 1 time: ${part1Duration.toDouble(DurationUnit.MILLISECONDS)} ms")

    require(part2(testInput) == 145) { "Part 2 check failed" }
    val part2Duration: Duration = measureTime {
        println(part2(input))
    }

    println("Part 2 time: ${part2Duration.toDouble(DurationUnit.MILLISECONDS)} ms")
}

private fun part1(input: List<String>): Int {
    return input.first().split(",")
            .sumOf { calculateHash(it) }
}

private fun part2(input: List<String>): Int {
    val instructions = input.first().split(",")
    val boxes = HashMap<Int, ArrayDeque<Lens>>()

    instructions.forEach { instruction ->
        executeInstruction(boxes, instruction)
    }

    return boxes.map { calculateBoxPower(it) }.sum()
}

private fun executeInstruction(boxes: HashMap<Int, ArrayDeque<Lens>>, instruction: String) {
    if (OPERATION_ADD_PATTERN.matches(instruction)) {
        addLens(boxes, instruction)
    } else if (OPERATION_REMOVE_PATTERN.matches(instruction)) {
        removeLens(boxes, instruction)
    }
}

private fun addLens(boxes: HashMap<Int, ArrayDeque<Lens>>, instruction: String) {
    // ab=3
    val label = instruction.split("=")[0]
    val boxNumber = calculateHash(label)
    val focalLength = instruction.split("=")[1].toInt()

    if (!boxes.containsKey(boxNumber)) {
        boxes[boxNumber] = ArrayDeque()
    }

    val lenses = boxes[boxNumber]!!
    val lens = Lens(label, focalLength)
    val existingIndex = lenses.indexOfFirst { it.label == label }

    if (existingIndex == -1) {
        lenses.addLast(lens)
    } else {
        lenses[existingIndex] = lens
    }
}

private fun removeLens(boxes: HashMap<Int, ArrayDeque<Lens>>, instruction: String) {
    // gh-
    val label = instruction.split("-")[0]
    val boxNumber = calculateHash(label)

    if (!boxes.containsKey(boxNumber)) {
        return
    }

    boxes[boxNumber]!!.removeIf { it.label == label }
}

private fun calculateBoxPower(box: Map.Entry<Int, ArrayDeque<Lens>>): Int {
    val lenses = box.value
    val boxNumber = box.key

    return lenses.mapIndexed { index, lens -> (boxNumber + 1) * (index + 1) * lens.focalLength }.sum()
}

private fun calculateHash(input: String): Int {
    var result = 0

    input.forEach { char ->
        result += char.code
        result *= 17
        result %= 256
    }

    return result
}

data class Lens(val label: String, val focalLength: Int)