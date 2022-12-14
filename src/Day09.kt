import kotlin.math.abs

enum class PlaneDirection(val x: Int, val y: Int) {
    RIGHT(1, 0),
    LEFT(-1, 0),
    DOWN(0, -1),
    UP(0, 1),
    RIGHT_DOWN(1, -1),
    RIGHT_UP(1, 1),
    LEFT_DOWN(-1, -1),
    LEFT_UP(-1, 1);
}

data class SegmentInput(val direction: PlaneDirection, val length: Int)
data class Point(val x: Int, val y: Int) {
    fun adjacent(p: Point): Boolean {
        return abs(x - p.x) <= 1 && abs(y - p.y) <= 1
    }

    fun directionTo(p: Point): PlaneDirection {
        val xD = p.x - x
        val yD = p.y - y
        return PlaneDirection.values().find {
            it.x == (if (xD == 0) 0 else xD / abs(xD)) && it.y == (if (yD == 0) 0 else yD / abs(yD))
        } ?: throw RuntimeException("Incorrect move")
    }

    override fun toString(): String {
        return "($x,$y)"
    }
}

fun main() {

    fun getPoints(input: List<String>): List<Point> {
        val points = input.map {
            it.split(" ").zipWithNext().map { command ->
                val direction = when (command.first.toCharArray().first()) {
                    'R' -> PlaneDirection.RIGHT
                    'L' -> PlaneDirection.LEFT
                    'D' -> PlaneDirection.DOWN
                    'U' -> PlaneDirection.UP
                    else -> throw IllegalArgumentException("Unknown direction identifier")
                }
                SegmentInput(direction, command.second.toInt())
            }.first()
        }.fold(mutableListOf(Point(0, 0))) { acc, segment ->
            val lastPoint = acc.last()
            val points = (1..segment.length).map {
                Point(lastPoint.x + segment.direction.x * it, lastPoint.y + segment.direction.y * it)
            }
            acc.addAll(points)
            acc
        }
        return points
    }

    fun part1(input: List<String>): Int {
        val points = getPoints(input)
        val tailPath = mutableListOf(points[0])
        var tail = 0
        var head = 0
        while (head < points.size) {
            val headPoint = points[head]
            if (!headPoint.adjacent(tailPath.last())) {
                tailPath.add(points[head - 1])
                tail++
            }
            head++
        }
        tailPath.add(points[0])
        val uniqueTailPoints = tailPath.toSet()
        return uniqueTailPoints.size
    }

    fun evaluateFollowerPath(points: List<Point>): List<Point> {
        val tailPath = mutableListOf(points[0])
        var head = 0
        while (head < points.size) {
            val headPoint = points[head]
            val tailPoint = tailPath.last()
            if (!tailPoint.adjacent(headPoint)) {
                val moveDirection = tailPoint.directionTo(headPoint)
                tailPath.add(Point(tailPoint.x + moveDirection.x, tailPoint.y + moveDirection.y))
            }
            head++
        }
        return tailPath.toList()
    }

    //    not very efficient, but at least straightforward
    fun part2(input: List<String>): Int {
        val points = getPoints(input)
        var path = points
        repeat((1..9).count()) {
            path = evaluateFollowerPath(path)
        }
        return path.toSet().size
    }


// test if implementation meets criteria from the description, like:
    val testInput = readInput("Day09")
    println(part1(testInput))
    println(part2(testInput))
}