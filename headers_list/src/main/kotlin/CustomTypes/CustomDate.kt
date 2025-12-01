package CustomTypes

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Objects

class CustomDate private constructor(private val date: LocalDate) : Comparable<CustomDate> {

    companion object {
        private val FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        @JvmStatic
        fun of(day: Int, month: Int, year: Int): CustomDate {
            return CustomDate(LocalDate.of(year, month, day))
        }

        @JvmStatic
        fun parse(dateString: String): CustomDate {
            return CustomDate(LocalDate.parse(dateString, FORMATTER))
        }
    }

    fun getDay(): Int {
        return date.dayOfMonth
    }

    fun getMonth(): Int {
        return date.monthValue
    }

    fun getYear(): Int {
        return date.year
    }

    fun addDays(days: Int): CustomDate {
        return CustomDate(this.date.plusDays(days.toLong()))
    }

    fun subtractDays(days: Int): CustomDate {
        return CustomDate(this.date.minusDays(days.toLong()))
    }

    override fun compareTo(other: CustomDate): Int {
        return this.date.compareTo(other.date)
    }

    override fun toString(): String {
        return date.format(FORMATTER)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as CustomDate
        return date == that.date
    }

    override fun hashCode(): Int {
        return Objects.hash(date)
    }
}
