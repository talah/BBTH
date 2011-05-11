package bbth.engine.sound;

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
		
		_patterns = patterns;
		
		// set indices/offsets
		_endIndices = new int[patterns.length];
		int duration = 0, size = 0;
		for (int i = 0; i < patterns.length; ++i) {
			BeatPattern pattern = patterns[i];
			// order important within this loop
			for (int beat = 0; beat < pattern.size(); ++beat) {
				pattern.getBeat(beat)._startTime += duration;
			}
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
