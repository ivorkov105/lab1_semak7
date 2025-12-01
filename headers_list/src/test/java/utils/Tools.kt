package test.tests.utils

class OpCounter {
    var comparisons: Long = 0

    fun reset() {
        comparisons = 0
    }
}

class InstrumentedString(val value: String, private val counter: OpCounter) : Comparable<InstrumentedString> {
    override fun compareTo(other: InstrumentedString): Int {
        counter.comparisons++
        return this.value.compareTo(other.value)
    }

    override fun toString(): String = value
}