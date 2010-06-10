/****************************************************************************
 * Copyright (c) 2010 Giorgio Sironi. All rights reserved.
 * This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ****************************************************************************/
package it.polimi.chansonnier.processing;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
				pageTitle = removeNoise(pageTitle);
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

	private String removeNoise(String pageTitle) {
		pageTitle = pageTitle.replaceAll("\\([^()]*\\)", "");
		pageTitle = pageTitle.replaceAll("\\[[^()]*\\]", "");
		Pattern badWord = Pattern.compile("(with){0,1} lyrics", Pattern.CASE_INSENSITIVE);
		Matcher matcher = badWord.matcher(pageTitle);
		pageTitle = matcher.replaceAll("");
		return pageTitle;
	}

	public void setLyricsService(LyricsService lyricsService) {
		_lyricsService = lyricsService;
	}
	
	public void unsetLyricsService(LyricsService lyricsService) {
		_lyricsService = null;
	}

}
