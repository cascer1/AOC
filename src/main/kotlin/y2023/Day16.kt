package y2023

import Coordinate
import Direction

class Day16(val input: List<String>) {

    fun part1(): Int {
        val grid = parseInput(input)
        val simulated = simulateBeams(grid, BeamHead(Coordinate(-1, 0), Direction.RIGHT))
        return simulated.values.count { it.isVisited() }
    }

    fun part2(): Int {
        val grid = parseInput(input)
        val minX = grid.keys.minOf { it.x }
        val maxX = grid.keys.maxOf { it.x }
        val minY = grid.keys.minOf { it.y }
        val maxY = grid.keys.maxOf { it.y }

        val startPositions = HashSet<BeamHead>()

        (minX..maxX).forEach { x ->
            startPositions.add(BeamHead(Coordinate(x, minY - 1), Direction.DOWN))
            startPositions.add(BeamHead(Coordinate(x, maxY + 1), Direction.UP))
        }

        (minY..maxY).forEach { y ->
            startPositions.add(BeamHead(Coordinate(minX - 1, y), Direction.RIGHT))
            startPositions.add(BeamHead(Coordinate(maxX + 1, y), Direction.LEFT))
        }

        return startPositions.map { simulateBeams(HashMap(grid), it) }
                .maxOf { grid -> grid.values.count { it.isVisited() } }

    }

    private fun simulateBeams(grid: HashMap<Coordinate, GridPosition>, startPoint: BeamHead): HashMap<Coordinate, GridPosition> {
        val thisGrid = HashMap<Coordinate, GridPosition>()

        grid.forEach { (coordinate, position) ->
            thisGrid[coordinate] = position.copy()
        }

        val beams = ArrayDeque<BeamHead>()
        beams.add(startPoint)

        while (beams.isNotEmpty()) {
            val beam = beams.removeFirst()
            val nextPosition = beam.nextPosition()

            val nextBlock = thisGrid[nextPosition] ?: continue
            val newDirections = nextBlock.determineOutputDirection(beam.direction)

            newDirections.forEach { direction ->
                beams.add(BeamHead(nextPosition, direction))
            }
        }

        return thisGrid
    }

    private fun parseInput(input: List<String>): HashMap<Coordinate, GridPosition> {
        val grid = HashMap<Coordinate, GridPosition>()

        input.forEachIndexed { y, row ->
            row.forEachIndexed { x, char ->
                grid[Coordinate(x, y)] = GridPosition(char)
            }
        }

        return grid
    }

    private data class GridPosition(val character: Char) {
        var enteredFromTop = false
        var enteredFromBottom = false
        var enteredFromRight = false
        var enteredFromLeft = false

        fun isVisited(): Boolean {
            return enteredFromTop || enteredFromBottom || enteredFromRight || enteredFromLeft
        }

        /**
         * Determine the direction in which the beam will exit this block
         *
         * @param inputDirection The direction in which the beam is travelling when entering the block
         * @return The direction(s) in which the beam will exit the block
         */
        fun determineOutputDirection(inputDirection: Direction): Set<Direction> {
            when (inputDirection) {
                Direction.UP -> {
                    if (enteredFromBottom) {
                        return emptySet()
                    }
                    enteredFromBottom = true
                }

                Direction.DOWN -> {
                    if (enteredFromTop) {
                        return emptySet()
                    }
                    enteredFromTop = true
                }

                Direction.LEFT -> {
                    if (enteredFromRight) {
                        return emptySet()
                    }
                    enteredFromRight = true
                }

                Direction.RIGHT -> {
                    if (enteredFromLeft) {
                        return emptySet()
                    }
                    enteredFromLeft = true
                }
            }

            return decideOutputDirection(inputDirection)
        }

        private fun decideOutputDirection(inputDirection: Direction): Set<Direction> {
            if (character == '.') {
                return setOf(inputDirection)
            }

            if (character == '|') {
                if (inputDirection == Direction.DOWN || inputDirection == Direction.UP) {
                    return setOf(inputDirection)
                }

                return setOf(Direction.UP, Direction.DOWN)
            }

            if (character == '-') {
                if (inputDirection == Direction.LEFT || inputDirection == Direction.RIGHT) {
                    return setOf(inputDirection)
                }

                return setOf(Direction.LEFT, Direction.RIGHT)
            }

            if (character == '\\') {
                return when (inputDirection) {
                    Direction.LEFT -> setOf(Direction.UP)
                    Direction.RIGHT -> setOf(Direction.DOWN)
                    Direction.UP -> setOf(Direction.LEFT)
                    Direction.DOWN -> setOf(Direction.RIGHT)
                }
            }

            if (character == '/') {
                return when (inputDirection) {
                    Direction.LEFT -> setOf(Direction.DOWN)
                    Direction.RIGHT -> setOf(Direction.UP)
                    Direction.UP -> setOf(Direction.RIGHT)
                    Direction.DOWN -> setOf(Direction.LEFT)
                }
            }

            error("Unknown direction")
        }
    }

    data class BeamHead(val position: Coordinate, val direction: Direction) {
        fun nextPosition(): Coordinate {
            return when (direction) {
                Direction.UP -> Coordinate(position.x, position.y - 1)
                Direction.DOWN -> Coordinate(position.x, position.y + 1)
                Direction.LEFT -> Coordinate(position.x - 1, position.y)
                Direction.RIGHT -> Coordinate(position.x + 1, position.y)
            }
        }
    }
}