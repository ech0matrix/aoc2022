fun main() {
    fun compareOrder(p1: Packet, p2: Packet): Int {
        //println("- Compare $p1 vs $p2")
        var i = 0
        while(true) {
            if (i >= p1.items.size && i >= p2.items.size) {
                // Both sides out of items. Go back up a level
                return 0
            } else if (i >= p1.items.size) {
                //println("    - The left list ran out of items first, the inputs are in the right order.")
                return -1
            } else if (i >= p2.items.size) {
                //println("    - The right list ran out of items first, the inputs are NOT in the right order.")
                return 1
            }

            val isP1List = p1.items[i][0] == '['
            val isP2List = p2.items[i][0] == '['

            if (!isP1List && !isP2List) {
                // both values are integers
                val int1 = p1.items[i].toInt()
                val int2 = p2.items[i].toInt()
                //println("  - Compare $int1 vs $int2")
                if (int1 < int2) {
                    //println ("    - Left side is smaller, so inputs are in the right order")
                    return -1
                } else if (int1 > int2) {
                    //println ("    - Right side is smaller, so inputs are NOT in the right order")
                    return 1
                }
            } else if (isP1List && isP2List) {
                // both values are lists
                val result = compareOrder(Packet(p1.items[i]), Packet(p2.items[i]))
                if (result != 0) {
                    return result
                }
            } else if (isP1List) {
                // exactly one value is an integer
                val result = compareOrder(Packet(p1.items[i]), Packet("[${p2.items[i]}]"))
                if (result != 0) {
                    return result
                }
            } else if (isP2List) {
                // exactly one value is an integer
                val result = compareOrder(Packet("[${p1.items[i]}]"), Packet(p2.items[i]))
                if (result != 0) {
                    return result
                }
            }

            i++
        }
    }

    fun part1(input: List<String>): Int {
        val pairs = input.chunked(3)
        var sumOfIndicies = 0
        for(i in pairs.indices) {
            val p1 = Packet(pairs[i][0])
            val p2 = Packet(pairs[i][1])

            //println("== Pair ${i+1} ==")
            val result = compareOrder(p1,p2)
            check(result != 0)

            if(result < 0) {
                //println("***Adding to sum: ${i+1}")
                sumOfIndicies += (i+1)
            }
            //println()
        }
        return sumOfIndicies
    }

    fun part2(input: List<String>): Int {
        val packets = (input.filter { it.isNotEmpty() } + "[[2]]" + "[[6]]").map { Packet(it) }
        val sorted = packets.sortedWith(::compareOrder)
        var product = 1
        for(i in sorted.indices) {
            if (sorted[i].toString() == "[[2]]" || sorted[i].toString() == "[[6]]") {
                product *= (i + 1)
            }
        }
        return product
    }

    val testInput = readInput("Day13_test")
    check(part1(testInput) == 13)
    check(part2(testInput) == 140)

    val input = readInput("Day13")
    println(part1(input))
    println(part2(input))
}

class Packet(
    input: String
) {
    val items: List<String>

    init {
        val itemBuilder = mutableListOf<String>()
        var level = 0
        var currentItem = ""
        for(i in input.indices) {
            if (input[i] == '[') {
                if (level > 0) {
                    currentItem += input[i]
                }
                level++
            } else if (input[i] == ']') {
                level--
                if (level > 0) {
                    currentItem += input[i]
                }
            } else if (level == 1 && input[i] == ',') {
                itemBuilder.add(currentItem)
                currentItem = ""
            } else {
                currentItem += input[i]
            }
        }
        if (currentItem.isNotEmpty()) {
            itemBuilder.add(currentItem)
        }

        items = itemBuilder.toList()
    }

    override fun toString(): String {
        return items.toString()
    }
}