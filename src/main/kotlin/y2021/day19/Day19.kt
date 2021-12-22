@file:OptIn(ExperimentalTime::class)

package y2021.day19

import readInput
import kotlin.math.abs
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

private val scanners: HashSet<Scanner> = hashSetOf()

private fun HashSet<Scanner>.addBeacon(scanner: Int, beacon: Beacon) {
    this.first { it.id == scanner }.beacons.add(beacon)
}

private fun Set<Distance>.matchCount(other: Set<Distance>): Int {
    return count { other.contains(it) }
}

private fun main() {
    val testInput = readInput("Day19_test")
    parseInput(testInput)
    check(part1() == 79)
    check(part2() == 3621)

    val input = readInput("Day19")
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

private fun parseInput(input: List<String>) {
    scanners.clear()
    var currentScanner = 0

    for (line in input) {
        if (line.isEmpty()) {
            continue
        }

        if (line.startsWith("---")) {
            // new scanner
            currentScanner = line.removePrefix("--- scanner ").removeSuffix(" ---").toInt()
            if (currentScanner == 0) {
                scanners.add(Scanner(0, 0, 0, 0).apply { positionKnown = true })
            } else {
                scanners.add(Scanner(currentScanner))
            }
        } else {
            val (x, y, z) = line.split(",").map { it.toInt() }
            scanners.addBeacon(currentScanner, Beacon(x, y, z))
        }
    }
}

private fun part1(): Int {

    do {
        scanners.filter { it.positionKnown }.forEach { anchor ->
            scanners.filter { !it.positionKnown }.forEach { target ->
                matchScanners(anchor, target)
            }
        }
    } while (scanners.any { !it.positionKnown })

    val beaconUnique = scanners.flatMap { it.beacons }.toSet()

    return beaconUnique.size
}

private fun part2(): Int {
    return scanners.maxOf { firstScanner ->
        scanners.maxOf { secondScanner ->
            abs(firstScanner.x!! - secondScanner.x!!) + abs(firstScanner.y!! - secondScanner.y!!) + abs(firstScanner.z!! - secondScanner.z!!)
        }
    }
}

private fun matchScanners(anchor: Scanner, target: Scanner) {
    if (target.positionKnown) {
        return
    }
    var success = false
    target.calculateDistances()

    if (anchor.matchesScanner(target)) {
        success = translateScanner(anchor, target)
        if (success) return
    }

    repeat(4) {
        target.rotateX()
        if (anchor.matchesScanner(target)) {
            success = translateScanner(anchor, target)
            if (success) return
        }
    }
    target.rotateY()

    repeat(4) {
        repeat(4) {
            target.rotateX()
            if (anchor.matchesScanner(target)) {
                success = translateScanner(anchor, target)
                if (success) return
            }
        }
        target.rotateZ()
    }

    target.rotateY()
    repeat(4) {
        target.rotateX()
        if (anchor.matchesScanner(target)) {
            success = translateScanner(anchor, target)
            if (success) return
        }
    }
}

private fun translateScanner(anchor: Scanner, target: Scanner): Boolean {
    val translationX: ArrayList<Int> = arrayListOf()
    val translationY: ArrayList<Int> = arrayListOf()
    val translationZ: ArrayList<Int> = arrayListOf()

    target.beacons.forEach { targetBeacon ->
        anchor.beacons.forEach { anchorBeacon ->
            val matches = targetBeacon.matchesBeacon(anchorBeacon)
            if (matches) {
                translationX += anchorBeacon.x - targetBeacon.x
                translationY += anchorBeacon.y - targetBeacon.y
                translationZ += anchorBeacon.z - targetBeacon.z
            }
        }
    }

    val averageX = translationX.average().toInt()
    val averageY = translationY.average().toInt()
    val averageZ = translationZ.average().toInt()

    if (averageX == translationX.first() && averageY == translationY.first() && averageZ == translationZ.first()) {
        target.positionKnown = true
        target.x = averageX
        target.y = averageY
        target.z = averageZ

        target.translate(averageX, averageY, averageZ)
        return true
    }

    return false
}

private data class Scanner(val id: Int, var x: Int? = null, var y: Int? = null, var z: Int? = null) {
    var positionKnown: Boolean = false
    var beacons: HashSet<Beacon> = hashSetOf()

    var distances: Set<Distance> = setOf()
        get() {
            return field.ifEmpty {
                field = calculateDistances()
                field
            }
        }

    fun translate(x: Int, y: Int, z: Int) {
        beacons.forEach { beacon ->
            beacon.x += x
            beacon.y += y
            beacon.z += z
        }
    }

    fun matchesScanner(other: Scanner): Boolean {
        var matchCount = 0

        beacons.forEach { thisBeacon ->
            other.beacons.forEach { otherBeacon ->
                if (thisBeacon.matchesBeacon(otherBeacon)) {
                    matchCount++
                }
            }
        }

        return matchCount >= 12
    }

    fun rotateX(): Scanner {
        beacons.forEach { beacon ->
            val oldY = beacon.y
            val oldZ = beacon.z
            beacon.y = oldZ * -1
            beacon.z = oldY
        }
        distances = calculateDistances()
        return this
    }

    fun rotateZ(): Scanner {
        beacons.forEach { beacon ->
            val oldX = beacon.x
            val oldY = beacon.y
            beacon.x = oldY * -1
            beacon.y = oldX
        }
        distances = calculateDistances()
        return this
    }

    fun rotateY(): Scanner {
        beacons.forEach { beacon ->
            val oldX = beacon.x
            val oldZ = beacon.z
            beacon.x = oldZ * -1
            beacon.z = oldX
        }
        distances = calculateDistances()
        return this
    }

    fun calculateDistances(): Set<Distance> {
        beacons.forEach { thisBeacon ->
            thisBeacon.distances.clear()
            beacons.filter { it != thisBeacon }.forEach {
                val distance = Distance(abs(thisBeacon.x - it.x), abs(thisBeacon.y - it.y), abs(thisBeacon.z - it.z))
                thisBeacon.distances.add(distance)
            }
        }

        return beacons.flatMap { it.distances }.toSet()
    }
}

private data class Beacon(var x: Int, var y: Int, var z: Int) {
    var distances: HashSet<Distance> = hashSetOf()

    fun matchesBeacon(other: Beacon): Boolean {
        val count = distances.matchCount(other.distances)
        return count >= 11
    }

    override fun toString(): String {
        return "Beacon[$x, $y, $z]"
    }
}

private data class Distance(var x: Int, var y: Int, var z: Int)