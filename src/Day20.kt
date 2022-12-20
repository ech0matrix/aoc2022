import kotlin.math.abs

fun main() {
    fun printList(startingNode: EncryptionNode) {
        print("   ${startingNode.number}")
        var current = startingNode.next
        while(current != startingNode) {
            print(", ${current!!.number}")
            current = current.next
        }
        println()
    }

    fun parse(input: List<String>, multiplier: Long): List<EncryptionNode> {
        val nodes = input.map { EncryptionNode(it.toLong() * multiplier) }
        for(i in 1 until nodes.size-1) {
            nodes[i-1].next = nodes[i]
            nodes[i].previous = nodes[i-1]

            nodes[i+1].previous = nodes[i]
            nodes[i].next = nodes[i+1]
        }
        nodes[0].previous = nodes[nodes.size-1]
        nodes[nodes.size-1].next = nodes[0]
        return nodes
    }

    fun mix(nodes: List<EncryptionNode>) {
        for(node in nodes) {
            //println("Moving ${node.number}")
            val move = node.number.let {
                val adjusted = it % (nodes.size-1) // Avoid multiple wraparounds
                //println("   Wraparound move adjusted to $adjusted")
                adjusted
            }.let {
                // Check if moving the opposite direction is shorter
                val directionAdjust = if (it < 0) {
                    val oppositeMove = (nodes.size-1) + it
                    if (oppositeMove < abs(it)) {
                        oppositeMove
                    } else {
                        it
                    }
                } else if (it > 0) {
                    val oppositeMove = it - (nodes.size-1)
                    if (abs(oppositeMove) < it) {
                        println("$oppositeMove is faster than $it")
                        oppositeMove
                    } else {
                        it
                    }
                } else {
                    0
                }
                //println("   Direction move adjusted to $directionAdjust")
                directionAdjust
            }

            if (move != 0L) {
                // Cut the node out for the move
                node.previous!!.next = node.next
                node.next!!.previous = node.previous

                var current = node

                if (move < 0) {
                    // Go through 'previous' links
                    for (i in 0 until abs(move)) {
                        current = current.previous!!
                    }
                } else {
                    // Go through 'next'' links
                    for (i in 0 until move) {
                        current = current.next!!
                    }
                    current = current.next!! // One more to adjust for general linkage
                }

                // Insert the node
                node.next = current
                node.previous = current.previous
                current.previous!!.next = node
                current.previous = node
            }
            //printList(nodes.first())
        }
    }

    fun sumAnswerNodes(nodes: List<EncryptionNode>): Long {
        var sum = 0L
        val zeroNode = nodes.find { it.number == 0L }!!
        var current = zeroNode
        repeat(3) {
            repeat(1000) {
                current = current.next!!
            }
            //println(current.number)
            sum += current.number
        }
        return sum
    }

    fun part1(input: List<String>): Long {
        val nodes = parse(input, 1L)
        //printList(nodes.first())
        mix(nodes)
        return sumAnswerNodes(nodes)
    }

    fun part2(input: List<String>): Long {
        val nodes = parse(input, 811589153L)
        //printList(nodes.first())
        repeat(10) {
            mix(nodes)
        }
        return sumAnswerNodes(nodes)
    }

    val testInput = readInput("Day20_test")
    check(part1(testInput) == 3L)
    check(part2(testInput) == 1623178306L)

    val input = readInput("Day20")
    println(part1(input))
    println(part2(input))
}

data class EncryptionNode(
    val number: Long
) {
    var previous: EncryptionNode? = null
    var next: EncryptionNode? = null
}
