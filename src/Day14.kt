fun main() {
    fun setupCave(input: List<String>): MutableSet<Coordinates> {
        val cave = mutableSetOf<Coordinates>()
        for(formation in input) {
            val parts = formation
                .split(" -> ")
                .map { it.split(',') }
                .map { Coordinates(it[1].toInt(), it[0].toInt()) }
            for(i in 0 until parts.size-1) {
                val first = parts[i]
                val second = parts[i+1]
                if (first.col == second.col) {
                    // Vertical line
                    val startRow = minOf(first.row, second.row)
                    val endRow = maxOf(first.row, second.row)
                    for(row in startRow .. endRow) {
                        cave.add(Coordinates(row, first.col))
                    }
                } else if (first.row == second.row) {
                    // Horizontal line
                    val startCol = minOf(first.col, second.col)
                    val endCol = maxOf(first.col, second.col)
                    for(col in startCol .. endCol) {
                        cave.add(Coordinates(first.row, col))
                    }
                } else {
                    throw IllegalArgumentException("Expected lines to only be horizontal or vertical")
                }
            }
        }
        return cave
    }

    fun part1(input: List<String>): Int {
        val cave = setupCave(input)

        val sandStart = Coordinates(0, 500)
        var currentSand = sandStart
        var sandCount = 0
        val bottomRow = cave.maxOf { it.row }
        while(currentSand.row < bottomRow) {
            val down = currentSand.add(Coordinates(1, 0))
            val left = currentSand.add(Coordinates(1, -1))
            val right = currentSand.add(Coordinates(1, 1))

            if (!cave.contains(down)) {
                currentSand = down
            } else if (!cave.contains(left)) {
                currentSand = left
            } else if (!cave.contains(right)) {
                currentSand = right
            } else {
                // Sand can't move
                cave.add(currentSand)
                sandCount++
                currentSand = sandStart
            }
        }

        return sandCount
    }

    fun part2(input: List<String>): Int {
        val cave = setupCave(input)

        val sandStart = Coordinates(0, 500)
        var currentSand = sandStart
        var sandCount = 0
        val bottomRow = cave.maxOf { it.row }

        while(true) {
            val down = currentSand.add(Coordinates(1, 0))
            val left = currentSand.add(Coordinates(1, -1))
            val right = currentSand.add(Coordinates(1, 1))

            if (currentSand.row == bottomRow + 1) {
                // Sand sitting on floor
                cave.add(currentSand)
                sandCount++
                currentSand = sandStart
            } else if (!cave.contains(down)) {
                currentSand = down
            } else if (!cave.contains(left)) {
                currentSand = left
            } else if (!cave.contains(right)) {
                currentSand = right
            } else {
                // Sand can't move
                cave.add(currentSand)
                sandCount++
                if (currentSand == sandStart) {
                    // Current sand just filled the top spot. Done.
                    break
                }
                currentSand = sandStart
            }
        }

        return sandCount
    }

    val testInput = readInput("Day14_test")
    check(part1(testInput) == 24)
    check(part2(testInput) == 93)

    val input = readInput("Day14")
    println(part1(input))
    println(part2(input))
}
