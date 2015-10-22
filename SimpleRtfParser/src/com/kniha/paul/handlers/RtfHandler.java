package com.kniha.paul.handlers;

import com.kniha.paul.utils.RtfComands;

/**
 * Listener interface to receive events from parser
 */
public interface RtfHandler {

	public void handleStartDocument();

	public void handleEndDocument();

	public void handleStartGroup();

	public void handleEndGroup();

	public void handleCharacterBytes(byte[] data);

	public void handleBinaryBytes(byte[] data);

	public void handleString(String string);

	public void handleCommand(RtfComands command, int param,
			boolean hasParam, boolean optional);
}
