fun main() {
    fun parsePoints(input: List<String>): Set<Point3D> {
        return input.map {
            val parts = it.split(',').map { n -> n.toInt() }
            Point3D(parts[0], parts[1], parts[2])
        }.toSet()
    }

    fun draw(points: Set<Point3D>) {
        val minX = points.minOf { it.x }
        val maxX = points.maxOf { it.x }
        val minY = points.minOf { it.y }
        val maxY = points.maxOf { it.y }
        val minZ = points.minOf { it.z }
        val maxZ = points.maxOf { it.z }

        println("==============")
        for(y in minY .. maxY) {
            for(x in minX .. maxX) {
                if (points.find { it.x == x && it.y == y } != null) {
                    print("#")
                } else {
                    print(".")
                }
            }
            println()
        }
        println("==============")
        for(z in minZ .. maxZ) {
            for(x in minX .. maxX) {
                if (points.find { it.x == x && it.z == z } != null) {
                    print("#")
                } else {
                    print(".")
                }
            }
            println()
        }
        println("==============")
        for(y in minY .. maxY) {
            for(z in minZ .. maxZ) {
                if (points.find { it.y == y && it.z == z } != null) {
                    print("#")
                } else {
                    print(".")
                }
            }
            println()
        }
    }

    fun part1(input: List<String>): Int {
        val points = parsePoints(input)
        return points.sumOf { 6 - points.intersect(it.getSides()).size }
    }

    fun part2(input: List<String>): Int {
        val points = parsePoints(input).toMutableSet()

        //draw(points)

        val bubbleCandidates = mutableSetOf<Point3D>()
        for((x,y,z) in points) {
            val left = Point3D(x+1,y,z)
            if (!points.contains(left) && points.find { (x2,y2,z2) -> x2 > left.x && y2 == left.y && z2 == left.z } != null) {
                bubbleCandidates.add(left)
            }

            val right = Point3D(x-1,y,z)
            if (!points.contains(right) && points.find { (x2,y2,z2) -> x2 < right.x && y2 == right.y && z2 == right.z } != null) {
                bubbleCandidates.add(right)
            }

            val up = Point3D(x,y+1,z)
            if (!points.contains(up) && points.find { (x2,y2,z2) -> x2 == up.x && y2 > up.y && z2 == up.z } != null) {
                bubbleCandidates.add(up)
            }

            val down = Point3D(x,y-1,z)
            if (!points.contains(down) && points.find { (x2,y2,z2) -> x2 == down.x && y2 < down.y && z2 == down.z } != null) {
                bubbleCandidates.add(down)
            }

            val front = Point3D(x,y,z+1)
            if (!points.contains(front) && points.find { (x2,y2,z2) -> x2 == front.x && y2 == front.y && z2 > front.z } != null) {
                bubbleCandidates.add(front)
            }

            val back = Point3D(x,y,z-1)
            if (!points.contains(back) && points.find { (x2,y2,z2) -> x2 == back.x && y2 == back.y && z2 < back.z } != null) {
                bubbleCandidates.add(back)
            }
        }

        val minBoundary = Point3D(points.minOf { it.x } - 1, points.minOf { it.y } - 1, points.minOf { it.z } - 1)
        val maxBoundary = Point3D(points.maxOf { it.x } + 1, points.maxOf { it.y } + 1, points.maxOf { it.z } + 1)

        for(candidate in bubbleCandidates) {
            //println("Candidate: $candidate")
            if (points.contains(candidate)) {
                continue
            }

            var isBubble = true
            val visited = mutableSetOf<Point3D>()
            val active = mutableSetOf(candidate)
            while(active.isNotEmpty()) {
                val current = active.first()
                if (current.x <= minBoundary.x || current.y <= minBoundary.y || current.z <= minBoundary.z
                    || current.x >= maxBoundary.x || current.y >= maxBoundary.y || current.z >= maxBoundary.z) {
                    isBubble = false
                    break
                }
                active.remove(current)
                visited.add(current)

                active.addAll(current.getSides().toSet().subtract(points).subtract(visited).subtract(active))
            }

            if(isBubble) {
                println("Found bubble. Size=${visited.size}")
                points.addAll(visited)
            }
        }

        return points.sumOf { 6 - points.intersect(it.getSides()).size }
    }

    val testInput = readInput("Day18_test")
    check(part1(testInput) == 64)
    check(part2(testInput) == 58)

    val input = readInput("Day18")
    println(part1(input))
    println(part2(input))
}

data class Point3D(
    val x: Int,
    val y: Int,
    val z: Int
) {
    fun getSides(): Set<Point3D> {
        return setOf(
            Point3D(x+1,y,z),
            Point3D(x-1,y,z),
            Point3D(x,y+1,z),
            Point3D(x,y-1,z),
            Point3D(x,y,z+1),
            Point3D(x,y,z-1)
        )
    }
}
