import java.rmi.UnexpectedException

fun main() {
    fun setupGrid(input: List<String>, grid: MutableMap<Coordinates, GridPosition>): Pair<Coordinates, Coordinates> {
        var start = Coordinates(-1,-1)
        var end = Coordinates(-1,-1)

        for(row in input.indices) {
            for(col in input[0].indices) {
                val height = when(input[row][col]) {
                    'S' -> { start = Coordinates(row,col); 0 }
                    'E' -> { end = Coordinates(row,col); 25 }
                    else -> { input[row][col].code - 'a'.code }
                }
                val coord = Coordinates(row, col)
                val pos = GridPosition(coord, height)
                grid[Coordinates(row, col)] = pos
            }
        }

        return Pair(start, end)
    }

    fun solveMinSteps(active: MutableSet<Path>, grid: MutableMap<Coordinates, GridPosition>, end: Coordinates): Int {
        val visited = mutableSetOf<Coordinates>()
        while (active.isNotEmpty()) {
            val current = active.minByOrNull { it.cost }!!
            if (current.position.coordinates == end) {
                return current.cost
            }

            visited.add(current.position.coordinates)
            active.remove(current)

            val possibilities = grid[current.position.coordinates]!!.getPossibleMoves(grid).filter { !visited.contains(it.coordinates) }
            for(possibility in possibilities) {
                val alreadyActive = active.find { it.position == possibility }
                if (alreadyActive != null) {
                    if (alreadyActive.cost > current.cost+1) {
                        active.remove(alreadyActive)
                        active.add(Path(possibility, current.cost+1))
                    }
                } else {
                    active.add(Path(possibility, current.cost+1))
                }
            }
        }

        throw UnexpectedException("Didn't find result")
    }

    fun part1(input: List<String>): Int {
        val grid = mutableMapOf<Coordinates, GridPosition>()
        val (start, end) = setupGrid(input, grid)

        val active = mutableSetOf(Path(grid[start]!!, 0))
        return solveMinSteps(active, grid, end)
    }

    fun part2(input: List<String>): Int {
        val grid = mutableMapOf<Coordinates, GridPosition>()
        val (start, end) = setupGrid(input, grid)

        val active = grid.filter { (coord, pos) -> pos.height == 0 }.map { (coord, pos) -> Path(pos, 0) }.toMutableSet()
        return solveMinSteps(active, grid, end)
    }

    val testInput = readInput("Day12_test")
    check(part1(testInput) == 31)
    check(part2(testInput) == 29)

    val input = readInput("Day12")
    println(part1(input))
    println(part2(input))
}

data class GridPosition(
    val coordinates: Coordinates,
    val height: Int
) {
    fun getPossibleMoves(grid: Map<Coordinates, GridPosition>): List<GridPosition> {
        val north = grid[coordinates.add(Coordinates(-1, 0))]
        val south = grid[coordinates.add(Coordinates(1, 0))]
        val west = grid[coordinates.add(Coordinates(0, -1))]
        val east = grid[coordinates.add(Coordinates(0, 1))]
        return listOfNotNull(north, south, west, east).filter { it.height <= height+1 }
    }
}

data class Path(
    val position: GridPosition,
    val cost: Int
)