package os.milestone3;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.IBinder;

public class BatteryMonitor extends Service {
	
	private final int BATTERY_CHECK_PERIOD = 300000;	// 5 minutes
	private final String BATTERY_LOG_FILENAME = "battery_log.txt";
	
	private Timer batteryCheckTimer;
	private float lastBatteryPct;
	
	/**========================================================================
	 * public void onCreate()
	 * ------------------------------------------------------------------------
	 */
	@Override
    public void onCreate() {	
		// Schedule battery check
		batteryCheckTimer = new Timer();
		batteryCheckTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				checkBattery();
			}
		}, 0, BATTERY_CHECK_PERIOD);
    }
	
	/**========================================================================
	 * private void checkBattery()
	 * ------------------------------------------------------------------------
	 */
	private void checkBattery() {
		
		IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent batteryStatus = this.registerReceiver(null, ifilter);
		
		// Are we charging?
		int chargingStatus = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
		boolean isCharging = chargingStatus == BatteryManager.BATTERY_STATUS_CHARGING || 
				             chargingStatus == BatteryManager.BATTERY_STATUS_FULL;
		
		// Only log if the phone is running on battery power
		if (!isCharging) {
		
			// Get battery information
			int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
			int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
	
			float currentBatteryPct = level / (float)scale;
			
			float batteryPctChange = 0;
			
			if (lastBatteryPct != 0) {
				batteryPctChange = Math.abs(lastBatteryPct - currentBatteryPct);
			}
			
			lastBatteryPct = currentBatteryPct;
			
			// Write
			LogFile log = new LogFile(BATTERY_LOG_FILENAME, true);
			log.write(currentBatteryPct + "," + batteryPctChange);
			log = null;
		}
		
		ifilter = null;
		batteryStatus = null;
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
