fun main() {
    fun shortestTurnsToProduce(cost: Int): Int {
        var turns = 0
        var costRemaining = cost
        while(costRemaining > 0) {
            turns++
            costRemaining -= turns
        }
        //println(" * Shortest to produce $cost: $turns")
        return turns
    }

    fun getBestOutcome(blueprint: Blueprint, maxMinutes: Int, minGeodeFactor: Int): State? {
        val active = mutableSetOf<State>()
        val done = mutableSetOf<State>()
        val logging = mutableSetOf<Int>()
        active.add(
            State(
                minutes = 1,
                robots = Materials(1, 0, 0, 0),
                inventory = Materials(0, 0, 0, 0),
                previousInventory = Materials(0, 0, 0, 0),
                justBuiltRobot = false
            )
        )

        // Hard limits
        val geodeBotBy = maxMinutes - shortestTurnsToProduce(minGeodeFactor)
        val obsidianBotBy = geodeBotBy - 1 - shortestTurnsToProduce(blueprint.geodeRobotCost.obsidian)
        val clayBotBy = obsidianBotBy - 1 - shortestTurnsToProduce(blueprint.obsidianRobotCost.clay)

        // Play with this to shift heuristic toward geode-only product in late game - save in search time to not produce needless factories
        val buildPivot = 29

        // Max resource needs for any recipe
        val maxOre = maxOf(
            blueprint.oreRobotCost.ore,
            blueprint.clayRobotCost.ore,
            blueprint.obsidianRobotCost.ore,
            blueprint.geodeRobotCost.ore
        )
        val maxClay = maxOf(
            blueprint.oreRobotCost.clay,
            blueprint.clayRobotCost.clay,
            blueprint.obsidianRobotCost.clay,
            blueprint.geodeRobotCost.clay
        )
        val maxObsidian = maxOf(
            blueprint.oreRobotCost.obsidian,
            blueprint.clayRobotCost.obsidian,
            blueprint.obsidianRobotCost.obsidian,
            blueprint.geodeRobotCost.obsidian
        )

        while (active.isNotEmpty()) {
            val current = active.first()
            if (!logging.contains(current.minutes)) {
                logging.add(current.minutes)
                //println("Minute ${current.minutes}. Possibilities: ${active.size}")
            }
            active.remove(current)
            if (current.minutes > maxMinutes) {
                done.add(current)
            } else if (current.minutes >= geodeBotBy && current.robots.geode == 0) {
            } else if (current.minutes >= obsidianBotBy && current.robots.obsidian == 0) {
            } else if (current.minutes >= clayBotBy && current.robots.clay == 0) {
            } else {
                val nextMinute = current.minutes + 1
                val collectedMinerals = current.robots
                val mineralsAtNextTurn = current.inventory.add(collectedMinerals)

                // Explore robot building options
                val buildOptions = mutableSetOf<State>()
                if (current.minutes < buildPivot && current.robots.ore < maxOre && (!current.previousInventory.hasEnough(
                        blueprint.oreRobotCost
                    ) || current.justBuiltRobot) && current.inventory.hasEnough(blueprint.oreRobotCost)
                ) {
                    buildOptions.add(
                        State(
                            minutes = nextMinute,
                            robots = current.robots.add(Materials(1, 0, 0, 0)),
                            inventory = mineralsAtNextTurn.subtract(blueprint.oreRobotCost),
                            previousInventory = current.inventory,
                            justBuiltRobot = true
                        )
                    )
                }
                if (current.minutes < buildPivot && current.robots.clay < maxClay && (!current.previousInventory.hasEnough(
                        blueprint.clayRobotCost
                    ) || current.justBuiltRobot) && current.inventory.hasEnough(blueprint.clayRobotCost)
                ) {
                    buildOptions.add(
                        State(
                            minutes = nextMinute,
                            robots = current.robots.add(Materials(0, 1, 0, 0)),
                            inventory = mineralsAtNextTurn.subtract(blueprint.clayRobotCost),
                            previousInventory = current.inventory,
                            justBuiltRobot = true
                        )
                    )
                }
                if (current.minutes < buildPivot && current.robots.obsidian < maxObsidian && (!current.previousInventory.hasEnough(
                        blueprint.obsidianRobotCost
                    ) || current.justBuiltRobot) && current.inventory.hasEnough(blueprint.obsidianRobotCost)
                ) {
                    buildOptions.add(
                        State(
                            minutes = nextMinute,
                            robots = current.robots.add(Materials(0, 0, 1, 0)),
                            inventory = mineralsAtNextTurn.subtract(blueprint.obsidianRobotCost),
                            previousInventory = current.inventory,
                            justBuiltRobot = true
                        )
                    )
                }
                if (((!current.previousInventory.hasEnough(blueprint.geodeRobotCost) || current.justBuiltRobot) && current.inventory.hasEnough(
                        blueprint.geodeRobotCost
                    ))
                    || (current.minutes >= buildPivot && current.inventory.hasEnough(blueprint.geodeRobotCost))
                ) {
                    buildOptions.add(
                        State(
                            minutes = nextMinute,
                            robots = current.robots.add(Materials(0, 0, 0, 1)),
                            inventory = mineralsAtNextTurn.subtract(blueprint.geodeRobotCost),
                            previousInventory = current.inventory,
                            justBuiltRobot = true
                        )
                    )
                }

                if (buildOptions.size == 4) {
                    // Better to build something than nothing. If possible to build every type, then skip "saving up" - no need to
                } else if (current.inventory.hasEnough(blueprint.oreRobotCost)
                    && current.inventory.hasEnough(blueprint.clayRobotCost)
                    && current.inventory.hasEnough(blueprint.obsidianRobotCost)
                    && current.inventory.hasEnough(blueprint.geodeRobotCost)
                ) {
                    // We've reached a case where it chose to save up, and has enough for everything, but decided not to build. Kill this branch.
                } else if (current.minutes >= buildPivot && current.inventory.hasEnough(blueprint.geodeRobotCost)) {
                } else {
                    // Save up (alongside other options)
                    buildOptions.add(
                        State(
                            minutes = nextMinute,
                            robots = current.robots,
                            inventory = mineralsAtNextTurn,
                            previousInventory = current.inventory,
                            justBuiltRobot = false
                        )
                    )
                }

                active.addAll(buildOptions)

//                    buildOptions.forEach { option ->
//                        println("== Minute ${current.minutes} ==")
//                        val robotDiff = option.robots.subtract(current.robots)
//                        if (robotDiff.ore == 1) {
//                            println("Spend ${blueprint.oreRobotCost} to start building an ore-collecting robot.")
//                        }
//                        if (robotDiff.clay == 1) {
//                            println("Spend ${blueprint.clayRobotCost} to start building a clay-collecting robot.")
//                        }
//                        if (robotDiff.obsidian == 1) {
//                            println("Spend ${blueprint.obsidianRobotCost} to start building an obsidian-collecting robot.")
//                        }
//                        if (robotDiff.geode == 1) {
//                            println("Spend ${blueprint.geodeRobotCost} to start building a geode-cracking robot.")
//                        }
//                        println("Robots ${current.robots} collect.")
//                        println("You now have minerals: ${option.inventory}")
//                        println("Robots ready, you now have ${option.robots}")
//                        println("        $option")
//                    }
            }
        }
        val maxState = done.maxByOrNull { it.inventory.geode }
        println("Blueprint ${blueprint.number}: $maxState")
        return maxState
    }

    fun parseBlueprints(input: List<String>): List<Blueprint> {
        return input.map { line ->
            val split = line.replace(":", "").split(" ")
            return@map Blueprint(
                split[1].toInt(),
                Materials(split[6].toInt(),0,0,0),
                Materials(split[12].toInt(),0,0,0),
                Materials(split[18].toInt(),split[21].toInt(),0,0),
                Materials(split[27].toInt(),0,split[30].toInt(),0)
            )
        }
    }

    fun part1(input: List<String>): Int {
        val blueprints = parseBlueprints(input)

        val qualityLevels = blueprints.associateWith { getBestOutcome(it, 24, 0) }

        return qualityLevels.map { (blueprint, state) -> blueprint.number * (state?.inventory?.geode ?: 0) }.sum()
    }

    fun part2(input: List<String>): Int {
        val blueprints = parseBlueprints(input)

        val b1 = blueprints[0] // Best on 24 min: 3 bots, 7 geodes
        val minExpectedGeodes1 = 31
        val outcome1 = getBestOutcome(b1, 32, minExpectedGeodes1)

        val b2 = blueprints[1] // Best on 24 min: 1 bot, 2 geodes
        val minExpectedGeodes2 = 10
        val outcome2 = getBestOutcome(b2, 32, minExpectedGeodes2)

        val b3 = blueprints[2] // Best on 24 min: 0???
        val minExpectedGeodes3 = 0
        val outcome3 = getBestOutcome(b3, 32, 0)

        return outcome1!!.inventory.geode * outcome2!!.inventory.geode * outcome3!!.inventory.geode
    }

    val testInput = readInput("Day19_test")
    check(part1(testInput) == 33)

    val input = readInput("Day19")
    println(part1(input))
    println(part2(input))
}

data class State(
    val minutes: Int,
    val robots: Materials,
    val inventory: Materials,
    val previousInventory: Materials,
    val justBuiltRobot: Boolean
)

data class Materials(
    val ore: Int,
    val clay: Int,
    val obsidian: Int,
    val geode: Int
) {
    fun hasEnough(cost: Materials): Boolean {
        return (ore >= cost.ore)
            && (clay >= cost.clay)
            && (obsidian >= cost.obsidian)
            && (geode >= cost.geode)
    }

    fun add(other: Materials) = Materials(
            ore + other.ore,
            clay + other.clay,
            obsidian + other.obsidian,
            geode + other.geode
        )

    fun subtract(other: Materials) = Materials(
        ore - other.ore,
        clay - other.clay,
        obsidian - other.obsidian,
        geode - other.geode
        )
}

data class Blueprint(
    val number: Int,
    val oreRobotCost: Materials,
    val clayRobotCost: Materials,
    val obsidianRobotCost: Materials,
    val geodeRobotCost: Materials
)