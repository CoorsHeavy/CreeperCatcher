package com.hudson.skbk;

import java.io.IOException;
import java.util.regex.PatternSyntaxException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.hudson.skbk.Load.OnTaskCompleted;

import android.support.v7.app.ActionBarActivity;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.HttpAuthHandler;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebView.FindListener;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class WebActivity extends Activity implements OnTaskCompleted {
	WebView webdriver;
	String ua = "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.4) Gecko/20100101 Firefox/4.0";
	ProgressDialog progDailog;

	// private boolean checkOne;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web);
		AlertDialog.Builder message = new AlertDialog.Builder(WebActivity.this);
		message.setMessage(
				"Once you have logged into Facebook and the news feed has loaded, wait as it might take time for the list loading process to initialize. This web browser does not remember your login information.")
				.setPositiveButton("Got it",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.dismiss();
							}
						}).setCancelable(true).create().show();
		webdriver = (WebView) findViewById(R.id.webview);
		CookieSyncManager.createInstance(this);
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeAllCookie();
		WebSettings webSettings = webdriver.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setUseWideViewPort(true);
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setBuiltInZoomControls(true);
		webdriver.setWebViewClient(new MyWebViewClient());
		webdriver.setWebChromeClient(new MyChromeViewClient());
		webdriver.addJavascriptInterface(new WebAppInterface(this), "Android");
		webdriver.getSettings().setUserAgentString(ua);
		progDailog = new ProgressDialog(this);
		progDailog.setMessage("Loading Page...");
		progDailog.setIndeterminate(false);
		progDailog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progDailog.setCancelable(false);

		webdriver.loadUrl("https://facebook.com");
	}
    @Override
    protected void onStop() {

        super.onStop();

        if (progDailog != null) {
            progDailog.dismiss();
            progDailog = null;
        }

    }
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Check if the key event was the Back button and if there's history
		try {
			if ((keyCode == KeyEvent.KEYCODE_BACK) && webdriver.canGoBack()) {
				webdriver.goBack();
				return true;
			}
		} catch (Exception e) {

		}
		// If it wasn't the Back key or there's no web page history, bubble up
		// to the default
		// system behavior (probably exit the activity)
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.web, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public class WebAppInterface {
		Context mContext;

		/** Instantiate the interface and set the context */
		WebAppInterface(Context c) {
			mContext = c;
		}

		/** Show a toast from the web page */
		@JavascriptInterface
		public void showToast(String toast) {
			Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
		}

		@JavascriptInterface
		public void showHTML(String html) {
			Log.d("Hudson", "showHTML");
            if(html != null){
			if (html.contains("InitialChatFriendsList")) {
                progDailog.dismiss();
                progDailog = null;
				Log.d("Hudson", "Found");
				Toast.makeText(mContext,
						"Wait a moment. Do not click any links.",
						Toast.LENGTH_SHORT);
				done = true;

				new Load(html, WebActivity.this, WebActivity.this,
						WebActivity.this).execute();
			} else {

				progDailog.dismiss();
				Toast.makeText(
						mContext,
						"Navigate to a page where you are signed into Facebook",
						Toast.LENGTH_LONG).show();
			}
		} else {
            progDailog.dismiss();
            Toast.makeText(
                    mContext,
                    "Navigate to a page where you are signed into Facebook",
                    Toast.LENGTH_LONG).show();
        }}
	}

	boolean done = false;

	public class MyWebViewClient extends WebViewClient {
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			// TODO Auto-generated method stub
			super.onPageStarted(view, url, favicon);
			Log.d("Hudson", url);
            if (done == false && progDailog != null)
				progDailog.show();
		}

		@Override
		public void onReceivedHttpAuthRequest(WebView view,
				HttpAuthHandler handler, String host, String realm) {
			// TODO Auto-generated method stub
			super.onReceivedHttpAuthRequest(view, handler, host, realm);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			// TODO Auto-generated method stub
			super.onPageFinished(view, url);
			Log.d("Hudson", "Finished Loading Page");
			if (done == false)
				injectJavaScript(
						webdriver,
						""
								// + "window.onload=function(){"
								+ "window.Android.showHTML"
								+ "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');"
				// + "}"
				);
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {

			return false;
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			Log.i("WEB_VIEW_TEST", "error code:" + errorCode);
            if(progDailog != null)
			progDailog.dismiss();
			error(description);
			super.onReceivedError(view, errorCode, description, failingUrl);
		}
	}

	public class MyChromeViewClient extends WebChromeClient {

		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			super.onProgressChanged(view, newProgress);
            if (progDailog != null) {
                progDailog.setProgress(newProgress);
            }
			// if (newProgress == 100)
			// progDailog.dismiss();
			// hide the progress bar if the loading is complete

		}
	}

	public void error(String reason) {
		// TODO Auto-generated method stub
		progDailog.dismiss();
		AlertDialog.Builder message = new AlertDialog.Builder(WebActivity.this);
		message.setTitle("Error")
				.setMessage(
						"Failed to load list or webpage. Usually caused by a lack of or interrupted internet connection.\nSpecific Reason:\n"
								+ reason)
				.setPositiveButton("Try Again",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								getApplicationContext()
										.startActivity(
												new Intent(
														getApplicationContext(),
														MainActivity.class)
														.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
							}
						}).setCancelable(false).create().show();
	}

	@Override
	public void onTaskCompleted(String unused) {
		// TODO Auto-generated method stub
		webdriver = null;
		Construction setter = new Construction(this, this);
		setter.execute(unused);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		// Save the state of the WebView
		try {
			webdriver.saveState(outState);
		} catch (Exception e) {

		}
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		// Restore the state of the WebView
		webdriver.restoreState(savedInstanceState);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	public void injectJavaScript(WebView view, final String javascript) {

		if (javascript == null || javascript.isEmpty()) {
			return;
		}

		// As of KitKat, evaluateJavascript(String javascript) should be used
		// over loadAd("javascript:(javaScriptMethod())")
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {

			String js = "javascript:" + javascript;
			view.loadUrl(js);
		} else {
			// Returns the value of the executed JavaScript as a JSON string.
			view.evaluateJavascript(javascript, new ValueCallback<String>() {
				@TargetApi(Build.VERSION_CODES.HONEYCOMB)
				@Override
				public void onReceiveValue(String stringCallback) {
					// If you'd like any response from the JS to confirm the
					// execution
					Log.d("Hudson", "onRecievedValue");
				}
			});
		}
	}

	@Override
	public void onTaskStarted() {
		// TODO Auto-generated method stub
        if (progDailog != null) {
            progDailog.dismiss();
            progDailog = null;
        }
	}
}
