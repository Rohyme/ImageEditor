package iamutkarshtiwari.github.io.ananas

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context

import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {
    companion object {

        fun getLoadingDialog(
            context: Context, titleId: Int,
            canCancel: Boolean
        ): Dialog {
            return getLoadingDialog(context, context.getString(titleId), canCancel)
        }


        fun getLoadingDialog(
            context: Context, title: String,
            canCancel: Boolean
        ): Dialog {
            val dialog = ProgressDialog(context)
            dialog.setCancelable(canCancel)
            dialog.setMessage(title)
            return dialog
        }
    }
}
