package y2022.day11


data class Monkey(
    val monkeyId: Int,
    val operation: (Long, Long) -> Long,
    val operationNumber: Long?,
    val testDivisor: Long,
    val trueDestination: Long,
    val falseDestination: Long,
    val items: ArrayList<Long>,
    var inspectCount: Long = 0L
) {

    companion object {
        var modulo: Long = 1
    }

    private fun inspect(worryLevel: Long, worryDivisor: Long): Long {
        inspectCount++
        if (operationNumber == null) {
            return operation.invoke(worryLevel, worryLevel) % modulo / worryDivisor
        }

        return operation.invoke(worryLevel, operationNumber) % modulo / worryDivisor
    }

    private fun test(worryLevel: Long): Boolean {
        return worryLevel % testDivisor == 0L
    }

    fun handle(worryDivisor: Long): List<Pair<Long, Long>> {
        return items.map { handle(it, worryDivisor) }
    }

    fun throwItems() {
        items.clear()
    }

    private fun handle(worryLevel: Long, worryDivisor: Long): Pair<Long, Long> {
        val newWorryLevel = inspect(worryLevel, worryDivisor)

        if (test(newWorryLevel)) {
            return Pair(trueDestination, newWorryLevel)
        }

        return Pair(falseDestination, newWorryLevel)
    }

    fun giveItem(worryLevel: Long) {
        this.items.add(worryLevel)
    }

    override fun toString(): String {
        return "Monkey $monkeyId (items=$items)"
    }


}
