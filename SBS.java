import java.util.*;
import java.util.Arrays;

public class SBS{
	protected ArrayList<CourseLab> courseLabList;
	protected ArrayList<Slot> slotCList;
	protected ArrayList<Slot> slotLList;
	protected ArrayList<Slot> slotList;
	protected int pen_courseMin;
	protected int pen_labMin;
	protected int pen_notPaired;
	protected int pen_section;
	protected ArrayList<ArrayList<CourseLab>> sameCoursesList;
	
	public SBS(ArrayList <CourseLab> coursesAndLabs,  ArrayList<Slot> courseSlots, ArrayList<Slot> labSlots, ArrayList<ArrayList<CourseLab>> sameCoursesList){
		this.courseLabList = coursesAndLabs;
		this.slotCList = courseSlots;
		this.slotLList = labSlots;
		this.slotList =  new ArrayList<Slot>();
		this.slotList.addAll(this.slotCList);
		this.slotList.addAll(this.slotLList);
		this.pen_courseMin = 0;
		this.pen_labMin = 0;
		this.pen_notPaired = 0;
		this.pen_section = 0;
		this.sameCoursesList = sameCoursesList;
		this.slotList =  new ArrayList<Slot>();
		this.slotList.addAll(this.slotCList);
		this.slotList.addAll(this.slotLList);
	}

	public void setPen_courseMin(int pen){
		this.pen_courseMin = pen;
	}
	
	public void setPen_labMin(int pen){
		this.pen_labMin = pen;
	}
	
	public void setPen_notPaired(int pen){
		this.pen_notPaired = pen;
	}
	
	public void setPen_section(int pen){
		this.pen_section = pen;
	}

//OUTLINE

// 1. Build starting population
// Let's try a starting pop of 5 for testng

    /*private int[][] getStartPop(int size){
        int[] startPop = new int[size];

        Parser aParser = new Parser("deptinst2.txt");
        aParser.start();
        ArrayList<CourseLab> courseLabList = aParser.getCourseLabList();
        ArrayList<Slot> slotCList = aParser.getCourseSlotList();
        ArrayList<Slot> slotLList = aParser.getLabSlotList();


    }*/
    
    /*
     * evalMinFilled checks the soft constraints for labmin and coursemin
     * @param assignment vector
     * @return score accumulated for vector
     */
    public int evalMinFilled(int [] assign){
		int score = 0;
		
		// Make lists to track each time a slot is used by a course or lab
		int[] slotUseCounts = new int[slotCList.size() + slotLList.size()];   // each element index corresponds to a slotId, and the contents of the element are the number of times it has been used
		for(int i = 0; i < assign.length; i++){                               // For each slot used in assign - track how many times it was used	  
		  if(assign[i] != 0)
			slotUseCounts[(assign[i])-1]++;
		}
		
		//Check each slot that was used to see if min uses violated
		for(int j = 0; j < slotUseCounts.length; j++){                        
			if(j < slotCList.size()){ //if checking course slot
				if(slotUseCounts[j] < slotCList.get(j).getMin()){
					//System.out.println("slot id: " + slotCList.get(j).getId());       //DEGUG statement TODO: delete when done debugging
					//System.out.println("course max for this slot: " + slotCList.get(j).getMax());//DEGUG statement TODO: delete when done debugging
					//System.out.println("courses assigned to this slot: " + slotUseCounts[j]);//DEGUG statement TODO: delete when done debugging
					System.out.println("*******************DEBUG: courseMin violated");
					System.out.println("Coursemin: " + slotCList.get(j).getMin());
					System.out.println("Number of courses in slot with id " + (j+1) + ": " + slotUseCounts[j]);
					score += pen_courseMin;
					
				}
			}
			else{ //if checking lab slot
				if(slotUseCounts[j] < slotLList.get(j - slotCList.size()).getMin()){
					System.out.println("*******************DEBUG: labMin violated ");
					System.out.println("Labmin: " + (slotLList.get(j - slotCList.size()).getMin()));
					System.out.println("Number of courses in slot with id " + (j+1) + ": " + slotUseCounts[j]);
					score += pen_labMin;
				}
			}
		  
		}
		
		System.out.println("evalMinFilled score: "+score);
		return score;
	}
	
	/*
     * evalMinFilled checks the soft constraint for preferences
     * @param assignment vector
     * @return score accumulated for vector
     */
	public int evalPref(int [] assign){
		int score = 0;
		//for each courseLab, check the slotPrefList if it is greater than zero
		for(int i = 0; i < assign.length; i++){                               
		  if (courseLabList.get(i).getSlotPrefList().size() > 0){
			ArrayList<slotPref> slotPrefList = courseLabList.get(i).getSlotPrefList();
			//for each slotPref found in slotPrefList of a course, get the preferred slotId
			for (int j = 0; j<slotPrefList.size(); j++){
				slotPref aSlotPref = slotPrefList.get(j);
				int aSlotId = aSlotPref.getSlotId();
				//if the courseLab is assigned to the preferred slot
				if (assign[i] == aSlotId){
					continue;
				}
				//if the courseLab is not assigned to the preferred slot
				else{
					System.out.println("slotPref violated: "+courseLabList.get(i).getName()+",SlotId: "+aSlotId+", PrefVal:"+aSlotPref.getPrefVal()); 
					score += aSlotPref.getPrefVal();
				}
				
			}
		  }
		}
		System.out.println("evalPref score: "+score);
		return score;		
	}
	
	/*
     * evalMinFilled checks the soft constraint for pairs
     * @param assignment vector
     * @return score accumulated for vector
     */
	public int evalPair (int [] assign){
		int score = 0;
		//for each courseLab, get the pairList
		for (int i=0; i<assign.length; i++){
			ArrayList<CourseLab> pairList = courseLabList.get(i).getPairList();
			//for each courseLab in pairList, check if the courseLab has the same time slot as assign[i]
			for (int j=0; j<pairList.size(); j++){
				int pairIndex = pairList.get(j).getId()-1;
				//if courseLabs overlap
				if (slotsOverlap(assign[pairIndex], assign[i])){
					continue;
				}
				else{//else they do not overlap
					System.out.println("Courses not paired: "+courseLabList.get(i).getName()+" in slot "+assign[i]+", "+courseLabList.get(pairIndex).getName()+" in slot "+assign[pairIndex]);
					score += pen_notPaired;
				}
				
			}
		
		}
		System.out.println("evalPair score: "+score);
		return score;
	}
	
	/*
     * evalMinFilled checks the soft constraints for different sections of a course being assigned to different time slots
     * @param assignment vector
     * @return score accumulated for vector
     */
	public int evalSecDiff (int [] assign){
		int score = 0;
		//for each member of sameCoursesList, check if the courses in the member overlap
		for (int i=0; i<sameCoursesList.size(); i++){
			//first retreive the member
			ArrayList<CourseLab> sameCourses= sameCoursesList.get(i);
			if (sameCourses.size()>1){ //ignore single member lists
				//for each course section in member, see if they overlap with each other
				for (int j=0; j<sameCourses.size(); j++){
					CourseLab aCourseLab1 = sameCourses.get(j);
					int aCourseLabIndex1 = aCourseLab1.getId()-1;
					for(int k=j+1; k<sameCourses.size(); k++){	//check for overlap of aCourseLab1 with the courseLabs that follow
						CourseLab aCourseLab2 = sameCourses.get(k);
						int aCourseLabIndex2 = aCourseLab2.getId()-1;
						
						if (assign[aCourseLabIndex1] == assign[aCourseLabIndex2]){ //if sections overlap
							System.out.println("Different sections overlap: "+aCourseLab1.getName()+", "+aCourseLab2.getName());
							score += pen_section;
						}
						
					}
				}
			}
		}
		System.out.println("evalSecDiff score: "+score);
		return score;
	}
	
	/*
     * eval assigns a score based on the four soft constraints and given weights
     * @param assignment vector
     * @return score accumulated for vector
     */
	public int eval(int [] assign, int wMinFilled, int wPref, int wPair, int wSecDiff){
		int score = 0;
		score = evalMinFilled(assign)*wMinFilled + evalPref(assign)*wPref + evalPair(assign)*wPair + evalSecDiff(assign)*wSecDiff;
		return score;
	}
	
	/*
	 * Function checks if the two time slots overlap in time including between course slots and lab slots
	 * @param The slotIds of the two slots to be compared
	 * @return Return true if slots overlap, false otherwise
	 */ 
	private boolean slotsOverlap(int slotId, int otherSlotId){
		if (slotId == otherSlotId){
			return true;
		}
		ArrayList<Integer>  overlappingSlots = this.slotList.get(slotId-1).getOverlappingSlots();
		for(int i=0; i < overlappingSlots.size(); i++){
			if(otherSlotId == overlappingSlots.get(i)){
				return true;//found overlapping slots
			}
		}
		return false;
	}



public static void main(String[] args){
	Parser aParser = new Parser("SE.txt");
	aParser.start();
    ArrayList<CourseLab> courseLabList = aParser.getCourseLabList();
    ArrayList<Slot> slotCList = aParser.getCourseSlotList();
    ArrayList<Slot> slotLList = aParser.getLabSlotList();
    ArrayList<ArrayList<CourseLab>> sameCoursesList = aParser.getSameCoursesList();
    SBS aSBS = new SBS(courseLabList, slotCList, slotLList, sameCoursesList);
    aSBS.setPen_courseMin(1);
    aSBS.setPen_labMin(1);
    aSBS.setPen_notPaired(1);
    aSBS.setPen_section(1);
    int [] assign = {2, 5, 2, 6, 2, 2, 6, 3, 6};
    int score = aSBS.eval(assign, 1, 1, 1, 1);
    System.out.println(score);

}


} // End SBS class
