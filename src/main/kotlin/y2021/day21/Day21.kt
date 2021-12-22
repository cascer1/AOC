@file:OptIn(ExperimentalTime::class)

package y2021.day21

import addCount
import readInput
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

private var playerOneStart = 0
private var playerTwoStart = 0
private var lastRoll = 0
private var rollCount = 0

private fun main() {
    val testInput = readInput("Day21_test")
    parseInput(testInput)
    check(part1() == 739785)
    check(part2() == 444356092776315L)

    val input = readInput("Day21")
    parseInput(input)
    val part1Duration: Duration = measureTime {
        println(part1())
    }
    val part2Duration: Duration = measureTime {
        println(part2())
    }

    println("Part 1 time: ${part1Duration.toDouble(DurationUnit.MILLISECONDS)} ms")
    println("Part 2 time: ${part2Duration.toDouble(DurationUnit.MILLISECONDS)} ms")
}

private fun part1(): Int {
    val game = Game(Player(1, playerOneStart, 0), Player(2, playerTwoStart, 0))

    do {
        game.roll(getDieRoll())
                .roll(getDieRoll())
                .roll(getDieRoll())
    } while (game.getWinner(1000) == null)

    return game.getLoser().score * game.totalRollCount
}

private fun part2(): Long {
    var states: HashMap<Game, Long> = hashMapOf()
    val startGame = Game(Player(1, playerOneStart, 0), Player(2, playerTwoStart, 0))
    states[startGame] = 1

    while (states.any { it.key.getWinner(21) == null }) {
        val newStates: HashMap<Game, Long> = hashMapOf()

        states.forEach { (game, count) ->
            if (game.getWinner(21) != null) {
                newStates.addCount(game, count)
            } else {
                val newGames = simulateRoll(game)
                newGames.forEach { newGame ->
                    newStates.addCount(newGame, count)
                }
            }
        }
        states = newStates
    }

    return states.map { entry -> Pair(entry.key.getWinner(21)!!.name, entry.value) }
            .groupBy { it.first }
            .mapValues { it.value.sumOf { list -> list.second } }
            .maxOf { it.value }
}

private fun parseInput(input: List<String>) {
    playerOneStart = input[0].removePrefix("Player 1 starting position: ").toInt()
    playerTwoStart = input[1].removePrefix("Player 2 starting position: ").toInt()
}

private fun getDieRoll(): Int {
    rollCount++
    return if (lastRoll < 100) {
        ++lastRoll
    } else {
        lastRoll = 1
        1
    }
}

private fun simulateRoll(game: Game): List<Game> {
    val games = listOf(game.deepCopy(), game.deepCopy(), game.deepCopy())

    games.forEachIndexed { index, thisGame -> thisGame.roll(index + 1) }

    return games
}

data class Player(var name: Int, var position: Int, var score: Int) {
    fun move(roll: Int) {
        position += roll
        while (position > 10) {
            position -= 10
        }
    }

    fun increaseScore() {
        score += position
    }
}

data class Game(var playerOne: Player, var playerTwo: Player) {
    private var rollingPlayer: Player = playerOne
    var rollCount = 0
    var totalRollCount = 0

    fun getWinner(winningScore: Int): Player? {
        if (playerOne.score >= winningScore) {
            return playerOne
        }

        if (playerTwo.score >= winningScore) {
            return playerTwo
        }

        return null
    }

    fun getLoser(): Player {
        return if (playerOne.score < playerTwo.score) {
            playerOne
        } else {
            playerTwo
        }
    }

    fun deepCopy(): Game {
        val result = Game(playerOne.copy(), playerTwo.copy())
        if (rollingPlayer == playerOne) {
            result.rollingPlayer = result.playerOne
        } else {
            result.rollingPlayer = result.playerTwo
        }
        result.rollCount = rollCount.toString().toInt()
        result.totalRollCount = totalRollCount.toString().toInt()
        return result
    }

    fun roll(roll: Int): Game {
        advance(rollingPlayer, roll)
        return this
    }

    private fun advance(player: Player, roll: Int) {
        player.move(roll)
        rollCount++
        totalRollCount++
        if (rollCount == 3) {
            rollingPlayer.increaseScore()
            rollingPlayer = if (rollingPlayer == playerOne) playerTwo else playerOne
            rollCount = 0
        }
    }
}