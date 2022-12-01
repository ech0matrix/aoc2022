fun main() {
    fun part1(input: List<String>): Int {
        return input.joinToString(",")
            .replace(",,", ";")
            .split(';')
            .maxOf { calList ->
                calList.split(',')
                    .sumOf { calString -> calString.toInt() }
            }
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
