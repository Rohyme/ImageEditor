package com.rohyme.imageeditor

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.sembozdemir.permissionskt.askPermissions
import kotlinx.android.synthetic.main.activity_main.*
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import pl.aprilapps.easyphotopicker.MediaFile
import pl.aprilapps.easyphotopicker.MediaSource
import technivance.github.io.rohyme.editimage.onEditImageResult
import technivance.github.io.rohyme.editimage.startImageEditor
import java.io.File

class MainActivity : AppCompatActivity() {

    var imagePath: String? = null

    private val easyImage by lazy {
        EasyImage.Builder(this)
            .allowMultiple(false)
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pickImage.setOnClickListener {
            askPermissions(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) {
                onGranted {
                    pickPicture()
                }
            }
        }

        editImage.setOnClickListener {
            imagePath?.let {
                startImageEditor(it, getDestinationUrl(it)) {
                    withForcePortrait = true
                    withPaint = true
                    withCrop = true
                    withRotate = true
                }
            } ?: run {
                Toast.makeText(this, "Please pick image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun pickPicture() {
        easyImage.openChooser(this)
    }

    private fun getDestinationUrl(sourceUrl: String): String? {
        val destinationFileURl = String.format(
            "crop_%s_%s",
            System.currentTimeMillis(),
            sourceUrl.substring(sourceUrl.lastIndexOf("/") + 1)
        )
        return Uri.fromFile(File(sourceUrl.replaceAfterLast("/", destinationFileURl))).path
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        easyImage.handleActivityResult(
            requestCode,
            resultCode,
            data,
            this,
            object : DefaultCallback() {
                override fun onMediaFilesPicked(imageFiles: Array<MediaFile>, source: MediaSource) {
                    onPhotosReturned(imageFiles)
                }

                override fun onImagePickerError(@NonNull error: Throwable, @NonNull source: MediaSource) {
                    // Some error handling
                    error.printStackTrace()
                }

                override fun onCanceled(@NonNull source: MediaSource) {
                    // Not necessary to remove any files manually anymore
                }
            })
        onEditImageResult(requestCode, resultCode, data) {
            onSuccessfulEdit = {
                loadImage(it)
            }

            onNoChanges = {
                Toast.makeText(this@MainActivity, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onPhotosReturned(imageFiles: Array<MediaFile>) {
        imageFiles.firstOrNull()?.let {
            loadImage(it.file.absolutePath)
        }
    }

    fun loadImage(url: String) {
        imagePath = url
        Glide.with(this).load(url).into(imageViewer)
    }

    companion object {
        const val EDIT_IMAGE_REQUEST = 40233
    }
}
