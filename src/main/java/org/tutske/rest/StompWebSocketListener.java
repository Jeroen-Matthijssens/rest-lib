package org.tutske.rest;

import org.eclipse.jetty.io.RuntimeIOException;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.tutske.util.stomp.StompFrame;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;


public abstract class StompWebSocketListener extends WebSocketAdapter implements WebSocketListener {

	public abstract void onWebSocketStomp (StompFrame frame);

	@Override
	public void onWebSocketBinary (byte[] bytes, int start, int length) {
		onWebSocketStomp (StompFrame.fromRaw (bytes, start, length));
	}

	@Override
	public void onWebSocketText (String s) {
		byte [] data = s.getBytes (Charset.forName ("utf-8"));
		onWebSocketBinary (data, 0, data.length);
	}

	public void sendFrame (StompFrame frame) {
		try { getRemote ().sendBytes (ByteBuffer.wrap (frame.raw ())); }
		catch (IOException e) { throw new RuntimeIOException (e); }
	}

}
