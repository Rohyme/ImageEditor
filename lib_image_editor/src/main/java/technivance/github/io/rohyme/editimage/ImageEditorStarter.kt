package technivance.github.io.rohyme.editimage

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import java.io.Serializable

data class ImageEditorScreens(
    var withPaint: Boolean = false,
    var withAddText: Boolean = false,
    var withFilter: Boolean = false,
    var withRotate: Boolean = false,
    var withCrop: Boolean = false,
    var withBrightness: Boolean = false,
    var withSaturation: Boolean = false,
    var withForcePortrait: Boolean = true,
    var withStickers: Boolean = false,
    var withBeauty: Boolean = false
) : Serializable

fun Fragment.startImageEditor(
    sourcePath: String?,
    destinationPath: String?,
    featuresScreens: (ImageEditorScreens.() -> Unit)? = null
) {

    val intent = Intent(
        context,
        EditImageActivity::class.java
    )
    val features = ImageEditorScreens()
    featuresScreens?.let {
        features.apply(it)
    }
    intent.putExtra(SOURCE_PATH, sourcePath)
    intent.putExtra(OUTPUT_PATH, destinationPath)
    intent.putExtra(FEATURES_SCREENS, features)
    startActivityForResult(intent, EDIT_IMAGE_REQUEST)
}

fun Activity.startImageEditor(
    sourcePath: String?,
    destinationPath: String?,
    featuresScreens: (ImageEditorScreens.() -> Unit)? = null
) {
    val intent = Intent(
        this,
        EditImageActivity::class.java
    )
    val features = ImageEditorScreens()
    featuresScreens?.let {
        features.apply(it)
    }
    intent.putExtra(SOURCE_PATH, sourcePath)
    intent.putExtra(OUTPUT_PATH, destinationPath)
    intent.putExtra(FEATURES_SCREENS, features)
    startActivityForResult(intent, EDIT_IMAGE_REQUEST)
}

fun onEditImageResult(
    requestCode: Int,
    resultCode: Int,
    data: Intent?,
    block: OnImageEdited.() -> Unit
) {
    if (data != null && resultCode == Activity.RESULT_OK && requestCode == EDIT_IMAGE_REQUEST) {
        val isEdited = data.getBooleanExtra(IS_IMAGE_EDITED, false)

        val onImageEdited = OnImageEdited()
        onImageEdited.apply(block)
        if (!isEdited) {
            onImageEdited.onNoChanges?.invoke("No Changes happened to the image")
            return
        }

        data.getStringExtra(OUTPUT_PATH)?.let {
            onImageEdited.onSuccessfulEdit?.invoke(it)
        } ?: run {
            onImageEdited.onNoChanges?.invoke("Image url is Null , Error Happened..")
        }
    }
}

data class OnImageEdited(
    var onSuccessfulEdit: ((String) -> Unit)? = null,
    var onNoChanges: ((message: String) -> Unit)? = null
)