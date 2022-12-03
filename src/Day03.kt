fun main() {
    fun getPriority(c: Char): Int {
        val asciiCode = c.code
        return if (asciiCode >= 'a'.code) {
            asciiCode - 'a'.code + 1
        } else {
            asciiCode - 'A'.code + 27
        }
    }

    fun part1(input: List<String>): Int {
        val compartments = input.map {
            val midIndex = it.length/2
            Pair(it.substring(0,midIndex),it.substring(midIndex))
        }
        val items: List<Char> = compartments.map { (c1, c2) ->
            c1.find { c2.contains(it) }!!
        }
        val priorities = items.map { getPriority(it) }
        return priorities.sum()
    }

    fun part2(input: List<String>): Int {
        val groups = input.chunked(3)
        val items: List<Char> = groups.map { chunk ->
            chunk[0].find {
                chunk[1].contains(it) && chunk[2].contains(it)
            }!!
        }
        val priorities = items.map { getPriority(it) }
        return priorities.sum()
    }

    val testInput = readInput("Day03_test")
    check(part1(testInput) == 157)
    check(part2(testInput) == 70)

    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}
