package y2023.day05

import readInput
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

val numberLinePatern = Regex("\\d+ \\d+ \\d+")

@OptIn(ExperimentalTime::class)
fun main() {
    val inputFile = "2023/Day05"
    val testInput = readInput("${inputFile}_test")
    require(part1(testInput) == 35L) { "Part 1 check failed" }

    val input = readInput(inputFile)
    val part1Duration: Duration = measureTime {
        println(part1(input))
    }
    println("Part 1 time: ${part1Duration.toDouble(DurationUnit.MILLISECONDS)} ms")

    require(part2(testInput) == 46L) { "Part 2 check failed" }
    val part2Duration: Duration = measureTime {
        println(part2(input))
    }

    println("Part 2 time: ${part2Duration.toDouble(DurationUnit.MILLISECONDS)} ms")
}

fun part1(input: List<String>): Long {
    val mappings = HashMap<MappingMode, List<Mapping>>()
    var mappingMode = MappingMode.SEED_TO_SOIL
    var seeds: List<Long> = emptyList()

    input.filter { it.isNotBlank() }.forEach { line ->
        if (line.startsWith("seeds: ")) {
            seeds = parseSeedsPartOne(line)
        } else if (numberLinePatern.matches(line)) {
            if (mappings[mappingMode] == null) {
                mappings[mappingMode] = listOf(parseRange(line))
            } else {
                mappings[mappingMode] = mappings[mappingMode]!!.plus(parseRange(line))
            }
        } else {
            mappingMode = MappingMode.fromValue(line.split(" ")[0])
        }

    }

    return mapSeedsToLocations(seeds, mappings).min()
}

fun part2(input: List<String>): Long {
    val mappings = HashMap<MappingMode, List<Mapping>>()
    var mappingMode = MappingMode.SEED_TO_SOIL
    var seeds: List<Long> = emptyList()
    var seedLine = ""

    input.filter { it.isNotBlank() }.forEach { line ->
        if (line.startsWith("seeds: ")) {
            seedLine = line.removePrefix("seeds: ")
        } else if (numberLinePatern.matches(line)) {
            if (mappings[mappingMode] == null) {
                mappings[mappingMode] = listOf(parseRange(line))
            } else {
                mappings[mappingMode] = mappings[mappingMode]!!.plus(parseRange(line))
            }
        } else {
            mappingMode = MappingMode.fromValue(line.split(" ")[0])
        }

    }

    val seedNumberGroups = seedLine.split(" ")
            .filter { it.isNotBlank() }
            .map { it.toLong() }
            .windowed(2, 2)

    val minNumber = seedNumberGroups.minOf { (start, size) ->
        (start until start + size).minOf { seed ->
            mapSeedsToLocations(listOf(seed), mappings).min()
        } }

    return minNumber
}

fun parseSeedsPartOne(line: String): List<Long> {
    return line.removePrefix("seeds: ")
            .split(" ")
            .filter { it.isNotBlank() }
            .map { it.toLong() }
}

fun parseSeedsPartTwo(line: String): List<Long> {
    val groups = line.removePrefix("seeds: ")
            .split(" ")
            .filter { it.isNotBlank() }
            .map { it.toLong() }
            .windowed(2, 2)

    return groups.map { (start, size) ->
        (start until start + size).toList()
    }.flatten().toSet().toList()
}

fun parseRange(input: String): Mapping {
    val (targetStart, sourceStart, length) = input.split(" ").map { it.toLong() }
    return Mapping(sourceStart, targetStart, length)
}

fun mapSeedsToLocations(seeds: List<Long>, mappings: Map<MappingMode, List<Mapping>>): List<Long> {
    return seeds.asSequence().map { seed ->
        mapNumber(seed, mappings[MappingMode.SEED_TO_SOIL]!!)
    }.map { soil ->
        mapNumber(soil, mappings[MappingMode.SOIL_TO_FERTILIZER]!!)
    }.map { fertilizer ->
        mapNumber(fertilizer, mappings[MappingMode.FERTILIZER_TO_WATER]!!)
    }.map { water ->
        mapNumber(water, mappings[MappingMode.WATER_TO_LIGHT]!!)
    }.map { light ->
        mapNumber(light, mappings[MappingMode.LIGHT_TO_TEMPERATURE]!!)
    }.map { temperature ->
        mapNumber(temperature, mappings[MappingMode.TEMPERATURE_TO_HUMIDITY]!!)
    }.map { humidity ->
        mapNumber(humidity, mappings[MappingMode.HUMIDITY_TO_LOCATION]!!)
    }.toList()
}

fun mapNumber(input: Long, mappings: List<Mapping>): Long {
    mappings.filter { it.appliesTo(input) }
            .forEach { return it.apply(input) }

    return input
}

enum class MappingMode(val value: String) {
    SEED_TO_SOIL("seed-to-soil"),
    SOIL_TO_FERTILIZER("soil-to-fertilizer"),
    FERTILIZER_TO_WATER("fertilizer-to-water"),
    WATER_TO_LIGHT("water-to-light"),
    LIGHT_TO_TEMPERATURE("light-to-temperature"),
    TEMPERATURE_TO_HUMIDITY("temperature-to-humidity"),
    HUMIDITY_TO_LOCATION("humidity-to-location");

    companion object {
        fun fromValue(value: String): MappingMode {
            return values().first { it.value == value }
        }
    }
}

class Mapping(val sourceStart: Long, val targetStart: Long, val length: Long) {
    var sourceEnd = 0L
    var offset = 0L

    init {
        sourceEnd = sourceStart + length
        offset = targetStart - sourceStart
    }

    fun appliesTo(number: Long): Boolean {
        return (number >= sourceStart && number < sourceEnd)
    }

    fun apply(source: Long): Long {
        return source + offset
    }

    override fun toString(): String {
        return "$sourceStart, $targetStart, $length"
    }
}