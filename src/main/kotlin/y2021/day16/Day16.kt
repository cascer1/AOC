@file:OptIn(ExperimentalTime::class)

package y2021.day16

import readInput
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

fun ArrayDeque<Char>.takeFirst(count: Int): String {
    val result = ArrayList<Char>()

    repeat(count) {
        result.add(this.removeFirst())
    }

    return result.joinToString("")
}

fun main() {
    check(part1("D2FE28") == 6)
    check(part1("8A004A801A8002F478") == 16)
    check(part1("620080001611562C8802118E34") == 12)
    check(part1("C0015000016115A2E0802F182340") == 23)
    check(part1("A0016C880162017C3686B18A3D4780") == 31)
    check(part2("C200B40A82") == 3L)
    check(part2("04005AC33890") == 54L)
    check(part2("880086C3E88112") == 7L)
    check(part2("CE00C43D881120") == 9L)
    check(part2("D8005AC2A8F0") == 1L)
    check(part2("F600BC2D8F") == 0L)
    check(part2("9C005AC2F8F0") == 0L)
    check(part2("9C0141080250320F1802104A08") == 1L)

    val input = readInput("Day16")

    val part1Duration: Duration = measureTime {
        println(part1(input[0]))
    }
    val part2Duration: Duration = measureTime {
        println(part2(input[0]))
    }

    println("Part 1 time: ${part1Duration.toDouble(DurationUnit.MILLISECONDS)} ms")
    println("Part 2 time: ${part2Duration.toDouble(DurationUnit.MILLISECONDS)} ms")
}

fun convertToBinary(input: String): ArrayDeque<Char> {
    val converted = input.map { it.toString().toInt(16).toByte() }
            .map { it.toString(2).padStart(4, '0') }
            .reduce { acc, s -> acc + s }
            .toList()

    return ArrayDeque(converted)
}

fun parseBinary(input: ArrayDeque<Char>): ArrayList<Packet> {
    val packets = ArrayList<Packet>()
    while (!input.isEmpty() && !input.all { it == '0' }) {
        packets.add(parsePacket(input))
    }
    return packets
}

fun parsePacket(input: ArrayDeque<Char>): Packet {
    val thisPacket = Packet()
    thisPacket.version = input.takeFirst(3).toInt(2)
    thisPacket.type = input.takeFirst(3).toInt(2)

    if (thisPacket.type == 4) {
        completeBasicPacket(thisPacket, input)
    } else {
        completeComplexPacket(thisPacket, input)
    }

    return thisPacket
}

fun completeBasicPacket(packet: Packet, input: ArrayDeque<Char>) {
    var groupType: Char
    do {
        // Loop through groups of bits until one is prefixed with 0
        // Group prefixed with 0 is the final group
        groupType = input.removeFirst()
        val numberPart = input.takeFirst(4)
        packet.numberString += numberPart
    } while (groupType == '1')
}

fun completeComplexPacket(packet: Packet, input: ArrayDeque<Char>) {
    val lengthType = input.removeFirst()

    if (lengthType == '0') {
        // 15 bits indicate total bit count
        val bitCount = input.takeFirst(15).toInt(2)
        val tempQueue = ArrayDeque(input.takeFirst(bitCount).toList())
        packet.subPackets.addAll(parseBinary(tempQueue))
    } else {
        // 11 bits indicate number of sub-packets
        val packetCount = input.takeFirst(11).toInt(2)
        repeat(packetCount) {
            packet.subPackets.add(parsePacket(input))
        }
    }
}

fun part1(input: String): Int {
    val binary = convertToBinary(input)
    val packets = parseBinary(binary)

    return packets.sumOf { it.totalVersion }
}

fun part2(input: String): Long {
    val binary = convertToBinary(input)
    val packets = parseBinary(binary)

    return packets.sumOf { it.answer() }
}

data class Packet(var version: Int = 0, var type: Int = 0, var numberString: String = "", var subPackets: ArrayList<Packet> = ArrayList()) {
    val number: Long
        get() = numberString.toLong(2)

    val totalVersion: Int
        get() = version + subPackets.sumOf { it.totalVersion }

    fun answer(): Long {
        return when (type) {
            0 -> subPackets.sumOf { it.answer() }
            1 -> if (subPackets.size == 1) subPackets[0].answer() else subPackets.map { it.answer() }.reduce { a, b -> a * b }
            2 -> subPackets.minOf { it.answer() }
            3 -> subPackets.maxOf { it.answer() }
            4 -> number
            5 -> if (subPackets[0].answer() > subPackets[1].answer()) 1 else 0
            6 -> if (subPackets[0].answer() < subPackets[1].answer()) 1 else 0
            7 -> if (subPackets[0].answer() == subPackets[1].answer()) 1 else 0
            else -> throw IllegalStateException("Unknown package type: $type")
        }
    }
}