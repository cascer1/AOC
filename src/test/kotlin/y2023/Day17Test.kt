package y2023

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import readInput

@DisplayName("Day 17")
class Day17Test {

    @Nested
    @DisplayName("Part 1")
    inner class Part1 {
        @Test
        fun test() {
            val input = readInput("2023/Day17_test")
            val result = Day17(input).part1()
            assertEquals(102, result)
        }

        @Test
        fun actualAnswer() {
            val input = readInput("2023/Day17")
            val result = Day17(input).part1()
            assertNotNull(result)
            println(result)
        }
    }

    @Nested
    @DisplayName("Part 2")
    inner class Part2 {
        @Test
        fun test() {
            val input = readInput("2023/Day17_test")
            val result = Day17(input).part2()
            assertEquals(58, result)
        }

        @Test
        fun actualAnswer() {
            val input = readInput("2023/Day17")
            val result = Day17(input).part2()
            assertNotNull(result)
            println(result)
        }
    }
}