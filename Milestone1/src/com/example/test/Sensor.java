package com.example.test;

import android.app.Service;
import android.content.Intent;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.IBinder;

public class Sensor extends Service {
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void onSensorChanged(SensorEvent event) {
		System.out.println(event.values[0]);
	}

}
