package iamutkarshtiwari.github.io.ananas.editimage.fragment.crop

import android.graphics.Bitmap
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.theartofdev.edmodo.cropper.CropImageView
import iamutkarshtiwari.github.io.ananas.R
import iamutkarshtiwari.github.io.ananas.editimage.EditImageActivity
import iamutkarshtiwari.github.io.ananas.editimage.ModuleConfig
import iamutkarshtiwari.github.io.ananas.editimage.fragment.BaseEditFragment
import iamutkarshtiwari.github.io.ananas.editimage.view.imagezoom.ImageViewTouchBase
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_edit_image_crop.*


class CropFragment : BaseEditFragment() {

    private var cropPanel: CropImageView? = null

    private var selectedTextView: TextView? = null

    private val disposables = CompositeDisposable()

    private val croppedBitmap: Single<Bitmap>
        get() = Single.fromCallable { cropPanel?.croppedImage }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_image_crop, null)
    }

    override fun onDestroyView() {
        selectedTextView =null
        cropPanel = null
        super.onDestroyView()
    }

    private fun setUpRatioList() {
        ratio_list_group.removeAllViews()
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        params.gravity = Gravity.CENTER
        params.leftMargin = 20
        params.rightMargin = 20

        val ratioTextList = RatioText.values()
        for (i in ratioTextList.indices) {
            val text = TextView(activity)
            toggleButtonStatus(text, false)
            text.textSize = 15f
            text.isAllCaps = true
            text.setTypeface(text.typeface, Typeface.BOLD)
            text.text = resources.getText(ratioTextList[i].ratioTextId)
            ratio_list_group.addView(text, params)

            if (i == 0) {
                selectedTextView = text
            }

            text.tag = ratioTextList[i]
            text.setOnClickListener {
                toggleButtonStatus(selectedTextView!!, false)

                val currentTextView = it as TextView
                toggleButtonStatus(currentTextView, true)

                selectedTextView = currentTextView

                val ratioText = currentTextView.tag as RatioText
                when {
                    ratioText === RatioText.FREE -> cropPanel?.setFixedAspectRatio(false)
                    ratioText === RatioText.FIT_IMAGE -> {
                        val currentBmp = ensureEditActivity().mainBit
                        cropPanel?.setAspectRatio(currentBmp.width, currentBmp.height)
                    }
                    else -> {
                        val aspectRatio = ratioText.aspectRatio
                        cropPanel?.setAspectRatio(aspectRatio.aspectX, aspectRatio.aspectY)
                    }
                }
            }
        }
        toggleButtonStatus(selectedTextView!!, true)
    }


    private fun toggleButtonStatus(view: TextView, isActive: Boolean) {
        view.setTextColor(getColorFromRes(if (isActive) SELECTED_COLOR else UNSELECTED_COLOR))
        view.typeface = if (isActive) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
    }

    private fun getColorFromRes(@ColorRes resId: Int): Int {
        return ContextCompat.getColor(activity, resId)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        setUpRatioList()
        this.cropPanel = ensureEditActivity().cropPanel
        back_to_main.setOnClickListener(BackToMenuClick())
    }

    override fun onShow() {
        activity.mode = EditImageActivity.MODE_CROP

        activity.mainImage.visibility = View.GONE
        cropPanel?.visibility = View.VISIBLE
        activity.mainImage.setImageBitmap(activity.mainBit)
        activity.mainImage.displayType = ImageViewTouchBase.DisplayType.FIT_TO_SCREEN
        activity.mainImage.setScaleEnabled(false)

        activity.bannerFlipper.showNext()
        cropPanel?.setImageBitmap(activity.mainBit)
        cropPanel?.setFixedAspectRatio(false)
    }


    private inner class BackToMenuClick : OnClickListener {
        override fun onClick(v: View) {
            backToMain()
        }
    }


    override fun backToMain() {
        activity.mode = EditImageActivity.MODE_NONE
        cropPanel?.visibility = View.GONE
        activity.mainImage.visibility = View.VISIBLE

        activity.mainImage.setScaleEnabled(true)
        activity.bottomGallery.currentItem = 0

        if (selectedTextView != null) {
            selectedTextView!!.setTextColor(getColorFromRes(UNSELECTED_COLOR))
        }

        activity.bannerFlipper.showPrevious()
    }


    fun applyCropImage() {
        disposables.add(croppedBitmap
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { subscriber -> ensureEditActivity()?.showLoadingDialog() }
            .doFinally { ensureEditActivity()?.dismissLoadingDialog() }
            .subscribe({ bitmap ->
                activity.changeMainBitmap(bitmap, true)
                backToMain()
            }, { e ->
                e.printStackTrace()
                backToMain()
                Toast.makeText(context, "Error while saving image", Toast.LENGTH_SHORT).show()
            })
        )
    }

    override fun onStop() {
        disposables.clear()
        super.onStop()
    }

    override fun onDestroy() {
        disposables.dispose()
        super.onDestroy()
    }

    companion object {
        const val INDEX = ModuleConfig.INDEX_CROP
        val TAG = CropFragment::class.java.name

        private val SELECTED_COLOR = R.color.white
        private val UNSELECTED_COLOR = R.color.text_color_gray_3

        fun newInstance(): CropFragment {
            return CropFragment()
        }
    }
}
