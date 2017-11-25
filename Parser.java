/**
 * @author Kevin Naval
 * version:
 * 0.1-11-11-2017
 * 0.2-16-11-2017
 * 
 * TODO
 * 	- modify parser to include one tutorial to many course number relation
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
		this.filepath	= filepath;
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
			}
			zipCourseLab();
			printLists();
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
				if(entry[0].equals("MO") || entry[0].equals("WE")) {
					h2 = h1+1;
					m2 = m1;
				}
				else if(entry[0].equals("FR")) {
					h2 = h1+2;
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
	        entry = courseLecture.split(" ");	        
	        courseList.add(new Course(courseName.trim(), Integer.parseInt(entry[1])));	        
	        line = buf.readLine();
	      }   
	    }
	    catch(IOException e){
	      e.printStackTrace();
	    }
	}
	
		private void printLists() {
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
			System.out.println(s.getDay()+" "+s.getMax()+" "+s.getMin()+" "+s.getStart().toString());
		}
		System.out.println("\nList of lab slots:");
		for(int i = 0; i<slotLList.size(); i++) {
			Slot s = slotLList.get(i);
			System.out.println(s.getDay()+" "+s.getMax()+" "+s.getMin()+" "+s.getStart().toString());
		}
		System.out.println("\nList of sorted course and lab list:");
		for(int i =0; i<courseLabList.size(); i++){
			System.out.println(courseLabList.get(i).getId()+": "+courseLabList.get(i).getName()+" Lecture: "+courseLabList.get(i).getLectureNumber()+"Not compatible: ");
			ArrayList<CourseLab> notCompatibleList = courseLabList.get(i).getNotCompatibleCoursesLabs();
			for (int y =0; y<notCompatibleList.size(); y++){
				System.out.println(notCompatibleList.get(y).getName());
			}
			System.out.println();
		}
	}
	
	private void zipCourseLab() {
		int cnum = -1;
		ArrayList<CourseLab> generic = new ArrayList<CourseLab>();
		
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
		
	}
	
		 //Check for lecture slots that overlap with other lecture slots
	private void populateOverlappingSlotsList(){
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
				entry[0] = entry[0].trim();
				//System.out.println(entry[0]);
				entry[1] = entry[1].trim();
				//System.out.println(entry[1]);
				//System.out.println();
	
				for (int i = 0; i<courseLabList.size(); i++){
					CourseLab aCourseLab = courseLabList.get(i);
					//System.out.println(aCourseLab.getName());
					if (entry[0].equals(aCourseLab.getName()))
					{
						courseLab1 = aCourseLab;
						System.out.print(entry[0]+", ");	
					}
					else if (entry[1].equals(aCourseLab.getName()))
					{
						courseLab2 = aCourseLab;
						System.out.print(entry[1]+", ");
					}
				}
				
				System.out.println();
				
				courseLab1NCList = courseLab1.getNotCompatibleCoursesLabs();
				courseLab2NCList = courseLab2.getNotCompatibleCoursesLabs();
				
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
	
	private void parseGeneralNotCompatible(){
		for (int i = 0; i < courseLabList.size(); i++){
			CourseLab aCourseLab1 = courseLabList.get(i);
			if (aCourseLab1.isCourse()){
				for (int j = 0; j < courseLabList.size(); j++){
					CourseLab aCourseLab2 = courseLabList.get(j);
					if (aCourseLab1.getGeneralName().equals(aCourseLab2.getGeneralName()) && aCourseLab2.isLab()){
						ArrayList<CourseLab> notCompatibleList1 = aCourseLab1.getNotCompatibleCoursesLabs();
						ArrayList<CourseLab> notCompatibleList2 = aCourseLab2.getNotCompatibleCoursesLabs();
						
						notCompatibleList1.add(aCourseLab2);
						notCompatibleList2.add(aCourseLab1);
					}
					//if (aCourseLab1.getName() ///general tutorial
				}
				
			}
		}
	}
}
