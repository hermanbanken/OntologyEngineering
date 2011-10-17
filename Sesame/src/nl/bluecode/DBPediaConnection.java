package nl.bluecode;

import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.http.HTTPRepository;

public class DBPediaConnection {
	private RepositoryConnection con;
	
	public DBPediaConnection() throws RepositoryException {
		String endpointURL = "http://dbpedia.org/sparql";
		HTTPRepository dbpediaEndpoint = new HTTPRepository(endpointURL, "");
		dbpediaEndpoint.initialize();

		con = dbpediaEndpoint.getConnection();
	}
	
	public static DBPediaConnection factory() throws RepositoryException {
		return new DBPediaConnection();
	}
	
	public RepositoryConnection get() {
		return con;
	}
}
