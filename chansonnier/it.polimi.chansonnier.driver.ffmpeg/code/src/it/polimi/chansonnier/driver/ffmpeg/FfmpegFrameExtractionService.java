/****************************************************************************
 * Copyright (c) 2010 Giorgio Sironi. All rights reserved.
 * This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ****************************************************************************/
package it.polimi.chansonnier.driver.ffmpeg;

import it.polimi.chansonnier.spi.FrameExtractionService;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class FfmpegFrameExtractionService implements FrameExtractionService {
	private final static String FFMPEG_BIN = "/usr/bin/ffmpeg";
	
	@Override
	public File getImage(File video, String seekTime) {
		String tmpdir = System.getProperty("java.io.tmpdir");
		String time = String.valueOf((new Date()).getTime());
		String outputPath = tmpdir + "/" + video.getName() + "_" + time + ".png";
		Runtime r = Runtime.getRuntime();
        // ffmpeg -i desmond.flv -ss 00:00:05 -t 00:00:01 -f image2 -r 1 prova.png
		String command = FFMPEG_BIN 
					   + " -i "
					   + video.getAbsolutePath()
					   + " -ss " + seekTime 
                       + " -t 00:00:01 -f image2 -r 1 "
					   + outputPath;
		try {
			Process ffmpeg = r.exec(command, new String[0]);
			int statusCode = ffmpeg.waitFor();

            /*
             * It's normal that statusCode it's not 0, but the images are produced anyway
			if (statusCode != 0) {
				throw new RuntimeException("ffmpeg exited with status: " + statusCode);
			}
            */
			File capturedFrame = new File(outputPath); 
            if (!capturedFrame.exists()) {
				throw new RuntimeException("ffmpeg didn't produce an output at " + outputPath);
            }
            return capturedFrame;
		} catch (IOException e) {
			throw new RuntimeException("Unable to run ffmpeg: " + e.getMessage());
		} catch (InterruptedException e) {
			throw new RuntimeException("Interruption on the main thread: " + e.getMessage());
		}
	}

}
