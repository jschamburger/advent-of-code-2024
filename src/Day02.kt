data class Distances(val distances: List<Int>, val previousValue: Int?)

fun main() {
    fun isSafe(list: List<Int>): Boolean {
        val distances = list.fold(Distances(emptyList(), null)) { acc, value ->
            if (acc.previousValue == null) {
                acc.copy(previousValue = value)
            } else {
                val newDistances = acc.distances + ((value - acc.previousValue))
                acc.copy(distances = newDistances, previousValue = value)
            }
        }.distances
        return distances.all { it in 1..3 } || distances.all { it in -3..-1 }
    }

    fun isSafeEnough(list: List<Int>): Boolean {
        val listPermutations = List(list.size) { index -> list.toMutableList().apply { removeAt(index) } }
        return isSafe(list) || listPermutations.any { isSafe(it) }
    }

    fun part1(input: List<String>): Int {
        return input.map { line ->
            val list = line.split(" ").map { it.toInt() }
            return@map isSafe(list)
        }.count { it }
    }

    fun part2(input: List<String>): Int {
        return input.map { line ->
            val list = line.split(" ").map { it.toInt() }
            return@map isSafe(list) || isSafeEnough(list)
        }.count { it }
    }

    // Or read a large test input from the `src/Day01_test.txt` file:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 2)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day02")
    part1(input).println()

    check(part2(testInput) == 4)
    part2(input).println()
}
