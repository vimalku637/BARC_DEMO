package com.vrp.barc_demo.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;

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
    public static boolean isInternetOn(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        if (connectivityManager.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED
                || connectivityManager.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING
                || connectivityManager.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING
                || connectivityManager.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {

            return true;

        } else if (connectivityManager.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED
                || connectivityManager.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {
            return false;
        }
        return false;
    }
    public static void showPopupForNoInternet(Context context) {
        new AlertDialog.Builder(context).setTitle("Alert!")
                .setMessage("Network Error, Please check internet connection.")
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
