package studying.cats.lab3_semak7.main

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import studying.cats.lab3_semak7.adapter.MainListAdapter
import studying.cats.lab3_semak7.databinding.ActivityMainBinding
import studying.cats.lab3_semak7.detail.DetailActivity

class MainActivity: AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var listAdapter: MainListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        initList()
        initCallbacks()
        subscribe()
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            viewModel.loadLists(this@MainActivity)
        }
    }

    private fun initList() {
        listAdapter = MainListAdapter { clickedName ->
            navigateToDetail(listName = clickedName)
        }
    }

    private fun initCallbacks() {
        binding.fabAdd.setOnClickListener {
            navigateToDetail()
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = listAdapter
        }
    }

    private fun subscribe() {
        viewModel.listNames.observe(this) { names ->
            listAdapter.submitList(names)
            if (names.isNullOrEmpty()) {
                Toast.makeText(this, "Список пуст, создайте новый!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToDetail(listName: String? = null) {
        val intent = Intent(this, DetailActivity::class.java)
        if (listName == null) {
            val newListName = "List_${System.currentTimeMillis()}"
            intent.putExtra("LIST_NAME", newListName)
            intent.putExtra("IS_NEW", true)
        } else {
            intent.putExtra("LIST_NAME", listName)
            intent.putExtra("IS_NEW", false)
        }
        startActivity(intent)
    }
}