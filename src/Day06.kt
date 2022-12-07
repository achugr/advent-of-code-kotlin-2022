fun main() {

    fun part1(input: String): Int {
        val windowSize = 4
        return input.asSequence()
            .windowed(windowSize, 1)
            .indexOfFirst { chars -> chars.toSet().size == chars.size }
            .plus(windowSize)
    }

    fun part2(input: String): Int {
        val windowSize = 14
        return input.asSequence()
            .windowed(windowSize, 1)
//            not cool to call toSet for every window, could be improved by adding some state
            .indexOfFirst { chars -> chars.toSet().size == chars.size }
            .plus(windowSize)
    }

// test if implementation meets criteria from the description, like:
    val testInput = readInputLine("Day06_test")
    println(part1(testInput))
    println(part2(testInput))
}
