package y2015.day04

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.toList
import md5
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
fun main() {
    check(part1("abcdef") == 609043)
    println("Part 1.1 check done")
    check(part1("pqrstuv") == 1048970)
    println("Part 1.2 check done")

    val input = "yzbqklnj"
    val part1Duration: Duration = measureTime {
        println(part1(input))
    }
    val part2Duration: Duration = measureTime {
        println(part2(input))
    }

    println("Part 1 time: ${part1Duration.toDouble(DurationUnit.MILLISECONDS)} ms")
    println("Part 2 time: ${part2Duration.toDouble(DurationUnit.MILLISECONDS)} ms")
}

private fun part1(input: String): Int {
    return findFirstHash(input, "00000")
}

private fun part2(input: String): Int {
    return findFirstHash(input, "000000")
}

fun findFirstHash(prefix: String, expectedStart: String): Int {
    val stepSize = 10
    var step = 0
    var first: Int

    do {
        runBlocking {
            val start = step * stepSize
            val end = start + stepSize
            first = validHashInRange(start until end, prefix, expectedStart)
        }
        step++
    } while (first == Int.MAX_VALUE)

    return first
}

suspend fun validHashInRange(range: IntRange, prefix: String, expectedStart: String): Int = coroutineScope {
    val results = Channel<Pair<Int, String>>()
    launch {
        range.forEach { i ->
            results.send(Pair(i, (prefix + i).md5()))
        }
        results.close()
    }

    val lowestValidHash = results.toList().filter { it.second.startsWith(expectedStart) }
            .minByOrNull { it.first } ?: Pair(Int.MAX_VALUE, "")

    return@coroutineScope lowestValidHash.first
}
