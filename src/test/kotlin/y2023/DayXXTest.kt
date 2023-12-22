package y2023

import DayXX
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import readInput

@DisplayName("Day XX")
class DayXXTest {
    val dayNumber = ""

    @Nested
    @DisplayName("Part 1")
    inner class Part1 {
        @Test
        fun test() {
            val input = readInput("2023/Day${dayNumber}_test")
            val result = DayXX(input).part1()
            assertEquals(1, result)
        }

        @Test
        fun actualAnswer() {
            val input = readInput("2023/Day${dayNumber}")
            val result = DayXX(input).part1()
            assertNotNull(result)
            println(result)
        }
    }

    @Nested
    @DisplayName("Part 2")
    inner class Part2 {
        @Test
        fun test() {
            val input = readInput("2023/Day${dayNumber}_test")
            val result = DayXX(input).part2()
            assertEquals(2, result)
        }

        @Test
        fun actualAnswer() {
            val input = readInput("2023/Day${dayNumber}")
            val result = DayXX(input).part2()
            assertNotNull(result)
            println(result)
        }
    }
}