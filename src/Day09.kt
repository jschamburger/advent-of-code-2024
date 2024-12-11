sealed class FileOrSpace(open val size: Int) {
    data class File(val number: Int, override val size: Int): FileOrSpace(size)
    data class Space(override val size: Int): FileOrSpace(size)
}

fun main() {
    fun part1(input: List<String>): Long {
        val stringRepresentation = input[0].toCharArray().map { it.digitToInt() }.mapIndexed { index, number ->
            if (index % 2 == 0) {
                buildString {
                    for (i in 0 until number) {
                        append("${index / 2}#")
                    }
                }
            } else {
                buildString {
                    for (i in 0 until number) {
                        append(".#")
                    }
                }
            }
        }.joinToString(separator = "").run { removeRange(length - 1, length) }

        val blocks = stringRepresentation.split("#").toMutableList()
        while (true) {
            val indexOfLastNumberBlock = blocks.indexOfLast { it != "." }
            val indexOfFirstDot = blocks.indexOfFirst { it == "." }
            if (indexOfFirstDot > indexOfLastNumberBlock) {
                break
            }
            blocks[indexOfFirstDot] = blocks[indexOfLastNumberBlock]
            blocks[indexOfLastNumberBlock] = "."
        }

        return blocks.filter { it != "." }.foldIndexed(0L) { index, acc, block ->
            acc + block.toInt() * index
        }
    }

    fun part2(input: List<String>): Long {
        val filesAndSpaces = input[0].toCharArray().map { it.digitToInt() }.mapIndexed { index, number ->
            if (index % 2 == 0) {
                FileOrSpace.File(index / 2, number)
            } else {
                FileOrSpace.Space(number)
            }
        }.filter { it != FileOrSpace.Space(0) }.toMutableList()

        val files = filesAndSpaces.filterIsInstance<FileOrSpace.File>()
        files.reversed().forEach { file ->
            val indexOfFile = filesAndSpaces.indexOf(file)
            val index = filesAndSpaces.indexOfFirst { it is FileOrSpace.Space && it.size >= file.size }
            if (index != -1 && index < indexOfFile) {
                val removed = filesAndSpaces.removeAt(index)
                filesAndSpaces.add(index, file)
                filesAndSpaces[indexOfFile] = FileOrSpace.Space(file.size)
                if (removed.size > file.size) {
                    filesAndSpaces.add(index + 1, FileOrSpace.Space(removed.size - file.size))
                }
            }
        }

        return filesAndSpaces.fold(0 to 0L) { acc, fileOrSpace ->
            when (fileOrSpace) {
                is FileOrSpace.Space -> acc.copy(first = acc.first + fileOrSpace.size)
                is FileOrSpace.File -> {
                    val currentIndex = acc.first
                    var addedValue = 0L
                    for (i in 0 until fileOrSpace.size) {
                        addedValue += fileOrSpace.number * (currentIndex + i)
                    }
                    acc.copy(first = acc.first + fileOrSpace.size, second = acc.second + addedValue)
                }
            }
        }.second
    }

    // Or read a large test input from the `src/Day01_test.txt` file:
    val testInput = readInput("Day09_test")
    check(part1(testInput) == 1928L)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day09")
    part1(input).println()

    check(part2(testInput) == 2858L)
    part2(input).println()
}
