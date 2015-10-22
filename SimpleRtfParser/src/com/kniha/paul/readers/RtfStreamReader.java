package com.kniha.paul.readers;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Implementation of RtfReader, read the RTF file from a stream.
 */
public class RtfStreamReader implements RtfReader {
	private final InputStream inStream;
	private int savedChar = -1;

	public RtfStreamReader(InputStream in) {
		if (in instanceof BufferedInputStream) {
			this.inStream = in;
		} else {
			this.inStream = new BufferedInputStream(in);
		}
	}

	@Override
	public int read() throws IOException {
		int resultChar;

		if (savedChar != -1) {
			resultChar = savedChar;
			savedChar = -1;
		} else {
			resultChar = inStream.read();
		}

		return resultChar;
	}

	@Override
	public void canselRead(int character) throws IOException {
		if (savedChar != -1) {
			throw new IOException("canselRead not possible");
		}

		savedChar = character;
	}

	@Override
	public int read(byte[] bytes) throws IOException {
		return inStream.read(bytes);
	}
}
