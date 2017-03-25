package com.phucphuong.noisesearch.Fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.phucphuong.noisesearch.R;
import com.phucphuong.noisesearch.Utilities.AsyncTaskMap;
import com.phucphuong.noisesearch.Utilities.GPSTracker;

import org.osmdroid.api.IMapController;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.OverlayManager;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.infowindow.BasicInfoWindow;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment {

    private MapView map;
    double longitude, latitude;

    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mapView = inflater.inflate(R.layout.fragment_map, container, true);

        return mapView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        map = (MapView)view.findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);

        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        //params for thread
        IMapController iMapController = map.getController();
        Marker startMarker = new Marker(map);

        //        TODO: check this thread : done

        AsyncTaskMap asyncTaskMap = new AsyncTaskMap(getActivity(), iMapController, startMarker);
        asyncTaskMap.execute();

        map.getOverlays().add(asyncTaskMap.startMarker);
        map.invalidate();

    }

    private void checkStartPoint(){

        GPSTracker TempGPS = new GPSTracker(getActivity());
        latitude = TempGPS.getLatitude();
        longitude = TempGPS.getLongitude();
        setStartPoint(latitude,longitude);
        TempGPS.stopUsingGPS();
    }

    private void setStartPoint(double x, double y){

        IMapController mapController = map.getController();
        mapController.setZoom(40);
        GeoPoint startPoint = new GeoPoint(x, y);
        //mapController.setCenter(startPoint);
        map.getController().animateTo(startPoint);

        Marker startMarker = new Marker(map);
        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(startMarker);
        map.invalidate();

    }

    private void setTheMarker(GeoPoint geoPoint, String name){

        //set the marker
        Marker startMarker = new Marker(map);
        startMarker.setPosition(geoPoint);
        startMarker.setTitle(name);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(startMarker);

    }

    public void startGPSTracker(){
        map.getOverlays().clear();
        checkStartPoint();
        setTheMarker(new GeoPoint(latitude, longitude), "Start Point");

        //MapEventsOverlay mapEventsOverlay = new MapEventsOverlay((MapEventsReceiver) getActivity());
        //map.getOverlays().add(0, mapEventsOverlay); //inserted at the "bottom" of all overlays
    }


    //for the route
    private void drawCircle(GeoPoint geoPoint){
        Polygon circle = new Polygon(getActivity());
        circle.setPoints(Polygon.pointsAsCircle(geoPoint, 2));

        //adjust some design aspects
        circle.setFillColor(Color.BLUE);
        circle.setStrokeColor(Color.RED);
        circle.setStrokeWidth(2);

        //add to the map
        map.getOverlays().add(circle);

        circle.setInfoWindow(new BasicInfoWindow(org.osmdroid.bonuspack.R.layout.bonuspack_bubble, map));
        circle.setTitle("Centered on "+geoPoint.getLatitude()+","+geoPoint.getLongitude());
    }

    private void drawLine(GeoPoint point1, GeoPoint point2){

        Polyline line = new Polyline(getActivity());
        line.setWidth(4.0f);

        List<GeoPoint> pts = new ArrayList<>();
        pts.add(point1);
        pts.add(point2);

        line.setPoints(pts);
        line.setGeodesic(true);
        line.setColor(Color.RED);
        map.getOverlays().add(line);

    }

    private void drawTheRoute(GeoPoint point1, GeoPoint point2){

        drawCircle(point1);
        drawCircle(point2);
        drawLine(point1, point2);
        map.getController().animateTo(point2);
        map.invalidate();

    }

    public void drawOnFrament(double p1, double p2){
        GeoPoint geoPoint1 = new GeoPoint(latitude, longitude);
        GeoPoint geoPoint2 = new GeoPoint(p1, p2);
        drawTheRoute(geoPoint1, geoPoint2);

        latitude = p1;
        longitude = p2;
    }

    public void setEndMarker(){
        setTheMarker(new GeoPoint(latitude, longitude), "End Point");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
