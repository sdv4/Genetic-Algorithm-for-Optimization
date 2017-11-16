/**
 * @author Kevin Naval
 * version: 0.1-11-11-2017
 */

package main;

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
	public void main() {
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
		}
		catch (FileNotFoundException e){
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Parses and adds the course slots entries from the text file to the corresponding array list.
	 * @param buf The buffered reader type to read the entries from.
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
			while(line!=null || !line.equals("")){
				entry = line.split(",");						// splits entry to a list ( 0=day, 1=start time, 2=course max, 3=course min)
				for(int i = 0; i <entry.length; i++) {			// trims trailing whitespaces for each entry
					entry[i].trim();
				}
				hm = entry[1].split(":");						// splits the time entry to hours and minutes: h1, m1 respectively
				h1 = Integer.parseInt(hm[0]);
				m1 = Integer.parseInt(hm[1]);
				
				//adjusts end time according to the day entry (+1hour for MWD, +1hour 30mins for TTh)				
				if(entry[0].equals("MON") || entry[0].equals("WED") || entry[0].equals("FRI")) {
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
	
	private void parseLSlots(BufferedReader buf) {
		
	}
	
	private void parseCourses(BufferedReader buf) {
		
	}
	
	private void parseLabs(BufferedReader buf) {
		
	}
}
