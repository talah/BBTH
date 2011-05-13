package bbth.engine.sound;

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
	private Context _context;
	
	public SoundManager(Context context, int maxSimultaneousSounds) {
		_soundPool = new SoundPool(maxSimultaneousSounds, AudioManager.STREAM_MUSIC, 0);
		_context = context;
	}
		
	public int addSound(int resourceId) {
	    return _soundPool.load(_context, resourceId, 1);
	}

	// plays a sound once
	public void play(int soundId) {
		_soundPool.play(soundId, 1.f, 1.f, 1, 0, 1.f);
	}
	
	public void play(int soundId, float volume) {
		_soundPool.play(soundId, volume, volume, 1, 0, 1.f);
	}
	
	// loops a sound
	public void loop(int soundId) {
		_soundPool.play(soundId, 1.f, 1.f, 1, -1, 1.f);
	}

	// loops a sound
	public void loop(int soundId, float volume) {
		_soundPool.play(soundId, volume, volume, 1, -1, 1.f);
	}
	
	// stops a sound
	public void stop(int soundId) {
		_soundPool.stop(soundId);
	}

	// release the resources used by the sound manager
	// call this if the sound manager is not used for the entire duration of the application
	public void releaseResources() {
		_soundPool.release();
		_soundPool = null;
	}
}
