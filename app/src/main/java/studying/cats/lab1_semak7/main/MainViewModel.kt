package studying.cats.lab1_semak7.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import studying.cats.lab1_semak7.HeadersListREPO

class MainViewModel : ViewModel() {
    private val repository = HeadersListREPO.Companion.getInstance()
    private val _listNames = MutableLiveData<List<String?>>()
    val listNames: LiveData<List<String?>> get() =  _listNames

    fun loadLists(context: Context) {
        _listNames.value = repository.getAllListNames(context)
    }
}