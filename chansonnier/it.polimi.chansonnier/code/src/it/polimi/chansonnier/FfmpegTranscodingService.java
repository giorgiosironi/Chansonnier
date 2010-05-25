/****************************************************************************
 * Copyright (c) 2010 Giorgio Sironi. All rights reserved.
 * This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ****************************************************************************/
package it.polimi.chansonnier;

import it.polimi.chansonnier.spi.TranscodingService;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class FfmpegTranscodingService implements TranscodingService {
	private final static String FFMPEG_BIN = "/usr/bin/ffmpeg";
	
	@Override
	public File convert(File original, String format) {
		String tmpdir = System.getProperty("java.io.tmpdir");
		String time = String.valueOf((new Date()).getTime());
		String outputPath = tmpdir + "/" + original.getName() + "_" + time + ".wav";
		Runtime r = Runtime.getRuntime();
		String command = FFMPEG_BIN 
					   + " -i "
					   + original.getAbsolutePath()
					   + " "
					   + outputPath;
		System.out.println(command);
		try {
			Process ffmpeg = r.exec(command, new String[0]);
			int statusCode = ffmpeg.waitFor();
			if (statusCode != 0) {
				throw new RuntimeException("ffmpeg exited with status: " + statusCode);
			}
			return new File(outputPath); 
		} catch (IOException e) {
			throw new RuntimeException("Unable to run ffmpeg: " + e.getMessage());
		} catch (InterruptedException e) {
			throw new RuntimeException("Interruption on the main thread: " + e.getMessage());
		}
	}

}
