fun main() {

    data class MatrixAndRobot(val matrix: Matrix<Char>, val robotPosition: Coordinate)

    fun Matrix<Char>.numberOf(char: Char) = this.fold(0) { acc, chars -> acc + chars.count { it == char } }

    fun tryToMoveItem(matrixAndRobot: MatrixAndRobot, startPosition: Coordinate, direction: Direction): MatrixAndRobot {
        val charMatrix = matrixAndRobot.matrix
        val targetPosition = startPosition.neighbour(direction)
        val item = charMatrix[startPosition]
        val newRobotPosition = if (item == '@') {
            targetPosition
        } else {
            matrixAndRobot.robotPosition
        }
        return if (charMatrix[targetPosition] == '.') {
            MatrixAndRobot(charMatrix.set(targetPosition, item).set(startPosition, '.'), newRobotPosition)
        } else if (charMatrix[targetPosition] == 'O') {
            val afterMove = tryToMoveItem(matrixAndRobot, targetPosition, direction)
            if (afterMove != matrixAndRobot) {
                MatrixAndRobot(afterMove.matrix.set(targetPosition, item).set(startPosition, '.'), newRobotPosition)
            } else {
                matrixAndRobot
            }
        } else {
            matrixAndRobot
        }
    }

    fun part1(input: List<String>): Int {
        val emptyLineIndex = input.indexOf("")
        var robotPosition = Coordinate(-1, -1)
        val charMatrix: Matrix<Char> = input.subList(0, emptyLineIndex).map {
            it.toList()
        }.foldIndexed(listOf()) { outerIndex, acc, chars ->
            chars.forEachIndexed { innerIndex, char ->
                if (char == '@') {
                    robotPosition = Coordinate(outerIndex, innerIndex)
                }
            }
            acc + listOf(chars)
        }

        val moves = input.subList(emptyLineIndex + 1, input.size).joinToString("").map { char ->
            Direction.fromChar(char)
        }
        val target = moves.fold(MatrixAndRobot(charMatrix, robotPosition)) { matrixAndRobot, move ->
            // move robot and update robot position and charMatrix
            val newMatrixAndRobot = tryToMoveItem(matrixAndRobot, matrixAndRobot.robotPosition, move)
            newMatrixAndRobot
        }
        return target.matrix.coordinates().fold(0) { acc, coordinate ->
            if (target.matrix[coordinate] == 'O') {
                acc + coordinate.y * 100 + coordinate.x
            } else {
                acc
            }
        }
    }

    fun tryToMoveRobot(
        matrixAndRobot: MatrixAndRobot,
        direction: Direction
    ): MatrixAndRobot {
        val matrix = matrixAndRobot.matrix
        val robotPosition = matrixAndRobot.robotPosition
        // collect items to be moved
        val itemsToBeMoved = mutableListOf<Coordinate>()
        when (direction) {
            Direction.LEFT -> {
                var nextIndex = robotPosition.neighbour(direction)
                while (matrix.safeGet(nextIndex) in listOf('[', ']')) {
                    itemsToBeMoved.add(nextIndex)
                    itemsToBeMoved.add(nextIndex.left())
                    nextIndex = nextIndex.left().left()
                }
            }

            Direction.RIGHT -> {
                var nextElement = robotPosition.neighbour(direction)
                while (matrix.safeGet(nextElement) in listOf('[', ']')) {
                    itemsToBeMoved.add(nextElement)
                    itemsToBeMoved.add(nextElement.right())
                    nextElement = nextElement.right().right()
                }
            }

            Direction.TOP, Direction.BOTTOM -> {
                val nextElements = mutableListOf<Pair<Coordinate, Coordinate>>()
                val neighbour = matrix.safeGet(robotPosition.neighbour(direction))
                if (neighbour in listOf('[', ']')) {
                    if (neighbour == '[') {
                        nextElements.add(
                            robotPosition.neighbour(direction) to robotPosition.neighbour(direction).right()
                        )
                        itemsToBeMoved.add(robotPosition.neighbour(direction))
                        itemsToBeMoved.add(robotPosition.neighbour(direction).right())
                    } else if (neighbour == ']') {
                        nextElements.add(
                            robotPosition.neighbour(direction).left() to robotPosition.neighbour(direction)
                        )
                        itemsToBeMoved.add(robotPosition.neighbour(direction).left())
                        itemsToBeMoved.add(robotPosition.neighbour(direction))
                    }
                }
                while (nextElements.isNotEmpty()) {
                    val element = nextElements.removeFirst()
                    listOf(element.first, element.second).forEach {
                        val elementNeighbour = matrix.safeGet(it.neighbour(direction))
                        if (elementNeighbour in listOf('[', ']')) {
                            if (elementNeighbour == '[') {
                                if (!itemsToBeMoved.containsAll(
                                        listOf(
                                            it.neighbour(direction),
                                            it.neighbour(direction).right()
                                        )
                                    )
                                ) {
                                    nextElements.add(
                                        it.neighbour(direction) to it.neighbour(direction).right()
                                    )
                                    itemsToBeMoved.add(it.neighbour(direction))
                                    itemsToBeMoved.add(it.neighbour(direction).right())
                                }
                            } else if (elementNeighbour == ']') {
                                if (!itemsToBeMoved.containsAll(
                                        listOf(
                                            it.neighbour(direction).left(),
                                            it.neighbour(direction)
                                        )
                                    )
                                ) {
                                    nextElements.add(
                                        it.neighbour(direction).left() to it.neighbour(direction)
                                    )
                                    itemsToBeMoved.add(it.neighbour(direction).left())
                                    itemsToBeMoved.add(it.neighbour(direction))
                                }
                            }
                        }
                    }
                }
            }
        }
        var newMatrix = matrix
        itemsToBeMoved.reversed().forEach { coordinate ->
            // move item to direction
            newMatrix = newMatrix.set(coordinate.neighbour(direction), newMatrix[coordinate]).set(coordinate, '.')
        }
        val newRobotPosition = robotPosition.neighbour(direction)
        newMatrix = newMatrix.set(newRobotPosition, '@')
        newMatrix = newMatrix.set(robotPosition, '.')
        // check if the number of # is the same
        return if (matrix.numberOf('#') == newMatrix.numberOf('#')) {
            MatrixAndRobot(newMatrix, newRobotPosition)
        } else {
            matrixAndRobot
        }
    }

    fun part2(input: List<String>): Int {
        val emptyLineIndex = input.indexOf("")
        var robotPosition = Coordinate(-1, -1)
        val charMatrix: Matrix<Char> = input.subList(0, emptyLineIndex).map {
            it.toList()
        }.foldIndexed(listOf()) { outerIndex, acc, chars ->
            val duplicated = chars.foldIndexed(listOf<Char>()) { innerIndex, innerAcc, char ->
                when (char) {
                    '@' -> {
                        robotPosition = Coordinate(outerIndex, innerIndex * 2)
                        innerAcc + char + '.'
                    }

                    'O' -> {
                        innerAcc + '[' + ']'
                    }

                    '#' -> {
                        innerAcc + '#' + '#'
                    }

                    else -> {
                        innerAcc + '.' + '.'
                    }
                }
            }
            val mutableAcc = acc.toMutableList()
            mutableAcc.add(duplicated)
            mutableAcc
        }

        val moves = input.subList(emptyLineIndex + 1, input.size).joinToString("").map { char ->
            Direction.fromChar(char)
        }
        val target = moves.fold(MatrixAndRobot(charMatrix, robotPosition)) { matrixAndRobot, move ->
            // move robot and update robot position and charMatrix
            tryToMoveRobot(matrixAndRobot, move)
        }
        return target.matrix.coordinates().fold(0) { acc, coordinate ->
            if (target.matrix[coordinate] == '[') {
                acc + coordinate.y * 100 + coordinate.x
            } else {
                acc
            }
        }
    }

    // Or read a large test input from the `src/Day01_test.txt` file:
    val testInput = readInput("Day15_test")
    check(part1(testInput) == 10092)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day15")
    part1(input).println()

    check(part2(testInput) == 9021)
    part2(input).println()
}
