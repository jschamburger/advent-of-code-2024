fun main() {
    data class Robot(val position: Coordinate, val velocityX: Int, val velocityY: Int)

    fun printRobots(robots: List<Robot>, gridSizeHorizontal: Int, gridSizeVertical: Int) {
        for (i in 0 until gridSizeVertical) {
            for (j in 0 until gridSizeHorizontal) {
                if (robots.any { it.position == Coordinate(i, j) }) {
                    print('#')
                } else {
                    print('.')
                }
            }
            println()
        }
    }

    val regex = Regex("p=(-?\\d+),(-?\\d+) v=(-?\\d+),(-?\\d+)")

    fun MatchResult.groupToInt(groupNumber: Int): Int = this.groups[groupNumber]?.value?.toInt() ?: 0

    fun parseRobots(input: List<String>): List<Robot> {
        val robots = input.mapNotNull { line ->
            val matchResult = regex.matchEntire(line)
            matchResult?.let {
                Robot(
                    position = Coordinate(
                        matchResult.groupToInt(2),
                        matchResult.groupToInt(1),
                    ),
                    velocityX = matchResult.groupToInt(3),
                    velocityY = matchResult.groupToInt(4),
                )
            }
        }
        return robots
    }

    fun moveRobotOneStep(robot: Robot, gridSizeHorizontal: Int, gridSizeVertical: Int): Robot {
        val positionX = (robot.position.x + robot.velocityX).let {
            if (it >= gridSizeHorizontal) {
                it - gridSizeHorizontal
            } else if (it < 0) {
                gridSizeHorizontal + it
            } else {
                it
            }
        }
        val positionY = (robot.position.y + robot.velocityY).let {
            if (it >= gridSizeVertical) {
                it - gridSizeVertical
            } else if (it < 0) {
                gridSizeVertical + it
            } else {
                it
            }
        }
        val newPosition = Coordinate(
            x = positionX,
            y = positionY
        )
        return robot.copy(position = newPosition)
    }

    fun calculateSafetyFactor(
        after100seconds: List<Robot>,
        gridSizeHorizontal: Int,
        gridSizeVertical: Int
    ) = after100seconds.groupBy { robot ->
        val middleX = gridSizeHorizontal / 2
        val middleY = gridSizeVertical / 2
        when {
            robot.position.x < middleX && robot.position.y < middleY -> 0
            robot.position.x > middleX && robot.position.y < middleY -> 1
            robot.position.x < middleX && robot.position.y > middleY -> 2
            robot.position.x > middleX && robot.position.y > middleY -> 3
            else -> -1
        }
    }.filter { it.key != -1 }.values.fold(1) { acc, list -> acc * list.size }

    fun part1(input: List<String>): Int {
        val robots = parseRobots(input)
        val gridSizeVertical = robots.maxOf { it.position.y + 1 }
        val gridSizeHorizontal = robots.maxOf { it.position.x + 1 }

        val after100seconds = (1..100).fold(robots) { acc, i ->
            // move each robot according to the rules
            acc.map { robot ->
                moveRobotOneStep(robot, gridSizeHorizontal, gridSizeVertical)
            }
        }
        return calculateSafetyFactor(after100seconds, gridSizeHorizontal, gridSizeVertical)
    }

    fun part2(input: List<String>): Int {
        val robots = parseRobots(input)
        val gridSizeVertical = robots.maxOf { it.position.y + 1 }
        val gridSizeHorizontal = robots.maxOf { it.position.x + 1 }

        var safetyFactor = Int.MAX_VALUE

        (1..10000).fold(robots) { acc, i ->
            // move each robot according to the rules
            val robotsNew = acc.map { robot ->
                moveRobotOneStep(robot, gridSizeHorizontal, gridSizeVertical)
            }
            val newSafetyFactor = calculateSafetyFactor(robotsNew, gridSizeHorizontal, gridSizeVertical)
            if (newSafetyFactor <= safetyFactor) {
                safetyFactor = newSafetyFactor
                println("After $i seconds")
                printRobots(robotsNew, gridSizeHorizontal, gridSizeVertical)
            }
            robotsNew
        }
        return input.size
    }

    // Or read a large test input from the `src/Day01_test.txt` file:
    val testInput = readInput("Day14_test")
    check(part1(testInput) == 12)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day14")
    part1(input).println()

//    check(part2(testInput) == 1)
    part2(input).println()
}
