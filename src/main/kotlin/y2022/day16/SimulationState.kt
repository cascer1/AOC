package y2022.day16

data class SimulationState(var timeRemaining: Int, var released: Int = 0, var valves: HashMap<String, Valve>, var currentValve: String) {

    fun tick(seconds: Int) {
        repeat(seconds) {
            if (timeRemaining > 0) {
                timeRemaining--
                released += flowRate
            }
        }
        valves = HashMap(valves)
    }

    fun openValve() {
        valves[currentValve] = currentValveObject.copy(open = true)
    }

    fun simulateStep(distances: HashMap<String, HashMap<String, Int>>): Set<SimulationState> {
        if (timeRemaining <= 0) {
            return emptySet()
        }

        val result = HashSet<SimulationState>()

        tick(1)

        // Standing still is a valid move
        result.add(this.copy())

        if (timeRemaining == 0) {
            // No need to do anything else if we've run out of time
            return result
        }

        // Open current valve as move
        if (!currentValveObject.open) {
            val newState = this.copy()
            newState.openValve()
            result.add(newState)
        }

        // only consider closed valves for visiting
        valves.values.filter { !it.open }.forEach {
            val distance = distances[currentValve]!![it.name]!!
            val newState = this.copy(currentValve = it.name)
            newState.tick(distance - 1)
            result.add(newState)
        }

        return result
    }

    val minimumScore: Int
        get() = released + flowRate * timeRemaining

    private val flowRate: Int
        get() = valves.getMatching { it.open }.sumOf { it.flowRate }

    val currentValveObject: Valve
        get() = valves[currentValve]!!

    override fun toString(): String {
        return "State(remaining=$timeRemaining, released=$released, current=$currentValve, minimum=$minimumScore)"
    }
}
