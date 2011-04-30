package bbth.sound;

import java.util.ArrayList;
import java.util.List;

/**
 * Tracks the beats of a song given a music player and a beat pattern
 * @author jardini
 *
 */
public class BeatTracker {

	private MusicPlayer _musicPlayer;
	private BeatPattern _beatPattern;
	private List<Integer> _offsets;
	private int _currentBeat;
	
	public BeatTracker(MusicPlayer player, BeatPattern pattern) {
		_musicPlayer = player;
		_beatPattern = pattern;
		_currentBeat = 0;
		_offsets = new ArrayList<Integer>();
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
	
	public List<Integer> getBeatOffsetsInRange(int lowerBound, int upperBound) {
		_offsets.clear();
		
		int beat = getClosestBeat();
		int currTime = _musicPlayer.getCurrentPosition();
		int offset = _beatPattern.getBeatTime(beat) - currTime;
		while (offset > lowerBound) {
			_offsets.add(offset);
			--beat;
			offset = _beatPattern.getBeatTime(beat);
			if (offset == Integer.MIN_VALUE) break;
			offset -= currTime;
		}
		
		beat = _currentBeat + 1;
		offset = _beatPattern.getBeatTime(beat) - currTime;
		while (offset < upperBound) {
			_offsets.add(offset);
			++beat;
			offset = _beatPattern.getBeatTime(beat);
			if (offset == Integer.MIN_VALUE) break;
			offset -= currTime;
		}
		
		return _offsets;
	}
}
