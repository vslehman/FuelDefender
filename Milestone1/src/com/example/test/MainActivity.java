package com.example.test;

import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {
	
	private SensorManager mSensorManager;
	private Sensor mSensor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Set up accelerometer
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
	    mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	    mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
	    
	    // Set up GPS

	    LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

	    // Define a listener that responds to location updates
	    LocationListener locationListener = new LocationListener() {
	    	
	    	public void onLocationChanged(Location location) {
	    		// Find the label and update with gps data
	   		 	TextView latitude = (TextView)findViewById(R.id.lat_data);
	   		 	latitude.setText(String.format("%.3f", location.getLatitude()));
	   		 	
	   		 	TextView longitude = (TextView)findViewById(R.id.long_data);
	   		 	longitude.setText(String.format("%.3f", location.getLongitude()));
	    	}

	        public void onStatusChanged(String provider, int status, Bundle extras) {}

	        public void onProviderEnabled(String provider) {}

	        public void onProviderDisabled(String provider) {}
	      };

	    // Register the listener with the Location Manager to receive location updates
	    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	 @Override
	 public final void onSensorChanged(SensorEvent event) {
		 
		 // Find the label and update with sensor data
		 TextView x = (TextView)findViewById(R.id.accel_x_data); 
		 x.setText(String.format("%.3f", event.values[0]));
		 
		 TextView y = (TextView)findViewById(R.id.accel_y_data); 
		 y.setText(String.format("%.3f", event.values[1]));
		 
		 TextView z = (TextView)findViewById(R.id.accel_z_data); 
		 z.setText(String.format("%.3f", event.values[2]));
		 
	}

	@Override
	public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

}
