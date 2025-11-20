package studying.cats.lab1_semak7.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import studying.cats.lab1_semak7.databinding.ItemHeadersListBinding

class MainListAdapter(
    private val onItemClick: (String) -> Unit
): ListAdapter<String, MainListAdapter.HeaderViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val binding = ItemHeadersListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HeaderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        val name = getItem(position)
        holder.bind(name, onItemClick)
    }

    class HeaderViewHolder(private val binding: ItemHeadersListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(name: String, onItemClick: (String) -> Unit) {
            binding.tvListName.text = name
            binding.root.setOnClickListener {
                onItemClick(name)
            }
        }
    }

    companion object DiffCallback: DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
}