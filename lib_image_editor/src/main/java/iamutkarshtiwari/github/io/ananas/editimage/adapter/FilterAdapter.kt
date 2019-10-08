package iamutkarshtiwari.github.io.ananas.editimage.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import iamutkarshtiwari.github.io.ananas.R
import iamutkarshtiwari.github.io.ananas.editimage.adapter.viewholders.FilterViewHolder
import iamutkarshtiwari.github.io.ananas.editimage.fragment.FilterListFragment

class FilterAdapter(
    private val filterListFragment: FilterListFragment,
    private val context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val filters: Array<String> = filterListFragment.resources.getStringArray(R.array.iamutkarshtiwari_github_io_ananas_filters)
    private val filterImages: Array<String> = filterListFragment.resources.getStringArray(R.array.iamutkarshtiwari_github_io_ananas_filter_drawable_list)

    override fun getItemCount(): Int {
        return filterImages.size
    }

    override fun getItemViewType(position: Int): Int {
        return DEFAULT_TYPE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.filter_item, parent, false
        )
        return FilterViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, pos: Int) {
        val filterViewHolder = holder as FilterViewHolder
        val name = filters[pos]
        filterViewHolder.text.text = name

        val imageUrl = "drawable/" + filterImages[pos]
        val imageKey =
            filterListFragment.resources.getIdentifier(imageUrl, "drawable", context.packageName)
        filterViewHolder.icon.setImageDrawable(ContextCompat.getDrawable(context,imageKey))

        filterViewHolder.icon.tag = pos
        filterViewHolder.icon.setOnClickListener { v -> filterListFragment.enableFilter(pos) }
    }

    companion object {
        private const val DEFAULT_TYPE = 1
    }
}
