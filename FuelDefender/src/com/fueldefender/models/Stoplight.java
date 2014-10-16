package com.fueldefender.models;

import android.location.Location;

public class Stoplight {
  private Location location;
  private long totalTimeAtLight;
  private int timesEncountered;
  
  public Location getLocation() {
    return location;
  }
  public void setLocation(Location location) {
    this.location = location;
  }
  public long getTotalTimeAtLight() {
    return totalTimeAtLight;
  }
  public void setTotalTimeAtLight(long totalTimeAtLight) {
    this.totalTimeAtLight = totalTimeAtLight;
  }
  public int getTimesEncountered() {
    return timesEncountered;
  }
  public void setTimesEncountered(int timesEncountered) {
    this.timesEncountered = timesEncountered;
  }
}
