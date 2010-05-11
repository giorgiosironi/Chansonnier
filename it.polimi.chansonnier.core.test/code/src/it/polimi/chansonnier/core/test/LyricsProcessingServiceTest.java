package it.polimi.chansonnier.core.test;

import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.datamodel.id.Id;

import it.polimi.chansonnier.processing.LyricsProcessingService;
import it.polimi.chansonnier.spi.LyricsService;

public class LyricsProcessingServiceTest extends ProcessingServiceTest implements LyricsService {
	LyricsProcessingService _service;
	private static final String LYRICS = "The warden threw a party in the county jail...";
	private static final String TITLE = "Jailhouse Rock";
	private static final String ARTIST = "Elvis Presley";
	
	protected void init() throws Exception {
		_service = new LyricsProcessingService();
		_service.setLyricsService(this);
	}
	
	public void testStoresLyricsAsAnAnnotationStartingFromThePageTitle() throws Exception {
	    final Id id = createBlackboardRecord("copy", "attribute-attachment");
	    setAttribute(id, new Path("PageTitle"), ARTIST + " - " + TITLE);
	    
	    _service.process(getBlackboard(), new Id[] { id });
	    
	    final String text = getAttribute(id, new Path("Lyrics")).toString();
	    assertEquals(LYRICS, text);
	}
	
	public void testCreatesArtistAndTitleAttributesBasingOnWhichCombinationFindsTheLyrics() throws Exception {
	    final Id id = createBlackboardRecord("copy", "attribute-attachment");
	    setAttribute(id, new Path("PageTitle"), ARTIST + " - " + TITLE);
	    
	    _service.process(getBlackboard(), new Id[] { id });
	    
	    assertEquals(ARTIST, getAttribute(id, new Path("Artist")).toString());
	    assertEquals(TITLE, getAttribute(id, new Path("Title")).toString());
	}
	
	public void testStoresLyricsAsAnAnnotationWhenTitleComesBeforeArtist() throws Exception {
	    final Id id = createBlackboardRecord("copy", "attribute-attachment");
	    setAttribute(id, new Path("PageTitle"), TITLE + " - " + ARTIST);
	    
	    _service.process(getBlackboard(), new Id[] { id });
	    
	    final String text = getAttribute(id, new Path("Lyrics")).toString();
	    assertEquals(LYRICS, text);
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
