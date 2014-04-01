package os.milestone3;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.MapFragment;

/******************************************************************************
* public class TripCalulator
*------------------------------------------------------------------------------
*/
public class TripCalculator extends FragmentActivity {
	
	private GoogleMap map;
	private Location lot;

	private Location gym;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Get map handle
		map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		
		// Move camera to position
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(35.125395, -89.937043), 15));
		
		// Create locations
		lot = new Location("Thin air");
		lot.setLatitude(35.125395);
		lot.setLongitude(-89.937043);

		gym = new Location("The keyboard");
		gym.setLatitude(35.114092);
		gym.setLongitude(-89.938277);
		
		// Add markers
		map.addMarker(new MarkerOptions().position(new LatLng(lot.getLatitude(), lot.getLongitude())).title("Central Parking Lot"));
		map.addMarker(new MarkerOptions().position(new LatLng(gym.getLatitude(), gym.getLongitude())).title("Memphis CRIS"));
		
		// Get travel time
		getTripDistance();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/**========================================================================
	 * public void getTripDistance()
	 * ------------------------------------------------------------------------
	 */
	public void getTripDistance() {
		GoogleTripTime tripTime = new GoogleTripTime(lot, gym);
		
		TextView driveView = (TextView)findViewById(R.id.driveTimeValue);
		driveView.setText(tripTime.getDriveTimeText());
		
		TextView walkView = (TextView)findViewById(R.id.walkTimeValue);
		walkView.setText(tripTime.getWalkTimeText());
	}
}
