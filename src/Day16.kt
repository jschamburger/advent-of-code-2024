fun main() {
    data class Maze(val matrix: Matrix<Char>, val start: Coordinate, val end: Coordinate)

    fun createMaze(input: List<String>): Maze {
        val maze = input.map {
            it.toList()
        }.foldIndexed(Maze(listOf(), Coordinate(-1, -1), Coordinate(-1, -1))) { outerIndex, acc, chars ->
            val accNew = chars.foldIndexed(acc) inner@{ innerIndex, accInner, c ->
                return@inner when (c) {
                    'S' -> accInner.copy(start = Coordinate(outerIndex, innerIndex))
                    'E' -> accInner.copy(end = Coordinate(outerIndex, innerIndex))
                    else -> accInner
                }
            }
            accNew.copy(matrix = accNew.matrix + listOf(chars))
        }
        return maze
    }

    data class Node(
        val coordinate: Coordinate,
        val score: Long,
        val direction: Direction,
        val cheapestPath: List<Coordinate>
    ) {
        fun findNeighbours(): List<Node> {
            return listOf(
                Node(
                    coordinate.neighbour(direction),
                    score + 1,
                    direction,
                    this.cheapestPath + coordinate.neighbour(direction)
                ),
                Node(
                    coordinate.neighbour(direction.rotateClockwise()),
                    score + 1001,
                    direction.rotateClockwise(),
                    this.cheapestPath + coordinate.neighbour(direction.rotateClockwise())
                ),
                Node(
                    coordinate.neighbour(direction.rotateCounterclockwise()),
                    score + 1001,
                    direction.rotateCounterclockwise(),
                    this.cheapestPath + coordinate.neighbour(direction.rotateCounterclockwise())
                ),
            )
        }
    }

    // Dijkstra https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm
    fun findCheapestPathToEnd(maze: Maze, blockedCoordinates: Set<Coordinate> = emptySet()): Node? {
        val unvisitedNodes = mutableSetOf<Node>()
        val nodes =
            maze.matrix.coordinates().filter { maze.matrix[it] != '#' && it !in blockedCoordinates }.map { element ->
                Node(
                    element, Long.MAX_VALUE,
                    Direction.RIGHT,
                    emptyList()
                )
            }
        unvisitedNodes.addAll(nodes)
        val visitedNodes = mutableSetOf<Node>()
        unvisitedNodes.removeIf { it.coordinate == maze.start }
        unvisitedNodes.add(Node(maze.start, 0, Direction.RIGHT, listOf(maze.start)))
        while (unvisitedNodes.isNotEmpty()) {
            val currentNode = unvisitedNodes.minBy { it.score }
            currentNode.findNeighbours().forEach { neighbour ->
                val previousNode = unvisitedNodes.find { it.coordinate == neighbour.coordinate }
                if (previousNode != null &&
                    maze.matrix[neighbour.coordinate] != '#' &&
                    neighbour.coordinate !in blockedCoordinates
                ) {
                    if (previousNode.score > neighbour.score) {
                        unvisitedNodes.remove(previousNode)
                        unvisitedNodes.add(neighbour)
                    }
                }
            }
            visitedNodes.add(currentNode)
            unvisitedNodes.remove(currentNode)
        }
        return visitedNodes.find { it.coordinate == maze.end }
    }

    fun part1(input: List<String>): Long {
        val maze = createMaze(input)
        return findCheapestPathToEnd(maze)?.score ?: 0
    }

    // k shortest paths routing https://en.wikipedia.org/wiki/K_shortest_path_routing
    // not very performant
    fun findKCheapestPathsToEnd(maze: Maze, k: Int): Set<Node> {
        val cheapestPathsToEnd = mutableSetOf<Node>()
        val unvisitedNodes = mutableSetOf<Node>()
        val nodes =
            maze.matrix.coordinates().filter { maze.matrix[it] != '#' }.map { element ->
                Node(
                    element, Long.MAX_VALUE,
                    Direction.RIGHT,
                    emptyList()
                )
            }
        unvisitedNodes.addAll(nodes)
        val pathsToCoordinates = maze.matrix.coordinates().associateWith { 0 }.toMutableMap()
        unvisitedNodes.removeIf { it.coordinate == maze.start }
        unvisitedNodes.add(Node(maze.start, 0, Direction.RIGHT, listOf(maze.start)))

        while (unvisitedNodes.isNotEmpty() && cheapestPathsToEnd.size < k) {
            val currentNode = unvisitedNodes.minBy { it.score }
            unvisitedNodes.remove(currentNode)
            pathsToCoordinates[currentNode.coordinate] = pathsToCoordinates.getOrDefault(currentNode.coordinate, 0) + 1
            if (currentNode.coordinate == maze.end) {
                if (cheapestPathsToEnd.isEmpty() || cheapestPathsToEnd.first().score == currentNode.score) {
                    cheapestPathsToEnd.add(currentNode)
                } else if (cheapestPathsToEnd.first().score > currentNode.score) {
                    cheapestPathsToEnd.clear()
                    cheapestPathsToEnd.add(currentNode)
                }
            }
            if (pathsToCoordinates.getOrDefault(currentNode.coordinate, 0) <= k) {
                currentNode.findNeighbours().forEach { neighbour ->
                    if (maze.matrix[neighbour.coordinate] != '#') {
                        unvisitedNodes.add(neighbour)
                    }
                }
            }
        }
        return cheapestPathsToEnd
    }

    fun part2(input: List<String>): Int {
        val maze = createMaze(input)
        val cheapestPathsToEnd = findKCheapestPathsToEnd(maze, 500)
        val coordinatesOnCheapestPaths = cheapestPathsToEnd.map { it.cheapestPath }.flatten().toSet()
        return coordinatesOnCheapestPaths.size
    }

    // Or read a large test input from the `src/Day01_test.txt` file:
    val testInput = readInput("Day16_test")
    check(part1(testInput) == 7036L)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day16")
    part1(input).println()

    check(part2(testInput) == 45)
    part2(input).println()
}

