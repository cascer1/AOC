package y2022.day21

class Monkey(val input: String, val monkeys: HashMap<String, Monkey>) {
    private var firstPart: () -> Double
    private var secondPart: () -> Double
    private var simple = true
    var operator = '+'
    var firstPartName: String
    var secondPartName: String? = null
    val name: String

    val hasHuman: Boolean
        get() {
            if (name == "humn") {
                return true
            }

            if (simple) {
                return false
            }

            return monkeys[firstPartName]!!.hasHuman || monkeys[secondPartName]!!.hasHuman
        }

    val answer: Double
        get() {
            if (simple) {
                return firstPart.invoke()
            }

            val firstPartAnswer = firstPart.invoke()
            val secondPartAnswer = secondPart.invoke()

            return when (operator) {
                '+' -> firstPartAnswer + secondPartAnswer
                '-' -> firstPartAnswer - secondPartAnswer
                '/' -> firstPartAnswer / secondPartAnswer
                '*' -> firstPartAnswer * secondPartAnswer
                '=' -> firstPartAnswer.compareTo(secondPartAnswer).toDouble()
                else -> error("Unknown operation: $operator")
            }
        }

    val leftAnswer: Double
        get() = firstPart.invoke()

    val rightAnswer: Double
        get() = secondPart.invoke()

    init {
        val inputParts = input.split(": ")
        name = inputParts[0]

        val parsedDouble = inputParts[1].toDoubleOrNull()

        if (parsedDouble != null) {
            firstPart = { parsedDouble }
            secondPart = { 0.0 }
            firstPartName = parsedDouble.toString()
            simple = true
        } else {
            simple = false
            val parts = inputParts[1].split(" ")
            firstPartName = parts[0]
            secondPartName = parts[2]

            firstPart = { monkeys[parts[0]]!!.answer }
            secondPart = { monkeys[parts[2]]!!.answer }

            operator = parts[1][0]
        }

    }

    fun setNumber(number: Double) {
        firstPart = { number }
        firstPartName = number.toString()
    }

    fun increaseNumber(step: Double) {
        val oldFirstPart = firstPart.invoke()
        firstPart = { oldFirstPart + step }
        firstPartName = firstPart.invoke().toString()
    }

    override fun toString(): String {
        if (name == "humn") {
            return name
        }

        if (simple) {
            return firstPart.invoke().toString()
        }

        val firstPartValue = if (firstPartName == "humn") {
            "humn"
        } else {
            monkeys[firstPartName].toString()
        }

        val secondPartValue = if (secondPartName == "humn") {
            "humn"
        } else {
            monkeys[secondPartName].toString()
        }

        if (hasHuman) {
            return "($firstPartValue $operator $secondPartValue)"
        }

        return answer.toString()
    }
}