package y2022.day15

import manhattanDistance

data class Sensor(val coordinates: Pair<Int, Int>, val beacon: Pair<Int, Int>, val distance: Int) {
    val x: Int
        get() = coordinates.first

    val y: Int
        get() = coordinates.second

    fun isNearY(targetY: Int): Boolean {
        return (y + distance > targetY) && (y - distance < targetY)
    }

    fun isNearY(yRange: IntRange): Boolean {
        return yRange.contains(y + distance) || yRange.contains(y - distance)
    }

    fun isNearX(xRange: IntRange): Boolean {
        return xRange.contains(x + distance) || xRange.contains(x - distance)
    }

    fun isWithin(xRange: IntRange, yRange: IntRange): Boolean {
        return isNearY(yRange) && isNearX(xRange)
    }

    fun isInRange(target: Pair<Int, Int>): Boolean {
        return coordinates.manhattanDistance(target) <= distance
    }

    fun getNearbyCoordinates(): Set<Pair<Int, Int>> {
        val returned = HashSet<Pair<Int, Int>>()

        val centerX = coordinates.first
        val centerY = coordinates.second
        val maxX = centerX + distance
        val minX = centerX - distance
        val maxY = centerY + distance
        val minY = centerY - distance

        (centerX + 1..maxX + 1).zip((maxY downTo centerY)).forEach(returned::add)
        (maxX downTo centerX).zip((centerY - 1 downTo minY - 1)).forEach(returned::add)
        (centerX - 1 downTo minX - 1).zip((minY..centerY)).forEach(returned::add)
        (minX..centerX).zip((centerY + 1..maxY + 1)).forEach(returned::add)

        return returned
    }
}
