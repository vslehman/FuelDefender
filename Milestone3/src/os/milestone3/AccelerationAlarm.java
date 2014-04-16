package os.milestone3;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.SystemClock;
import android.widget.Toast;

/******************************************************************************
* public class AccelerationAlarm
*------------------------------------------------------------------------------
*/
public class AccelerationAlarm extends Service implements SensorEventListener {
	
	private SensorManager mSensorManager;
    private Sensor mSensor;
    
    private MediaPlayer mediaPlayer;
    
    private boolean usingLinearAcceleration = true;
    private double[] gravity;
    private double alpha = 0.75;
    
    private final int X_INDEX = 0;
    private final int Y_INDEX = 1;
    private final int Z_INDEX = 2;
    
    private final double ACCELERATION_LIMIT = 12.0;
    
    private long lastAlarmTime;
    private final long ALARM_COOLDOWN_NANOSECONDS = 5000000000L;
	
    /**========================================================================
	 * public void onCreate()
	 * ------------------------------------------------------------------------
	 */
    @SuppressLint("InlinedApi")
	@Override
    public void onCreate() {	
    	// Initialize sound
        mediaPlayer = MediaPlayer.create(this, R.raw.acceleration_alarm);
        lastAlarmTime = System.nanoTime();
    }
    
    /**========================================================================
	 * public int onStartCommand()
	 * ------------------------------------------------------------------------
	 */
    @SuppressLint("InlinedApi")
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
    	// Set up sensor
        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
	    mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
	    
	    // Phone doesn't support Linear Accelerometer
	    if (mSensor == null) {
	    	mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	    	usingLinearAcceleration = false;
	    	
	    	// Will need to track gravity for weighted moving average
	    	if (gravity == null) {
        		gravity = new double[3];
        	}
	    }
        
        // Register the accelerometer
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        
        // Show notification
    	Toast.makeText(this, "Starting Acceleration Alarm", Toast.LENGTH_SHORT).show();
    	
    	// Allow the service to run uninterrupted in the background
		return START_STICKY;
	}
	
	/**========================================================================
	 * private void playAlarm()
	 * ------------------------------------------------------------------------
	 */
    private void playAlarm(){
    	mediaPlayer.seekTo(0);
    	mediaPlayer.start();
    }
    
    /**========================================================================
	 * public void onSensorChanged
	 * ------------------------------------------------------------------------
	 */
	@Override
	public void onSensorChanged(SensorEvent event) {
		
		// Has the user been alerted recently?
		if (lastAlarmTime + ALARM_COOLDOWN_NANOSECONDS > System.nanoTime()) {
			return;
		}
		
        // Get sensor data
        long timestamp = event.timestamp;
        double xAccel;
        double yAccel;
        double zAccel;
        
        // If using the linear accelerometer, no need to remove gravity
        if (usingLinearAcceleration) {
        	xAccel = event.values[X_INDEX];
            yAccel = event.values[Y_INDEX];
            zAccel = event.values[Z_INDEX];
        }
        else {
        	// Using the standard accelerometer, need to remove gravity
        	// Calculate low pass filter with weighted moving average 
        	gravity[X_INDEX] = alpha*gravity[X_INDEX] + (1 - alpha)*event.values[X_INDEX];
            gravity[Y_INDEX] = alpha*gravity[Y_INDEX] + (1 - alpha)*event.values[Y_INDEX];
            gravity[Z_INDEX] = alpha*gravity[Z_INDEX] + (1 - alpha)*event.values[Z_INDEX];

            xAccel = event.values[X_INDEX] - gravity[X_INDEX];
            yAccel = event.values[Y_INDEX] - gravity[Y_INDEX];
            zAccel = event.values[Z_INDEX] - gravity[Z_INDEX];
        }
        
        double magnitude = Math.sqrt(Math.pow(xAccel, 2) + Math.pow(yAccel, 2) + Math.pow(zAccel, 2));

        if (magnitude > ACCELERATION_LIMIT) {
            playAlarm();
            lastAlarmTime = System.nanoTime();
        }
	}
    
    /**========================================================================
	 * public void onDestroy()
	 * ------------------------------------------------------------------------
	 */
    @Override
    public void onDestroy(){
    	mediaPlayer.stop();
    	mediaPlayer.release();
    	mediaPlayer = null;
	}
    
    /**========================================================================
	 * public void onBind()
	 * ------------------------------------------------------------------------
	 */
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**========================================================================
	 * public void onAccuracyChanged
	 * ------------------------------------------------------------------------
	 */
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
}