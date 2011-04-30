package bbth.sound;

import java.util.List;

/**
 * A custom beat pattern based on a list of beat lengths
 * @author justin
 *
 */
public class CustomBeatPattern implements BeatPattern {

	private List<Integer> _beatTimes;
	private boolean _looping;
	
	// pattern will loop after last beat only if loop is set to true
	public CustomBeatPattern(int firstBeatOffset, List<Integer> beatLengths, boolean loop) {
		int currTime = firstBeatOffset;
		for (int beatLength : beatLengths) {
			_beatTimes.add(currTime);
			currTime += beatLength;
		}
	}
	
	// returns MIN_VALUE if the beat number is invalid
	public int getBeatTime(int beatNumber) {
		if (beatNumber < 0) {
			return Integer.MIN_VALUE;
		}
		if (beatNumber < _beatTimes.size()) {
			return _beatTimes.get(beatNumber);
		}
		return _looping ? _beatTimes.get(beatNumber % _beatTimes.size()) : Integer.MIN_VALUE;
	}
}
