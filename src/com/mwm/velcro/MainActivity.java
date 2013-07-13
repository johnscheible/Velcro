package com.mwm.velcro;

import java.io.File;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
