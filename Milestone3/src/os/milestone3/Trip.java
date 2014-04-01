package os.milestone3;
import android.annotation.SuppressLint;
import android.location.Location;


public class Trip {
	private Location origin;
	private Location destination;
	
	@SuppressLint("NewApi")
	public long getElapsedTime() {
		return (destination.getElapsedRealtimeNanos() - origin.getElapsedRealtimeNanos());
	}
	
	public Location getOrigin() {
		return origin;
	}

	public Location getDestination() {
		return destination;
	}
	
}
