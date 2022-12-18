import kotlin.math.abs

fun main() {
    fun part1(input: List<String>): Int {
        val chamber = mutableSetOf<Coordinates>()
        val jetPattern = input[0]
        var currentJet = 0
        for(shapeNum in 0 until 2022) {
            val nextShapeBottom = (chamber.minOfOrNull { it.row } ?: 0) - 4
            var currentShape = Shape.createShape(shapeNum, nextShapeBottom)
            while(true) {
                // Jet moves
                val jetDirection = jetPattern[currentJet % jetPattern.length]
                currentJet++
                if (jetDirection == '<') {
                    val movedShape = currentShape.moveLeft()
                    val movedPoints = movedShape.getPoints()
                    if (movedPoints.none { it.col <= 0 } && chamber.intersect(movedPoints).isEmpty()) {
                        currentShape = movedShape
                    }
                } else if (jetDirection == '>') {
                    val movedShape = currentShape.moveRight()
                    val movedPoints = movedShape.getPoints()
                    if (movedPoints.none { it.col >= 8 } && chamber.intersect(movedPoints).isEmpty()) {
                        currentShape = movedShape
                    }
                } else {
                    throw IllegalArgumentException("Unexpected jet direction: '$jetDirection'")
                }

                // Shape drops
                val droppedShape = currentShape.moveDown()
                val droppedPoints = droppedShape.getPoints()
                if (droppedPoints.none { it.row >= 0 } && chamber.intersect(droppedPoints).isEmpty()) {
                    currentShape = droppedShape
                } else {
                    // Shape stopped
                    chamber.addAll(currentShape.getPoints())
                    break
                }
            }
        }

        val topRow = chamber.minOf { it.row }

//        for(row in topRow until 0) {
//            print("|")
//            for(col in 1 .. 7) {
//                if (chamber.contains(Coordinates(row, col))) {
//                    print("#")
//                } else {
//                    print(".")
//                }
//            }
//            print("| ${abs(row)}")
//            println()
//        }
//        println("+-------+")

        return abs(topRow)
    }

    fun part2(input: List<String>): Int {
        val chamber = mutableSetOf<Coordinates>()
        val jetPattern = input[0]
        var currentJet = 0
        val log = mutableMapOf<String, Pair<Int, Int>>()
        for(shapeNum in 0 until 1010) {
            val nextShapeBottom = (chamber.minOfOrNull { it.row } ?: 0) - 4
            var currentShape = Shape.createShape(shapeNum, nextShapeBottom)
            while(true) {
                // Jet moves
                val jetNum = currentJet % jetPattern.length
                val jetDirection = jetPattern[jetNum]
                currentJet++
                if (jetDirection == '<') {
                    val movedShape = currentShape.moveLeft()
                    val movedPoints = movedShape.getPoints()
                    if (movedPoints.none { it.col <= 0 } && chamber.intersect(movedPoints).isEmpty()) {
                        currentShape = movedShape
                    }
                } else if (jetDirection == '>') {
                    val movedShape = currentShape.moveRight()
                    val movedPoints = movedShape.getPoints()
                    if (movedPoints.none { it.col >= 8 } && chamber.intersect(movedPoints).isEmpty()) {
                        currentShape = movedShape
                    }
                } else {
                    throw IllegalArgumentException("Unexpected jet direction: '$jetDirection'")
                }

                // Shape drops
                val droppedShape = currentShape.moveDown()
                val droppedPoints = droppedShape.getPoints()
                if (droppedPoints.none { it.row >= 0 } && chamber.intersect(droppedPoints).isEmpty()) {
                    currentShape = droppedShape
                } else {
                    // Shape stopped
                    chamber.addAll(currentShape.getPoints())
//                    val settledRow = currentShape.getPoints().minOf { it.row }
//                    val logKey = "ShapeNum=${shapeNum%5},JetNum=$jetNum"
//                    val logValue = Pair(shapeNum, settledRow)
//                    val repeatedValue = log[logKey]
//                    if (repeatedValue != null) {
//                        println("Repeat: $logKey")
//                        println("Shape ${repeatedValue.first}, Bottom Row ${repeatedValue.second}")
//                        println("Shape ${logValue.first}, Bottom Row ${logValue.second}")
//                    } else {
//                        log[logKey] = logValue
//                    }
                    break
                }
            }
        }

        val topRow = chamber.minOf { it.row }

        // Build row signatures
        val rows = mutableMapOf<Int, String>()
        val rowIndex = mutableMapOf<String, MutableSet<Int>>()
        for(row in topRow until 0) {
            var signature = ""
            for (col in 1 .. 7) {
                if (chamber.contains(Coordinates(row, col))) {
                    signature += "#"
                } else {
                    signature += "."
                }
            }

            rows[row] = signature
            val index = rowIndex[signature]
            if (index == null) {
                rowIndex[signature] = mutableSetOf(row)
            } else {
                index.add(row)
            }
        }

        for(row in topRow until 0) {
            val signature = rows[row]!!
            val rowList = rowIndex[signature]!!.map{abs(it)}.sorted()
            println("|$signature| (${abs(row)}) $rowList")
        }
        println("+-------+")

        //log.forEach { println(it) }

        return abs(topRow)
    }

    // Part2() doesn't actually outright solve the problem.
    // It provides tools to help draw and identify the cycle, which I did manually.
    // I then calculated the cycle and the size of it using an online big integer calculator.
    // Finally, I re-ran with a specific number of shapes to get the remainder outside of the cycle.

    val testInput = readInput("Day17_test")
    //check(part1(testInput) == 3068)
    //check(part2(testInput) == 3068)

    // Test input, part 2:
    //First 14 shapes, 25 rows
    //
    //Repeating 35 shapes, 53 rows
    //
    //1000000000000 total shapes
    //999999999985 shapes, 1514285714263 rows
    //+ first 14 shapes, 1514285714288 rows

    val input = readInput("Day17")
    //println(part1(input))
    println(part2(input))

    // Real input, Part 2:
    //Repeating 1745 shapes, 2750 rows
    //
    //1000000000000 total shapes
    //999999998990 shapes, 1575931230500 rows
    //1010 remaining
    //
    //1575931230500
    //+ 1576
    //1575931232076
}

abstract class Shape {
    abstract fun getPoints(): Set<Coordinates>

    abstract fun moveLeft(): Shape
    abstract fun moveRight(): Shape
    abstract fun moveDown(): Shape

    companion object {
        fun createShape(shapeNum: Int, bottomEdge: Int): Shape {
            return when(shapeNum % 5) {
                0 -> Shape0(bottomEdge, 3)
                1 -> Shape1(bottomEdge, 3)
                2 -> Shape2(bottomEdge, 3)
                3 -> Shape3(bottomEdge, 3)
                4 -> Shape4(bottomEdge, 3)
                else -> throw IllegalStateException("This can't happen")
            }
        }
    }
}

// ####
class Shape0(private val bottomEdge: Int, private val leftEdge: Int): Shape() {
    override fun getPoints() = setOf(
        Coordinates(bottomEdge, leftEdge), Coordinates(bottomEdge, leftEdge+1), Coordinates(bottomEdge, leftEdge+2), Coordinates(bottomEdge, leftEdge+3),
    )

    override fun moveLeft() = Shape0(bottomEdge, leftEdge-1)
    override fun moveRight() = Shape0(bottomEdge, leftEdge+1)
    override fun moveDown() = Shape0(bottomEdge+1, leftEdge)
}

// .#.
// ###
// .#.
class Shape1(private val bottomEdge: Int, private val leftEdge: Int): Shape() {
    override fun getPoints() = setOf(
                                                  Coordinates(bottomEdge-2, leftEdge+1),
        Coordinates(bottomEdge-1, leftEdge), Coordinates(bottomEdge-1, leftEdge+1), Coordinates(bottomEdge-1, leftEdge+2),
                                                  Coordinates(bottomEdge, leftEdge+1)
    )

    override fun moveLeft() = Shape1(bottomEdge, leftEdge-1)
    override fun moveRight() = Shape1(bottomEdge, leftEdge+1)
    override fun moveDown() = Shape1(bottomEdge+1, leftEdge)
}

// ..#
// ..#
// ###
class Shape2(private val bottomEdge: Int, private val leftEdge: Int): Shape() {
    override fun getPoints() = setOf(
                                                                                    Coordinates(bottomEdge-2, leftEdge+2),
                                                                                    Coordinates(bottomEdge-1, leftEdge+2),
        Coordinates(bottomEdge, leftEdge), Coordinates(bottomEdge, leftEdge+1), Coordinates(bottomEdge, leftEdge+2)
    )

    override fun moveLeft() = Shape2(bottomEdge, leftEdge-1)
    override fun moveRight() = Shape2(bottomEdge, leftEdge+1)
    override fun moveDown() = Shape2(bottomEdge+1, leftEdge)
}

// #
// #
// #
// #
class Shape3(private val bottomEdge: Int, private val leftEdge: Int): Shape() {
    override fun getPoints() = setOf(
        Coordinates(bottomEdge-3, leftEdge),
        Coordinates(bottomEdge-2, leftEdge),
        Coordinates(bottomEdge-1, leftEdge),
        Coordinates(bottomEdge, leftEdge)
    )

    override fun moveLeft() = Shape3(bottomEdge, leftEdge-1)
    override fun moveRight() = Shape3(bottomEdge, leftEdge+1)
    override fun moveDown() = Shape3(bottomEdge+1, leftEdge)
}

// ##
// ##
class Shape4(private val bottomEdge: Int, private val leftEdge: Int): Shape() {
    override fun getPoints() = setOf(
        Coordinates(bottomEdge-1, leftEdge), Coordinates(bottomEdge-1, leftEdge+1),
        Coordinates(bottomEdge, leftEdge), Coordinates(bottomEdge, leftEdge+1)
    )

    override fun moveLeft() = Shape4(bottomEdge, leftEdge-1)
    override fun moveRight() = Shape4(bottomEdge, leftEdge+1)
    override fun moveDown() = Shape4(bottomEdge+1, leftEdge)
}