package iamutkarshtiwari.github.io.ananas.editimage.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup

import java.util.ArrayList
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import iamutkarshtiwari.github.io.ananas.R
import iamutkarshtiwari.github.io.ananas.editimage.adapter.viewholders.StickerViewHolder
import iamutkarshtiwari.github.io.ananas.editimage.fragment.StickerFragment

class StickerAdapter(private val stickerFragment: StickerFragment) :
    RecyclerView.Adapter<ViewHolder>() {
    private val pathList = ArrayList<String>()

    override fun getItemCount(): Int {
        return pathList.size
    }

    override fun getItemViewType(position: Int): Int {
        return 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewtype: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(
            R.layout.view_sticker_item, parent, false
        )
        return StickerViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val stickerViewHolder = viewHolder as StickerViewHolder
        val path = pathList[position]

        val imageUrl = "drawable/$path"
        val imageKey = stickerFragment.resources.getIdentifier(
            imageUrl,
            "drawable",
            stickerFragment.context!!.packageName
        )
        stickerViewHolder.image.setImageDrawable(stickerFragment.resources.getDrawable(imageKey))
        stickerViewHolder.image.tag = imageUrl
        stickerViewHolder.image.setOnClickListener{
            val data = it.tag as String
            stickerFragment.selectedStickerItem(data)
        }
    }

    fun addStickerImages(folderPath: String, stickerCount: Int) {
        pathList.clear()
        for (i in 0 until stickerCount) {
            pathList.add(folderPath + "_" + (i + 1))
        }
        this.notifyDataSetChanged()
    }

}
