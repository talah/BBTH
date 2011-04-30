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
	
	// returns -1 if the time is past the end of the song
	public int getBeatTime(int beatNumber) {
		if (beatNumber < _beatTimes.size()) {
			return _beatTimes.get(beatNumber);
		}
		return _looping ? _beatTimes.get(beatNumber % _beatTimes.size()) : -1;
	}
}
