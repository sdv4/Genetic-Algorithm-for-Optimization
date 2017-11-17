/**
 * @author Chi Zhang
 * version: 1
 * date: 11-11-2017
 */
//This is a generalized class for Course or Lab objects;
//Id's for all courses/labs will be unique.
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class CourseLab {

	private static final AtomicInteger counter = new AtomicInteger(0);
	// Class attributes
	private final String name;
	private final int id;
	private final int lectureNumber;
	private final int labNumber;
	private final boolean isCourse;
	private final boolean isLab;

	private ArrayList<CourseLab> notCompatibleCoursesLabs;//replaces associatedLecture/Course/Lab, plus additional not compatible CourseLabs
	private int slotId = -1;//default value is -1 for unassigned slot

	public CourseLab(String name, int id, int lectureNumber, int labNumber, boolean isCourse, boolean isLab){
		this.name = name;
		this.id = counter.incrementAndGet();
		this.lectureNumber = lectureNumber;
		this.labNumber = labNumber;
		this.isCourse = isCourse;
		this.isLab = isLab;

	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}

	public int getLectureNumber() {
		return lectureNumber;
	}

	public int getLabNumber() {
		return labNumber;
	}

	public boolean isCourse() {
		return isCourse;
	}

	public boolean isLab() {
		return isLab;
	}

	public ArrayList<CourseLab> getNotCompatibleCoursesLabs() {
		return notCompatibleCoursesLabs;
	}

	public void setNotCompatibleCoursesLabs(ArrayList<CourseLab> notCompatibleCoursesLabs) {
		this.notCompatibleCoursesLabs = notCompatibleCoursesLabs;
	}

	public int getSlotId() {
		return slotId;
	}

	public void setSlotId(int slotId) {
		this.slotId = slotId;
	}

	/**
	 * iterate through list notCompatibleCoursesLabs
	 * @return true if slotid is compatible, false if not compatible
	 */
	/*
	public boolean checkCompatible(){
		int assignedSlotId = this.getSlotId();
		boolean isCompatible = true;
		for(int i = 0; i < this.notCompatibleCoursesLabs.size(); i++){
			int notCompatibleSlotId = this.notCompatibleCoursesLabs.get(i).getSlotId();
			if(notCompatibleSlotId == -1){
				continue;//move to end of for loop i, if it doesn't have Slot assigned
			}
			else if(assignedSlotId == notCompatibleSlotId){
				isCompatible = false;
				return isCompatible;//return false is there is at least one inCompatible CourseLab in assigned Slot
			}
			else{//check slots that are overlapping with assigned SlotId to see if they hold CourseLabs that are incompatible with this CourseLab
			        ArrayList<int> overlappingSlotIDs = Slots[assignedSlotId].getOverlappingSlotIDs();
				for(int j = 0; j < overlappingSlotIDs.size(); j++){
					notCompatibleSlotId = overlappingSlotIDs.get(j);//notCompatible id is updated to overlapping slotid
					if(assignedSlotId == notCompatibleSlotId){
						isCompatible = false;
						return isCompatible;
					}
				}
			}
		}
		return isCompatible;//if isCompatible not updated to false in loops, return default value true
	}*/
}
