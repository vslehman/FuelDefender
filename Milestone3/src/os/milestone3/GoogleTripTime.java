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
import android.widget.TextView;

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
		ResponsePair driveParsed = getDurationFromJsonResponse(driveResponse);

		driveTime = driveParsed.value;
		driveTimeText = driveParsed.text;
		
		// Biking
		String bikeResponse = getGoogleMapsDistanceMatrix(origin, destination, TRANSPORT_MODE_BIKE);
		ResponsePair bikeParsed = getDurationFromJsonResponse(bikeResponse);
		
		bikeTime = bikeParsed.value;
		bikeTimeText = bikeParsed.text;
		
		// Walking
		String walkResponse = getGoogleMapsDistanceMatrix(origin, destination, TRANSPORT_MODE_WALK);
		ResponsePair walkParsed = getDurationFromJsonResponse(walkResponse);
		
		walkTime = walkParsed.value;
		walkTimeText = walkParsed.text;
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

	/**========================================================================
	 * public String getGoogleMapsDistanceMatrix()
	 * ------------------------------------------------------------------------
	 */
	public String getGoogleMapsDistanceMatrix(Location origin, Location destination, String mode) {
		try {
			String originCoords = "origins=" + origin.getLatitude() + "," + origin.getLongitude();
			String destinationCoords = "destinations=" + destination.getLatitude() + "," + destination.getLongitude();
			
			String apiCall = "http://maps.googleapis.com/maps/api/distancematrix/json?";
			apiCall += originCoords + "&" + destinationCoords +"&mode=" + mode + "&sensor=true&units=imperial";

			URI url = new URI(apiCall);
		
			HttpClient httpclient = new DefaultHttpClient();
		    HttpResponse response = httpclient.execute(new HttpGet(url));
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
	 * public ResponsePair getDurationFromJsonResponse()
	 * ------------------------------------------------------------------------
	 */
	public ResponsePair getDurationFromJsonResponse(String response) {
		
		try {
			JSONObject json = new JSONObject(response);
	        JSONArray rows = json.getJSONArray("rows");
	        JSONObject object = rows.getJSONObject(0);
	        JSONArray elements = object.getJSONArray("elements");
	        JSONObject obj2 = elements.getJSONObject(0);
	        JSONObject duration = obj2.getJSONObject("duration");
	        long value = duration.getLong("value");
	        String text = duration.getString("text");
	        
	        return new ResponsePair(value, text);
		}
		catch (JSONException e) {
			return null;
		}
	}
	
	/******************************************************************************
	* public class ResponsePair
	*------------------------------------------------------------------------------
	*/
	private class ResponsePair {
		
		public long value;
		public String text;
		
		public ResponsePair(long value, String text) {
			this.value = value;
			this.text = text;
		}
	}
}
