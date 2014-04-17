package os.milestone3;
import android.annotation.SuppressLint;
import android.location.Location;


public class Trip {
	private int id;
	private Location origin;
	private Location destination;
	private int timesTraveled;
	
	public Trip(int id, Location origin, Location destination, int timesTraveled) {
		this.id = id;
		this.origin = origin;
		this.destination = destination;
		this.timesTraveled = timesTraveled;
	}
	
	public Trip(Location origin, Location destination) {
		this.id = 0;
		this.origin = origin;
		this.destination = destination;
	}
	
	public int getId() {
		return id;
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
	
	public int getTimesTraveled() {
		return timesTraveled;
	}
	
}
