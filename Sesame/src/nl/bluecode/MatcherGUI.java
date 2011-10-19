package nl.bluecode;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.*;

public class MatcherGUI implements ActionListener {
	JTextArea f_output;
	JTextArea f_query;
	JTextArea f_local;
	JTextArea f_remote;
	ArrayList<Class<Sesame>> listeners;
	
	JButton action;
	
	public MatcherGUI(){
		JFrame f = new JFrame();
		listeners = new ArrayList<Class<Sesame>>();
		
		f.setSize(640, 500);
		Container contentPane = f.getContentPane();
		contentPane.setLayout(new BorderLayout());

		Container inputs = new Container();
		inputs.setLayout(new GridLayout(1,2));
		
		f.add(inputs, BorderLayout.CENTER);

		f_local = new JTextArea(20, 50);
        JScrollPane scroll1 = new JScrollPane(f_local);
		inputs.add(scroll1);
		f_remote = new JTextArea(20, 50);
        JScrollPane scroll2 = new JScrollPane(f_remote);
		inputs.add(scroll2); 

		f_output = new JTextArea(20, 50);
        JScrollPane scroll = new JScrollPane(f_output);
		f.add(scroll, BorderLayout.SOUTH);       

		action = new JButton("Find matches");
		action.addActionListener(this);
		f.add(action, BorderLayout.EAST);   
		
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
		int error = -1;
		if(arg0.getSource() == action)
			error = Matcher.go(f_local.getText(), f_remote.getText());
			//"SELECT DISTINCT ?subject ?label WHERE { ?subject rdf:type :Organization. ?subject rdfs:label ?label. FILTER regex(?label, \"university\" , \"i\") }"
			//"SELECT DISTINCT ?subject ?label WHERE { ?subject rdf:type <http://dbpedia.org/ontology/EducationalInstitution>. ?subject rdfs:label ?label. FILTER regex(?label, \"university\" , \"i\"). FILTER langMatches( lang(?label), 'en') }"
		
		if(error > -1)
			f_output.setText("Found "+error+" matches");
		else 
			f_output.setText("An error occured");
	}
}
