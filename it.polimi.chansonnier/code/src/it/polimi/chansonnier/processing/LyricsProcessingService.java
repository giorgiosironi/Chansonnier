package it.polimi.chansonnier.processing;

import it.polimi.chansonnier.spi.LyricsService;

import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.blackboard.BlackboardAccessException;
import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.processing.ProcessingException;
import org.eclipse.smila.processing.ProcessingService;

public class LyricsProcessingService implements ProcessingService {
	LyricsService _lyricsService;

	@Override
	public Id[] process(Blackboard blackboard, Id[] recordIds)
			throws ProcessingException {
		try {
			for (Id id : recordIds) {
				String pageTitle = blackboard.getLiteral(id, new Path("PageTitle")).toString();
				String[] pieces = pageTitle.split("-");
				String title;
				String artist;
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
			}
		} catch (BlackboardAccessException e) {
			// TODO Auto-generated catch block
			throw new ProcessingException(e);
		}
		return recordIds;
	}

	public void setLyricsService(LyricsService lyricsService) {
		// TODO Auto-generated method stub
		_lyricsService = lyricsService;
	}

}
