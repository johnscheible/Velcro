package com.mwm.velcro;

public class Song {
	private String filename;
	private Integer bpm;

	Song(String fn, Integer b) {
		filename = fn;
		bpm = b;
	}

	public String getFilename() {
		return filename;
	}

	public Integer getBPM() {
		return bpm;
	}
	
	public String toString() {
		return "('" + filename + "', " + bpm + ")";
	}
}
