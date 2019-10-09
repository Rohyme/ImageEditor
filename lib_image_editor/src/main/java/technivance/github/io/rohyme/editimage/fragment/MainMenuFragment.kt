package technivance.github.io.rohyme.editimage.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import technivance.github.io.rohyme.R
import technivance.github.io.rohyme.editimage.FEATURES_SCREENS
import technivance.github.io.rohyme.editimage.ImageEditorScreens
import technivance.github.io.rohyme.editimage.ModuleConfig
import technivance.github.io.rohyme.editimage.fragment.crop.CropFragment
import technivance.github.io.rohyme.editimage.fragment.paint.PaintFragment

class MainMenuFragment : BaseEditFragment(), View.OnClickListener {

    private var stickerBtn: View? = null
    private var fliterBtn: View? = null
    private var cropBtn: View? = null
    private var rotateBtn: View? = null
    private var mTextBtn: View? = null
    private var mPaintBtn: View? = null
    private var mBeautyBtn: View? = null
    private var mBrightnessBtn: View? = null
    private var mSaturationBtn: View? = null

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
        if (view == null) return
        stickerBtn = view?.findViewById(R.id.btn_stickers)
        fliterBtn = view?.findViewById(R.id.btn_filter)
        cropBtn = view?.findViewById(R.id.btn_crop)
        rotateBtn = view?.findViewById(R.id.btn_rotate)
        mTextBtn = view?.findViewById(R.id.btn_text)
        mPaintBtn = view?.findViewById(R.id.btn_paint)
        mBeautyBtn = view?.findViewById(R.id.btn_beauty)
        mBrightnessBtn = view?.findViewById(R.id.btn_brightness)
        mSaturationBtn = view?.findViewById(R.id.btn_contrast)

        stickerBtn?.initView(featuresScreens.withStickers)
        fliterBtn?.initView(featuresScreens.withFilter)
        cropBtn?.initView(featuresScreens.withCrop)
        rotateBtn?.initView(featuresScreens.withRotate)
        mTextBtn?.initView(featuresScreens.withAddText)
        mPaintBtn?.initView(featuresScreens.withPaint)
        mBeautyBtn?.initView(featuresScreens.withBeauty)
        mBrightnessBtn?.initView(featuresScreens.withBrightness)
        mSaturationBtn?.initView(featuresScreens.withSaturation)
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
        if (v === stickerBtn) {
            onStickClick()
        } else if (v === fliterBtn) {
            onFilterClick()
        } else if (v === cropBtn) {
            onCropClick()
        } else if (v === rotateBtn) {
            onRotateClick()
        } else if (v === mTextBtn) {
            onAddTextClick()
        } else if (v === mPaintBtn) {
            onPaintClick()
        } else if (v === mBeautyBtn) {
            onBeautyClick()
        } else if (v === mBrightnessBtn) {
            onBrightnessClick()
        } else if (v === mSaturationBtn) {
            onContrastClick()
        }
    }

    private fun onStickClick() {
        activity.bottomGallery.currentItem = StickerFragment.INDEX
        activity.stickerFragment.onShow()
    }

    private fun onFilterClick() {
        activity.bottomGallery.currentItem = FilterListFragment.INDEX
        activity.filterListFragment.onShow()
    }

    private fun onCropClick() {
        activity.bottomGallery.currentItem = CropFragment.INDEX
        activity.cropFragment.onShow()
    }

    private fun onRotateClick() {
        activity.bottomGallery.currentItem = RotateFragment.INDEX
        activity.rotateFragment.onShow()
    }

    private fun onAddTextClick() {
        activity.bottomGallery.currentItem = AddTextFragment.INDEX
        activity.addTextFragment.onShow()
    }

    private fun onPaintClick() {
        activity.bottomGallery.currentItem = PaintFragment.INDEX
        activity.paintFragment.onShow()
    }

    private fun onBeautyClick() {
        activity.bottomGallery.currentItem = BeautyFragment.INDEX
        activity.beautyFragment.onShow()
    }

    private fun onBrightnessClick() {
        activity.bottomGallery.currentItem = BrightnessFragment.INDEX
        activity.brightnessFragment.onShow()
    }

    private fun onContrastClick() {
        activity.bottomGallery.currentItem = SaturationFragment.INDEX
        activity.saturationFragment.onShow()
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
