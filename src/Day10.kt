fun main() {
    fun createMatrixAndFindTrailHeads(input: List<String>): Pair<List<MutableList<Int>>, List<Coordinate>> {
        val (charMatrix, trailHeads) = input.map {
            it.toMutableList()
        }.foldIndexed(Pair(mutableListOf<MutableList<Int>>(), mutableListOf<Coordinate>())) { index, acc, chars ->
            acc.first.add(chars.map { it.digitToInt() }.toMutableList())
            chars.onEachIndexed { innerIndex, c ->
                if (c.digitToInt() == 0) {
                    acc.second.add(Coordinate(index, innerIndex))
                }
            }
            acc
        }
        return Pair(charMatrix, trailHeads)
    }

    fun findTrails(head: Coordinate, matrix: Matrix<Int>): List<List<Coordinate>> {
        val currentTrails = mutableSetOf(listOf(head))
        val completeTrails = mutableListOf<List<Coordinate>>()
        while (currentTrails.isNotEmpty()) {
            val trail = currentTrails.removeFirst()
            if (trail.size == 10) {
                completeTrails.add(trail)
            } else {
                val headOfTrail = trail.last()
                for (next in headOfTrail.neighbours()) {
                    if (matrix.safeGet(next) == matrix.get(headOfTrail) + 1) {
                        currentTrails.add(trail + next)
                    }
                }
            }
        }
        return completeTrails
    }

    fun part1(input: List<String>): Int {
        val (matrix, trailHeads) = createMatrixAndFindTrailHeads(input)
        return trailHeads.sumOf { head ->
            findTrails(head, matrix).distinctBy { it.last() }.size
        }
    }

    fun part2(input: List<String>): Int {
        val (matrix, trailHeads) = createMatrixAndFindTrailHeads(input)
        return trailHeads.sumOf { head ->
            findTrails(head, matrix).size
        }
    }

    // Or read a large test input from the `src/Day01_test.txt` file:
    val testInput = readInput("Day10_test")
    check(part1(testInput) == 36)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day10")
    part1(input).println()

    check(part2(testInput) == 81)
    part2(input).println()
}
