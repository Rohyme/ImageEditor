package iamutkarshtiwari.github.io.ananas.editimage.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import iamutkarshtiwari.github.io.ananas.R
import iamutkarshtiwari.github.io.ananas.editimage.fragment.StickerFragment

class StickerTypeAdapter(private val stickerFragment: StickerFragment) :
    RecyclerView.Adapter<ViewHolder>() {
    private val stickerPath: Array<String> = stickerFragment.resources.getStringArray(R.array.iamutkarshtiwari_github_io_ananas_types)
    private val stickerPathName: Array<String> = stickerFragment.resources.getStringArray(R.array.iamutkarshtiwari_github_io_ananas_type_names)
    private val stickerCount: IntArray = stickerFragment.resources.getIntArray(R.array.iamutkarshtiwari_github_io_ananas_type_count)

    inner class ImageHolder internal constructor(itemView: View) : ViewHolder(itemView) {
        var icon: ImageView = itemView.findViewById(R.id.icon)
        var text: TextView = itemView.findViewById(R.id.text)

    }

    override fun getItemCount(): Int {
        return stickerPathName.size
    }

    override fun getItemViewType(position: Int): Int {
        return 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewtype: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.view_sticker_type_item, parent, false
        )
        return ImageHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageHolder = holder as ImageHolder
        val name = stickerPathName[position]
        imageHolder.text.text = name
        imageHolder.text.setTag(
            R.id.iamutkarshtiwari_github_io_ananas_TAG_STICKERS_PATH,
            stickerPath[position]
        )
        imageHolder.text.setTag(
            R.id.iamutkarshtiwari_github_io_ananas_TAG_STICKERS_COUNT,
            stickerCount[position]
        )
        imageHolder.text.setOnClickListener{v ->
            val data = v.getTag(R.id.iamutkarshtiwari_github_io_ananas_TAG_STICKERS_PATH) as String
            val count = v.getTag(R.id.iamutkarshtiwari_github_io_ananas_TAG_STICKERS_COUNT) as Int
            stickerFragment.swipToStickerDetails(data, count)
        }
    }

}
