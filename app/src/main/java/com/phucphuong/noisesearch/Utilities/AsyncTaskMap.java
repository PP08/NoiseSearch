package com.phucphuong.noisesearch.Utilities;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.phucphuong.noisesearch.R;

import org.osmdroid.api.IMapController;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

/**
 * Created by phucphuong on 3/20/17.
 */

public class AsyncTaskMap extends AsyncTask<Void, Void, double[]> {

    Activity activity;
    MapView map;
    GPSTracker TempGPS;
    IMapController iMapController;
    public Marker startMarker;

    public AsyncTaskMap(Activity activity, IMapController mapController, Marker startMarker){
        this.activity = activity;
        this.iMapController = mapController;
        this.startMarker = startMarker;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        map = (MapView)activity.findViewById(R.id.map);
        TempGPS = new GPSTracker(activity);

    }

    @Override
    protected double[] doInBackground(Void... params) {
        double latitude = TempGPS.getLatitude();
        double longitude = TempGPS.getLongitude();
        double[] test = new double[]{latitude, longitude};
        return test;
    }

    @Override
    protected void onPostExecute(double[] point) {
        super.onPostExecute(point);
        setStartPoint(point[0], point[1]);
        TempGPS.stopUsingGPS();

    }

    private void setStartPoint(double x, double y){
        iMapController.setZoom(40);
        GeoPoint startPoint = new GeoPoint(x, y);
        iMapController.animateTo(startPoint);
        startMarker.setTitle("Your current location");
        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
    }


}
