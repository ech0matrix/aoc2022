fun main() {
    fun parseInput(input: List<String>): Map<String, String> {
        return input.associate { line ->
            val split = line.split(": ")
            Pair(split[0], split[1])
        }
    }

    fun getMonkeyValue(monkeyName: String, monkeys: Map<String, String>): Long {
        val monkey = monkeys[monkeyName]!!
        val split = monkey.split(" ")
        if (split.size == 1) {
            return split[0].toLong()
        } else if (split.size != 3) {
            throw IllegalArgumentException("Expected 3 parts to the equation: $split")
        }

        val part1 = getMonkeyValue(split[0], monkeys)
        val part2 = getMonkeyValue(split[2], monkeys)

        return when (split[1]) {
            "+" -> part1 + part2
            "-" -> part1 - part2
            "*" -> part1 * part2
            "/" -> part1 / part2
            else -> throw IllegalArgumentException("Unknown operand: ${split[1]}")
        }
    }

    fun part1(input: List<String>): Long {
        val monkeys = parseInput(input)
        return getMonkeyValue("root", monkeys)
    }

    // This doesn't actually solve for "humn", but instead provides a tool for guess and check
    fun part2(input: List<String>, humanAdjustment: Long): Long {
        val monkeys = parseInput(input).mapValues { (k, v) ->
            if (k == "humn") {
                (v.toLong() + humanAdjustment).toString()
            } else {
                v
            }
        }

        val rootMonkey = monkeys["root"]!!
        val split = rootMonkey.split(" ")
        val part1 = getMonkeyValue(split[0], monkeys)
        val part2 = getMonkeyValue(split[2], monkeys)

        println("Term1: $part1")
        println("Term1: $part2")
        return if (part1 == part2) {
            val humanValue = monkeys["humn"]!!.toLong()
            println("Equal when human: $humanValue")
            humanValue
        } else {
            -1
        }
    }

    val testInput = readInput("Day21_test")
    check(part1(testInput) == 152L)
    check(part2(testInput, 296L) == 301L)

    val input = readInput("Day21")
    println("**Part1 answer: ${part1(input)}")
    println("**Part2 answer: ${part2(input, 3221245821750L)}")
}
