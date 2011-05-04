package bbth.engine.sound;

import java.util.HashMap;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

/**
 * Manages sound effects used by a game.  
 * 
 * @author jardini
 */
public class SoundManager {
	private SoundPool _soundPool;
	private HashMap<Integer, Integer> _soundMap;
	private AudioManager _audioManager;
	
	public SoundManager(Context context, int maxSimultaneousSounds) {
		_soundPool = new SoundPool(maxSimultaneousSounds, AudioManager.STREAM_MUSIC, 0);
		_soundMap = new HashMap<Integer, Integer>();
		_audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
	}
		
	public void addSound(int index, int soundId, Context context) {
	    _soundMap.put(index, _soundPool.load(context, soundId, 1));
	}

	// plays a sound once
	public void play(int index) {
		float streamVolume = _audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		streamVolume /= _audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		_soundPool.play(_soundMap.get(index), streamVolume, streamVolume, 1, 0, 1.f);
	}
	
	// loops a sound
	public void loop(int index) {
		float streamVolume = _audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		streamVolume /= _audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		_soundPool.play(_soundMap.get(index), streamVolume, streamVolume, 1, -1, 1.f);
	}

	// stops a sound
	public void stop(int index) {
		_soundPool.stop(_soundMap.get(index));
	}

	// release the resources used by the sound manager
	// call this if the sound manager is not used for the entire duration of the application
	public void releaseResources() {
		_soundPool.release();
		_soundPool = null;
	}
}
