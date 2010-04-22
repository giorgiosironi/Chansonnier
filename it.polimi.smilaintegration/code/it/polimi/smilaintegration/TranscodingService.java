package it.polimi.smilaintegration;

import java.io.File;

public interface TranscodingService {

	File convert(File original, String format);

}
