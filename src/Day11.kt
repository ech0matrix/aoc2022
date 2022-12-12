fun main() {
    fun solve(monkeys: List<Monkey>, numRounds: Int): Long {
        for(i in 0 until numRounds) {
            for(monkey in monkeys) {
                monkey.testItems(monkeys)
            }
        }
        val inspectCounts = monkeys.map { it.numInspected }
        println(inspectCounts)
        println()
        val shenanigans = inspectCounts.sortedDescending().take(2).reduce{ a, b -> a * b }
        println(shenanigans)
        println()
        return shenanigans
    }

    val testDivisor = 23L * 19L * 13L * 17L
    val testInput = listOf(
        Monkey(
            mutableListOf(79L, 98L),
            { it * 19L },
            { it / 3L },
            { it % 23L == 0L},
            2,
            3
        ),
        Monkey(
            mutableListOf(54L,65L,75L,74L),
            { it + 6L },
            { it / 3L },
            { it % 19L == 0L},
            2,
            0
        ),
        Monkey(
            mutableListOf(79L,60L,97L),
            { it * it },
            { it / 3L },
            { it % 13L == 0L},
            1,
            3
        ),
        Monkey(
            mutableListOf(74L),
            { it + 3L },
            { it / 3L },
            { it % 17L == 0L},
            0,
            1
        )
    )
    check(solve(testInput, 20) == 10605L)
    val testInput2 = listOf(
        Monkey(
            mutableListOf(79L, 98L),
            { it * 19L },
            { item -> item % testDivisor },
            { it % 23L == 0L},
            2,
            3
        ),
        Monkey(
            mutableListOf(54L,65L,75L,74L),
            { it + 6L },
            { item -> item % testDivisor },
            { it % 19L == 0L},
            2,
            0
        ),
        Monkey(
            mutableListOf(79L,60L,97L),
            { it * it },
            { item -> item % testDivisor },
            { it % 13L == 0L},
            1,
            3
        ),
        Monkey(
            mutableListOf(74L),
            { it + 3L },
            { item -> item % testDivisor },
            { it % 17L == 0L},
            0,
            1
        )
    )
    check(solve(testInput2, 10000) == 2713310158L)


    //
    // Omitting hard-coded inputs as part of check-in
    //
    // val input = listOf<Monkey>(...)
    // val divisor = ...
    // val input2 = listOf<Monkey>(...)

    // println(solve(input, 20))
    // println(solve(input2, 10000))
}

data class Monkey(
    private val items: MutableList<Long>,
    private val operation: (Long) -> (Long),
    private val reduceWorry: (Long) -> (Long),
    private val test: (Long) -> Boolean,
    private val trueMonkey: Int,
    private val falseMonkey: Int
) {
    var numInspected = 0L

    fun testItems(monkeys: List<Monkey>) {
        while (items.isNotEmpty()) {
            // Per item:
            // 1) Inspect operation
            // 2) Reduce worry (divide 3)
            // 3) Test item & throw
            val inspectedItem = items.removeFirst()
                .let { operation(it) }
                .let { reduceWorry(it) }
            check (inspectedItem >= 0)
            val throwToMonkey = if (test(inspectedItem)) {
                trueMonkey
            } else {
                falseMonkey
            }
            monkeys[throwToMonkey].items.add(inspectedItem)
            numInspected++
        }
    }
}