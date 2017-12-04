import java.util.*;
import java.util.Arrays;

public class SBS{
    private static final int maxPopSize = 1500;
    private static final int cullToSize = 1200;

    // Instance variables
    protected OTS orTreeSearchHelper;
    protected int[] bestAssignmentFound;
    protected int evalOfBestSoFar;
    protected int generations;

    protected ArrayList<CourseLab> courseLabList;
	protected ArrayList<Slot> slotCList;
	protected ArrayList<Slot> slotLList;
	protected ArrayList<Slot> slotList;
	protected int pen_courseMin;
	protected int pen_labMin;
	protected int pen_notPaired;
	protected int pen_section;
	protected int wMin;
	protected int wPair;
	protected int wPref;
	protected int wSecDiff;
	protected ArrayList<ArrayList<CourseLab>> sameCoursesList;
    protected double aveFitnessOfInds;

    // Constructor
    public SBS(){
        Parser aParser = new Parser("deptinst1.txt");
        aParser.start();
        courseLabList = aParser.getCourseLabList();
        slotCList = aParser.getCourseSlotList();
        slotLList = aParser.getLabSlotList();
        this.orTreeSearchHelper = new OTS(courseLabList,  slotCList, slotLList, aParser.getPartialAssign());
        this.bestAssignmentFound = new int[0];
        this.evalOfBestSoFar = Integer.MAX_VALUE;
        this.generations = 0;

        this.pen_courseMin = 0;
		this.pen_labMin = 0;
		this.pen_notPaired = 0;
		this.pen_section = 0;
		this.wMin = 1;
		this.wPair = 1;
		this.wPref = 1;
		this.wSecDiff = 1;
		this.sameCoursesList = aParser.getSameCoursesList();
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

	public void setWMin(int weight){
		this.wMin = weight;
	}

	public void setWPair(int weight){
		this.wPair = weight;
	}

	public void setWPref(int weight){
		this.wPref = weight;
	}

	public void setWSecDiff(int weight){
		this.wSecDiff = weight;
	}



//OUTLINE

// 1. Build starting population
// Let's try a starting pop of 5 for testng
    private ArrayList<int[]> getStartPop(int size){
        ArrayList<int[]> startPop = new ArrayList<int[]>();
        int foundIndividuals = 0;

        while(foundIndividuals < size){
            int[] candidate = orTreeSearchHelper.getIndividual();
            if(candidate.length > 0){ // add check that candidate not already in list
                startPop.add(candidate);
                foundIndividuals++;
                System.out.println("Individuals found for start population: " + foundIndividuals);
            }
        }

        return startPop;
    }

//2. Build search control
    public void searchControl(int startSize, int generationsToRun){
        ArrayList<int[]> state = getStartPop(startSize);                                // Get initial population
//            System.out.println("Starting population:");/////////////////////////////NOTE: DEBUG  STATEMENT
//            for(int[] individual : state){/////////////////////////////NOTE: DEBUG  STATEMENT
//            System.out.println();/////////////////////////////NOTE: DEBUG  STATEMENT
//            System.out.println(Arrays.toString(individual));/////////////////////////////NOTE: DEBUG  STATEMENT
//        }
        this.generations = 1;
        while(generations < generationsToRun){                                                //TODO: change this to while true when kill switch implemented
            state = fSelect(state);

            this.generations++;
            if(this.generations % 10 == 0)
                System.out.print(".");
            if(this.generations % 100 == 0)
                System.out.println("Current generation: " + this.generations + " average FIT: " + this.aveFitnessOfInds);
        }
        System.out.println("Eval: " + this.evalOfBestSoFar);/////////////////////////////NOTE: DEBUG  STATEMENT
        System.out.println("Fittest Individual produced: \n" + Arrays.toString(this.bestAssignmentFound));/////////////////////////////NOTE: DEBUG  STATEMENT


    }

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
//					System.out.println("*******************DEBUG: courseMin violated");
//					System.out.println("Coursemin: " + slotCList.get(j).getMin());
//					System.out.println("Number of courses in slot with id " + (j+1) + ": " + slotUseCounts[j]);
					score += pen_courseMin;

				}
			}
			else{ //if checking lab slot
				if(slotUseCounts[j] < slotLList.get(j - slotCList.size()).getMin()){
					//System.out.println("*******************DEBUG: labMin violated ");
					//System.out.println("Labmin: " + (slotLList.get(j - slotCList.size()).getMin()));
					//System.out.println("Number of courses in slot with id " + (j+1) + ": " + slotUseCounts[j]);
					score += pen_labMin;
				}
			}

		}

//		System.out.println("evalMinFilled score: "+score);
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
//					System.out.println("slotPref violated: "+courseLabList.get(i).getName()+",SlotId: "+aSlotId+", PrefVal:"+aSlotPref.getPrefVal());
					score += aSlotPref.getPrefVal();
				}

			}
		  }
		}
//		System.out.println("evalPref score: "+score);
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
	//				System.out.println("Courses not paired: "+courseLabList.get(i).getName()+" in slot "+assign[i]+", "+courseLabList.get(pairIndex).getName()+" in slot "+assign[pairIndex]);
					score += pen_notPaired;
				}

			}

		}
	//	System.out.println("evalPair score: "+score);
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
							//System.out.println("Different sections overlap: "+aCourseLab1.getName()+", "+aCourseLab2.getName());
							score += pen_section;
						}

					}
				}
			}
		}
		//System.out.println("evalSecDiff score: "+score);
		return score;
	}

	/*
     * eval assigns a score based on the four soft constraints and given weights
     * @param assignment vector
     * @return score accumulated for vector
     */
	public int eval(int [] assign){
		int score = 0;
		score = evalMinFilled(assign)*wMin + evalPref(assign)*wPref + evalPair(assign)*wPair + evalSecDiff(assign)*wSecDiff;
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



    private ArrayList<int[]> fSelect(ArrayList<int[]> currentState){

        ArrayList<int[]> nextState = new ArrayList<int[]>();

        //1. Order all individuals by decreasing fitness
        ArrayList<int[]> fit = new ArrayList<int[]>();          // Will hold currentState ordered by fitness
        ArrayList<ArrayList<Integer>> fitnessValues = new ArrayList<ArrayList<Integer>>();

        for(int i = 0; i < currentState.size(); i++){
            int evalOfCurrent =  eval(currentState.get(i));
            ArrayList<Integer> fitValIndexPair = new ArrayList<Integer>();
            fitValIndexPair.add(evalOfCurrent);
            fitValIndexPair.add(i);
            fitnessValues.add(fitValIndexPair);                                 // Each element of fitnessValues will be a [fitValue,indexInCurrent] pair
        }
            // Order fitnessValues by value of first element of each pair
            // Custom comparitor for fitness values
            Collections.sort(fitnessValues, new Comparator<ArrayList<Integer>>() {
                @Override
                public int compare(ArrayList<Integer> one, ArrayList<Integer> two) {
                    return one.get(0).compareTo(two.get(0));
                }
            });

            if(fitnessValues.get(0).get(0) < this.evalOfBestSoFar){// Update fittest found
                this.evalOfBestSoFar = fitnessValues.get(0).get(0);
                this.bestAssignmentFound = currentState.get(fitnessValues.get(0).get(1));
                System.out.println("New fittest individual found:");
                System.out.println("Eval = " + this.evalOfBestSoFar);
                System.out.println(Arrays.toString(this.bestAssignmentFound));
            }

            // Step 2. determine value of FIT
            int FIT = 0;

            for(int j = 0; j < fitnessValues.size(); j++){
                int value = fitnessValues.get(j).get(0);
                fit.add(j, currentState.get(fitnessValues.get(j).get(1)));
                FIT += value;
            }
            this.aveFitnessOfInds = FIT/fit.size();
//            System.out.println();/////////////////////////////NOTE: DEBUG  STATEMENT
//            System.out.println("DEBUG: checking if fitnessValues ordered by increasing eval:");/////////////////////////////NOTE: DEBUG  STATEMENT
            //for(int i = 0; i < fitnessValues.size(); i++){/////////////////////////////NOTE: DEBUG  STATEMENT
            //    System.out.println(Arrays.toString(fitnessValues.get(i).toArray()));/////////////////////////////NOTE: DEBUG  STATEMENT
            //}
//            System.out.println();                                               /////////////////////////////NOTE: DEBUG  STATEMENT
//            System.out.println("Individuals ordered by fitness:");/////////////////////////////NOTE: DEBUG  STATEMENT
            //for(int[] individual : fit){/////////////////////////////NOTE: DEBUG  STATEMENT
            //    System.out.println();/////////////////////////////NOTE: DEBUG  STATEMENT
            //    System.out.println(Arrays.toString(individual));/////////////////////////////NOTE: DEBUG  STATEMENT
            //}


//        System.out.println("FIT = " + FIT);/////////////////////////////NOTE: DEBUG  STATEMENT

        // Step 3. Associate to each individual in the state a part of an array from 1 to FIT
        int[] fitnessInterval = new int[FIT];
        int spotsAllocated = 0;                                                 // Keep track of spots in fitnessInterval filled
        for(int i = 0; i < fitnessValues.size(); i++){
            int indexOfInd = fitnessValues.get(i).get(1);                       // Get index of individual with (i+1)'th lowest eval value
            int spotsOfFitnessInterval = fitnessValues.get(fitnessValues.size() - i - 1).get(0);

            for(int j = spotsAllocated; j < (spotsAllocated + spotsOfFitnessInterval); j++){
                fitnessInterval[j] = indexOfInd;
            }
            spotsAllocated += spotsOfFitnessInterval;
        }

        // Step 4. Roulette wheel seletion of individuals
        Random rand = new Random();
        int parent1Index = fitnessInterval[rand.nextInt(FIT)];
        int parent2Index = fitnessInterval[rand.nextInt(FIT)];
//        System.out.println("Index of parent 1: " + parent1Index + "\nIndex of parent 2: " + parent2Index);

        // Step 5. call crossMut to get new individual for population
        int [] child = new int[0];
        while(child.length == 0 || (currentState.contains(child))){
            child = crossMut(currentState.get(parent1Index), currentState.get(parent2Index));
        }

        //int childFitness = eval(child);
        if(fit.size() > this.maxPopSize){
            System.out.println("Population size: " + fit.size());
            while(fit.size() > this.cullToSize)
                fit.remove(fit.size()-1);
            fit.subList((fit.size() - 51),(fit.size() - 1));
            System.out.println("Population culled");
            System.out.println("Population size: " + fit.size());

        }
        nextState = fit;                                                        // pass ordered version of state to next state
        nextState.add(child);                                                   // Add new individual to state





        return nextState;
    }


    private int[] crossMut(int[] parent1, int[] parent2){
        int[] child = orTreeSearchHelper.control2(parent1,parent2);
        return child;

    }



// Make keyboard interrupt 'kill switch' that returns result when control-d pressed



    public static void main(String[] args){
        SBS testGA = new SBS();
//        ArrayList<int[]> testStartPop = testGA.getStartPop(20);
//        System.out.println("Starting Population:");
//        for(int[] individual : testStartPop){
//            System.out.println();
//            System.out.println(Arrays.toString(individual));
//        }
        testGA.searchControl(100,30000);

    }


} // End SBS class
