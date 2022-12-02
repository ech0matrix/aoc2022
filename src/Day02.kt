import Play.*
import Outcome.*

fun main() {
    fun part1(input: List<String>): Int {
        val scores = input.map {
            Game(it[0].toPlay(), it[2].toPlay())
        }.map { it.score() }
        //scores.forEach{println(it)}
        return scores.sum()
    }

    fun part2(input: List<String>): Int {
        val scores = input.map {
            val opponent = it[0].toPlay()
            val outcome = it[2].toOutcome()
            Game(opponent, outcome.toPlay(opponent))
        }.map { it.score() }
        return scores.sum()
    }

    val testInput = readInput("Day02_test")
    check(part1(testInput) == 15)

    val input = readInput("Day02")
    println(part1(input))
    println(part2(input))
}

fun Char.toPlay(): Play {
    return when(this) {
        'A', 'X' -> ROCK
        'B', 'Y' -> PAPER
        'C', 'Z' -> SCISSORS
        else -> throw IllegalArgumentException("Invalid letter: $this")
    }
}

fun Outcome.toPlay(opponent: Play): Play {
    return when(this) {
        DRAW -> opponent
        LOSE -> when(opponent) {
            ROCK -> SCISSORS
            PAPER -> ROCK
            SCISSORS -> PAPER
        }
        WIN -> when(opponent) {
            ROCK -> PAPER
            PAPER -> SCISSORS
            SCISSORS -> ROCK
        }
    }
}

fun Char.toOutcome(): Outcome {
    return when(this) {
        'X' -> LOSE
        'Y' -> DRAW
        'Z' -> WIN
        else -> throw IllegalArgumentException("Invalid letter: $this")
    }
}

enum class Play {
    ROCK,
    PAPER,
    SCISSORS
}

enum class Outcome {
    LOSE,
    DRAW,
    WIN
}

data class Game(
    val opponent: Play,
    val self: Play
) {
    fun score(): Int {
        //println(this)
        val playScore = when(self) {
            ROCK -> 1
            PAPER -> 2
            SCISSORS -> 3
        }
        //println(playScore)

        val matchScore = when {
            (self == opponent) -> 3
            isWinner() -> 6
            else -> 0
        }
        //println(matchScore)
        //println()

        return playScore + matchScore
    }

    // Denotes if 'self' wins or loses (undefined on tie)
    private fun isWinner(): Boolean {
        return when(self) {
            ROCK -> opponent == SCISSORS
            PAPER -> opponent == ROCK
            SCISSORS -> opponent == PAPER
        }
    }
}