import java.util.Stack

class Move(val amount: Int, val from: Int, val to: Int) {
    companion object Parser {
        private val moveRegex = Regex("move (\\d+) from (\\d+) to (\\d+)")
        fun parse(input: String): Move? {
            return moveRegex.find(input)
                ?.groupValues
                ?.drop(1)
                ?.map { it.toInt() }
                ?.let { values ->
                    Move(values[0], values[1] - 1, values[2] - 1)
                }
        }
    }
}

class ShipStacks(private val stacks: List<Stack<Char>>) {

    fun applyMove(move: Move) {
        repeat(move.amount) {
            val item = stacks[move.from].pop()
            stacks[move.to].push(item)
        }
    }

    fun applyMoveKeepingOrder(move: Move) {
        val stack = Stack<Char>()
        repeat(move.amount) {
            stack.push(stacks[move.from].pop())
        }
        repeat(move.amount) {
            stacks[move.to].push(stack.pop())
        }
    }

    fun getTop(): String {
        return stacks
            .map { it.peek() }
            .joinToString(separator = "")
    }

    companion object Reader {
        fun read(input: List<String>): ShipStacks {
            val columnPositions = getColumnPositions(input)
            val stacks = input.takeWhile { !isColumnLegendLine(it) }
                .flatMap { line -> columnPositions.map { column -> Pair(column, line.elementAtOrNull(column)) } }
                .filter { it.second != null && it.second!!.isLetter() }
                .sortedBy { it.first }
                .groupBy({ it.first }, { it.second!! })
                .values
                .map { column -> column.reversed().toCollection(Stack()) }

            return ShipStacks(stacks);
        }

        private fun getColumnPositions(input: List<String>) = (input
            .find { isColumnLegendLine(it) }
            ?.mapIndexed { idx, char -> Pair(idx, char) }
            ?.filter { it.second.isDigit() }
            ?.map { it.first }
            ?: throw IllegalArgumentException("Incorrect input"))

        private fun isColumnLegendLine(it: String) = it.trimStart().startsWith("1")
    }
}

fun main() {

    fun part1(input: List<String>): String {
        val shipStacks = ShipStacks.read(input)
        input.stream()
            .map { Move.parse(it) }
            .forEach {
                it?.let { shipStacks.applyMove(it) }
            }
        return shipStacks.getTop()
    }

    fun part2(input: List<String>): String {
        val shipStacks = ShipStacks.read(input)
        input.stream()
            .map { Move.parse(it) }
            .forEach {
                it?.let { shipStacks.applyMoveKeepingOrder(it) }
            }
        return shipStacks.getTop()
    }

// test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05")
    println(part1(testInput))
    println(part2(testInput))
}
