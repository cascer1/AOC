@file:OptIn(ExperimentalTime::class)

package y2021.day18

import readInput
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

fun main() {
    val testInput = readInput("Day18_test")
    check(part1(testInput) == 4140)
    check(part2(testInput) == 3993)


    val input = readInput("Day18")
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
    val numbers = input.map { ArrayDeque(it.toList()) }.map {
        it.removeFirst()
        parseNumber(it)
    }

    val result = numbers.reduce { acc, snailNumber -> acc + snailNumber }

    return result.magnitude
}

fun part2(input: List<String>): Int {
    return input.indices.flatMap { firstIndex ->
        input.indices.map { secondIndex ->
            firstIndex to secondIndex
        }
    }
        .filter { it.first != it.second }
        .maxOf { (firstIndex, secondIndex) -> (parseNumber(input[firstIndex]) + parseNumber(input[secondIndex])).magnitude }
}

fun parseNumber(input: String): SnailNumber {
    val deque = ArrayDeque(input.toList())
    deque.removeFirst()
    return parseNumber(deque)
}

fun parseNumber(input: ArrayDeque<Char>, parent: SnailNumber? = null): SnailNumber {
    val result = SnailNumber()
    result.parent = parent

    val firstChar = input.removeFirst()

    if (firstChar == '[') {
        result.left = parseNumber(input, result)
    } else {
        val left = SnailNumber(firstChar.digitToInt())
        left.parent = result
        result.left = left
    }

    val nextChar = input.removeFirst()

    if (nextChar != ',') {
        throw IllegalStateException("Expected , as next char")
    }

    val nextNumber = input.removeFirst()

    if (nextNumber == '[') {
        result.right = parseNumber(input, result)
    } else {
        val right = SnailNumber(nextNumber.digitToInt())
        right.parent = result
        result.right = right
    }

    input.removeFirst()

    return result
}

class SnailNumber() {
    var left: SnailNumber? = null
    var right: SnailNumber? = null
    var value: Int? = null
    var parent: SnailNumber? = null

    constructor(left: SnailNumber, right: SnailNumber) : this() {
        this.left = left
        this.right = right
    }

    constructor(value: Int) : this() {
        this.value = value
    }

    val magnitude: Int get() = value ?: (3 * left!!.magnitude + 2 * right!!.magnitude)

    operator fun plus(other: SnailNumber): SnailNumber {
        return SnailNumber(this, other).apply {
            left?.parent = this
            right?.parent = this
            reduce()
        }
    }

    fun reduce() {
        findNumberToExplode()?.apply {
            val (l, r) = this.left?.value!! to this.right?.value!!
            left = null
            right = null
            findNumberOnLeft()?.let { it.value = it.value!! + l }
            findNumberOnRight()?.let { it.value = it.value!! + r }
            value = 0
            this@SnailNumber.reduce()
        }
        findNumberToSplit()?.apply {
            val newLeft = value!! / 2
            val newRight = value!! - newLeft

            left = SnailNumber(newLeft)
            right = SnailNumber(newRight)
            left!!.parent = this
            right!!.parent = this
            value = null
            this@SnailNumber.reduce()
        }
    }

    fun findNumberToExplode(depth: Int = 0): SnailNumber? {
        if (value == null && depth >= 4) {
            return this
        }

        return left?.findNumberToExplode(depth + 1) ?: right?.findNumberToExplode(depth + 1)
    }

    fun findNumberToSplit(): SnailNumber? {
        return if ((value ?: 0) > 9) this else left?.findNumberToSplit() ?: right?.findNumberToSplit()
    }

    fun findNumberOnLeft(): SnailNumber? {
        if (value != null) return this
        if (this == parent?.left) return parent!!.findNumberOnLeft()
        if (this == parent?.right) return parent!!.left?.findRightMostNumber()
        return null
    }

    fun findNumberOnRight(): SnailNumber? {
        if (value != null) return this
        if (this == parent?.left) return parent!!.right?.findLeftMostNumber()
        if (this == parent?.right) return parent!!.findNumberOnRight()
        return null
    }

    fun findLeftMostNumber(): SnailNumber = if (value != null) this else left!!.findLeftMostNumber()
    fun findRightMostNumber(): SnailNumber = if (value != null) this else right!!.findRightMostNumber()

    override fun toString(): String {
        if (this.value != null) {
            return this.value.toString()
        }

        val result = StringBuilder().append('[')

        left?.let { result.append(it) }

        result.append(',')

        right?.let { result.append(it) }

        result.append(']')
        return result.toString()
    }
}