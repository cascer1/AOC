import java.io.File
import java.util.function.Predicate
import kotlin.math.max
import kotlin.math.min

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("inputs", "2021/$name.txt").readLines()

/**
 * Get the item at index[x][y], or return the unknonw value if none is found
 */
fun <T> Array<Array<T>>.getAt(x: Int, y: Int, unknown: T): T {
    return this.getAt(x, y) ?: unknown
}

fun <T> Array<Array<T>>.getAt(x: Int, y: Int): T? {
    return this.getOrNull(y)?.getOrNull(x)
}

fun <T> HashMap<Pair<Int, Int>, T>.getAt(x: Int, y: Int): T? {
    return this[Pair(x, y)]
}

fun <K> HashMap<K, Long>.addCount(sequence: K, amount: Long) {
    val old = this.getOrDefault(sequence, 0L)
    this[sequence] = old + amount
}

fun <T> Array<Array<T>>.allMatch(predicate: Predicate<T>): Boolean {
    return this.all { row ->
        row.all { predicate.test(it) }
    }
}

fun <T> Array<Array<T>>.anyMatch(predicate: Predicate<T>): Boolean {
    return this.any { row ->
        row.any { predicate.test(it) }
    }
}

fun <T> Array<Array<T>>.get(predicate: Predicate<T>): Set<T> {
    return this.flatMap { row ->
        row.filter { predicate.test(it) }
    }.toSet()
}

/**
 * Get the first x items in the deque as string
 */
fun ArrayDeque<Char>.takeFirst(count: Int): String {
    val result = ArrayList<Char>()

    repeat(count) {
        result.add(this.removeFirst())
    }

    return result.joinToString("")
}

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