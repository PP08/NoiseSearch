package com.phucphuong.noisesearch.Activities;

import android.app.ProgressDialog;
import android.hardware.camera2.params.InputConfiguration;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.phucphuong.noisesearch.Fragments.MapFragment;
import com.phucphuong.noisesearch.R;
import com.phucphuong.noisesearch.Utilities.AsyncTaskGPS;
import com.phucphuong.noisesearch.Utilities.AsyncTaskMap;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.IConfigurationProvider;
import org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class MeasureAtMultiPoints extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure_at_multi_points);

//        MapFragment mapFragment = (MapFragment)getSupportFragmentManager().findFragmentById(R.id.graphFragment);

        MapView map = (MapView)findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);



        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        //params for thread
        IMapController iMapController = map.getController();
        Marker startMarker = new Marker(map);

        AsyncTaskMap asyncTaskMap = new AsyncTaskMap(MeasureAtMultiPoints.this, iMapController, startMarker);
        asyncTaskMap.execute();
    }
}
