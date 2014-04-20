package os.milestone3;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;

/******************************************************************************
* public class GoogleTripTime
*------------------------------------------------------------------------------
*/
public class GoogleTripTime {
	private long driveTime;
	private long bikeTime;
	private long walkTime;
	
	private String driveTimeText;
	private String bikeTimeText;
	private String walkTimeText;
	
	private long driveDistance;
	private long bikeDistance;
	private long walkDistance;

	private String driveDistanceText;
	private String bikeDistanceText;
	private String walkDistanceText;
	
	private final String TRANSPORT_MODE_DRIVE = "driving";
	private final String TRANSPORT_MODE_BIKE  = "bicycling";
	private final String TRANSPORT_MODE_WALK  = "walking";
	
	/**========================================================================
	 * public GoogleTripTime()
	 * ------------------------------------------------------------------------
	 */
	public GoogleTripTime(Location origin, Location destination) {
		// Driving
		String driveResponse = getGoogleMapsDistanceMatrix(origin, destination, TRANSPORT_MODE_DRIVE);
		Response driveParsed = getValuesFromJsonResponse(driveResponse);
		
		if (driveParsed != null) {
			driveTime = driveParsed.timeValue;
			driveTimeText = driveParsed.timeText;
			
			driveDistance = driveParsed.distanceValue;
			driveDistanceText = driveParsed.distanceText;
		}
		
		// Biking
		String bikeResponse = getGoogleMapsDistanceMatrix(origin, destination, TRANSPORT_MODE_BIKE);
		Response bikeParsed = getValuesFromJsonResponse(bikeResponse);
		
		if (bikeParsed != null) {
			bikeTime = bikeParsed.timeValue;
			bikeTimeText = bikeParsed.timeText;
			
			bikeDistance = bikeParsed.distanceValue;
			bikeDistanceText = bikeParsed.distanceText;
		}
		
		// Walking
		String walkResponse = getGoogleMapsDistanceMatrix(origin, destination, TRANSPORT_MODE_WALK);
		Response walkParsed = getValuesFromJsonResponse(walkResponse);
		
		if (walkParsed != null) {
			walkTime = walkParsed.timeValue;
			walkTimeText = walkParsed.timeText;
			
			walkDistance = walkParsed.distanceValue;
			walkDistanceText = walkParsed.distanceText;
		}
	}
	
	/**========================================================================
	 * Getters
	 * ------------------------------------------------------------------------
	 */
	public long getDriveTime() {
		return driveTime;
	}

	public long getBikeTime() {
		return bikeTime;
	}
	
	public long getWalkTime() {
		return walkTime;
	}
	
	public String getDriveTimeText() {
		return driveTimeText;
	}

	public String getBikeTimeText() {
		return bikeTimeText;
	}

	public String getWalkTimeText() {
		return walkTimeText;
	}
	
	public long getDriveDistance() {
		return driveDistance;
	}

	public long getBikeDistance() {
		return bikeDistance;
	}

	public long getWalkDistance() {
		return walkDistance;
	}

	public String getDriveDistanceText() {
		return driveDistanceText;
	}

	public String getBikeDistanceText() {
		return bikeDistanceText;
	}

	public String getWalkDistanceText() {
		return walkDistanceText;
	}

	/**========================================================================
	 * private String getGoogleMapsDistanceMatrix()
	 * ------------------------------------------------------------------------
	 */
	private String getGoogleMapsDistanceMatrix(Location origin, Location destination, String mode) {
		try {
			String originCoords = "origins=" + origin.getLatitude() + "," + origin.getLongitude();
			String destinationCoords = "destinations=" + destination.getLatitude() + "," + destination.getLongitude();
			
			String apiCall = "http://maps.googleapis.com/maps/api/distancematrix/json?";
			apiCall += originCoords + "&" + destinationCoords +"&mode=" + mode + "&sensor=true&units=imperial";

			URI url = new URI(apiCall);
		
			HttpClient httpclient = new DefaultHttpClient();
			
			HttpResponse response = null;
			int connectionAttempts = 0;
			
			while (response == null && connectionAttempts < 3) {
				try {
					response = httpclient.execute(new HttpGet(url));
				}
				catch (Exception e) {
					connectionAttempts++;
				}
			}
			
		    StatusLine statusLine = response.getStatusLine();
		    if(statusLine.getStatusCode() == HttpStatus.SC_OK){
		        ByteArrayOutputStream out = new ByteArrayOutputStream();
		        response.getEntity().writeTo(out);
		        out.close();
		        String responseString = out.toString();
		        
		        return responseString;

		    } else{
		        //Closes the connection.
		        response.getEntity().getContent().close();
		        throw new IOException(statusLine.getReasonPhrase());
		    }
		}
		catch (Exception e) {
			e.printStackTrace();
			return "null";
		}
	}
	
	/**========================================================================
	 * public ResponsePair getValuesFromJsonResponse()
	 * ------------------------------------------------------------------------
	 */
	private Response getValuesFromJsonResponse(String response) {
		
		try {
			JSONObject json = new JSONObject(response);
	        JSONArray rows = json.getJSONArray("rows");
	        JSONObject object = rows.getJSONObject(0);
	        JSONArray elements = object.getJSONArray("elements");
	        JSONObject obj2 = elements.getJSONObject(0);
	        
	        JSONObject duration = obj2.getJSONObject("duration");
	        long timeValue = duration.getLong("value");
	        String timeText = duration.getString("text");
	        
	        JSONObject distance = obj2.getJSONObject("distance");
	        long distanceValue = distance.getLong("value");
	        String distanceText = distance.getString("text");
	        
	        return new Response(timeValue, timeText, distanceValue, distanceText);
		}
		catch (JSONException e) {
			return null;
		}
	}
	
	/******************************************************************************
	* public class Response
	*------------------------------------------------------------------------------
	*/
	private class Response {
		
		public long timeValue;
		public String timeText;
		
		public long distanceValue;
		public String distanceText;
		
		public Response(long timeValue, String timeText, long distanceValue, String distanceText) {
			this.timeValue = timeValue;
			this.timeText = timeText;
			this.distanceValue = distanceValue;
			this.distanceText = distanceText;
		}
	}
}
