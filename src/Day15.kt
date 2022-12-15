import java.util.Date
import kotlin.math.abs

fun main() {
    fun parseSensorBeaconPairs(input: List<String>): List<Pair<Coordinates, Coordinates>> {
        return input
            .map { it.substring(12).split(": closest beacon is at x=") }
            .map {
                val sensorRaw = it[0].split(", y=")
                val beaconRaw = it[1].split(", y=")
                val sensor = Coordinates(sensorRaw[1].toInt(), sensorRaw[0].toInt())
                val beacon = Coordinates(beaconRaw[1].toInt(), beaconRaw[0].toInt())
                Pair(sensor, beacon)
            }
        }

    fun part1(input: List<String>, targetRow: Int): Int {
        val pairs = parseSensorBeaconPairs(input)

        val ranges = mutableListOf<InclusiveRange>()
        for((sensor, beacon) in pairs) {
            val distanceToBeacon = sensor.manhattanDistance(beacon)
            val distanceToRow = abs(targetRow - sensor.row)
            val colsInRow = distanceToBeacon - distanceToRow
            if (colsInRow >= 0) {
                val midCol = sensor.col
                val left = midCol - colsInRow
                val right = midCol + colsInRow
                val leftAdjust  = left  + if(beacon.row == targetRow && left == beacon.col)  1 else 0
                val rightAdjust = right - if(beacon.row == targetRow && right == beacon.col) 1 else 0

                var range = InclusiveRange(leftAdjust, rightAdjust)
                var overlaps = ranges.find { it.overlaps(range) }
                while (overlaps != null) {
                    ranges.remove(overlaps)
                    range = overlaps.merge(range)
                    overlaps = ranges.find { it.overlaps(range) }
                }
                ranges.add(range)
            }
        }

        val result = ranges.sumOf { it.y - it.x + 1 }
        return result
    }

    fun part2(input: List<String>, limit: InclusiveRange): Long {
        val pairs = parseSensorBeaconPairs(input)

        for(targetRow in limit.x .. limit.y) {
            val ranges = mutableListOf<InclusiveRange>()
            for ((sensor, beacon) in pairs) {
                val distanceToBeacon = sensor.manhattanDistance(beacon)
                val distanceToRow = abs(targetRow - sensor.row)
                val colsInRow = distanceToBeacon - distanceToRow
                if (colsInRow >= 0) {
                    val midCol = sensor.col
                    val left = maxOf(midCol - colsInRow, limit.x)
                    val right = minOf(midCol + colsInRow, limit.y)

                    var range = InclusiveRange(left, right)
                    var overlaps = ranges.find { it.overlaps(range) }
                    while (overlaps != null) {
                        ranges.remove(overlaps)
                        range = overlaps!!.merge(range)
                        overlaps = ranges.find { it.overlaps(range) }
                    }
                    ranges.add(range)
                }
            }

            if (ranges.size > 1) {
                // Found missing beacon
                val x = minOf(ranges[0].y, ranges[1].y) + 1
                val y = targetRow
                println(targetRow)
                return 4000000L * x.toLong() + y.toLong()
            }
        }
        throw IllegalStateException("Didn't find result")
    }

    val testInput = readInput("Day15_test")
    check(part1(testInput, 10) == 26)
    check(part2(testInput, InclusiveRange(0, 20)) == 56000011L)

    val input = readInput("Day15")
    val start = Date().time
    println(part1(input, 2000000))
    println("Part1 time: ${Date().time - start}ms")

    val start2 = Date().time
    println(part2(input, InclusiveRange(0, 4000000)))
    println("Part2 time: ${Date().time - start2}ms")
}
