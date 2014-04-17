package os.milestone3;

import android.content.Context;
import android.location.Location;

public class TripRecorder {
	
	private Location tripOrigin;
	
	public TripRecorder() {
	
	}
	
	public void startTrip(Location origin) {
		tripOrigin = origin;
	}
	
	public void stopTrip(Location destination, Context context) {
		TripDao dao = new TripDao(context);		
		dao.addTrip(new Trip(tripOrigin, destination));
	}
}
