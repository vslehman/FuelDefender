package os.milestone3;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.os.Environment;

/******************************************************************************
* public class TripDao
*------------------------------------------------------------------------------
*/
public class TripDao extends SQLiteOpenHelper {
	
	private static final String DATABASE_NAME = "fuel_defender";
	private static final int    DATABASE_VERSION = 1;
	private static final String TABLE_NAME = "trips";
	
	private static final String ID_KEY = "id";
	private static final String ID_DEFINITION = "INTEGER PRIMARY KEY AUTOINCREMENT";
	
	private static final String ORIGIN_LATITUDE_KEY = "origin_latitude";
	private static final String ORIGIN_LATITUDE_DEFINITION = "DOUBLE";
	
	private static final String ORIGIN_LONGITUDE_KEY = "origin_longitude";
	private static final String ORIGIN_LONGITUDE_DEFINITION = "DOUBLE";
	
	private static final String ORIGIN_TIMESTAMP_KEY = "origin_timestamp";
	private static final String ORIGIN_TIMESTAMP_DEFINITION = "LONG";
	
	private static final String DESTINATION_LATITUDE_KEY = "destination_latitude";
	private static final String DESTINATION_LATITUDE_DEFINITION = "DOUBLE";
	
	private static final String DESTINATION_LONGITUDE_KEY = "destination_longitude";
	private static final String DESTINATION_LONGITUDE_DEFINITION = "DOUBLE";
	
	private static final String DESTINATION_TIMESTAMP_KEY = "destination_timestamp";
	private static final String DESTINATION_TIMESTAMP_DEFINITION = "LONG";
	
	private static final String TIMES_TRAVELED_KEY = "times_traveled";
	private static final String TIMES_TRAVELED_DEFINITION = "INT DEFAULT 1";
	
	private static final String CREATE_TABLE =  "CREATE TABLE " + TABLE_NAME + " ( " +
			                                    ID_KEY + " " + ID_DEFINITION + ", " +
			                                    ORIGIN_LATITUDE_KEY + " " + ORIGIN_LATITUDE_DEFINITION + ", " +
			                                    ORIGIN_LONGITUDE_KEY + " " + ORIGIN_LONGITUDE_DEFINITION + ", " +
			                                    ORIGIN_TIMESTAMP_KEY + " " + ORIGIN_TIMESTAMP_DEFINITION + ", " +
			                                    DESTINATION_LATITUDE_KEY + " " + DESTINATION_LATITUDE_DEFINITION + ", " +
			                                    DESTINATION_LONGITUDE_KEY + " " + DESTINATION_LONGITUDE_DEFINITION + ", " +
			                                    DESTINATION_TIMESTAMP_KEY + " " + DESTINATION_TIMESTAMP_DEFINITION + ", " +
			                                    TIMES_TRAVELED_KEY + " " + TIMES_TRAVELED_DEFINITION + ");";
	
	/**========================================================================
	 * public TripDao()
	 * ------------------------------------------------------------------------
	 */
	public TripDao(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	/**========================================================================
	 * public void onCreate()
	 * ------------------------------------------------------------------------
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE);
		
	}
	
	/**========================================================================
	 * public ArrayList<Trip> getTrips()
	 * ------------------------------------------------------------------------
	 */
	public ArrayList<Trip> getTrips() {
		
		ArrayList<Trip> tripList = new ArrayList<Trip>();
		
		SQLiteDatabase db = getReadableDatabase();
		
		Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        
		while (cursor.moveToNext()) {
			int id = cursor.getInt(cursor.getColumnIndex(ID_KEY));
			
			double originLatitude = cursor.getDouble(cursor.getColumnIndex(ORIGIN_LATITUDE_KEY));
			double originLongitude = cursor.getDouble(cursor.getColumnIndex(ORIGIN_LONGITUDE_KEY));
			long originTimestamp = cursor.getLong(cursor.getColumnIndex(ORIGIN_TIMESTAMP_KEY));
			
			double destinationLatitude = cursor.getDouble(cursor.getColumnIndex(DESTINATION_LATITUDE_KEY));
			double destinationLongitude = cursor.getDouble(cursor.getColumnIndex(DESTINATION_LONGITUDE_KEY));
			long destinationTimestamp = cursor.getLong(cursor.getColumnIndex(DESTINATION_TIMESTAMP_KEY));
			
			int timesTraveled = cursor.getInt(cursor.getColumnIndex(TIMES_TRAVELED_KEY));
			
			Location origin = new Location("Database");
			origin.setLatitude(originLatitude);
			origin.setLongitude(originLongitude);
			origin.setTime(originTimestamp);
			
			Location destination = new Location("Database");
			destination.setLatitude(destinationLatitude);
			destination.setLongitude(destinationLongitude);
			destination.setTime(destinationTimestamp);
			
			tripList.add(new Trip(id, origin, destination, timesTraveled));
		}
		
		cursor.close();
		cursor = null;
		
		db.close();
		db = null;
		
		return tripList;
	}
	
	/**========================================================================
	 * public void addTrip()
	 * ------------------------------------------------------------------------
	 */
	public void addTrip(Trip trip) {
		
		SQLiteDatabase db = getWritableDatabase();
		
		ContentValues values = new ContentValues();
		
	    values.put(ORIGIN_LATITUDE_KEY, trip.getOrigin().getLatitude());
	    values.put(ORIGIN_LONGITUDE_KEY, trip.getOrigin().getLongitude());
	    values.put(ORIGIN_TIMESTAMP_KEY, trip.getOrigin().getTime());
	    
	    values.put(DESTINATION_LATITUDE_KEY, trip.getDestination().getLatitude());
	    values.put(DESTINATION_LONGITUDE_KEY, trip.getDestination().getLongitude());
	    values.put(DESTINATION_TIMESTAMP_KEY, trip.getDestination().getTime());

	    db.insert(TABLE_NAME, null, values);
	    
	    db.close();
		db = null;
	}
	
	/**========================================================================
	 * public void incrementTimesTraveled()
	 * ------------------------------------------------------------------------
	 */
	public void incrementTimesTraveled(int id) {
		//SQLiteDatabase db = getWritableDatabase();
		//db.execSQL("UPDATE " + TABLE_NAME +" SET " + TIMES_TRAVELED_KEY + " = " + TIMES_TRAVELED_KEY + " + 1 WHERE id=" + id );
		//db.close();
		//db = null;
	}
	
	/**========================================================================
	 * public void onUpgrade()
	 * ------------------------------------------------------------------------
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
	/**========================================================================
	 * public void exportToFile
	 * ------------------------------------------------------------------------
	 */
	private static final String logDirectory = Environment.getExternalStorageDirectory().getPath() + "/fuel_defender/";
	private static final String tripLog = logDirectory + "trip_log.txt";
	
	public void exportToFile() {
		
		// Create directory, if needed
		File appDirectory = new File(logDirectory);
		appDirectory.mkdirs();

		File logFile = new File(tripLog);

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
			
			// Write header
			buf.append("origin_latitude,origin_longitude,destination_latitude,destination_longitude,timesTraveled");
			buf.newLine();
			
			ArrayList<Trip> tripList = getTrips();
			
			for (Trip trip : tripList) {
				buf.append(String.format("%f,%f,%f,%f,%d", trip.getOrigin().getLatitude(), trip.getOrigin().getLongitude(),
						trip.getDestination().getLatitude(), trip.getDestination().getLongitude(),
						trip.getTimesTraveled()));
				buf.newLine();
			}
			
			buf.close();
			buf = null;
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
