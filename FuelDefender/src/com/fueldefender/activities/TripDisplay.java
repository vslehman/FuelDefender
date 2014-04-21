package com.fueldefender.activities;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

import java.text.NumberFormat;

import com.fueldefender.R;
import com.fueldefender.controllers.TripDao;
import com.fueldefender.models.GoogleTripTime;
import com.fueldefender.models.Trip;
import com.fueldefender.services.BatteryMonitor;
import com.fueldefender.services.DrivingMonitor;


public class TripDisplay extends Activity {
	
	private double gasPricePerGallon = 3.32;
	private final double AVERAGE_MPG = 24.8;
	private final double METERS_TO_MILES_FACTOR = 0.000621371;
	private Timer drivingCheckTimer;
	
	/**========================================================================
	 * public void onCreate()
	 * ------------------------------------------------------------------------
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trip_display);
		
		TripDao dao = new TripDao(this);
		
		// Start monitoring the user's driving
		startService(new Intent(this, DrivingMonitor.class));
		
		// Start monitoring the battery level
		startService(new Intent(this, BatteryMonitor.class));
		
		// Display trips from the database
		ArrayList<Trip> tripList = dao.getTrips();
		
		for (Trip trip : tripList) {
			addTripButton(trip);
		}
		
		dao = null;
		
		// Schedule driving check
		drivingCheckTimer = new Timer();
		drivingCheckTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				checkDrivingStatus();
			}
		}, 0, 30000);
	}
	
	/**========================================================================
	 * public void addTripButton()
	 * ------------------------------------------------------------------------
	 */
	private void addTripButton(Trip trip) {
		GoogleTripTime googleTrip = new GoogleTripTime(trip.getOrigin(), trip.getDestination());
		
		// Get distance in meters, convert to miles, divide by mpg to get total gallons needed
		double gallonsNeeded = (googleTrip.getDriveDistance()*METERS_TO_MILES_FACTOR)/AVERAGE_MPG;
		double gasCost = trip.getTimesTraveled()*gallonsNeeded*gasPricePerGallon;
		
		// Format to currency string
		NumberFormat fmt = NumberFormat.getCurrencyInstance();
		String gasCostText = fmt.format(gasCost);
		
		String buttonText = "Trip\n" + 
		                    "Drive time: " + googleTrip.getDriveTimeText() + "\n" + 
		                    "Walk time: " + googleTrip.getWalkTimeText() + "\n" +
		                    "Times traveled: " + trip.getTimesTraveled() + "\n" +
		                    "Gas cost: " + gasCostText;
		
		Button button = new Button(this);
		button.setText(buttonText);
		button.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		
		// Store the trip in the button object so the data can be used in the callback
		button.setTag(trip);
		
		button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent intent = new Intent(getApplicationContext(), TripCalculator.class);
            	
            	Trip trip = (Trip)v.getTag();
            	// Pass the trip to the map display
            	intent.putExtra("id", trip.getId());
            	intent.putExtra("origin_latitude", trip.getOrigin().getLatitude());
            	intent.putExtra("origin_longitude", trip.getOrigin().getLongitude());
            	intent.putExtra("destination_latitude", trip.getDestination().getLatitude());
            	intent.putExtra("destination_longitude", trip.getDestination().getLongitude());
        		startActivity(intent);
            }
        });
		
		
		ViewGroup layout = (ViewGroup)findViewById(R.id.button_layout);
		layout.addView(button);
	}
	
	/**========================================================================
	 * public void exportDatabase()
	 * ------------------------------------------------------------------------
	 */
	public void exportDatabase(View view) {
		TripDao dao = new TripDao(this);
		dao.exportToFile();
	}
	
	/**========================================================================
	 * public void checkDrivingStatus()
	 * ------------------------------------------------------------------------
	 */
	private void checkDrivingStatus() {
		runOnUiThread(new Runnable() {
	        public void run() {
	        	TextView status = (TextView)findViewById(R.id.driving_status);
	        	if (DrivingMonitor.userIsDriving()) {
	    			status.setText("Driving");
	    			status.setTextColor(Color.GREEN);
	    		}
	    		else {
	    			status.setText("Not Driving");
	    			status.setTextColor(Color.RED);
	    		}
	        } 
	    });
	}
}
