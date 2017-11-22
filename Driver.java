package main;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import javax.swing.*;
import javax.swing.text.BadLocationException;

import utility.CustomOutputStream;

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
		
	}
}
