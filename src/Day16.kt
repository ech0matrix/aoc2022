// I hate this code. Please don't look at it.

fun main() {
    fun calcDistance(valveSource: String, valveDestination: String, valves: Map<String, Valve>): Int {
        val active = mutableSetOf(TunnelPath(valves[valveSource]!!, 0))
        val visited = mutableSetOf<Valve>()
        while (active.isNotEmpty()) {
            val current = active.minByOrNull { it.cost }!!
            if (current.valve.name == valveDestination) {
                return current.cost
            }

            visited.add(current.valve)
            active.remove(current)

            val possibilities = current.valve.tunnels.map{ valves[it]!! }.filter { !visited.contains(it) }
            for(possibility in possibilities) {
                val alreadyActive = active.find { it.valve == possibility }
                if (alreadyActive != null) {
                    if (alreadyActive.cost > current.cost+1) {
                        active.remove(alreadyActive)
                        active.add(TunnelPath(possibility, current.cost+1))
                    }
                } else {
                    active.add(TunnelPath(possibility, current.cost+1))
                }
            }
        }

        throw IllegalStateException("Didn't find result")
    }

    fun part1(input: List<String>, maxTime: Int): Int {
        // Parse input
        val valves = input.map {
            // Remove excess words in input
            it.substring(6)
                .replace(" has flow rate=", ";")
                .replace(" tunnels lead to valves ", "")
                .replace(" tunnel leads to valve ", "")
                .replace(", ", ",")
        }.map {
            // Parse useful input
            val split = it.split(';')
            Valve(split[0], split[1].toInt(), split[2].split(','))
        }.associateBy { it.name }

        val usefulValves = valves.filterValues { it.flow > 0 || it.name == "AA" }.values
        val flowValves = usefulValves.filter { it.flow > 0 }

//        println("All valves: $valves")
//        println("Useful valves: $usefulValves")

        // Get distance between all valves
        val valveDistances = mutableMapOf<Valve, MutableMap<Valve, Int>>()
        for(valveSource in usefulValves) {
            valveDistances[valveSource] = mutableMapOf()
            for(valveDestination in usefulValves) {
                if (valveSource != valveDestination) {
                    //println("Calculating distance between ${valveSource.name} and ${valveDestination.name}")
                    val distance = calcDistance(valveSource.name, valveDestination.name, valves)
                    //println("Distance between ${valveSource.name} and ${valveDestination.name}: $distance")
                    valveDistances[valveSource]!![valveDestination] = distance + 1
                }
            }
        }

        // Build all possible paths
        var valvePaths = mutableMapOf<List<Valve>, Int>()
        val limitedPaths = mutableMapOf<List<Valve>, Int>()
        for((valve, distances) in valveDistances.filter { (key, _) -> key.name == "AA" }) {
            for((destination, distance) in distances) {
                valvePaths[listOf(valve, destination)] = distance
            }
        }
        //println(valvePaths.keys.map { it.map { v -> v.name} })
        repeat(flowValves.size-1) {
            val nextPaths = mutableMapOf<List<Valve>, Int>()
            for((path, cost) in valvePaths) {
                for(valve in flowValves) {
                    if (!path.contains(valve)) {
                        //println("Path: ${path.map {it.name}} -> ${valve.name}")
                        val newCost = cost + valveDistances[path.last()]!![valve]!!
                        if (newCost < maxTime) {
                            nextPaths[path + valve] = newCost
                        } else {
                            limitedPaths[path] = cost
                        }
                    }
                }
            }
            valvePaths = nextPaths
        }
        val allPaths = valvePaths + limitedPaths
        //println("Num possible paths: ${allPaths.size}")

        // Walk paths
        var maxFlow = 0
        for((path, _) in allPaths) {
            var currentFlow = 0
            var time = 1

            for(i in 0 until path.size-1) {
                time += valveDistances[path[i]]!![path[i+1]]!!
                val flow = path[i+1].flow
                val fullTimeFlow = (maxTime-time+1) * flow
                currentFlow += fullTimeFlow
                //println("t=$time: Turn on ${path[i+1].name} for $flow (full time = $fullTimeFlow)")
            }

            maxFlow = maxOf(maxFlow, currentFlow)
        }

        return maxFlow
    }

    fun part2(input: List<String>, maxTime: Int): Int {
        // Parse input
        val valves = input.map {
            // Remove excess words in input
            it.substring(6)
                .replace(" has flow rate=", ";")
                .replace(" tunnels lead to valves ", "")
                .replace(" tunnel leads to valve ", "")
                .replace(", ", ",")
        }.map {
            // Parse useful input
            val split = it.split(';')
            Valve(split[0], split[1].toInt(), split[2].split(','))
        }.associateBy { it.name }

        val usefulValves = valves.filterValues { it.flow > 0 || it.name == "AA" }.values
        val flowValves = usefulValves.filter { it.flow > 0 }

//        println("All valves: $valves")
//        println("Useful valves: $usefulValves")

        // Get distance between all valves
        val valveDistances = mutableMapOf<Valve, MutableMap<Valve, Int>>()
        for(valveSource in usefulValves) {
            valveDistances[valveSource] = mutableMapOf()
            for(valveDestination in usefulValves) {
                if (valveSource != valveDestination) {
                    //println("Calculating distance between ${valveSource.name} and ${valveDestination.name}")
                    val distance = calcDistance(valveSource.name, valveDestination.name, valves)
                    //println("Distance between ${valveSource.name} and ${valveDestination.name}: $distance")
                    valveDistances[valveSource]!![valveDestination] = distance + 1
                }
            }
        }

        // Build all possible paths
        var valvePaths = mutableMapOf<List<Valve>, Int>()
        val limitedPaths = mutableMapOf<List<Valve>, Int>()
        for((valve, distances) in valveDistances.filter { (key, _) -> key.name == "AA" }) {
            for((destination, distance) in distances) {
                valvePaths[listOf(valve, destination)] = distance
            }
        }
        //println(valvePaths.keys.map { it.map { v -> v.name} })
        repeat(flowValves.size-1) {
            val nextPaths = mutableMapOf<List<Valve>, Int>()
            for((path, cost) in valvePaths) {
                for(valve in flowValves) {
                    if (!path.contains(valve)) {
                        //println("Path: ${path.map {it.name}} -> ${valve.name}")
                        val newCost = cost + valveDistances[path.last()]!![valve]!!
                        if (newCost < maxTime) {
                            nextPaths[path + valve] = newCost
                        } else {
                            limitedPaths[path] = cost
                        }
                    }
                }
            }
            limitedPaths += valvePaths
            valvePaths = nextPaths
        }
        val allPaths = valvePaths + limitedPaths
        val blah = allPaths.keys.map { it.map {v -> v.name} }.toSet()
        //println("Num possible paths: ${allPaths.size}")

        // Walk paths
        var pathFlows = mutableMapOf<List<Valve>, Int>()
        for((path, _) in allPaths) {
            var currentFlow = 0
            var time = 1

            for(i in 0 until path.size-1) {
                time += valveDistances[path[i]]!![path[i+1]]!!
                val flow = path[i+1].flow
                val fullTimeFlow = (maxTime-time+1) * flow
                currentFlow += fullTimeFlow
                //println("t=$time: Turn on ${path[i+1].name} for $flow (full time = $fullTimeFlow)")
            }

            pathFlows[path] = currentFlow
        }

        // Find non-overlapping path pairs, and take max
        val pathsWithoutStart = pathFlows.mapKeys { (key, _) -> key.subList(1, key.size) }
        val pathList = pathsWithoutStart.keys.toList()
        var pairMax = 0
        //println("Num to compare: ${pathList.size}")
//        for(path1 in pathsWithoutStart.keys) {
//            for(path2 in pathsWithoutStart.keys.filter { it.intersect(path1.toSet()).isEmpty() }) {
//                pairMax = maxOf(pairMax, pathsWithoutStart[path1]!! + pathsWithoutStart[path2]!!)
//            }
//        }
        for(i in pathList.indices) {
            val path1 = pathList[i]
            //if(i % 1000 == 0) println(i)
            for(path2 in pathsWithoutStart.keys.filter { it.intersect(path1.toSet()).isEmpty() }) {
                pairMax = maxOf(pairMax, pathsWithoutStart[path1]!! + pathsWithoutStart[path2]!!)
            }
        }

        //println("PairMax: $pairMax")
        return pairMax
    }

    val testInput = readInput("Day16_test")
    check(part1(testInput, 30) == 1651)
    check(part2(testInput, 26) == 1707)

    val input = readInput("Day16")
    //val time = java.util.Date().time
    println(part1(input, 30))
    //println("Time: ${java.util.Date().time - time}ms")
    println(part2(input, 26))
}

data class Valve(
    val name: String,
    val flow: Int,
    val tunnels: List<String>
)

data class TunnelPath(
    val valve: Valve,
    val cost: Int
)
