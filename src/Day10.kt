import kotlin.math.abs

fun main() {
    fun part1(input: List<String>): Int {
        return input.flatMap {
            when {
                it.startsWith("noop") -> listOf(0)
                it.startsWith("addx") -> listOf<Int>(0, it.replace("addx", "").trim().toInt())
                else -> throw IllegalArgumentException("Unexpected input")
            }
        }
            .fold(mutableListOf(Pair(1, 1))) { acc, value ->
                val previous = acc.last()
                acc.add(Pair(previous.first + 1, previous.second + value))
                acc
            }
            .filter {
                it.first in setOf(20, 60, 100, 140, 180, 220)
            }
            .map {
                it.first * it.second
            }
            .sum()
    }

    fun part2(input: List<String>): Int {
        return 1
    }


// test if implementation meets criteria from the description, like:
    val testInput = readInput("Day10")
    println(part1(testInput))
    println(part2(testInput))
}