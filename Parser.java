package main;

/**
 * @author Kevin Naval
 * version: 0.1-11-11-2017
 */


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;

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
		this.filepath	= filepath;
	}
	
	/**
	 * The main parsing function. Parses the textfile into its appropriate data structures.
	 */
	public static void main(String[]args){
		Parser aParser = new Parser("ShortExample.txt");
		aParser.start();
	}
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
			printLists();
		}
		catch (FileNotFoundException e){
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
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
				slotLList.add(new Slot(true, LocalTime.of(h1,m1), LocalTime.of(h2, m2), entry[0], Integer.parseInt(entry[2]), Integer.parseInt(entry[3])));
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
	        courseName = line.substring(0,lecIndex);
	        courseName = courseName.replaceAll("[ ]+"," ");
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
	
	/**
	 * Parse and add the course entries from the text file to the corresponding array list.
	 * @param buf The buffered reader to read the entries.
	 */
	private void parseLabs(BufferedReader buf) {
		String line = "";
		String[] entry;
		String labName = "";
		String labNumberStr = "";
		String labLectureFull = "";
		String labLecture = "";
		int index = -1;
		int lecNum = -1;
		
		try {
			line = buf.readLine();
			while(!line.equals("")) {
				line = line.replaceAll("[ ]+", " ");
				labName = line;
				if((index=line.indexOf("TUT"))!=-1) {			// stores full lecture name to labLectureFull
					labLectureFull = line.substring(0, index);
					labNumberStr = line.substring(index);
				}
				else if((index = line.indexOf("LAB"))!=-1) {
					labLectureFull = line.substring(0, index);
					labNumberStr = line.substring(index);
				}
				entry = labLectureFull.split(" ");
				if(entry.length>2) {						// stores lecture number to lecNum
					lecNum = Integer.parseInt(entry[3]);	
				}
				labLecture = entry[0]+" "+entry[1];			// stores lecture name to labLecture
				Course c = null;

				for(int i = 0; i<courseList.size(); i++) {			// finds matching course name and lecture number from course list, store a match to variable of type Course 
					if(courseList.get(i).name().equals(labLecture) && courseList.get(i).getLecNum()==lecNum) {
						c = courseList.get(i);
					}
				}
				if(c!=null) {			// if a course is found, create lab object and add to list
					entry = labNumberStr.split(" ");
					labList.add(new Lab(labName, Integer.parseInt(entry[1]), c));
					
					//System.out.println(labName + " added");
				}				
				line = buf.readLine();
			}
			
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private void zipCourseLab() {
		ArrayList<Object> olist = new ArrayList<>();
		olist.addAll(courseList);
		olist.addAll(labList);
		for(int i = 0; i<olist.size(); i++){
			
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
	}
}
