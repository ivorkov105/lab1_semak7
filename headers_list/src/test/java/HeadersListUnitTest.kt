package test.tests

import CustomTypes.CustomDate
import CustomTypes.Point2D
import HeadersList.HeadersList
import org.junit.Assert.*
import org.junit.Test
import java.util.ArrayList

class HeadersListUnitTest {

    // Вспомогательный метод для проверки, что список отсортирован
    private fun <T : Comparable<T>> assertListSorted(list: HeadersList<T>) {
        if (list.size() < 2) return

        val iterator = list.iterator()
        var previous = iterator.next()

        while (iterator.hasNext()) {
            val current = iterator.next()
            // Проверяем, что предыдущий элемент меньше или равен текущему
            assertTrue(
                "Список не отсортирован: ${previous.header} > ${current.header}",
                previous.header <= current.header
            )
            previous = current
        }
    }

    // Вспомогательный метод для наполнения списка
    private fun <T : Comparable<T>> createList(vararg items: T): HeadersList<T> {
        val headersList = HeadersList<T>()
        for (item in items) {
            // AssociatedList нам для сортировки не важен, но он должен перемещаться вместе с заголовком
            // Добавим туда сам элемент, чтобы проверить целостность данных
            val dummyList = ArrayList<T>()
            dummyList.add(item)
            headersList.add(item, dummyList)
        }
        return headersList
    }

    // --- Тесты для типа String ---

    @Test
    fun testString_Unordered() {
        // Исходный набор неупорядочен
        val list = createList("Banana", "Apple", "Cherry", "Date", "Elderberry")
        list.sort()
        assertListSorted(list)
        assertEquals("Apple", list.getHeader(0)) // Проверка первого элемента
    }

    @Test
    fun testString_OrderedAsc() {
        // Исходный набор упорядочен в прямом порядке
        val list = createList("A", "B", "C", "D", "E")
        list.sort()
        assertListSorted(list)
        assertEquals("A", list.getHeader(0))
    }

    @Test
    fun testString_OrderedDesc() {
        // Исходный набор упорядочен в обратном порядке
        val list = createList("E", "D", "C", "B", "A")
        list.sort()
        assertListSorted(list)
        assertEquals("A", list.getHeader(0))
    }

    @Test
    fun testString_Identical() {
        // Исходный набор содержит одинаковые значения
        val list = createList("A", "A", "A", "A")
        list.sort()
        assertListSorted(list)
        assertEquals("A", list.getHeader(3))
    }

    @Test
    fun testString_ExtremeAtMiddle() {
        // Экстремальное значение (Min/Max) находится в середине
        // "Z" - max, "A" - min
        val list = createList("M", "N", "Z", "A", "O")
        list.sort()
        assertListSorted(list)
        assertEquals("A", list.getHeader(0))
        assertEquals("Z", list.getHeader(4))
    }

    // --- Тесты для типа CustomDate ---

    @Test
    fun testDate_DuplicatesGroups() {
        // В наборе имеются несколько групп повторяющихся элементов
        val d1 = CustomDate.of(1, 1, 2020)
        val d2 = CustomDate.of(5, 5, 2025)

        val list = createList(d2, d1, d2, d1, d2)
        list.sort()
        assertListSorted(list)

        // Проверяем порядок: сначала все d1, потом все d2
        assertEquals(d1, list.getHeader(0))
        assertEquals(d1, list.getHeader(1))
        assertEquals(d2, list.getHeader(2))
    }

    @Test
    fun testDate_ExtremeAtStart() {
        // Экстремальное значение находится в начале набора (Max в начале)
        val maxDate = CustomDate.of(31, 12, 2999)
        val normalDate = CustomDate.of(1, 1, 2020)

        val list = createList(maxDate, normalDate, normalDate)
        list.sort()
        assertListSorted(list)
        assertEquals(maxDate, list.getHeader(2)) // Max должен уйти в конец
    }

    @Test
    fun testDate_ExtremeAtEnd() {
        // Экстремальное значение находится в конце (Min в конце)
        val minDate = CustomDate.of(1, 1, 1900)
        val normalDate = CustomDate.of(1, 1, 2020)

        val list = createList(normalDate, normalDate, minDate)
        list.sort()
        assertListSorted(list)
        assertEquals(minDate, list.getHeader(0)) // Min должен уйти в начало
    }

    // --- Тесты для типа Point2D ---
    // Point2D сравнивается по длине вектора (от начала координат)

    @Test
    fun testPoint2D_MultipleExtremes() {
        // В наборе несколько совпадающих экстремальных значений
        // (0,0) - минимально возможный вектор
        val pZero1 = Point2D.of(0.0, 0.0)
        val pZero2 = Point2D.of(0.0, 0.0)
        val pBig = Point2D.of(100.0, 100.0)

        val list = createList(pBig, pZero1, pBig, pZero2)
        list.sort()
        assertListSorted(list)

        assertEquals(pZero1, list.getHeader(0)) // Должен быть (0,0)
        assertEquals(pZero2, list.getHeader(1)) // Должен быть (0,0)
    }

    @Test
    fun testPoint2D_GeneralSort() {
        // Обычная проверка для Point2D (проверка логики compareTo)
        // 3,4 -> длина 5
        // 1,1 -> длина sqrt(2) ~= 1.41
        // 0,10 -> длина 10
        val p1 = Point2D.of(3.0, 4.0)
        val p2 = Point2D.of(1.0, 1.0)
        val p3 = Point2D.of(0.0, 10.0)

        val list = createList(p1, p3, p2) // [5, 10, 1.41]
        list.sort() // Должно стать: [1.41, 5, 10] -> p2, p1, p3

        assertEquals(p2, list.getHeader(0))
        assertEquals(p1, list.getHeader(1))
        assertEquals(p3, list.getHeader(2))
    }

    // --- Проверка целостности данных после сортировки ---

    @Test
    fun testDataIntegrity() {
        // Проверяем, что при сортировке заголовков, associatedList перемещается вместе с ними
        val list = HeadersList<String>()
        val listA = mutableListOf("ListA")
        val listB = mutableListOf("ListB")

        // Добавляем в обратном порядке: B, A
        list.add("B", listB)
        list.add("A", listA)

        list.sort()

        // Ожидаем порядок: A, B
        assertEquals("A", list.getHeader(0))
        assertEquals(listA, list.getList(0)) // Список должен соответствовать заголовку A

        assertEquals("B", list.getHeader(1))
        assertEquals(listB, list.getList(1)) // Список должен соответствовать заголовку B
    }

    // --- Граничные случаи (пустой список и 1 элемент) ---
    // Это часть структурного тестирования (покрытие путей if size < 2)

    @Test
    fun testEmptyList() {
        val list = HeadersList<String>()
        list.sort()
        assertEquals(0, list.size())
    }

    @Test
    fun testSingleElement() {
        val list = createList("Solo")
        list.sort()
        assertEquals(1, list.size())
        assertEquals("Solo", list.getHeader(0))
    }
}