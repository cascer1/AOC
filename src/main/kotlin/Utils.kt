import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import kotlin.math.max
import kotlin.math.min

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("inputs", "2021/$name.txt").readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5(): String = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray())).toString(16)

fun IntRange.containsAll(other: IntRange): Boolean {
    return this.first <= other.first && this.last >= other.last
}

fun IntRange.intersects(other: IntRange): Boolean {
    return this.contains(other.first) || this.contains(other.last) || other.contains(this.first) || other.contains(this.last)
}

fun IntRange.left(other: IntRange): IntRange {
    return min(this.first, other.first) until max(this.first, other.first)
}

fun IntRange.center(other: IntRange): IntRange {
    return max(this.first, other.first)..min(this.last, other.last)
}

fun IntRange.right(other: IntRange): IntRange {
    return min(this.last + 1, other.last + 1)..max(this.last, other.last)
}

fun IntRange.split(other: IntRange): List<IntRange> {
    return listOf(
            left(other),
            center(other),
            right(other)
    ).filterNot { it.isEmpty() }
}