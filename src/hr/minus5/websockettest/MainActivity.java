package hr.minus5.websockettest;

import com.codebutler.android_websockets.WebSocketFactory;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Build.VERSION_CODES;
import android.util.Log;
import android.view.Menu;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

@SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
public class MainActivity extends Activity {
	private WebSocketFactory wsFactory;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		WebView webView = (WebView) findViewById(R.id.webView);
		webView.getSettings().setJavaScriptEnabled(true);
		wsFactory = new WebSocketFactory(webView);
		webView.addJavascriptInterface(wsFactory, "WebSocketFactory");
		
		if (Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
			webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
		}
		
		webView.setWebChromeClient(new WebChromeClient(){
			@Override
			public boolean onConsoleMessage(ConsoleMessage p_consoleMessage) {
				Log.d("JSConsole", p_consoleMessage.message());
				return super.onConsoleMessage(p_consoleMessage);
			}
		});
		webView.loadUrl("file:///android_asset/test.html");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
}
