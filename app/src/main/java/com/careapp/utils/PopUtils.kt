package com.careapp.utils

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import com.careapp.R

class PopUtils {
    companion object {

        fun progressDialog(context: Context): Dialog {
            val dialog = Dialog(context)
            val inflate = LayoutInflater.from(context).inflate(R.layout.layout_progress, null)
            dialog.setContentView(inflate)
            dialog.setCancelable(false)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            return dialog
        }

        @SuppressLint("InflateParams")
        fun SimpleProgressDialog(context: Context): Dialog {
            val dialog = Dialog(context)
            val inflate = LayoutInflater.from(context).inflate(R.layout.layout_progress_simple, null)
            dialog.setContentView(inflate)
            dialog.setCancelable(false)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            return dialog
        }

        fun deleteProductDialog(context: Context, message: String, onClickListener: DialogInterface.OnClickListener) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(context.getString(R.string.app_name))
            builder.setMessage(message)
            builder.setPositiveButton("Delete") { dialog, which ->
                dialog.dismiss()
                onClickListener.onClick(dialog, which)
            }
            builder.setNeutralButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            val dialog: AlertDialog = builder.create()
            dialog.setCanceledOnTouchOutside(false)
            dialog.setCancelable(true)
            dialog.show()
        }

        fun simpleDialog(context: Context, message: String, positiveButtonText: String, onClickListener: DialogInterface.OnClickListener) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(context.getString(R.string.app_name))
            builder.setMessage(message)
            builder.setPositiveButton(positiveButtonText) { dialog, which ->
                dialog.dismiss()
                onClickListener.onClick(dialog, which)
            }
            builder.setNeutralButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            val dialog: AlertDialog = builder.create()
            dialog.setCanceledOnTouchOutside(false)
            dialog.setCancelable(true)
            dialog.show()
        }

    }
}