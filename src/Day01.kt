fun main() {
    fun groupAndSum(input: List<String>) =
        input.foldIndexed(mutableListOf<MutableList<Int>>(mutableListOf())) { index, list, value ->
            when {
                value.isBlank() -> list.add(mutableListOf())
                list.isNotEmpty() -> list.last().add(value.toInt())
                else -> list.add(mutableListOf())
            }
            list
        }
            .filter { list -> list.isNotEmpty() }
            .map { list -> list.sum() }

    fun part1(input: List<String>): Int {
        return groupAndSum(input)
            .max()
    }

    fun part2(input: List<String>): Int {
        return groupAndSum(input)
            .sorted()
            .reversed()
            .take(3)
            .sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01")
    println(part1(testInput))
    println(part2(testInput))
}
