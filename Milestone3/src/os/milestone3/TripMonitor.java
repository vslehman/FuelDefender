package os.milestone3;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/******************************************************************************
* public class TripMonitor
*------------------------------------------------------------------------------
*/
public class TripMonitor extends Activity {
	
	private LocationManager locationManager;
	private String locationProvider = LocationManager.GPS_PROVIDER;
	private Trip tempTrip;
	
	private static final String logDirectory = Environment.getExternalStorageDirectory().getPath() + "/fuel_defender/";
	private static final String tripLog = logDirectory + "trip_log.txt";
	
	/**========================================================================
	 * public void onCreate()
	 * ------------------------------------------------------------------------
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record_trip);
		
		locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
		
		// If GPS is not available, use Wifi
		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			locationProvider = LocationManager.NETWORK_PROVIDER;
		}
		
		final Button button = (Button)findViewById(R.id.record_trip);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	startTrip();
            }
        });
	}
	
	/**========================================================================
	 * public void switchToTripCalculator()
	 * ------------------------------------------------------------------------
	 */
	public void switchToTripCalculator(View view) {
		Intent myIntent = new Intent(this, TripCalculator.class);
		startActivity(myIntent);
	}
	
	/**========================================================================
	 * public String getTripLog()
	 * ------------------------------------------------------------------------
	 */
	public static String getTripLog() {
		return tripLog;
	}
	
	/**========================================================================
	 * public void startTrip()
	 * ------------------------------------------------------------------------
	 */
	public void startTrip() {
		
		// Update button label
		Button tripButton = (Button)findViewById(R.id.record_trip);
		tripButton.setText("Stop trip");
		
		// Clear location values
		TextView originValue = (TextView)findViewById(R.id.origin_value);
		originValue.setText("");
		
		TextView destinationValue = (TextView)findViewById(R.id.destination_value);
		destinationValue.setText("");

		// Register for updates
		locationManager.requestLocationUpdates(locationProvider, 0, 0, new StartTripListener());

		// Change button's functionality
        tripButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	stopTrip();
            }
        });
	}
	
	/**========================================================================
	 * public void stopTrip()
	 * ------------------------------------------------------------------------
	 */
	public void stopTrip() {
		// Update button label
		Button tripButton = (Button)findViewById(R.id.record_trip);
		tripButton.setText("Start trip");
		
		// Register for updates
		locationManager.requestLocationUpdates(locationProvider, 0, 0, new StopTripListener());
			    
		// Change button's functionality
        tripButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	startTrip();
            }
        });

	}
	
	/**========================================================================
	 * private void logTrip()
	 * ------------------------------------------------------------------------
	 */
	private void logTrip(Trip trip, String filename) {
		// Create directory, if needed
		File appDirectory = new File(logDirectory);
		appDirectory.mkdirs();

		File logFile = new File(filename);
		
		if (!logFile.exists())
		{
			try
			{
				logFile.createNewFile();
			} 
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try
		{
			//BufferedWriter for performance, true to set append to file flag
			BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
			
			buf.append(String.format("%f %f %f %f %d", trip.getOrigin().getLatitude(), trip.getOrigin().getLongitude(),
					                                   trip.getDestination().getLatitude(), trip.getDestination().getLongitude(),
					                                   trip.getElapsedTime()));
			buf.newLine();
			buf.close();
			buf = null;
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/******************************************************************************
	* public class StartTripListener
	*------------------------------------------------------------------------------
	*/
	private class StartTripListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			// Record location value
			tempTrip = new Trip(null, null);
			tempTrip.setOrigin(location);
			
			// Update text values
    		TextView originValue = (TextView)findViewById(R.id.origin_value);
    		originValue.setText(location.getLongitude() + " " + location.getLatitude());
    		
    		// Unregister self
    		locationManager.removeUpdates(this);
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub	
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
		}
	}
	
	/******************************************************************************
	* public class StopTripListener
	*------------------------------------------------------------------------------
	*/
	private class StopTripListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			// Record location value
			tempTrip.setDestination(location);

			// Update text values
    		TextView destinationValue = (TextView)findViewById(R.id.destination_value);
    		destinationValue.setText(location.getLongitude() + " " + location.getLatitude());

    		// Log trip
    		logTrip(tempTrip, tripLog);
 
    		// Unregister self
    		locationManager.removeUpdates(this);
		}

		@Override
		public void onProviderDisabled(String provider) {
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			
		}
	}
}
