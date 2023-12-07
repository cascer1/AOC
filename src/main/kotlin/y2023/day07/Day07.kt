package y2023.day07

import readInput
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
fun main() {
    val inputFile = "2023/Day07"
    val testInput = readInput("${inputFile}_test")
    require(part1(testInput) == 6440) { "Part 1 check failed" }

    val input = readInput(inputFile)
    val part1Duration: Duration = measureTime {
        println(part1(input))
    }
    println("Part 1 time: ${part1Duration.toDouble(DurationUnit.MILLISECONDS)} ms")

    require(part2(testInput) == 5905) { "Part 2 check failed" }
    val part2Duration: Duration = measureTime {
        println(part2(input))
    }

    println("Part 2 time: ${part2Duration.toDouble(DurationUnit.MILLISECONDS)} ms")
}

fun part1(input: List<String>): Int {
    return parseInput(input, false)
            .sorted()
            .mapIndexed { index, hand ->
                (index + 1) * hand.bid
            }.sum()
}

fun part2(input: List<String>): Int {
    return parseInput(input, true)
            .sorted()
            .mapIndexed { index, hand ->
                (index + 1) * hand.bid
            }.sum()
}

fun parseInput(input: List<String>, joker: Boolean): List<Hand> {
    return input.map { Hand(it, joker) }
}

class Hand(private val input: String, joker: Boolean) : Comparable<Hand> {
    private val cards: MutableList<Card> = input.split(" ")[0].map { Card.fromSymbol(it) }.toMutableList()
    private val originalCards = cards.toList()
    val bid: Int = input.split(" ")[1].toInt()
    private val groupedCards: List<Int>
    private var jokerCount = 0
    var score: Int? = null
        get() {
            if (field == null) {
                field = calculateScore()
            }

            return field
        }

    init {
        if (joker) {
            cards.filter { it == Card.JACK }
                    .forEach {
                        it.value = 1
                        jokerCount++
                    }

            val cardCounts = cards.groupingBy { it }.eachCount().entries.filter { it.key != Card.JACK }

            if (cardCounts.isNotEmpty() && cards.contains(Card.JACK)) {
                val maxCardCount = cardCounts.maxBy { it.value }.value

                val highestCard = cardCounts.filter { it.value == maxCardCount }.maxBy { it.key }.key

                cards.replaceAll { card ->
                    if (card == Card.JACK) {
                        highestCard
                    } else {
                        card
                    }
                }
            }
        }

        groupedCards = cards.groupBy { it }.values.map { it.size }
    }

    private fun isFiveOfAKind(): Boolean {
        return groupedCards.contains(5)
    }

    private fun isFourOfAKind(): Boolean {
        return groupedCards.contains(4)
    }

    private fun isFullHouse(): Boolean {
        return groupedCards.contains(2) && groupedCards.contains(3)
    }

    private fun isThreeOfAKind(): Boolean {
        return groupedCards.contains(3)
    }

    private fun isTwoPair(): Boolean {
        return groupedCards.filter { it == 2 }.size == 2
    }

    private fun isOnePair(): Boolean {
        return groupedCards.any { it == 2 }
    }

    fun calculateScore(): Int {
        if (isFiveOfAKind()) {
            return 6
        }

        if (isFourOfAKind()) {
            return 5
        }

        if (isFullHouse()) {
            return 4
        }

        if (isThreeOfAKind()) {
            return 3
        }

        if (isTwoPair()) {
            return 2
        }

        if (isOnePair()) {
            return 1
        }

        return 0
    }

    override fun compareTo(other: Hand): Int {
        if (score != other.score) {
            return score!!.compareTo(other.score!!)
        }

        originalCards.forEachIndexed { index, card ->
            if (card != other.originalCards[index]) {
                return card.value.compareTo(other.originalCards[index].value)
            }
        }

        return 1
    }

    override fun toString(): String {
        return "Hand(input = $input, cards=$cards, bid=$bid, score=${calculateScore()})"
    }
}

enum class Card(val symbol: Char, var value: Int) : Comparable<Card> {
    // A, K, Q, J, T, 9, 8, 7, 6, 5, 4, 3, or 2
    TWO('2', 2),
    THREE('3', 3),
    FOUR('4', 4),
    FIVE('5', 5),
    SIX('6', 6),
    SEVEN('7', 7),
    EIGHT('8', 8),
    NINE('9', 9),
    TEN('T', 10),
    JACK('J', 11),
    QUEEN('Q', 12),
    KING('K', 13),
    ACE('A', 14);

    override fun toString(): String {
        return symbol.toString()
    }

    companion object {
        fun fromSymbol(symbol: Char): Card {
            return Card.values().first { it.symbol == symbol }
        }
    }

}
