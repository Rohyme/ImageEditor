package iamutkarshtiwari.github.io.ananas.editimage.fragment.paint

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar

import com.google.android.material.bottomsheet.BottomSheetDialogFragment

import iamutkarshtiwari.github.io.ananas.R

class EraserConfigDialog : BottomSheetDialogFragment(), SeekBar.OnSeekBarChangeListener {

    private var mProperties: Properties? = null

    interface Properties {
        fun onBrushSizeChanged(brushSize: Int)
    }

    override fun onDestroyView() {
        mProperties =null
        super.onDestroyView()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_eraser_config, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val eraserSizeSb = view.findViewById<SeekBar>(R.id.sbSize)
        eraserSizeSb.setOnSeekBarChangeListener(this)
    }

    fun setPropertiesChangeListener(properties: Properties) {
        mProperties = properties
    }

    override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
        val id = seekBar.id
        if (id == R.id.sbSize) {
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
