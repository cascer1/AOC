package y2022.day07

data class Node(val name: String, val parent: Node? = null, val size: Int? = null, val children: HashSet<Node> = HashSet()) {

    val totalSize: Int
        get() = children.sumOf { it.totalSize } + (size ?: 0)

    val isDirectory: Boolean
        get() = size == null

    val childDirectories: Set<Node>
        get() = children.filter { it.isDirectory }.toSet()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Node

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun toString(): String {
        return "Node(name='$name', size=$size, isDirectory=$isDirectory)"
    }

}
