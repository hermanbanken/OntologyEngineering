package nl.bluecode;

import java.io.File;
import java.io.IOException;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.memory.MemoryStore;

public class LocalConnection {
	private Repository myRepository;
	private RepositoryConnection con;
	
	public LocalConnection() throws RDFParseException, RepositoryException, IOException {
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
	}
	
	public static LocalConnection factory() throws RDFParseException, RepositoryException, IOException {
		return new LocalConnection();
	}
	
	public RepositoryConnection get() {
		return con;
	}
}
