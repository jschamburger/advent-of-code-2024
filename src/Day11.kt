import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.util.concurrent.ConcurrentHashMap

suspend fun main() {
    val regex = Regex("(\\d+)")
    val map = ConcurrentHashMap<String, Long>()
    fun calculateNumberOfStones(stone: Long, iterations: Int): Long {
        val stoneAsString = stone.toString()
        val key = "$stone-$iterations"
        val itemInMap = map[key]
        return when {
            itemInMap != null -> itemInMap
            iterations == 0 -> 1
            stone == 0L -> {
                val number = calculateNumberOfStones(1L, iterations - 1)
                map[key] = number
                number
            }

            (stoneAsString.length % 2) == 0 -> {
                val stone1 = stoneAsString.substring(0, stoneAsString.length / 2).toLong()
                val stone2 = stoneAsString.substring(stoneAsString.length / 2, stoneAsString.length).toLong()
                val number1 = calculateNumberOfStones(stone1, iterations - 1)
                val number2 = calculateNumberOfStones(stone2, iterations - 1)
                map[key] = number1 + number2
                number1 + number2
            }

            else -> {
                val number = calculateNumberOfStones(stone * 2024L, iterations - 1)
                map[key] = number
                number
            }
        }
    }

    suspend fun part1(input: List<String>): Long = coroutineScope {
        val stones = regex.findAll(input[0]).map { it.value.toLong() }.toList()
        stones.println()
        stones.map { stone ->
            async { calculateNumberOfStones(stone, 25) }
        }.awaitAll().sum()
    }

    fun part2(input: List<String>): Long {
        val stones = regex.findAll(input[0]).map { it.value.toLong() }.toList()
        stones.println()
        return stones.sumOf { stone ->
            calculateNumberOfStones(stone, 75)
        }
    }

    // Or read a large test input from the `src/Day01_test.txt` file:
    val testInput = readInput("Day11_test")
    check(part1(testInput) == 55312L)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day11")
    part1(input).println()

    part2(input).println()
}
