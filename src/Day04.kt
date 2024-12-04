private val directions = listOf(Pair(-1,-1), Pair(-1,0), Pair(-1,1), Pair(0,-1), Pair(0,1), Pair(1,-1), Pair(1,0), Pair(1,1))

fun main() {
    fun List<List<Char>>.characterAt(index: Pair<Int, Int>): Char {
        return this.getOrNull(index.first)?.getOrNull(index.second) ?: ' '
    }
    operator fun Pair<Int, Int>.plus(other: Pair<Int, Int>): Pair<Int, Int> {
        return Pair(this.first + other.first, this.second + other.second)
    }
    fun findNumberOfXmases(xIndex: Pair<Int, Int>, charMatrix: List<List<Char>>): Int {
        return directions.count { direction ->
            return@count charMatrix.characterAt(xIndex + direction) == 'M' &&
                    charMatrix.characterAt(xIndex + direction + direction) == 'A' &&
                    charMatrix.characterAt(xIndex + direction + direction + direction) == 'S'
        }
    }

    fun findIndicesForCharAndCreateCharMatrix(input: List<String>, char: Char): Pair<MutableList<Pair<Int, Int>>, List<List<Char>>> {
        val charIndices = mutableListOf<Pair<Int, Int>>()
        val charMatrix = input.map {
            it.toList()
        }.foldIndexed(mutableListOf<List<Char>>()) { index, acc, chars ->
            acc.add(chars)
            chars.onEachIndexed { innerIndex, c ->
                if (c == char) {
                    charIndices.add(Pair(index, innerIndex))
                }
            }
            acc
        }.toList()
        return Pair(charIndices, charMatrix)
    }

    fun part1(input: List<String>): Int {
        val (xIndices, charMatrix) = findIndicesForCharAndCreateCharMatrix(input, 'X')
        return xIndices.sumOf {
            findNumberOfXmases(it, charMatrix)
        }
    }

    fun isAnXmas(index: Pair<Int, Int>, charMatrix: List<List<Char>>): Boolean {
        val diagonalLeftChars =
            setOf(charMatrix.characterAt(index + Pair(-1, -1)), charMatrix.characterAt(index + Pair(1, 1)))
        val diagonalRightChars =
            setOf(charMatrix.characterAt(index + Pair(-1, 1)), charMatrix.characterAt(index + Pair(1, -1)))
        val set = setOf('M', 'S')
        return (diagonalLeftChars == set && diagonalRightChars == set)

    }

    fun part2(input: List<String>): Int {
        val (aIndices, charMatrix) = findIndicesForCharAndCreateCharMatrix(input, 'A')
        return aIndices.count {
            isAnXmas(it, charMatrix)
        }
    }

    // Or read a large test input from the `src/Day01_test.txt` file:
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 18)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day04")
    part1(input).println()

    part2(testInput).println()
    check(part2(testInput) == 9)
    part2(input).println()
}
