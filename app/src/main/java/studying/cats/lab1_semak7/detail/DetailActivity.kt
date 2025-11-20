package studying.cats.lab1_semak7.detail

import CustomTypes.CustomDate
import CustomTypes.Point2D
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import studying.cats.lab1_semak7.databinding.ActivityDetailBinding
import studying.cats.lab1_semak7.composables.AddElementDialog
import studying.cats.lab1_semak7.detail.composables.HeadersListScreen

class DetailActivity: AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val viewModel: DetailViewModel by viewModels()
    private lateinit var listName: String
    private var isNew: Boolean = false
    private var type = ""

    private val showAddDialog = mutableStateOf(false)
    private val showAddSortedDialog = mutableStateOf(false)
    private val showAddByIndexDialog = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        listName = intent.getStringExtra("LIST_NAME") ?: "default_name"
        isNew = intent.getBooleanExtra("IS_NEW", false)
        if (!isNew) {
            type = when {
                listName.startsWith("Point2D_") -> "Point2D"
                listName.startsWith("CustomDate_") -> "CustomDate"
                else -> "String"
            }
        }

        initViews()
        initCallbacks()
        subscribe()

        if (isNew) {
            binding.creationPanel.visibility = View.VISIBLE
            binding.controlPanel1.visibility = View.GONE
            binding.controlPanel2.visibility = View.GONE
            binding.controlPanel3.visibility = View.GONE
        } else {
            binding.creationPanel.visibility = View.GONE
            binding.controlPanel1.visibility = View.VISIBLE
            binding.controlPanel2.visibility = View.VISIBLE
            binding.controlPanel3.visibility = View.VISIBLE
            viewModel.loadList(context = this, name = listName)
        }
    }

    private fun initViews() {
        val types = arrayOf("String", "Point2D", "CustomDate")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, types)
        binding.spinnerType.adapter = adapter
    }

    private fun initCallbacks() {
        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.btnCreate.setOnClickListener {
            val selectedType = binding.spinnerType.selectedItem.toString()
            type = selectedType
            viewModel.createNewList(listName, selectedType)
            binding.creationPanel.visibility = View.GONE
            binding.controlPanel1.visibility = View.VISIBLE
            binding.controlPanel2.visibility = View.VISIBLE
            binding.controlPanel3.visibility = View.VISIBLE
        }

        binding.btnAdd.setOnClickListener { showDialogIfListExists { showAddDialog.value = true } }
        binding.btnSort.setOnClickListener { viewModel.sortList() }
        binding.btnBalance.setOnClickListener { viewModel.balanceList() }
        binding.btnSaveJson.setOnClickListener { viewModel.saveToJson(this) }
        binding.btnDeleteList.setOnClickListener {
            viewModel.deleteCurrentList(this)
            finish()
        }

        binding.btnAddSorted.setOnClickListener { showDialogIfListExists { showAddSortedDialog.value = true } }
        binding.btnRemoveByIndex.setOnClickListener { showDialogIfListExists { showRemoveByIndexDialog() } }
        binding.btnFindByHeader.setOnClickListener { showDialogIfListExists { showFindByHeaderDialog() } }
        binding.btnLogHeaders.setOnClickListener {
            viewModel.logAllHeaders()
            Toast.makeText(this, "Заголовки выведены в Logcat", Toast.LENGTH_SHORT).show()
        }
        binding.btnAddByIndex.setOnClickListener { showDialogIfListExists { showDialogAddByIndex() } }
    }

    private fun subscribe() {
        binding.composeView.setContent {
            val listState = viewModel.currentList.observeAsState()

            val showAddDialogState by remember { showAddDialog }
            val showAddSortedDialogState by remember { showAddSortedDialog }
            val showAddByIndexDialogState by remember { showAddByIndexDialog }

            val hint = when (type) {
                "Point2D" -> "Элементы через ; (10,20; 1,1)"
                "CustomDate" -> "Элементы через ; (25/12/2025; 01/01/2023)"
                else -> "Элементы через ; (Элемент1; Элемент2)"
            }

            if (showAddDialogState) {
                AddElementDialog(
                    hint = hint,
                    onDismissRequest = { showAddDialog.value = false },
                    onConfirm = { inputText ->
                        try {
                            val (header, associatedList) = parseInput(inputText)
                            viewModel.addElement(header, associatedList)
                        } catch (e: Exception) { toastError(e) }
                    }
                )
            }

            if (showAddSortedDialogState) {
                AddElementDialog(
                    hint = hint,
                    onDismissRequest = { showAddSortedDialog.value = false },
                    onConfirm = { inputText ->
                        try {
                            val (header, associatedList) = parseInput(inputText)
                            viewModel.addSortedElement(header, associatedList)
                        } catch (e: Exception) { toastError(e) }
                    }
                )
            }

            if (showAddByIndexDialogState) {
                AddElementDialog(
                    hint = "Индекс;Заголовок;$hint",
                    onDismissRequest = { showAddByIndexDialog.value = false },
                    onConfirm = { inputText ->
                        try {
                            val (index, header, associatedList) = parseInputWithIndex(inputText)
                            viewModel.addElementAt(index, header, associatedList)
                        } catch (e: Exception) { toastError(e) }
                    }
                )
            }

            listState.value?.let { list ->
                HeadersListScreen(
                    list = list,
                    onItemClick = { clickedItem ->
                        Toast.makeText(this, "Нажат элемент: $clickedItem", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }

    private fun showDialogAddByIndex() {
        showAddByIndexDialog.value = true
    }

    private fun showRemoveByIndexDialog() {
        showInputDialog(
            title = "Удалить по индексу",
            hint = "Введите индекс элемента",
            inputType = InputType.TYPE_CLASS_NUMBER
        ) { text ->
            val index = text.toIntOrNull()
            if (index == null) {
                Toast.makeText(this, "Введите корректное число", Toast.LENGTH_SHORT).show()
                return@showInputDialog
            }
            viewModel.removeElementAt(index)
        }
    }

    private fun showFindByHeaderDialog() {
        val hint = when (type) {
            "Point2D" -> "Введите заголовок (x,y)"
            "CustomDate" -> "Введите заголовок (dd/MM/yyyy)"
            else -> "Введите заголовок"
        }
        showInputDialog(title = "Найти по заголовку", hint = hint) { text ->
            try {
                val headerToFind = parseHeader(text)
                val foundList = viewModel.findListByHeader(headerToFind)
                val message = if (foundList != null) "Найден список: $foundList" else "Список с таким заголовком не найден"
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            } catch (e: Exception) { toastError(e) }
        }
    }

    private fun showInputDialog(
        title: String,
        hint: String,
        inputType: Int = InputType.TYPE_CLASS_TEXT,
        onConfirm: (String) -> Unit
    ) {
        val editText = EditText(this).apply {
            this.hint = hint
            this.inputType = inputType
        }

        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(editText)
            .setPositiveButton("OK") { dialog, _ ->
                onConfirm(editText.text.toString())
                dialog.dismiss()
            }
            .setNegativeButton("Отмена") { dialog, _ -> dialog.cancel() }
            .show()
    }

    private fun showDialogIfListExists(action: () -> Unit) {
        if (viewModel.currentList.value == null) {
            Toast.makeText(this, "Список еще не создан", Toast.LENGTH_SHORT).show()
            return
        }
        action()
    }

    private fun toastError(e: Exception) {
        Toast.makeText(this, "Ошибка: ${e.message}", Toast.LENGTH_LONG).show()
        Log.e("DetailActivityError", "Error parsing input", e)
    }

    private fun parseInputWithIndex(listText: String): Triple<Int, Comparable<*>, List<Comparable<*>>> {
        if (listText.isBlank()) throw IllegalArgumentException("Ввод не может быть пустым")
        val allElementsText = listText.split(';').map { it.trim() }
        if (allElementsText.size < 2) throw IllegalArgumentException("Требуется как минимум индекс и заголовок")

        val index = allElementsText[0].toIntOrNull()
            ?: throw IllegalArgumentException("Первый элемент должен быть корректным индексом (числом)")

        val headerText = allElementsText[1]
        val associatedListText = if (allElementsText.size > 2) allElementsText.subList(2, allElementsText.size) else emptyList()

        val (header, associatedList) = when (type) {
            "Point2D" -> parsePoint(headerText) to associatedListText.map { parsePoint(it) }
            "CustomDate" -> parseDate(headerText) to associatedListText.map { parseDate(it) }
            else -> headerText to associatedListText
        }
        return Triple(index, header, associatedList)
    }

    private fun parseHeader(headerText: String): Comparable<*> {
        return when (type) {
            "Point2D" -> parsePoint(headerText)
            "CustomDate" -> parseDate(headerText)
            else -> headerText
        }
    }

    private fun parseInput(listText: String): Pair<Comparable<*>, List<Comparable<*>>> {
        if (listText.isBlank()) throw IllegalArgumentException("Список не может быть пустым")
        val allElementsText = listText.split(';').map { it.trim() }
        val headerText = allElementsText[0]
        val associatedListText = if (allElementsText.size > 1) allElementsText.subList(1, allElementsText.size) else emptyList()

        return when (type) {
            "Point2D" -> parsePoint(headerText) to associatedListText.map { parsePoint(it) }
            "CustomDate" -> parseDate(headerText) to associatedListText.map { parseDate(it) }
            else -> headerText to associatedListText
        }
    }

    private fun parsePoint(point: String): Point2D = try {
        Point2D.parse(point.trim())
    } catch (_: Exception) {
        throw IllegalArgumentException("Точка должна быть формата x,y. Вы ввели: '$point'")
    }

    private fun parseDate(text: String): CustomDate = try {
        CustomDate.parse(text.trim())
    } catch (_: Exception) {
        throw IllegalArgumentException("Дата должна быть в формате dd/MM/yyyy. Вы ввели: '$text'")
    }
}