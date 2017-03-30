package com.phucphuong.noisesearch.Activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.phucphuong.noisesearch.Fragments.MeterFragment;
import com.phucphuong.noisesearch.R;
import com.phucphuong.noisesearch.Utilities.AsyncTaskMap;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class MeasureAtMultiPoints extends AppCompatActivity {

    MeterFragment meterFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure_at_multi_points);

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

    @Override
    public void onBackPressed() {
        if (getVisibleFragment()!=null) {
            meterFragment = getVisibleFragment();
            meterFragment.handleBackPress();
        }
    }

    public MeterFragment getVisibleFragment(){
        FragmentManager fragmentManager = MeasureAtMultiPoints.this.getSupportFragmentManager();
        return (MeterFragment) fragmentManager.findFragmentById(R.id.meterFragment);
    }
}
