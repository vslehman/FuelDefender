package os.milestone3;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

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
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import com.google.android.gms.maps.SupportMapFragment;

public class TripCalculator extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getTripDistance();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/**========================================================================
	 * public void getTripDistance()
	 * ------------------------------------------------------------------------
	 */
	public void getTripDistance() {
		
		Location lot = new Location("Thin air");
		lot.setLatitude(35.125395);
		lot.setLongitude(-89.937043);

		Location gym = new Location("The keyboard");
		gym.setLatitude(35.114092);
		gym.setLongitude(-89.938277);
		
		String response = getGoogleMapsDistanceMatrix(lot, gym);
		System.out.println("Duration: " + getDurationFromJsonResponse(response));
	}
	
	/**========================================================================
	 * public String getGoogleMapsDistanceMatrix()
	 * ------------------------------------------------------------------------
	 */
	public String getGoogleMapsDistanceMatrix(Location origin, Location destination) {
		try {
			String originCoords = "origins=" + origin.getLatitude() + "," + origin.getLongitude();
			String destinationCoords = "destinations=" + destination.getLatitude() + "," + destination.getLongitude();
			
			String apiCall = "http://maps.googleapis.com/maps/api/distancematrix/json?";
			apiCall += originCoords + "&" + destinationCoords +"&mode=walking&sensor=true&units=imperial";
			System.out.println(apiCall);
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
	 * public String getDurationFromJsonResponse()
	 * ------------------------------------------------------------------------
	 */
	public String getDurationFromJsonResponse(String response) {
		
		try {
			JSONObject json = new JSONObject(response);
	        JSONArray rows = json.getJSONArray("rows");
	        JSONObject object = rows.getJSONObject(0);
	        JSONArray elements = object.getJSONArray("elements");
	        JSONObject obj2 = elements.getJSONObject(0);
	        JSONObject duration = obj2.getJSONObject("duration");
	        return duration.getString("text");
		}
		catch (JSONException e) {
			return "null";
		}
	}

}
