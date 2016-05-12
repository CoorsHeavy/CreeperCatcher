package com.hudson.skbk;

import java.io.IOException;
import java.lang.reflect.Array;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;

import com.hudson.skbk.WebActivity.MyChromeViewClient;
import com.hudson.skbk.WebActivity.MyWebViewClient;
import com.hudson.skbk.WebActivity.WebAppInterface;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.HttpAuthHandler;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

class Load extends AsyncTask<String, String, String> {
	Context mCont;
	ProgressDialog progDailog;
	String html;
	Activity act;
	String lastString = "";
	private OnTaskCompleted listener;

	public Load(String html, Context c, OnTaskCompleted listener, Activity act) {
		this.act = act;
		this.mCont = c;
		this.html = html;
		this.listener = listener;
		progDailog = new ProgressDialog(mCont);
	}

	public interface OnTaskCompleted {
		void onTaskCompleted(String unused);
		void onTaskStarted();
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		Log.d("Hudson", "Started");
		progDailog = new ProgressDialog(mCont);
		progDailog.setMessage("Loading List...");
		progDailog.setIndeterminate(false);
		progDailog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progDailog.setCancelable(false);
		
		progDailog.show();
	}

	@Override
	protected String doInBackground(String... aurl) {
		// do something while spinning circling show
		listener.onTaskStarted();
		String results = "";
		String ids = "";
		String starterPack = "InitialChatFriendsList\",[],{\"list\":[\"";
		String start = html.substring(html.indexOf(starterPack)
				+ starterPack.length());
		String list = start.substring(0, start.indexOf("]"));
		String[] a = list.split("\",\"");
        if(a.length == 1)nofriends();
		int turn = 0;
		int index = 0;
		try {
			while (turn < 5) {
				try {
					String html = Jsoup
							.connect(
									"http://graph.facebook.com/"
											+ trim(a[index])).ignoreContentType(true).get().text();
					String subname = html.substring(html.indexOf("\"name") + 8);
					String name = subname.substring(0, subname.indexOf("\""));
					results = results
							+ name + "\n";
					ids = ids + trim(a[index]) + "\n";
					turn++;
					progDailog.setProgress(turn * 20);
				} catch (HttpStatusException e) {
					// TODO Auto-generated catch block
					Log.d("Hudson", "Screw Up");
					if (e.getLocalizedMessage().contains("HTTP error fetching")) {

					}
				} catch (ArrayIndexOutOfBoundsException e) {
					break;
				}
                catch (StringIndexOutOfBoundsException e) {
                    break;
                }
				index++;
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			error(e.getLocalizedMessage());
			return "";
		}
		Log.d("hudson", results);
		return results + ";;" + ids;
	}

	@Override
	protected void onPostExecute(String unused) {
		super.onPostExecute(unused);
		progDailog.dismiss();
		listener.onTaskCompleted(unused);
	}

	String trim(String input) {
		return input.substring(0, input.indexOf("-"));
	}

	public void error(final String reason) {
		// TODO Auto-generated method stub
		progDailog.dismiss();
		act.runOnUiThread(new Runnable() {
			public void run() {
				AlertDialog.Builder message = new AlertDialog.Builder(mCont);
				message.setTitle("Error")
						.setMessage(
								"Failed to load list or webpage. Usually caused by a lack of or interrupted an internet connection.\nSpecific Reason:\n"
										+ reason)
						.setPositiveButton("Try Again",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										mCont.startActivity(new Intent(mCont,
												MainActivity.class)
												.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
									}
								}).setCancelable(true).create().show();
			}
		});
	}
    public void nofriends() {
        // TODO Auto-generated method stub
        Log.d("Hudson", "No Friends");
        progDailog.dismiss();
        act.runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder message = new AlertDialog.Builder(mCont);
                message.setTitle("Error")
                        .setMessage(
                                "Not enough friends interact with your profile for a results list to be produced. Try a separate account.")
                        .setPositiveButton("Try Again",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        mCont.startActivity(new Intent(mCont,
                                                MainActivity.class)
                                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                    }
                                }).setCancelable(true).create().show();
            }
        });
    }
}