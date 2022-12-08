fun main() {
    fun parseInput(input: List<String>): Map<Coordinates, Tree> {
        val rows = input.size
        val cols = input[0].length

        val grid = mutableMapOf<Coordinates, Tree>()
        for(row in 0 until rows) {
            for (col in 0 until cols) {
                val position = Coordinates(row, col)
                val tree = Tree(position, input[row][col].toString().toInt())
                grid[position] = tree
            }
        }
        return grid
    }

    fun part1(input: List<String>): Int {
        val grid = parseInput(input)
        return grid.count { (_, tree) -> tree.isVisible(grid) }
    }

    fun part2(input: List<String>): Int {
        val grid = parseInput(input)
        return grid.map { (_, tree) -> tree.getScenicScore(grid) }.max()
    }


    val testInput = readInput("Day08_test")
    check(part1(testInput) == 21)
    check(part2(testInput) == 8)

    val input = readInput("Day08")
    println(part1(input))
    println(part2(input))
}

data class Coordinates(
    val row: Int,
    val col: Int
)

data class Tree(
    val position: Coordinates,
    val height: Int
) {
    private var tallestHeightToWest: Int? = null
    private var tallestHeightToEast: Int? = null
    private var tallestHeightToNorth: Int? = null
    private var tallestHeightToSouth: Int? = null

    private fun getTallestHeightToWest(grid: Map<Coordinates, Tree>): Int {
        if (tallestHeightToWest == null) {
            val westPosition = position.copy(col = position.col - 1)
            val westNeighbor = grid[westPosition]
            tallestHeightToWest = maxOf(westNeighbor?.height ?: -1, westNeighbor?.getTallestHeightToWest(grid) ?: -1)
        }
        return tallestHeightToWest!!
    }

    private fun getTallestHeightToEast(grid: Map<Coordinates, Tree>): Int {
        if (tallestHeightToEast == null) {
            val eastPosition = position.copy(col = position.col + 1)
            val eastNeighbor = grid[eastPosition]
            tallestHeightToEast = maxOf(eastNeighbor?.height ?: -1, eastNeighbor?.getTallestHeightToEast(grid) ?: -1)
        }
        return tallestHeightToEast!!
    }

    private fun getTallestHeightToNorth(grid: Map<Coordinates, Tree>): Int {
        if (tallestHeightToNorth == null) {
            val northPosition = position.copy(row = position.row - 1)
            val northNeighbor = grid[northPosition]
            tallestHeightToNorth = maxOf(northNeighbor?.height ?: -1, northNeighbor?.getTallestHeightToNorth(grid) ?: -1)
        }
        return tallestHeightToNorth!!
    }

    private fun getTallestHeightToSouth(grid: Map<Coordinates, Tree>): Int {
        if (tallestHeightToSouth == null) {
            val southPosition = position.copy(row = position.row + 1)
            val southNeighbor = grid[southPosition]
            tallestHeightToSouth = maxOf(southNeighbor?.height ?: -1, southNeighbor?.getTallestHeightToSouth(grid) ?: -1)
        }
        return tallestHeightToSouth!!
    }

    fun isVisible(grid: Map<Coordinates, Tree>): Boolean {
        val neighborHeights = listOf(
            getTallestHeightToWest(grid),
            getTallestHeightToEast(grid),
            getTallestHeightToNorth(grid),
            getTallestHeightToSouth(grid)
        )
        return neighborHeights.any { it < height }
    }

    private fun distanceWestUntil(maxHeight: Int, grid: Map<Coordinates, Tree>): Int {
        val westPosition = position.copy(col = position.col - 1)
        val westNeighbor = grid[westPosition]
        return if (westNeighbor == null) {
            0
        } else if (westNeighbor.height >= maxHeight) {
            1
        } else {
            1 + westNeighbor.distanceWestUntil(maxHeight, grid)
        }
    }

    private fun distanceEastUntil(maxHeight: Int, grid: Map<Coordinates, Tree>): Int {
        val eastPosition = position.copy(col = position.col + 1)
        val eastNeighbor = grid[eastPosition]
        return if (eastNeighbor == null) {
            0
        } else if (eastNeighbor.height >= maxHeight) {
            1
        } else {
            1 + eastNeighbor.distanceEastUntil(maxHeight, grid)
        }
    }

    private fun distanceNorthUntil(maxHeight: Int, grid: Map<Coordinates, Tree>): Int {
        val northPosition = position.copy(row = position.row - 1)
        val northNeighbor = grid[northPosition]
        return if (northNeighbor == null) {
            0
        } else if (northNeighbor.height >= maxHeight) {
            1
        } else {
            1 + northNeighbor.distanceNorthUntil(maxHeight, grid)
        }
    }

    private fun distanceSouthUntil(maxHeight: Int, grid: Map<Coordinates, Tree>): Int {
        val southPosition = position.copy(row = position.row + 1)
        val southNeighbor = grid[southPosition]
        return if (southNeighbor == null) {
            0
        } else if (southNeighbor.height >= maxHeight) {
            1
        } else {
            1 + southNeighbor.distanceSouthUntil(maxHeight, grid)
        }
    }

    fun getScenicScore(grid: Map<Coordinates, Tree>): Int {
        val west = distanceWestUntil(height, grid)
        val east = distanceEastUntil(height, grid)
        val north = distanceNorthUntil(height, grid)
        val south = distanceSouthUntil(height, grid)
        return west * east * north * south
    }
}