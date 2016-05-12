package com.hudson.skbk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class MainActivity extends Activity {

	ListView lv;
	TextView empty;

	@Override
	protected void onStart() {
		super.onStart();
		// The activity is about to become visible.
		loadlist();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// The activity has become visible (it is now "resumed").
		loadlist();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
        Tracker t = ((Tracking) this.getApplication()).getTracker(
                Tracking.TrackerName.APP_TRACKER);

// Set screen name.
        t.setScreenName("screenName");

// Send a screen view.
        t.send(new HitBuilders.AppViewBuilder().build());
		setContentView(R.layout.activity_main);

		lv = (ListView) findViewById(R.id.listVi);
		loadlist();
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					final int arg2, long arg3) {
				try {
					String line = "";
					String name = "";
					File filed = null;
					File[] files = getFilesDir().listFiles();
					for (File file : files) {
						if (file.getName()
								.substring(0, file.getName().length() - 4)
								.contentEquals(
										arg0.getAdapter().getItem(arg2)
												.toString())) {
							InputStream inputStream = openFileInput(file
									.getName());

							if (inputStream != null) {
								InputStreamReader inputStreamReader = new InputStreamReader(
										inputStream);
								BufferedReader bufferedReader = new BufferedReader(
										inputStreamReader);
								String receiveString = "";
								StringBuilder stringBuilder = new StringBuilder();

								while ((receiveString = bufferedReader
										.readLine()) != null) {
									stringBuilder.append(receiveString);
									stringBuilder.append("\n");
								}

								inputStream.close();
								line = stringBuilder.toString();
								name = file.getName().substring(0,
										file.getName().length() - 4);
								filed = file;
							}
						}
						Log.d("Hudson", line);
					}
					Intent intent = new Intent(MainActivity.this, ListLoader.class);
					Bundle b = new Bundle();
					b.putString("line", line);
					b.putString("name", name);
					b.putString("filed", filed.getAbsolutePath());
					intent.putExtras(b); //Put your id to your next Intent 
					startActivity(intent);
					finish(); 
				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(getApplicationContext(),
							"Unable to open file.", Toast.LENGTH_SHORT).show();
					Log.d("hudson", "sdsa");
				}
			}
		});
	}

	private void loadlist() {
		// TODO Auto-generated method stub
		try {
			ArrayList<String> filelist = new ArrayList<String>();
			File[] files = getFilesDir().listFiles();
			filelist.clear();
			for (File file : files) {
				if (file.getName().endsWith(".txt")) {
					filelist.add(file.getName().substring(0,
							file.getName().length() - 4));
				}
			}

			ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, filelist);
			lv.setAdapter(arrayAdapter);
			empty = (TextView) findViewById(android.R.id.empty);
			lv.setEmptyView(viewseer(empty));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private View viewseer(TextView empty2) {
		// TODO Auto-generated method stub
		empty2.setVisibility(View.VISIBLE);
		return empty2;
	}

	public void start(View view) {
		this.startActivity(new Intent(this, WebActivity.class));
	}

	public void tutorial(View view) {
		this.startActivity(new Intent(this, LessonActivity.class));
	}

	public void exit(View view) {
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(1);
	}

	public void paid(View view) {
		final String appPackageName = getPackageName(); // getPackageName() from
														// Context or Activity
														// object
		try {
			startActivity(new Intent(Intent.ACTION_VIEW,
					Uri.parse("market://details?id=com.hudson.skbkPro")));
		} catch (android.content.ActivityNotFoundException anfe) {
			startActivity(new Intent(
					Intent.ACTION_VIEW,
					Uri.parse("market://details?id=com.hudson.skbkPro")));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

}
