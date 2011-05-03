package bbth.sound;

/**
 * A beat pattern composed of a sequence of smaller beat patterns
 * @author jardini
 *
 */
public class CompositeBeatPattern implements BeatPattern {

	private BeatPattern [] _patterns;
	private int [] _offsets;
	private int _duration;
	private int _size;
	
	public CompositeBeatPattern(BeatPattern[] patterns) {
		assert(patterns.length > 1);
		_patterns = patterns;
		_offsets = new int[patterns.length];
		int duration = 0, size = 0;
		for (int i = 0; i < patterns.length; ++i) {
			_offsets[i] = duration;
			duration += patterns[i].getDuration();
			size += patterns[i].size();
		}
		_size = size;
		_duration = duration;
	}

	@Override
	public Beat getBeat(int beatNumber) {
		int i = 0;
		while (beatNumber >= _offsets[i + 1]) {
			++i;
		}
		beatNumber -= _offsets[i];
		return _patterns[i].getBeat(beatNumber);
	}
	
	public int getDuration() {
		return _duration;
	}

	@Override
	public int size() {
		return _size;
	}
}
