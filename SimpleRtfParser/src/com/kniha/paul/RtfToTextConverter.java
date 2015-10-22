package com.kniha.paul;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.Deque;

import com.kniha.paul.handlers.BaseRtfHandler;
import com.kniha.paul.parser.RtfParser;
import com.kniha.paul.parser.SimpleRtfParser;
import com.kniha.paul.readers.RtfReader;
import com.kniha.paul.utils.RtfComands;
import com.kniha.paul.utils.CommandType;

/**
 * Main RTF to Text converter
 */

public class RtfToTextConverter extends BaseRtfHandler {
	private Charset charset;
	private OutputStream outputStream;
	private RtfComands actualDestination = RtfComands.rtf;
	private final Deque<RtfComands> currentDestinationStack = new ArrayDeque<RtfComands>();

	public void convert(RtfReader reader, OutputStream outputStream,
			String outputCharset) throws IOException {
		this.outputStream = outputStream;
		this.charset = Charset.forName(outputCharset);
		this.actualDestination = RtfComands.rtf;
		RtfParser parser = new SimpleRtfParser();
		parser.parse(reader, this);
	}


	@Override
	public void handleStartGroup() {
		currentDestinationStack.push(actualDestination);
	}

	@Override
	public void handleEndGroup() {
		actualDestination = currentDestinationStack.pop();
	}

	@Override
	public void handleString(String string) {
		switch (actualDestination) {
		case rtf:
		case pntext:
		case fldrslt: {
			handleExtractedText(string);
			break;
		}

		default: {
			// Do nothing
			break;
		}
		}

	}

	@Override
	public void handleCommand(RtfComands command, int param,
			boolean hasParam, boolean optional) {
		if (command.getCommandType() == CommandType.Destination) {
			actualDestination = command;
		}

		switch (command) {
		case par:
		case line:
		case row: {
			handleExtractedText("\n");
			break;
		}

		case tab:
		case cell: {
			handleExtractedText("\t");
			break;
		}

		default: {
			// Do nothing
			break;
		}
		}
	}
	
	/**
	 * Handle text that we have extracted from the RTF file.
	 */
	private void handleExtractedText(String text){
		try {
			outputStream.write(text.getBytes(charset));
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

}
