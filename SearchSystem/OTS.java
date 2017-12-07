/**
* OTS.java
*
* Or Tree Search is a class for
* implementing an or-tree based search. It includes a nested class for the nodes
* of the or-tree. It can be used as the global environment for Or-tree seaches
* conducted in the scope of our larger set based search environment
*
* @author Justina Lem
* @author Kevin Naval
* @author Chi Zhang
* @author Shane Sims
*
* @version 6 December 2017
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
    protected int[] partialAssignment;

    /**
    * Constructor - creates an Or-tree based search instance that will produce a
    * valid (hard constraint satisfying) course assignment when appropriate method
    * is executed.
    * @param coursesAndLabs  - the list of courseLab objects parsed from input file - also serves as index vector
    * @param courseSlots     - list of possible time slots that can hold courseLab objects of type course
    * @param labSlots        - the list of possible time slots that can hold courseLab objects of type lab
    */
    public OTS(ArrayList <CourseLab> coursesAndLabs,  ArrayList<Slot> courseSlots, ArrayList<Slot> labSlots, int [] rootArray){
    this.courseLabList = coursesAndLabs;
    this.slotCList = courseSlots;
    this.slotLList = labSlots;
    this.slotList =  new ArrayList<Slot>();
    this.slotList.addAll(this.slotCList);
    this.slotList.addAll(this.slotLList);
    this.root = new otsNode(null, rootArray);
    this.partialAssignment = rootArray;
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
            }
            else{
                this.depth = parent.getDepth() + 1;
            }
            this.children = new ArrayList<otsNode>();
        }

        //public accessor and mutator methods
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

    ///// Helper methods for controls 1 and 2 /////

    /**
    * Check if a vector has all classes assigned
    * @param aNode node with the vector to be checked
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
    	int[] parentVector = aNode.getAssign();				                    //get the vector from the node
    	ArrayList<otsNode> children = new ArrayList<>();
    	int index = searchArray(parentVector, 0);			                    //get index of first unassigned class
    	if (index > -1){
            if (courseLabList.get(index).isCourse()){		                    //if index in vector is a course
                for (int i = 1; i<=slotCList.size(); i++){	                    //create a branch for each course slot
                    int[] copy = parentVector.clone();
                    copy[index] = i;
                    children.add(new otsNode(aNode, copy));
                }
                aNode.setChildren(children);
            }
            else{											                    //if index in vector is a lab
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
    * @param aNode  a node whose children are being chosen
    * @return       a valid child node
    */
    private otsNode chooseNode(otsNode aNode){
        ArrayList<otsNode> children = aNode.getChildren();
        ArrayList<otsNode> validChildren = new ArrayList<>();

        for (int i=0; i<children.size(); i++){					                // Check if the children are valid vectors or not.
            if (children.get(i).getSolvedStatus() != NO && constr(children.get(i).getAssign()) == true){ // Add valid children to a separate list
                children.get(i).setSolvedStatus(TBD);
                validChildren.add(children.get(i));
            }
            else{
                children.get(i).setSolvedStatus(NO);				            // Set invalid child's solvedStatus = NO
            }
        }
        aNode.setChildren(validChildren);                                       // This should allow for invalid children to be garbage collected?
        int randSize = validChildren.size();
        if (randSize == 0){				                                        // If all children are invalid, go back to the aNode's parent node and choose a different node
            aNode.setSolvedStatus(NO);
            if(aNode.getParent() != null)
                return chooseNode(aNode.getParent());
            else{
                System.out.println("Error: Hard constraints cannot be satisfied given current input file.");
                System.exit(0);
            }
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
        int firstNull = aNode.getNextToSchedule();                              //Find position of left most null entry
        // Make a children whose most recent non null node matches a parent
        int[] byParent1 = aNodeVector.clone();
        byParent1[firstNull] = parent1[firstNull];
        int[] byParent2 = aNodeVector.clone();
        byParent2[firstNull] = parent2[firstNull];
        int[][] parents = {byParent1, byParent2};
        // //Check if the children are valid vectors or not.
        for (int i=0; i<children.size(); i++){
            if (children.get(i).getSolvedStatus() != NO && constr(children.get(i).getAssign()) == true){ //Add valid children to a separate list
                children.get(i).setSolvedStatus(TBD);
                validChildren.add(children.get(i));
            }
            else{
                children.get(i).setSolvedStatus(NO);				            // Set invalid children to solvedStatus = NO
            }
        }
        int randSize = validChildren.size();
        if (randSize == 0){				                                        // If all children are invalid, go back to the aNode's parent node and choose a different node
            aNode.setSolvedStatus(NO);
            return chooseNode(aNode.getParent());
        }
        else if((constr(byParent1) == true) && (constr(byParent2) == true)){    // If both parents will produce valid child, choose one randomly
            //choose one at random
            Random rand = new Random();
            int parent = rand.nextInt(2);                                       // If zero, use parent1, else use parent2
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
        else{                                                                   //else choose one at random
            Random rand = new Random();
            int n = rand.nextInt(randSize);
            return validChildren.get(n);
        }
        return validChildren.get(1);
    }


    /*
    * check if assign is valid (against hard constraints)
    * returns true for valid, false for invalid
    */
    public boolean constr(int[] assign) {
        //// Ensure that all 500 level courses are in different slots ////
        ////                                                          ////
        ArrayList<Integer> seniorCourseSlotIds = new ArrayList<Integer>();      // Create empty list to hold slot ids of slots that contain 500 level courses

        for(int i = 0; i < assign.length; i++){                                 //for#1
            CourseLab aCourseLab = courseLabList.get(i);
            if(aCourseLab.isCourse()){                                          //check if element i is a course
                String[] courseNameNumber = (aCourseLab.getGeneralName()).split(" ");// get course number
                int courseNumber = Integer.parseInt(courseNameNumber[1]);
                if(courseNumber >= 500 & assign[i] != 0){
                    int aCourseInSlot = assign[i];
                    if(seniorCourseSlotIds.contains(aCourseInSlot)){
                        return false;
                    }
                    else
                        seniorCourseSlotIds.add(aCourseInSlot);
                }
            }
        }// End for#1

        //// Ensure that all courses with lecture number >= 9 are scheduled in evening (>= 18:00) slots  ////
        ////                                                                                            ////
        for(int i = 0; i < assign.length; i++){                                 //for#2: for each element in assign
            if(assign[i] != 0){
                CourseLab aCourseLab = courseLabList.get(i);
                if(aCourseLab.isCourse() && (aCourseLab.getLectureNumber() >= 9)){   //check if if course lecture num >= 9
                    int courseStartHour = ((slotCList.get(assign[i]-1)).getStart()).getHour();
                    if(courseStartHour < 18){
                        return false;
                    }
                }
            }
        }// End for#2

        //// Ensure that neither coursemax or labmax are violated for each slot used in assign ////
        ////                                                                                   ////
        // Make lists to track each time a slot is used by a course or lab
        int[] slotUseCounts = new int[slotCList.size() + slotLList.size()];     // each element index corresponds to a slotId, and the contents of the element are the number of times it has been used
        for(int i = 0; i < assign.length; i++){                                 // For each slot used in assign - track how many times it was used
            if(assign[i] != 0)
                slotUseCounts[(assign[i])-1]++;
        }

        //Check each slot that was used to see if max uses violated
        for(int j = 0; j < slotUseCounts.length; j++){                          // For#3
            if(slotUseCounts[j] > 0){
                if(j < slotCList.size()){
                    if(slotUseCounts[j] > slotCList.get(j).getMax()){
                        return false;
                    }
                }
                else{
                    if(slotUseCounts[j] > slotLList.get(j - slotCList.size()).getMax()){
                        return false;
                    }
                }
            }
        }// End for#3

        //
        //In this loop, check for course incompatibility as well as unwanted time slots
        //
        for(int i = 0; i < assign.length; i++){
            int slotId = assign[i];
            if (slotId == 0){ continue;}//no slot index assigned, move to next index
            //-----check that the course is not assigned to an unwanted slot. This includes the tuesday at 11:00 slot for courses
            ArrayList<Integer> unwantedList = courseLabList.get(i).getUnWantedList();
            for (int j=0; j<unwantedList.size();j++){
                int unwantedId = unwantedList.get(j);
                if (unwantedId == slotId){
                    return false;
                }
            }
            //-----check for incompatibility with other courses
            ArrayList<CourseLab> incompatibleList = this.courseLabList.get(i).getNotCompatibleCoursesLabs();
            for (int j=0; j<incompatibleList.size(); j++){		                //incompatibleLists can be optimized later****
                int courseIndex = incompatibleList.get(j).getId();
                int otherSlotId = assign[courseIndex-1];
                if (otherSlotId == 0){ continue;}
                if (slotsOverlap(slotId, otherSlotId)){
                    return false;
                }
            }
        }//end incompatibility check

        return true;
    }// End constr

	public boolean slotsOverlap(int slotId, int otherSlotId){
		if (slotId == otherSlotId){
			return true;
		}
		ArrayList<Integer>  overlappingSlots = this.slotList.get(slotId-1).getOverlappingSlots();
		for(int i=0; i < overlappingSlots.size(); i++){
			if(otherSlotId == overlappingSlots.get(i)){
				return true;                                                    //found overlapping slots
			}
		}
		return false;
	}

///////////////////////// Control Functions ///////////////////////////////////

    // Control 1
    public int[] getIndividual(){
        long terminateSearchAtThisTime = System.currentTimeMillis() + 1000;
        foundIndividual = 0;
        altern(root);
        ArrayList<otsNode> childrenOfRoot = root.getChildren();
		otsNode currentNode = root;
		while (foundIndividual == 0){
            if (System.currentTimeMillis() > terminateSearchAtThisTime) {
                int[] empty = new int[0];
                return empty;
            }
            currentNode = chooseNode(currentNode);
            if (isFullVector(currentNode)){
                foundIndividual = 1;
            }
            else{
                if(currentNode.getChildren().size() == 0){
                    altern(currentNode);
                }
            }
        }
        return currentNode.getAssign();
    }


    /**
    * Or-tree search control 2 - produces a course/lab assignment that satisfies
    * all hard constraints, but choices for each time slot assignment are
    * highly influenced by two input assignments which are themselves valid
    * assignments.
    * @param assign1   - a valid course assignment satisfying all hard constraints
    * @param assign2   - a valid course assignment satisfying all hard constraints
    * @return child    - a valid course assignment satisfying all hard constraints
    *                    while sharing many assingment choices in common with parents
    */
    public int[] control2(int[] assign1, int[] assign2){
        long terminateSearchAtThisTime = System.currentTimeMillis() + 1000;
        int foundIndividual = 0;
        altern(root);                                                           // expand root node/ instantiate its list of children
        otsNode currentNode = root;
        while(foundIndividual == 0){
            if (System.currentTimeMillis() > terminateSearchAtThisTime) {
                int[] empty = new int[0];
                return empty;
            }
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

}// end OTS.java
