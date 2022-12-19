import java.math.BigInteger

class ItemRouter(private val router: (BigInteger) -> Monkey) {
    fun send(item: BigInteger) {
        router.invoke(item).offer(item)
    }
}

class Monkey(
    private val items: MutableList<BigInteger>, private val operation: (BigInteger) -> BigInteger,
    private val stressReduces: Boolean, val divisibleBy: BigInteger, val firstMate: Int, val secondMate: Int
) {
    lateinit var router: ItemRouter
    lateinit var mod: BigInteger
    var itemsCounter: Int = 0

    fun playRound() {
        items.forEach { item ->
            var value = operation.invoke(item.mod(mod))
            if (stressReduces) {
                value = value.divide(BigInteger.valueOf(3))
            }
            router.send(value)
            itemsCounter++
        }
        items.clear()
    }

    fun offer(item: BigInteger) {
        items.add(item)
    }

    override fun toString(): String {
        return "Monkey(items=$items)"
    }

    companion object MonkeyInitializer {

        val monkeyRegex =
            """
            Monkey \d+:
              Starting items: (?<startingItems>[\d,\s]+)
              Operation: new = old (?<operation>[+*] (?<operand>\w+))
              Test: divisible by (?<divisibleBy>\d+)
                If true: throw to monkey (?<throwTo1>\d+)
                If false: throw to monkey (?<throwTo2>\d+)
        """.trimIndent().toRegex()

        fun init(str: String, stressReduces: Boolean): Monkey {
            val match = monkeyRegex.find(str) ?: throw IllegalArgumentException("Failed to parse monkey definition")
            val startingItems =
                match.getValue("startingItems").split(",").map { it.trim().toBigInteger() }.toMutableList()
            val operand = match.getValue("operand")
            val itemOperation = with(match.getValue("operation")) {
                when {
                    startsWith("* old") -> { i: BigInteger -> i * i }
                    startsWith("*") -> { i: BigInteger -> i * operand.toBigInteger() }
                    startsWith("+") -> { i: BigInteger -> i + operand.toBigInteger() }
                    startsWith("-") -> { i: BigInteger -> i - operand.toBigInteger() }
                    else -> throw IllegalArgumentException("Unexpected input")
                }
            }
            val divisibleBy = match.getValue("divisibleBy").toBigInteger()
            return Monkey(
                startingItems, itemOperation, stressReduces,
                divisibleBy,
                match.getValue("throwTo1").toInt(),
                match.getValue("throwTo2").toInt()
            )
        }
    }
}

fun main() {

    fun readMonkeys(input: String, stressReduces: Boolean): List<Monkey> {
        val monkeys = input.split("\n\n")
            .map { Monkey.init(it, stressReduces) }
            .toList()

        val mod = monkeys
            .map { it.divisibleBy }
            .reduce { acc, value -> acc * value }

        monkeys.forEach { monkey ->
            monkey.mod = mod
            monkey.router = ItemRouter { i: BigInteger ->
                if (i.mod(monkey.divisibleBy).equals(BigInteger.ZERO)) {
                    monkeys[monkey.firstMate]
                } else {
                    monkeys[monkey.secondMate]
                }
            }
        }
        return monkeys
    }

    fun getAnswer(monkeys: List<Monkey>) = monkeys
        .map { it.itemsCounter.toLong() }
        .sortedByDescending { it }
        .take(2)
        .reduce { acc, value -> acc * value }

    fun playGame(input: String, rounds: Int, stressReduces: Boolean): Long {
        val monkeys = readMonkeys(input, stressReduces)
        (0 until rounds).forEach { idx ->
            monkeys.forEach { monkey -> monkey.playRound() }
        }
        return getAnswer(monkeys)
    }

    fun part1(input: String): Long {
        return playGame(input, 20, true)
    }

    fun part2(input: String): Long {
        return playGame(input, 10_000, false)
    }


// test if implementation meets criteria from the description, like:
    println(part1(readInputText("Day11")))
    println(part2(readInputText("Day11")))
}

fun MatchResult.getValue(name: String): String =
    this.groups[name]?.value ?: throw IllegalArgumentException("Group for name $name not found")