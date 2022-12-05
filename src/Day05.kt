
fun main() {
    fun parseInput(input: List<String>): Pair<Map<Int, ArrayDeque<Char>>, List<Instruction>> {
        val inputSplitIndex = input.indexOf("")

        // Allocate stacks
        val stacksInput = input.subList(0, inputSplitIndex)
        val stackNumToIndex: Map<Int, Int> = stacksInput[stacksInput.size - 1]
            .split(' ')
            .filter { it.isNotEmpty() }
            .associate {
                Pair(it.toInt(), stacksInput[stacksInput.size - 1].indexOf(it[0]))
            }
        val stacks: Map<Int, ArrayDeque<Char>> = stackNumToIndex.keys.associateWith { ArrayDeque() }

        // Initialize stacks
        stacksInput.dropLast(1).reversed().forEach{ line ->
            stackNumToIndex.forEach{ (stackNum, index) ->
                val crate = line[index]
                if (crate != ' ') {
                    stacks[stackNum]!!.addLast(crate)
                }
            }
        }

        // Parse instructions
        val instructionInput = input.subList(inputSplitIndex+1, input.size)
        val instructions = instructionInput.map {
            val split = it.split(' ')
            // Example: move 1 from 2 to 1
            check(split[0] == "move")
            val amount = split[1].toInt()
            check(split[2] == "from")
            val source = split[3].toInt()
            check(split[4] == "to")
            val destination = split[5].toInt()
            Instruction(amount, source, destination)
        }

        return Pair(stacks, instructions)
    }

    fun buildOutput(stacks: Map<Int, ArrayDeque<Char>>): String {
        return stacks.entries.sortedBy { it.key }.map { it.value.last() }.joinToString("")
    }

    fun part1(input: List<String>): String {
        val (stacks, instructions) = parseInput(input)

        // Run instructions
        instructions.forEach {
            val sourceStack = stacks[it.source]!!
            val destinationStack = stacks[it.destination]!!
            repeat(it.amount) {
                val crate = sourceStack.removeLast()
                destinationStack.addLast(crate)
            }
        }

        // Build output
        return buildOutput(stacks)
    }

    fun part2(input: List<String>): String {
        val (stacks, instructions) = parseInput(input)

        // Run instructions
        instructions.forEach {
            val sourceStack = stacks[it.source]!!
            val destinationStack = stacks[it.destination]!!
            val tempStack = ArrayDeque<Char>()
            repeat(it.amount) {
                val crate = sourceStack.removeLast()
                tempStack.addLast(crate)
            }
            repeat(it.amount) {
                val crate = tempStack.removeLast()
                destinationStack.addLast(crate)
            }
        }

        // Build output
        return buildOutput(stacks)
    }

    val testInput = readInput("Day05_test")
    check(part1(testInput) == "CMZ")
    check(part2(testInput) == "MCD")

    val input = readInput("Day05")
    println(part1(input))
    println(part2(input))
}

data class Instruction(
    val amount: Int,
    val source: Int,
    val destination: Int
)