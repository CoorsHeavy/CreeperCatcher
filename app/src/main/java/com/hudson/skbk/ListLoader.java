package com.hudson.skbk;

import java.io.File;

import android.support.v7.app.ActionBarActivity;
import android.view.Window;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class ListLoader extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		Bundle b = getIntent().getExtras();
		Constructioned setter = new Constructioned(
				ListLoader.this, ListLoader.this);
		if (b.getString("line") != "")
			setter.execute(b.getString("line"), b.getString("name"), new File(b.getString("filed")));
	}
	@Override
	public void onBackPressed() {
		startActivity(new Intent(this, MainActivity.class)
				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
				.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
	}

}
