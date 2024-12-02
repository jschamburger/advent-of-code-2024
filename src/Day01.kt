import kotlin.math.abs

fun main() {
    fun part1(input: List<String>): Int {
        val lists = input.map { line ->
            val split = line.split("\\s+".toRegex())
            Pair(split[0], split[1])
        }.fold(listOf(mutableListOf<Int>(), mutableListOf<Int>())) { acc, pair ->
            acc[0].add(pair.first.toInt())
            acc[1].add(pair.second.toInt())
            acc
        }.map { it.sorted() }
        return lists[0].zip(lists[1]).sumOf { abs(it.first - it.second) }
    }

    fun part2(input: List<String>): Int {
        val lists = input.map { line ->
            val split = line.split("\\s+".toRegex())
            Pair(split[0], split[1])
        }.fold(listOf(mutableListOf<Int>(), mutableListOf<Int>())) { acc, pair ->
            acc[0].add(pair.first.toInt())
            acc[1].add(pair.second.toInt())
            acc
        }.map { it.sorted() }
        val mapValues = lists[1].groupBy { it }.mapValues { it.value.size }
        return lists[0].sumOf {
            it * (mapValues[it] ?: 0)
        }
    }

    // Test if implementation meets criteria from the description, like:
    check(part1(listOf("0 1")) == 1)

    // Or read a large test input from the `src/Day01_test.txt.txt` file:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 11)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day01")
    part1(input).println()

    check(part2(testInput) == 31)
    part2(input).println()
}
