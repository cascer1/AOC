package y2022.day07

import readInput
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

var root = Node(name = "/")
var lastCommand = ""
var currentDirectory = root

@OptIn(ExperimentalTime::class)
fun main() {
    val inputFile = "2022/Day07"
    val testInput = readInput("${inputFile}_test")
    check(part1(testInput) == 95437)
    println("Part 1 check successful!")
    check(part2(testInput) == 24933642)
    println("Part 2 check successful!")

    val input = readInput(inputFile)
    val part1Duration: Duration = measureTime {
        println(part1(input))
    }
    val part2Duration: Duration = measureTime {
        println(part2(input))
    }

    println("Part 1 time: ${part1Duration.toDouble(DurationUnit.MILLISECONDS)} ms")
    println("Part 2 time: ${part2Duration.toDouble(DurationUnit.MILLISECONDS)} ms")
}

fun part1(input: List<String>): Int {
    parseInput(input)
    return getTotalSizeWithLimit(100_000, root);
}

fun part2(input: List<String>): Int {
    parseInput(input)

    val maxSize = 70_000_000
    val spaceRequired = 30_000_000
    val freeSpace = maxSize - root.totalSize
    val spaceToFree = spaceRequired - freeSpace

    return getDirectorySizes(root).filter { it >= spaceToFree }.minOf { it }
}

fun parseInput(input: List<String>) {
    root = Node(name = "/")
    currentDirectory = root

    input.forEach { line ->
        if (line.startsWith("$")) {
            parseCommand(line)
        } else {
            parseOutput(line)
        }
    }
}

fun parseCommand(line: String) {
    lastCommand = line.removePrefix("$ ")

    if (lastCommand.startsWith("cd ")) {
        changeDirectory(lastCommand.removePrefix("cd "))
    }
}

fun parseOutput(line: String) {
    if (line.startsWith("dir ")) {
        currentDirectory.children.add(Node(name = line.removePrefix("dir "), parent = currentDirectory))
    } else {
        val parts = line.split(" ")
        currentDirectory.children.add(Node(name = parts[1], parent = currentDirectory, size = parts[0].toInt()))
    }
}

fun changeDirectory(directoryName: String) {
    if (directoryName == "/") {
        currentDirectory = root
        return
    }

    if (directoryName == "..") {
        currentDirectory = currentDirectory.parent!!
        return
    }

    currentDirectory = currentDirectory.children.first { it.name == directoryName }
}

fun getTotalSizeWithLimit(limit: Int, directory: Node): Int {
    var returned = 0

    directory.childDirectories.forEach { child ->
        if (child.totalSize <= limit) {
            returned += child.totalSize
        }

        returned += getTotalSizeWithLimit(limit, child)
    }

    return returned
}

fun getDirectorySizes(directory: Node): List<Int> {
    val returned = ArrayList<Int>()

    returned.add(directory.totalSize)

    returned.addAll(directory.childDirectories.flatMap { getDirectorySizes(it) })
    return returned
}