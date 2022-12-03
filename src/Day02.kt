fun main() {


    fun part1(input: List<String>): Int {

        return input.map {
            it.split(" ").map { str ->
                when (str) {
                    "A", "X" -> 1
                    "B", "Y" -> 2
                    "C", "Z" -> 3
                    else -> throw IllegalArgumentException("Unsupported bet")
                }
            }
        }.map { Pair(it[1], (it[1] - it[0] + 3) % 3) }.map {
            it.first + when (it.second) {
                0 -> 3
                1 -> 6
                2 -> 0
                else -> throw IllegalArgumentException("Unsupported round outcome")
            }
        }.sumOf { it }
    }

    fun part2(input: List<String>): Int {
        return input.map {
            it.split(" ").map { str ->
                when (str) {
                    "A", "X" -> 1
                    "B", "Y" -> 2
                    "C", "Z" -> 3
                    else -> throw IllegalArgumentException("Unsupported bet")
                }
            }
        }.map {
            val usersBet = when (it[1]) {
                1 -> (it[0] + 1) % 3 + 1
                2 -> it[0]
                3 -> it[0] % 3 + 1
                else -> throw IllegalArgumentException("Unsupported round outcome")
            }
            Pair(it[0], usersBet)
        }.map { Pair(it.second, (it.second - it.first + 3) % 3) }.map {
            it.first + when (it.second) {
                0 -> 3
                1 -> 6
                2 -> 0
                else -> throw IllegalArgumentException("Unsupported round outcome")
            }
        }.sumOf { it }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02")
    println(part1(testInput))
    println(part2(testInput))
}
