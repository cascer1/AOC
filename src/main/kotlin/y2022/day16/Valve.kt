package y2022.day16

data class Valve(val name: String, var flowRate: Int = 0, var tunnels: Set<Valve> = emptySet(), var open: Boolean = false) {

    fun stepsToReach(other: Valve): Int {
        if (this.name == other.name) {
            return 0
        }

        return stepsToReach(other, HashSet())
    }

    private fun stepsToReach(other: Valve, checked: HashSet<Valve>): Int {
        checked.add(this)

        if (this == other) {
            return 0
        }

        val viableTunnels = tunnels.filterNot { checked.contains(it) }

        if (viableTunnels.isEmpty()) {
            return 9999
        }

        return viableTunnels.minOf { it.stepsToReach(other, checked) + 1 }
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Valve

        if (name != other.name) return false
        if (flowRate != other.flowRate) return false
        if (tunnels != other.tunnels) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun toString(): String {
        val tunnelNames = tunnels.map { it.name }
        return "Valve(name='$name', open=$open, flowRate=$flowRate, tunnels=$tunnelNames)"
    }


}