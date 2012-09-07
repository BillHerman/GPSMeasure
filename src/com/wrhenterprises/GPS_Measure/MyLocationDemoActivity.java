package com.wrhenterprises.GPS_Measure;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;

import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MyLocationDemoActivity extends MapActivity {
    
	// Local variables
    MapView mapView = null;
    MapController mapController = null;
    MyLocationOverlay whereAmI = null;
    Location lastLocation = null;
    private Timer myTimer;
    InterestingLocations funPlaces = null;
    
    @Override
    protected boolean isLocationDisplayed() 
    	{
    	return whereAmI.isMyLocationEnabled();
    	}

    @Override
    protected boolean isRouteDisplayed() 
    	{
        return false;
    	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Link to the map layout
        mapView = (MapView)findViewById(R.id.geoMap);
        
        // Add zoom function to the map 
        mapView.setBuiltInZoomControls(true);
                
        // Initialize the map controller 
        mapController = mapView.getController();
        mapController.setZoom(17);

        // Create new overlay by call to MyCustomLocationOverlay module
        whereAmI = new MyCustomLocationOverlay(this, mapView);
        
        // Add the "whereAmI" overlay to the existing empty list of overlays
		mapView.getOverlays().add(whereAmI);
		
		// Save critical info if user tilts screen 
		LocationInfo saveInfo = (LocationInfo) getLastNonConfigurationInstance();
		
		
        if (saveInfo != null) 
        	{
        	lastLocation = saveInfo.mLocation; 
        	funPlaces = saveInfo.mPlaces;
        	if (funPlaces != null) mapView.getOverlays().add(funPlaces);
            }
        
    	
		// Tell the system to re-draw the map
		mapView.postInvalidate();
		

      
		// Set time to compare last and current locations for distance value
		myTimer = new Timer();
		myTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				TimerMethod();
			}

		}, 0, 1000);
		

  	
    } 

	private void TimerMethod()
	{
		this.runOnUiThread(Timer_Tick); 
	}

	private Runnable Timer_Tick = new Runnable() {
		public void run() 
		{
			
		// Get current location from overlay value
		Location currentLocation = whereAmI.getLastFix();
		if (currentLocation != null)
			{
			Log.v("log", "last location = " + Double.toString(currentLocation.getLatitude()));
			}
		else
			{
			Log.v("log", "last location = null");
			}
		
		// If both last and current location are not null
		if ((lastLocation != null) && (currentLocation != null))
			{
		
			
			// calculate the distance
			Float distance = currentLocation.distanceTo(lastLocation);
			
			
	        // set the distance on the screen
	        TextView DistanceValue = (TextView)findViewById(R.id.DistanceValue);
	        DistanceValue.setText(Integer.toString(Math.round(distance)));
			
			}
			
		}
	};

    @Override
    public Object onRetainNonConfigurationInstance() 
    	{    
    	// combine last location info into object and save
    	LocationInfo saveInfo = new LocationInfo(lastLocation, funPlaces);
   		return saveInfo;
    	}

    class LocationInfo 
	{
    Location mLocation;
    InterestingLocations mPlaces;
    
    public LocationInfo(Location loc, InterestingLocations places)
    	{
        mLocation = loc;
        mPlaces = places;
        }
	}
    
    public void startButtonAdd(View v)
    	{
    	// Get current location 
    	GeoPoint testPoint = whereAmI.getMyLocation();
    	
    	// Save current location as last location
    	lastLocation = whereAmI.getLastFix();
    	
    	// Display the start icon on the screen
        displayButton(testPoint);
    	}
    
    public void displayButton(GeoPoint testPoint)
    	{
    	// Create a marker from the resource file and position it correctly
        Drawable marker=getResources().getDrawable(R.drawable.marker); 
        marker.setBounds((int)(-marker.getIntrinsicWidth()/2),
        		-marker.getIntrinsicHeight(),
        		(int) (marker.getIntrinsicWidth()/2), 
        		0);
    	
    	// Remove old marker 
        if (funPlaces != null) 
    		{
    		mapView.getOverlays().remove(funPlaces);
    		}
        
        // Add the new marker to the overlay
        funPlaces = new InterestingLocations(marker, testPoint);
        mapView.getOverlays().add(funPlaces);
        
    	}
    
    class InterestingLocations extends ItemizedOverlay 
    	{
        private ArrayList<OverlayItem> locations = new ArrayList<OverlayItem>();
  
        // Create new array entry of one item and add it to the list
        public InterestingLocations(Drawable marker, GeoPoint GP)
        	{
            super(marker);
            locations.add(new OverlayItem(GP, "Start", "Start"));
            populate();
        	}
    
        @Override
        protected OverlayItem createItem(int i) 
        	{
        	return locations.get(i);
        	}
        
        @Override
        public int size() 
        	{
        	return locations.size();
        	}
    	}
    
    
    @Override
    public void onResume()
    {
    	super.onResume();
    	
    	// Only enable a location when the activity is active - save battery life
    	whereAmI.enableMyLocation();
    	
    	// Create a thread to center the map around the current location
		whereAmI.runOnFirstFix(new Runnable() {
            public void run() {
                mapController.setCenter(whereAmI.getMyLocation());
            }
        });

      
        }

    @Override
    public void onPause()
    {
    	super.onPause();
    	
    	// Disable the location when the activity is paused or stopped - save battery life
        whereAmI.disableMyLocation();

     	
    }
}