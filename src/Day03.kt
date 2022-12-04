import kotlin.math.log2

fun main() {


    fun part1(input: List<String>): Int {
        return input.map { line ->
            line.toList()
                .chunked(line.length / 2)
                .map {
                    it.fold(0L) { acc, c ->
                        acc or (1L shl (c - 'A'))
                    }
                }
                .reduce { acc, i ->
                    acc and i
                }
        }.map {
            log2(it.toDouble()).toInt()
        }
            .map { (it + 'A'.code).toChar() }
            .map {
                when (it) {
                    in 'A'..'Z' -> it - 'A' + 27
                    in 'a'..'z' -> it - 'a' + 1
                    else -> throw IllegalArgumentException("Unexpected code")
                }
            }
            .sum()
    }

    fun part2(input: List<String>): Int {
        return input.windowed(3, 3)
            .map { group ->
                group
                    .map {
                        it.fold(0L) { acc, c ->
                            acc or (1L shl (c - 'A'))
                        }
                    }
                    .reduce { acc, i ->
                        acc and i
                    }
            }.map {
                log2(it.toDouble()).toInt()
            }
            .map { (it + 'A'.code).toChar() }
            .map {
                when (it) {
                    in 'A'..'Z' -> it - 'A' + 27
                    in 'a'..'z' -> it - 'a' + 1
                    else -> throw IllegalArgumentException("Unexpected code")
                }
            }
            .sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03")
    println(part1(testInput))
    println(part2(testInput))
}
