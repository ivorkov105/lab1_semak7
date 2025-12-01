package test.tests

import CustomTypes.CustomDate
import CustomTypes.Point2D
import HeadersList.HeadersList
import org.junit.Assert.*
import org.junit.Test
import java.util.ArrayList

class HeadersListFuncTest {

    private fun <T : Comparable<T>> createListWithMarkers(headers: List<T>): HeadersList<T> {
        val list = HeadersList<T>()
        for (h in headers) {
            val associated = ArrayList<T>()
            associated.add(h)
            list.add(h, associated)
        }
        return list
    }

    private fun <T : Comparable<T>> assertSorted(list: HeadersList<T>) {
        if (list.size() < 2) return
        val iterator = list.iterator()
        var prev = iterator.next().header
        while (iterator.hasNext()) {
            val curr = iterator.next().header
            assertTrue("Нарушен порядок сортировки: $prev > $curr", prev <= curr)
            prev = curr
        }
    }

    private fun <T : Comparable<T>> assertIntegrity(list: HeadersList<T>) {
        val iterator = list.iterator()
        while (iterator.hasNext()) {
            val node = iterator.next()
            val header = node.header
            val associatedValue = node.associatedList[0]
            assertEquals(
                "Нарушена целостность! Заголовок $header, а в списке лежит $associatedValue",
                header,
                associatedValue
            )
        }
    }


    @Test
    fun testBoundary_EmptyList() {
        val list = HeadersList<String>()
        try {
            list.sort()
        } catch (e: Exception) {
            fail("Сортировка пустого списка вызвала ошибку: ${e.message}")
        }
        assertEquals(0, list.size())
    }

    @Test
    fun testBoundary_SingleElement() {
        val list = createListWithMarkers(listOf("Solo"))
        list.sort()
        assertEquals("Solo", list.getHeader(0))
        assertIntegrity(list)
    }

    @Test
    fun testBoundary_TwoElementsReverse() {
        val list = createListWithMarkers(listOf("B", "A"))
        list.sort()
        assertEquals("A", list.getHeader(0))
        assertEquals("B", list.getHeader(1))
        assertIntegrity(list)
    }

    @Test
    fun testBoundary_AllDuplicates() {
        val list = createListWithMarkers(listOf("A", "A", "A", "A"))
        list.sort()
        assertSorted(list)
        assertEquals(4, list.size())
        assertIntegrity(list)
    }

    @Test
    fun testFunctional_AlreadySorted() {
        val dates = listOf(
            CustomDate.of(1, 1, 2020),
            CustomDate.of(2, 2, 2021),
            CustomDate.of(3, 3, 2022)
        )
        val list = createListWithMarkers(dates)
        list.sort()
        assertSorted(list)
        assertIntegrity(list)
    }

    @Test
    fun testFunctional_ReverseSorted_Large() {
        val input = listOf("Z", "Y", "X", "W", "V", "U", "T")
        val list = createListWithMarkers(input)
        list.sort()
        assertSorted(list) // Должно стать T, U, V...
        assertEquals("T", list.getHeader(0))
        assertIntegrity(list)
    }

    @Test
    fun testDataProp_Point2D_EqualMagnitude() {
        val p1 = Point2D.of(3.0, 4.0) // len 5
        val p2 = Point2D.of(4.0, 3.0) // len 5
        val p3 = Point2D.of(0.0, 1.0) // len 1

        val list = createListWithMarkers(listOf(p1, p2, p3))
        list.sort()

        assertSorted(list)
        assertEquals(p3, list.getHeader(0))
        assertIntegrity(list)
    }

    @Test
    fun testDataProp_String_CaseAndSpecial() {
        val input = listOf("banana", "Apple", "", "123", " ")
        val list = createListWithMarkers(input)
        list.sort()

        assertSorted(list)
        assertEquals("", list.getHeader(0))   // Пустая строка первая
        assertEquals(" ", list.getHeader(1))  // Пробел второй
        assertIntegrity(list)
    }
}