package bbth.engine.sound;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple beat pattern where every beat is the same length
 * @author jardini
 *
 */
public class SimpleBeatPattern implements BeatPattern {

	private Beat[] _beats;
	private int _duration;
	
	// create a pattern from an list of beats and an offset
	public SimpleBeatPattern(int offset, List<Beat> beats) {
		_beats = new Beat[beats.size()];	
		_duration = 0;
		for (int i = 0; i < beats.size(); ++i) {
			_beats[i] = new Beat(beats.get(i).type, beats.get(i).duration);
			_beats[i]._startTime = offset + _duration;
			_duration += _beats[i].duration;
		}
	}
	
	// offset, beat length and duration in milliseconds
	public SimpleBeatPattern(int firstBeatOffset, int beatLength, int duration) {
		_duration = firstBeatOffset + duration;
		
		List<Beat> beats = new ArrayList<Beat>();
		int time = firstBeatOffset;
		while (time < duration) {
			Beat beat = Beat.tap(beatLength);
			beat._startTime = time;
			beats.add(beat);
			time += beat.duration;
		}
		
		_beats = new Beat[beats.size()];
		beats.toArray(_beats);
	}
	
	@Override
	public Beat getBeat(int beatNumber) {
		if (beatNumber >= 0 && beatNumber < _beats.length) {
			return _beats[beatNumber];
		}
		return null;
	}
	
	@Override
	public int getDuration() {
		return _duration;
	}

	@Override
	public int size() {
		return _beats.length;
	}
}
