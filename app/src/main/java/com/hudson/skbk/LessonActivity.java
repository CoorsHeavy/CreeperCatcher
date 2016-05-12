package com.hudson.skbk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebView;

public class LessonActivity extends Activity {
	WebView mAboutWebText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lesson);
		String webText = "IT_about.html";

		mAboutWebText = (WebView) findViewById(R.id.about_html_text);

		String aboutText = "";
		try {
			InputStream is = getAssets().open(webText);
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			String line;

			while ((line = reader.readLine()) != null) {
				aboutText += line;
			}
		} catch (IOException e) {
			Log.e("Hudson", "About html loading failed");
		}

		mAboutWebText.loadData(aboutText, "text/html", "UTF-8");

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.lesson, menu);
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
}
