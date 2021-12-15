package y2021.day13

import readInput
import kotlin.math.abs

var dots: HashSet<Dot> = HashSet()
var folds: ArrayList<Fold> = ArrayList()

fun HashSet<Dot>.getCharAt(x: Int, y: Int): Char {
    if (this.any { it.x == x && it.y == y }) {
        return '#'
    }
    return ' '
}

fun parseInput(input: List<String>) {
    dots = input
            .filter { it.matches(Regex("[0-9]+,[0-9]+")) }
            .map {
                val (x, y) = it.split(",")
                Dot(x.toInt(), y.toInt())
            }.toHashSet()

    val parsedFolds = input
            .filter { it.startsWith("fold along") }
            .map {
                val (axis, number) = it.split(" ").last().split("=")
                Fold(axis, number.toInt())
            }.toMutableList()

    folds = ArrayList(parsedFolds)
}

fun main() {
    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day13_test")
    parseInput(testInput)
    check(part1() == 17)

    val input = readInput("Day13")
    parseInput(input)
    println(part1())
    part2()
}

fun executeFold(fold: Fold) {
    val foldedDots = if (fold.vertical) {
        dots.filter { it.x > fold.number }.toSet()
    } else {
        dots.filter { it.y > fold.number }.toSet()
    }

    dots.removeAll(foldedDots)

    if (fold.vertical) {
        foldedDots.forEach {
            it.x = abs(it.x - fold.number * 2)
        }
    } else {
        foldedDots.forEach {
            it.y = abs(it.y - fold.number * 2)
        }
    }

    dots.addAll(foldedDots)
}


fun part1(): Int {
    executeFold(folds.removeFirst())

    return dots.size
}

fun part2() {
    folds.forEach { executeFold(it) }

    val maxX = dots.maxOf { it.x }
    val maxY = dots.maxOf { it.y }

    (0 .. maxY).forEach { y ->
        (0 .. maxX).forEach { x ->
            print(dots.getCharAt(x, y))
        }
        println()
    }
}

data class Dot(var x: Int, var y: Int)

data class Fold(val axis: String, val number: Int) {
    val vertical: Boolean
        get() = axis == "x"
}