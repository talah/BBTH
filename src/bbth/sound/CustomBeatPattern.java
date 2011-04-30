package bbth.sound;

/**
 * A custom beat pattern based on a list of beat lengths
 * @author justin
 *
 */
public class CustomBeatPattern implements BeatPattern {

	private int[] _beatTimes;
	private boolean _looping;
	private int _loopSum;
	
	// pattern will loop after last beat only if loop is set to true
	public CustomBeatPattern(int firstBeatOffset, int []beatLengths, boolean loop) {
		_beatTimes = new int[beatLengths.length];
		int currTime = firstBeatOffset;
		for (int i = 0; i < beatLengths.length; ++i) {
			_beatTimes[i] = currTime;
			currTime += beatLengths[i];
		}
		_loopSum = currTime - firstBeatOffset;
		_looping = loop;
	}
	
	// returns MIN_VALUE if the beat number is invalid
	public int getBeatTime(int beatNumber) {
		if (beatNumber < 0) {
			return Integer.MIN_VALUE;
		}
		if (beatNumber < _beatTimes.length) {
			return _beatTimes[beatNumber];
		}
		if (_looping) {
			return _loopSum * (beatNumber / _beatTimes.length) + _beatTimes[beatNumber % _beatTimes.length];
		}
		return Integer.MIN_VALUE;
	}
}
