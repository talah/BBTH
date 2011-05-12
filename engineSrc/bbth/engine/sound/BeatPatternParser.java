package bbth.engine.sound;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import bbth.engine.core.GameActivity;
import android.content.res.XmlResourceParser;
import android.util.Log;

/**
 * Parse an XML beat pattern
 * @author jardini
 *
 */
public class BeatPatternParser {

	// parse an entire pattern
    public static BeatPattern parse(int resourceId) {
    	XmlResourceParser parser = GameActivity.instance.getResources().getXml(resourceId);
    	Map<String, BeatPattern> patterns = new HashMap<String, BeatPattern>();
    	List<BeatPattern> song = new ArrayList<BeatPattern>();
    	try {
        	int eventType = parser.next(); // skip start of document
        	
    		while (eventType != XmlPullParser.END_DOCUMENT) {
    			if (eventType == XmlPullParser.START_TAG) {
    				String name = parser.getName();
    				if (name.equals("pattern")) {
    					Log.d("BBTH", "Starting pattern");
    					String id = parser.getAttributeValue(null, "id");
    					patterns.put(id, parseSubpattern(parser));
    					eventType = parser.getEventType();
    				} else if (name.equals("song")) {
    					Log.d("BBTH", "Starting song");
    					song = parseSong(parser, patterns);
    					eventType = parser.getEventType();
    				} else {
    					eventType = parser.next();
    				}
    			} else {
    				eventType = parser.next();
    			}
    		}
    		
    	} catch (XmlPullParserException e) {
    		Log.e("BBTH", "Error parsing beat track");
    	} catch (IOException e) {
    		Log.e("BBTH", "Error parsing beat track");	
    	}
    	
    	BeatPattern []songArray = new BeatPattern[song.size()];
    	song.toArray(songArray);
    	return new CompositeBeatPattern(songArray);
    }
    
    // parse a subpattern
    private static BeatPattern parseSubpattern(XmlResourceParser parser) throws IOException, XmlPullParserException {
    	 ArrayList<Beat> beats = new ArrayList<Beat>();
    	 int bpm = Integer.parseInt(parser.getAttributeValue(null, "bpm"));
    	 
		 parser.next();
		 String name = parser.getName();
		 boolean patternOver = false;    	
		 
    	 while (!patternOver) {
    		 if (parser.getEventType() == XmlPullParser.START_TAG) {

    			 String type = parser.getAttributeValue(null, "type");
    			 Log.d("BBTH", type);
    			 int duration;
    			 if (type != null) {
    				 duration = parseNoteType(type, bpm);
    			 } else {
    				 // check for manual duration entry
    				 String durationStr = parser.getAttributeValue(null, "duration");
    				 if (durationStr == null) {
    					 throw new IOException();
    				 }
    				 duration = Integer.parseInt(durationStr);
    			 }

    			 parser.next();

    			 name = parser.getName();
    			 if (name.equals("rest")) {
    				 beats.add(Beat.rest(duration));
    			 } else if (name.equals("hold")) {
    				 beats.add(Beat.hold(duration));
    			 } else if (name.equals("beat")) {
    				 beats.add(Beat.tap(duration));
    			 } else {
    				 patternOver = true;
    			 }
    		 } else if (parser.getEventType() == XmlPullParser.END_TAG) {
    			 // end of pattern tag
    			 patternOver = true;
    		 }

    		 // skip the end tag
    		 parser.next();
    	 }

    	 return new SimpleBeatPattern(0, beats);
    }
    
    // parse a note type
    private static int parseNoteType(String type, int bpm) {
    	if (type.equals("sixteenth")) {
    		return bpm / 4;
    	}
    	if (type.equals("eighth")) {
    		return bpm / 2;
    	}
    	if (type.equals("quarter")) {
    		return bpm;
    	}
    	if (type.equals("half")) {
    		return bpm * 2;
    	}
    	if (type.equals("whole")) {
    		return bpm * 4;
    	}
    	if (type.equals("eighth_triplet")) {
    		return bpm / 3;
    	}
    	if (type.equals("quarter_triplet")) {
    		return bpm * 2 / 3;
    	}
    	
    	return bpm;
    }
    
    // parse the song
    private static List<BeatPattern> parseSong(XmlResourceParser parser, 
    	Map<String, BeatPattern> patterns) throws IOException, XmlPullParserException {
    	List<BeatPattern> song = new ArrayList<BeatPattern>();

    	while (true) {
    		parser.next();

    		if (parser.getEventType() == XmlPullParser.START_TAG) {
    			String name = parser.getName();
    			if (name.equals("load-pattern")) {
    				String id = parser.getAttributeValue(null, "id");
    				if (!patterns.containsKey(id)) {
    					throw new XmlPullParserException("bad load-pattern tag");
    				}
    				song.add(patterns.get(id));
    				// skip the end tag
    				parser.next();
    			} else {
    				break;
    			}
    		} else if (parser.getEventType() == XmlPullParser.END_TAG) {
    			// skip end of song tag
    			parser.next();
    			break;
    		}
    	}

   	 	return song;
    }
}
