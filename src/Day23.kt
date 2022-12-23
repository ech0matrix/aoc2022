fun main() {
    fun print(elves: Set<Coordinates>) {
//        for(row in elves.minOf { it.row } .. elves.maxOf { it.row }) {
//            for(col in elves.minOf { it.col } .. elves.maxOf { it.col }) {
        for(row in -2 .. 9) {
            for(col in -3 .. 10) {
                if (elves.contains(Coordinates(row,col))) {
                    print('#')
                } else {
                    print('.')
                }
            }
            println()
        }
        println()
    }

    fun parse(input: List<String>): MutableSet<Coordinates> {
        val elves = mutableSetOf<Coordinates>()
        for(row in 0 until input.size) {
            for(col in 0 until input[row].length) {
                if (input[row][col] == '#') {
                    elves.add(Coordinates(row,col))
                }
            }
        }
        return elves
    }

    val standingChecks = listOf(
        Coordinates(-1,-1),Coordinates(-1,0),Coordinates(-1,1),
        Coordinates(0,-1),                            Coordinates(0,1),
        Coordinates(1,-1), Coordinates(1,0), Coordinates(1,1)
    )
    val walkingChecks = listOf(
        listOf(Coordinates(-1,-1),Coordinates(-1,0),Coordinates(-1,1)),// North
        listOf(Coordinates(1,-1), Coordinates(1,0), Coordinates(1,1)), // South
        listOf(Coordinates(-1,-1),Coordinates(0,-1),Coordinates(1,-1)),// West
        listOf(Coordinates(-1,1),Coordinates(0,1),Coordinates(1,1))    // East
    )
    val walkDirectionIndex = 1

    fun move(elves: MutableSet<Coordinates>, walkingCheckIndex: Int): Boolean {
        val totalElves = elves.size
        var didMove = false

        // Key = proposed positions
        // Value = previous positions
        val proposals = mutableMapOf<Coordinates, MutableSet<Coordinates>>()
        fun addProposal(proposal: Coordinates, elf: Coordinates) {
            val p = proposals[proposal]
            if (p == null) {
                proposals[proposal] = mutableSetOf(elf)
            } else {
                p.add(elf)
            }
        }

        for(elf in elves) {
            if(standingChecks.map { elf.add(it) }.intersect(elves).isEmpty()) {
                // Don't walk
                addProposal(elf, elf)
            } else {
                var hasProposed = false
                for(i in walkingCheckIndex until walkingCheckIndex+4) {
                    val checkIndex = i%4
                    if(walkingChecks[checkIndex].map { elf.add(it) }.intersect(elves).isEmpty()) {
                        addProposal(walkingChecks[checkIndex][walkDirectionIndex].add(elf), elf)
                        hasProposed = true
                        break
                    }
                }
                if(!hasProposed) {
                    addProposal(elf,elf)
                }
            }
        }

        elves.clear()
        for((proposal, previous) in proposals) {
            if (previous.size == 1) {
                elves.add(proposal)
                if(proposal != previous.first()) {
                    didMove = true
                }
            } else {
                elves.addAll(previous)
            }
        }

        //print(elves)
        check(totalElves == elves.size)
        return didMove
    }

    fun part1(input: List<String>): Int {
        val elves = parse(input)

        var walkingCheckIndex = 0
        //print(elves)

        repeat(10) {
            move(elves, walkingCheckIndex)
            walkingCheckIndex++
        }

        val minCol = elves.minOf { it.col }
        val maxCol = elves.maxOf { it.col }
        val minRow = elves.minOf { it.row }
        val maxRow = elves.maxOf { it.row }
        val area = (maxCol - minCol + 1) * (maxRow - minRow + 1)

//        println("Rows: $minRow -> $maxRow")
//        println("Cols: $minCol -> $maxCol")

        return area - elves.size
    }

    fun part2(input: List<String>): Int {
        val elves = parse(input)

        var walkingCheckIndex = 0
        while(move(elves, walkingCheckIndex)) {
            walkingCheckIndex++
        }

        return walkingCheckIndex+1
    }

//    fun part2(input: List<String>): Int {
//        return input.size
//    }

    val testInput = readInput("Day23_test")
    check(part1(testInput) == 110)
    check(part2(testInput) == 20)

    val input = readInput("Day23")
    println(part1(input))
    println(part2(input))
}
