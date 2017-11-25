/**
* OTS.java
* Or Tree Search is a class for
* implementing an or-tree based search. It includes a nested class for the nodes
* of the or-tree. It can be used as the global environment for Or-tree seaches
* conducted in the scope of our larger set based search environment
* @author Shane Sims
* version: 17 November 2017
*/

import java.util.*;                                                             // for HashMap

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
    this.root = new otsNode(null, (new ArrayList<>()));
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
    protected ArrayList<Integer> assign;                                                     // let 0 represent ?, and 1-int.max represent time slot id
    protected int solvedStatus;
    protected ArrayList<otsNode> children;                                      //Note: using ArrayList to permit resizing with we delete 'no' nodes

    otsNode(otsNode parent, ArrayList<Integer> assign){
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
	
	public setChildren(ArrayList<otsNode> childrenArray){
		this.children = childrenArray;
	}

  }
  // end otsNode Class 

	public int[] getIndividual(){
		foundIndividual = 0;
		Altern(root);
		while (foundIndividual == 0){
			
		}
	}
	
	private ArrayList<otsNode> Altern(otsNode aNode){
		bool isL
	}
	
	

  // main method for testing - TODO: delete when class fully implemented and tested
//  public static void main(String[] args){
//    int[] testvec = new int[20];
//    otsNode root = new otsNode(null, testvec);
// }



}
