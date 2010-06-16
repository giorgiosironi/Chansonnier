/****************************************************************************
 * Copyright (c) 2010 Giorgio Sironi. All rights reserved.
 * This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ****************************************************************************/
package it.polimi.chansonnier.driver.ffmpeg.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import junit.framework.TestCase;

import it.polimi.chansonnier.spi.FrameExtractionService;
import it.polimi.chansonnier.driver.ffmpeg.FfmpegFrameExtractionService;
import it.polimi.chansonnier.utils.URLUtils;

public class FfmpegFrameExtractionServiceTest extends TestCase {
	private FrameExtractionService _service;
	
	protected void setUp() throws Exception {
		_service = new FfmpegFrameExtractionService();
	}
	
	public void testPngImageIsExtractedFromFlvVideoAtTheGivenSeekTime() throws Exception {
        File image = _service.getImage(new File("fixtures/desmond.flv"), "00:00:04");
        InputStream actual = new FileInputStream(image);
        assertEquals(URLUtils.readStart("fixtures/desmond.png"), URLUtils.readStart(actual));
	}
}
