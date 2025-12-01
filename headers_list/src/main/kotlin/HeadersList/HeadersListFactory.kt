package HeadersList

object HeadersListFactory {

    private fun <T : Comparable<T>> copy(original: HeadersList<T>): HeadersList<T> {
        val newInstance = HeadersList<T>()

        for (nodeData in original) {
            val associatedListCopy = ArrayList(nodeData.associatedList)
            newInstance.add(nodeData.header, associatedListCopy)
        }

        return newInstance
    }

    fun <T : Comparable<T>> add(source: HeadersList<T>, header: T, list: MutableList<T>): HeadersList<T> {
        val newInstance = copy(source)
        newInstance.add(header, list)
        return newInstance
    }

    fun <T : Comparable<T>> add(source: HeadersList<T>, index: Int, header: T, list: MutableList<T>): HeadersList<T> {
        val newInstance = copy(source)
        newInstance.add(index, header, list)
        return newInstance
    }

    fun <T : Comparable<T>> addSorted(source: HeadersList<T>, header: T, list: MutableList<T>): HeadersList<T> {
        val newInstance = copy(source)
        newInstance.addSorted(header, list)
        return newInstance
    }

    fun <T : Comparable<T>> remove(source: HeadersList<T>, index: Int): HeadersList<T> {
        val newInstance = copy(source)
        newInstance.remove(index)
        return newInstance
    }

    fun <T : Comparable<T>> sort(source: HeadersList<T>): HeadersList<T> {
        val newInstance = copy(source)
        newInstance.sort()
        return newInstance
    }

    fun <T : Comparable<T>> balance(source: HeadersList<T>): HeadersList<T> {
        val newInstance = copy(source)
        newInstance.balance()
        return newInstance
    }
}
