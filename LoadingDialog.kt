package com.example.museodelokal.uitel

import android.app.Activity
import android.app.AlertDialog
import com.example.museodelokal.R


class LoadingDialog(val mActivity:Activity) {
    private lateinit var isdialog:AlertDialog
    fun startLoading(){
        val infalter = mActivity.layoutInflater
        val dialogView = infalter.inflate(R.layout.loading,null)
        val bulider = AlertDialog.Builder(mActivity)
        bulider.setView(dialogView)
        bulider.setCancelable(false)
        isdialog = bulider.create()
        isdialog.show()
    }
    fun isDismiss(){
        isdialog.dismiss()
    }
}