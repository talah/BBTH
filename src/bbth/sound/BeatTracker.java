package bbth.sound;

/**
 * Tracks the beats of a song given a music player and a beat pattern
 * @author jardini
 *
 */
public class BeatTracker {

	private MusicPlayer _musicPlayer;
	private BeatPattern _beatPattern;
	private int _currentBeat;
	
	public BeatTracker(MusicPlayer player, BeatPattern pattern) {
		_musicPlayer = player;
		_beatPattern = pattern;
		_currentBeat = 0;
	}

	// returns millisecond time difference from closest beat to current time
	public int getClosestBeatOffset() {
		int currTime = _musicPlayer.getCurrentPosition();
		int beatTime = _beatPattern.getBeatTime(_currentBeat);
		while (Math.abs(currTime - _beatPattern.getBeatTime(_currentBeat + 1)) < Math.abs(currTime - beatTime)) {
			++_currentBeat;
			beatTime = _beatPattern.getBeatTime(_currentBeat);
		}
		
		return beatTime - currTime;
	}
	
	// return index into BeatPattern for closest beat
	public int getClosestBeat() {
		int currTime = _musicPlayer.getCurrentPosition();
		int beatTime = _beatPattern.getBeatTime(_currentBeat);
		while (Math.abs(currTime - _beatPattern.getBeatTime(_currentBeat + 1)) < Math.abs(currTime - beatTime)) {
			++_currentBeat;
			beatTime = _beatPattern.getBeatTime(_currentBeat);
		}
		
		return _currentBeat;
	}
}
