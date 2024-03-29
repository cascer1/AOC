import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import java.util.function.Predicate
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun String.md5(): String =
        BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray())).toString(16).padStart(32, '0')

fun Byte.toggle(): Byte {
    if (this == 0x0.toByte()) {
        return 0x1.toByte()
    }
    return 0x0.toByte()
}

fun Int.toggle(): Int {
    return if (this == 0) 1 else 0
}

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("inputs", "$name.txt").readLines()

enum class CardinalDirection {
    NORTH, SOUTH, WEST, EAST
}

enum class Direction {
    UP, DOWN, LEFT, RIGHT
}

/**
 * Get the item at index[x][y], or return the unknown value if none is found
 */
fun <T> Array<Array<T>>.getAt(x: Int, y: Int, unknown: T): T {
    return this.getAt(x, y) ?: unknown
}

fun <T> Array<Array<T>>.getAt(x: Int, y: Int): T? {
    return this.getOrNull(y)?.getOrNull(x)
}

fun <T> Array<Array<T>>.getAtOrDefault(x: Int, y: Int, default: T): T {
    return this.getAt(x, y) ?: default
}

inline fun <reified T> Array<Array<T>>.getColumn(x: Int): Array<T> {
    val returned = arrayListOf<T>()

    (0..this.lastIndex).forEach { rowIndex ->
        returned.add(this.getAt(x, rowIndex)!!)
    }

    return returned.toTypedArray()
}

typealias Coordinate = TwoDimensionalCoordinates

fun <T> Map<Coordinate, T>.getAt(x: Int, y: Int): T? {
    return this[Coordinate(x, y)]
}

fun <T> Map<Coordinate, T>.getAtOrDefault(x: Int, y: Int, default: T): T {
    return this.getAt(x, y) ?: default
}

fun <T, R> Map<Pair<R, R>, T>.allEmpty(coordinates: Set<Pair<R, R>>): Boolean {
    return coordinates.none { this.contains(it) }
}

fun <T> Set<Pair<T, T>>.allEmpty(coordinates: Set<Pair<T, T>>): Boolean {
    return coordinates.none { this.contains(it) }
}

fun Set<Coordinate>.hasAbove(coordinate: Coordinate): Boolean {
    return this.contains(coordinate.copy(y = coordinate.y - 1))
}

fun Set<Coordinate>.hasBelow(coordinate: Coordinate): Boolean {
    return this.contains(coordinate.copy(y = coordinate.y + 1))
}

fun Set<Coordinate>.hasLeft(coordinate: Coordinate): Boolean {
    return this.contains(coordinate.copy(x = coordinate.x - 1))
}

fun Set<Coordinate>.hasRight(coordinate: Coordinate): Boolean {
    return this.contains(coordinate.copy(x = coordinate.x + 1))
}

fun <T> Map<Coordinate, T>.surroundingMatching(x: Int, y: Int, filter: (T) -> Boolean): ArrayList<T> {
    return ArrayList(
            setOfNotNull(
                    this.getAt(x, y - 1), // above
                    this.getAt(x - 1, y), // left
                    this.getAt(x + 1, y), // right
                    this.getAt(x, y + 1)  // below
            ).filter(filter)
    )
}

fun <T> HashMap<Coordinate, T>.getAtOrDefault(x: Int, y: Int, default: T): T {
    return this.getAt(x, y) ?: default
}

fun <T> HashMap<Coordinate, T>.hasKeyBelow(coordinate: Coordinate): Boolean {
    return this.containsKey(coordinate.copy(y = coordinate.y + 1))
}

fun <T> HashMap<Coordinate, T>.hasKeyAbove(coordinate: Coordinate): Boolean {
    return this.containsKey(coordinate.copy(y = coordinate.y - 1))
}

fun <T> HashMap<Coordinate, T>.hasKeyLeft(coordinate: Coordinate): Boolean {
    return this.containsKey(coordinate.copy(x = coordinate.x - 1))
}

fun <T> HashMap<Coordinate, T>.hasKeyRight(coordinate: Coordinate): Boolean {
    return this.containsKey(coordinate.copy(x = coordinate.x + 1))
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

fun <T> Array<Array<T>>.getSurrounding(x: Int, y: Int): Set<T> {
    return setOfNotNull(
            this.getAt(x, y - 1), // above
            this.getAt(x - 1, y), // left
            this.getAt(x + 1, y), // right
            this.getAt(x, y + 1)  // below
    )
}

fun <T> Array<Array<T>>.getSurrounding(x: Int, y: Int, allowedDirections: Set<Direction>): Set<T> {
    val returned = HashSet<T>()
    if (allowedDirections.contains(Direction.UP)) {
        this.getAt(x, y - 1)?.let { returned.add(it) }
    }
    if (allowedDirections.contains(Direction.LEFT)) {
        this.getAt(x - 1, y)?.let { returned.add(it) }
    }
    if (allowedDirections.contains(Direction.RIGHT)) {
        this.getAt(x + 1, y)?.let { returned.add(it) }
    }
    if (allowedDirections.contains(Direction.DOWN)) {
        this.getAt(x, y + 1)?.let { returned.add(it) }
    }
    return returned
}

fun <T> Array<Array<T>>.getSurroundingMatching(x: Int, y: Int, predicate: Predicate<T>): Set<T> {
    return setOfNotNull(
            this.getAt(x, y - 1), // above
            this.getAt(x - 1, y), // left
            this.getAt(x + 1, y), // right
            this.getAt(x, y + 1)  // below
    ).filter { predicate.test(it) }.toSet()
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

data class TwoDimensionalCoordinates(var x: Int, var y: Int, var direction: Direction? = null) {
    companion object {
        @JvmStatic
        fun fromString(input: String): TwoDimensionalCoordinates {
            val (x, y) = input.split(',').map { it.toInt() }
            return TwoDimensionalCoordinates(x, y)
        }
    }

    val first: Int
        get() = x

    val second: Int
        get() = y

    fun manhattanDistance(other: TwoDimensionalCoordinates): Int {
        val xDistance = abs(this.x - other.x)
        val yDistance = abs(this.y - other.y)
        return xDistance + yDistance
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TwoDimensionalCoordinates

        if (x != other.x) return false
        if (y != other.y) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        return result
    }
}

open class ThreeDimensionalCoordinates(var x: Int, var y: Int, var z: Int) {
    companion object {
        @JvmStatic
        fun fromString(input: String): ThreeDimensionalCoordinates {
            val (x, y, z) = input.split(',').map { it.toInt() }
            return ThreeDimensionalCoordinates(x, y, z)
        }
    }
}

fun <T> ArrayList<ArrayDeque<T>>.insertInDeque(index: Int, value: T) {
    if (this.isEmpty()) {
        this.add(ArrayDeque())
    }
    if (this.lastIndex < index) {
        (this.lastIndex..index).forEach {
            this.add(ArrayDeque())
        }
    }
    this[index].addLast(value)
}

fun Pair<Int, Int>.manhattanDistance(other: Pair<Int, Int>): Int {
    val xDistance = abs(this.first - other.first)
    val yDistance = abs(this.second - other.second)
    return xDistance + yDistance
}

fun <T> Sequence<T>.repeat() = sequence { while (true) yieldAll(this@repeat) }