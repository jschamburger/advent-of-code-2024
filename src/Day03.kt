import java.util.regex.Pattern

fun main() {
    fun doMultiplications(string: String): Int {
        val pattern = Pattern.compile("mul\\((\\d{1,3}),(\\d{1,3})\\)")
        val matcher = pattern.matcher(string)
        var result = 0
        while (matcher.find()) {
            val firstNumber = matcher.group(1).toInt()
            val secondNumber = matcher.group(2).toInt()
            result += firstNumber * secondNumber
        }
        return result
    }

    fun part1(input: List<String>): Int {
        val string = input.joinToString()
        return doMultiplications(string)
    }

    fun part2(input: List<String>): Int {
        val string = input.joinToString()
        val inputWithoutDisabledParts = string.replace(Regex("don\'t\\(\\)(.*?)do\\(\\)"), "").replaceAfter("don't()", "")
        return doMultiplications(inputWithoutDisabledParts)
    }

    // Or read a large test input from the `src/Day01_test.txt` file:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 161)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day03")
    part1(input).println()

    part2(testInput).println()
    check(part2(testInput) == 48)
    part2(input).println()
}
