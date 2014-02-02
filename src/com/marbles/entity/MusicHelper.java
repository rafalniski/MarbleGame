package com.marbles.entity;

import java.io.IOException;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.engine.Engine;

import com.marbles.activity.MarbleGameActivity;
 
public class MusicHelper {
	public static Music moveMusic, scoreMusic, failMusic, gameOverMusic;
	public static void initSounds(Engine mEngine, MarbleGameActivity activity) {
		try {
			failMusic = MusicFactory.createMusicFromAsset(
					mEngine.getMusicManager(), activity, "snd/fail.mp3");
			moveMusic = MusicFactory.createMusicFromAsset(
					mEngine.getMusicManager(), activity, "snd/moveMusic.wav");
			scoreMusic = MusicFactory.createMusicFromAsset(
					mEngine.getMusicManager(), activity, "snd/scoreMusic.mp3");
			gameOverMusic = MusicFactory.createMusicFromAsset(
					mEngine.getMusicManager(), activity,
					"snd/gameOverMusic.wav");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
