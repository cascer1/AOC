package y2023

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import readInput

class Year2023Test {

    @Nested
    @DisplayName("Day 18 part 1")
    inner class Part1{
        @Test
        fun test() {
            val input = readInput("2023/Day18_test")
            val result = Day18(input).part1()
            assertEquals(62L, result)
        }

        @Test
        fun actualAnswer() {
            val input = readInput("2023/Day18")
            val result = Day18(input).part1()
            assertNotNull(result)
            println(result)
        }
    }

    @Nested
    @DisplayName("Day 18 part 2")
    inner class Part2{
        @Test
        fun test() {
            val input = readInput("2023/Day18_test")
            val result = Day18(input).part2()
            assertEquals(952_408_144_115L, result)
        }

        @Test
        fun actualAnswer() {
            val input = readInput("2023/Day18")
            val result = Day18(input).part2()
            assertNotNull(result)
            println(result)
        }
    }
}