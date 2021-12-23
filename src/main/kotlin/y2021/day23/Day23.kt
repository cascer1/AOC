@file:OptIn(ExperimentalTime::class)

package y2021.day23

import readInput
import java.util.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

fun main() {
    val testInput = readInput("Day23_test")
    check(part1(testInput) == 12521)
//    check(part2() == 2)

    val input = readInput("Day23")
    val part1Duration: Duration = measureTime {
        println(part1(input))
    }
//    val part2Duration: Duration = measureTime {
//        println(part2())
//    }

    println("Part 1 time: ${part1Duration.toDouble(DurationUnit.MILLISECONDS)} ms")
//    println("Part 2 time: ${part2Duration.toDouble(DurationUnit.MILLISECONDS)} ms")
}

fun part1(input: List<String>): Int {
    val initialState = parseInput(input)

    val queue = PriorityQueue<State>()
    queue.add(initialState)

    while(queue.isNotEmpty()) {
        val thisState = queue.remove()
        queue.addAll(thisState.potentialStates())
    }



//    var currentStates: List<State>
//    var newStates = listOf(initialState)
//    var iteration = 0
//    do {
//        println(iteration++)
//        println(newStates.size)
//        currentStates = newStates
//        newStates = currentStates.flatMap { it.potentialStates() }
//    } while (newStates.any { !it.complete })
//
//    return newStates.minOf { it.score }
}

fun part2(): Int {
    return 2
}

fun parseInput(input: List<String>): State {
    val relevantInput = input.drop(2).dropLast(1)
    val hallwayCells = setOf(
            Cell(0, 0, null, null),
            Cell(1, 0, null, null),
            Cell(3, 0, null, null),
            Cell(5, 0, null, null),
            Cell(7, 0, null, null),
            Cell(9, 0, null, null),
            Cell(10, 0, null, null)
    )

    val occupants = relevantInput.map { it.split("#").filter { room -> room.isNotBlank() } }

    val roomCells = occupants.flatMapIndexed { index, row ->
        val y = index + 1
        row.mapIndexed { colIndex, type ->
            val x = colIndex * 2 + 2
            val neighbors = mutableSetOf(Pair(x, y + 1))
            if (y == 1) {
                neighbors.addAll(setOf(Pair(x - 1, 0), Pair(x + 1, 0)))
            }
            Cell(x, y, Amphipod(type), AmphipodType.values()[colIndex])
        }
    }

    val allCells = hallwayCells + roomCells

    return State(allCells)
}

data class Amphipod(val type: AmphipodType, var steps: Int = 0) {
    val totalCost: Int
        get() = steps * type.movementCost

    constructor(input: String) : this(AmphipodType.valueOf(input))

    fun move(xFrom: Int, yFrom: Int, xTo: Int, yTo: Int) {
        val distance = abs(xFrom - xTo) + abs(yFrom - yTo)
        steps += distance
    }

    fun move(steps: Int) {
        this.steps += steps
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Amphipod

        return (this.type == other.type && this.steps == other.steps)
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + steps
        return result
    }
}

data class Cell(val x: Int, val y: Int, var occupant: Amphipod?, var type: AmphipodType?) {
    fun deepCopy(): Cell {
        return Cell(x, y, occupant?.copy(), type)
    }

    val occupied: Boolean
        get() = occupant != null

    val room: Boolean
        get() = type != null

    fun possibleMoves(cells: Set<Cell>): List<Pair<Pair<Int, Int>, Pair<Int, Int>>> {
        if (occupant == null) {
            return listOf()
        }

        if (isComplete(cells)) {
            return listOf()
        }

        val freeCells = cells.filterNot { it.occupied }

        val potentialTargets = (if (isCompletable(cells)) {
            // Must move down if in own completable room
            listOf(freeCells.filter { it.x == x && it.y > y }.maxByOrNull { it.y }!!)
        } else {
            if (y == 0) {
                // Must move down from hallway
                listOfNotNull(freeCells.filter { it.y > 0 }.filter { it.type!! == occupant!!.type }.maxByOrNull { it.y })
            } else {
                // Must move up from room
                freeCells.filter { it.y == 0 }
            }
        }).filter { isFreePath(x, y, it.x, it.y, cells) }
                .map { Pair(Pair(x, y), Pair(it.x, it.y)) }

        return potentialTargets
    }

    fun isFreePath(xFrom: Int, yFrom: Int, xTo: Int, yTo: Int, cells: Set<Cell>): Boolean {
        var horizontalFree = false
        var verticalFree = false

        val verticalSteps = (min(yFrom, yTo) .. max(yFrom, yTo))
        val horizontalSteps = (min(xFrom, xTo) .. max(xFrom, xTo))

        if (yFrom != 0) {
            // start by going up
            verticalFree = cells.filter { it.x == xFrom }
                    .filterNot { it.y == yFrom } // the cell we're leaving is always occupied
                    .filter { it.y in verticalSteps }
                    .none { it.occupied }

            horizontalFree = cells.filter { it.y == 0 }
                    .filter { it.x in (horizontalSteps) }
                    .none { it.occupied }
        } else {
            // Start by going horizontal
            horizontalFree = cells.filter { it.y == 0 }
                    .filterNot { it.x == xFrom }
                    .filter { it.x in (horizontalSteps) }
                    .none { it.occupied }

            verticalFree = cells.filter { it.x == xTo }
                    .filter { it.y in (verticalSteps) }
                    .none { it.occupied }
        }

        return horizontalFree && verticalFree
    }

    fun isComplete(cells: Set<Cell>): Boolean {
        if (type == null || occupant == null) {
            return false
        }

        if (occupant!!.type != type) {
            return false
        }

        val lowerNeighbor = cells.firstOrNull { it.x == x && it.y == y + 1 }

        if (lowerNeighbor != null) {
            return lowerNeighbor.isComplete(cells)
        }

        return true
    }

    fun isCompletable(cells: Set<Cell>): Boolean {
        if (type == null || occupant == null) {
            return false
        }

        if (occupant!!.type != type) {
            return false
        }

        val lowerNeighbors = cells.filter { it.x == x && it.y > y }

        return lowerNeighbors.all { !it.occupied || it.occupant!!.type == it.type!! }
    }

    fun canEnter(type: AmphipodType, cells: Set<Cell>): Boolean {
        // Hallways can accomodate all types
        if (this.type == null) {
            return true
        }

        // Rooms may only be entered if they contain only the correct type
        if (!roomFillable(cells)) {
            return false
        }

        return type == this.type!!
    }

    // Room can be filled if all its cells are either empty or occupied by the correct type
    private fun roomFillable(cells: Set<Cell>): Boolean {
        return cells.filter { it.x == x && it.y > 0 }
                .none { it.type!! != it.occupant?.type }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Cell

        if (x != other.x) return false
        if (y != other.y) return false
        if (occupant != other.occupant) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        result = 31 * result + (occupant?.hashCode() ?: 0)
        return result
    }


}

data class State(val cells: Set<Cell>): Comparable<State> {
    override fun compareTo(other: State): Int {
        return this.score.compareTo(other.score)
    }

    fun deepCopy(): State {
        return State(cells.map { it.deepCopy() }.toSet())
    }

    val complete: Boolean
        get() {
            return cells.filter { it.type != null }
                    .all { it.occupied && it.occupant!!.type == it.type }
        }

    val score: Int
        get() {
            return cells.filter { it.occupied }.sumOf { it.occupant!!.totalCost }
        }

    fun move(from: Pair<Int, Int>, to: Pair<Int, Int>): State {
        val occupant = getOccupant(from.first, from.second) ?: return this
        setOccupant(to.first, to.second, occupant)
        setOccupant(from.first, from.second, null)
        occupant.move(from.first, from.second, to.first, to.second)
        return this
    }

    fun setOccupant(x: Int, y: Int, occupant: Amphipod?) {
        cells.first { it.x == x && it.y == y }.occupant = occupant
    }

    fun getOccupant(x: Int, y: Int): Amphipod? {
        return cells.first { it.x == x && it.y == y }.occupant
    }

    fun bestScore(): Int {
        if (complete) {
            return score
        }

        return potentialStates().minOf { it.bestScore() }
    }

    fun potentialStates(): List<State> {
        if (complete) {
            return listOf(this)
        }

        val possibleMoves = cells.flatMap { it.possibleMoves(cells) }

        if (possibleMoves.isEmpty()) {
            return listOf(this)
        }

        val newStates = possibleMoves.map {
            val from = it.first
            val to = it.second
            val newState = this.deepCopy()
            newState.move(from, to)
        }.toList()

        if (newStates.any { it.complete }) {
            return newStates.filter { it.complete }.toList()
        }

        return newStates
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as State

        if (cells != other.cells) return false

        return true
    }

    override fun hashCode(): Int {
        return cells.hashCode()
    }
}

enum class AmphipodType(val movementCost: Int) {
    A(1), B(10), C(100), D(1000)
}
