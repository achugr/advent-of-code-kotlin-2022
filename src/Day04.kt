fun main() {

    class Interval(val left: Int, val right: Int) {
        fun contain(interval: Interval): Boolean {
            return (interval.left in left..right) and (interval.right in left..right)
        }

        fun overlap(interval: Interval): Boolean {
            return (interval.left <= right) and (left <= interval.right)
        }
    }

    fun readIntervals(input: List<String>) = input.map { line ->
        line.split(",")
            .map { range ->
                range.split("-")
                    .map { it.toInt() }
                    .zipWithNext()
                    .map { Interval(it.first, it.second) }
                    .first()
            }
            .zipWithNext()
            .first()
    }

    fun part1(input: List<String>): Int {
        return readIntervals(input).count { it.first.contain(it.second) || it.second.contain(it.first) }
    }

    fun part2(input: List<String>): Int {
        return readIntervals(input).count { it.first.overlap(it.second) }
    }

// test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04")
    println(part1(testInput))
    println(part2(testInput))
}
