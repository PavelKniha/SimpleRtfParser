package com.kniha.paul.readers;

import java.io.IOException;

/**
 * input reader interface, 
 */
public interface RtfReader {
	/**
	 * read byte.
	 */
	int read() throws IOException;

	/**
	 * save last byte to read again later.
	 */
	void canselRead(int c) throws IOException;

	/**
	 * read byte array
	 */
	int read(byte[] b) throws IOException;
}
