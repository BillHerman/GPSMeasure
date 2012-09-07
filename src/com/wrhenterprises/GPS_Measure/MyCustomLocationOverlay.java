package com.wrhenterprises.GPS_Measure;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class MyCustomLocationOverlay extends MyLocationOverlay {
    MapView mMapView = null;
    
	public MyCustomLocationOverlay(Context ctx, MapView mapView) {
		super(ctx, mapView);
		mMapView = mapView;
	}

	public void onLocationChanged(Location loc) {
		super.onLocationChanged(loc);
		
		// Create geo point based on passed location
		GeoPoint newPt = new GeoPoint((int) (loc.getLatitude()*1E6),
				(int) (loc.getLongitude()*1E6));
		
		// Log a message about the new location
		
		// Move the map to the new location
		mMapView.getController().animateTo(newPt);
	
	}
}
