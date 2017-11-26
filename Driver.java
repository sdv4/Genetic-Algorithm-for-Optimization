

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.text.BadLocationException;



public class Driver extends JFrame{
	 	private JTextArea textArea;
     
	    private JButton buttonStart = new JButton("Start");
	    private JButton buttonClear = new JButton("Clear");
	     
	    private PrintStream standardOut;
	     
	    public Driver() {
	        super("Demo printing to JTextArea");
	         
	        textArea = new JTextArea(50, 10);
	        textArea.setEditable(false);
	        PrintStream printStream = new PrintStream(new CustomOutputStream(textArea));
	         
	        // keeps reference of standard output stream
	        standardOut = System.out;
	         
	        // re-assigns standard output stream and error output stream
	        System.setOut(printStream);
	        System.setErr(printStream);
	 
	        // creates the GUI
	        setLayout(new GridBagLayout());
	        GridBagConstraints constraints = new GridBagConstraints();
	        constraints.gridx = 0;
	        constraints.gridy = 0;
	        constraints.insets = new Insets(10, 10, 10, 10);
	        constraints.anchor = GridBagConstraints.WEST;
	         
//	        add(buttonStart, constraints);
	         
	        constraints.gridx = 1;
//	        add(buttonClear, constraints);
	         
	        constraints.gridx = 0;
	        constraints.gridy = 1;
	        constraints.gridwidth = 2;
	        constraints.fill = GridBagConstraints.BOTH;
	        constraints.weightx = 1.0;
	        constraints.weighty = 1.0;
	         
	        add(new JScrollPane(textArea), constraints);
	         
	        // adds event handler for button Start
//	        buttonStart.addActionListener(new ActionListener() {
//	            @Override
//	            public void actionPerformed(ActionEvent evt) {
//	                printLog();
//	            }
//	        });
	         
	        // adds event handler for button Clear
	        buttonClear.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent evt) {
	                // clears the text area
	                try {
	                    textArea.getDocument().remove(0,
	                            textArea.getDocument().getLength());
	                    standardOut.println("Text area cleared");
	                } catch (BadLocationException ex) {
	                    ex.printStackTrace();
	                }
	            }
	        });
	         
	        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        setSize(480, 320);
	        setLocationRelativeTo(null);    // centers on screen
	    }
	
	public static void main(String[]args){
		
		String input =  null;
		File file = null;
		do {
			input= JOptionPane.showInputDialog("Please input the text file to be processed (with the extension)");
			if(input==null) {
				System.out.println("Exiting program.");
				System.exit(0);
			}
			
			file = new File(input);
			if(!file.exists()) {
				JOptionPane.showMessageDialog(new JFrame(), "No such file found!", "File not found", JOptionPane.WARNING_MESSAGE);
			}
		}
		while(!file.exists());
		
		SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Driver().setVisible(true);
            }
        });
		
		Parser aParser = new Parser(input);		
		aParser.start();
		
		ArrayList<CourseLab> courseLabList = aParser.getCourseLabList();
		ArrayList<Slot> slotCList = aParser.getCourseSlotList();
		ArrayList<Slot> slotLList = aParser.getLabSlotList();
		
		OTS tester = new OTS(courseLabList,  slotCList, slotLList);
		int [] anIndividual = tester.getIndividual();
		
		for (int i=0; i<aParser.getCourseLabList().size(); i++){
			System.out.print(anIndividual[i]);
		}
		System.out.println();
		for (int i=0; i<courseLabList.size(); i++){
			System.out.print(courseLabList.get(i).getName()+" ");
			int timeSlot = anIndividual[i];
			if (timeSlot < slotCList.size()+1){
				for (int j = 0; j<slotCList.size(); j++){
					Slot aSlot = slotCList.get(j);
					if (timeSlot == aSlot.getId()){
						System.out.print(aSlot.getDay()+" "+aSlot.getStart());
						break;
					}
				}
			}
			else{
				for (int j = 0; j<slotLList.size(); j++){
					Slot aSlot = slotLList.get(j);
					if (timeSlot == aSlot.getId()){
						System.out.print(aSlot.getDay()+" "+aSlot.getStart());
						break;
					}
				}
			}
			System.out.println();
		}
		//boolean outcome = tester.constr(tester.root.assign);
		//System.out.println("Result of constr is " + outcome);
	}
//	
//	public boolean constr(ArrayList<Integer> pr, ArrayList<CourseLab> courseLabList, ArrayList<Slot> slotCList, ArrayList<Slot> slotLList) {
//		for(int i = 0; i < courseLabList.size(); i++){
//			System.out.println("Courselab is not compatible with");
////			for(int j = 0; i < courseLabList.get(i).getNotCompatibleCoursesLabs().size(); i++){
////				System.out.println(courseLabList.get(i).getNotCompatibleCoursesLabs().get(j).getId());
////			}
//		}
//		
//		for(int i = 0; i < slotCList.size(); i++){
//			System.out.println("slotC is not compatible with");
//			for(int j = 0; j < slotCList.get(i).getOverlappingSlots().size(); j++){
//				System.out.println(slotCList.get(i).getOverlappingSlots().get(j));
//			}
//		}
//		
//		return true;
//	}
}
