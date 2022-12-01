fun main() {
    fun parseGroups(input: List<String>): List<List<String>> {
        val splitIndex = input.indexOf("")
        if (splitIndex == -1)
            return listOf(input)
        val first: List<String> = input.subList(0, splitIndex)
        val remainder: List<List<String>> = parseGroups(input.subList(splitIndex+1, input.size))
        return remainder.plusElement(first)
    }

    fun part1(input: List<String>): Int {
        return parseGroups(input).maxOf { group -> group.sumOf { it.toInt() } }
    }

    fun part2(input: List<String>): Int {
        return input.joinToString(",")
            .replace(",,", ";")
            .split(';')
            .map { calList ->
                calList.split(',')
                    .sumOf { calString -> calString.toInt() }
            }.sortedDescending()
            .subList(0, 3)
            .sum()
    }

    val testInput = readInput("Day01_test")
    check(part1(testInput) == 24000)
    check(part2(testInput) == 45000)

    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}
