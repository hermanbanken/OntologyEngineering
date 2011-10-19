package nl.bluecode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openrdf.model.Namespace;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryResult;


public class Sesame {
	static GUI gui;
	static RepositoryConnection local, dbpedia;
	
	public static void main(String[] args) {
		gui = new GUI();
		
		try {
			local = LocalConnection.factory().get();
			dbpedia = DBPediaConnection.factory().get();
			
			// The query
			String query = "SELECT DISTINCT ?class WHERE { ?subject rdf:type ?class }";
			
			gui.setOutput(doQuery(query,local), query);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static ArrayList<Statement> lastResults;
	
	public static String doQuery(String query, RepositoryConnection con){
		String out = "";
		String namespaces = "";
		lastResults = new ArrayList<Statement>();
		
		// Execute the query
		TupleQuery tupleQuery;
		BooleanQuery booleanQuery;
		GraphQuery graphQuery;
		HashMap<String, Namespace> nsp = new HashMap<String, Namespace>();
		
		try {
			if(con == Sesame.local) {
			RepositoryResult<Namespace> namespacesinRepository = con.getNamespaces();
			while (namespacesinRepository.hasNext()) {
				Namespace namespace = namespacesinRepository.next();
				namespaces += "PREFIX " + namespace.getPrefix() + ": <" + namespace.getName() + ">\n";
				System.out.println("Namespace " + namespace.getPrefix() + ": <" + namespace.getName() + ">");
				nsp.put(namespace.getName(), namespace);
			}
			}
			
			if(query.contains("CONSTRUCT"))
			{
				graphQuery = con.prepareGraphQuery(QueryLanguage.SPARQL, namespaces + query);
				GraphQueryResult result = graphQuery.evaluate();
				
				System.out.println(result);
				
				// Get results
				int numResults = 0;
				
				while (result.hasNext()) {
					System.out.println("Result found");
					numResults++;
					Statement a = result.next();
					System.out.println(a);
					lastResults.add(a);
					out = out + 
							a.getSubject() + "\t" + 
							a.getPredicate() + "\t" + 
							a.getObject() + " .\n";
				}

				out = out + "Number of results: " + numResults + "\n";
			} 
			else if(query.contains("SELECT"))
			{
				tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, namespaces + query);
				TupleQueryResult result = tupleQuery.evaluate();
				
				// Get results
				List<String> bindingNames = result.getBindingNames();
				int numResults = 0;
		
				while (result.hasNext()) {
					numResults++;
					BindingSet bindingSet = result.next();
		
					out = out + "\n";
		
					for (int i = 0; i < bindingNames.size(); i++) {
						String bindingName = bindingNames.get(i);
						Value value = bindingSet.getValue(bindingName);
						out = out + bindingName + "\t => \t" + value + "\n";
					}
				}
				
				out = out + "Number of results: " + numResults + "\n";
			}
			else if(query.contains("ASK")){
				booleanQuery = con.prepareBooleanQuery(QueryLanguage.SPARQL, namespaces + query);
				out = out + "The anwser is " + (booleanQuery.evaluate() ? "yes" : "no");
			}
			else if(query.contains("DESCRIBE")){
				out = out + "Let's describe that query as.. Euhm, non-defined?";
			}
			
		
		} catch (Exception e) {
			out = e.getMessage();
		}
		
		return out;
	}
	
	public static String addLastResultToKB() {
		try {	
			for(Statement i : lastResults){
				local.add(i);
			}
			local.commit();
			return "Done.";
		} catch(Exception e) {
			return e.getMessage();
		}
	}
	
	public static String removeSpaces(String s) {
		return s.replaceAll("[\t ]{2,}", " ");
	}
}
