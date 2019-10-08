package iamutkarshtiwari.github.io.ananas.editimage.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast

import java.util.ArrayList

import iamutkarshtiwari.github.io.ananas.R
import iamutkarshtiwari.github.io.ananas.editimage.EditImageActivity
import iamutkarshtiwari.github.io.ananas.editimage.ModuleConfig
import iamutkarshtiwari.github.io.ananas.editimage.gesture.MultiTouchListener
import iamutkarshtiwari.github.io.ananas.editimage.interfaces.OnGestureControl
import iamutkarshtiwari.github.io.ananas.editimage.interfaces.OnMainBitmapChangeListener
import iamutkarshtiwari.github.io.ananas.editimage.interfaces.OnMultiTouchListener
import iamutkarshtiwari.github.io.ananas.editimage.interfaces.OnPhotoEditorListener
import iamutkarshtiwari.github.io.ananas.editimage.interfaces.OnTextEditorListener
import iamutkarshtiwari.github.io.ananas.editimage.layout.ZoomLayout
import iamutkarshtiwari.github.io.ananas.editimage.view.TextStickerView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class AddTextFragment : BaseEditFragment(), OnPhotoEditorListener, View.OnClickListener,
    OnMainBitmapChangeListener, OnMultiTouchListener {

    private var mainView: View? = null
    private var textStickersParentView: TextStickerView? = null
    private var zoomLayout: ZoomLayout? = null

    private var inputMethodManager: InputMethodManager? = null
    private val compositeDisposable = CompositeDisposable()
    private var addedViews: MutableList<View>? = null

    private val isInputMethodShow: Boolean
        get() = inputMethodManager!!.isActive

    private val textStickerLayout: View
        get() {
            val layoutInflater = LayoutInflater.from(context)
            val rootView = layoutInflater.inflate(R.layout.view_text_sticker_item, null)
            val txtText = rootView.findViewById<TextView>(R.id.text_sticker_tv)
            if (txtText != null) {
                txtText.gravity = Gravity.CENTER
                val imgClose = rootView.findViewById<ImageView>(R.id.sticker_delete_btn)
                imgClose?.setOnClickListener { view -> deleteViewFromParent(rootView) }
            }
            return rootView
        }

    override fun onDestroyView() {
        addedViews = null
        zoomLayout = null
        mainView = null
        super.onDestroyView()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = inflater.inflate(R.layout.fragment_edit_image_add_text, container, false)
        return mainView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val editImageActivity = ensureEditActivity()

        inputMethodManager =
            editImageActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        textStickersParentView = editImageActivity.findViewById(R.id.text_sticker_panel)
        textStickersParentView!!.isDrawingCacheEnabled = true
        addedViews = ArrayList()

        zoomLayout = editImageActivity.findViewById(R.id.text_sticker_panel_frame)

        val backToMenu = mainView!!.findViewById<View>(R.id.back_to_main)
        backToMenu.setOnClickListener(BackToMenuClick())

        val addTextButton = mainView!!.findViewById<LinearLayout>(R.id.add_text_btn)
        addTextButton.setOnClickListener(this)
    }

    private fun showTextEditDialog(rootView: View, text: String, colorCode: Int) {
        val textEditorDialogFragment = TextEditorDialogFragment.show(activity, text, colorCode)
        textEditorDialogFragment.setOnTextEditorListener(OnTextEditorListener { inputText, colorCode1 ->
            editText(
                rootView,
                inputText,
                colorCode1
            )
        })
    }

    override fun onAddViewListener(numberOfAddedViews: Int) {}

    override fun onRemoveViewListener(numberOfAddedViews: Int) {}

    override fun onStartViewChangeListener() {}

    override fun onStopViewChangeListener() {}

    override fun onRemoveViewListener(removedView: View) {}

    override fun onClick(v: View) {
        val id = v.id
        if (id == R.id.add_text_btn) {
            val textEditorDialogFragment = TextEditorDialogFragment.show(activity)
            textEditorDialogFragment.setOnTextEditorListener(OnTextEditorListener { text, colorCodeTextView ->
                this.addText(
                    text,
                    colorCodeTextView
                )
            })
        }
    }

    fun hideInput() {
        if (getActivity() != null && getActivity()!!.currentFocus != null && isInputMethodShow) {
            inputMethodManager!!.hideSoftInputFromWindow(
                getActivity()!!.currentFocus!!.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    override fun onMainBitmapChange() {
        textStickersParentView!!.updateImageBitmap(activity.mainBit)
    }

    private inner class BackToMenuClick : OnClickListener {
        override fun onClick(v: View) {
            backToMain()
        }
    }

    override fun backToMain() {
        hideInput()
        clearAllStickers()
        activity.mode = EditImageActivity.MODE_NONE
        activity.bottomGallery.currentItem = MainMenuFragment.INDEX
        activity.mainImage.visibility = View.VISIBLE
        activity.bannerFlipper.showPrevious()
        textStickersParentView!!.visibility = View.GONE
    }

    override fun onShow() {
        activity.mode = EditImageActivity.MODE_TEXT
        activity.mainImage.visibility = View.GONE
        textStickersParentView!!.updateImageBitmap(activity.mainBit)
        activity.bannerFlipper.showNext()
        textStickersParentView!!.visibility = View.VISIBLE

        autoScaleImageToFitBounds()
    }

    private fun autoScaleImageToFitBounds() {
        textStickersParentView!!.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                textStickersParentView!!.viewTreeObserver.removeOnGlobalLayoutListener(this)
                scaleImage()
            }
        })
    }

    private fun scaleImage() {
        val zoomLayoutWidth = zoomLayout!!.width.toFloat()
        val zoomLayoutHeight = zoomLayout!!.height.toFloat()

        val imageViewWidth = textStickersParentView!!.width.toFloat()
        val imageViewHeight = textStickersParentView!!.height.toFloat()

        // To avoid divideByZero exception
        if (imageViewHeight != 0f && imageViewWidth != 0f && zoomLayoutHeight != 0f && zoomLayoutWidth != 0f) {
            val offsetFactorX = zoomLayoutWidth / imageViewWidth
            val offsetFactorY = zoomLayoutHeight / imageViewHeight

            val scaleFactor = Math.min(offsetFactorX, offsetFactorY)
            zoomLayout!!.setChildScale(scaleFactor)
        }
    }

    fun applyTextImage() {
        // Hide borders of all stickers before save
        updateViewsBordersVisibilityExcept(null)
        compositeDisposable.clear()
        val applyTextDisposable =
            Observable.fromCallable { getFinalBitmapFromView(textStickersParentView!!) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { bitmap ->
                        if (addedViews!!.size > 0) {
                            activity.changeMainBitmap(bitmap, true)
                        }
                        backToMain()
                    },
                    { e ->
                        e.printStackTrace()
                        backToMain()
                        Toast.makeText(
                            context,
                            getString(R.string.iamutkarshtiwari_github_io_ananas_save_error),
                            Toast.LENGTH_SHORT
                        ).show()
                    })
        compositeDisposable.add(applyTextDisposable)
    }

    private fun clearAllStickers() {
        textStickersParentView!!.removeAllViews()
    }

    private fun getFinalBitmapFromView(view: View): Bitmap {
        val finalBitmap = view.drawingCache
        val resultBitmap = finalBitmap.copy(Bitmap.Config.ARGB_8888, true)

        val textStickerHeightCenterY = textStickersParentView!!.height / 2
        val textStickerWidthCenterX = textStickersParentView!!.width / 2

        val imageViewHeight = textStickersParentView!!.bitmapHolderImageView.height
        val imageViewWidth = textStickersParentView!!.bitmapHolderImageView.width

        // Crop actual image from textStickerView
        return Bitmap.createBitmap(
            resultBitmap,
            textStickerWidthCenterX - imageViewWidth / 2,
            textStickerHeightCenterY - imageViewHeight / 2,
            imageViewWidth,
            imageViewHeight
        )
    }

    override fun onPause() {
        compositeDisposable.clear()
        super.onPause()
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun addText(text: String, colorCodeTextView: Int) {
        val textStickerView = textStickerLayout
        val textInputTv = textStickerView.findViewById<TextView>(R.id.text_sticker_tv)
        val imgClose = textStickerView.findViewById<ImageView>(R.id.sticker_delete_btn)
        val frameBorder = textStickerView.findViewById<FrameLayout>(R.id.sticker_border)

        textInputTv.text = text
        textInputTv.setTextColor(colorCodeTextView)
        textInputTv.setTextSize(
            TypedValue.COMPLEX_UNIT_SP,
            resources.getDimension(R.dimen.text_sticker_size)
        )

        val multiTouchListener = MultiTouchListener(
            imgClose,
            this.textStickersParentView,
            activity.mainImage,
            this, context
        )
        multiTouchListener.setOnGestureControl(object : OnGestureControl {

            internal var isDownAlready = false

            override fun onClick() {
                val isBackgroundVisible = frameBorder.tag != null && frameBorder.tag as Boolean
                if (isBackgroundVisible && !isDownAlready) {
                    val textInput = textInputTv.text.toString()
                    val currentTextColor = textInputTv.currentTextColor
                    showTextEditDialog(textStickerView, textInput, currentTextColor)
                }
            }

            override fun onDown() {
                val isBackgroundVisible = frameBorder.tag != null && frameBorder.tag as Boolean
                if (!isBackgroundVisible) {
                    frameBorder.setBackgroundResource(R.drawable.background_border)
                    imgClose.visibility = View.VISIBLE
                    frameBorder.tag = true
                    updateViewsBordersVisibilityExcept(textStickerView)
                    isDownAlready = true
                } else {
                    isDownAlready = false
                }
            }

            override fun onLongClick() {}
        })

        textStickerView.setOnTouchListener(multiTouchListener)
        addViewToParent(textStickerView)
    }

    private fun updateViewsBordersVisibilityExcept(keepView: View?) {
        for (view in addedViews!!) {
            if (view !== keepView) {
                val border = view.findViewById<FrameLayout>(R.id.sticker_border)
                border.setBackgroundResource(0)
                val closeBtn = view.findViewById<ImageView>(R.id.sticker_delete_btn)
                closeBtn.visibility = View.GONE
                border.tag = false
            }
        }
    }

    private fun editText(view: View, inputText: String, colorCode: Int) {
        val inputTextView = view.findViewById<TextView>(R.id.text_sticker_tv)
        if (inputTextView != null && addedViews!!.contains(view) && !TextUtils.isEmpty(inputText)) {
            inputTextView.text = inputText
            inputTextView.setTextColor(colorCode)
            textStickersParentView!!.updateViewLayout(view, view.layoutParams)
            val i = addedViews!!.indexOf(view)
            if (i > -1) addedViews!![i] = view
        }
    }

    private fun addViewToParent(view: View) {
        val params = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
        textStickersParentView!!.addView(view, params)
        addedViews!!.add(view)
        updateViewsBordersVisibilityExcept(view)
    }

    private fun deleteViewFromParent(view: View) {
        textStickersParentView!!.removeView(view)
        addedViews!!.remove(view)
        textStickersParentView!!.invalidate()
        updateViewsBordersVisibilityExcept(null)
    }

    companion object {

       const val INDEX = ModuleConfig.INDEX_ADDTEXT
        val TAG = AddTextFragment::class.java.name

        fun newInstance(): AddTextFragment {
            return AddTextFragment()
        }
    }
}
