package com.example.milestone2;

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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import com.example.test.R;

public class Milestone2 extends Activity {
	
	private SensorManager mSensorManager;
	private Sensor mSensor;
	
	private SensorEventListener stoplightListener;
	private SensorEventListener parkingListener;
	
	private final String parkingFilename = "sdcard/parking_log.txt";
	private final String stoplightFilename = "sdcard/stoplight_log.txt";
	
	private final long LOG_TIME = 10;	// In seconds
	
	/**
	 * void onCreate
	 * Assign sensor managers, sensors; define event listener methods
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Set up accelerometer
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
	    mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
	    
	    // Setup buttons
	    Button stopButton = (Button)findViewById(R.id.stop_button);
	    stopButton.setOnClickListener(new View.OnClickListener() {
	    	@Override
	    	public void onClick(View v) {
	    		startStoplight();
	    	}
	    });
	    
	    Button parkButton = (Button)findViewById(R.id.park_button);
	    parkButton.setOnClickListener(new View.OnClickListener() {
	    	@Override
	    	public void onClick(View v) {
	    		startParking();
	    	}
	    });
	    
	    // Setup listeners
	    stoplightListener = new SensorEventListener() {
			@Override
			public void onAccuracyChanged(Sensor arg0, int arg1) {}
			
			@Override
			public void onSensorChanged(SensorEvent event) {
				logAccelerometerEvent(event, stoplightFilename);
			}
	    };
	    
	    parkingListener = new SensorEventListener() {
	    	@Override
			public void onAccuracyChanged(Sensor arg0, int arg1) {}
			
			@Override
			public void onSensorChanged(SensorEvent event) {
				logAccelerometerEvent(event, parkingFilename);
			}
	    };
	}
	
	/**
	 * boolean onCreateOptionsMenu
	 * Stuff I don't care about
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/**
	 * void startStoplight()
	 * Called when the stop_button is pressed; disables other button and creates
	 * expiration timer.
	 */
	private void startStoplight() {
		
		// Update button label
		Button stopButton = (Button)findViewById(R.id.stop_button);
		stopButton.setText("Logging...");
		
		// Disable other button
		Button parkButton = (Button)findViewById(R.id.park_button);
		parkButton.setEnabled(false);
		
		// Register the accelerometer
	    mSensorManager.registerListener(stoplightListener, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
		
		// Schedule a timer to stop the accelerometer logging
		Timer clock = new Timer();
		clock.schedule(new TimerTask() {
				@Override
				public void run() {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							stopStoplight();
						}
					});
				}
			}, 1000*LOG_TIME);
	}
	
	/**
	 * void stopStoplight
	 * Callback for the timer created when the stoplight button is pressed
	 */
	private void stopStoplight() {
		
		// Update button label
		Button stopButton = (Button)findViewById(R.id.stop_button);
		stopButton.setText("Leave stoplight");
		
		// Enable other button
		Button parkButton = (Button)findViewById(R.id.park_button);
		parkButton.setEnabled(true);
		
		// Unregister accelerometer
	    mSensorManager.unregisterListener(stoplightListener);
	    
	    // Separate log entries
	    addLogTail(stoplightFilename);
	}
	
	/**
	 * void startParking()
	 * Called when the park_button is pressed; disables other button and creates
	 * expiration timer.
	 */
	private void startParking() {
		
		// Update button label
		Button parkButton = (Button)findViewById(R.id.park_button);
		parkButton.setText("Logging...");

		// Disable other button
		Button stopButton = (Button)findViewById(R.id.stop_button);
		stopButton.setEnabled(false);

		// Register the accelerometer
		mSensorManager.registerListener(parkingListener, mSensor, SensorManager.SENSOR_DELAY_FASTEST);

		// Schedule a timer to stop the accelerometer logging
		Timer clock = new Timer();
		clock.schedule(new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						stopParking();
					}
				});
			}
		}, 1000*LOG_TIME);
	}
	
	/**
	 * void stopParking
	 * Callback for the timer created when the parking button is pressed
	 */
	private void stopParking() {
		
		// Update button label
		Button parkButton = (Button)findViewById(R.id.park_button);
		parkButton.setText("Leave car");

		// Enable other button
		Button stopButton = (Button)findViewById(R.id.stop_button);
		stopButton.setEnabled(true);

		// Unregister accelerometer
		mSensorManager.unregisterListener(parkingListener);
		
		// Separate log entries
		addLogTail(parkingFilename);
	}
	
	/**
	 * void addLogTail
	 * @param filename - name of the file the delimiter will be appended to
	 * Adds a delimiter at the end of the passed log file
	 */
	private void addLogTail(String filename) {
		
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
			buf.append("-----------------------\n");
			buf.close();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * void logAccelerometerEvent
	 * @param event - event passed by the sensor
	 * @param filename - name of the file to log to
	 * Callback registered with the manager when the button is pressed
	 */
	private void logAccelerometerEvent(SensorEvent event, String filename) {
		
		// Get sensor data
		long timestamp = event.timestamp;
		float xAccel = event.values[0];
		float yAccel = event.values[1];
		float zAccel = event.values[2];
		
		File logFile = new File(filename);
		System.out.println(logFile.getAbsolutePath());
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
			buf.append(String.format("%d %.3f %.3f %.3f %n", timestamp, xAccel, yAccel, zAccel));
			buf.newLine();
			buf.close();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
