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
	private final CourseLab associatedLecture;
	private final String general;
	private final String specificLecture;

	private ArrayList<CourseLab> notCompatibleCoursesLabs;//replaces associatedLecture/Course/Lab, plus additional not compatible CourseLabs
	private int slotId = -1;//default value is -1 for unassigned slot
	private ArrayList<Integer> unwantedSlotIds;
	private ArrayList<slotPref> slotPrefList;
	private ArrayList<CourseLab> pairList;

//TODO: provide documentation for this constructor. What is int id argument for?
	public CourseLab(String name, int id, int lectureNumber, int labNumber, boolean isCourse, boolean isLab, CourseLab associatedLecture){
		this.name = name;
		this.id = counter.incrementAndGet();
		this.lectureNumber = lectureNumber;
		this.labNumber = labNumber;
		this.isCourse = isCourse;
		this.isLab = isLab;
		this.associatedLecture = associatedLecture;
		this.notCompatibleCoursesLabs = new ArrayList<>();
		this.unwantedSlotIds = new ArrayList<>();
		this.slotPrefList = new ArrayList<>();
		this.pairList = new ArrayList<>();

		String[] s = name.split(" ");
		this.general = s[0]+" "+s[1];
		this.specificLecture = s[0]+" "+s[1]+" "+s[2]+" "+s[3];
	}

	public String getGeneralName() {
		return general;
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}

	public String getSpecificLecture(){
		return this.specificLecture;
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

	public CourseLab getAssociatedLecture(){
		return this.associatedLecture;
	}

	public ArrayList<Integer> getUnWantedList(){
		return this.unwantedSlotIds;
	}
	
	public ArrayList<slotPref> getSlotPrefList(){
		return this.slotPrefList;
	}
	
	public ArrayList<CourseLab> getPairList(){
		return this.pairList;
	}

}
