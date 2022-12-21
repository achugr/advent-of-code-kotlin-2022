import java.util.*

data class GridCell(val row: Int, val col: Int, val height: Int)
data class PathNode(val cell: GridCell, var length: Int) :
    Comparable<PathNode> {
    override fun compareTo(other: PathNode): Int {
        return length.compareTo(other.length)
    }
}

class D12Grid(private val grid: List<List<Int>>, private val start: GridCell, private val finish: GridCell) {

    fun findShortestPath1Length(): Int {
        return findShortestPathLength { _: PathNode -> 1 }
    }

    fun findShortestPath2Length(): Int {
        return findShortestPathLength { to: PathNode -> if (to.cell.height == 0) 0 else 1 }
    }

    private fun findShortestPathLength(moveLengthFunction: (to: PathNode) -> Int): Int {
        val queue = PriorityQueue<PathNode>()
        val visited = mutableSetOf<GridCell>()
        var currentNode: PathNode = PathNode(start, 0)
        while (currentNode.cell != finish) {
            getNextPathNodes(currentNode)
                .filter { nextNode -> nextNode.cell.height <= currentNode.cell.height + 1 }
                .filter { nextNode -> visited.add(nextNode.cell) }
                .map { nextNode ->
                    nextNode.copy(length = currentNode.length + moveLengthFunction.invoke(nextNode))
                }.forEach { nextNode ->
                    queue.add(nextNode)
                }
            currentNode = queue.poll()
        }
        return currentNode.length
    }


    private fun getNextPathNodes(pathNode: PathNode): List<PathNode> {
        return Direction.values()
            .mapNotNull { direction ->
                val row = pathNode.cell.row + direction.row
                val col = pathNode.cell.col + direction.col
                if (row in grid.indices && col in grid[0].indices) {
                    PathNode(GridCell(row, col, grid[row][col]), Int.MAX_VALUE)
                } else {
                    null
                }
            }
    }

    companion object GridParser {
        fun parseInput(input: List<String>): D12Grid {
            lateinit var start: GridCell
            lateinit var finish: GridCell
            val data = input.mapIndexed { row, line ->
                line.toCharArray().mapIndexed { col, c ->
                    when (c) {
                        'S' -> {
                            start = GridCell(row, col, 0)
                            0
                        }

                        'E' -> {
                            val finishHeight = 'z'.code - 'a'.code
                            finish = GridCell(row, col, finishHeight)
                            finishHeight
                        }

                        else -> c.code - 'a'.code
                    }
                }.toList()
            }
            return D12Grid(data, start, finish)
        }
    }
}

fun main() {

    fun part1(input: List<String>): Int {
        return D12Grid.parseInput(input).findShortestPath1Length()
    }

    fun part2(input: List<String>): Int {
        return D12Grid.parseInput(input).findShortestPath2Length()
    }


// test if implementation meets criteria from the description, like:
    println(part1(readInput("Day12")))
    println(part2(readInput("Day12")))
}