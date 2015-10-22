package com.kniha.paul.parser;
import java.io.IOException;

import com.kniha.paul.handlers.RtfHandler;
import com.kniha.paul.readers.RtfReader;


public interface RtfParser {
	/**
	 * parser will read rtf file and send events to handler
	 */
	public void parse(RtfReader reader, RtfHandler handler)
			throws IOException;
}
