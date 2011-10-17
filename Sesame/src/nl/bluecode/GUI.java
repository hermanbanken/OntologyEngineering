package nl.bluecode;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.*;

public class GUI implements ActionListener {
	JTextArea f_output;
	JTextArea f_query;
	ArrayList<Class<Sesame>> listeners;
	
	public GUI(){
		JFrame f = new JFrame();
		listeners = new ArrayList<Class<Sesame>>();
		
		f.setSize(640, 400);
		Container contentPane = f.getContentPane();
		contentPane.setLayout(new BorderLayout());

		JLabel l_query = new JLabel("Query");
		f.add(l_query);
		f_query = new JTextArea(20, 50);
		f.add(f_query, BorderLayout.CENTER);
		JButton b_query = new JButton("Execute");
		b_query.addActionListener(this);
		f.add(b_query, BorderLayout.EAST);
		f_output = new JTextArea(20, 50);
        JScrollPane scroll = new JScrollPane(f_output);
		f.add(scroll, BorderLayout.SOUTH);       
		
		// pack();
		f.setVisible(true);
		f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
	
	public void setOutput(String out){
		f_output.setText(out);
	}
	public void setOutput(String out, String query){
		setOutput(out);
		f_query.setText(query);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		String output = Sesame.doQuery(f_query.getText());
		f_query.setText(Sesame.removeSpaces(f_query.getText()));
		setOutput(output);
	}
}
