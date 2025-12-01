package studying.cats.lab3_semak7.detail

import CustomTypes.CustomDate
import CustomTypes.Point2D
import HeadersList.HeadersList
import HeadersList.HeadersListFactory
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import studying.cats.lab3_semak7.HeadersListREPO
import java.lang.reflect.Type

class DetailViewModel : ViewModel() {
    private val repository = HeadersListREPO.getInstance()
    private val _currentList = MutableLiveData<HeadersList<*>>()
    val currentList: LiveData<HeadersList<*>> = _currentList

    var listName: String? = null
        private set

    @Suppress("UNCHECKED_CAST")
    fun loadList(context: Context, name: String) {
        this.listName = name
        val type: Type = when {
            name.startsWith("Point2D_") -> Point2D::class.java
            name.startsWith("CustomDate_") -> CustomDate::class.java
            else -> String::class.java
        }
        _currentList.value = repository.getListByName(context, name, type)
    }

    fun createNewList(baseName: String, type: String) {
        val newList: HeadersList<*>
        when (type) {
            "Point2D" -> {
                this.listName = "Point2D_$baseName"
                newList = HeadersList<Point2D>()
            }
            "CustomDate" -> {
                this.listName = "CustomDate_$baseName"
                newList = HeadersList<CustomDate>()
            }
            else -> {
                this.listName = baseName
                newList = HeadersList<String>()
            }
        }
        this.listName?.let { repository.createList(it, newList) }
        _currentList.value = newList
    }

    private fun updateStateWith(newList: HeadersList<*>) {
        _currentList.value = newList
        listName?.let { name ->
            repository.createList(name, newList)
        }
    }

    private fun performOperation(operation: (HeadersList<*>) -> HeadersList<*>) {
        _currentList.value?.let { oldList ->
            val newList = operation(oldList)
            updateStateWith(newList)
        }
    }

    fun addElement(header: Comparable<*>, associatedList: List<Comparable<*>>) {
        performOperation { oldList ->
            when (header) {
                is String -> {
                    @Suppress("UNCHECKED_CAST")
                    HeadersListFactory.add(oldList as HeadersList<String>, header, associatedList as MutableList<String>)
                }
                is Point2D -> {
                    @Suppress("UNCHECKED_CAST")
                    HeadersListFactory.add(oldList as HeadersList<Point2D>, header, associatedList as MutableList<Point2D>)
                }
                is CustomDate -> {
                    @Suppress("UNCHECKED_CAST")
                    HeadersListFactory.add(oldList as HeadersList<CustomDate>, header, associatedList as MutableList<CustomDate>)
                }
                else -> oldList
            }
        }
    }

    fun addElementAt(index: Int, header: Comparable<*>, associatedList: List<Comparable<*>>) {
        performOperation { oldList ->
            if (index < 0 || index > oldList.size()) {
                return@performOperation oldList
            }
            when (header) {
                is String -> {
                    @Suppress("UNCHECKED_CAST")
                    HeadersListFactory.add(oldList as HeadersList<String>, index, header, associatedList as MutableList<String>)
                }
                is Point2D -> {
                    @Suppress("UNCHECKED_CAST")
                    HeadersListFactory.add(oldList as HeadersList<Point2D>, index, header, associatedList as MutableList<Point2D>)
                }
                is CustomDate -> {
                    @Suppress("UNCHECKED_CAST")
                    HeadersListFactory.add(oldList as HeadersList<CustomDate>, index, header, associatedList as MutableList<CustomDate>)
                }
                else -> oldList
            }
        }
    }

    fun addSortedElement(header: Comparable<*>, associatedList: List<Comparable<*>>) {
        performOperation { oldList ->
            when (header) {
                is String -> {
                    @Suppress("UNCHECKED_CAST")
                    HeadersListFactory.addSorted(oldList as HeadersList<String>, header, associatedList as MutableList<String>)
                }
                is Point2D -> {
                    @Suppress("UNCHECKED_CAST")
                    HeadersListFactory.addSorted(oldList as HeadersList<Point2D>, header, associatedList as MutableList<Point2D>)
                }
                is CustomDate -> {
                    @Suppress("UNCHECKED_CAST")
                    HeadersListFactory.addSorted(oldList as HeadersList<CustomDate>, header, associatedList as MutableList<CustomDate>)
                }
                else -> oldList
            }
        }
    }

    fun removeElementAt(index: Int) {
        performOperation { oldList ->
            if (index < 0 || index >= oldList.size()) {
                oldList
            } else {
                HeadersListFactory.remove(oldList, index)
            }
        }
    }

    fun findListByHeader(header: Comparable<*>): List<*>? {
        val list = _currentList.value ?: return null
        return when (header) {
            is String -> @Suppress("UNCHECKED_CAST") (list as HeadersList<String>).getListByHeader(header)
            is Point2D -> @Suppress("UNCHECKED_CAST") (list as HeadersList<Point2D>).getListByHeader(header)
            is CustomDate -> @Suppress("UNCHECKED_CAST") (list as HeadersList<CustomDate>).getListByHeader(header)
            else -> null
        }
    }

    fun logAllHeaders() {
        _currentList.value?.forEachHeader { header ->
            Log.d("HeadersListLog", "Header: $header")
        }
    }

    fun sortList() {
        val oldList = _currentList.value ?: return

        viewModelScope.launch {
            val newList = withContext(Dispatchers.Default) {
                HeadersListFactory.sort(oldList)
            }
            updateStateWith(newList)
        }
    }

    fun balanceList() {
        val oldList = _currentList.value ?: return
        viewModelScope.launch {
            val newList = withContext(Dispatchers.Default) {
                HeadersListFactory.balance(oldList)
            }
            updateStateWith(newList)
        }
    }

    fun saveToJson(context: Context) {
        listName?.let { name ->
            viewModelScope.launch {
                repository.saveListToJson(context, name)
            }
        }
    }

    fun deleteCurrentList(context: Context) {
        listName?.let { name ->
            viewModelScope.launch {
                repository.deleteList(context, name)
            }
        }
    }
}