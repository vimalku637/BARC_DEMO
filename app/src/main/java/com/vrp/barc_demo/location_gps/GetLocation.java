/*
 * Copyright (c)  2020. Indev Consultancy Private Limited,
 * Auther : Vimal Kumar
 * Date : 2020/12/15
 * Class :
 * Modified Date :
 * Modifications :
 * Modified By :
 */

package com.vrp.barc_demo.location_gps;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender.SendIntentException;
import android.location.Location;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.Builder;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;


/**
 * Created by tetra on 7/4/2016.
 * this is Location class in this class all the Setting for Gps Is Avaiable.
 */

public class GetLocation implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener, ResultCallback<LocationSettingsResult> {
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
    protected static final String KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string";
    protected static final String KEY_LOCATION = "location";
    protected static final String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    public static final int PERMISSION_REQUEST_CODE = 1;
    public static final int REQUEST_CHECK_SETTINGS = 1;
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private String TAG = "Location";
    private Context mContext;
    protected Location mCurrentLocation;
    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;
    protected LocationSettingsRequest mLocationSettingsRequest;
    protected Boolean mRequestingLocationUpdates;

    class C05811 implements ResultCallback<Status> {
        C05811() {
        }

        public void onResult(Status status) {
            GetLocation.this.mRequestingLocationUpdates = Boolean.valueOf(true);
        }
    }

    class C05822 implements ResultCallback<Status> {
        C05822() {
        }

        public void onResult(Status status) {
            GetLocation.this.mRequestingLocationUpdates = Boolean.valueOf(false);
        }
    }

    public GetLocation(Context context) {
        this.mContext = context;
        buildGoogleApiClient(context);
        createLocationRequest(context);
        buildLocationSettingsRequest(context);
    }

    protected synchronized void buildGoogleApiClient(Context context) {
        Log.i(this.TAG, "Building GoogleApiClient");
        this.mGoogleApiClient = new Builder(context).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        this.mGoogleApiClient.connect();
    }

    protected void createLocationRequest(Context context) {
        this.mLocationRequest = new LocationRequest();
        this.mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        this.mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        this.mLocationRequest.setPriority(100);
    }

    protected void buildLocationSettingsRequest(Context context) {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(this.mLocationRequest);
        this.mLocationSettingsRequest = builder.build();
    }

    public void startLocationUpdates(Context context) {
        if (ContextCompat.checkSelfPermission(context, "android.permission.ACCESS_FINE_LOCATION") == 0) {
            LocationServices.FusedLocationApi.requestLocationUpdates(this.mGoogleApiClient, this.mLocationRequest, (LocationListener) this).setResultCallback(new C05811());
        }
    }

    public void checkLocationPermission() {
        if (VERSION.SDK_INT < 23) {
            checkLocationSettings();
        } else if (ContextCompat.checkSelfPermission(this.mContext, "android.permission.ACCESS_FINE_LOCATION") != 0) {
            ((Activity) this.mContext).requestPermissions(new String[]{"android.permission.ACCESS_FINE_LOCATION"}, 1);
        } else {
            checkLocationSettings();
        }
    }
    public void checkStoragePermission() {
        if (VERSION.SDK_INT < 23) {
           // checkLocationSettings();
        } else if (ContextCompat.checkSelfPermission(this.mContext, "android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
            ((Activity) this.mContext).requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 1);
        }
    }

    public void checkLocationSettings() {
        LocationServices.SettingsApi.checkLocationSettings(this.mGoogleApiClient, this.mLocationSettingsRequest).setResultCallback(this);
    }

    public void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(this.mGoogleApiClient, (LocationListener) this).setResultCallback(new C05822());
    }
    public GoogleApiClient getGoogleApiClient() {
        return this.mGoogleApiClient;
    }

    public void disConnectGoogleClient() {
        this.mGoogleApiClient.disconnect();
    }

    public void onConnected(@Nullable Bundle bundle) {
        Log.e("I am Connected", "I am Connected");
    }

    public void onConnectionSuspended(int i) {
    }

    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case 0:
                Log.i(this.TAG, "All location settings are satisfied.");
               /* ((Map) this.mContext).onServiceStart();*/
                return;
            case 6:
                Log.i(this.TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");
                try {
                    status.startResolutionForResult((Activity) this.mContext, 1);
                    return;
                } catch (SendIntentException e) {
                    Log.i(this.TAG, "PendingIntent unable to execute request.");
                    return;
                }
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE /*8502*/:
                Log.i(this.TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                return;
            default:
                return;
        }
    }

    public void onLocationChanged(Location location) {
    }
}
