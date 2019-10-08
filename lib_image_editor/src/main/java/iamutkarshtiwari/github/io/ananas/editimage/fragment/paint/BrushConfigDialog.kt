package iamutkarshtiwari.github.io.ananas.editimage.fragment.paint


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.google.android.material.bottomsheet.BottomSheetDialogFragment

import iamutkarshtiwari.github.io.ananas.R
import iamutkarshtiwari.github.io.ananas.editimage.adapter.ColorPickerAdapter

class BrushConfigDialog : BottomSheetDialogFragment(), SeekBar.OnSeekBarChangeListener {

    private var mProperties: Properties? = null

    interface Properties {
        fun onColorChanged(colorCode: Int)

        fun onOpacityChanged(opacity: Int)

        fun onBrushSizeChanged(brushSize: Int)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_brush_config, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mProperties = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rvColor = view.findViewById<RecyclerView>(R.id.rvColors)
        val sbOpacity = view.findViewById<SeekBar>(R.id.sbOpacity)
        val sbBrushSize = view.findViewById<SeekBar>(R.id.sbSize)

        sbOpacity.setOnSeekBarChangeListener(this)
        sbBrushSize.setOnSeekBarChangeListener(this)

        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        rvColor.layoutManager = layoutManager
        rvColor.setHasFixedSize(true)
        val colorPickerAdapter = ColorPickerAdapter(activity!!)
        colorPickerAdapter.setOnColorPickerClickListener { colorCode ->
            if (mProperties != null) {
                dismiss()
                mProperties!!.onColorChanged(colorCode)
            }
        }

        rvColor.adapter = colorPickerAdapter
    }

    fun setPropertiesChangeListener(properties: Properties) {
        mProperties = properties
    }

    override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
        val id = seekBar.id
        if (id == R.id.sbOpacity) {
            if (mProperties != null) {
                mProperties!!.onOpacityChanged(i)
            }
        } else if (id == R.id.sbSize) {
            if (mProperties != null) {
                mProperties!!.onBrushSizeChanged(i)
            }
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {

    }
}
