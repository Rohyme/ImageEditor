package technivance.github.io.rohyme.editimage.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_edit_image_main_menu.*

import technivance.github.io.rohyme.R
import technivance.github.io.rohyme.editimage.FEATURES_SCREENS
import technivance.github.io.rohyme.editimage.ImageEditorScreens
import technivance.github.io.rohyme.editimage.ModuleConfig
import technivance.github.io.rohyme.editimage.fragment.crop.CropFragment
import technivance.github.io.rohyme.editimage.fragment.paint.PaintFragment

class MainMenuFragment : BaseEditFragment(), View.OnClickListener {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.fragment_edit_image_main_menu,
            null
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        btn_stickers?.initView(false)
        btn_filter?.initView(false)
        btn_crop?.initView(featuresScreens.withCrop)
        btn_rotate?.initView(featuresScreens.withRotate)
        btn_text?.initView(featuresScreens.withAddText)
        btn_paint?.initView(featuresScreens.withPaint)
        btn_beauty?.initView(featuresScreens.withBeauty)
        btn_brightness?.initView(featuresScreens.withBrightness)
        btn_contrast?.initView(featuresScreens.withSaturation)
    }

    private fun View.initView(isShown: Boolean) {
        setOnClickListener(this@MainMenuFragment)
        showOrHide(isShown)
    }

    private fun View.showOrHide(isShown: Boolean) {
        visibility = if (isShown) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    override fun onShow() {
        // do nothing
    }

    override fun backToMain() {
        // do nothing
    }

    override fun onClick(v: View) {
        when (v) {
            btn_stickers -> onStickClick()
            btn_filter -> onFilterClick()
            btn_crop -> onCropClick()
            btn_rotate -> onRotateClick()
            btn_text -> onAddTextClick()
            btn_paint -> onPaintClick()
            btn_beauty -> onBeautyClick()
            btn_brightness -> onBrightnessClick()
            btn_contrast -> onContrastClick()
        }
    }

    private fun onStickClick() {
        ensureEditActivity().bottomGallery.currentItem = StickerFragment.INDEX
//        ensureEditActivity().stickerFragment.onShow()
    }

    private fun onFilterClick() {
        ensureEditActivity().bottomGallery.currentItem = FilterListFragment.INDEX
        ensureEditActivity().filterListFragment.onShow()
    }

    private fun onCropClick() {
        ensureEditActivity().bottomGallery.currentItem = CropFragment.INDEX
        ensureEditActivity().cropFragment.onShow()
    }

    private fun onRotateClick() {
        ensureEditActivity().bottomGallery.currentItem = RotateFragment.INDEX
        ensureEditActivity().rotateFragment.onShow()
    }

    private fun onAddTextClick() {
        ensureEditActivity().bottomGallery.currentItem = AddTextFragment.INDEX
        ensureEditActivity().addTextFragment.onShow()
    }

    private fun onPaintClick() {
        ensureEditActivity().bottomGallery.currentItem = PaintFragment.INDEX
        ensureEditActivity().paintFragment.onShow()
    }

    private fun onBeautyClick() {
        ensureEditActivity().bottomGallery.currentItem = BeautyFragment.INDEX
        ensureEditActivity().beautyFragment.onShow()
    }

    private fun onBrightnessClick() {
        ensureEditActivity().bottomGallery.currentItem = BrightnessFragment.INDEX
        ensureEditActivity().brightnessFragment.onShow()
    }

    private fun onContrastClick() {
        ensureEditActivity().bottomGallery.currentItem = SaturationFragment.INDEX
        ensureEditActivity().saturationFragment.onShow()
    }

    private val featuresScreens by lazy {
        (arguments?.getSerializable(FEATURES_SCREENS) as? ImageEditorScreens)
            ?: ImageEditorScreens()
    }

    companion object {
        const val INDEX = ModuleConfig.INDEX_MAIN

        val TAG = MainMenuFragment::class.java.name

        fun newInstance(screens: ImageEditorScreens?): MainMenuFragment {
            return MainMenuFragment().apply {
                arguments = Bundle().apply {
                    screens?.let {
                        putSerializable(FEATURES_SCREENS, it)
                    }
                }
            }
        }
    }
}
