package it.polimi.chansonnier.spi;

import java.io.File;

public interface TranscodingService {

	File convert(File original, String format);

}
