package os.milestone3;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;

import java.text.NumberFormat;

public class TripDisplay extends Activity {
	
	private double gasPricePerGallon = 3.32;
	private final double AVERAGE_MPG = 24.8;
	private final double METERS_TO_MILES_FACTOR = 0.000621371;
	
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
		
		// Add trips for test data
		Location origin = new Location("Test");
		origin.setLatitude(35.1252276);
		origin.setLongitude(-89.937345);
		
		Location destination = new Location("Test");
		destination.setLatitude(35.1149945);
		destination.setLongitude(-89.9388618);
		
		dao.addTrip(new Trip(origin, destination));
		
		Location lot = new Location("Test");
		lot.setLatitude(35.1152366);
		lot.setLongitude(-89.9390134);
		
		Location dest2 = new Location("Test");
		dest2.setLatitude(35.1076483);
		dest2.setLongitude(-89.9289166);
		
		dao.addTrip(new Trip(lot, dest2));
		
		Location origin3 = new Location("Test");
		origin3.setLatitude(35.1252887);
		origin3.setLongitude(-89.9370801);
		
		Location dest3 = new Location("Test");
		dest3.setLatitude(35.0836777);
		dest3.setLongitude(-89.7295976);
		
		dao.addTrip(new Trip(origin3, dest3));
		
		// Display trips from the database
		ArrayList<Trip> tripList = dao.getTrips();
		
		for (Trip trip : tripList) {
			addTripButton(trip);
		}
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
}
