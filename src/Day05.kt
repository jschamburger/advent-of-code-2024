import java.util.regex.Pattern

fun main() {
    val rulePattern = Pattern.compile("(\\d+)\\|(\\d+)")
    fun parseRules(
        input: List<String>,
        delimiterIndex: Int
    ): List<Pair<Int, Int>> {
        val rulesLines = input.subList(0, delimiterIndex)
        val rules = rulesLines.map { line ->
            val matcher = rulePattern.matcher(line)
            matcher.matches()
            matcher.group(1).toInt() to matcher.group(2).toInt()
        }
        return rules
    }

    fun parseUpdates(
        input: List<String>,
        delimiterIndex: Int
    ): List<List<Int>> {
        val updatesLines = input.subList(delimiterIndex + 1, input.size).map { line ->
            line.split(',').map { it.toInt() }
        }
        return updatesLines
    }

    fun doAllRulesApply(
        rules: List<Pair<Int, Int>>,
        target: List<Int>
    ) = rules.all { rule ->
        val secondIndex = target.indexOf(rule.second).let { if (it == -1) Int.MAX_VALUE else it }
        target.indexOf(rule.first) < secondIndex
    }

    fun part1(input: List<String>): Int {
        val delimiterIndex = input.indexOf("")
        // parse rules
        val rules = parseRules(input, delimiterIndex)
        // parse manual updates
        val updatesLines = parseUpdates(input, delimiterIndex)

        return updatesLines.filter { update ->
            // check rules for update
            doAllRulesApply(rules, update)
        }.sumOf { it[it.lastIndex / 2] }
    }

    fun part2(input: List<String>): Int {
        val delimiterIndex = input.indexOf("")
        // parse rules
        val rules = parseRules(input, delimiterIndex)
        // parse manual updates
        val updatesLines = parseUpdates(input, delimiterIndex)

        return updatesLines.map { update ->
            // apply rules for update
            val correctedUpdate = update.toMutableList()
            while (!doAllRulesApply(rules, correctedUpdate)) {
                rules.forEach { rule ->
                    val firstIndex = correctedUpdate.indexOf(rule.first)
                    val secondIndex = correctedUpdate.indexOf(rule.second)
                    if (firstIndex != -1 && secondIndex != -1 && firstIndex > secondIndex) {
                        // move first element to the position before second element
                        val element = correctedUpdate.removeAt(firstIndex)
                        correctedUpdate.add(secondIndex, element)
                    }
                }
            }
            Pair(correctedUpdate, correctedUpdate != update)
        }.filter {
            it.second
        }.map {
            it.first
        }.sumOf { it[it.lastIndex / 2] }
    }

    // Or read a large test input from the `src/Day01_test.txt` file:
    val testInput = readInput("Day05_test")
    check(part1(testInput) == 143)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day05")
    part1(input).println()

    check(part2(testInput) == 123)
    part2(input).println()
}
