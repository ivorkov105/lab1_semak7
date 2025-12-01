package HeadersList

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.lang.reflect.Type
import java.util.*

//ъуъ
class HeadersList<T : Comparable<T>> : Iterable<HeadersList.SerializableNode<T>> {

    //башку в узел завязал(пара заголовок + список, надо для линковки)
    private class HeaderNode<T>(
        var header: T,
        var associatedList: MutableList<T>,
        var next: HeaderNode<T>? = null
    )

    data class SerializableNode<T>(val header: T, val associatedList: List<T>)

    private var root: HeaderNode<T>? = null
    private var size: Int = 0

    override fun iterator(): Iterator<SerializableNode<T>> {
        return HeadersListIterator()
    }

    private inner class HeadersListIterator : Iterator<SerializableNode<T>> {
        private var current: HeaderNode<T>? = root

        override fun hasNext(): Boolean {
            return current != null
        }

        override fun next(): SerializableNode<T> {
            if (!hasNext()) {
                throw NoSuchElementException()
            }
            val data = SerializableNode(current!!.header, current!!.associatedList)
            current = current!!.next
            return data
        }
    }

    //в конец добавляем списачек и загаловочек
    fun add(header: T, list: MutableList<T>) {
        if (root == null) {
            root = HeaderNode(header, list)
        } else {
            var currNode = root
            while (currNode!!.next != null) {
                currNode = currNode.next
            }
            currNode.next = HeaderNode(header, list)
        }
        size++
    }

    //тотальнейшая вставка списка, его башки ослиной по индексу
    fun add(index: Int, header: T, list: MutableList<T>) {
        if (index < 0 || index > size) throw IndexOutOfBoundsException("ты чё твориш...")
        val newNode = HeaderNode(header, list)
        if (index == 0) {
            newNode.next = root
            root = newNode
        } else {
            var currNode = root
            for (i in 0 until index - 1) {
                currNode = currNode!!.next
            }
            newNode.next = currNode!!.next
            currNode.next = newNode
        }
        size++
    }

    //сортиров очка + балансиров очка
    fun sort() {
        if (size >= 2) {
            var last = root
            while (last!!.next != null) {
                last = last.next
            }
            quickSort(root, last)
        }
    }

    private fun quickSort(start: HeaderNode<T>?, end: HeaderNode<T>?) {
        if (start == null || end == null || start == end || start == end.next) {
            return
        }

        val pivot = partition(start, end)

        if (pivot != start) {
            var prevPivot = start
            while (prevPivot!!.next != pivot) {
                prevPivot = prevPivot.next
            }
            quickSort(start, prevPivot)
        }

        if (pivot != end && pivot.next != null) {
            quickSort(pivot.next, end)
        }
    }

    private fun partition(start: HeaderNode<T>, end: HeaderNode<T>): HeaderNode<T> {
        val pivotValue = end.header
        var i: HeaderNode<T>? = start

        var j: HeaderNode<T>? = start
        while (j != end) {
            if (j!!.header < pivotValue) {
                swapNodes(i!!, j)
                i = i.next
            }
            j = j.next
        }

        swapNodes(i!!, end)
        return i
    }

    private fun swapNodes(node1: HeaderNode<T>, node2: HeaderNode<T>) {
        if (node1 != node2) {
            val tempHeader = node1.header
            val tempList = node1.associatedList

            node1.header = node2.header
            node1.associatedList = node2.associatedList

            node2.header = tempHeader
            node2.associatedList = tempList
        }
    }

    fun balance() {
        if (root == null || root!!.next == null) {
            return
        }

        val allItems = ArrayList<T>()
        var current = root
        while (current != null) {
            allItems.addAll(current.associatedList)
            current = current.next
        }

        current = root
        while (current != null) {
            current.associatedList.clear()
            current = current.next
        }

        val totalItems = allItems.size
        val numLists = this.size
        val baseSize = totalItems / numLists
        val remainder = totalItems % numLists

        var currentItemIndex = 0
        var nodeIndex = 0
        current = root

        while (current != null) {
            val sublistSize = baseSize + if (nodeIndex < remainder) 1 else 0

            for (j in 0 until sublistSize) {
                if (currentItemIndex < totalItems) {
                    current.associatedList.add(allItems[currentItemIndex++])
                }
            }

            nodeIndex++
            current = current.next
        }
    }

    //сортир... Нажмите для продолжения...
    fun addSorted(header: T, list: MutableList<T>) {
        val newNode = HeaderNode(header, list)

        if (root == null) {
            root = newNode
            size++
            return
        }

        var comparison = header.compareTo(root!!.header)
        if (comparison < 0) {
            newNode.next = root
            root = newNode
            size++
            return
        }

        if (comparison == 0) {
            root!!.associatedList.addAll(list)
            return
        }

        var current = root

        while (current!!.next != null) {
            comparison = header.compareTo(current.next!!.header)

            if (comparison == 0) {
                current.next!!.associatedList.addAll(list)
                return
            }

            if (comparison < 0) {
                newNode.next = current.next
                current.next = newNode
                size++
                return
            }
            current = current.next
        }
        current.next = newNode
        size++
    }

    //бошку открути
    fun getHeader(index: Int): T {
        if (index !in 0..<size) {
            throw IndexOutOfBoundsException("ты чё делаешь? мужик, успокойся")
        }
        var curr = root
        for (i in 0 until index) {
            curr = curr!!.next
        }
        return curr!!.header
    }

    //вы списков продаете?
    //нет тока показываю
    //красивое...
    fun getList(index: Int): MutableList<T> {
        if (index !in 0..<size) {
            throw IndexOutOfBoundsException("ты чё делаешь? мужик, успокойся")
        }
        var curr = root
        for (i in 0 until index) {
            curr = curr!!.next
        }
        return curr!!.associatedList
    }

    //списачик палучаим дада
    fun getListByHeader(header: T): MutableList<T>? {
        var curr = root
        while (curr != null) {
            if (curr.header == header) {
                return curr.associatedList
            }
            curr = curr.next
        }
        return null
    }

    //минус элемент
    fun remove(index: Int) {
        if (index !in 0..<size) {
            throw IndexOutOfBoundsException("да харош уже нет такого индекса")
        }

        if (index == 0) {
            root = root!!.next
            size--
            return
        }
        var prev = root
        for (i in 0 until index - 1) {
            prev = prev!!.next
        }
        val removingNode = prev!!.next
        prev.next = removingNode!!.next
        size--
    }

    //да это на морозе уменьшился чесно
    fun size(): Int {
        return size
    }

    //вешаем итератор
    fun forEachHeader(action: (T) -> Unit) {
        var curr = root
        while (curr != null) {
            action(curr.header)
            curr = curr.next
        }
    }

    //парсуем, я сказала парсуем!
    @Throws(IOException::class)
    fun saveToFile(file: File) {
        val dataToSave = ArrayList<SerializableNode<T>>()

        var current = root
        while (current != null) {
            dataToSave.add(SerializableNode(current.header, current.associatedList))
            current = current.next
        }

        val gson = GsonBuilder().setPrettyPrinting().create()
        FileWriter(file).use { writer ->
            gson.toJson(dataToSave, writer)
        }
    }

    companion object {
        @JvmStatic
        @Throws(IOException::class)
        fun <T : Comparable<T>> loadFromFile(file: File, type: Type): HeadersList<T> {
            val gson = Gson()
            val resultList = HeadersList<T>()
            FileReader(file).use { reader ->
                val listType = TypeToken.getParameterized(List::class.java, type).type
                val loadedData: List<SerializableNode<T>>? = gson.fromJson(reader, listType)

                if (loadedData != null) {
                    for (nodeData in loadedData) {
                        resultList.add(nodeData.header, nodeData.associatedList.toMutableList())
                    }
                }
            }
            return resultList
        }
    }

    override fun toString(): String {
        if (root == null) {
            return "HeadersList{[]}"
        }

        val sb = StringBuilder()
        sb.append("HeadersList{[")
        var current = root
        while (current != null) {
            sb.append(current.header.toString())
                .append(": ")
                .append(current.associatedList.toString())
            if (current.next != null) {
                sb.append("], [")
            }
            current = current.next
        }
        sb.append("]}")
        return sb.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }

        val otherList = other as HeadersList<*>

        if (this.size != otherList.size) {
            return false
        }

        var currentThis = this.root
        var currentOther = otherList.root

        while (currentThis != null) {
            if (currentThis.header != currentOther!!.header ||
                currentThis.associatedList != currentOther.associatedList
            ) {
                return false
            }
            currentThis = currentThis.next
            currentOther = currentOther.next
        }
        return true
    }

    override fun hashCode(): Int {
        var result = 1
        var current = root

        while (current != null) {
            val headerHash = Objects.hashCode(current.header)
            val listHash = Objects.hashCode(current.associatedList)

            result = 31 * result + headerHash
            result = 31 * result + listHash

            current = current.next
        }
        return result
    }
}
