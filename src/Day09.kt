import kotlin.math.abs

fun main() {
    fun getNumTailVisitedPositions(input: List<String>, ropeLength: Int): Int {
        val rope = MutableList(ropeLength) { Coordinates(0, 0) }

        val tailVisited = mutableSetOf<Coordinates>()
        tailVisited.add(rope.last())

        for(line in input) {
            val split = line.split(' ')
            val direction = split[0]
            val amount = split[1].toInt()
            val vector = when(direction) {
                "U" -> Coordinates(-1, 0)
                "D" -> Coordinates(1, 0)
                "L" -> Coordinates(0, -1)
                "R" -> Coordinates(0, 1)
                else -> throw IllegalArgumentException("Unexpected direction: $direction")
            }

            repeat(amount) {
                rope[0] = rope[0].add(vector)

                for(i in 1 until rope.size) {
                    val head = rope[i-1]
                    val tail = rope[i]

                    // Check distance
                    if ((abs(head.col - tail.col) > 1) || (abs(head.row - tail.row) > 1)) {
                        // Snap tail
                        val moveCol = if (head.col == tail.col) 0 else (head.col - tail.col) / abs(head.col - tail.col)
                        val moveRow = if (head.row == tail.row) 0 else (head.row - tail.row) / abs(head.row - tail.row)
                        val tailVector = Coordinates(moveRow, moveCol)
                        rope[i] = tail.add(tailVector)
                    }
                }

                tailVisited.add(rope.last())
            }
        }

        return tailVisited.size
    }

    val testInput = readInput("Day09_test")
    check(getNumTailVisitedPositions(testInput, 2) == 13)
    check(getNumTailVisitedPositions(testInput, 10) == 1)
    val testInput2 = readInput("Day09_test2")
    check(getNumTailVisitedPositions(testInput2, 10) == 36)

    val input = readInput("Day09")
    check(getNumTailVisitedPositions(input, 2) == 6030)
    println(getNumTailVisitedPositions(input, 2))
    println(getNumTailVisitedPositions(input, 10))
}
