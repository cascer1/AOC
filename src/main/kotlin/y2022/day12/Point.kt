package y2022.day12

data class Point (var height: Byte, var visited: Boolean = false, var x: Int, var y: Int, var distance: Int = Int.MAX_VALUE) {
    val unvisited: Boolean
        get() = !visited
}