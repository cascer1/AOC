package y2021.day04

import readInput

private fun List<BingoNumber>.isComplete(): Boolean = this.all { it.marked }

private var boards: List<BingoBoard> = emptyList()
private var numbers: List<Int> = emptyList()

private fun main() {
    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 4512)
    check(part2(testInput) == 1924)

    val input = readInput("Day04")
    println(part1(input))
    println(part2(input))
}

private fun parseInput(input: List<String>) {
    numbers = input[0].split(",").map { it.toInt() }
    boards = input
        .drop(1)
        .chunked(6)
        .map { boardLines ->
            boardLines.filter { singleBoardLine ->
                singleBoardLine.isNotBlank()
            }.map {
                it.trim().split(Regex("\\s+")).map { number ->
                    BingoNumber(number.toInt())
                }
            }
        }.map { BingoBoard(it) }
}

private fun part1(input: List<String>): Int {
    parseInput(input)

    numbers.forEach { number ->
        boards.forEach { board ->
            board.markNumber(number)

            if (board.complete) {
                return board.score * number
            }
        }
    }

    return 0
}

private fun part2(input: List<String>): Int {
    parseInput(input)

    val totalBoards = boards.size
    var completeBoards = 0

    numbers.forEach { number ->
        boards.filter { !it.complete }
            .forEach { board ->
                board.markNumber(number)

                if (board.complete) {
                    completeBoards++
                    if (completeBoards == totalBoards) {
                        return board.score * number
                    }
                }
            }
    }

    return 2
}

private data class BingoNumber(val number: Int, var marked: Boolean = false)

private class BingoBoard(inputLines: List<List<BingoNumber>>) {
    private val lines: ArrayList<List<BingoNumber>> = ArrayList()
    var complete: Boolean = false
        private set

    val score: Int
        get() = score()

    init {
        inputLines.forEach { lines.add(it) }
    }

    fun markNumber(number: Int) {
        lines.forEach mark@{ lines ->
            lines.forEach { line ->
                if (line.number == number) {
                    line.marked = true
                    return@mark
                }
            }
        }
        complete = isComplete()
    }

    private fun isComplete(): Boolean {
        if (lines.any { it.isComplete() }) {
            return true
        }

        lines[0].indices.forEach { index ->
            if (lines.all { line -> line[index].marked }) {
                return true
            }
        }

        return false
    }

    private fun score(): Int {
        var result = 0

        lines.forEach { line ->
            line.forEach { number ->
                if (!number.marked) {
                    result += number.number
                }
            }
        }

        return result
    }
}