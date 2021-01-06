package com.advengers.mabo.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.IntentSender;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.advengers.mabo.Model.User;
import com.advengers.mabo.R;
import com.advengers.mabo.Services.OnClearFromRecentService;
import com.advengers.mabo.Utils.LogUtils;
import com.advengers.mabo.Utils.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import static com.advengers.mabo.Interfaces.Keys.USER;

public class SplashActivity extends AppCompatActivity{//} implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    Gson gson;
    User user;
    LocationManager locationManager;
    RelativeLayout lyt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        lyt = (RelativeLayout)findViewById(R.id.baselayout);
        gson = new Gson();
        user = new User();
        User.setUser(gson.fromJson(Utils.getInstance(SplashActivity.this).getString(USER),User.class));
        startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
        if(User.getUser().isLogged())
        {
            startActivity(new Intent(SplashActivity.this, DashboardActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }else
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                    Intent i = new Intent(SplashActivity.this, IntroscreenActivity.class);
                    startActivity(i);
                    finish();

            }
        }, 3000);
       /* locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {

        }else{
            EnableGPSAutoMatically();
        }*/

    }
 /*   private void EnableGPSAutoMatically() {
        GoogleApiClient googleApiClient = null;
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            googleApiClient.connect();
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            // **************************
            builder.setAlwaysShow(true); // this is the key ingredient
            // **************************

            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                    .checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result
                            .getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                        //    toast("Success");
                            // All location settings are satisfied. The client can
                            // initialize location
                            // requests here.
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            toast("Enable GPS to get into the app");
                            // screen.Location settings are not satisfied. But could be
                            // fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling
                                // startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(SplashActivity.this, 1000);

                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            toast("Setting change not allowed");
                            // screen.Location settings are not satisfied. However, we have
                            // no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1000) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("result");
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if(User.getUser().isLogged())
                        {
                            startActivity(new Intent(SplashActivity.this, DashboardActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        }else
                        {
                            Intent i = new Intent(SplashActivity.this, IntroscreenActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }
                }, 2000);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
                EnableGPSAutoMatically();
            }
        }
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        toast("Suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        toast("Failed");
    }
    private void toast(String message) {
        try {
          //  Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            Snackbar.make(lyt, message, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } catch (Exception ex) {
            LogUtils.e("Window has been closed");
        }
    }
*/
}
