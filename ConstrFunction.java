//Constr function
public class ConstrFunction(){
//All course sections with a section number starting LEC 9 are evening classes and have to be scheduled into evening slots.
//All courses (course sections) on the 500-level have to be scheduled into different time slots.

	/*
	 * check if assign is valid (against hard constraints)
	 * returns true for valid, false for invalid
	 */
	public boolean constr(int[] assign) {
		//assign = new int[]{ 0,3,0,5};//sample assignment
		//check every index in array
		for(int i = 0; i < assign.length; i++){
			int slotId = assign[i];
			if (slotId == 0){ continue;}//no slot index assigned, move to next index
			//else get list of not compatibles at index i of courseLabs list
			ArrayList<CourseLab> ImNotCompatibleWithCoursesLabs = this.courseLabList.get(i).getNotCompatibleCoursesLabs();
//			for(int z = 0; z < ImNotCompatibleWithCoursesLabs.size(); z++){
//				System.out.println(ImNotCompatibleWithCoursesLabs.get(z).getId());
//			}
			//loop through remaining slotIds in assign vector and find all matches
			for(int j = i + 1; j < assign.length; j++){//check index i against every index j (after i)
				int otherSlotId = assign[j];//get one of remaining slotIds
				//4 cases,
				if(otherSlotId == 0){//no slot id assigned, skip THIS CASE PASSED
					continue;
				}
				else if( slotId == otherSlotId){//matches current slotId THIS CASE PASSED
					System.out.println("found matching slotID " + otherSlotId);
					//loop through assigned Ids and look for incompatible courses
					ArrayList<Integer> assignedIDs = this.slotList.get(slotId-1).getCoursesAssigned();
//					assignedIDs = new ArrayList<Integer>(Arrays.asList(1, 3, 4, 6));
//					for(int k = 0; k < assignedIDs.size(); k++){
//						System.out.println("assignedID " + assignedIDs.get(k));
//					}
					for(int k = 0; k < assignedIDs.size(); k++){
						for(int l = 0; l < ImNotCompatibleWithCoursesLabs.size(); l++){//every index l is compared against every index k
							if(assignedIDs.get(k) == ImNotCompatibleWithCoursesLabs.get(l).getId()){
								return false;//a match is found not compatibles
							}
						}//end of loop l
					}//end of loop k
				}
				else if(slotsOverlap(slotId, otherSlotId)){//slots overlap
					System.out.println("found overlapping slot ID " + otherSlotId);
					ArrayList<Integer> assignedIDsInOverlappingSlot = this.slotList.get(otherSlotId-1).getCoursesAssigned();
					for(int k = 0; k < assignedIDsInOverlappingSlot.size(); k++){
						for(int l = 0; l < ImNotCompatibleWithCoursesLabs.size(); l++){//every index l is compared against every index k
							if(assignedIDsInOverlappingSlot.get(k) == ImNotCompatibleWithCoursesLabs.get(l).getId()){
								return false;//a match is found not compatibles
							}
						}//end of loop l
					}//end of loop k
				}
				else{
					continue;
				}
			}//end of loop j
		}//end of loop i
		//print to console the notCompatibles/overlappingSlots
//		for(int i = 0; i < this.courseLabList.size(); i++){
//			System.out.println(i+"Courselab " + this.courseLabList.get(i).getId() +" " + this.courseLabList.get(i).getName() +" is not compatible with");
//			for(int j = 0; j < this.courseLabList.get(i).getNotCompatibleCoursesLabs().size(); j++){
//				System.out.println(this.courseLabList.get(i).getNotCompatibleCoursesLabs().get(j).getId());
//			}
//		}
//		for(int i = 0; i < this.slotCList.size(); i++){
//			System.out.println(i + "slotC "+ this.slotCList.get(i).getId() +" is not compatible with");
//			for(int j = 0; j < this.slotCList.get(i).getOverlappingSlots().size(); j++){
//				System.out.println(this.slotCList.get(i).getOverlappingSlots().get(j));
//			}
//		}
//
//		for(int i = 0; i < this.slotLList.size(); i++){
//			System.out.println(i+"slotL "+ this.slotLList.get(i).getId() +" is not compatible with");
//			for(int j = 0; j < this.slotLList.get(i).getOverlappingSlots().size(); j++){
//				System.out.println(this.slotLList.get(i).getOverlappingSlots().get(j));
//			}
//		}
		return true;
	}
}
