import java.util.PriorityQueue

fun main() {
    fun part1(input: List<String>): Long {
        // Set border
        val borderMin = Coordinates(0, 0)
        val borderMax = Coordinates(input.size-1, input[0].length-1)

        // Set start/end
        val start = borderMin.add(Coordinates(0, 1))
        val end = borderMax.add(Coordinates(0, -1))
        val startNext = start.add(Coordinates(1, 0))
        val endPrecursor = end.add(Coordinates(-1, 0))

        // Parse blizzards
        val blizzards = mutableListOf<Blizzard>()
        for(row in borderMin.row+1 .. borderMax.row-1) {
            for(col in borderMin.col+1 .. borderMax.col-1) {
                if (input[row][col] != '.') {
                    val orientation = when(input[row][col]) {
                        '>' -> 0
                        'v' -> 1
                        '<' -> 2
                        '^' -> 3
                        else -> throw IllegalArgumentException("Unexpected orientation symbol: ${input[row][col]}")
                    }
                    blizzards.add(Blizzard(
                        Coordinates(row, col),
                        orientation,
                        borderMin,
                        borderMax
                    ))
                }
            }
        }
        val blizzardManager = BlizzardStateManager(blizzards)

        var quickestTime = Long.MAX_VALUE
        val visited = mutableSetOf<ValleyTile>()
        val active = PriorityQueue<ValleyTile>(compareBy { it.costDistance })
        active.offer(ValleyTile(start, 0, start.manhattanDistance(end).toLong()))
        while(!active.isEmpty()) {
            // Get next tile
            val current = active.poll()!!
            //println(current)
            if (current.position == end) {
                // Found the end
                quickestTime = minOf(current.time, quickestTime)
            }
            visited.add(current)

            // Update blizzards
            val time = current.time + 1
            val blizzardState = blizzardManager.get(time)

            if (time < quickestTime) {
                // Find possible moves
                val possibleMoves = mutableListOf<ValleyTile>()
                if (!blizzardState.contains(current.position)) {
                    // Standing still
                    possibleMoves.add(current.copy(time = time))
                }

                if (current.position == start && !blizzardState.contains(startNext)) {
                    // Move out of start
                    possibleMoves.add(ValleyTile(startNext, time, startNext.manhattanDistance(end).toLong()))
                } else if (current.position == endPrecursor) {
                    // Move to end
                    quickestTime = minOf(time, quickestTime)
                } else {
                    // All cardinal directions
                    val directionalMoves = listOf(
                        current.position.add(Coordinates(0, 1)),
                        current.position.add(Coordinates(0, -1)),
                        current.position.add(Coordinates(1, 0)),
                        current.position.add(Coordinates(-1, 0))
                    )
                    for (move in directionalMoves) {
                        // Use moves within border and no blizzards
                        if (move.row > borderMin.row && move.row < borderMax.row
                            && move.col > borderMin.col && move.col < borderMax.col
                            && !blizzardState.contains(move)
                        ) {
                            possibleMoves.add(ValleyTile(move, time, move.manhattanDistance(end).toLong()))
                        }
                    }
                }

                // Check for repeated moves
                possibleMoves.filter { !visited.contains(it) && !active.contains(it) }.forEach { active.offer(it) }
            }
        }

        return quickestTime
    }

    fun part2(input: List<String>): Long {
        // Set border
        val borderMin = Coordinates(0, 0)
        val borderMax = Coordinates(input.size-1, input[0].length-1)

        // Set start/end
        val start = borderMin.add(Coordinates(0, 1))
        val end = borderMax.add(Coordinates(0, -1))
        val startNext = start.add(Coordinates(1, 0))
        val endPrecursor = end.add(Coordinates(-1, 0))

        val lapDistance = start.manhattanDistance(end).toLong()

        // Parse blizzards
        val blizzards = mutableListOf<Blizzard>()
        for(row in borderMin.row+1 .. borderMax.row-1) {
            for(col in borderMin.col+1 .. borderMax.col-1) {
                if (input[row][col] != '.') {
                    val orientation = when(input[row][col]) {
                        '>' -> 0
                        'v' -> 1
                        '<' -> 2
                        '^' -> 3
                        else -> throw IllegalArgumentException("Unexpected orientation symbol: ${input[row][col]}")
                    }
                    blizzards.add(Blizzard(
                        Coordinates(row, col),
                        orientation,
                        borderMin,
                        borderMax
                    ))
                }
            }
        }
        val blizzardManager = BlizzardStateManager(blizzards)

        var quickestTime = Long.MAX_VALUE
        val visited = mutableSetOf<ValleyTile2>()
        val active = PriorityQueue<ValleyTile2>(compareBy { it.costDistance })
        active.offer(ValleyTile2(start, 0, lapDistance*3L))
        while(!active.isEmpty()) {
            // Get next tile
            val current = active.poll()!!
            //println(current)
            if (current.position == end && current.reachedStart) {
                // Found the end
                quickestTime = minOf(current.time, quickestTime)
                //return quickestTime // -- For debugging only
            }
            visited.add(current)

            // Update blizzards
            val time = current.time + 1
            val blizzardState = blizzardManager.get(time)

            if (time < quickestTime) {
                // Find possible moves
                val possibleMoves = mutableListOf<ValleyTile2>()
                if (!blizzardState.contains(current.position)) {
                    // Standing still
                    possibleMoves.add(current.copy(time = time))
                }

                if (current.position == start && !blizzardState.contains(startNext)) {
                    // Move out of start
                    possibleMoves.add(ValleyTile2(startNext, time, startNext.manhattanDistance(end).toLong() + if (current.reachedEnd) 0 else lapDistance*2L, current.reachedEnd, current.reachedStart))
                } else if (current.position == startNext && current.reachedEnd && !current.reachedStart) {
                    val nextMove = ValleyTile2(start, time, lapDistance, reachedEnd = true, reachedStart = true)
                    possibleMoves.add(nextMove)
//                    active.clear() // -- For debugging only
//                    println("Reached start again, second lap")
//                    println("   Current: $current")
//                    println("   Next: $nextMove")
                } else if (current.position == endPrecursor && !current.reachedEnd) {
                    val nextMove = ValleyTile2(end, time, lapDistance*2L, reachedEnd = true, reachedStart = false)
                    possibleMoves.add(nextMove)
//                    active.clear() // -- For debugging only
//                    println("Reached end, first lap")
//                    println("   Current: $current")
//                    println("   Next: $nextMove")
                } else if (current.position == endPrecursor && current.reachedStart) {
                    quickestTime = minOf(time, quickestTime)
                    //return quickestTime // -- For debugging only
                } else if (current.position == end && !blizzardState.contains(endPrecursor)) {
                    possibleMoves.add(ValleyTile2(endPrecursor, time, endPrecursor.manhattanDistance(start).toLong() + lapDistance, current.reachedEnd, current.reachedStart))
                } else {
                    // All cardinal directions
                    val directionalMoves = listOf(
                        current.position.add(Coordinates(0, 1)),
                        current.position.add(Coordinates(0, -1)),
                        current.position.add(Coordinates(1, 0)),
                        current.position.add(Coordinates(-1, 0))
                    )
                    for (move in directionalMoves) {
                        // Use moves within border and no blizzards
                        if (move.row > borderMin.row && move.row < borderMax.row
                            && move.col > borderMin.col && move.col < borderMax.col
                            && !blizzardState.contains(move)
                        ) {
                            val distance = when {
                                current.reachedEnd -> move.manhattanDistance(start).toLong() + lapDistance
                                current.reachedStart -> move.manhattanDistance(end).toLong()
                                else -> move.manhattanDistance(end).toLong() + lapDistance*2L
                            }
                            possibleMoves.add(ValleyTile2(move, time, distance, current.reachedEnd, current.reachedStart))
                        }
                    }
                }

                // Check for repeated moves
                possibleMoves.filter { !visited.contains(it) && !active.contains(it) }.forEach { active.offer(it) }
            }
        }

        return quickestTime
    }

    val testInput = readInput("Day24_test")
    check(part1(testInput) == 18L)
    check(part2(testInput) == 54L)

    val input = readInput("Day24")

    val startTime1 = java.util.Date().time
    val part1Answer = part1(input)
    val endTime1 = java.util.Date().time
    println("Part 1 (${endTime1-startTime1}ms): $part1Answer")

    val startTime2 = java.util.Date().time
    val part2Answer = part2(input)
    val endTime2 = java.util.Date().time
    println("Part 2 (${endTime2-startTime2}ms): $part2Answer")
}

data class ValleyTile(
    val position: Coordinates,
    val time: Long,
    val distance: Long
) {
    val costDistance = time * distance
}

data class ValleyTile2(
    val position: Coordinates,
    val time: Long,
    val distance: Long,
    val reachedEnd: Boolean = false,
    val reachedStart: Boolean = false
) {
    val costDistance = time * distance
}

class BlizzardStateManager(
    initialState: List<Blizzard>
) {
    private var currentTime = 0L
    private var currentState = initialState
    private val stateCache = mutableMapOf<Long, Set<Coordinates>>()

    fun get(time: Long): Set<Coordinates> {
        val cachedState = stateCache[time]
        if (cachedState != null) {
            return cachedState
        }

        for(t in currentTime+1 .. time) {
            currentState = currentState.map { it.move() }
            val newCachedState = currentState.map{it.position}.toSet()
            stateCache[t] = newCachedState
        }
        currentTime = time
        return stateCache[time]!!
    }
}

data class Blizzard(
    val position: Coordinates,
    val orientation: Int,
    val borderMin: Coordinates,
    val borderMax: Coordinates
) {
    companion object {
        private val vectors = listOf(
            Coordinates(0, 1), // Right
            Coordinates(1, 0), // Down
            Coordinates(0, -1), // Left
            Coordinates(-1, 0)  // Up
        )
    }

    fun move(): Blizzard {
        var next = position.add(vectors[orientation])
        if (next.col == borderMin.col && orientation == 2) {
            // Left border
            next = Coordinates(next.row, borderMax.col-1)
        } else if (next.col == borderMax.col && orientation == 0) {
            // Right border
            next = Coordinates(next.row, borderMin.col+1)
        } else if (next.row == borderMin.row && orientation == 3) {
            // Top border
            next = Coordinates(borderMax.row-1, next.col)
        } else if (next.row == borderMax.row && orientation == 1) {
            next = Coordinates(borderMin.row+1, next.col)
        }
        return this.copy(position = next)
    }
}