package bbth.sound;

/**
 * Represents a pattern of Beats (in its simplest form, an array of Beats)
 * @author jardini
 *
 */
public interface BeatPattern {	
	/**
	 * @param beatNumber zero-indexed beat number in the pattern
	 * @return time from start of song (in milliseconds)
	 */
	Beat getBeat(int beatNumber);
	
	/**
	 * @return time occupied by the pattern (in milliseconds)
	 */
	int getDuration();
	
	/**
	 * @return number of beats in this patterns
	 */
	int size();
}
