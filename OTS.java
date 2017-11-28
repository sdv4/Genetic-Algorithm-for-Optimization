/**
* OTS.java
* Or Tree Search is a class for
* implementing an or-tree based search. It includes a nested class for the nodes
* of the or-tree. It can be used as the global environment for Or-tree seaches
* conducted in the scope of our larger set based search environment
* @author Shane Sims
* @author Justina Lem
* version:
* - 17 November 2017
* - 25 November 2017
* - 26 November 2017
*/

import java.util.*;                                                             // for HashMap
import java.util.Arrays;

public class OTS{

  public static final int YES = 1;
  public static final int NO = 2;
  public static final int TBD = 3;                                            // This represents '?' pr in {yes,?,no}
  //Instance variables
  protected otsNode root;
  protected ArrayList<CourseLab> courseLabList;
  protected ArrayList<Slot> slotCList;
  protected ArrayList<Slot> slotLList;
  protected ArrayList<Slot> slotList;
  protected int foundIndividual;

  ////
  // Constructor - creates an Or-tree based search instance that will produce a
  // valid (hard constraint satisfying) course assignment when appropriate method (TODO: name TBD)
  // is executed.
  // @param coursesAndLabs  - the list of courseLab objects parsed from input file - also serves as index vector
  // @param courseSlots     - list of possible time slots that can hold courseLab objects of type course
  // @param labSlots        - the list of possible time slots that can hold courseLab objects of type lab
  ////
  public OTS(ArrayList <CourseLab> coursesAndLabs,  ArrayList<Slot> courseSlots, ArrayList<Slot> labSlots){
    this.courseLabList = coursesAndLabs;
    this.slotCList = courseSlots;
    this.slotLList = labSlots;
    this.slotList =  new ArrayList<Slot>();
    this.slotList.addAll(this.slotCList);
    this.slotList.addAll(this.slotLList);
    this.root = new otsNode(null, new int[courseLabList.size()]);
  }

  //Nested class for Otree instantiation
  protected class otsNode{

    protected otsNode parent;
    protected int depth;
    protected int[] assign;                                                     // let 0 represent ?, and 1-int.max represent time slot id
    protected int solvedStatus;
    protected ArrayList<otsNode> children;                                      //Note: using ArrayList to permit resizing with we delete 'no' nodes

    otsNode(otsNode parent, int[] assign){
      this.parent = parent;
      this.assign = assign;
      this.solvedStatus = TBD;
      if (parent == null)
        this.depth = 0;
      else
        this.depth = parent.getDepth() + 1;
    }

    //public accessor and mutator methods - may not need these since data is protected
    public int getDepth(){
		    return this.depth;
    }

    public ArrayList<otsNode> getChildren(){
		    return this.children;
	  }

	  public void setChildren(ArrayList<otsNode> childrenArray){
      this.children = childrenArray;
	  }

	  public int[] getAssign(){
		    return this.assign;
	  }

	  public int getSolvedStatus(){
	     return this.solvedStatus;
	  }

    public otsNode getParent(){
  		return this.parent;
  	}

	  public void setSolvedStatus(int i){
		    this.solvedStatus = i;
	  }

    //TODO: make is full method like Chi did
    public int getNumNull(){
      int numNull=0;
      for(int i = 0; i < assign.length; i++){
        if(assign[i] == 0)
          numNull++;
      }
      return numNull;
    }

    // Method to determine the next position in the assignment vector to fill - the leftmost null position
    public int getNextToSchedule(){
      int[] aNodeVector = this.getAssign();
      int firstNull = -1;
      for(int i = 0; i < aNodeVector.length; i++){
        if(aNodeVector[i]==0){
          firstNull = i;
          break;
        }
      }
      return firstNull;
    }

  }// end otsNode Class

  /* Helper methods for controls 1 and 2 */

	 /** TODO: move this to be a mutator method of otsNode
	 * Check if a vector has all classes assigned
	 * @param the node with the vector to be checked
	 * @return return true if all classes assigned, elsewise false.
	 */
   public boolean isFullVector(otsNode aNode){
	    int[] aNodeVector = aNode.getAssign();
		  for (int i = 0; i<aNodeVector.length;i++){
			     if (aNodeVector[i] == 0)
				       return false;
		  }
		  return true;
	 }

	/**
	 * Altern creates the branches for a given node depending on if the next class to be assigned
	 * is a lecture or a lab
	 * @param otsNode to branch from
	 */
	private void altern(otsNode aNode){
		int[] parentVector = aNode.getAssign();				                              //get the vector from the node
		ArrayList<otsNode> children = new ArrayList<>();
		int index = searchArray(parentVector, 0);			                              //get index of first unassigned class
		if (index > -1){
			if (courseLabList.get(index).isCourse()){		                              //if index in vector is a course
				for (int i = 1; i<=slotCList.size(); i++){	                            //create a branch for each course slot
					int[] copy = parentVector.clone();
					copy[index] = i;
					children.add(new otsNode(aNode, copy));
				}
				aNode.setChildren(children);
			}
		}
		else{											                                                  //if index in vector is a lab
			for (int i = slotCList.size()+1; i<=slotCList.size()+slotLList.size(); i++){	//create a branch for each lab slot
				int[] copy = parentVector.clone();
				copy[index] = i;
				children.add(new otsNode(aNode, copy));
			}
			aNode.setChildren(children);
		}
	}


/**
 * Search for the first instance of x
 * @param an integer array and the key to find
 * @return returns -1 if key was not found, else the index of the key
 */
private int searchArray(int [] array, int x){
  int i= -1;
  for (i = 0; i<array.length; i++){
    if (array[i] == x)
      break;
  }
  return i;
}

 /**
  * Choose a child node of aNode that has a valid vector.
  * If all children of aNode are invalid, recursively search through aNode's parent until a valid child node is found
  * @param the node whose children are being chosen
  * @return a valid child node
  */
  private otsNode chooseNode(otsNode aNode){
    ArrayList<otsNode> children = aNode.getChildren();
    ArrayList<otsNode> validChildren = new ArrayList<>();
    for (int i=0; i<children.size(); i++){					                            //Check if the children are valid vectors or not.
      if (children.get(i).getSolvedStatus() != 2 && constr(children.get(i).getAssign()) == true){ //Add valid children to a separate list
        children.get(i).setSolvedStatus(TBD);
        validChildren.add(children.get(i));
      }
      else{
        children.get(i).setSolvedStatus(2);				                              //Set invalid children to solvedStatus = NO
      }
    }
    int randSize = validChildren.size();
    if (randSize == 0){				                                                  //if all children are invalid, go back to the aNode's parent node and choose a different node
       aNode.setSolvedStatus(2);
       return chooseNode(aNode.getParent());
    }
    randSize = validChildren.size();
    Random rand = new Random();
    int n = rand.nextInt(randSize);
    return validChildren.get(n);
  }

  private otsNode chooseNode2(otsNode aNode, int[] parent1, int[] parent2){
    ArrayList<otsNode> children = aNode.getChildren();
    ArrayList<otsNode> validChildren = new ArrayList<>();
    int[] aNodeVector = aNode.getAssign();
    int firstNull = aNode.getNextToSchedule();                                    //Find position of left most null entry
    // Make a children whose most recent non null node matches a parent
    int[] byParent1 = aNodeVector.clone();
    byParent1[firstNull] = parent1[firstNull];
    int[] byParent2 = aNodeVector.clone();
    byParent2[firstNull] = parent2[firstNull];
    int[][] parents = {byParent1, byParent2};
    // //Check if the children are valid vectors or not. NOTE: we might be able to only do this after we know that the byParent children wont work
    for (int i=0; i<children.size(); i++){
      if (children.get(i).getSolvedStatus() != NO && constr(children.get(i).getAssign()) == true){ //Add valid children to a separate list
        children.get(i).setSolvedStatus(TBD);
        validChildren.add(children.get(i));
      }
      else{
        children.get(i).setSolvedStatus(NO);				                          // Set invalid children to solvedStatus = NO
      }
    }
    int randSize = validChildren.size();
    if (randSize == 0){				                                                // If all children are invalid, go back to the aNode's parent node and choose a different node
       aNode.setSolvedStatus(NO);
       return chooseNode(aNode.getParent());
    }
    else if((constr(byParent1) == true) && (constr(byParent2) == true)){      // If both parents will produce valid child, choose one randomly
      //choose one at random
      Random rand = new Random();
      int parent = rand.nextInt(1);                                           // If zero, use parent1, else use parent2
      //find the child with this partial assignment vector and then return that node
      for (otsNode child : children){
        if(Arrays.equals(child.getAssign(), parents[parent]))
          return child;
      }
    }
    else if(constr(byParent1) == true){
      for (otsNode child : children){
        if(Arrays.equals(child.getAssign(), parents[0]))
          return child;
      }
    }
    else if(constr(byParent2) == true){
      for (otsNode child : children){
        if(Arrays.equals(child.getAssign(), parents[1]))
          return child;
      }
    }
    else{
      //else choose one at random
      Random rand = new Random();
      int n = rand.nextInt(randSize);
      return validChildren.get(n);

    }
    return validChildren.get(1);    //TODO: fix this
  }






     /*
  	 * check if assign is valid (against hard constraints)
  	 * returns true for valid, false for invalid
  	 */
	public boolean constr(int[] assign) {
		 //In this loop, check for course incompatibility as well as unwanted time slots
		for(int i = 0; i < assign.length; i++){
			int slotId = assign[i];
			if (slotId == 0){ continue;}//no slot index assigned, move to next index
			
			//-----check that the course is not assigned to an unwanted slot. This includes the tuesday at 11:00 slot for courses
			ArrayList<Integer> unwantedList = courseLabList.get(i).getUnWantedList();
			for (int j=0; j<unwantedList.size();j++){
				int unwantedId = unwantedList.get(j);
				//System.out.println("unwantedId: "+unwantedId);
				if (unwantedId == slotId){
					//System.out.println(courseLabList.get(i).getName()+" did not want "+unwantedId);
					return false;
				}
			}
			
			//-----check for incompatibility with other courses
			ArrayList<CourseLab> incompatibleList = this.courseLabList.get(i).getNotCompatibleCoursesLabs();
			for (int j=0; j<incompatibleList.size(); j++){		//incompatibleLists can be optimized later****
				int courseIndex = incompatibleList.get(j).getId();
				int otherSlotId = assign[courseIndex-1];
				if (otherSlotId == 0){ continue;}
				if (slotsOverlap(slotId, otherSlotId))
				{
					//System.out.println("courseIndex: "+courseIndex);
					//System.out.println("slotId: "+slotId+" otherSlotId: "+otherSlotId);
					//System.out.println("Incompatible: "+courseLabList.get(i).getName()+", "+courseLabList.get(courseIndex-1).getName());
					return false;
				}
			}
		}
		

	
			
		
		 

          //// Ensure that all 500 level courses are in different slots ////
          ////                                                          ////
          ArrayList<Integer> seniorCourseSlotIds = new ArrayList<Integer>();    // Create empty list to hold slot ids of slots that contain 500 level courses

          for(int i = 0; i < assign.length; i++){                               //for#1
            CourseLab aCourseLab = courseLabList.get(i);
            if(aCourseLab.isCourse()){ //check if element i is a course
                String[] courseNameNumber = (aCourseLab.getGeneralName()).split(" ");// get course number
                int courseNumber = Integer.parseInt(courseNameNumber[1]);
                if(courseNumber >= 500){
                  int aCourseInSlot = assign[i];
                  if(seniorCourseSlotIds.contains(aCourseInSlot))
                      return false;
                  else
                      seniorCourseSlotIds.add(aCourseInSlot);
                }
            }
          }// End for#1

          //// Ensure that all courses with lecture number >= 9 are scheduled in evening (>= 18:00) slots  ////
          ////                                                                                            ////
          for(int i = 0; i < assign.length; i++){                               //for#2: for each element in assign
              CourseLab aCourseLab = courseLabList.get(i);
              if(aCourseLab.isCourse() && (aCourseLab.getLectureNumber() >= 9)){   //check if if course lecture num >= 9
                  int courseStartHour = ((slotCList.get(assign[i])).getStart()).getHour();
                  if(courseStartHour < 18){
                      System.out.println(aCourseLab.getName());
                      return false;
                  }
              }

          }// End for#2

          //// Ensure that neither coursemax or labmax are violated for each slot used in assign ////
          ////                                                                                   ////

          // Make lists to track each time a slot is used by a course or lab
          int[] slotUseCounts = new int[slotCList.size() + slotLList.size()];   // each element index corresponds to a slotId, and the contents of the element are the number of times it has been used
          for(int i = 0; i < assign.length; i++){                               // For each slot used in assign - track how many times it was used
              slotUseCounts[assign[i]-1]++;
          }

          //Check each slot that was used to see if max uses violated
          for(int j = 0; j < slotUseCounts.length; j++){                        // For#3
              if(slotUseCounts[j] > 0){
                if(j < slotCList.size()){
                    if(slotUseCounts[j] > slotCList.get(j).getMax()){
                        //System.out.println("slot id: " + slotCList.get(j).getId());       //DEGUG statement TODO: delete when done debugging
                        //System.out.println("course max for this slot: " + slotCList.get(j).getMax());//DEGUG statement TODO: delete when done debugging
                        //System.out.println("courses assigned to this slot: " + slotUseCounts[j]);//DEGUG statement TODO: delete when done debugging
                        return false;
                    }
                }
                else{
                    if(slotLList.get(j - slotCList.size()).getMax() > slotUseCounts[j]){
                        return false;
                    }
                }
              }
          }// End for#3


          return true;
     }// End constr

	public boolean slotsOverlap(int slotId, int otherSlotId){
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









  // Control 1
  public int[] getIndividual(){
	   foundIndividual = 0;
		altern(root);
		 otsNode currentNode = root;
		 while (foundIndividual == 0){
		     currentNode = chooseNode(currentNode);
			   if (isFullVector(currentNode)){
				       foundIndividual = 1;
			   }
			   else{
				       if (currentNode.getChildren().size() == 0){
					            altern(currentNode);
				       }
			   }
		 }
		 return currentNode.getAssign();
	 }


  ////
  // Or-tree search control 2 - produces a course/lab assignment that satisfies
  // all hard constraints, but choices for each time slot assignment are
  // highly influenced by two input assignments which are themselves valid
  // assignments.
  // @param assign1   - a valid course assignment satisfying all hard constraints
  // @param assign2   - a valid course assignment satisfying all hard constraints
  // @return child    - a valid course assignment satisfying all hard constraints
  //                    while sharing many assingment choices in common with parents
  // NOTE: this assumes a new instance of OTS so that root is null.
  ////
  public int[] control2(int[] assign1, int[] assign2){
    int foundIndividual = 0;
    altern(root);                                                               // expand root node/ instantiate its list of children
    otsNode currentNode = root;
    while(foundIndividual == 0){
      currentNode = chooseNode2(currentNode, assign1, assign2);
      if(isFullVector(currentNode))
        foundIndividual = 1;
      else {
        if (currentNode.getChildren().size() == 0)
          altern(currentNode);
      }
    }
    int[] offSpring = currentNode.getAssign();
    return offSpring;
  }//end control2


// main method for testing - TODO: delete when class fully implemented and tested
  public static void main(String[] args){
    Parser aParser = new Parser("SE.txt");
    aParser.start();
    ArrayList<CourseLab> courseLabList = aParser.getCourseLabList();
    ArrayList<Slot> slotCList = aParser.getCourseSlotList();
	ArrayList<Slot> slotLList = aParser.getLabSlotList();

	OTS test = new OTS(courseLabList,  slotCList, slotLList);

    System.out.println("Length of CL list: " + courseLabList.size());

    int[] testAssign = {3,6,1,5,2,5,2,6};
    System.out.println("Constr test result: " + test.constr(testAssign));

    CourseLab aCourseLab = courseLabList.get(0);
    if(aCourseLab.isCourse()){ //check if element i is a course
        String[] courseNameNumber = (aCourseLab.getGeneralName()).split(" ");// get course number
        int courseNumber = Integer.parseInt(courseNameNumber[1]);
        System.out.println("Here:      ***********         : " + courseNumber);
        //if(aCourse.get)
    }






    System.out.println(courseLabList.get(0).getGeneralName());


  }



}// end OTS.java
