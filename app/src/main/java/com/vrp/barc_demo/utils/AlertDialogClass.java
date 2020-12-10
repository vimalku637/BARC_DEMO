package com.vrp.barc_demo.utils;

import android.content.Context;
import android.graphics.Color;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AlertDialogClass {
    public static SweetAlertDialog pDialog;
    public static void showProgressDialog(Context context) {
        pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();
    }
    public static void dismissProgressDialog() {
        pDialog.dismiss();
    }
}
