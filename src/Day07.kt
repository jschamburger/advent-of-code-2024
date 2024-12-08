enum class Operation { ADD, MULTIPLY, CONCATENATE }
data class OperationAndNumber(val operation: Operation, val number: Long)

fun main() {
    fun evaluate(equation: List<OperationAndNumber>): Long {
        return equation.fold(0) { acc, operationAndNumber ->
            val result = when(operationAndNumber.operation) {
                Operation.ADD -> acc + operationAndNumber.number
                Operation.MULTIPLY -> acc * operationAndNumber.number
                Operation.CONCATENATE -> (acc.toString() + operationAndNumber.number).toLong()
            }
            result
        }
    }

    fun createPermutations(numbers: List<Long>, start: Boolean = false): List<List<OperationAndNumber>> {
        if (numbers.size == 1) {
            return listOf(
                listOf(OperationAndNumber(Operation.ADD, numbers[0])),
                listOf(OperationAndNumber(Operation.MULTIPLY, numbers[0]))
            )
        }
        val addPermutations = createPermutations(numbers.subList(1, numbers.size)).map {
            val list = it.toMutableList()
            list.add(0, OperationAndNumber(Operation.ADD, numbers[0]))
            list
        }
        return if (start) {
            addPermutations
        } else {
            addPermutations + createPermutations(numbers.subList(1, numbers.size)).map {
                val list = it.toMutableList()
                list.add(0, OperationAndNumber(Operation.MULTIPLY, numbers[0]))
                list
            }
        }
    }

    fun createPermutations2(numbers: List<Long>, start: Boolean = false): List<List<OperationAndNumber>> {
        if (numbers.size == 1) {
            return listOf(
                listOf(OperationAndNumber(Operation.ADD, numbers[0])),
                listOf(OperationAndNumber(Operation.MULTIPLY, numbers[0])),
                listOf(OperationAndNumber(Operation.CONCATENATE, numbers[0]))
            )
        }
        val addPermutations = createPermutations2(numbers.subList(1, numbers.size)).map {
            val list = it.toMutableList()
            list.add(0, OperationAndNumber(Operation.ADD, numbers[0]))
            list
        }
        return if (start) {
            addPermutations
        } else {
            addPermutations + createPermutations2(numbers.subList(1, numbers.size)).map {
                val list = it.toMutableList()
                list.add(0, OperationAndNumber(Operation.MULTIPLY, numbers[0]))
                list
            } + createPermutations2(numbers.subList(1, numbers.size)).map {
                val list = it.toMutableList()
                list.add(0, OperationAndNumber(Operation.CONCATENATE, numbers[0]))
                list
            }
        }
    }

    fun isResultPossible(result: Long, numbers: List<Long>): Boolean {
        return createPermutations(numbers, start = true).any { evaluate(it) == result}
    }

    fun isResultPossible2(result: Long, numbers: List<Long>): Boolean {
        return createPermutations2(numbers, start = true).any { evaluate(it) == result}
    }

    fun parseInput(input: List<String>) = input.map { line ->
        val split = line.split(":")
        val result = split[0].toLong()
        val numbers = split[1].split(" ").filterNot { it.isBlank() }.map { it.toLong() }
        result to numbers
    }

    fun part1(input: List<String>): Long {
        return parseInput(input).sumOf { (result, numbers) ->
            if (isResultPossible(result, numbers)) {
                result
            } else {
                0
            }
        }
    }

    fun part2(input: List<String>): Long {
        return parseInput(input).sumOf { (result, numbers) ->
            if (isResultPossible2(result, numbers)) {
                result
            } else {
                0
            }
        }
    }

    // Or read a large test input from the `src/Day01_test.txt` file:
    val testInput = readInput("Day07_test")
    check(part1(testInput) == 3749L)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day07")
    part1(input).println()

    check(part2(testInput) == 11387L)
    part2(input).println()
}
