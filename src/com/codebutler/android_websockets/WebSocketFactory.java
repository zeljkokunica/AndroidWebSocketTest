package com.codebutler.android_websockets;

import java.util.Random;

import android.webkit.WebView;

public class WebSocketFactory {

	/** The app view. */
	WebView appView;

	/**
	 * Instantiates a new web socket factory.
	 * 
	 * @param appView
	 *            the app view
	 */
	public WebSocketFactory(WebView appView) {
		this.appView = appView;
	}

	public WebSocket getInstance(String url) {
		// use Draft75 by default
		return new WebSocket(getRandonUniqueId(), appView, url);
	}
	/**
	 * Generates random unique ids for WebSocket instances
	 * 
	 * @return String
	 */
	private String getRandonUniqueId() {
		return "WEBSOCKET." + new Random().nextInt(100);
	}

}
