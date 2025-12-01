package studying.cats.lab3_semak7
import CustomTypes.CustomDate
import CustomTypes.Point2D
import HeadersList.HeadersList
import android.content.Context
import android.util.Log
import java.io.File
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
class HeadersListREPO private constructor() {
    private val inMemoryStorage: MutableMap<String, HeadersList<*>> = LinkedHashMap()

    @Suppress("UNCHECKED_CAST")
    fun getListByName(context: Context, name: String, type: Type): HeadersList<*>? {
        if (inMemoryStorage.containsKey(name)) {
            return inMemoryStorage[name]
        }

        val file = File(context.filesDir, "$name.json")
        if (file.exists()) {
            val loadedList = when (type) {
                is Class<*> -> when {
                    Point2D::class.java.isAssignableFrom(type) -> loadFromFile<Point2D>(file, type)
                    CustomDate::class.java.isAssignableFrom(type) -> loadFromFile<CustomDate>(file, type)
                    String::class.java.isAssignableFrom(type) -> loadFromFile<String>(file, type)
                    else -> null
                }
                else -> null
            }

            if (loadedList != null) {
                inMemoryStorage[name] = loadedList
            }
            return loadedList
        }

        return null
    }

    fun createList(name: String, list: HeadersList<*>) {
        inMemoryStorage[name] = list
    }

    fun deleteList(context: Context, name: String) {
        inMemoryStorage.remove(name)
        val file = File(context.filesDir, "$name.json")
        if (file.exists()) {
            file.delete()
        }
    }

    fun getAllListNames(context: Context): List<String> {
        return context.filesDir.listFiles { _, name -> name.endsWith(".json") }
            ?.map { it.nameWithoutExtension }
            ?: emptyList()
    }

    fun saveListToJson(context: Context, name: String) {
        val list = inMemoryStorage[name] ?: return
        val file = File(context.filesDir, "$name.json")
        try {
            list.saveToFile(file)
            Log.d("path:", file.path)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun <T : Comparable<T>> loadFromFile(file: File, type: Type): HeadersList<T>? {
        return try {
            val nodeType = object : ParameterizedType {
                override fun getActualTypeArguments(): Array<Type> = arrayOf(type)
                override fun getRawType(): Type = HeadersList.SerializableNode::class.java
                override fun getOwnerType(): Type? = null
            }
            HeadersList.loadFromFile(file, nodeType)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    companion object {
        @Volatile
        private var instance: HeadersListREPO? = null

        fun getInstance(): HeadersListREPO =
            instance ?: synchronized(this) {
                instance ?: HeadersListREPO().also { instance = it }
            }
    }
}