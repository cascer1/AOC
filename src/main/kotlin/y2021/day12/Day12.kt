package y2021.day12

import readInput

private var caves: HashSet<Cave> = HashSet()
private var routes: HashSet<ArrayList<Cave>> = HashSet()

private fun HashSet<Cave>.addConnection(from: String, to: String) {
    if (!this.any { it.name == from }) {
        this.add(Cave(from, from.uppercase() == from))
    }

    if (!this.any { it.name == to }) {
        this.add(Cave(to, to.uppercase() == to))
    }

    this.find(from)!!.addConnection(this.find(to)!!)
    this.find(to)!!.addConnection(this.find(from)!!)
}

private fun HashSet<Cave>.find(name: String): Cave? {
    return this.firstOrNull { it.name == name }
}

private fun parseInput(input: List<String>) {
    caves.clear()
    routes.clear()
    caves.add(Cave("start", false))
    caves.add(Cave("end", false))
    input.forEach { cave ->
        val (name, connection) = cave.split("-")
        caves.addConnection(name, connection)
    }
}

private fun main() {
    // test if implementation meets criteria from the description, like:
    val testInput = readInput("2021/Day12_test")
    parseInput(testInput)
    check(part1() == 226)
    check(part2() == 3509)

    val input = readInput("2021/Day12")
    parseInput(input)
    println(part1())
    println(part2())
}

private fun part1(): Int {
    getRouteCount(listOf(caves.find("start")!!))

    return routes.size
}

private fun part2(): Int {
    caves.filter { !it.large }
            .filter { it.name != "start" && it.name != "end" }
            .forEach { doubleCave ->
                getRouteCount(listOf(caves.find("start")!!), doubleCave)
            }

    return routes.size
}

private fun getRouteCount(routeSoFar: List<Cave>, doubleCave: Cave? = null) {
    if (routeSoFar.last().name == "end") {
        routes.add(ArrayList(routeSoFar))
        return
    }

    var options = routeSoFar.last().connections
            .filter { it.large || !routeSoFar.contains(it) || it == doubleCave }

    if (routeSoFar.count { it == doubleCave } == 2) {
        options = options.minus(doubleCave!!)
    }

    options.forEach { getRouteCount(routeSoFar + it, doubleCave) }
}

private data class Cave(val name: String, val large: Boolean, var connections: HashSet<Cave> = HashSet()) {
    fun addConnection(cave: Cave) {
        connections.add(cave)
    }

    // Ignore connections list in equality check because it can cause stack overflows
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Cave

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun toString(): String {
        return name
    }


}