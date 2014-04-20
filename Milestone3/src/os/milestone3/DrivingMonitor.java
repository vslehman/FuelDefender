package os.milestone3;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
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
	private int GPS_UPDATE_MIN_TIME_SECONDS = 30;
	private double DRIVING_SPEED_THRESHOLD = 4;	// In meters per second
	
	private Location lastDrivingLocation;
	private double DRIVING_COOLDOWN_SECONDS = 300;
	
	private static boolean isDriving = false;
	private static boolean onTrip = false;
	
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
    	locationManager.requestLocationUpdates(locationProvider, GPS_UPDATE_MIN_TIME_SECONDS*1000, 0, this);
        System.out.println("Registering for updates");
    	// Show notification
    	Toast.makeText(this, "Starting Driving Monitor", Toast.LENGTH_SHORT).show();
    	
    	// Allow the service to run uninterrupted in the background
		return START_STICKY;
	}
    
    /**========================================================================
	 * private void startTrip()
	 * ------------------------------------------------------------------------
	 */
    private void startTrip(Location currentLocation) {
    	isDriving = true;
		startDrivingServices();
		trip.startTrip(currentLocation);
		onTrip = true;
    }
    
    /**========================================================================
	 * private void stopTrip()
	 * ------------------------------------------------------------------------
	 */
    private void stopTrip(Location destination) {
    	isDriving = false;
		stopDrivingServices();
		trip.stopTrip(destination, getApplicationContext());
		onTrip = false;
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
		long travelTimeInSeconds = (currentLocation.getTime() - lastLocation.getTime())/1000;
		
		// Average speed in meters per second
		float avgSpeed = distanceTraveled/travelTimeInSeconds;
		
		// The user is driving
		if (avgSpeed > DRIVING_SPEED_THRESHOLD) {

			lastDrivingLocation = currentLocation;
			
			// Is this the beginning of the trip?
			if (onTrip == false) {
				startTrip(currentLocation);
			}
			
		} // The user is not moving at driving speeds
		else {
			if (lastDrivingLocation != null) {
				long timeSinceLastDrivingUpdate = (currentLocation.getTime() - lastDrivingLocation.getTime())/1000;
				
				// If the user has not recorded a new driving location in the cooldown interval
				if (timeSinceLastDrivingUpdate > DRIVING_COOLDOWN_SECONDS && isDriving == true) {
					// The user has stopped driving
					stopTrip(lastDrivingLocation);
				}
			}
		}
		
		lastLocation = currentLocation;
		
		printDebug(distanceTraveled, travelTimeInSeconds, avgSpeed);
	}
	
	/**========================================================================
	 * public static boolean userIsDriving()
	 * ------------------------------------------------------------------------
	 */
	public static boolean userIsDriving() {
		return isDriving;
	}
	
	/**========================================================================
	 * public static boolean userIsDriving()
	 * ------------------------------------------------------------------------
	 */
	private void printDebug(float distanceTraveled, long travelTimeInSeconds, float avgSpeed) {
		System.out.println("=============================");
		System.out.println("GPS update\n-----------------");
		System.out.println("Distance traveled: " + distanceTraveled + "m");
		System.out.println("Travel time: " + travelTimeInSeconds + "s");
		System.out.println("Average speed: " + avgSpeed + "m/s");
		System.out.println("Is driving: " + isDriving);
		System.out.println("=============================");
	}
	
	/**========================================================================
	 * private void startDrivingServices()
	 * ------------------------------------------------------------------------
	 */
	private void startDrivingServices() {
		// Start acceleration alarm
		startService(new Intent(this, AccelerationAlarm.class));
	}
	
	/**========================================================================
	 * private void stopDrivingServices()
	 * ------------------------------------------------------------------------
	 */
	private void stopDrivingServices() {
		// Start acceleration alarm
		stopService(new Intent(this, AccelerationAlarm.class));
	}
	
	/**========================================================================
	 * public void onProviderDisabled()
	 * ------------------------------------------------------------------------
	 */
	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	
	/**========================================================================
	 * public void onProviderEnabled()
	 * ------------------------------------------------------------------------
	 */
	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	
	/**========================================================================
	 * public void onStatusChanged()
	 * ------------------------------------------------------------------------
	 */
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	
	/**========================================================================
	 * public IBinder onBind()
	 * ------------------------------------------------------------------------
	 */
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
}