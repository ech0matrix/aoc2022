fun main() {
    fun parseElfPairs(input: List<String>): List<Pair<InclusiveRange, InclusiveRange>> {
        return input.map { line->
            val points = line.split(',', '-').map { it.toInt() }
            check(points.size == 4)
            Pair(InclusiveRange(points[0], points[1]), InclusiveRange(points[2], points[3]))
        }
    }

    fun part1(input: List<String>): Int {
        val elfPairs = parseElfPairs(input)
        return elfPairs.count { (elf1, elf2) -> elf1.fullyContains(elf2) || elf2.fullyContains(elf1) }
    }

    fun part2(input: List<String>): Int {
        val elfPairs = parseElfPairs(input)
        return elfPairs.count { (elf1, elf2) -> elf1.overlaps(elf2) }
    }

    val testInput = readInput("Day04_test")
    check(part1(testInput) == 2)
    check(part2(testInput) == 4)

    val input = readInput("Day04")
    println(part1(input))
    println(part2(input))
}
