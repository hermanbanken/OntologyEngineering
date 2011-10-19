package nl.bluecode;

import java.util.ArrayList;
import java.util.List;

import org.openrdf.model.Namespace;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;

public class Matcher {
	static MatcherGUI gui;
	static RepositoryConnection local, dbpedia;
	
	public static void main(String[] args) {
		MatcherGUI m = new MatcherGUI();
	}
	public static int go(String qlocal, String qremote){
		try {
			local = LocalConnection.factory().get();
			dbpedia = DBPediaConnection.factory().get();
			
			//"SELECT DISTINCT ?subject ?label WHERE { ?subject rdf:type :Organization. ?subject rdfs:label ?label. FILTER regex(?label, \"university\" , \"i\") }"
			//"SELECT DISTINCT ?subject ?label WHERE { ?subject rdf:type <http://dbpedia.org/ontology/EducationalInstitution>. ?subject rdfs:label ?label. FILTER regex(?label, \"university\" , \"i\"). FILTER langMatches( lang(?label), 'en') }"
			ArrayList<BindingSet> localresults = query(removeSpaces(qlocal),local);
			ArrayList<BindingSet> remoteresults = query(removeSpaces(qremote),dbpedia);
			
			int count = 0;
			for(BindingSet i : localresults) {
				String label = i.getValue("label").stringValue();
				for(BindingSet j : remoteresults) {
					if(label.equals(j.getValue("label").stringValue())) {
						System.out.println("<" + i.getValue("subject").stringValue() + "> skos:exactMatch <" + j.getValue("subject").stringValue() + ">. ");
						count++;
					}
				}
			}
			System.out.println(count);
			return count;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
	
	
	private static ArrayList<BindingSet> query(String query, RepositoryConnection con) throws RepositoryException, MalformedQueryException, QueryEvaluationException {
		ArrayList<BindingSet> out = new ArrayList<BindingSet>();
		if(con != Matcher.dbpedia) {
			String namespaces = "";
			RepositoryResult<Namespace> namespacesinRepository = con.getNamespaces();
			while (namespacesinRepository.hasNext()) {
				Namespace namespace = namespacesinRepository.next();
				namespaces += "PREFIX " + namespace.getPrefix() + ": <" + namespace.getName() + ">\n";
				System.out.println("Namespace " + namespace.getPrefix() + ": <" + namespace.getName() + ">");
				//nsp.put(namespace.getName(), namespace);
				}
			query = namespaces+query;
		}
		
		TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, query);
		TupleQueryResult result = tupleQuery.evaluate();
		
		// Get results
		List<String> bindingNames = result.getBindingNames();
		int numResults = 0;

		while (result.hasNext()) {
			numResults++;
			BindingSet bindingSet = result.next();
			out.add(bindingSet);
		}
		return out;
	}
	
	public static String removeSpaces(String s) {
		return s.replaceAll("[\t ]{2,}", " ");
	}
}
