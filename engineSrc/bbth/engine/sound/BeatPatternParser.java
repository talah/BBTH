package bbth.engine.sound;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.res.XmlResourceParser;
import android.util.Log;
import bbth.engine.core.GameActivity;

/**
 * Parse an XML beat pattern
 * @author jardini
 *
 */
public class BeatPatternParser {

	// parse an entire pattern into an array of beats
    public static Beat[] parse(int resourceId) {
    	XmlResourceParser parser = GameActivity.instance.getResources().getXml(resourceId);
    	Map<String, List<Beat>> patterns = new HashMap<String, List<Beat>>();
    	List<Beat> song = new ArrayList<Beat>();
    	int millisPerBeat = 0;
    	
    	try {
        	int eventType = parser.next(); // skip start of document
        	
    		while (eventType != XmlPullParser.END_DOCUMENT) {
    			if (eventType == XmlPullParser.START_TAG) {
    				String name = parser.getName();
    				if (name.equals("pattern")) {
    					Log.d("BBTH", "Starting pattern");
    					String id = parser.getAttributeValue(null, "id");
    					patterns.put(id, parseSubpattern(parser, millisPerBeat));
    					eventType = parser.getEventType();
    				} else if (name.equals("song")) {
    					Log.d("BBTH", "Starting song");
    					song = parseSong(parser, patterns);
    					eventType = parser.getEventType();
    				} else if (name.equals("root")) {
    					String mpb = parser.getAttributeValue(null, "mpb");
    					millisPerBeat = Integer.parseInt(mpb);
    					eventType = parser.next();
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
    	
    	Beat []songArray = new Beat[song.size()];
    	song.toArray(songArray);
    	return songArray;
    }
    
    // parse a subpattern into a List of beats
    private static List<Beat> parseSubpattern(XmlResourceParser parser, int millisPerBeat) throws IOException, XmlPullParserException {
    	 ArrayList<Beat> beats = new ArrayList<Beat>();
    	 
		 parser.next();
		 String name = parser.getName();
		 boolean patternOver = false;
		 
    	 while (!patternOver) {
    		 if (parser.getEventType() == XmlPullParser.START_TAG) {

    			 String type = parser.getAttributeValue(null, "type");
    			 Log.d("BBTH", type);
    			 int duration;
    			 if (type != null) {
    				 duration = parseNoteType(type, millisPerBeat);
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

    	 return beats;
    }
    
    // parse a note type
    private static int parseNoteType(String type, int millisPerBeat) {
    	if (type.equals("sixteenth")) {
    		return millisPerBeat / 4;
    	}
    	if (type.equals("eighth")) {
    		return millisPerBeat / 2;
    	}
    	if (type.equals("quarter")) {
    		return millisPerBeat;
    	}
    	if (type.equals("half")) {
    		return millisPerBeat * 2;
    	}
    	if (type.equals("whole")) {
    		return millisPerBeat * 4;
    	}
    	if (type.equals("eighth_triplet")) {
    		return millisPerBeat / 3;
    	}
    	if (type.equals("quarter_triplet")) {
    		return millisPerBeat * 2 / 3;
    	}
    	
    	return millisPerBeat;
    }
    
    // parse the song
    private static List<Beat> parseSong(XmlResourceParser parser, 
    	Map<String, List<Beat>> patterns) throws IOException, XmlPullParserException {
    	List<Beat> song = new ArrayList<Beat>();

    	while (true) {
    		parser.next();

    		if (parser.getEventType() == XmlPullParser.START_TAG) {
    			String name = parser.getName();
    			if (name.equals("load-pattern")) {
    				String id = parser.getAttributeValue(null, "id");
    				if (!patterns.containsKey(id)) {
    					throw new XmlPullParserException("bad load-pattern tag");
    				}
    				
    				// copy the pattern
    				List<Beat> subpattern = patterns.get(id);
    				for (int i = 0; i < subpattern.size(); ++i) {
    					song.add(new Beat(subpattern.get(i).type, subpattern.get(i).duration));
    				}
    				
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
    	
    	// set starting times
    	int currTime = 0;
    	for (int i = 0; i < song.size(); ++i) {
    		song.get(i)._startTime = currTime;
    		currTime += song.get(i).duration;
    	}

   	 	return song;
    }
}
