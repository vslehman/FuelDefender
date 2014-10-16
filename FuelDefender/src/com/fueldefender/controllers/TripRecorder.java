package com.fueldefender.controllers;

import java.util.ArrayList;

import com.fueldefender.models.Trip;


import android.content.Context;
import android.location.Location;

public class TripRecorder {
  
  private static final double TRIP_ERROR_MARGIN_METERS = 100.0;
  
  private Location tripOrigin;
  
  public TripRecorder() {
  
  }
  
  public void startTrip(Location origin) {
    tripOrigin = origin;
  }
  
  public void stopTrip(Location tripDestination, Context context) {
    TripDao dao = new TripDao(context); 
    
    // Get a list of the previously recorded trips and look for a trip
    // similar to the one that was just recorded
    ArrayList<Trip> tripList = dao.getTrips();
    
    for (Trip trip : tripList) {
      
      // Is this trip's origin close to another trip's origin?
      double originDelta = tripOrigin.distanceTo(trip.getOrigin());
      
      if (originDelta <= TRIP_ERROR_MARGIN_METERS) {
        // Is this trip's destination also close to the other trip's destination?
        double destinationDelta = tripDestination.distanceTo(trip.getDestination());
        
        if (destinationDelta <= TRIP_ERROR_MARGIN_METERS) {
          // The trip's origin and destination are within the margin of error
          // to match this trip; increment it's counter
          dao.incrementTimesTraveled(trip.getId());
          return;
        }
      }
    }
    
    // The trip did not match any previously recorded trips
    dao.addTrip(new Trip(tripOrigin, tripDestination));
  }
}
