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
	
	JButton r_query_l, r_query_r, add;
	
	public GUI(){
		JFrame f = new JFrame();
		listeners = new ArrayList<Class<Sesame>>();
		
		f.setSize(640, 500);
		Container contentPane = f.getContentPane();
		contentPane.setLayout(new BorderLayout());

		JLabel l_query = new JLabel("Query");
		f.add(l_query);
		f_query = new JTextArea(20, 50);
		f.add(f_query, BorderLayout.CENTER);
		Container buttons = new Container();
		buttons.setLayout(new GridLayout(3,1));
		r_query_l = new JButton("Execute Locally");
		r_query_l.addActionListener(this);
		buttons.add(r_query_l);
		r_query_r = new JButton("Execute Remote");
		r_query_r.addActionListener(this);
		buttons.add(r_query_r);
		add = new JButton("Add results to KB");
		add.addActionListener(this);
		buttons.add(add);
		
		f.add(buttons, BorderLayout.EAST);
		
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
		String output = "";
		if(arg0.getSource() == r_query_r)
			output = Sesame.doQuery(f_query.getText(), Sesame.dbpedia);
		else if(arg0.getSource() == r_query_l)
			output = Sesame.doQuery(f_query.getText(), Sesame.local);
		else if(arg0.getSource() == add) {
			output = Sesame.addLastResultToKB();
		}
		f_query.setText(Sesame.removeSpaces(f_query.getText()));
		setOutput(output);
	}
}
