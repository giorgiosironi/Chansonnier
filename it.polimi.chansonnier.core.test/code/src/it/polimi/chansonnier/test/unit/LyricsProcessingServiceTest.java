package it.polimi.chansonnier.test.unit;

import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.blackboard.BlackboardAccessException;
import org.eclipse.smila.blackboard.BlackboardFactory;
import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.id.IdFactory;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.test.DeclarativeServiceTestCase;

import it.polimi.chansonnier.processing.LyricsProcessingService;
import it.polimi.chansonnier.spi.LyricsService;

public class LyricsProcessingServiceTest extends DeclarativeServiceTestCase implements LyricsService {
	private Blackboard _blackboard;
	private LyricsProcessingService _service;
	private static final String LYRICS = "The warden threw a party in the county jail...";
	private static final String TITLE = "Jailhouse Rock";
	private static final String ARTIST = "Elvis Presley";
	
	protected void setUp() throws Exception {
		super.setUp();
	    forceStartBundle("org.eclipse.smila.blackboard");
	    forceStartBundle("org.eclipse.smila.processing");
	    final BlackboardFactory factory = getService(BlackboardFactory.class);
	    assertNotNull("no BlackboardFactory service found.", factory);
	    _blackboard = factory.createPersistingBlackboard();
	    assertNotNull("no Blackboard created", _blackboard);
		_service = new LyricsProcessingService();
		_service.setLyricsService(this);
	}
	
	protected Id createBlackboardRecord(String source, String key) {
		final Id id = IdFactory.DEFAULT_INSTANCE.createId(source, key);
		_log.info("Invalidating and re-creating test record on blackboard.");
		_log.info("This may cause an exception to be logged that can be safely ignored.");
		_blackboard.invalidate(id);
		_blackboard.create(id);
		return id;
	}
	
	/**
	 * @return the blackboard
	 */
	public Blackboard getBlackboard() {
	    return _blackboard;
	}
	
	public void testStoresLyricsAsAnAnnotationStartingFromThePageTitle() throws Exception {
	    final Id id = createBlackboardRecord("copy", "attribute-attachment");
	    setAttribute(id, new Path("PageTitle"), ARTIST + " - " + TITLE);
	    
	    _service.process(getBlackboard(), new Id[] { id });
	    
	    final String text = getAttribute(id, new Path("Lyrics")).toString();
	    assertEquals(LYRICS, text);
	}
	
	public void testStoresLyricsAsAnAnnotationWhenTitleComesBeforeArtist() throws Exception {
	    final Id id = createBlackboardRecord("copy", "attribute-attachment");
	    setAttribute(id, new Path("PageTitle"), TITLE + " - " + ARTIST);
	    
	    _service.process(getBlackboard(), new Id[] { id });
	    
	    final String text = getAttribute(id, new Path("Lyrics")).toString();
	    assertEquals(LYRICS, text);
	}
	
	/**
	 * Set the attribute value.
	 * 
	 * @param id
	 *          the record id
	 * @param path
	 *          the attribute path
	 * @throws BlackboardAccessException
	 *           if any error occurs
	 */
	private void setAttribute(final Id id, final Path path, String value) throws BlackboardAccessException {
    	final Literal literal = getBlackboard().createLiteral(id);
    	literal.setStringValue(value);
    	getBlackboard().setLiteral(id, path, literal);
    }

	  /**
	   * Gets the attribute value.
	   * 
	   * @param id
	   *          the record id
	   * @param path
	   *          the attribute path
	   * @return a Literal containing the attribute value
	   * @throws BlackboardAccessException
	   *           if any error occurs
	   */
	private Literal getAttribute(final Id id, final Path path) throws BlackboardAccessException {
		return getBlackboard().getLiteral(id, path);
	}
	
	@Override
	public String getLyrics(String title, String artist) {
		if (title.equals("Jailhouse Rock")
	     && artist.equals("Elvis Presley")) {
			return "The warden threw a party in the county jail...";
	    }
		return null;
	}
}
