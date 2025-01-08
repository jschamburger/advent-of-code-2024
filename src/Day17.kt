import kotlin.math.max
import kotlin.math.pow

fun main() {

    fun executeProgram(
        initialA: Long,
        initialB: Long,
        initialC: Long,
        program: List<Int>
    ): List<Int> {
        var registerA = initialA
        var registerB = initialB
        var registerC = initialC
        fun comboOperand(operand: Int): Long {
            return when (operand) {
                4 -> registerA
                5 -> registerB
                6 -> registerC
                else -> operand.toLong()
            }
        }

        var instructionPointer = 0
        val output = mutableListOf<Int>()
        while (program.size > instructionPointer) {
            val instruction = program[instructionPointer]
            val operand = program[instructionPointer + 1]
            when (instruction) {
                0 -> registerA = (registerA / 2.0.pow(comboOperand(operand).toDouble())).toLong()
                1 -> registerB = registerB.xor(operand.toLong())
                2 -> registerB = comboOperand(operand) % 8
                3 -> {
                    if (registerA != 0L) {
                        instructionPointer = operand
                        continue
                    }
                }

                4 -> registerB = registerB.xor(registerC)
                5 -> output.add((comboOperand(operand) % 8).toInt())
                6 -> registerB = (registerA / 2.0.pow(comboOperand(operand).toDouble())).toLong()
                7 -> registerC = (registerA / 2.0.pow(comboOperand(operand).toDouble())).toLong()
            }
            instructionPointer += 2
        }
        return output
    }

    fun part1(input: List<String>): String {
        // parse registers and program
        val initialA: Long = input[0].split(": ")[1].toLong()
        val initialB: Long = input[1].split(": ")[1].toLong()
        val initialC: Long = input[2].split(": ")[1].toLong()
        val program = input[4].split(": ")[1].split(",").map { it.toInt() }
        return executeProgram(initialA, initialB, initialC, program).joinToString(",")
    }

    fun findNumberOfMatchedDigits(listA: List<Int>, listB: List<Int>): Int {
        for (i in listA.indices) {
            if (listA[listA.size - i - 1] != listB[listA.size - i - 1]) {
                return i
            }
        }
        return listA.size
    }

    fun part2(input: List<String>): Long {
        val program = input[4].split(": ")[1].split(",").map { it.toInt() }
        var i = 8.0.pow(15).toLong()
        var power = 14
        var matchedDigits = 0
        while (i < 8.0.pow(16)) {
            val output =
                executeProgram(initialA = i, initialB = 0, initialC = 0, program = program)
            if (output == program) {
                break
            }
            if (output.size == program.size) {
                val newMatchedDigits = findNumberOfMatchedDigits(output, program)
                if (newMatchedDigits > matchedDigits) {
                    matchedDigits = newMatchedDigits
                    power = max(14 - matchedDigits, 0)
                }
            }
            i += 8.0.pow(power).toInt()
        }
        return i
    }

    // Or read a large test input from the `src/Day01_test.txt` file:
    val testInput = readInput("Day17_test")
    val part1 = part1(testInput)
    part1.println()
    check(part1 == "4,6,3,5,6,3,5,2,1,0")

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day17")
    part1(input).println()

//    val testInput2 = readInput("Day17_test2")
//    check(part2(testInput2) == 117440L)
    part2(input).println()
}
