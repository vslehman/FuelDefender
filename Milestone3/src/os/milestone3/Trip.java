package os.milestone3;
import android.annotation.SuppressLint;
import android.location.Location;


public class Trip {
	private Location begin;
	private Location end;
	
	@SuppressLint("NewApi")
	public long getElapsedTime() {
		return (end.getElapsedRealtimeNanos() - begin.getElapsedRealtimeNanos());
	}
	
	public Location getBegin() {
		return begin;
	}
	public void setBegin(Location begin) {
		this.begin = begin;
	}
	public Location getEnd() {
		return end;
	}
	public void setEnd(Location end) {
		this.end = end;
	}
	
	
}
