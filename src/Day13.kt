import kotlinx.serialization.json.*

val packetDataComparator = PacketDataComparator()

data class Packet(private val rawData: String, private val data: JsonElement) : Comparable<Packet> {
    companion object PacketParser {
        fun parse(input: String): Packet {
            return Packet(input, Json.parseToJsonElement(input))
        }
    }

    override fun compareTo(other: Packet): Int {
        return packetDataComparator.compare(this.data, other.data)
    }

    override fun toString(): String {
        return data.toString()
    }

    fun isDivider(): Boolean {
        return "[[2]]" == rawData || "[[6]]" == rawData
    }
}

class PacketDataComparator : Comparator<JsonElement> {
    override fun compare(o1: JsonElement?, o2: JsonElement?): Int {
        return when {
            o1 is JsonPrimitive && o2 is JsonPrimitive -> o1.int.compareTo(o2.int)
            o1 is JsonPrimitive -> compare(JsonArray(listOf(o1)), o2)
            o2 is JsonPrimitive -> compare(o1, JsonArray(listOf(o2)))
            o1 is JsonArray && o2 is JsonArray -> {
                val firstMismatch = o1.zip(o2).firstOrNull { compare(it.first, it.second) != 0 }
                when {
                    firstMismatch == null && o1.size == o2.size -> 0
                    firstMismatch == null && o1.size < o2.size -> -1
                    firstMismatch == null -> 1
                    else -> compare(firstMismatch.first, firstMismatch.second)
                }
            }

            else -> throw RuntimeException("Unexpected arguments: $o1 and $o2")
        }
    }
}

fun main() {

    fun part1(input: String): Int {
        return input.split("\n")
            .windowed(2, 3)
            .mapIndexedNotNull { idx, window ->
                val packet1 = Packet.parse(window[0])
                val packet2 = Packet.parse(window[1])
                if (packet1 < packet2) {
                    idx
                } else {
                    null
                }
            }
            .sumOf { it + 1 }
    }

    fun part2(input: String): Int {
        return input.split("\n")
            .asSequence()
            .filter { it.trim().isNotEmpty() }
            .map { Packet.parse(it) }
            .sorted()
            .mapIndexedNotNull { idx, packet -> if (packet.isDivider()) idx + 1 else null }
            .reduce { acc, value -> acc * value }
    }

// test if implementation meets criteria from the description, like:
    println(part1(readInputText("Day13")))
    println(part2(readInputText("Day13")))
}

