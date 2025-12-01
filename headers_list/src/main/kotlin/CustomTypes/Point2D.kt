package CustomTypes

import java.util.Objects
import kotlin.math.sqrt

class Point2D private constructor(private val x: Double, private val y: Double) : Comparable<Point2D> {

    companion object {
        @JvmStatic
        fun of(x: Double, y: Double): Point2D {
            return Point2D(x, y)
        }

        @JvmStatic
        fun parse(point: String): Point2D {
            require(!point.trim().isEmpty()) { "Входная строка не может быть пустой." }
            
            val parts = point.split(",")
            require(parts.size == 2) { "Строка должна быть в формате 'x,y'. Получено: $point" }
            
            try {
                val x = parts[0].trim().toDouble()
                val y = parts[1].trim().toDouble()
                return of(x, y)
            } catch (e: NumberFormatException) {
                throw IllegalArgumentException("Координаты должны быть числами. Получено: $point", e)
            }
        }
    }

    fun getX(): Double {
        return x
    }

    fun getY(): Double {
        return y
    }

    fun getCoords(): DoubleArray {
        return doubleArrayOf(x, y)
    }

    fun getVector(): Double {
        return sqrt(x * x + y * y)
    }

    override fun compareTo(other: Point2D): Int {
        return this.getVector().compareTo(other.getVector())
    }

    override fun toString(): String {
        return "Point2D($x, $y)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val point2D = other as Point2D
        return point2D.x.compareTo(x) == 0 && point2D.y.compareTo(y) == 0
    }

    override fun hashCode(): Int {
        return Objects.hash(x, y)
    }
}
