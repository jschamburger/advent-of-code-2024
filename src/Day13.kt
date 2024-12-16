import kotlin.math.ceil
import kotlin.math.floor

data class Machine(
    val aX: Double,
    val aY: Double,
    val bX: Double,
    val bY: Double,
    val prizeX: Double,
    val prizeY: Double
)

fun main() {
    val regex = Regex(
        "Button A: X\\+(?<aX>\\d+), Y\\+(?<aY>\\d+)\\s+" +
                "Button B: X\\+(?<bX>\\d+), Y\\+(?<bY>\\d+)\\s+" +
                "Prize: X=(?<prizeX>\\d+), Y=(?<prizeY>\\d+)"
    )

    fun mapToMachines(matches: Sequence<MatchResult>) = matches.map { match ->
        Machine(
            aX = match.groups["aX"]?.value?.toDouble() ?: -1.0,
            aY = match.groups["aY"]?.value?.toDouble() ?: -1.0,
            bX = match.groups["bX"]?.value?.toDouble() ?: -1.0,
            bY = match.groups["bY"]?.value?.toDouble() ?: -1.0,
            prizeX = match.groups["prizeX"]?.value?.toDouble() ?: -1.0,
            prizeY = match.groups["prizeY"]?.value?.toDouble() ?: -1.0,
        )
    }

    fun calculateResult(it: Machine): Pair<Double, Double>? {
        with(it) {
            val numberOfA =
                (prizeY * bX - bY * prizeX) / (aY * bX - bY * aX)
            val numberOfBX = (prizeX - numberOfA * aX) / bX
            val numberOfBY = (prizeY - numberOfA * aY) / bY
            if (numberOfBX == numberOfBY) {
                return Pair(numberOfA, numberOfBX)
            } else {
                return null
            }
        }
    }

    fun part1(input: List<String>): Int {
        val matches = regex.findAll(input.joinToString("\n"))
        return mapToMachines(matches).map {
            calculateResult(it)
        }.filterNotNull()
            .filter { result ->
                result.first >= 0 && result.second >= 0 &&
                        ceil(result.first) == floor(result.first) &&
                        ceil(result.second) == floor(result.second)
            }.sumOf { it.first.toInt() * 3 + it.second.toInt() }
    }

    fun part2(input: List<String>): Double {
        val matches = regex.findAll(input.joinToString("\n"))
        return mapToMachines(matches).map {
            it.copy(prizeX = it.prizeX + 10000000000000.0, prizeY = it.prizeY + 10000000000000.0)
        }.map {
            calculateResult(it)
        }.filterNotNull()
            .filter { result ->
                result.first >= 0 && result.second >= 0 &&
                        ceil(result.first) == floor(result.first) &&
                        ceil(result.second) == floor(result.second)
            }.map { it.first * 3 + it.second }.sum()
    }

    // Or read a large test input from the `src/Day01_test.txt` file:
    val testInput = readInput("Day13_test")
    check(part1(testInput) == 480)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day13")
    part1(input).println()

//    check(part2(testInput) == 1)
    "%.0f".format(part2(input)).println()
}
