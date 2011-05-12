package bbth.engine.sound;

import java.util.ArrayList;
import java.util.List;

/**
 * A beat pattern composed of a sequence of smaller beat patterns
 * @author jardini
 *
 */
public class CompositeBeatPattern implements BeatPattern {

	private BeatPattern [] _patterns;
	private int [] _endIndices; // end index of each pattern
	private int _duration;
	private int _size;
	
	public CompositeBeatPattern(BeatPattern[] patterns) {
		assert(patterns.length > 1);
		
		_patterns = new BeatPattern[patterns.length];
		
		// set indices/offsets
		_endIndices = new int[patterns.length];
		int duration = 0, size = 0;
		List<Beat> beats = new ArrayList<Beat>();
		for (int i = 0; i < patterns.length; ++i) {
			beats.clear();
			BeatPattern pattern = patterns[i];
			// order important within this loop
			for (int beat = 0; beat < pattern.size(); ++beat) {
				Beat oldBeat = pattern.getBeat(beat);
				Beat newBeat = new Beat(oldBeat.type, oldBeat.duration);
				newBeat._startTime = oldBeat._startTime + duration;
				beats.add(newBeat);
			}
			_patterns[i] = new SimpleBeatPattern(duration, beats);
			duration += pattern.getDuration();
			size += pattern.size();
			_endIndices[i] = size;
		}
		_size = size;
		_duration = duration;
	}

	@Override
	// Linear in the number of patterns for now, so don't use a ton of patterns
	public Beat getBeat(int beatNumber) {
		if (beatNumber < 0 || beatNumber >= _size) return null;
		
		int pattern = 0;
		while (beatNumber >= _endIndices[pattern]) {
			++pattern;
		}
		beatNumber -= (pattern == 0) ? 0 : _endIndices[pattern - 1];
		return _patterns[pattern].getBeat(beatNumber);
	}
	
	public int getDuration() {
		return _duration;
	}

	@Override
	public int size() {
		return _size;
	}
}
