package com.fueldefender.models;
import android.annotation.SuppressLint;
import android.location.Location;


public class Trip {
  private int id;
  private Location origin;
  private Location destination;
  private int timesTraveled;
  
  /**========================================================================
   * public Trip()
   * ------------------------------------------------------------------------
   */
  public Trip(int id, Location origin, Location destination, int timesTraveled) {
    this.id = id;
    this.origin = origin;
    this.destination = destination;
    this.timesTraveled = timesTraveled;
  }
  
  /**========================================================================
   * public Trip()
   * ------------------------------------------------------------------------
   */
  public Trip(Location origin, Location destination) {
    this.id = 0;
    this.origin = origin;
    this.destination = destination;
  }
  
  /**========================================================================
   * public int getId()
   * ------------------------------------------------------------------------
   */
  public int getId() {
    return id;
  }
  
  /**========================================================================
   * public void setOrigin()
   * ------------------------------------------------------------------------
   */
  public void setOrigin(Location origin) {
    this.origin = origin;
  }

  /**========================================================================
   * public void setDestination()
   * ------------------------------------------------------------------------
   */
  public void setDestination(Location destination) {
    this.destination = destination;
  }
  
  /**========================================================================
   * public long getElapsedTime()
   * ------------------------------------------------------------------------
   */
  @SuppressLint("NewApi")
  public long getElapsedTime() {
    return (destination.getTime() - origin.getTime());
  }
  
  /**========================================================================
   * public Location getOrigin()
   * ------------------------------------------------------------------------
   */
  public Location getOrigin() {
    return origin;
  }
  
  /**========================================================================
   * public Location getDestination()
   * ------------------------------------------------------------------------
   */
  public Location getDestination() {
    return destination;
  }
  
  /**========================================================================
   * public int getTimesTraveled()
   * ------------------------------------------------------------------------
   */
  public int getTimesTraveled() {
    return timesTraveled;
  }
  
}
