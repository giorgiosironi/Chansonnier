package it.polimi.chansonnier.processing;

import it.polimi.chansonnier.spi.LyricsService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.blackboard.BlackboardAccessException;
import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.processing.ProcessingException;
import org.eclipse.smila.processing.ProcessingService;

public class LyricsProcessingService implements ProcessingService {
	private LyricsService _lyricsService;
	
	private final Log _log = LogFactory.getLog(LyricsProcessingService.class);
	
	public LyricsProcessingService() {
		_log.debug("it.polimi.chansonnier.processing.LyricsProcessingService: created");
	}
	
	@Override
	public Id[] process(Blackboard blackboard, Id[] recordIds)
			throws ProcessingException {
		try {
			if (this._lyricsService == null) {
				throw new ProcessingException("Unable to search lyrics: LyricsService is not initialized.");
			}
			for (Id id : recordIds) {
				_log.debug("it.polimi.chansonnier.processing.LyricsProcessingService: processing " + id.toString());
				String pageTitle = blackboard.getLiteral(id, new Path("PageTitle")).toString();
				String[] pieces = pageTitle.split("-");
				String title = "Unknown";
				String artist = "Unknown";
				String lyrics = null;
				if (pieces.length == 2) {
					// Led Zeppelin - Stairway to heaven
					artist = pieces[0].trim();
					title = pieces[1].trim();
					lyrics = _lyricsService.getLyrics(title, artist);
					if (lyrics == null) {
						// Stairway to heaven - Led Zeppelin
						title = pieces[0].trim();
						artist = pieces[1].trim();
						lyrics = _lyricsService.getLyrics(title, artist);
					}
				}
				
				if (lyrics != null) {
					Literal lyricsAttribute = blackboard.createLiteral(id);
					lyricsAttribute.setStringValue(lyrics);
					blackboard.setLiteral(id, new Path("Lyrics"), lyricsAttribute);
				}
				Literal artistAttribute = blackboard.createLiteral(id);
				artistAttribute.setStringValue(artist);
				blackboard.setLiteral(id, new Path("Artist"), artistAttribute);
				Literal titleAttribute = blackboard.createLiteral(id);
				titleAttribute.setStringValue(title);
				blackboard.setLiteral(id, new Path("Title"), titleAttribute);
			}
		} catch (BlackboardAccessException e) {
			// TODO Auto-generated catch block
			throw new ProcessingException(e);
		}
		return recordIds;
	}

	public void setLyricsService(LyricsService lyricsService) {
		_lyricsService = lyricsService;
	}
	
	public void unsetLyricsService(LyricsService lyricsService) {
		_lyricsService = null;
	}

}
