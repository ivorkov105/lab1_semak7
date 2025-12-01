package test.tests

import CustomTypes.Point2D
import HeadersList.HeadersList
import org.junit.Assert.*
import org.junit.Test
import java.util.ArrayList

class HeadersListGrayBoxTest {

    private fun <T : Comparable<T>> createMutableList(vararg items: T): MutableList<T> {
        val list = ArrayList<T>()
        for (item in items) list.add(item)
        return list
    }

    @Test
    fun testAddSorted_MergeDuplicates_Root() {
        val list = HeadersList<String>()
        list.addSorted("A", createMutableList("Item1"))

        list.addSorted("A", createMutableList("Item2"))

        assertEquals(1, list.size())
        assertEquals("A", list.getHeader(0))

        val content = list.getList(0)
        assertEquals(2, content.size)
        assertTrue(content.contains("Item1"))
        assertTrue(content.contains("Item2"))
    }

    @Test
    fun testAddSorted_MergeDuplicates_Middle() {
        val list = HeadersList<String>()
        list.addSorted("A", createMutableList("A1"))
        list.addSorted("C", createMutableList("C1"))

        list.addSorted("B", createMutableList("B1"))
        assertEquals(3, list.size())

        list.addSorted("B", createMutableList("B2"))

        assertEquals(3, list.size())
        assertEquals("B", list.getHeader(1))
        assertEquals(2, list.getList(1).size)
    }

    @Test
    fun testAddSorted_NewMin_UpdatesRoot() {
        val list = HeadersList<Int>()
        list.addSorted(10, createMutableList(10))
        list.addSorted(5, createMutableList(5))

        assertEquals(5, list.getHeader(0))
        assertEquals(10, list.getHeader(1))
    }

    @Test
    fun testRemove_Root_Logic() {
        val list = HeadersList<String>()
        list.add("A", createMutableList("A"))
        list.add("B", createMutableList("B"))

        list.remove(0)

        assertEquals(1, list.size())
        assertEquals("B", list.getHeader(0))
    }

    @Test
    fun testRemove_Tail_Logic() {
        val list = HeadersList<String>()
        list.add("A", createMutableList("A"))
        list.add("B", createMutableList("B"))
        list.add("C", createMutableList("C"))

        list.remove(2)

        assertEquals(2, list.size())
        assertEquals("B", list.getHeader(1))
    }

    @Test
    fun testSort_PivotIsMin_ReverseSorted() {
        val p1 = Point2D.of(10.0, 10.0)
        val p2 = Point2D.of(5.0, 5.0)
        val p3 = Point2D.of(1.0, 1.0)

        val list = HeadersList<Point2D>()
        list.add(p1, createMutableList(p1))
        list.add(p2, createMutableList(p2))
        list.add(p3, createMutableList(p3))

        list.sort()

        assertEquals(p3, list.getHeader(0))
        assertEquals(p2, list.getHeader(1))
        assertEquals(p1, list.getHeader(2))
    }
}