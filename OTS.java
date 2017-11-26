/**
* OTS.java
* Or Tree Search is a class for
* implementing an or-tree based search. It includes a nested class for the nodes
* of the or-tree. It can be used as the global environment for Or-tree seaches
* conducted in the scope of our larger set based search environment
* @author Shane Sims
* version: 17 November 2017
*/

import java.util.*;                                                             // for Random


public class OTS{

  //Instance variables
  protected otsNode root;
  protected ArrayList<CourseLab> courseLabList;
  protected ArrayList<Slot> slotCList;
  protected ArrayList<Slot> slotLList;
  protected int foundIndividual;

  protected Map<Integer,Integer> indexVector;                             // init as HashMap Key is the course/lab id and value is the index of the course/lab position in a solution vector
  //TODO: will need to discuss when to imitialize this.
  // Option 1: constructor for OTS could take a list of courses and labs prepared by the parser and
  // then call a method to order these appropriately (course followed by its labs, etc.)
  // Option 2: could create this index vector object at the global search level and pass it in
  // to the OTS constructor. This probably makes more sense as this indexVector will be used in
  // the GA as well.

  //Constructor
  public OTS(ArrayList <CourseLab> coursesAndLabs,  ArrayList<Slot> courseSlots, ArrayList<Slot> labSlots){
    this.root = new otsNode(null, (new int[coursesAndLabs.size()]));
    this.courseLabList = coursesAndLabs;
    this.slotCList = courseSlots;
    this.slotLList = labSlots;
  }

  //Nested class for Otree instantiation
  protected class otsNode{
    public static final int YES = 1;
    public static final int NO = 2;
    public static final int TBD = 3;                                            // This represents '?' pr in {yes,?,no}

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
	
	public void setSolvedStatus(int i){
		this.solvedStatus = i;
	}
	
	public otsNode getParent(){
		return this.parent;
	}

  }
  // end otsNode Class 

	/**
	 * 
	 */
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
	
	/**
	 * Check if a vector has all classes assigned
	 * @param the node with the vector to be checked
	 * @return return true if all classes assigned, elsewise false.
	 */ 
	
	public boolean isFullVector(otsNode aNode){
		int[] aNodeVector = aNode.getAssign();
		for (int i; i<aNodeVector.length;i++){
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
		int[] parentVector = aNode.getAssign();				//get the vector from the node
		ArrayList<otsNode> children = new ArrayList<>();
		int index = searchArray(parentVector, 0);			//get index of first unassigned class
		if (index > -1){					
			if (courseLabList.get(index).isCourse()){		//if index in vector is a course
				for (int i = 1; i<=slotCList.size(); i++){	//create a branch for each course slot 
					int[] copy = parentVector.clone();
					copy[index] = i;
					children.add(new otsNode(aNode, copy));
				}
			}
		}
		else{											//if index in vector is a lab
			for (int i = slotCList.size()+1; i<=slotCList.size()+slotLList.size(); i++){	//create a branch for each lab slot
				int[] copy = parentVector.clone();
				copy[index] = i;
				children.add(new otsNode(aNode, copy));
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
		for (int i=0; i<children.size(); i++){					//Check if the children are valid vectors or not. 
																//Add valid children to a separate list
			if (children.get(i).getSolvedStatus() != 2 && constr(children.get(i).getAssign()) == true)
			{
				validChildren.add(children.get(i));
			}
			else{
				children.get(i).setSolvedStatus(2);				//Set invalid children to solvedStatus = NO
			}
		}
		
		int randSize = validChildren.size();
		
		if (randSize == 0){				//if all children are invalid, go back to the aNode's parent node and choose a different node
			aNode.setSolvedStatus(2);
			return chooseNode(aNode.getParent());
		}
		
		randSize = validChildren.size();
		Random rand = new Random();
		int n = rand.nextInt(randSize); 
		
		return validChildren.get(n);
		
	}
	
	

  // main method for testing - TODO: delete when class fully implemented and tested
//  public static void main(String[] args){
//    int[] testvec = new int[20];
//    otsNode root = new otsNode(null, testvec);
// }



}
