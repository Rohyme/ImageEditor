package iamutkarshtiwari.github.io.ananas.editimage.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import iamutkarshtiwari.github.io.ananas.R
import java.util.*

class ColorPickerAdapter(private val context: Context) : RecyclerView.Adapter<ColorPickerAdapter.ViewHolder>() {

    private val colorPickerColors: List<Int>
    private var onColorPickerClickListener: ((Int) -> Unit)? = null

    init {
        this.colorPickerColors = getKelly22Colors(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =  LayoutInflater.from(context).inflate(R.layout.color_picker_item_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.colorPickerView.setBackgroundColor(colorPickerColors[position])
    }

    override fun getItemCount(): Int {
        return colorPickerColors.size
    }


    fun setOnColorPickerClickListener(block: ((Int) -> Unit)?) {
        this.onColorPickerClickListener = block
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var colorPickerView: View = itemView.findViewById(R.id.color_picker_view)

        init {
            itemView.setOnClickListener { v ->
                if (onColorPickerClickListener != null)
                    onColorPickerClickListener?.invoke(colorPickerColors[adapterPosition])
            }
        }
    }

    private fun getKelly22Colors(context: Context): List<Int> {
        val resources = context.resources
        val colorList = ArrayList<Int>()
        for (i in 0..21) {
            val resourceId =
                resources.getIdentifier("kelly_" + (i + 1), "color", context.packageName)
            colorList.add(ContextCompat.getColor(context,resourceId))
        }
        return colorList
    }
}
