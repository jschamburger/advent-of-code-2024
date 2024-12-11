fun main() {

    fun createMatrixAndFindAntennas(input: List<String>): Pair<List<MutableList<Char>>, Map<Char, List<Coordinate>>> {
        val (charMatrix, antennas) = input.map {
            it.toMutableList()
        }.foldIndexed(Pair(mutableListOf<MutableList<Char>>(), mutableMapOf<Char, MutableList<Coordinate>>())) { index, acc, chars ->
            acc.first.add(chars)
            chars.onEachIndexed { innerIndex, c ->
                if (c != '.') {
                    val list = acc.second.getOrDefault(c, mutableListOf())
                    list.add(Coordinate(index, innerIndex))
                    acc.second[c] = list
                }
            }
            acc
        }
        return Pair(charMatrix, antennas)
    }

    fun pairs(list: List<Coordinate>): List<Pair<Coordinate, Coordinate>> {
        return list.indices.map { i ->
            val result = mutableListOf<Pair<Coordinate, Coordinate>>()
            for (j in i + 1 until list.size) {
                result.add(Pair(list[i], list[j]))
            }
            return@map result
        }.flatten()
    }

    fun calculateAntinodeLocations(first: Coordinate, second: Coordinate): List<Coordinate> {
        return listOf(first + (second - first) + (second - first), second + (first - second) + (first - second))
    }

    fun isInGrid(coordinate: Coordinate, charMatrix: List<List<Char>>) =
        coordinate.run { y >= 0 && x >= 0 && y < charMatrix.size && x < charMatrix[0].size }

    fun calculateAntinodeLocations(charMatrix: List<List<Char>>, antennas: Map<Char, List<Coordinate>>): List<Coordinate> {
        return antennas.values.asSequence().map { coordinates ->
            return@map pairs(coordinates).map { (first, second) ->
                calculateAntinodeLocations(first, second)
            }.flatten()
        }.flatten().toSet()
            .filter { coordinate ->
                isInGrid(coordinate, charMatrix)
            }.sortedBy { it.x }.sortedBy { it.y }.toList()
    }

    fun calculateAntinodeLocations2(charMatrix: List<List<Char>>, first: Coordinate, second: Coordinate): Set<Coordinate> {
        val locations = mutableSetOf<Coordinate>()
        var nextCandidate = first
        while (isInGrid(nextCandidate, charMatrix)) {
            locations.add(nextCandidate)
            nextCandidate += second - first
        }
        nextCandidate = first
        while (isInGrid(nextCandidate, charMatrix)) {
            locations.add(nextCandidate)
            nextCandidate += first - second
        }
        return locations
    }

    fun calculateAntinodeLocations2(charMatrix: List<List<Char>>, antennas: Map<Char, List<Coordinate>>): List<Coordinate> {
        return antennas.values.asSequence().map { coordinates ->
            return@map pairs(coordinates).map { (first, second) ->
                calculateAntinodeLocations2(charMatrix, first, second)
            }.flatten()
        }.flatten().toSet()
            .filter { coordinate ->
                isInGrid(coordinate, charMatrix)
            }.sortedBy { it.x }.sortedBy { it.y }.toList()
    }

    fun part1(input: List<String>): Int {
        val (charMatrix, antennas) = createMatrixAndFindAntennas(input)
        return calculateAntinodeLocations(charMatrix, antennas).size
    }

    fun part2(input: List<String>): Int {
        val (charMatrix, antennas) = createMatrixAndFindAntennas(input)
        return calculateAntinodeLocations2(charMatrix, antennas).size
    }

    // Or read a large test input from the `src/Day01_test.txt` file:
    val testInput = readInput("Day08_test")
    check(part1(testInput) == 14)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day08")
    part1(input).println()

    check(part2(testInput) == 34)
    part2(input).println()
}
