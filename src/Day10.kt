import kotlin.math.abs

fun main() {
    fun part1(input: List<String>): Int {
        var x = 1
        var cycle = 0
        val calcCycle = setOf(20, 60, 100, 140, 180, 220)
        var signal = 0

        for(line in input) {
            cycle++

            val split = line.split(' ')
            val command = split[0]

            if (calcCycle.contains(cycle)) {
                signal += cycle * x
            }

            if(command != "noop") {
                cycle++

                if (calcCycle.contains(cycle)) {
                    signal += cycle * x
                }

                x += split[1].toInt()
            }
        }

        return signal
    }

    fun part2(input: List<String>) {
        var x = 1
        var cycle = 0

        for(line in input) {
            cycle++

            val split = line.split(' ')
            val command = split[0]

            // During
            if (cycle%40-1 == x || cycle%40-1 == x-1 || cycle%40-1 == x+1) {
                print("#")
            } else {
                print(".")
            }
            if (cycle%40==0) {
                println()
            }


            if(command != "noop") {
                cycle++

                // During
                if (cycle%40-1 == x || cycle%40-1 == x-1 || cycle%40-1 == x+1) {
                    print("#")
                } else {
                    print(".")
                }
                if (cycle%40==0) {
                    println()
                }

                x += split[1].toInt()
            }
        }
    }

    val testInput = readInput("Day10_test")
    check(part1(testInput) == 13140)
    println()
    part2(testInput)
    println()
    println()

    val input = readInput("Day10")
    println(part1(input))
    part2(input)
}
