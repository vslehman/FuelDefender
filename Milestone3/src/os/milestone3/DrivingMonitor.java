package os.milestone3;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/******************************************************************************
* public class TripRecorder
*------------------------------------------------------------------------------
*/
public class DrivingMonitor extends Service implements LocationListener {
	
	private LocationManager locationManager;
	private String locationProvider = LocationManager.GPS_PROVIDER;
	
	private Location lastLocation;
	
	private int GPS_UPDATE_MIN_DISTANCE_METERS = 100;
	private double DRIVING_SPEED_THRESHOLD = 8;	// In meters per second
	
	private Location lastDrivingLocation;
	private double DRIVING_COOLDOWN_SECONDS = 300;
	
	private static boolean isDriving = false;
	
	private TripRecorder trip = new TripRecorder();
	
	/**========================================================================
	 * public void onCreate()
	 * ------------------------------------------------------------------------
	 */
    @SuppressLint("InlinedApi")
	@Override
    public void onCreate() {	
    	
    	locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
		
		// If GPS is not available, use Wifi
		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			locationProvider = LocationManager.NETWORK_PROVIDER;
		}
    }
    
    /**========================================================================
	 * public int onStartCommand()
	 * ------------------------------------------------------------------------
	 */
    @SuppressLint("InlinedApi")
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
    	
    	// Register for updates
    	locationManager.requestLocationUpdates(locationProvider, 0, GPS_UPDATE_MIN_DISTANCE_METERS, this);
        
    	// Show notification
    	Toast.makeText(this, "Starting Driving Monitor", Toast.LENGTH_SHORT).show();
    	
    	// Allow the service to run uninterrupted in the background
		return START_STICKY;
	}
    
    /**========================================================================
	 * public void startTrip()
	 * ------------------------------------------------------------------------
	 */
	private void startTrip() {				
		
	}

	/**========================================================================
	 * public void onLocationChanged()
	 * ------------------------------------------------------------------------
	 */
	@Override
	public void onLocationChanged(Location currentLocation) {
		
		// This is the first GPS update
		if (lastLocation == null) {
			lastLocation = currentLocation;
		}
		
		float distanceTraveled = lastLocation.distanceTo(currentLocation);
		long travelTimeInSeconds = (currentLocation.getTime() - lastLocation.getTime())*1000;
		
		// Average speed in meters per second
		float avgSpeed = distanceTraveled/travelTimeInSeconds;
		
		// The user is driving
		if (avgSpeed > DRIVING_SPEED_THRESHOLD) {
			isDriving = true;
			lastDrivingLocation = currentLocation;
			startDrivingServices();
			trip.startTrip(lastLocation);
		} // The user is not moving at driving speeds
		else {
			if (lastDrivingLocation != null) {
				long timeSinceLastDrivingUpdate = (currentLocation.getTime() - lastDrivingLocation.getTime())*1000;
				
				// If the user has not recorded a new driving location in the cooldown interval
				if (timeSinceLastDrivingUpdate > DRIVING_COOLDOWN_SECONDS) {
					// The user has stopped driving
					isDriving = false;
					stopDrivingServices();
					trip.stopTrip(lastDrivingLocation, getApplicationContext());
				}
			}
		}
		
		lastLocation = currentLocation;
	}
	
	/**========================================================================
	 * public static boolean userIsDriving()
	 * ------------------------------------------------------------------------
	 */
	public static boolean userIsDriving() {
		return isDriving;
	}
	
	private void startDrivingServices() {
		// Start acceleration alarm
		startService(new Intent(this, AccelerationAlarm.class));
	}
	
	private void stopDrivingServices() {
		// Start acceleration alarm
		stopService(new Intent(this, AccelerationAlarm.class));
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

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
}