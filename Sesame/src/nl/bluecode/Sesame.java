package nl.bluecode;

import java.io.File;
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
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.sail.memory.MemoryStore;


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
			
			gui.setOutput(doQuery(query,dbpedia), query);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String doQuery(String query, RepositoryConnection con){
		String out = "";
		String namespaces = "";
		
		// Execute the query
		TupleQuery tupleQuery;
		BooleanQuery booleanQuery;
		GraphQuery graphQuery;
		HashMap<String, Namespace> nsp = new HashMap<String, Namespace>();
		
		try {
			/*RepositoryResult<Namespace> namespacesinRepository = con.getNamespaces();
			while (namespacesinRepository.hasNext()) {
				Namespace namespace = namespacesinRepository.next();
				namespaces += "PREFIX " + namespace.getPrefix() + ": <" + namespace.getName() + ">\n";
				System.out.println("Namespace " + namespace.getPrefix() + ": <" + namespace.getName() + ">");
				nsp.put(namespace.getName(), namespace);
			}*/
			
			if(query.contains("CONSTRUCT"))
			{
				graphQuery = con.prepareGraphQuery(QueryLanguage.SPARQL, namespaces + query);
				GraphQueryResult result = graphQuery.evaluate();
				
				// Get results
				int numResults = 0;
				
				while (result.hasNext()) {
					numResults++;
					Statement a = result.next();
					out = out + 
							a.getSubject() + "\t" + 
							nsp.get(a.getPredicate().getNamespace()).getPrefix() + ":"+a.getPredicate().getLocalName() + "\t" + 
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
	
	public static String removeSpaces(String s) {
		return s.replaceAll("[\t ]{2,}", " ");
	}
}
