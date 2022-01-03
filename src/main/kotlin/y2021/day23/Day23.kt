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

var bestCost = Long.MAX_VALUE

fun main() {
    check(findBestSteps(readInput("2021/Day23_test_a")) == 12521L)
    println("part 1 test succeeded!")
    check(findBestSteps(readInput("2021/Day23_test_b")) == 44169L)
    println("part 2 test succeeded!")

    var input = readInput("2021/Day23_a")
    val part1Duration: Duration = measureTime {
        println(findBestSteps(input))
    }
    input = readInput("2021/Day23_b")
    val part2Duration: Duration = measureTime {
        println(findBestSteps(input))
    }

    println("Part 1 time: ${part1Duration.toDouble(DurationUnit.MILLISECONDS)} ms")
    println("Part 2 time: ${part2Duration.toDouble(DurationUnit.MILLISECONDS)} ms")
}

fun findBestSteps(input: List<String>): Long {
    bestCost = Long.MAX_VALUE

    // Keep track of the lowest cost of reaching a given state
    // If we then encounter that state again at a higher cost, we can discard it
    val alreadyProcessed: HashMap<String, Long> = hashMapOf()

    val queue = PriorityQueue<State>(compareBy { it.cost })
    queue.add(parseInput(input))

    while (queue.isNotEmpty()) {
        val thisState = queue.poll()
        if (thisState.cost >= bestCost) {
            break
        }

        thisState.potentialStates().forEach { newState ->
            if (newState.complete) {
                bestCost = min(bestCost, newState.cost)
            } else {
                val representation = newState.representation()
                if (newState.cost < alreadyProcessed.getOrDefault(representation, Long.MAX_VALUE)) {
                    alreadyProcessed[representation] = newState.cost
                    queue.offer(newState)
                }
            }
        }
    }

    return bestCost
}

fun parseInput(input: List<String>): State {
    val relevantInput = input.drop(2).dropLast(1)
    val hallwayCells = listOf(
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
            Cell(x, y, Amphipod.valueOf(type), Amphipod.values()[colIndex])
        }
    }

    val allCells = hallwayCells + roomCells

    return State(allCells)
}

/**
 * Checks whether there is a clear route between the two cells
 */
fun isFreePath(xFrom: Int, yFrom: Int, xTo: Int, yTo: Int, cells: List<Cell>): Boolean {
    val horizontalFree: Boolean
    val verticalFree: Boolean

    val verticalSteps = (min(yFrom, yTo)..max(yFrom, yTo))
    val horizontalSteps = (min(xFrom, xTo)..max(xFrom, xTo))

    if (yFrom != 0) {
        // start by going up
        verticalFree = cells.filter { it.x == xFrom }
            .filterNot { it.y == yFrom } // the cell we're leaving is always occupied
            .filter { it.y in verticalSteps }
            .all { it.empty }

        horizontalFree = cells.filter { it.y == 0 }
            .filter { it.x in (horizontalSteps) }
            .all { it.empty }
    } else {
        // Start by going horizontal
        horizontalFree = cells.filter { it.y == 0 }
            .filterNot { it.x == xFrom }
            .filter { it.x in (horizontalSteps) }
            .all { it.empty }

        verticalFree = cells.filter { it.x == xTo }
            .filter { it.y in (verticalSteps) }
            .all { it.empty }
    }

    return horizontalFree && verticalFree
}

data class Cell(val x: Int, val y: Int, var occupant: Amphipod?, var type: Amphipod?) {
    val occupied: Boolean
        get() = occupant != null

    val empty: Boolean
        get() = occupant == null

    fun possibleMoves(cells: List<Cell>): List<Pair<Pair<Int, Int>, Pair<Int, Int>>> {
        if (occupant == null) {
            // Can't move someone out of an empty room
            return listOf()
        }

        if (isComplete(cells)) {
            // The occupant is right where they must be
            return listOf()
        }

        val freeCells = cells.filter { it.empty }

        val potentialTargets = (
                if (y == 0) {
                    // Must move out of hallway
                    val targetX = occupant!!.ordinal * 2 + 2
                    val roomCells = cells.filter { it.x == targetX }
                    val roomAvailable = roomCells.all { it.empty || it.occupant!! == it.type!! }

                    if (roomAvailable) {
                        listOfNotNull(roomCells.filter { it.empty }.maxByOrNull { it.y })
                    } else {
                        listOf()
                    }
                } else {
                    // Must move up from room
                    freeCells.filter { it.y == 0 }
                }
                ).filter { isFreePath(x, y, it.x, it.y, cells) }
            .map { Pair(Pair(x, y), Pair(it.x, it.y)) }

        return potentialTargets
    }

    /**
     * Returns true if this cell and all those below it are of the correct type
     */
    private fun isComplete(cells: List<Cell>): Boolean {
        if (type == null || occupant == null) {
            return false
        }

        if (occupant!! != type) {
            return false
        }

        // if there is a lower neighbor, return its completion status. Else, return true
        return cells.firstOrNull { it.x == x && it.y == y + 1 }?.isComplete(cells) ?: true
    }
}

data class State(val cells: List<Cell>, var cost: Long = 0L) : Comparable<State> {
    private var representation: String? = null

    override fun compareTo(other: State): Int {
        return this.cost.compareTo(other.cost)
    }

    private fun deepCopy(): State {
        return State(cells.map { it.copy() }, cost)
    }

    val complete: Boolean
        get() {
            return cells.filter { it.type != null }
                .all { it.occupied && it.occupant!! == it.type }
        }

    private fun move(from: Pair<Int, Int>, to: Pair<Int, Int>): State {
        val occupant = getOccupant(from.first, from.second)
        setOccupant(to.first, to.second, occupant)
        setOccupant(from.first, from.second, null)
        cost += occupant.move(from.first, from.second, to.first, to.second)
        return this
    }

    private fun setOccupant(x: Int, y: Int, occupant: Amphipod?) {
        cells.first { it.x == x && it.y == y }.occupant = occupant
    }

    private fun getOccupant(x: Int, y: Int): Amphipod {
        return cells.first { it.x == x && it.y == y }.occupant!!
    }

    fun potentialStates(): List<State> {
        return cells.flatMap { it.possibleMoves(cells) }
            .map {
                val newState = this.deepCopy()
                newState.move(it.first, it.second)
            }
    }

    fun representation(): String {
        return representation ?: cells.map { it.occupant?.name ?: "." }.reduce { acc, s -> acc + s }
    }
}

enum class Amphipod(private val movementCost: Int) {
    A(1), B(10), C(100), D(1000);

    fun move(xFrom: Int, yFrom: Int, xTo: Int, yTo: Int): Int {
        val distance = abs(xFrom - xTo) + abs(yFrom - yTo)
        return distance * movementCost
    }
}
