package bbth.sound;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Tracks the beats of a song given a music player and a beat pattern
 * @author jardini
 *
 */
public class BeatTracker {

	public static final float TOLERANCE = 80; // millisecond difference in what is still considered a valid touch

	private MusicPlayer _musicPlayer;
	private BeatPattern _beatPattern;
	private List<Beat> _beats;
	private int _currentBeatIndex;
	
	public BeatTracker(MusicPlayer player, BeatPattern pattern) {
		_musicPlayer = player;
		_beatPattern = pattern;
		_currentBeatIndex = 0;
		_beats = new ArrayList<Beat>();
	}

	// returns whether a beat was successfully tapped
	public boolean onTouchDown() {
		Beat beat = getClosestBeat();
		return beat != null && beat.onTouchDown(_musicPlayer.getCurrentPosition());
	}
	
	// handle a release
	public void onTouchUp() {
		// does nothing for now
	}
		
	// return index into BeatPattern for closest beat
	public Beat getClosestBeat() {
		int currTime = _musicPlayer.getCurrentPosition();
		Beat beat = _beatPattern.getBeat(_currentBeatIndex);
		Beat nextBeat = _beatPattern.getBeat(_currentBeatIndex + 1);
		while (beat != null && nextBeat != null && Math.abs(currTime - nextBeat._startTime) < Math.abs(currTime - beat._startTime)) {
			++_currentBeatIndex;
			beat = _beatPattern.getBeat(_currentBeatIndex);
			nextBeat = _beatPattern.getBeat(_currentBeatIndex + 1);
		}
		
		return beat;
	}
	
	// get all the beats in a time window relative to the current time in the song being played
	public List<Beat> getBeatsInRange(int lowerBound, int upperBound) {
		_beats.clear();
		
		Beat beat = getClosestBeat();
		int i = _currentBeatIndex;
		int currTime = _musicPlayer.getCurrentPosition();
		
		while (beat != null && beat.getEndTime() - currTime > lowerBound) {
			_beats.add(beat);
			--i;
			beat = _beatPattern.getBeat(i);
		}
		
		i = _currentBeatIndex + 1;
		beat = _beatPattern.getBeat(i);
		while (beat != null && beat._startTime - currTime < upperBound) {
			_beats.add(beat);
			++i;
			beat = _beatPattern.getBeat(i);
		}
		
		return _beats;
	}
	
	// draw a list of beats, likely obtained from getBeatsInRange
	public void drawBeats(List<Beat> beats, float xMid, float yMid, Canvas canvas, Paint paint) {
		int time = _musicPlayer.getCurrentPosition();
		for (int i = 0; i < beats.size(); ++i) {
			_beats.get(i).draw(time, xMid, yMid, canvas, paint);
		}
	}
}
