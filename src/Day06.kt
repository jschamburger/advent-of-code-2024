data class Guard (var direction: Char, var position: Pair<Int, Int>)

fun Guard.nextPosition(): Pair<Int, Int> {
    return when (direction) {
        '^' -> Pair(position.first - 1, position.second)
        '>' -> Pair(position.first, position.second + 1)
        '<' -> Pair(position.first, position.second - 1)
        else -> Pair(position.first + 1, position.second)
    }
}

fun Guard.ninetyDegreesDirection(): Char {
    return when (direction) {
        '^' -> '>'
        '>' -> 'v'
        '<' -> '^'
        else -> '<'
    }
}

fun main() {
    fun isInArea(nextPosition: Pair<Int, Int>, charMatrix: List<List<Char>>): Boolean {
        return nextPosition.first > -1 && nextPosition.second > -1 && nextPosition.first < charMatrix.size && nextPosition.second < charMatrix[0].size
    }

    operator fun List<List<Char>>.get(position: Pair<Int, Int>) = this.getOrNull(position.first)?.getOrNull(position.second)

    fun createMatrixAndGuard(input: List<String>): Pair<List<MutableList<Char>>, Guard> {
        var startIndex = Pair(-1, -1)
        val charMatrix = input.map {
            it.toMutableList()
        }.foldIndexed(mutableListOf<MutableList<Char>>()) { index, acc, chars ->
            acc.add(chars)
            chars.onEachIndexed { innerIndex, c ->
                if (c == '^') {
                    startIndex = index to innerIndex
                }
            }
            acc
        }.toList()
        return Pair(charMatrix, Guard('^', startIndex))
    }

    fun part1(input: List<String>): Int {
        val pair = createMatrixAndGuard(input)
        val charMatrix = pair.first
        var guard = pair.second
        while (isInArea(guard.position, charMatrix)) {
            charMatrix[guard.position.first][guard.position.second] = 'X'
            if (charMatrix[guard.nextPosition()] == '#') {
                guard = Guard(guard.ninetyDegreesDirection(), guard.position)
            }
            guard = Guard(guard.direction, guard.nextPosition())
        }
        return charMatrix.sumOf { it.count { char -> char == 'X' } }
    }

    fun containsLoop(
        charMatrix: List<List<Char>>,
        initialGuard: Guard
    ): Boolean {
        var guard = initialGuard
        val hitObstacles = mutableSetOf<Pair<Char, Pair<Int, Int>>>()
        while (isInArea(guard.position, charMatrix)) {
            if (charMatrix[guard.nextPosition()] in listOf('#', 'O')) {
                if (!hitObstacles.add(guard.direction to guard.nextPosition())) {
                    return true
                }
                guard = Guard(guard.ninetyDegreesDirection(), guard.position)
            } else {
                guard = Guard(guard.direction, guard.nextPosition())
            }
        }
        return false
    }

    fun createPermutations(charMatrix: List<MutableList<Char>>): List<List<List<Char>>> {
        val acc = mutableListOf<List<List<Char>>>()
        charMatrix.indices.forEach { y ->
            charMatrix[y].indices.forEach { x ->
                if (charMatrix[y][x] == '.') {
                    val newLine = charMatrix[y].toMutableList()
                    newLine[x] = 'O'
                    val newCharMatrix = charMatrix.toMutableList()
                    newCharMatrix[y] = newLine
                    acc.add(newCharMatrix)
                }
            }
        }
        return acc.toList()
    }

    fun part2(input: List<String>): Int {
        val pair = createMatrixAndGuard(input)
        return createPermutations(pair.first).count { permutation ->
            val containsLoop = containsLoop(permutation, pair.second)
            containsLoop
        }
    }

    // Or read a large test input from the `src/Day01_test.txt` file:
    val testInput = readInput("Day06_test")
    check(part1(testInput) == 41)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day06")
    part1(input).println()

    check(part2(testInput) == 6)
    part2(input).println()
}
