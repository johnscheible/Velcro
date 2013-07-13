package com.mwm.velcro;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.Track;

public class MainActivity extends Activity {
	private static final String TAG = "Velcro";
	
	MediaPlayer mPlayer;
	
	final File rootDir = android.os.Environment.getExternalStorageDirectory();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		final Button button = (Button) findViewById(R.id.music_button);
		button.setTag(1); // tag = 1 when paused
		button.setText("Play");
		
		String filepath = rootDir.getAbsolutePath() + "/Music/song.mp3";
		File file = new File(filepath);
		Uri uri = Uri.fromFile(file);
		
		mPlayer = MediaPlayer.create(MainActivity.this, uri);
		
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				final int stopped = (Integer) v.getTag();
				
				if(stopped == 1) {
					mPlayer.start();
					button.setText("Pause");
					v.setTag(0); // pause
				} else {
					mPlayer.pause();
					button.setText("Play");
					v.setTag(1);
				}
			}
		});
		
//		final Button analyzeButton = (Button) findViewById(R.id.analyze_button);
//		analyzeButton.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				new Thread(new Runnable() {
//					@Override
//					public void run() {
//						Log.d(TAG, "Analyze button clicked");
//						EchoNestAPI echoNest = new EchoNestAPI("QFFBM2YO4ZECD5QYB");
//						Log.d(TAG, "Created API");
//						File song = new File("/sdcard/Music/song.mp3");
//						Log.d(TAG, "Accessed song");
//						
//						try {
//							Log.d(TAG, "Uploading track");
//							Track track = echoNest.uploadTrack(song, false);
//							Log.d(TAG, "Waiting for analysis...");
//							track.waitForAnalysis(30000);
//							
//							if (track.getStatus() == Track.AnalysisStatus.COMPLETE) {
//								TextView tv = new TextView(getApplicationContext());
//								tv.setText("BPM: " + track.getTempo());
//								LinearLayout status = (LinearLayout) findViewById(R.id.status_area);
//								status.addView(tv);
//							} else {
//								Log.d(TAG, "Track was not analyzed");
//							}
//						} catch (EchoNestException e) {
//							Log.e(TAG, "EchoNestException: " + e.getMessage());
//							e.printStackTrace();
//						} catch (IOException e) {
//							Log.e(TAG, "IOException on upload");
//							e.printStackTrace();
//						}	
//					}
//				}).start();
//				
//			}
//		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
