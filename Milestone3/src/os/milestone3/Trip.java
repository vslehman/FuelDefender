package os.milestone3;
import android.annotation.SuppressLint;
import android.location.Location;


public class Trip {
	private Location origin;
	private Location destination;
	
	public Trip(Location origin, Location destination) {
		this.origin = origin;
		this.destination = destination;
	}
	
	public void setOrigin(Location origin) {
		this.origin = origin;
	}

	public void setDestination(Location destination) {
		this.destination = destination;
	}
	
	@SuppressLint("NewApi")
	public long getElapsedTime() {
		return (destination.getTime() - origin.getTime());
		//return (destination.getElapsedRealtimeNanos() - origin.getElapsedRealtimeNanos());
	}
	
	public Location getOrigin() {
		return origin;
	}

	public Location getDestination() {
		return destination;
	}
	
}
