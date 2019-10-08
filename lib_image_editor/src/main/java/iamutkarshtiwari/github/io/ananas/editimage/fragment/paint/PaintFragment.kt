package iamutkarshtiwari.github.io.ananas.editimage.fragment.paint

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import iamutkarshtiwari.github.io.ananas.BaseActivity
import iamutkarshtiwari.github.io.ananas.R
import iamutkarshtiwari.github.io.ananas.editimage.EditImageActivity
import iamutkarshtiwari.github.io.ananas.editimage.ModuleConfig
import iamutkarshtiwari.github.io.ananas.editimage.fragment.BaseEditFragment
import iamutkarshtiwari.github.io.ananas.editimage.fragment.MainMenuFragment
import iamutkarshtiwari.github.io.ananas.editimage.utils.Matrix3
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_edit_paint.*

class PaintFragment : BaseEditFragment(), View.OnClickListener, BrushConfigDialog.Properties,
    EraserConfigDialog.Properties {


    private var isEraser = false


    private val brushConfigDialog: BrushConfigDialog by lazy {
        BrushConfigDialog()
    }
    private val eraserConfigDialog: EraserConfigDialog by lazy {
        EraserConfigDialog()
    }
    private val custom_paint_view by lazy {
        ensureEditActivity().paintView
    }

    private var loadingDialog: Dialog? = null

    private var brushSize = INITIAL_WIDTH
    private var eraserSize = INITIAL_WIDTH
    private var brushAlpha = MAX_ALPHA
    private var brushColor = Color.WHITE

    private val compositeDisposable = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_paint, null)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        loadingDialog = BaseActivity.getLoadingDialog(
            getActivity()!!, R.string.iamutkarshtiwari_github_io_ananas_loading,
            false
        )
        settings.setOnClickListener(this)

        setupOptionsConfig()

        back_to_main.setOnClickListener(this)

        setClickListeners()
        initStroke()
    }

    private fun setupOptionsConfig() {
        brushConfigDialog.setPropertiesChangeListener(this)
        eraserConfigDialog.setPropertiesChangeListener(this)
    }

    private fun setClickListeners() {
        brush_btn.setOnClickListener(this)
        eraser_btn.setOnClickListener(this)
    }

    private fun initStroke() {
        custom_paint_view.setWidth(INITIAL_WIDTH)
        custom_paint_view.setColor(Color.WHITE)
        custom_paint_view.setStrokeAlpha(MAX_ALPHA)
        custom_paint_view.setEraserStrokeWidth(INITIAL_WIDTH)
    }

    override fun onClick(view: View) {
        if (view === back_to_main) {
            backToMain()
        } else if (view === eraser_btn) {
            if (!isEraser) {
                toggleButtons()
            }
        } else if (view === brush_btn) {
            if (isEraser) {
                toggleButtons()
            }
        } else if (view.id == R.id.settings) {
            showDialog(if (isEraser) eraserConfigDialog else brushConfigDialog)
        }
    }

    private fun showDialog(dialogFragment: BottomSheetDialogFragment) {
        val tag = dialogFragment.tag

        // Avoid IllegalStateException "Fragment already added"
        if (dialogFragment.isAdded) return

        dialogFragment.show(requireFragmentManager(), tag)

        if (isEraser) {
            updateEraserSize()
        } else {
            updateBrushParams()
        }
    }

    override fun onPause() {
        compositeDisposable.clear()
        super.onPause()
    }

    override fun backToMain() {
        activity.mode = EditImageActivity.MODE_NONE
        activity.bottomGallery.currentItem = MainMenuFragment.INDEX
        activity.mainImage.visibility = View.VISIBLE
        activity.bannerFlipper.showPrevious()

        custom_paint_view.reset()
        custom_paint_view.visibility = View.GONE
    }

    override fun onShow() {
        activity.mode = EditImageActivity.MODE_PAINT
        activity.mainImage.setImageBitmap(activity.mainBit)
        activity.bannerFlipper.showNext()

        custom_paint_view.visibility = View.VISIBLE
    }

    private fun toggleButtons() {
        isEraser = !isEraser
        custom_paint_view.setEraser(isEraser)
        eraser_icon.setImageResource(if (isEraser) R.drawable.ic_eraser_enabled else R.drawable.ic_eraser_disabled)
        brush_icon.setImageResource(if (isEraser) R.drawable.ic_brush_grey_24dp else R.drawable.ic_brush_white_24dp)
    }

    fun savePaintImage() {
        compositeDisposable.clear()

        val applyPaintDisposable = applyPaint(activity.mainBit)
            .flatMap {
                return@flatMap Single.just(it)
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { subscriber -> loadingDialog!!.show() }
            .doFinally { loadingDialog!!.dismiss() }
            .subscribe({ bitmap ->
                custom_paint_view.reset()
                activity.changeMainBitmap(bitmap, true)
                backToMain()
            }, { e ->
                // Do nothing on error
            })

        compositeDisposable.add(applyPaintDisposable)
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }

    private fun applyPaint(mainBitmap: Bitmap): Single<Bitmap?> {
        return Single.fromCallable {
            val touchMatrix = activity.mainImage.imageViewMatrix

            val resultBit = Bitmap.createBitmap(mainBitmap).copy(
                Bitmap.Config.ARGB_8888, true
            )
            val canvas = Canvas(resultBit)

            val data = FloatArray(9)
            touchMatrix.getValues(data)
            val cal = Matrix3(data)
            val inverseMatrix = cal.inverseMatrix()
            val matrix = Matrix()
            matrix.setValues(inverseMatrix.values)

            handleImage(canvas, matrix)

            resultBit
        }
    }

    private fun handleImage(canvas: Canvas, matrix: Matrix) {
        val f = FloatArray(9)
        matrix.getValues(f)

        val dx = f[Matrix.MTRANS_X].toInt()
        val dy = f[Matrix.MTRANS_Y].toInt()

        val scale_x = f[Matrix.MSCALE_X]
        val scale_y = f[Matrix.MSCALE_Y]

        canvas.save()
        canvas.translate(dx.toFloat(), dy.toFloat())
        canvas.scale(scale_x, scale_y)

        if (custom_paint_view.paintBit != null) {
            canvas.drawBitmap(custom_paint_view.paintBit, 0f, 0f, null)
        }
        canvas.restore()
    }

    override fun onColorChanged(colorCode: Int) {
        brushColor = colorCode
        updateBrushParams()
    }

    override fun onOpacityChanged(opacity: Int) {
        brushAlpha = opacity / MAX_PERCENT * MAX_ALPHA
        updateBrushParams()
    }

    override fun onBrushSizeChanged(brushSize: Int) {
        if (isEraser) {
            this.eraserSize = brushSize.toFloat()
            updateEraserSize()
        } else {
            this.brushSize = brushSize.toFloat()
            updateBrushParams()
        }
    }

    private fun updateBrushParams() {
        custom_paint_view.setColor(brushColor)
        custom_paint_view.setWidth(brushSize)
        custom_paint_view.setStrokeAlpha(brushAlpha)
    }

    private fun updateEraserSize() {
        custom_paint_view.setEraserStrokeWidth(eraserSize)
    }

    companion object {

        const val INDEX = ModuleConfig.INDEX_PAINT
        val TAG = PaintFragment::class.java.name

        private val MAX_PERCENT = 100f
        private val MAX_ALPHA = 255f
        private val INITIAL_WIDTH = 50f

        fun newInstance(): PaintFragment {
            return PaintFragment()
        }
    }
}
