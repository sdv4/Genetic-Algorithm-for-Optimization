/**
* Slot.java
* A class implementing time slots, which can be either a course or lab/tutorial
* type timeslot
* @author Shane Sims, Justina, Chi
* version: 11 November 2017
*/

import java.time.LocalTime;
import java.lang.Number.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Slot {

  private static final AtomicInteger counter = new AtomicInteger(0);

  // Object attributes
  private final int id;
  private final boolean slotType;

  private final LocalTime startTime;
  private final LocalTime endTime;
  private final String day;
  private final int maxInSlot;
  private final int minInSlot;
  
  private final ArrayList<int> overlappingSlotIDs;
  private ArrayList<int> assignedCourselabIDs;


  // Example use:
  // Slot testSlot = new Slot(true, LocalTime.of(9,0), LocalTime.of(10,30), "MO", 4, 1);

  public Slot(boolean type, LocalTime start, LocalTime end, String day, int max, int min, ArrayList<int> overlappingSlotIDs){
    this.id = counter.incrementAndGet();
    this.slotType = type;
    this.startTime = start;
    this.endTime = end;
    this.day = day;
    this.maxInSlot = max;
    this.minInSlot = min;
    this.overlappingSlotIDs = overlappingSlotIDs;

  }

  public int getId(){
    return id;
  }

  public boolean getType(){
    return slotType;
  }

  public LocalTime getStart(){
    return startTime;
  }

  public LocalTime getEnd(){
    return endTime;
  }

  public String getDay(){
    return day;
  }

  public int getMax(){
    return maxInSlot;
  }

  public int getMin(){
    return minInSlot;
  }
  
  public ArrayList<int> getOverlappingSlotIDs(){
	return overlappingSlotIDs;
  }
  
  public ArrayList<int> getAssignedCourselabIDs(){
	return assignedCourselabIDs;
  }

}
