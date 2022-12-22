fun main() {
    fun parseInstructions(input: List<String>): List<String> {
        val rawInstructions = input[input.size - 1]
        val distances = rawInstructions.split('L', 'R').filter { it.isNotEmpty() }
        val turns = rawInstructions.split('0', '1', '2', '3', '4', '5', '6', '7', '8', '9').filter { it.isNotEmpty() }
        return distances.zip(turns) { a, b -> listOf(a, b) }.flatten() + distances.last()
    }

    fun parseGrid(input: List<String>): Map<Coordinates, Char> {
        val grid = mutableMapOf<Coordinates, Char>()
        for (row in 0 until input.size - 2) {
            for (col in 0 until input[row].length) {
                if (input[row][col] != ' ') {
                    grid[Coordinates(row + 1, col + 1)] = input[row][col]
                }
            }
        }
        return grid
    }

    // Helpers
    fun getMinCol(row: Int, grid: Map<Coordinates, Char>) = grid.filterKeys { it.row == row }.minOf { (k, _) -> k.col }
    fun getMaxCol(row: Int, grid: Map<Coordinates, Char>) = grid.filterKeys { it.row == row }.maxOf { (k, _) -> k.col }
    fun getMinRow(col: Int, grid: Map<Coordinates, Char>) = grid.filterKeys { it.col == col }.minOf { (k, _) -> k.row }
    fun getMaxRow(col: Int, grid: Map<Coordinates, Char>) = grid.filterKeys { it.col == col }.maxOf { (k, _) -> k.row }

    fun part1(input: List<String>): Int {
        val instructions = parseInstructions(input)
        val grid = parseGrid(input)

        // Setup initial position and orientation
        var position = Coordinates(1, getMinCol(1, grid))
        val vectors = listOf(
            Coordinates(0, 1), // Right
            Coordinates(1, 0), // Down
            Coordinates(0, -1), // Left
            Coordinates(-1, 0)  // Up
        )
        var orientation = 0 // Right

        for (instruction in instructions) {
            //println("Instruction: $instruction")
            //println(" -Current position $position, orientation $orientation")
            if (instruction == "L") {
                orientation--
                if (orientation < 0) {
                    orientation = 3
                }
                //println("  Turn left to: $orientation")
            } else if (instruction == "R") {
                orientation++
                if (orientation >= 4) {
                    orientation = 0
                }
                //println("  Turn right to: $orientation")
            } else {
                // Walk
                val distance = instruction.toInt()
                //println("  Walk: $distance")
                for (d in 1..distance) {
                    //println("  Step $d")
                    var nextPosition = position.add(vectors[orientation])
                    var nextGridSpot = grid[nextPosition]
                    //println("  Next position: $nextPosition")
                    if (nextGridSpot == null) {
                        // Wrap around
                        nextPosition = when (orientation) {
                            0 -> Coordinates(nextPosition.row, getMinCol(nextPosition.row, grid))
                            1 -> Coordinates(getMinRow(nextPosition.col, grid), nextPosition.col)
                            2 -> Coordinates(nextPosition.row, getMaxCol(nextPosition.row, grid))
                            3 -> Coordinates(getMaxRow(nextPosition.col, grid), nextPosition.col)
                            else -> throw IllegalStateException("Bad orientation '$orientation'")
                        }
                        //println("  Calculating wraparound to $nextPosition")
                        nextGridSpot = grid[nextPosition]
                        if (nextGridSpot == null) {
                            throw IllegalStateException("After calculating wraparound of $position to $nextPosition, found no valid spot on grid")
                        }
                    }

                    if (nextGridSpot == '#') {
                        // Wall, have to stop
                        //println("  Wall, have to stop")
                        break
                    } else {
                        // Walk
                        //println("  Walk")
                        position = nextPosition
                    }
                }
            }
        }

//        grid[position] = 'O'
//        val maxRow = grid.maxOf { (k,_) -> k.row}
//        val maxCol = grid.maxOf { (k,_) -> k.col}
//        for(row in 1 .. maxRow) {
//            for(col in 1 .. maxCol) {
//                val tile = grid[Coordinates(row,col)]
//                if (tile == null) {
//                    print(' ')
//                } else {
//                    print(tile)
//                }
//            }
//            println()
//        }

        //println(position)
        return (position.row * 1000) + (position.col * 4) + orientation
    }

    fun transition(position: Coordinates, orientation: Int): Pair<Coordinates, Int> {
        if (orientation == 3) {
            // Up
            if (position.row == 1) {
                if (position.col in 51..100) {
                    // Face 1->6
                    return Pair(Coordinates(position.col+100, 1), 0)
                } else if (position.col in 101..150) {
                    // Face 2->6
                    return Pair(Coordinates(200, position.col-100), 3)
                }
            } else if (position.row == 101 && position.col in 1..50) {
                // Face 5->3
                return Pair(Coordinates(position.col+50, 51), 0)
            }
        } else if (orientation == 2) {
            // Left
            if (position.col == 51) {
                if (position.row in 1 .. 50) {
                    // Face 1->5
                    return Pair(Coordinates(151-position.row, 1), 0)
                } else if (position.row in 51 .. 100) {
                    // Face 3->5
                    return Pair(Coordinates(101, position.row-50), 1)
                }
            } else if (position.col == 1) {
                if (position.row in 101 .. 150) {
                    // Face 5->1
                    return Pair(Coordinates(151-position.row, 51), 0)
                } else if (position.row in 151 .. 200) {
                    // Face 6->1
                    return Pair(Coordinates(1, position.row-100), 1)
                }
            }
        } else if (orientation == 1) {
            // Down
            if (position.row == 50 && position.col in 101 .. 150) {
                // Face 2->3
                return Pair(Coordinates(position.col-50,100), 2)
            } else if (position.row == 150 && position.col in 51 .. 100) {
                // Face 4->6
                return Pair(Coordinates(position.col+100,50), 2)
            } else if (position.row == 200 && position.col in 1 .. 50) {
                // Face 6->2
                return Pair(Coordinates(1,position.col+100), 1)
            }
            //return Pair(Coordinates(), 0)
        } else if (orientation == 0) {
            // Right
            if (position.col == 150 && position.row in 1 .. 50) {
                // Face 2->4
                return Pair(Coordinates(151-position.row,100), 2)
            } else if (position.col == 100) {
                if (position.row in 51 .. 100) {
                    // Face 3->2
                    return Pair(Coordinates(50,position.row+50), 3)
                } else if (position.row in 101 .. 150) {
                    // Face 4->2
                    return Pair(Coordinates(151-position.row,150), 2)
                }
            } else if (position.col == 50 && position.row in 151 .. 200) {
                // Face 6->4
                return Pair(Coordinates(150,position.row-100), 3)
            }
        }
        throw IllegalArgumentException("Unknown transition for $position with orientation $orientation")
    }

    // Only works for how my specific input map is folded. Does not work for sample.
    fun part2(input: List<String>): Int {
        val instructions = parseInstructions(input)
        val grid = parseGrid(input)

        // Setup initial position and orientation
        var position = Coordinates(1, getMinCol(1, grid))
        val vectors = listOf(
            Coordinates( 0,  1), // Right
            Coordinates( 1,  0), // Down
            Coordinates( 0, -1), // Left
            Coordinates(-1,  0)  // Up
        )
        var orientation = 0 // Right

        for(instruction in instructions) {
            //println("Instruction: $instruction")
            //println(" -Current position $position, orientation $orientation")
            if (instruction == "L") {
                orientation--
                if (orientation < 0) {
                    orientation = 3
                }
                //println("  Turn left to: $orientation")
            } else if (instruction == "R") {
                orientation++
                if (orientation >= 4) {
                    orientation = 0
                }
                //println("  Turn right to: $orientation")
            } else {
                // Walk
                val distance = instruction.toInt()
                //println("  Walk: $distance")
                for(d in 1 .. distance) {
                    //println("  Step $d")
                    var nextOrientation = orientation
                    var nextPosition = position.add(vectors[orientation])
                    var nextGridSpot = grid[nextPosition]
                    //println("  Next position: $nextPosition")
                    if (nextGridSpot == null) {
                        // Wrap around
                        val (translatedPosition, translatedOrientation) = transition(position, orientation)
                        nextPosition = translatedPosition
                        nextOrientation = translatedOrientation
                        //println("  Calculating wraparound to $nextPosition")
                        nextGridSpot = grid[nextPosition]
                        if (nextGridSpot == null) {
                            throw IllegalStateException("After calculating wraparound of $position to $nextPosition, found no valid spot on grid")
                        }
                    }

                    if (nextGridSpot == '#') {
                        // Wall, have to stop
                        //println("  Wall, have to stop")
                        break
                    } else {
                        // Walk
                        //println("  Walk")
                        position = nextPosition
                        orientation = nextOrientation
                    }
                }
            }
        }

//        grid[position] = 'O'
//        val maxRow = grid.maxOf { (k,_) -> k.row}
//        val maxCol = grid.maxOf { (k,_) -> k.col}
//        for(row in 1 .. maxRow) {
//            for(col in 1 .. maxCol) {
//                val tile = grid[Coordinates(row,col)]
//                if (tile == null) {
//                    print(' ')
//                } else {
//                    print(tile)
//                }
//            }
//            println()
//        }

        //println(position)
        return (position.row*1000) + (position.col*4) + orientation
    }

    val testInput = readInput("Day22_test")
    check(part1(testInput) == 6032)

    val input = readInput("Day22")
    println(part1(input))
    println(part2(input))
}