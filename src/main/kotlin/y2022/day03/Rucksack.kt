package y2022.day03

class Rucksack () {
    lateinit var contents: String

    lateinit var left: String
    lateinit var right: String



    constructor(contents: String) : this() {
        this.contents = contents

        val contentLength = contents.length
        val compartmentLength = contentLength/2
        left = contents.substring(IntRange(0, compartmentLength - 1))
        right = contents.substringAfter(left)
    }

    fun findDuplicate(): Char? {
        left.forEach { letter ->
            if (right.contains(letter)) {
                return letter
            }
        }

        return null
    }

    fun getUniqueLetters(): Set<Char> {
        return contents.toSet()
    }
}