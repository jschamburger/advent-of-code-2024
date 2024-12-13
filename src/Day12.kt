data class Region(val character: Char, val coordinates: Set<Coordinate>)
data class Fence(val direction: Direction, val start: Coordinate, val end: Coordinate)

fun main() {
    fun createMatrix(input: List<String>): Matrix<Char> {
        val charMatrix = input.map {
            it.toList()
        }.fold(listOf<List<Char>>()) { acc, chars ->
            acc + listOf(chars)
        }
        return charMatrix
    }

    fun createRegion(matrix: Matrix<Char>, start: Coordinate): Region {
        val queue = mutableSetOf(start)
        val region = mutableSetOf<Coordinate>()
        while (queue.isNotEmpty()) {
            val element = queue.removeFirst()
            region.add(element)
            for (next in element.neighbours()) {
                if (matrix.safeGet(next) == matrix.get(element) && !region.contains(next)) {
                    queue.add(next)
                }
            }
        }
        return Region(matrix.get(start), region)
    }

    fun calculateNumberOfFencesForRegion(matrix: Matrix<Char>, region: Region): Int {
        return region.coordinates.sumOf { coordinate ->
            coordinate.neighbours().count { neighbour ->
                matrix.safeGet(neighbour) != region.character
            }
        }
    }

    fun findAdjacentFence(fences: Set<Fence>, fence: Fence) = fences.find {
        it.direction == fence.direction &&
                (it.start in fence.end.neighbours() || it.end in fence.start.neighbours())
    }

    fun mergeFences(adjacent: Fence, fence: Fence): Fence {
        val minX = minOf(adjacent.start.x, fence.start.x)
        val minY = minOf(adjacent.start.y, fence.start.y)
        val maxX = maxOf(adjacent.end.x, fence.end.x)
        val maxY = maxOf(adjacent.end.y, fence.end.y)
        val merged = Fence(adjacent.direction, Coordinate(y = minY, x = minX), Coordinate(y = maxY, x = maxX))
        return merged
    }

    fun calculateNumberOfFencesForRegion2(matrix: Matrix<Char>, region: Region): Int {
        return region.coordinates.fold(mutableListOf<Fence>()) { acc, coordinate ->
            Direction.entries.forEach { direction ->
                if (matrix.safeGet(coordinate.neighbour(direction)) != region.character) {
                    acc.add(Fence(direction, coordinate, coordinate))
                }
            }
            acc
        }.sortedBy { it.start.x }
            .sortedBy { it.start.y }
            .fold(mutableSetOf<Fence>()) { acc, fence ->
                val adjacent = findAdjacentFence(acc, fence)
                if (adjacent != null) {
                    // replace adjacent fence with longer one
                    acc.remove(adjacent)
                    acc.add(mergeFences(adjacent, fence))
                } else {
                    acc.add(fence)
                }
                acc
            }.count()
    }

    fun part1(input: List<String>): Int {
        val matrix = createMatrix(input)
        val regions = mutableSetOf<Region>()
        matrix.coordinates().forEach { coordinate ->
            if (regions.none { it.coordinates.contains(coordinate) }) {
                val region = createRegion(matrix, coordinate)
                regions.add(region)
            }
        }
        return regions.sumOf { region ->
            val fences = calculateNumberOfFencesForRegion(matrix, region)
            region.coordinates.size * fences
        }
    }

    fun part2(input: List<String>): Int {
        val matrix = createMatrix(input)
        val regions = mutableSetOf<Region>()
        matrix.coordinates().forEach { coordinate ->
            if (regions.none { it.coordinates.contains(coordinate) }) {
                val region = createRegion(matrix, coordinate)
                regions.add(region)
            }
        }
        return regions.sumOf { region ->
            region.coordinates.size * calculateNumberOfFencesForRegion2(matrix, region)
        }
    }

    // Or read a large test input from the `src/Day01_test.txt` file:
    val testInput = readInput("Day12_test")
    check(part1(testInput) == 1930)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day12")
    part1(input).println()

    check(part2(testInput) == 1206)
    part2(input).println()
}
