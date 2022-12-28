fun main() {
    fun part1(input: List<String>): String {
        return input.map { SnafuNumber(it) }.reduce{ num1, num2 -> num1.add(num2) }.toString()
    }

//    fun part2(input: List<String>): String {
//        return input.size
//    }

    val testInput = readInput("Day25_test")
    check(part1(testInput) == "2=-1=0")

    val input = readInput("Day25")
    println(part1(input))
//    println(part2(input))
}

data class SnafuNumber(
    val digits: List<SnafuDigit>
) {
    constructor(number: String) : this(number.map { SnafuDigit(it) }.reversed())

    fun add (other: SnafuNumber): SnafuNumber {
        val result = mutableListOf<SnafuDigit>()
        var overflow = SnafuDigit(0)
        val maxIndex = maxOf(this.digits.size, other.digits.size) // (max index + 1 is intended to account for overflow in last digit)
        for(i in 0 .. maxIndex) {
            val digit1 = if (i < this.digits.size) this.digits[i] else SnafuDigit(0)
            val digit2 = if (i < other.digits.size) other.digits[i] else SnafuDigit(0)
            val digitResult = digit1.add(digit2, overflow)
            result.add(digitResult.result)
            overflow = digitResult.overflow
        }
        // Trim trailing 0's
        while (result[result.size-1].value == 0) {
            result.removeAt(result.size-1)
        }
        return SnafuNumber(result.toList())
    }

    override fun toString(): String {
        return digits.map { it.toString() }.reversed().joinToString("")
    }
}

data class SnafuDigit(
    val value: Int
) {
    init {
        require(value in -2 .. 2)
    }

    constructor(digit: Char) : this(when(digit) {
        '2' -> 2
        '1' -> 1
        '0' -> 0
        '-' -> -1
        '=' -> -2
        else -> throw IllegalArgumentException("Unknown SnafuDigit: $digit")
    })

    fun add(other1: SnafuDigit, other2: SnafuDigit = SnafuDigit(0)): SnafuDigitResult {
        var result = this.value + other1.value + other2.value
        var overflow = 0
        if (result < -2) {
            result += 5
            overflow--
        } else if (result > 2) {
            result -= 5
            overflow++
        }
        return SnafuDigitResult(SnafuDigit(result), SnafuDigit(overflow))
    }

    override fun toString(): String {
        return when(value) {
            2 -> "2"
            1 -> "1"
            0 -> "0"
            -1 -> "-"
            -2 -> "="
            else -> throw IllegalArgumentException("Unknown value for SnafuDigit: $value")
        }
    }
}

data class SnafuDigitResult(
    val result: SnafuDigit,
    val overflow: SnafuDigit
)