fun main() {
    data class Maze(val matrix: Matrix<Char>, val start: Coordinate, val end: Coordinate)

    data class Node(
        val coordinate: Coordinate,
        val score: Int,
    ) {
        fun findNeighbours(): List<Node> {
            return Direction.entries.map { Node(this.coordinate.neighbour(it), this.score + 1) }
        }
    }

    fun findCheapestPathToEnd(maze: Maze): Int {
        val unvisitedNodes = mutableSetOf<Node>()
        val nodes =
            maze.matrix.coordinates().filter { maze.matrix[it] != '#' }.map { element ->
                Node(
                    element, Int.MAX_VALUE,
                )
            }
        unvisitedNodes.addAll(nodes)
        val visitedNodes = mutableSetOf<Node>()
        unvisitedNodes.removeIf { it.coordinate == maze.start }
        unvisitedNodes.add(Node(maze.start, 0))
        while (unvisitedNodes.isNotEmpty()) {
            val currentNode = unvisitedNodes.minBy { it.score }
            currentNode.findNeighbours().forEach { neighbour ->
                if (visitedNodes.find { it.coordinate == neighbour.coordinate } == null &&
                    maze.matrix.coordinates().contains(neighbour.coordinate) &&
                    maze.matrix[neighbour.coordinate] != '#'
                ) {
                    unvisitedNodes.add(neighbour)
                }
            }
            visitedNodes.add(currentNode)
            unvisitedNodes.remove(currentNode)
        }
        return visitedNodes.find { it.coordinate == maze.end }?.score ?: -1
    }

    fun generateMaze(input: List<String>, numberOfBytes: Int): Maze {
        val blockedCoordinates = input.map { line ->
            val split = line.split(",")
            Coordinate(y = split[1].toInt(), x = split[0].toInt())
        }.subList(0, numberOfBytes)
        val maxX = blockedCoordinates.maxOf { it.x }
        val maxY = blockedCoordinates.maxOf { it.y }
        val matrix = mutableListOf<List<Char>>()
        (0..maxY).forEach { y ->
            val row = (0..maxX).map { x ->
                if (blockedCoordinates.contains(Coordinate(y = y, x = x))) {
                    '#'
                } else {
                    '.'
                }
            }.toList()
            matrix.add(row)
        }
        val maze = Maze(matrix = matrix, start = Coordinate(0, 0), end = Coordinate(y = maxY, x = maxX))
        return maze
    }

    fun part1(input: List<String>, numberOfBytes: Int): Int {
        // parse coordinates
        val maze = generateMaze(input, numberOfBytes)
        return findCheapestPathToEnd(maze)
    }

    fun part2(input: List<String>, startNumberOfBytes: Int): String {
        // binary search
        var area = startNumberOfBytes to input.size
        while (area.first != area.second) {
            val nextIndex = (area.first + area.second) / 2
            val maze = generateMaze(input, nextIndex)
            val cheapestPathToEnd = findCheapestPathToEnd(maze)
            area = if (cheapestPathToEnd < 0) {
                if (area.first == nextIndex - 1) {
                    return input[area.first - 1]
                }
                area.first to nextIndex
            } else {
                nextIndex + 1 to area.second
            }
        }
        return input[area.first - 1]
    }

    // Or read a large test input from the `src/Day01_test.txt` file:
    val testInput = readInput("Day18_test")
    val part1 = part1(testInput, 12)
    part1.println()
    check(part1 == 22)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day18")
    part1(input, 1024).println()

    val part2 = part2(testInput, 12)
    part2.println()
    check(part2 == "6,1")
    part2(input, 1024).println()
}
