package com.kniha.paul.parser;

import java.io.EOFException;
import java.io.IOException;

import com.kniha.paul.handlers.RtfHandler;
import com.kniha.paul.readers.RtfReader;
import com.kniha.paul.utils.ByteBuffer;
import com.kniha.paul.utils.RtfComands;
import com.kniha.paul.utils.HexUtils;


public class SimpleRtfParser implements RtfParser {
	private RtfReader source;
	private int groupDepth;
	private boolean parsingHex;
	private ByteBuffer buffer;
	private RtfHandler handler;

	private static final int MAX_PARAMETER_LENGTH = 20;
	private static final int MAX_COMMAND_LENGTH = 32;

	/**
	 * Parse RTF data from an input source.
	 */
	@Override
	public void parse(RtfReader source, RtfHandler listener)
			throws IOException {
		this.source = source;
		this.handler = listener;
		this.groupDepth = 0;
		this.parsingHex = false;
		this.buffer = new ByteBuffer();

		listener.handleStartDocument();

		int ch;
		parsingHex = false;

		while (true) {

			ch = source.read();
			if (ch == -1) {
				break;
			}

			if (groupDepth < 0) {
				throw new IllegalStateException("Group stack underflow");
			}

			switch (ch) {
			case '{': {
				handleGroupStart();
				break;
			}

			case '}': {
				handleGroupEnd();
				break;
			}

			case '\\': {
				handleCommand();
				break;
			}

			case '\r':
			case '\n': {
				break;
			}

			case '\t': {
				handleCharacterData();
				listener.handleCommand(RtfComands.tab, 0, false, false);
				break;
			}

			default: {
				handleCharacterByte(ch);
				break;
			}
			}
		}

		if (groupDepth < 0) {
			throw new IllegalStateException("Group stack underflow");
		}

		if (groupDepth > 0) {
			throw new IllegalStateException("Unmatched brace");
		}

		listener.handleEndDocument();
	}

	/**
	 * Process a single character byte, or hex encoded character byte.
	 */
	private void handleCharacterByte(int ch) throws IOException {
		if (parsingHex) {
			int b = HexUtils.parseHexDigit(ch) << 4;
			ch = source.read();
			if (ch == -1) {
				throw new IllegalStateException("Unexpected end of file");
			}
			b += HexUtils.parseHexDigit(ch);
			buffer.add(b);
			parsingHex = false;
		} else {
			buffer.add(ch);
		}
	}

	/**
	 * Read and process an RTF command.
	 */
	private void handleCommand() throws IOException {
		boolean commandHasParameter = false;
		boolean parameterIsNegative = false;
		int parameterValue = 0;
		StringBuilder commandText = new StringBuilder();
		StringBuilder parameterText = new StringBuilder();

		int ch = source.read();
		if (ch == -1) {
			throw new EOFException();
		}

		commandText.append((char) ch);

		if (!Character.isLetter(ch)) {
			handleCommand(commandText, 0, commandHasParameter);
			return;
		}

		while (true) {
			ch = source.read();
			if (ch == -1 || !Character.isLetter(ch)) {
				break;
			}
			commandText.append((char) ch);
			if (commandText.length() > MAX_COMMAND_LENGTH) {
				break;
			}
		}

		if (ch == -1) {
			throw new EOFException();
		}

		if (commandText.length() > MAX_COMMAND_LENGTH) {
			throw new IllegalArgumentException("Invalid keyword: "
					+ commandText.toString());
		}

		if (ch == '-') {
			parameterIsNegative = true;
			ch = source.read();
			if (ch == -1) {
				throw new EOFException();
			}
		}
		if (Character.isDigit(ch)) {
			commandHasParameter = true;
			parameterText.append((char) ch);
			while (true) {
				ch = source.read();
				if (ch == -1 || !Character.isDigit(ch)) {
					break;
				}
				parameterText.append((char) ch);
				if (parameterText.length() > MAX_PARAMETER_LENGTH) {
					break;
				}
			}

			if (parameterText.length() > MAX_PARAMETER_LENGTH) {
				throw new IllegalArgumentException("Invalid parameter: "
						+ parameterText.toString());
			}

			parameterValue = Integer.parseInt(parameterText.toString());
			if (parameterIsNegative) {
				parameterValue = -parameterValue;
			}
		}

		if (ch != ' ') {
			source.canselRead(ch);
		}

		handleCommand(commandText, parameterValue, commandHasParameter);
	}

	/**
	 * Determine what to do with the extracted command.
	 */
	private void handleCommand(StringBuilder commandBuffer, int parameter,
			boolean hasParameter) throws IOException {
		String commandName = commandBuffer.toString();
		RtfComands command = RtfComands.getInstance(commandName);

		if (command != null) {
			if (command != RtfComands.hex) {
				handleCharacterData();

			}

			switch (command) {
			case bin: {
				handleBinaryData(parameter);
				break;
			}

			case hex: {
				parsingHex = true;
				break;
			}

			default: {
				handler.handleCommand(command, parameter, hasParameter, false);
				break;
			}
			}
		}
	}

	/**
	 * Pass accumulated character data to the handler.
	 */
	private void handleCharacterData() {
		if (!buffer.isEmpty()) {
			byte[] data = buffer.toArray();
			buffer.clear();
			handler.handleString(new String(data));
		}
	}

	/**
	 * Pass binary data to the handler.
	 */
	private void handleBinaryData(int size) throws IOException {
		byte[] data = new byte[size];
		int bytesRead = source.read(data);
		if (bytesRead != size) {
			throw new EOFException();
		}
		handler.handleBinaryBytes(data);
	}


	private void handleGroupStart() {
		handleCharacterData();
		groupDepth++;
		handler.handleStartGroup();
	}


	private void handleGroupEnd() {
		handleCharacterData();
		handler.handleEndGroup();
		groupDepth--;
	}

}
