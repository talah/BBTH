package bbth.sound;

/**
 * Tracks the beats of a song given its first beat time and beats-per-millisecond
 * @author jardini
 *
 */
public class BeatTracker {

	private MusicPlayer _musicPlayer;
	private int _firstBeat;
	private int _bpm; // beats-per-millisecond
	
	public BeatTracker(MusicPlayer player, int firstBeat, int beatsPerMilli) {
		_musicPlayer = player;
		_firstBeat = firstBeat;
		_bpm = beatsPerMilli;
	}

	// returns the millisecond time difference between an event and a beat in the song
	public int getOffsetFromBeat(int currentTime) {
		
	}
}
