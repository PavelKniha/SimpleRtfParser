package com.kniha.paul.handlers;

import com.kniha.paul.utils.RtfComands;

public abstract class BaseRtfHandler implements RtfHandler{

	@Override
	public void handleStartDocument() {	
	}

	@Override
	public void handleEndDocument() {		
	}

	@Override
	public void handleStartGroup() {		
	}

	@Override
	public void handleEndGroup() {		
	}

	@Override
	public void handleCharacterBytes(byte[] data) {		
	}

	@Override
	public void handleBinaryBytes(byte[] data) {		
	}

	@Override
	public void handleString(String string) {		
	}

	@Override
	public void handleCommand(RtfComands command, int param, boolean hasParam,
			boolean optional) {		
	}

}
