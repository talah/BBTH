package bbth.sound;

/**
 * Represents a pattern of beats
 * @author jardini
 *
 */
public interface BeatPattern {	
	/**
	 * @param beatNumber zero-indexed beat number in the pattern
	 * @return time from start of song (in milliseconds)
	 */
	int getBeatTime(int beatNumber);
}
