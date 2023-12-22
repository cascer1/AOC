package y2023

import Coordinate
import Direction
import kotlin.math.abs

class Day18(private val input: List<String>) {
    fun part1(): Long {
        val instructions = parseInstructions(input)
        return calculateArea(instructions)
    }

    fun part2(): Long {
        val instructions = parseInstructions(input, true)
        return calculateArea(instructions)
    }

    private val instructionPattern = "([UDRL]) ([0-9]+) \\(#([0-9a-f]{5})([0-3])\\)".toRegex()

    private fun parseInstructions(input: List<String>, partTwo: Boolean = false): List<Instruction> {
        val result = ArrayList<Instruction>()

        input.forEach { instruction ->
            var (direction, distance, partTwoDistance, partTwoDirection) = instructionPattern.matchEntire(instruction)!!.destructured

            if (partTwo) {
                distance = partTwoDistance.toInt(16).toString()
                direction = when (partTwoDirection) {
                    "0" -> "R"
                    "1" -> "D"
                    "2" -> "L"
                    "3" -> "U"
                    else -> error("Unexpected partTwoDirection")
                }
            }
            val parsedDirection = when (direction) {
                "U" -> Direction.UP
                "D" -> Direction.DOWN
                "L" -> Direction.LEFT
                "R" -> Direction.RIGHT
                else -> error("Unexpected direction")
            }

            result.add(Instruction(parsedDirection, distance.toInt()))
        }

        return result
    }

    private fun followInstructions(instructions: List<Instruction>): List<Coordinate> {
        val result = ArrayList<Coordinate>()
        var position = Coordinate(0, 0)
        result.add(position)

        instructions.forEach { instruction ->
            position = move(instruction, position)
            result.add(position)
        }

        return result
    }

    /**
     * Calculates the area of the polygon defined by the given instructions.
     * This uses the shoelace formula.
     *
     * @see <a href="https://en.wikipedia.org/wiki/Shoelace_formula">Shoelace formula</a>
     */
    private fun calculateArea(instructions: List<Instruction>): Long {
        var area = 0UL
        var position = Coordinate(0, 0)
        var previousPosition = position
        instructions.forEach { instruction ->
            position = move(instruction, position)
            area += previousPosition.x.toULong() * position.y.toULong() - position.x.toULong() * previousPosition.y.toULong()
            previousPosition = position
        }
        val finalArea = abs((area / 2UL).toLong())

        return finalArea + (instructions.sumOf { it.distance.toLong() } / 2) + 1
    }

    private fun move(instruction: Instruction, position: Coordinate): Coordinate {
        val newPosition = when (instruction.direction) {
            Direction.UP -> position.copy(y = position.y - instruction.distance)
            Direction.DOWN -> position.copy(y = position.y + instruction.distance)
            Direction.LEFT -> position.copy(x = position.x - instruction.distance)
            Direction.RIGHT -> position.copy(x = position.x + instruction.distance)
        }

        return newPosition
    }

    private data class Instruction(val direction: Direction, val distance: Int)
}