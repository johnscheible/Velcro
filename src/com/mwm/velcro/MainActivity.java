package com.mwm.velcro;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.mwm.velcro.MAKr.MAKErListener;

public class MainActivity extends Activity {
	@SuppressWarnings("unused")
	private static final String TAG = "Velcro";

	private MediaPlayer mPlayer;
	private SQLiteDatabase mDb;
	private Random mRandomGenerator = new Random();
	private MAKr mMakr;

	private Button mPlaybackButton, mStopButton, mFastButton, mSlowButton;

	final String rootDir = android.os.Environment.getExternalStorageDirectory()
			.getAbsolutePath();
	final String musicDir = rootDir + "/Music/";
	ArrayList<ArrayList<Song>> songs = new ArrayList<ArrayList<Song>>();

	Integer totalSongs = 0;
	Integer songMode = 0; // slow = 0, fast = 1
	Integer playingSongMode = 0;

	Uri songUri;

	private void stopPlayer() {
		if (mPlayer.isPlaying()) {
			mPlayer.stop();
			mPlayer.reset();
			try {
				mPlayer.setDataSource(MainActivity.this, songUri);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			// set playback to play
			mPlaybackButton.setTag(1);
			mPlaybackButton.setText(getResources().getString(R.string.play));
		}
	}

	private void changeSong() {
		int num_songs = songs.get(songMode).size();

		if (num_songs == 0) {
			Log.d(TAG, "No songs available");
			return;
		} else {
			int song_ind = mRandomGenerator.nextInt(songs.get(songMode).size());
			String filepath = musicDir
					+ songs.get(songMode).get(song_ind).getFilename();
			System.out.println(filepath);
			File file = new File(filepath);
			songUri = Uri.fromFile(file);
			mPlayer = MediaPlayer.create(MainActivity.this, songUri);
			playingSongMode = songMode;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		String db_fn = "/data/data/com.mwm.velcro/velcro.sqlite";

		// setup db
		try {
			mDb = SQLiteDatabase.openDatabase(db_fn, null, 0);
		} catch (SQLiteException e) {
			System.out.println(e.toString());
			Log.d(TAG, "Failed to open song database");
		} catch (NullPointerException e) {
			System.out.println(e.toString() + "; " + db_fn);
			Log.d(TAG, "Failed to open song database");
		}

		songs.add(0, new ArrayList<Song>()); // slow songs
		songs.add(1, new ArrayList<Song>()); // fast songs

		/*
		 * Categorize songs based on BPM. fast: BPM >= 120; slow: BPM < 120
		 */
		if (mDb != null) {
			Cursor q = mDb.query("songs", null, null, null, null, null, null);
			// Update number of songs loaded
			totalSongs = q.getCount();
			final TextView songsLoaded = (TextView) findViewById(R.id.song_count);
			songsLoaded.setText(getResources().getString(
					R.string.number_of_songs_loaded)
					+ " " + totalSongs.toString());
			// load songs into ArrayList
			q.moveToFirst();
			while (q.isAfterLast() == false) {
				String fn = q.getString(0);
				Integer bpm = q.getInt(1);

				Song s = new Song(fn, bpm);
				if (bpm >= 120) {
					songs.get(1).add(s);
				} else {
					songs.get(0).add(s);
				}

				q.moveToNext();
			}
		}

		// setup interface
		mPlaybackButton = (Button) findViewById(R.id.playback_button);
		mStopButton = (Button) findViewById(R.id.stop_button);
		mFastButton = (Button) findViewById(R.id.fast_button);
		mSlowButton = (Button) findViewById(R.id.slow_button);
		mPlaybackButton.setTag(1); // tag = 1 when paused
		mPlaybackButton.setText(getResources().getString(R.string.play));

		// setup player
		changeSong();

		mPlaybackButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				final int paused = (Integer) v.getTag();
				try {
					mPlayer.prepare();
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (paused == 1) {
					mPlayer.start();
					mPlaybackButton.setText(getResources().getString(
							R.string.pause));
					v.setTag(0); // pause
				} else {
					mPlayer.pause();
					mPlaybackButton.setText(getResources().getString(
							R.string.play));
					v.setTag(1); // play
				}
			}
		});

		mStopButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				stopPlayer();
			}
		});

		mFastButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				songMode = 1;
				if (playingSongMode != songMode && mPlayer.isPlaying()) {
					stopPlayer();
					changeSong();
				}
			}
		});

		mSlowButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				songMode = 0;
				if (playingSongMode != songMode && mPlayer.isPlaying()) {
					stopPlayer();
					changeSong();
				}
			}
		});

		// final Button analyzeButton = (Button)
		// findViewById(R.id.analyze_button);
		// analyzeButton.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// new Thread(new Runnable() {
		// @Override
		// public void run() {
		// Log.d(TAG, "Analyze button clicked");
		// EchoNestAPI echoNest = new EchoNestAPI("QFFBM2YO4ZECD5QYB");
		// Log.d(TAG, "Created API");
		// File song = new File("/sdcard/Music/song.mp3");
		// Log.d(TAG, "Accessed song");
		//
		// try {
		// Log.d(TAG, "Uploading track");
		// Track track = echoNest.uploadTrack(song, false);
		// Log.d(TAG, "Waiting for analysis...");
		// track.waitForAnalysis(30000);
		//
		// if (track.getStatus() == Track.AnalysisStatus.COMPLETE) {
		// TextView tv = new TextView(getApplicationContext());
		// tv.setText("BPM: " + track.getTempo());
		// LinearLayout status = (LinearLayout) findViewById(R.id.status_area);
		// status.addView(tv);
		// } else {
		// Log.d(TAG, "Track was not analyzed");
		// }
		// } catch (EchoNestException e) {
		// Log.e(TAG, "EchoNestException: " + e.getMessage());
		// e.printStackTrace();
		// } catch (IOException e) {
		// Log.e(TAG, "IOException on upload");
		// e.printStackTrace();
		// }
		// }
		// }).start();
		//
		// }
		// });

		mMakr = new MAKr(this);
		mMakr.addListener(new MAKErListener() {

			private boolean previous;

			@Override
			public void onRawDataReceived(byte[] buffer, int size) {

			}

			@Override
			public void onCommandReceived(String cmd, String value) {
				Log.d(TAG, "received: " + cmd + "::" + value);
			}
		});
		mMakr.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}