import java.util.*

enum class Direction(val row: Int, val col: Int) {
    RIGHT(0, 1),
    LEFT(0, -1),
    DOWN(1, 0),
    UP(-1, 0)
}


class Grid(
    private val grid: List<List<Cell>>,
) {
    fun getVisible(): List<Cell> {
        return grid.flatten().filter { cell -> cell.visible }
    }

    fun markVisible(row: Int, col: Int) {
        grid[row][col].visible = true
    }

    fun getCell(row: Int, col: Int): Cell? {
        return if (cellExists(row, col)) {
            grid[row][col]
        } else {
            null
        }
    }

    fun nextCell(cell: Cell, direction: Direction): Cell? {
        val nextRow = cell.row + direction.row
        val nextCol = cell.col + direction.col
        return getCell(nextRow, nextCol)
    }

    private fun cellExists(nextRow: Int, nextCol: Int): Boolean = nextRow in grid.indices && nextCol in grid[0].indices

    fun height(): Int = grid.size
    fun width(): Int = grid[0].size
    data class Cell(val row: Int, val col: Int, val height: Int) {
        var visible: Boolean = false
        override fun toString(): String {
            return "Cell(row=$row, col=$col, height=$height)"
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Cell

            if (row != other.row) return false
            if (col != other.col) return false

            return true
        }

        override fun hashCode(): Int {
            var result = row
            result = 31 * result + col
            return result
        }


    }

    companion object Initializer {
        fun init(input: List<String>): Grid {
            val gridData = input.mapIndexed { row, line ->
                line.toList().mapIndexed { col, v -> Cell(row, col, v.digitToInt()) }
                    .toList()
            }

            return Grid(gridData)
        }
    }
}

class ScenicViewScanner(
    private val grid: Grid,
    private val startCell: Grid.Cell,
) {
    fun scan(): Int {
        return Direction.values()
            .map { direction ->
                val count = generateSequence(startCell) { position ->
                    grid.nextCell(position, direction)
                }
                    .zipWithNext()
                    .takeWhile { it.first == startCell || it.first.height < startCell.height }
                    .count()
                count
            }.reduce { v1, v2 -> v1 * v2 }
    }
}

class OneDirectionVisibilityScanner(
    private val grid: Grid,
    private var cell: Grid.Cell,
    private val direction: Direction,
) :
    Comparable<OneDirectionVisibilityScanner> {
    private var maxHeight: Int = -1
    private var prevHeight: Int = Int.MAX_VALUE
    lateinit var pairedScanner: OneDirectionVisibilityScanner

    fun scanCell(): Boolean {
        registerCell()
        if (cell != pairedScanner.cell) {
            prevHeight = grid.getCell(cell.row, cell.col)!!.height
            cell = grid.nextCell(cell, direction)!!
            return true
        }
        return false
    }

    private fun registerCell() {
        val height = cell.height
        if (height > maxHeight) {
            grid.markVisible(cell.row, cell.col)
            maxHeight = height
        }
    }

    override fun compareTo(other: OneDirectionVisibilityScanner): Int {
        return cell.height.compareTo(other.cell.height)
    }
}

fun main() {


    fun processGrid(grid: Grid) {
        val oneDirectionVisibilityScanners = PriorityQueue<OneDirectionVisibilityScanner>()
        (0 until grid.width()).forEach { i ->
            val rightOneDirectionVisibilityScanner = OneDirectionVisibilityScanner(grid, grid.getCell(i, 0)!!, Direction.RIGHT)
            val leftOneDirectionVisibilityScanner = OneDirectionVisibilityScanner(grid, grid.getCell(i, grid.width() - 1)!!, Direction.LEFT)
            rightOneDirectionVisibilityScanner.pairedScanner = leftOneDirectionVisibilityScanner
            leftOneDirectionVisibilityScanner.pairedScanner = rightOneDirectionVisibilityScanner
            oneDirectionVisibilityScanners.offer(rightOneDirectionVisibilityScanner)
            oneDirectionVisibilityScanners.offer(leftOneDirectionVisibilityScanner)
        }
        (0 until grid.height()).forEach { i ->
            val downOneDirectionVisibilityScanner = OneDirectionVisibilityScanner(grid, grid.getCell(0, i)!!, Direction.DOWN)
            val upOneDirectionVisibilityScanner = OneDirectionVisibilityScanner(grid, grid.getCell(grid.height() - 1, i)!!, Direction.UP)
            downOneDirectionVisibilityScanner.pairedScanner = upOneDirectionVisibilityScanner
            upOneDirectionVisibilityScanner.pairedScanner = downOneDirectionVisibilityScanner
            oneDirectionVisibilityScanners.offer(downOneDirectionVisibilityScanner)
            oneDirectionVisibilityScanners.offer(upOneDirectionVisibilityScanner)
        }
        while (oneDirectionVisibilityScanners.isNotEmpty()) {
            val walker = oneDirectionVisibilityScanners.poll()
            if (walker.scanCell()) {
                oneDirectionVisibilityScanners.offer(walker)
            }
        }
    }

    fun part1(input: List<String>): Int {
        val grid = Grid.init(input)
        processGrid(grid)
        return grid.getVisible().size
    }

    fun part2(input: List<String>): Int {
        val grid = Grid.init(input)
        processGrid(grid)
        return grid.getVisible()
            .map { cell ->
                ScenicViewScanner(grid, cell).scan()
            }
            .max()
    }

// test if implementation meets criteria from the description, like:
    val testInput = readInput("Day08")
    println(part1(testInput))
    println(part2(testInput))
}