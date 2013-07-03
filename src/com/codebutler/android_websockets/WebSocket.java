package com.codebutler.android_websockets;

import java.net.URI;
import java.util.Arrays;

import org.apache.http.message.BasicNameValuePair;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.webkit.WebView;

import com.codebutler.android_websockets.WebSocketClient.Listener;


public class WebSocket {
	/**
	 * The connection has not yet been established.
	 */
	public final static int WEBSOCKET_STATE_CONNECTING = 0;
	/**
	 * The WebSocket connection is established and communication is possible.
	 */
	public final static int WEBSOCKET_STATE_OPEN = 1;
	/**
	 * The connection is going through the closing handshake.
	 */
	public final static int WEBSOCKET_STATE_CLOSING = 2;
	/**
	 * The connection has been closed or could not be opened.
	 */
	public final static int WEBSOCKET_STATE_CLOSED = 3;

	/**
	 * An empty string
	 */
	private static String BLANK_MESSAGE = "";
	/**
	 * The javascript method name for onOpen event.
	 */
	private static String EVENT_ON_OPEN = "onopen";
	/**
	 * The javascript method name for onMessage event.
	 */
	private static String EVENT_ON_MESSAGE = "onmessage";
	/**
	 * The javascript method name for onClose event.
	 */
	private static String EVENT_ON_CLOSE = "onclose";
	/**
	 * The javascript method name for onError event.
	 */
	private static String EVENT_ON_ERROR = "onerror";
	
	private WebSocketClient client;
	private Handler mainThreadHandler;
	private WebView webView;
	private String url;
	private String id;
	private int readyState = WEBSOCKET_STATE_CONNECTING;
	
	public WebSocket(String id, WebView p_webView, String p_url) {
		super();
		this.id = id;
		webView = p_webView;
		url = p_url;
		client = new WebSocketClient(URI.create(url), new Listener(){
			private void loadUrl(String data) {
				Message message = new Message();
				message.getData().putString("command", "towebview");
				message.getData().putString("data", data);
				mainThreadHandler.sendMessage(message);
			}
			@Override
			public void onConnect() {
				setReadyState(WEBSOCKET_STATE_OPEN);
				loadUrl(buildJavaScriptData(EVENT_ON_OPEN, BLANK_MESSAGE));
			}

			@Override
			public void onMessage(String p_message) {
				loadUrl(buildJavaScriptData(EVENT_ON_MESSAGE, p_message));
			}

			@Override
			public void onMessage(byte[] p_data) {
			}

			@Override
			public void onDisconnect(int p_code, String p_reason) {
				setReadyState(WEBSOCKET_STATE_CLOSED);
				loadUrl(buildJavaScriptData(EVENT_ON_CLOSE, BLANK_MESSAGE));
			}

			@Override
			public void onError(Exception p_error) {
				loadUrl(buildJavaScriptData(EVENT_ON_ERROR, p_error.getMessage()));
			}}, 
			Arrays.asList(new BasicNameValuePair("Cookie", "session=abcd")));
		mainThreadHandler = new MainThreadHandler(Looper.getMainLooper(), webView, client);
		setReadyState(readyState);
		client.connect();
	}
	
	static class MainThreadHandler extends Handler {
		WebSocketClient client;
		WebView webView;
		public MainThreadHandler(Looper p_looper, WebView webView, WebSocketClient client) {
			super(p_looper);
			this.client = client;
			this.webView = webView;
		}

		@Override
		public void handleMessage(final Message p_msg) {
			String command = p_msg.getData().getString("command");
			if ("send".equals(command)) {
				this.client.send(p_msg.getData().getString("data"));
			}
			else if ("close".equals(command)) {
				this.client.disconnect();
			}
			else if ("towebview".equals(command)) {
				this.webView.loadUrl(p_msg.getData().getString("data"));
			}
		}
	}
	
	
	public String getId() {
		return id;
	}

	private String buildJavaScriptData(String event, String msg) {
		String _d = "javascript:WebSocket." + event + "(" + "{" + "\"_target\":\"" + id + "\"," + "\"data\":'" + msg.replaceAll("'", "\\\\'")
				+ "'" + "}" + ")";
		return _d;
	}
	
	public void send(String data) {
		Message message = new Message();
		message.getData().putString("command", "send");
		message.getData().putString("data", data);
		mainThreadHandler.sendMessage(message);
	}
	
	public void close() {
		setReadyState(WEBSOCKET_STATE_CLOSED);
		Message message = new Message();
		message.getData().putString("command", "close");
		mainThreadHandler.sendMessage(message);
	}

	public int getReadyState() {
		return readyState;
	}

	public void setReadyState(int p_readyState) {
		readyState = p_readyState;
		Message message = new Message();
		message.getData().putString("command", "towebview");
		message.getData().putString("data", buildJavaScriptData("onreadystatechanged", String.valueOf(p_readyState)));
		mainThreadHandler.sendMessage(message);
	}
	
}
