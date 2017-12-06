/**
 * @author Kevin Naval
 * version:
 * 0.1-11-11-2017
 * 0.2-16-11-2017
 * 0.3-25-11-2017
 *
 * TODO
 * 	- pending soft constraints
 */


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Parser {
	private Course 				course;
	private Lab 				lab;
	private Slot 				slot;
	private String				filepath;
	private ArrayList<Course>	courseList;
	private ArrayList<Lab>		labList;
	private ArrayList<Slot>		slotLList;
	private ArrayList<Slot>		slotCList;
	private ArrayList<CourseLab> courseLabList;
	private int []				partialAssign;
	private ArrayList<ArrayList<CourseLab>> sameCoursesList;
	private boolean validFileGiven;


	public Parser() {

	}

	/**
	 * The primary constructor. This initializes declared variables to null values.
	 * @param filepath The file path of the textfile that contains the entries.
	 */
	public Parser(String filepath) {
		this.course		= null;
		this.lab		= null;
		this.slot		= null;
		this.courseList	= new ArrayList<>();
		this.labList	= new ArrayList<>();
		this.slotLList	= new ArrayList<>();
		this.slotCList	= new ArrayList<>();
		this.courseLabList = new ArrayList<>();
		this.sameCoursesList = new ArrayList<>();
		this.filepath	= filepath;
		this.validFileGiven = true;
	}

	/**
	 * Starting method. This method is invoked after creating the Parser object
	 */
	public void start() {
		File file = new File(filepath);
		try{
			BufferedReader buf = new BufferedReader(new FileReader(file));
			String line = "";
			while((line=buf.readLine())!=null) {
				if(line.contains("Course slots:")){
					parseCSlots(buf);
				}
				if(line.contains("Lab slots:")) {
					parseLSlots(buf);
				}
				if(line.contains("Courses:")) {
					parseCourses(buf);
				}
				if(line.contains("Labs:")) {
					parseLabs(buf);
				}
				if(line.contains("Not compatible:")) {
					parseNotCompatible(buf);
				}
				if(line.contains("Unwanted:")){
					parseUnwanted(buf);
				}
				if(line.contains("Preferences:")){
					parsePreferences(buf);
				}
				if(line.contains("Pair:")){
					parsePair(buf);
				}
				if(line.contains("Partial assignments:")){
					parsePartialAssignments(buf);
				}
			}
			populateOverlappingSlotsList();
			parseGeneralNotCompatible();
			addTuesUnwanted();
			add813913Unwanted();
			findSameCourseLectures();
			//printLists();
			buf.close();
		}
		catch (FileNotFoundException e){
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//
	// GETTER METHODS
	//
	//

	/**
	 * Returns list of all CourseLabs
	 * @return ArrayList of type CourseLab
	 */
	public ArrayList<CourseLab> getCourseLabList(){
		return courseLabList;
	}

	/**
	 * Returns list of all Courses
	 * @return ArrayList of type Course
	 */
	public ArrayList<Course> getCourseList(){
		return courseList;
	}

	/**
	 * Returns list of all Labs
	 * @return ArrayList of type Lab
	 */
	public ArrayList<Lab> getLabList(){
		return labList;
	}

	/**
	 * Returns list of all Lab Slots
	 * @return ArrayList of type Slot
	 */
	public ArrayList<Slot> getLabSlotList(){
		return slotLList;
	}

	/**
	 * Returns list of all Course Slots
	 * @return ArrayList of type Slot
	 */
	public ArrayList<Slot> getCourseSlotList(){
		return slotCList;
	}

	/**
	 * Returns partial assignment vector
	 * @return int array
	 */
	public int [] getPartialAssign(){
		return partialAssign;
	}

	public ArrayList<ArrayList<CourseLab>> getSameCoursesList(){
		return sameCoursesList;
	}

	public boolean getValidFileGiven(){
		return validFileGiven;
	}


	//
	// PRIVATE METHODS
	//
	//

	/**
	 * Parses and adds the course slots entries from the text file to the corresponding array list.
	 * @param buf The buffered reader variable to read the entries from.
	 */
	private void parseCSlots(BufferedReader buf) {
		String line = "";
		String[] entry;
		String[] hm;
		int h1 = -1;
		int h2 = -1;
		int m1 = -1;
		int m2 = -1;

		try{
			line = buf.readLine();								// reads first entry to line
			while(!line.equals("")){
				entry = line.split(",");						// splits entry to a list ( 0=day, 1=start time, 2=course max, 3=course min)
				for(int i = 0; i <entry.length; i++) {			// trims trailing whitespaces for each entry
					entry[i] = entry[i].replaceAll(" ","");
				}
				hm = entry[1].split(":");						// splits the time entry to hours and minutes: h1, m1 respectively
				h1 = Integer.parseInt(hm[0]);
				m1 = Integer.parseInt(hm[1]);

				//adjusts end time according to the day entry (+1hour for MWD, +1hour 30mins for TTh)
				if(entry[0].equals("MO") || entry[0].equals("WE") || entry[0].equals("FR")) {
					h2 = h1+1;
					m2 = m1;
				}
				else {
					h2 = h1+1;
					m2 = m1+30;
					if(m2>=60) {
						h2++;
						m2 = m2-60;
					}
				}
				slotCList.add(new Slot(true, LocalTime.of(h1,m1), LocalTime.of(h2, m2), entry[0], Integer.parseInt(entry[2]), Integer.parseInt(entry[3])));
				line = buf.readLine();							// reads next entry to line
			}
		}
		catch (IOException e){
			e.printStackTrace();
		}

	}

	/**
	 * Parse and add the lab slot entries from the text file to the corresponding array list.
	 * @param buf The buffered reader to read the entries.
	 */
	private void parseLSlots(BufferedReader buf) {
		String line = "";
		String[] entry;
		String[] hm;
		int h1 = -1;
		int h2 = -1;
		int m1 = -1;
		int m2 = -1;

		try{
			line = buf.readLine();								// reads first entry to line
			while(!line.equals("")){
				entry = line.split(",");						// splits entry to a list ( 0=day, 1=start time, 2=course max, 3=course min)
				for(int i = 0; i <entry.length; i++) {			// trims trailing whitespaces for each entry
					entry[i] = entry[i].replaceAll(" ","");
				}
				hm = entry[1].split(":");						// splits the time entry to hours and minutes: h1, m1 respectively
				hm[0] = hm[0].replaceAll(" ","");
				hm[1] = hm[1].replaceAll(" ","");
				h1 = Integer.parseInt(hm[0]);
				m1 = Integer.parseInt(hm[1]);

				//adjusts end time according to the day entry (+1hour for MWD, +1hour 30mins for TTh)
				if(entry[0].equals("MO") || entry[0].equals("WE") ||entry[0].equals("TU")) {
					h2 = h1+1;
					m2 = m1;
				}
				else if(entry[0].equals("FR")) {
					h2 = h1+2;
					m2 = m1;
				}

				slotLList.add(new Slot(false, LocalTime.of(h1,m1), LocalTime.of(h2, m2), entry[0], Integer.parseInt(entry[2]), Integer.parseInt(entry[3])));
				line = buf.readLine();							// reads next entry to line
			}
		}
		catch (IOException e){
			e.printStackTrace();
		}

	}
	/**
	 * Parse and add the course entries from the text file to the corresponding array list.
	 * @param buf The buffered reader to read the entries.
	 */
	private void parseCourses(BufferedReader buf) {
		String line = "";
	    String[] entry;
	    String courseName = "";
	    String courseLecture = "";

	    try{
	      line = buf.readLine();				// reada first entry to line
	      while(!line.equals("")){
	        int lecIndex = line.indexOf("LEC");
	        courseName = line;
	        courseLecture = line.substring(lecIndex);
	        courseLecture = courseLecture.replaceAll("[ ]+"," ");
	        courseName = courseName.replaceAll("[ ]+"," ");
	        entry = courseLecture.split(" ");
	        courseList.add(new Course(courseName.trim(), Integer.parseInt(entry[1])));
	        line = buf.readLine();
	      }
	    }
	    catch(IOException e){
	      e.printStackTrace();
	    }
	}

	private void parseLabs(BufferedReader buf) {
		String line = "";

		try {
			line = buf.readLine();							// line is the full lab name
			line.trim();
			while(!line.equals("")){
				line = line.replaceAll("[ ]+", " ");		// replaces multiple whitespace to a single whitespace
				int index = -1;
				if(line.contains("TUT"))					// stores index of TUT or LAB in line entry
					index = line.indexOf("TUT");
				else if(line.contains("LAB"))
					index = line.indexOf("LAB");
				if(index >-1) {								// if there is an index of tut or lab, record lab number and add to lab list
					int num = Integer.parseInt(line.substring(index+4));
					labList.add(new Lab(line, num, null));
				}
				line = buf.readLine();
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		catch(NumberFormatException e) {
			e.printStackTrace();
		}
		zipCourseLab();


	}

	/*
	 * parse through the Not Compatible list, and add the courseLabs that are not compatible to each other's notCompatibleLists
	 * @param buffered reader
	 */

	private void parseNotCompatible(BufferedReader buf){
		String[] entry;
		String line = "";
		CourseLab courseLab1 = courseLabList.get(0);
		CourseLab courseLab2 = courseLabList.get(1);
		ArrayList<CourseLab> courseLab1NCList = courseLab1.getNotCompatibleCoursesLabs();
		ArrayList<CourseLab> courseLab2NCList = courseLab2.getNotCompatibleCoursesLabs();
		try {
			line = buf.readLine();
			//System.out.println("start");
			while(!line.equals("")) {
				entry = line.split(",");
				//remove extra spaces
				for (int i = 0; i< entry.length; i++){
					entry[i] = entry[i].trim();
					entry[i] = entry[i].replaceAll("[ ]+"," ");
					//System.out.println(entry[i]);
				}
				//System.out.println();
				//find the two courseLabs specified in the statement

				for (int i = 0; i<courseLabList.size(); i++){
					CourseLab aCourseLab = courseLabList.get(i);
					//System.out.println(aCourseLab.getName());
					if (entry[0].equals(aCourseLab.getName()))
					{
						courseLab1 = aCourseLab;
						//System.out.print(entry[0]+", ");
					}
					else if (entry[1].equals(aCourseLab.getName()))
					{
						courseLab2 = aCourseLab;
						//System.out.print(entry[1]+", ");
					}
				}

				//System.out.println();
				//get the notCompatibleLists for the two courseLabs in the statement
				courseLab1NCList = courseLab1.getNotCompatibleCoursesLabs();
				courseLab2NCList = courseLab2.getNotCompatibleCoursesLabs();
				//for both notCompatibleLists, add the courseLab to the other's list

				courseLab1NCList.add(courseLab2);
				courseLab2NCList.add(courseLab1);

				line = buf.readLine();
				//System.out.println(line);
			}
		}

		catch(IOException e) {
			e.printStackTrace();
		}

	}

	/*
	 * Parse unwanted list
	 * @param bufferedreader
	 */


	private void parseUnwanted(BufferedReader buf){
		String line = "";
		String[] entry;
		String[] hm;
		try {
			line = buf.readLine();
			//System.out.println(line);
			while(!line.equals("")) {
				//delimit statement
				entry = line.split(",");
				//remove extra spaces
				for (int i = 0; i< entry.length; i++){					//entry[0] = courseLab name, entry[1] = day
					entry[i] = entry[i].trim();							//entry[2] = time
					entry[i] = entry[i].replaceAll("[ ]+"," ");
				}
				//parse time and cast to LocalTime
				hm = entry[2].split(":");
				int h1 = Integer.parseInt(hm[0]);
				int m1 = Integer.parseInt(hm[1]);
				LocalTime time = LocalTime.of(h1, m1);
			//	System.out.println("LocalTime: "+time.toString());


				for (int i = 0; i < courseLabList.size(); i++){			//find the courseLab with the exact same name
					CourseLab aCourseLab = courseLabList.get(i);
					ArrayList<Integer> aCourseLabUnwantedList = aCourseLab.getUnWantedList();
					if (entry[0].equals(aCourseLab.getName())){
						//System.out.println("entry[0]: "+entry[0]);
						if (aCourseLab.isCourse()){						//if the courseLab is a course
							//System.out.println(entry[1]+" "+entry[2]);
							for (int j= 0; j<slotCList.size(); j++){	//find slot that matches time and day given
								Slot aSlot = slotCList.get(j);
								//System.out.println(aSlot.getDay()+" "+aSlot.getStart().toString());
								if (aSlot.getDay().equals(entry[1]) && aSlot.getStart().toString().equals(time.toString())){ //if the slot is found, add the slot id to the unwantedIdsList for the courseLab
									aCourseLabUnwantedList.add(aSlot.getId());
									//System.out.println(entry[1]+" "+entry[2]);
									break; //break from searching for slot
								}
							}
						}
						else{											//if the courseLab is a lab
							for (int j= 0; j<slotLList.size(); j++){
								Slot aSlot = slotLList.get(j);
								if (aSlot.getDay().equals(entry[1]) && aSlot.getStart().toString().equals(time.toString())){
									aCourseLabUnwantedList.add(aSlot.getId());
									//System.out.println(entry[1]+" "+entry[2]);
									break;	//
								}
							}
						}
						break;	//break from searching for courseLab
					}
				}
				line = buf.readLine();
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * parse Preference list
	 * @param bufferedreader
	 */
	private void parsePreferences(BufferedReader buf){
		String line ="";
		String[] entry;
		String[] hm;
		try {
			line = buf.readLine();
			while(!line.equals("")) {
				//System.out.println(line);
				entry = line.split(",");								//delimit statement
				//remove extra spaces
				for (int i = 0; i< entry.length; i++){					//split line by ",", entry[0] = day
					entry[i] = entry[i].trim();							//entry[1] = time, entry[2] = course
					entry[i] = entry[i].replaceAll("[ ]+"," ");			//entry[3] = preference value
					//System.out.println(entry[i]);
				}

				hm = entry[1].split(":");								//cast given time to LocalTime
				int h1 = Integer.parseInt(hm[0]);
				int m1 = Integer.parseInt(hm[1]);
				LocalTime time = LocalTime.of(h1,m1);

				int prefVal = Integer.parseInt(entry[3]);				//cast given prefVal to integer

				for (int i = 0; i < courseLabList.size(); i++){			//find the courseLab with the exact same name
					CourseLab aCourseLab = courseLabList.get(i);
					if (entry[2].equals(aCourseLab.getName())){
						//System.out.println("entry[0]: "+entry[0]);
						if (aCourseLab.isCourse()){						//if the courseLab is a course
							//System.out.println(entry[1]+" "+entry[2]);
							for (int j= 0; j<slotCList.size(); j++){	//find slot that matches given day and time
								Slot aSlot = slotCList.get(j);
								//System.out.println(aSlot.getDay()+" "+aSlot.getStart().toString());
								if (aSlot.getDay().equals(entry[0]) && aSlot.getStart().toString().equals(time.toString())){ //if the slot is found, add the slotPref object to the courseLab's slotPrefList
									ArrayList<slotPref> slotPrefList = aCourseLab.getSlotPrefList();
									slotPrefList.add(new slotPref(aSlot.getId(), prefVal));
									//System.out.println(entry[1]+" "+entry[2]);
									break; //break from searching for slot
								}
							}
						}
						else{											//if the courseLab is a lab
							for (int j= 0; j<slotLList.size(); j++){
								//System.out.println(j);
								Slot aSlot = slotLList.get(j);
								if (aSlot.getDay().equals(entry[0]) && aSlot.getStart().toString().equals(time.toString())){
									ArrayList<slotPref> slotPrefList = aCourseLab.getSlotPrefList();
									slotPrefList.add(new slotPref(aSlot.getId(), prefVal));
									break;	//break from searching for slot
								}
							}
						}
						break;	//break from searching for courseLab
					}
				}
				line = buf.readLine();
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	/*
	 * parse Pair
	 * @param BufferedReader
	 */
	private void parsePair(BufferedReader buf){
		String[] entry;
		String line = "";
		//System.out.println(courseLabList.size());
		CourseLab courseLab1 = courseLabList.get(0);
		CourseLab courseLab2 = courseLabList.get(1);
		ArrayList<CourseLab> courseLab1PairList = courseLab1.getPairList();
		try {
			line = buf.readLine();
			//System.out.println("start");
			while(!line.equals("")) {
				entry = line.split(",");
				for (int i = 0; i< entry.length; i++){
					entry[i] = entry[i].trim();							//entry[0] = a courseLab name
					entry[i] = entry[i].replaceAll("[ ]+"," ");			//entry[1] = a courseLab name
					//System.out.println(entry[i]);
				}
				//System.out.println();

				for (int i = 0; i<courseLabList.size(); i++){			//find the courseLab objects with the corresponding courseLab names
					CourseLab aCourseLab = courseLabList.get(i);
					//System.out.println(aCourseLab.getName());
					if (entry[0].equals(aCourseLab.getName()))
					{
						courseLab1 = aCourseLab;
						//System.out.print(entry[0]+", ");
					}
					else if (entry[1].equals(aCourseLab.getName()))
					{
						courseLab2 = aCourseLab;
						//System.out.print(entry[1]+", ");
					}
				}

				//System.out.println();

				courseLab1PairList = courseLab1.getPairList();			//add one of the courseLabs to the other's pairList

				courseLab1PairList.add(courseLab2);

				line = buf.readLine();
				//System.out.println(line);
			}
		}

		catch(IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * parse Partial Assignment to set the partialAssign vector
	 * @param bufferedreader
	 */

	private void parsePartialAssignments(BufferedReader buf){
		String line = "";
		String[] entry;
		String[] hm;
		boolean validPartialAssign = true;
		try {
			line = buf.readLine();

			while(line != null && !line.equals("")) {
				//System.out.println(line);

				entry = line.split(",");
				for (int i = 0; i< entry.length; i++){					//split line by ",", entry[0] = course name
					entry[i] = entry[i].trim();							//entry[1] = day, entry[2] = time
					entry[i] = entry[i].replaceAll("[ ]+"," ");
					//System.out.println(entry[i]);
				}
				hm = entry[2].split(":");
				int h1 = Integer.parseInt(hm[0]);
				int m1 = Integer.parseInt(hm[1]);
				LocalTime time = LocalTime.of(h1, m1);
				//System.out.println("LocalTime: "+time.toString()); 		//cast time to LocalTime for comparison


				for (int i = 0; i < courseLabList.size(); i++){			//find the courseLab with the exact same name
					CourseLab aCourseLab = courseLabList.get(i);
					if (entry[0].equals(aCourseLab.getName())){
						//System.out.println("entry[0]: "+entry[0]);
						if (aCourseLab.isCourse()){						//if the courseLab is a course
							//System.out.println(entry[1]+" "+entry[2]);
							boolean foundSlot = false;
							for (int j= 0; j<slotCList.size(); j++){	//find slot that matches entry[1] and time.ToString()
								Slot aSlot = slotCList.get(j);
								//System.out.println(aSlot.getDay()+" "+aSlot.getStart().toString());
								if (aSlot.getDay().equals(entry[1]) && aSlot.getStart().toString().equals(time.toString())){ //if the slot is found, add the slot id to the unwantedIdsList for the courseLab
									foundSlot = true;
									partialAssign[i] = aSlot.getId();
									//System.out.println(entry[1]+" "+entry[2]);
									break; //break from searching for slot
								}
							}
							if (foundSlot == false){
								validPartialAssign = false;
							}
						}
						else{
							boolean foundSlot = false;											//if the courseLab is a lab
							for (int j= 0; j<slotLList.size(); j++){
							//	System.out.println(j);
								Slot aSlot = slotLList.get(j);
								if (aSlot.getDay().equals(entry[1]) && aSlot.getStart().toString().equals(time.toString())){
									foundSlot = true;
									partialAssign[i] = aSlot.getId();
									//System.out.println(entry[1]+" "+entry[2]);
									break;	//
								}
							}

							if (foundSlot == false){
								validPartialAssign = false;
							}
						}
						break;	//break from searching for courseLab
					}
				}
				line = buf.readLine();
				if (line == null)
					break;
			}
			//System.out.print("partialAssign: ");
	//		for (int i = 0; i<courseLabList.size(); i++){
		//		System.out.print(partialAssign[i]);
			//}
			//System.out.println();

			if (validPartialAssign == false){
				System.out.println("partial assignment list was invalid");
				validFileGiven = false;
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}

	public void printLists() {
		System.out.println("List of courses:");
		for(int i = 0; i<courseList.size(); i++) {
			System.out.println(courseList.get(i).name()+" LEC: "+courseList.get(i).getLecNum());
		}

		System.out.println("\nList of labs:");
		for(int i = 0; i<labList.size(); i++) {
			System.out.println(labList.get(i).name()+" LAB: "+labList.get(i).getLabNum());
		}

		System.out.println("\nList of course slots:");
		for(int i = 0; i<slotCList.size(); i++) {
			Slot s = slotCList.get(i);
			System.out.print(s.getId()+": "+s.getDay()+" "+s.getMax()+" "+s.getMin()+" "+s.getStart().toString()+" "+s.getEnd()+" Overlapping slots: ");
			ArrayList<Integer> overlappingSlots = s.getOverlappingSlots();
			for (int j = 0; j<overlappingSlots.size(); j++){
				System.out.print(overlappingSlots.get(j)+" ");
			}
			System.out.println();
		}
		System.out.println("\nList of lab slots:");
		for(int i = 0; i<slotLList.size(); i++) {
			Slot s = slotLList.get(i);
			System.out.print(s.getId()+": "+s.getDay()+" "+s.getMax()+" "+s.getMin()+" "+s.getStart().toString()+" "+s.getEnd()+" Overlapping slots: ");
			ArrayList<Integer> overlappingSlots = s.getOverlappingSlots();
			for (int j = 0; j<overlappingSlots.size(); j++){
				System.out.print(overlappingSlots.get(j)+" ");
			}
			System.out.println();
		}
		System.out.println("\nList of sorted course and lab list:");
		for(int i =0; i<courseLabList.size(); i++){
			System.out.println(courseLabList.get(i).getId()+": "+courseLabList.get(i).getName()+" Lecture: "+courseLabList.get(i).getLectureNumber()+" Not compatible: ");
			ArrayList<CourseLab> notCompatibleList = courseLabList.get(i).getNotCompatibleCoursesLabs();
			for (int y =0; y<notCompatibleList.size(); y++){
				System.out.println(notCompatibleList.get(y).getName());
			}
			System.out.print("Unwanted: ");
			for (int j= 0; j<courseLabList.get(i).getUnWantedList().size();j++){
				System.out.println(courseLabList.get(i).getUnWantedList().get(j)+" ");
			}
			System.out.println();
			System.out.println("Preferences: ");
			for (int j = 0; j<courseLabList.get(i).getSlotPrefList().size();j++){
				ArrayList<slotPref> slotPrefList = courseLabList.get(i).getSlotPrefList();
				System.out.println(slotPrefList.get(j).getSlotId()+" "+slotPrefList.get(j).getPrefVal());
			}
			System.out.println();
			System.out.println("Pair: ");
			for (int j = 0; j<courseLabList.get(i).getPairList().size(); j++){
				ArrayList<CourseLab> pairList = courseLabList.get(i).getPairList();
				System.out.println(pairList.get(j).getName());
			}

			System.out.println();
		}
	}

	private void zipCourseLab() {
		int cnum = -1;

		for(int i = 0; i<courseList.size(); i++) {		// traverse course list
			Course c = courseList.get(i);
			String[] ss = c.name().split(" ");
			cnum = Integer.parseInt(ss[1]);
			courseLabList.add(new CourseLab(c.name(), c.getId(), c.getLecNum(), -1, true, false, null));	// add course to entry
			int cindex = courseLabList.size()-1;		// index of latest course type entry

			for(int j = 0; j<labList.size(); j++) {		// traverse lab list
				Lab l = labList.get(j);
				if(l.name().contains(c.name())) {		// lab is associated with the current lecture num
					courseLabList.add(new CourseLab(l.name(), l.getId(), c.getLecNum(), l.getLabNum(), false, true, courseLabList.get(cindex)));	// add lab to entry
				}
				else if(!l.name().contains("LEC") && l.name().contains(Integer.toString(cnum))) {	// lab is NOT associated with current lecture num, but is associated with course num = GENERIC
					if((i+1)!=courseList.size()) {				// if not end of list
						Course c2 = courseList.get(i+1);
						String[] ss2 = c2.name().split(" ");
						int cnumnxt = Integer.parseInt(ss2[1]);
						if(cnum!=cnumnxt) {
							String cpar = ss[0]+" "+ss[1];
							if(l.name().contains(cpar))
								courseLabList.add(new CourseLab(l.name(), l.getId(), -1, l.getLabNum(), false, true, null));
						}
					}
					else {										// handling end of course list entry
						String cpar = ss[0]+" "+ss[1];
						if(l.name().contains(cpar))
							courseLabList.add(new CourseLab(l.name(), l.getId(), -1, l.getLabNum(), false, true, null));
					}
				}
			}
		}
		this.partialAssign = new int[courseLabList.size()];
	}

	/*
	 * Find the Tuesday 11:00 course slot (if it exists), and for each course,
	 * add the slotID for Tuesday 11:00 to its unwantedList
	 */

	private void addTuesUnwanted(){
		int tuesdaySlotId = 0;
		for (int i = 0; i<slotCList.size(); i++){
			Slot aSlot = slotCList.get(i);
			if (aSlot.getDay().equals("TU") && aSlot.getStart().toString().equals("11:00")){
				tuesdaySlotId = aSlot.getId();
			}
		}
		if (tuesdaySlotId > 0){
			for (int i = 0; i<courseLabList.size(); i++){
				CourseLab aCourseLab = courseLabList.get(i);
				if (aCourseLab.isCourse()){
					ArrayList<Integer> unwantedList = aCourseLab.getUnWantedList();
					unwantedList.add(tuesdaySlotId);
				}
			}
		}
	}

	private void add813913Unwanted(){
		//CPSC 813 constraint
		boolean cpsc313exists = false;
		boolean cpsc413exists = false;
		boolean labSlotTU18exists = false;
		for (int i = 0; i<slotCList.size(); i++){
			Slot aSlot = slotCList.get(i);
			//find the course slots that overlap with the time slot 18:00-19:00 on Tuesdays
			if ((aSlot.getEnd().toString().equals("18:30") || aSlot.getStart().toString().equals("18:30")) && aSlot.getDay().equals("TU"))
			{
				//find the CPSC 313/413 Lec sections and add the slotId to their unwantedList
				for (int j = 0; j<courseLabList.size(); j++){
					CourseLab aCourseLab = courseLabList.get(j);
					if (aCourseLab.isCourse() && (aCourseLab.getGeneralName().equals("CPSC 313") || aCourseLab.getGeneralName().equals("CPSC 413"))){
						ArrayList<Integer> unwantedList = aCourseLab.getUnWantedList();
						unwantedList.add(aSlot.getId());
						//also add the slotId to the Lec sections that CPSC Lec sections cannot overlap with
						ArrayList<CourseLab> incompatibleList = aCourseLab.getNotCompatibleCoursesLabs();
						for (int k = 0; k< incompatibleList.size(); k++){
							CourseLab bCourseLab = incompatibleList.get(k);
							if (bCourseLab.isCourse()){
								ArrayList<Integer> bUnwantedList = bCourseLab.getUnWantedList();
								bUnwantedList.add(aSlot.getId());
							}
						}
					}
				}
			}//add lab slot TU at 18:00 to unwantedLists of labs
		}
		for (int i=0; i<slotLList.size(); i++){
			Slot aSlot = slotLList.get(i);
			if (aSlot.getStart().toString().equals("18:00") && aSlot.getDay().equals("TU")){
				//find the CPSC 313/413 labs and add the slotId to their unwantedList
				labSlotTU18exists = true;
				//System.out.println("found");
				for (int j=0; j<courseLabList.size(); j++){
					CourseLab aCourseLab = courseLabList.get(j);
					if (aCourseLab.isLab() && (aCourseLab.getGeneralName().equals("CPSC 313") || aCourseLab.getGeneralName().equals("CPSC 413"))){
						ArrayList<Integer> unwantedList = aCourseLab.getUnWantedList();
						unwantedList.add(aSlot.getId());
					}
				}

			}
		}

		for (int i=0; i<courseLabList.size(); i++){
			CourseLab aCourseLab = courseLabList.get(i);
			if (aCourseLab.isCourse()){
				if (aCourseLab.getGeneralName().equals("CPSC 313")){
					cpsc313exists = true;
				}
				if (aCourseLab.getGeneralName().equals("CPSC 413")){
					cpsc413exists = true;
				}
			}
		}

		if (labSlotTU18exists){
			for (int i = 0; i<slotLList.size(); i++){
				Slot aSlot = slotLList.get(i);
				if (aSlot.getStart().toString().equals("18:00") && aSlot.getDay().equals("TU")){
					int slotMax = aSlot.getMax();
					int slotMin = aSlot.getMin();
					if (cpsc313exists){
						if (slotMin != 0){
							slotMin--;
						}
						if (slotMax != 0){
							slotMax--;
						}
						else{
							System.out.println("Can't schedule CPSC 813. Tu 18:00 lab is already full. Exiting");
							System.exit(0);
						}
					}
					if (cpsc413exists){
						if (slotMin != 0){
							slotMin--;
						}
						if (slotMax != 0){
							slotMax--;
						}
						else{
							System.out.println("Can't schedule CPSC 913. Tu 18:00 lab is already full. Exiting");
							System.exit(0);
						}
					}
					aSlot.setMin(slotMin);
					aSlot.setMax(slotMax);
				}
			}
		}

		if ((cpsc313exists || cpsc413exists) && !labSlotTU18exists){
			System.out.println("CPSC 313/413 exist(s) but not lab slot TU at 18:00!");
			validFileGiven = false;
		}
	}

	/*
	 * Find all the sections of the same course and group them together into sameCoursesList.
	 * sameCoursesList is used to check for the soft constraint where different sections should be schedule into different slots
	 */
	private void findSameCourseLectures(){
		//find all the unique course names for lectures
		ArrayList<String> uniqueCourseNames = new ArrayList<>();
		for (int i = 0; i<courseLabList.size(); i++){
			CourseLab aCourseLab = courseLabList.get(i);
			if (!uniqueCourseNames.contains(aCourseLab.getGeneralName()) && aCourseLab.isCourse())
			{
				uniqueCourseNames.add(aCourseLab.getGeneralName());
				//System.out.println("Added: "+aCourseLab.getGeneralName());
			}
		}

		//for each unique course name, compile all the lectures with the same course name into one list, and add to sameCoursesList
		for (int i = 0;i<uniqueCourseNames.size(); i++){
			ArrayList<CourseLab> sameCourses = new ArrayList<>();
			for (int j=0; j<courseLabList.size(); j++){
				CourseLab aCourseLab = courseLabList.get(j);
				if (uniqueCourseNames.get(i).equals(aCourseLab.getGeneralName()) && aCourseLab.isCourse()){
					sameCourses.add(aCourseLab);
				}
			}
			sameCoursesList.add(sameCourses);
		}

		//print results
//		for (int i = 0; i<sameCoursesList.size(); i++){
//			ArrayList<CourseLab> sameCourses = sameCoursesList.get(i);
//			System.out.println("Group: ");
//			for (int j = 0; j<sameCourses.size(); j++){
//				System.out.println(sameCourses.get(j).getName());
//			}
//		}


	}

	/*
	 * This function populates each slot's overlappingSlots list.
	 * It is used by constr to determine if two slots overlap
	 */

	private void populateOverlappingSlotsList(){
	//Check for lecture slots that overlap with other lecture slots
		for (int x = 0; x < slotLList.size(); x++)
		{
			Slot aSlot1 = slotLList.get(x);
			ArrayList<Integer> overlapList1 = aSlot1.getOverlappingSlots();
			LocalTime aSlot1Start = aSlot1.getStart();
			LocalTime aSlot1End = aSlot1.getEnd();
			String aSlot1Day = aSlot1.getDay();
			for (int y = x+1; y < slotLList.size();y++)
			{
				Slot aSlot2 = slotLList.get(y);
				ArrayList<Integer> overlapList2 = aSlot2.getOverlappingSlots();
				LocalTime aSlot2Start = aSlot2.getStart();
				LocalTime aSlot2End = aSlot2.getEnd();
				String aSlot2Day = aSlot2.getDay();
				int aSlot1Id = aSlot1.getId();
				int aSlot2Id = aSlot2.getId();

				if (aSlot1Day.equals(aSlot2Day) == true)
				{
					if (aSlot1Start.equals(aSlot2Start) == true || aSlot1End.equals(aSlot2End) == true)
					{
						overlapList1.add(aSlot2Id);
						overlapList2.add(aSlot1Id);
					}

					else if (aSlot1Start.isBefore(aSlot2Start) == true && aSlot1End.isAfter(aSlot2Start) == true)
					{
							overlapList1.add(aSlot2Id);
							overlapList2.add(aSlot1Id);
					}
					else if (aSlot2Start.isBefore(aSlot1Start) == true && aSlot2End.isAfter(aSlot1Start) == true)
					{
							overlapList1.add(aSlot2Id);
							overlapList2.add(aSlot1Id);
					}
				}
			}
		}

		//Check for course slots overlapping with course slots
		for (int x = 0; x < slotCList.size(); x++)
		{
			Slot aSlot1 = slotCList.get(x);
			ArrayList<Integer> overlapList1 = aSlot1.getOverlappingSlots();
			LocalTime aSlot1Start = aSlot1.getStart();
			LocalTime aSlot1End = aSlot1.getEnd();
			String aSlot1Day = aSlot1.getDay();
			for (int y = x+1; y < slotCList.size();y++)
			{
				Slot aSlot2 = slotCList.get(y);
				ArrayList<Integer> overlapList2 = aSlot2.getOverlappingSlots();
				LocalTime aSlot2Start = aSlot2.getStart();
				LocalTime aSlot2End = aSlot2.getEnd();
				String aSlot2Day = aSlot2.getDay();
				int aSlot1Id = aSlot1.getId();
				int aSlot2Id = aSlot2.getId();


				if (aSlot1Day.equals(aSlot2Day))
				{
					if (aSlot1Start.equals(aSlot2Start) == true || aSlot1End.equals(aSlot2End) == true)
					{
						overlapList1.add(aSlot2Id);
						overlapList2.add(aSlot1Id);
					}

					else if (aSlot1Start.isBefore(aSlot2Start) == true && aSlot1End.isAfter(aSlot2Start) == true)
					{
							overlapList1.add(aSlot2Id);
							overlapList2.add(aSlot1Id);
					}
					else if (aSlot2Start.isBefore(aSlot1Start) == true && aSlot2End.isAfter(aSlot1Start) == true)
					{
							overlapList1.add(aSlot2Id);
							overlapList2.add(aSlot1Id);
					}
				}
			}
		}

		//Check for lecture slots overlapping with course slots
		for (int x = 0; x < slotLList.size(); x++)
		{
			Slot aSlot1 = slotLList.get(x);
			ArrayList<Integer> overlapList1 = aSlot1.getOverlappingSlots();
			LocalTime aSlot1Start = aSlot1.getStart();
			LocalTime aSlot1End = aSlot1.getEnd();
			String aSlot1Day = aSlot1.getDay();
			for (int y = 0; y < slotCList.size();y++)
			{
				Slot aSlot2 = slotCList.get(y);
				ArrayList<Integer> overlapList2 = aSlot2.getOverlappingSlots();
				LocalTime aSlot2Start = aSlot2.getStart();
				LocalTime aSlot2End = aSlot2.getEnd();
				String aSlot2Day = aSlot2.getDay();
				int aSlot1Id = aSlot1.getId();
				int aSlot2Id = aSlot2.getId();


				if (aSlot1Day.equals(aSlot2Day))
				{
					if (aSlot1Start.equals(aSlot2Start) == true || aSlot1End.equals(aSlot2End) == true)
					{
						overlapList1.add(aSlot2Id);
						overlapList2.add(aSlot1Id);
					}

					else if (aSlot1Start.isBefore(aSlot2Start) == true && aSlot1End.isAfter(aSlot2Start) == true)
					{
							overlapList1.add(aSlot2Id);
							overlapList2.add(aSlot1Id);
					}
					else if (aSlot2Start.isBefore(aSlot1Start) == true && aSlot2End.isAfter(aSlot1Start) == true)
					{
							overlapList1.add(aSlot2Id);
							overlapList2.add(aSlot1Id);
					}
				}
			}
		}
	}


	private void parseGeneralNotCompatible(){
		for (int i = 0; i < courseLabList.size(); i++){
			CourseLab aCourseLab1 = courseLabList.get(i);
			if (aCourseLab1.isCourse()){
				for (int j = 0; j < courseLabList.size(); j++){
					CourseLab aCourseLab2 = courseLabList.get(j);
					//General tutorial incompatibility
					if (aCourseLab1.getGeneralName().equals(aCourseLab2.getGeneralName()) && aCourseLab2.isLab() && aCourseLab2.getAssociatedLecture() == null){
						ArrayList<CourseLab> notCompatibleList1 = aCourseLab1.getNotCompatibleCoursesLabs();
						ArrayList<CourseLab> notCompatibleList2 = aCourseLab2.getNotCompatibleCoursesLabs();

						notCompatibleList1.add(aCourseLab2);
						notCompatibleList2.add(aCourseLab1);
					}
					//Specific lecture tutorial incompatibility
					else if (aCourseLab1.getName().equals(aCourseLab2.getSpecificLecture()) && aCourseLab1 != aCourseLab2){
						ArrayList<CourseLab> notCompatibleList1 = aCourseLab1.getNotCompatibleCoursesLabs();
						ArrayList<CourseLab> notCompatibleList2 = aCourseLab2.getNotCompatibleCoursesLabs();

						notCompatibleList1.add(aCourseLab2);
						notCompatibleList2.add(aCourseLab1);
					}
				}

			}
		}
	}
}
