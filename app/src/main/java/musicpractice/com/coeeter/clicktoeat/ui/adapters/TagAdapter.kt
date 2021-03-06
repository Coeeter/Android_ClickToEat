package musicpractice.com.coeeter.clicktoeat.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import musicpractice.com.coeeter.clicktoeat.databinding.RecyclerTagItemBinding

class TagAdapter(private val tags: List<String>) : RecyclerView.Adapter<TagAdapter.ViewHolder>() {
    class ViewHolder(val binding: RecyclerTagItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(
                RecyclerTagItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tag.text = tags[position]
    }

    override fun getItemCount(): Int = tags.size
}