package iamutkarshtiwari.github.io.ananas.editimage.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import iamutkarshtiwari.github.io.ananas.R
import iamutkarshtiwari.github.io.ananas.editimage.FEATURES_SCREENS
import iamutkarshtiwari.github.io.ananas.editimage.ImageEditorScreens
import iamutkarshtiwari.github.io.ananas.editimage.ModuleConfig
import iamutkarshtiwari.github.io.ananas.editimage.fragment.crop.CropFragment
import iamutkarshtiwari.github.io.ananas.editimage.fragment.paint.PaintFragment

class MainMenuFragment : BaseEditFragment(), View.OnClickListener {
    private var mainView: View? = null

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
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = inflater.inflate(
            R.layout.fragment_edit_image_main_menu,
            null
        )
        return mainView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (mainView == null) return
        stickerBtn = mainView!!.findViewById(R.id.btn_stickers)
        fliterBtn = mainView!!.findViewById(R.id.btn_filter)
        cropBtn = mainView!!.findViewById(R.id.btn_crop)
        rotateBtn = mainView!!.findViewById(R.id.btn_rotate)
        mTextBtn = mainView!!.findViewById(R.id.btn_text)
        mPaintBtn = mainView!!.findViewById(R.id.btn_paint)
        mBeautyBtn = mainView!!.findViewById(R.id.btn_beauty)
        mBrightnessBtn = mainView!!.findViewById(R.id.btn_brightness)
        mSaturationBtn = mainView!!.findViewById(R.id.btn_contrast)

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
        //do nothing
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
