package bbth.sound;

/**
 * A simple beat pattern where every beat is the same length
 * @author jardini
 *
 */
public class SimpleBeatPattern implements BeatPattern {

	private int _firstBeat;
	private int _beatLength;
	
	// offset and beat length in milliseconds
	public SimpleBeatPattern(int firstBeatOffset, int beatLength) {
		_firstBeat = firstBeatOffset;
		_beatLength = beatLength;
	}
	
	public int getBeatTime(int beatNumber) {
		return _firstBeat + beatNumber * _beatLength;
	}
}
