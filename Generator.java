/**
 * This class create randomly generated instance files for testing. 
*/
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class Generator {
	static PrintWriter outfile = null;
	static int course_capacity_count = 0;
	static int lab_capacity_count = 0;
	public static void main(String[] args) {
		
		ArrayList<String>course_slots = new ArrayList<String>(Arrays.asList(
				"MO,  8:00, ", 
				"MO,  9:00, ",
				"MO, 10:00, ",
				"MO, 11:00, ",
				"MO, 12:00, ",
				"MO, 13:00, ",
				"MO, 14:00, ",
				"MO, 15:00, ",
				"MO, 16:00, ",
				"MO, 17:00, ",
				"MO, 18:00, ",
				"MO, 19:00, ",
				"MO, 20:00, ",
				"TU,  8:00, ",
				"TU,  9:30, ",
				"TU, 11:00, ",
				"TU, 12:30, ",
				"TU, 14:00, ",
				"TU, 15:30, ",
				"TU, 17:00, ",
				"TU, 18:30, "	
				));
		
		ArrayList<String>lab_slots = new ArrayList<String>(Arrays.asList(
				"MO,  8:00, ", 
				"MO,  9:00, ",
				"MO, 10:00, ",
				"MO, 11:00, ",
				"MO, 12:00, ",
				"MO, 13:00, ",
				"MO, 14:00, ",
				"MO, 15:00, ",
				"MO, 16:00, ",
				"MO, 17:00, ",
				"MO, 18:00, ",
				"MO, 19:00, ",
				"MO, 20:00, ",
				"TU,  8:00, ",
				"TU,  9:00, ",
				"TU, 10:00, ",
				"TU, 11:00, ",
				"TU, 12:00, ",
				"TU, 13:00, ",
				"TU, 14:00, ",
				"TU, 15:00, ",
				"TU, 16:00, ",
				"TU, 17:00, ",
				"TU, 18:00, ",
				"TU, 19:00, ",
				"TU, 20:00, ",
				"FR,  8:00, ",
				"FR, 10:00, ",
				"FR, 12:00, ",
				"FR, 14:00, ",
				"FR, 16:00, ",
				"FR, 18:00, "
				));
		
		ArrayList<String>courses = new ArrayList();
		ArrayList<String>labs = new ArrayList();
		
		try{
			outfile = new PrintWriter(new FileOutputStream("test1.txt", false));
		}
		catch(IOException f){
			System.out.println("error while writing file");
		}
		
		course_capacity_count = addCourseMaxMinCapacity(course_slots);
		lab_capacity_count = addLabMaxMinCapacity(lab_slots);
		System.out.println( "course cap is " + course_capacity_count );
		System.out.println( "lab cap is " + lab_capacity_count );
		populateCourses(courses);
		populateLabs(labs, courses);
		
		
		System.out.println( "course count is " + courses.size() );
		System.out.println( "lab count is " + labs.size() );
		//outfile.println("");
		outfile.println("Name:");
		outfile.println("departmentTest\r\n");
		outfile.println("Course slots:");
		printToFile(course_slots);
		outfile.println("\r\nLab slots:");
		printToFile(lab_slots);
		outfile.println("\r\nCourses:");
		printToFile(courses);
		outfile.println("\r\nLabs:");
		printToFile(labs);
		outfile.println("\r\nNot compatible:");
		
		outfile.println("\r\nUnwanted:");
		
		outfile.println("\r\nPreferences:");
		
		outfile.println("\r\nPair:");
		
		outfile.println("\r\nPartial assignments:");
		
		outfile.close();
	}
    
	public static void printToFile(ArrayList<String> a){
		for(int i = 0; i < a.size(); i++){
			outfile.println(a.get(i));
		}
	}
	
	public static int addCourseMaxMinCapacity(ArrayList<String> a){
		int count = 0;
		int min = 0;
		int max = 6;
		for(int i = 0; i < a.size(); i++){
			//randomly generate max capacity
			int randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);
			count+= randomNum;
			a.set(i, a.get(i) + randomNum + ", " + min);//concat to string
			
		}
		return count;
	}
	
	public static int addLabMaxMinCapacity(ArrayList<String> a){
		int count = 0;
		int min = 0;
		int max = 9;
		for(int i = 0; i < a.size(); i++){
			//randomly generate max capacity
			int randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);
			count+= randomNum;
			a.set(i, a.get(i) + randomNum + ", " + min);//concat to string
			
		}
		return count;
	}
	
	public static void populateCourses(ArrayList<String> a){
		String cpsc = "CPSC ";
		String lec = " LEC 01";
		int minLevel = 200;
		for(int i = course_capacity_count; i>0; i--){
			a.add(cpsc + minLevel + lec);
			minLevel++;
		}
	}
	
	public static void populateLabs(ArrayList<String> a, ArrayList<String> courses){
		//String cpsc = "CPSC ";
		//String lec = " LEC 01";
		String tut = " TUT 0";
		//int minLevel = 200;
		int lab_count = 0;
		int course_index = 0;
		for(int i = lab_capacity_count; i>0; ){
			int min = 1;
			int max = 5;
			int randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);
			if(i<randomNum){
				for(int j = 1; j <= i; j++){
					a.add(courses.get(course_index) + tut +  j);
					System.out.println(a.get(lab_count));
					lab_count++;
					
				}
				i = 0;
			}
			else{
				for(int j = 1; j <= randomNum; j++){
					a.add(courses.get(course_index) + tut +  j);
					System.out.println(a.get(lab_count));
					lab_count++;
				}
				i -= randomNum;
			}
			
			course_index++;
		}
	}
}
