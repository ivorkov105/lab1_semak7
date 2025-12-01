package test.tests

import HeadersList.HeadersList
import org.junit.Test
import test.tests.utils.InstrumentedString
import test.tests.utils.OpCounter
import java.util.ArrayList

class HeadersListPerformanceTest {

    private val counter = OpCounter()

    private val sizes = listOf(100, 500, 1000, 2000, 3000, 4000, 5000)

    private fun generateRandomList(size: Int): HeadersList<InstrumentedString> {
        val list = HeadersList<InstrumentedString>()
        for (i in 0 until size) {
            val str = java.util.UUID.randomUUID().toString()
            val item = InstrumentedString(str, counter)
            list.add(item, ArrayList())
        }
        return list
    }

    private fun generateSortedList(size: Int): HeadersList<InstrumentedString> {
        val list = HeadersList<InstrumentedString>()
        for (i in 0 until size) {
            val str = String.format("%05d", i)
            val item = InstrumentedString(str, counter)
            list.add(item, ArrayList())
        }
        return list
    }

    @Test
    fun testTimeAndComplexity_RandomData() {
        println("=== TEST 1: Random Data (Average Case) ===")
        println("N; Time(ns); Comparisons")

        for (n in sizes) {
            val list = generateRandomList(n)

            counter.reset()
            System.gc()

            val start = System.nanoTime()
            list.sort()
            val end = System.nanoTime()

            val time = end - start
            val ops = counter.comparisons

            println("$n; $time; $ops")
        }
        println()
    }

    @Test
    fun testTimeAndComplexity_WorstCase() {
        println("=== TEST 1: Sorted Data (Worst Case for QuickSort with End Pivot) ===")
        println("N; Time(ns); Comparisons")

        val smallSizes = listOf(100, 500, 1000, 1500, 2000)

        for (n in smallSizes) {
            val list = generateSortedList(n)

            counter.reset()
            System.gc()

            val start = System.nanoTime()
            list.sort()
            val end = System.nanoTime()

            val time = end - start
            val ops = counter.comparisons

            println("$n; $time; $ops")
        }
        println()
    }

    @Test
    fun testMemoryUsage() {
        println("=== TEST 2: Memory Usage ===")
        println("N; UsedMemory(Bytes); HeaderNodesCount")

        val runtime = Runtime.getRuntime()

        for (n in sizes) {
            var list: HeadersList<InstrumentedString>? = null
            System.gc()
            Thread.sleep(100)
            val memoryBefore = runtime.totalMemory() - runtime.freeMemory()

            list = generateRandomList(n)

            val memoryAfter = runtime.totalMemory() - runtime.freeMemory()

            val usedMemory = memoryAfter - memoryBefore
            val nodesCount = list.size()

            println("$n; $usedMemory; $nodesCount")
        }
    }
}