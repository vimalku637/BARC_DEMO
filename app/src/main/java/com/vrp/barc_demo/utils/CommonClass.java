package com.vrp.barc_demo.utils;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

public class CommonClass {
    public static void setPopupForStopSurvey(Context context) {
        new AlertDialog.Builder(context).setTitle("Alert!")
                .setMessage("Are you sure to want stop survey.")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //TODO here
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //TODO here
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert).show();
    }
}
