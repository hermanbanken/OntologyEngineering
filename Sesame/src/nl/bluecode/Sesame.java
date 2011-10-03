package nl.bluecode;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.rio.RDFFormat;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.openrdf.model.Namespace;
import org.openrdf.model.Value;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;

public class Sesame {
	static Repository myRepository;
	static RepositoryConnection con;
	static GUI gui;
	
	public static void main(String[] args) {
		gui = new GUI();
		
		try {
			myRepository = new SailRepository(new MemoryStore());
			myRepository.initialize();

			con = myRepository.getConnection();

			
			// Add files to the repository
			con.add(new File("data/iswc-2006-complete.rdf"), "http://data/iswc-2006/", RDFFormat.RDFXML);
			con.add(new File("data/iswc-2008-complete.rdf"), "http://data/iswc-2008/", RDFFormat.RDFXML);
			con.add(new File("data/iswc-2009-complete.rdf"), "http://data/iswc-2009/", RDFFormat.RDFXML);
			con.add(new File("data/iswc-2010-complete.rdf"), "http://data/iswc-2010/", RDFFormat.RDFXML);
			
			con.add(new File("data/www-2007-complete.rdf"), "http://data/www-2007/", RDFFormat.RDFXML);
			con.add(new File("data/www-2008-complete.rdf"), "http://data/www-2008/", RDFFormat.RDFXML);
			con.add(new File("data/www-2009-complete.rdf"), "http://data/www-2009/", RDFFormat.RDFXML);
			con.add(new File("data/www-2010-complete.rdf"), "http://data/www-2010/", RDFFormat.RDFXML);
			
			// The query
			String query = "SELECT ?class WHERE { ?subject rdf:type ?class }";
			
			gui.setOutput(doQuery(query), query);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String doQuery(String query){
		String out = "";
		String namespaces = "";
		
		// Execute the query
		TupleQuery tupleQuery;
		try {
			RepositoryResult<Namespace> namespacesinRepository = con.getNamespaces();
			while (namespacesinRepository.hasNext()) {
				Namespace namespace = namespacesinRepository.next();
				namespaces += "PREFIX " + namespace.getPrefix() + ": <" + namespace.getName() + ">\n";
				System.out.println("Namespace " + namespace.getPrefix() + ": <" + namespace.getName() + ">");
			}
			
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
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
		
		return out;
	}
}
