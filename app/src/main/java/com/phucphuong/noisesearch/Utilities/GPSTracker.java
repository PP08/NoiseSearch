package com.phucphuong.noisesearch.Utilities;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by phucphuong on 3/17/17.
 */

public class GPSTracker extends Service implements LocationListener {

    @Override
    public void onLocationChanged(Location location) {
        //Log.e("test", "test");
        if (isBetterLocation(location, lastLocation)){
            lastLocation = location;
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {
        showSettingsAlert();
    }

    private Context context;
    boolean checkGPS = false;
    boolean canGetLocation = false;

    Location lastLocation;
    double latitude, longitude;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;
    protected LocationManager locationManager;

    public GPSTracker(Context context){
        this.context = context;
        getLocation();
    }

    private Location getLocation(){
        try {
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

            //getting GPS status
            checkGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (!checkGPS){
                Toast.makeText(context, "No Service Provider Available", Toast.LENGTH_LONG).show();
            }else {
                this.canGetLocation = true;
//                Toast.makeText(context, "GPS", Toast.LENGTH_SHORT).show();

                try {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 3.5f, this);
                    Log.e("GPS Enabled", "GPS Enabled");
                    if (locationManager != null) {
                        lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (lastLocation != null) {
                            latitude = lastLocation.getLatitude();
                            longitude = lastLocation.getLongitude();
                        }
                    }
                } catch (SecurityException e) {

                }

            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return lastLocation;
    }

    public double getLongitude() {
        if (lastLocation != null) {
            longitude = lastLocation.getLongitude();
        }
        return longitude;
    }

    public double getLatitude() {
        if (lastLocation != null) {
            latitude = lastLocation.getLatitude();
        }
        return latitude;
    }
    public boolean canGetLocation(){
        return this.canGetLocation;
    }

    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);


        alertDialog.setTitle("GPS Not Enabled");

        alertDialog.setMessage("Do you wants to turn On GPS");


        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });


        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });


        alertDialog.show();
    }

    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(GPSTracker.this);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


//    private static final int TWO_MINUTES = 1000 * 60 * 2;
    private static final int THIRTY_SECONDS = 1000 * 30;

    /** Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new one
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > THIRTY_SECONDS;
        boolean isSignificantlyOlder = timeDelta < -THIRTY_SECONDS;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        }else if (isNewer && !isSignificantlyLessAccurate) {
            return true;
        }
        return false;
    }

}
