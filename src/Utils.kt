import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readText

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/$name.txt").readText().trim().lines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

data class Coordinate(val y: Int, val x: Int) {
    operator fun plus(other: Coordinate): Coordinate = Coordinate(y + other.y, x + other.x)
    operator fun minus(other: Coordinate): Coordinate = Coordinate(y - other.y, x - other.x)
    fun top() = Coordinate(y - 1, x)
    fun right() = Coordinate(y, x + 1)
    fun bottom() = Coordinate(y + 1, x)
    fun left() = Coordinate(y, x - 1)
    fun next() = setOf(top(), right(), bottom(), left())
}

typealias Matrix<T> = List<List<T>>

fun <T> Matrix<T>.safeGet(coordinate: Coordinate): T? {
    return if (coordinate.x < 0 || coordinate.y < 0 || coordinate.x >= this[0].size || coordinate.y >= this.size) {
        null
    } else {
        this[coordinate.y][coordinate.x]
    }
}

fun <T> Matrix<T>.get(coordinate: Coordinate) = this[coordinate.y][coordinate.x]

fun <T> MutableCollection<T>.removeFirst() = first().also{ remove(it) }