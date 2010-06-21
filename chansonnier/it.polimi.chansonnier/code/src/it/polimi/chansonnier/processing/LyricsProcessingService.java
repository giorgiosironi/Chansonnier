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

import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.blackboard.BlackboardAccessException;
import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.processing.ProcessingException;
import org.eclipse.smila.processing.ProcessingService;

public class LyricsProcessingService extends AbstractProcessingService implements ProcessingService {
	private LyricsService _lyricsService;
	
	@Override
	public Id[] process(Blackboard blackboard, Id[] recordIds)
			throws ProcessingException {
		try {
			if (this._lyricsService == null) {
				throw new ProcessingException("Unable to search lyrics: LyricsService is not initialized.");
			}
			for (Id id : recordIds) {
				String pageTitle = blackboard.getLiteral(id, new Path(getInputPath(blackboard, id))).toString();
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
					blackboard.setLiteral(id, new Path(getOutputPath(blackboard, id)), lyricsAttribute);
				}
				Literal artistAttribute = blackboard.createLiteral(id);
				artistAttribute.setStringValue(artist);
				blackboard.setLiteral(id, new Path("artist"), artistAttribute);
				Literal titleAttribute = blackboard.createLiteral(id);
				titleAttribute.setStringValue(title);
				blackboard.setLiteral(id, new Path("title"), titleAttribute);
			}
		} catch (BlackboardAccessException e) {
			// TODO Auto-generated catch block
			throw new ProcessingException(e);
		}
		return recordIds;
	}

	private String removeNoise(String pageTitle) {
		pageTitle = removeTextBetweenParenthesis(pageTitle);
		pageTitle = removeBadChars(pageTitle); 
		pageTitle = removeCommonExpressions(pageTitle);
		return pageTitle;
	}

	public String removeTextBetweenParenthesis(String text) {
		return text.replaceAll("\\([^()]*\\)", "");
	}
	
	private String removeBadChars(String text) {
		return text.replaceAll("[^A-Za-z0-9- ]{1}", "");
	}
	
	private String removeCommonExpressions(String text) {
		Pattern badWord = Pattern.compile("(with){0,1} lyrics", Pattern.CASE_INSENSITIVE);
		Matcher matcher = badWord.matcher(text);
		text = matcher.replaceAll("");
		return text;
	}

	public void setLyricsService(LyricsService lyricsService) {
		_lyricsService = lyricsService;
	}
	
	public void unsetLyricsService(LyricsService lyricsService) {
		_lyricsService = null;
	}

}
