package y2022.day16

import readInput
import java.util.*
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
fun main() {
    val inputFile = "2022/Day16"
    val testInput = readInput("${inputFile}_test")
    check(part1(testInput) == 1651)
    println("Part 1 check successful!")
//    check(part2(testInput) == 58)
    println("Part 2 check successful!")

    val input = readInput(inputFile)
    val part1Duration: Duration = measureTime {
        println(part1(input))
    }
    val part2Duration: Duration = measureTime {
        println(part2(input))
    }

    println("Part 1 time: ${part1Duration.toDouble(DurationUnit.MILLISECONDS)} ms")
    println("Part 2 time: ${part2Duration.toDouble(DurationUnit.MILLISECONDS)} ms")
}

private fun part1(input: List<String>): Int {
    val valves = parseInput(input)

    val distances: HashMap<String, HashMap<String, Int>> = HashMap()

    valves.forEach { (_, from) ->
        valves.forEach { (_, to) ->
            val distance = from.stepsToReach(to)
            val map = distances.getOrDefault(from.name, HashMap())
            map[to.name] = distance
            distances[from.name] = map
        }
    }

    val queue = PriorityQueue<SimulationState>(compareByDescending { it.minimumScore })
    queue.add(SimulationState(timeRemaining = 30, valves = valves, currentValve = "AA"))
    var bestResult = 0

    val processedStates = HashSet<SimulationState>()

    while (queue.isNotEmpty()) {
        val thisState = queue.remove()
        processedStates.add(thisState.copy())
        val simulationOutputs = thisState.simulateStep(distances)
        if (simulationOutputs.isNotEmpty()) {
            val bestSimulationScore = simulationOutputs.maxOf { it.released }
            bestResult = bestResult.coerceAtLeast(bestSimulationScore)
            simulationOutputs.filter { !processedStates.contains(it) }.forEach { queue.add(it) }
        }
    }

    return bestResult
}

private fun part2(input: List<String>): Int {
    parseInput(input)

    return 2
}



//fun simulateStep(valves: HashMap<String, Valve>, currentValve: String, minute: Int): Int {
//    if (minute == 30) {
//        return valves.getMatching { it.open }.sumOf { it.flowRate }
//    }
//}

fun parseInput(input: List<String>): HashMap<String, Valve> {
    val valves: HashMap<String, Valve> = HashMap()

    input.forEach { line ->
        // Valve BB has flow rate=13; tunnels lead to valves CC, AA
        val parts = line.removePrefix("Valve ")
            .split(" has flow rate=", "; tunnels lead to valves ", "; tunnel leads to valve ")
        val valveName = parts[0]
        val flowRate = parts[1].toInt()
        val tunnels = parts[2].split(", ").map {
            valves.getOrElse(it) {
                val newValve = Valve(name = it)
                valves[it] = newValve
                newValve
            }
        }.toSet()

        if (valves.containsKey(valveName)) {
            valves[valveName]!!.flowRate = flowRate
            valves[valveName]!!.tunnels = tunnels
        } else {
            valves[valveName] = Valve(valveName, flowRate, tunnels)
        }

    }
    return valves
}

fun HashMap<String, Valve>.getMatching(filter: (Valve) -> Boolean): Set<Valve> {
    return this.values.filter(filter).toSet()
}