import java.util.regex.Pattern

class FileSystem(val root: Directory) {

    companion object Reader {
        private val spacePatter = Pattern.compile("\\s+")

        interface Output

        class DirOutput(val name: String) : Output
        class FileOutput(val name: String, val size: Long) : Output

        interface Command : Output
        class CdCommand(val dir: String) : Command
        class LsCommand : Command

        fun read(input: List<String>): FileSystem {
            val tree = input.map { line ->
                parseOutput(line)
            }.fold(Directory(File("/", true, 0, null), mutableListOf())) { acc, out ->
                when (out) {
                    is FileOutput -> {
                        acc.addItem(File(out.name, false, out.size, acc))
                        acc
                    }

                    is DirOutput -> {
                        acc.addItem(Directory(File(out.name, true, 0, acc), mutableListOf()))
                        acc
                    }

                    is CdCommand -> {
                        acc.getDir(out.dir) ?: acc
                    }

                    is LsCommand -> acc
                    else -> throw IllegalArgumentException("Unknown output")
                }
            }
            var root: Directory = tree
            while (root.parent() != null) {
                root = root.parent()!!
            }
            return FileSystem(root)
        }

        private fun parseOutput(outputLine: String): Output {
            return with(outputLine) {
                when {
                    startsWith("\$") -> parseCommand(outputLine)
                    startsWith("dir") -> DirOutput(replace("dir", "").trim())
                    matches(Regex("\\d+ .+")) -> {
                        val fileOutParts = split(spacePatter)
                        FileOutput(name = fileOutParts[1], size = fileOutParts[0].toLong())
                    }

                    else -> throw IllegalArgumentException("Unknown output")
                }
            }
        }

        private fun parseCommand(commandStr: String): Command {
            return with(commandStr.replace("\$", "").trim()) {
                when {
                    startsWith("cd") -> CdCommand(replace("cd", "").trim())
                    startsWith("ls") -> LsCommand()
                    else -> throw IllegalArgumentException("Unknown command")
                }
            }
        }
    }
}

interface FsItem {
    fun name(): String
    fun parent(): Directory?
    fun size(): Long
    fun absolutePath(): String
    fun isDirectory(): Boolean
}

class File(
    private val name: String,
    private val isDirectory: Boolean = false,
    private val size: Long = 0,
    private val parent: Directory?
) : FsItem {
    private val absolutePath: String by lazy {
        if (parent == null) {
            name
        } else {
            val parentAbsolutePath = parent.absolutePath()
            if (parentAbsolutePath.endsWith("/")) {
                parentAbsolutePath + name
            } else {
                "$parentAbsolutePath/$name"
            }
        }
    }

    override fun name(): String {
        return name
    }

    override fun parent(): Directory? {
        return parent
    }

    override fun size(): Long {
        return size
    }

    override fun absolutePath(): String {
        return absolutePath
    }

    override fun isDirectory(): Boolean {
        return isDirectory
    }

    override fun toString(): String {
        return name;
    }
}

class Directory(private val f: File, private val contents: MutableList<FsItem> = mutableListOf()) : FsItem by f {
    private val size: Long by lazy { contents.sumOf { it.size() } }
    fun addItem(fsItem: FsItem) {
        contents.add(fsItem)
    }

    fun getDir(name: String): Directory? {
        if (name == "..") {
            return f.parent()
        }
        return contents
            .find { item -> name == item.name() && item is Directory } as Directory?
    }

    fun accept(visitor: FsItemVisitor) {
        visitor.visit(this)
        contents.forEach { content ->
            visitor.visit(content)
            if (content is Directory) {
                content.accept(visitor)
            }
        }
    }

    override fun size(): Long {
        return size
    }

    override fun toString(): String {
        return "dir ${f.name()}"
    }
}


interface FsItemVisitor {
    fun visit(fsItem: FsItem)
}

class TaskPart1 : FsItemVisitor {
    private val dirToSize: MutableMap<String, Long> = mutableMapOf()
    override fun visit(fsItem: FsItem) {
        if (fsItem.isDirectory()) {
            val size = fsItem.size()
            if (size < 100000) {
                dirToSize.put(fsItem.absolutePath(), size)
            }
        }
    }

    fun getSpaceAvailableForCleanup(): Long {
        return dirToSize.values.sum()
    }
}

class TaskPart2 : FsItemVisitor {
    private val fsSize = 70000000
    private val requiredFreeSpace = 30000000
    private val dirToSize: MutableMap<String, Long> = mutableMapOf()
    override fun visit(fsItem: FsItem) {
        if (fsItem.isDirectory()) {
            dirToSize[fsItem.absolutePath()] = fsItem.size()
        }
    }

    fun getSpaceAvailableForCleanup(): Long {
        val busySpace = dirToSize["/"]!!
        val spaceToFree = requiredFreeSpace - (fsSize - busySpace)
        return dirToSize.values
            .sorted()
            .first { it > spaceToFree }
    }
}

fun main() {

    fun part1(input: List<String>): Long {
        val fs = FileSystem.read(input)
        val taskPart1 = TaskPart1()
        fs.root.accept(taskPart1)
        return taskPart1.getSpaceAvailableForCleanup()
    }

    fun part2(input: List<String>): Long {
        val fs = FileSystem.read(input)
        val taskPart2 = TaskPart2()
        fs.root.accept(taskPart2)
        return taskPart2.getSpaceAvailableForCleanup()
    }

// test if implementation meets criteria from the description, like:
    val testInput = readInput("Day07")
    println(part1(testInput))
    println(part2(testInput))
}
