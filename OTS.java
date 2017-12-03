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

import java.util.*;
import java.util.Arrays;

public class OTS{

  public static final int YES = 1;
  public static final int NO = 2;
  public static final int TBD = 3;                                              // This represents '?' pr in {yes,?,no}
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
  public OTS(ArrayList <CourseLab> coursesAndLabs,  ArrayList<Slot> courseSlots, ArrayList<Slot> labSlots, int [] rootArray){
    this.courseLabList = coursesAndLabs;
    this.slotCList = courseSlots;
    this.slotLList = labSlots;
    this.slotList =  new ArrayList<Slot>();
    this.slotList.addAll(this.slotCList);
    this.slotList.addAll(this.slotLList);
    this.root = new otsNode(null, rootArray);
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
      if (parent == null){
        this.depth = 0;
        System.out.println("********************** DUBUG: Root node created.");
      }
      else{
        this.depth = parent.getDepth() + 1;
        System.out.println("********************** DUBUG: Non-root node created.");
      }
      this.children = new ArrayList<otsNode>();
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
    //System.out.println("********************** DUBUG: parent assignment vector: " + Arrays.toString(parentVector));
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
  		else{											                                                  //if index in vector is a lab
  			for (int i = (slotCList.size()+1); i<=(slotCList.size()+slotLList.size()); i++){	//create a branch for each lab slot
  				int[] copy = parentVector.clone();
  				copy[index] = i;
  				children.add(new otsNode(aNode, copy));
  			}
  			aNode.setChildren(children);
  		}
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
    if(aNode.getParent() == null){
      System.out.println("********************** DUBUG: In chooseNode(root)");
      System.out.println("********************** DUBUG: root has " + children.size() + " children");
    }

    for (int i=0; i<children.size(); i++){					                            // Check if the children are valid vectors or not.
      if (children.get(i).getSolvedStatus() != NO && constr(children.get(i).getAssign()) == true){ // Add valid children to a separate list
        children.get(i).setSolvedStatus(TBD);
        validChildren.add(children.get(i));
      }
      else{
        children.get(i).setSolvedStatus(NO);				                              // Set invalid child's solvedStatus = NO
        //children.remove(i);
      }
    }
    if(aNode.getParent() == null){
        System.out.println("********************** DUBUG: root has " + validChildren.size() + " valid children.");

    }
//    aNode.setChildren(validChildren);                                         // This should allow for invalid children to be garbage collected?
    int randSize = validChildren.size();
    if (randSize == 0){				                                                  // If all children are invalid, go back to the aNode's parent node and choose a different node
       aNode.setSolvedStatus(NO);
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
    int firstNull = aNode.getNextToSchedule();                                  //Find position of left most null entry
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
      int parent = rand.nextInt(2);                                           // If zero, use parent1, else use parent2
      System.out.println("**********************DEBUG: parent " + parent + " chosen.");
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
          System.out.println("*******************DEBUG: Constr-unwanted failed");
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
          System.out.println("*******************DEBUG: Constr-incompatability failed");
					return false;
				}
			}
		}//end incompatibility check


          //// Ensure that all 500 level courses are in different slots ////
          ////                                                          ////
          ArrayList<Integer> seniorCourseSlotIds = new ArrayList<Integer>();    // Create empty list to hold slot ids of slots that contain 500 level courses

          for(int i = 0; i < assign.length; i++){                               //for#1
            CourseLab aCourseLab = courseLabList.get(i);
            if(aCourseLab.isCourse()){ //check if element i is a course
                String[] courseNameNumber = (aCourseLab.getGeneralName()).split(" ");// get course number
                int courseNumber = Integer.parseInt(courseNameNumber[1]);
                if(courseNumber >= 500 & assign[i] != 0){
                  int aCourseInSlot = assign[i];
                  if(seniorCourseSlotIds.contains(aCourseInSlot)){
                      System.out.println("*******************DEBUG: Constr-500 Level in different slots failed with: ");
                      System.out.println(Arrays.toString(assign));
                      return false;
                  }
                  else
                      seniorCourseSlotIds.add(aCourseInSlot);
                }
            }
          }// End for#1

          //// Ensure that all courses with lecture number >= 9 are scheduled in evening (>= 18:00) slots  ////
          ////                                                                                            ////
          for(int i = 0; i < assign.length; i++){                               //for#2: for each element in assign
              if(assign[i] != 0){
                CourseLab aCourseLab = courseLabList.get(i);
                if(aCourseLab.isCourse() && (aCourseLab.getLectureNumber() >= 9)){   //check if if course lecture num >= 9
                    int courseStartHour = ((slotCList.get(assign[i]-1)).getStart()).getHour();
                    if(courseStartHour < 18){
                        System.out.println(aCourseLab.getName());
                        System.out.println("*******************DEBUG: Constr- lecture numbers>9 in different slots failed with: ");
                        System.out.println(Arrays.toString(assign));
                        return false;
                    }
                }
              }

          }// End for#2

          //// Ensure that neither coursemax or labmax are violated for each slot used in assign ////
          ////                                                                                   ////

          // Make lists to track each time a slot is used by a course or lab
          int[] slotUseCounts = new int[slotCList.size() + slotLList.size()];   // each element index corresponds to a slotId, and the contents of the element are the number of times it has been used
          for(int i = 0; i < assign.length; i++){                               // For each slot used in assign - track how many times it was used
              if(assign[i] != 0)
                slotUseCounts[(assign[i])-1]++;
          }

          //Check each slot that was used to see if max uses violated
          for(int j = 0; j < slotUseCounts.length; j++){                        // For#3
              if(slotUseCounts[j] > 0){
                if(j < slotCList.size()){
                    if(slotUseCounts[j] > slotCList.get(j).getMax()){
                        //System.out.println("slot id: " + slotCList.get(j).getId());       //DEGUG statement TODO: delete when done debugging
                        //System.out.println("course max for this slot: " + slotCList.get(j).getMax());//DEGUG statement TODO: delete when done debugging
                        //System.out.println("courses assigned to this slot: " + slotUseCounts[j]);//DEGUG statement TODO: delete when done debugging
                        System.out.println("*******************DEBUG: Constr- coursemax failed: ");
                        System.out.println("Coursemax: " + slotCList.get(j).getMax());
                        System.out.println("Number of courses in slot with id " + (j+1) + ": " + slotUseCounts[j]);

                        return false;
                    }
                }
                else{
                    if(slotUseCounts[j] > slotLList.get(j - slotCList.size()).getMax()){
                        System.out.println("*******************DEBUG: Constr- labmax failed: ");
                        System.out.println("Labmax: " + (slotLList.get(j - slotCList.size()).getMax()));
                        System.out.println("Number of courses in slot with id " + ((j - slotCList.size())+1) + ": " + slotUseCounts[j]);
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

///////////////////////// Control Functions ///////////////////////////////////

  // Control 1
  public int[] getIndividual(){
	   foundIndividual = 0;
		altern(root);

    //System.out.println("********************** DUBUG: assignment vectors of children of root:");
    ArrayList<otsNode> childrenOfRoot = root.getChildren();

    System.out.println("********************** DUBUG: Returned from altern(root)");
    System.out.println("********************** DUBUG: Root has " + childrenOfRoot.size() + " children.");
    System.out.println("********************** DUBUG: assignment vectors of children of root:");
    for(int i = 0; i < childrenOfRoot.size(); i++){                             ////////////////////////////////DEBUG
      int[] cAssign = childrenOfRoot.get(i).getAssign();
      System.out.println("********************** DUBUG: " + Arrays.toString(cAssign));
      System.out.println("****************************** DUBUG: this child has " + childrenOfRoot.get(i).getChildren().size() + " children.");
    }

		 otsNode currentNode = root;
		 while (foundIndividual == 0){
		     currentNode = chooseNode(currentNode);
         System.out.println("********************** DUBUG: returned from chooseNode");
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
    return currentNode.getAssign();
  }//end control2


// main method for testing - TODO: delete when class fully implemented and tested
  public static void main(String[] args){
    Parser aParser = new Parser("deptinst1.txt");
    aParser.start();
    ArrayList<CourseLab> courseLabList = aParser.getCourseLabList();
    ArrayList<Slot> slotCList = aParser.getCourseSlotList();
    ArrayList<Slot> slotLList = aParser.getLabSlotList();

    //System.out.println(courseLabList.size());
    //System.out.println(slotCList.size());
    //System.out.println(slotLList.size());

    OTS testOrTreeSearchInstance = new OTS(courseLabList,  slotCList, slotLList, aParser.getPartialAssign());


    ////////////// For testing search control 1 ////////////////////////////////
    int[] tAssign = testOrTreeSearchInstance.getIndividual();
    System.out.println("Valid individual: " + Arrays.toString(tAssign));

    ////////////// For testing search control 2 ////////////////////////////////
/*
    int[] assign2 = {8, 23, 44, 26, 36, 53, 38, 2, 32, 48, 41, 24, 36, 28, 10, 53, 46, 28, 26, 36, 34, 21, 22, 52, 44, 40, 48, 47, 13, 22, 31, 35, 49, 41, 28, 18, 24, 51, 30, 28, 10, 42, 49, 45, 28, 30, 34, 8, 5, 33, 51, 30, 40, 52, 23, 27, 3, 43, 29, 25, 31, 9, 43, 23, 33, 24, 12, 25, 52, 39, 9, 6, 43, 38, 52, 50, 3, 35, 18, 44, 24, 17, 29, 23, 26, 5, 51, 15, 29, 44, 50, 40, 19, 28, 31, 45, 8, 30, 9, 32, 39, 11, 52, 40, 43, 17, 29, 49, 44, 14, 30, 27, 47, 25, 1, 37, 30, 20, 38, 25, 6, 40, 52, 2, 50, 27, 44, 39, 3, 37, 43, 49, 23, 4, 42, 10, 27, 2, 29, 5, 46, 1, 39, 9, 23, 20, 40, 15, 31, 29, 3, 36, 51, 19, 24, 50, 8, 26, 37, 7, 43, 49, 6, 25, 24, 14, 38, 4, 26, 31, 38, 18, 7, 51, 27, 49, 26, 50, 18, 31, 25, 17, 41};
    int[] assign1 = {12, 51, 47, 31, 48, 29, 23, 7, 45, 53, 47, 49, 22, 36, 14, 44, 48, 29, 26, 49, 53, 6, 41, 26, 37, 51, 24, 52, 21, 33, 51, 29, 31, 30, 37, 14, 28, 25, 22, 38, 18, 44, 36, 50, 28, 33, 24, 18, 10, 43, 32, 34, 37, 27, 24, 25, 3, 51, 25, 32, 26, 4, 30, 31, 28, 26, 18, 36, 45, 34, 13, 6, 37, 30, 26, 23, 5, 43, 19, 50, 23, 9, 28, 46, 27, 10, 35, 20, 30, 24, 36, 51, 7, 44, 23, 40, 3, 26, 10, 35, 37, 5, 36, 52, 24, 17, 29, 31, 52, 9, 31, 28, 27, 50, 6, 43, 30, 2, 49, 38, 15, 28, 29, 19, 23, 40, 51, 31, 8, 40, 43, 25, 49, 5, 39, 6, 41, 1, 49, 9, 44, 15, 50, 18, 39, 17, 37, 11, 23, 40, 7, 39, 27, 3, 41, 39, 19, 30, 24, 4, 39, 46, 20, 50, 29, 10, 50, 1, 40, 52, 52, 4, 18, 38, 27, 36, 52, 44, 2, 49, 40, 8, 27};
*/
/*
    int[] assign1 = {3, 4, 1, 5, 3, 6, 2, 6};
    int[] assign2 = {2, 4, 3, 6, 1, 5, 1, 6};
    int[] tAssign = testOrTreeSearchInstance.control2(assign1, assign2);
    System.out.println("Valid individual: " + Arrays.toString(tAssign));
    int mutations = 0;
    for(int i = 0; i < tAssign.length; i++){
      if(tAssign[i] != assign1[i] && tAssign[i] != assign2[i])
        mutations++;
    }
    System.out.println("Number of mutations: " + mutations);
*/
  }



}// end OTS.java
