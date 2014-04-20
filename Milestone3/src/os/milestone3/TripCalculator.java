package os.milestone3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import android.location.Location;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/******************************************************************************
* public class TripCalulator
*------------------------------------------------------------------------------
*/
public class TripCalculator extends FragmentActivity {
	
	private GoogleMap map;
	
	/**========================================================================
	 * public void onCreate()
	 * ------------------------------------------------------------------------
	 */
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Get map handle
		map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		
		// Recover trip from passed data
		Bundle extras = getIntent().getExtras();
		
		// If no trip data was passed, there's no reason to run the activity
		if (extras == null) {
			finish();
			return;
		}
		
		int id = extras.getInt("id");
		double originLatitude = extras.getDouble("origin_latitude");
		double originLongitude = extras.getDouble("origin_longitude");
		double destinationLatitude = extras.getDouble("destination_latitude");
		double destinationLongitude = extras.getDouble("destination_longitude");
		
		Location origin = new Location("Database");
		origin.setLatitude(originLatitude);
		origin.setLongitude(originLongitude);
		
		Location destination = new Location("Database");
		destination.setLatitude(destinationLatitude);
		destination.setLongitude(destinationLongitude);
		
		Trip trip = new Trip(origin, destination);
		
		// Add markers
		map.addMarker(new MarkerOptions().position(new LatLng(trip.getOrigin().getLatitude(), trip.getOrigin().getLongitude()))
					                     .title("Origin")
					                     .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
			
		map.addMarker(new MarkerOptions().position(new LatLng(trip.getDestination().getLatitude(), trip.getDestination().getLongitude()))
					                     .title("Destination")
					                     .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
		
		// Is there a trip to snap to
		if (trip != null) {
			// Move camera to first trip's origin
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(trip.getOrigin().getLatitude(), trip.getOrigin().getLongitude()), 15));
			
			// Get travel time for first trip
			getTripDistance(trip);
		}
		
		final Button button = (Button) findViewById(R.id.return_to_main);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	finish();
            }
        });
	}
	
	/**========================================================================
	 * public boolean onCreateOptionsMenu()
	 * ------------------------------------------------------------------------
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/**========================================================================
	 * public void switchToRecordTrip()
	 * ------------------------------------------------------------------------
	 */
	public void switchToRecordTrip() {
		Intent myIntent = new Intent(this, TripMonitor.class);
		startActivity(myIntent);
	}
	
	/**========================================================================
	 * public void getTripDistance()
	 * ------------------------------------------------------------------------
	 */
	public void getTripDistance(Trip trip) {
		if (trip == null) {
			return;
		}
		
		GoogleTripTime tripTime = new GoogleTripTime(trip.getOrigin(), trip.getDestination());
		
		TextView driveView = (TextView)findViewById(R.id.driveTimeValue);
		driveView.setText(tripTime.getDriveTimeText());
		
		TextView walkView = (TextView)findViewById(R.id.walkTimeValue);
		walkView.setText(tripTime.getWalkTimeText());
	}
	
	/**========================================================================
	 * private ArrayList<Trip> loadTrips()
	 * ------------------------------------------------------------------------
	 */
	private ArrayList<Trip> loadTrips() {

		ArrayList<Trip> trips = new ArrayList<Trip>();

		try {
			BufferedReader in = new BufferedReader(new FileReader(TripMonitor.getTripLog()));
			String line = in.readLine();
			while (line  != null) {

				String[] tokens = line.split(" ");

				Location origin = new Location("File");
				origin.setLatitude(Double.parseDouble(tokens[0]));
				origin.setLongitude(Double.parseDouble(tokens[1]));

				Location destination = new Location("File");
				destination.setLatitude(Double.parseDouble(tokens[2]));
				destination.setLongitude(Double.parseDouble(tokens[3]));

				//trips.add(new Trip(origin, destination));
				
				line = in.readLine();
			}
			in.close();
		}
		catch (IOException e){

		}

		return trips;
	}
}
