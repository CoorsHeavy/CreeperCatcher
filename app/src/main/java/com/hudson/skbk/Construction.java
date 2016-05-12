package com.hudson.skbk;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import org.jsoup.helper.StringUtil;

import com.hudson.skbk.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class Construction {
	Activity activity;
	Context context;
	Button quit;
	Button save;
	Button rate;
	private Button mSearchEdt;
	private TextWatcher mSearchTw;

	public Construction(Activity activity, Context context) {
		this.activity = activity;
		this.context = context;
	}

	public boolean execute(final String unused) {
		// TODO Auto-generated method stub
		final String[] names;
		final String[] ids;
		try {
			names = unused.split(";;")[0].split("\n");
			ids = unused.split(";;")[1].split("\n");
		} catch (Exception e) {
			return false;
		}
		activity.setContentView(R.layout.resultlayout);
		quit = (Button) activity.findViewById(R.id.backup);
		mSearchEdt = (Button) activity.findViewById(R.id.Search);
		quit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				context.startActivity(new Intent(context, MainActivity.class)
						.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
			}
		});
		save = (Button) activity.findViewById(R.id.save);
		save.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				AlertDialog.Builder alert = new AlertDialog.Builder(context);

				alert.setTitle("Enter a name for this list.");
				// Set an EditText view to get user input
				final EditText input = new EditText(context);
				alert.setView(input);
				alert.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								String value = input.getText().toString();
								// Do something with value!
								if (value == "")
									value = "list";
								try {
									ArrayList<String> filelist = new ArrayList<String>();
									boolean found = false;
									File[] files = activity.getFilesDir()
											.listFiles();
									filelist.clear();
									for (File file : files) {
										filelist.add(file.getName());
										Log.d("Hudson",file.getName());
									}
									int l = 0;
									while (true) {
										String str = value + ".txt";
										if(l != 0)str = value + String.valueOf(l) + ".txt";
										if (!filelist.contains(str)) {
											Log.d("Hudson","broken");
											break;
										} else {
											l = l + 1;
											Log.d("Hudson","still going");
										}
									}
									if(l != 0)
									value = value + String.valueOf(l);
									Log.d("Hudson", value + String.valueOf(l));
									FileOutputStream fOut = activity
											.openFileOutput(value + ".txt",
													context.MODE_PRIVATE);
									OutputStreamWriter osw = new OutputStreamWriter(
											fOut);
									// ---write the contents to the file---
									osw.write(unused);
									osw.flush();
									osw.close();
								} catch (Exception e) {
									// TODO Auto-generated catch block
									Toast.makeText(context,
											"Unable to save file",
											Toast.LENGTH_SHORT).show();
									e.printStackTrace();
								}
								dialog.dismiss();
							}
						});

				alert.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// Canceled.
							}
						});

				alert.show();
			}
		});
        ArrayList<String> items = new ArrayList<>();
        int length = names.length;
        int i = 0;
        while(i < length){
            items.add(String.valueOf(i + 1)+": "+names[i]);
            i++;
        }
        ListView listView = (ListView) activity.findViewById(R.id.listView1);
        listView.setTextFilterEnabled(true);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adapter);
		mSearchEdt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				final String appPackageName = context.getPackageName(); // getPackageName()
																// from
				// Context or Activity
				// object
				try {
					context.startActivity(new Intent(Intent.ACTION_VIEW, Uri
							.parse("market://details?id=com.hudson.skbkPro")));
				} catch (android.content.ActivityNotFoundException anfe) {
					context.startActivity(new Intent(
							Intent.ACTION_VIEW,
							Uri.parse("market://details?id=com.hudson.skbkPro"
									+ appPackageName)));
				}
			}
		});
		rate = (Button) activity.findViewById(R.id.rate);
		rate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				final String appPackageName = context.getPackageName(); // getPackageName()
																// from
				// Context or Activity
				// object
				try {
					context.startActivity(new Intent(Intent.ACTION_VIEW, Uri
							.parse("market://details?id=com.hudson.skbk")));
				} catch (android.content.ActivityNotFoundException anfe) {
					context.startActivity(new Intent(
							Intent.ACTION_VIEW,
							Uri.parse("market://details?id=com.hudson.skbk")));
				}
			}
		});
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					final int arg2, long arg3) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setCancelable(true);
				String[] list = { "Copy to Clipboard", "Go to Page" };
				builder.setItems(list, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						if (item == 0) {
							ClipboardManager clipboard = (ClipboardManager) context
									.getSystemService(Context.CLIPBOARD_SERVICE);
							ClipData clip = ClipData.newPlainText(names[arg2],
									names[arg2]);
							clipboard.setPrimaryClip(clip);
							Toast.makeText(context, "Copied",
									Toast.LENGTH_SHORT).show();
						}
						if (item == 1) {
							String uri = "http://facebook.com/" + ids[arg2];
							Intent intent = new Intent(Intent.ACTION_VIEW, Uri
									.parse(uri));
							context.startActivity(intent);
						}
					}
				});
				AlertDialog alert = builder.create();
				alert.show();
			}
		});
		
		return true;
	}
}
