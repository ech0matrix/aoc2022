fun main() {
    fun findMarker(input: String, numDistinct: Int): Int {
        for(index in (numDistinct-1) until input.length) {
            val set = (0 until numDistinct).map { i -> input[index - i] }.toSet()
            if (set.size == numDistinct) {
                return index+1
            }
        }
        throw IllegalStateException("Reached end of string without finding marker")
    }

    fun part1(input: String) = findMarker(input, 4)

    fun part2(input: String) = findMarker(input, 14)

    val testInput = readInput("Day06_test")
    val expectedResultsPart1 = listOf(7, 5, 6, 10, 11)
    val testsPart1 = testInput.mapIndexed{ index, input -> Pair(input, expectedResultsPart1[index]) }
    testsPart1.forEach {
        check(part1(it.first) == it.second)
    }
    val expectedResultsPart2 = listOf(19, 23, 23, 29, 26)
    val testsPart2 = testInput.mapIndexed{ index, input -> Pair(input, expectedResultsPart2[index]) }
    testsPart2.forEach {
        check(part2(it.first) == it.second)
    }

    val input = readInput("Day06")[0]
    println(part1(input))
    println(part2(input))
}
