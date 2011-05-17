package bbth.engine.achievements;

import org.xmlpull.v1.XmlPullParser;

import android.content.res.*;
import android.graphics.*;
import android.util.Log;
import bbth.engine.core.GameActivity;
import bbth.engine.util.Bag;

public class AchievementInfoParser {
	public static AchievementInfo[] parseAchievementInfos(int resourceID) {
		XmlResourceParser parser = GameActivity.instance.getResources().getXml(resourceID);
		Bag<AchievementInfo> achievementInfos = new Bag<AchievementInfo>();
		
		try {
        	int eventType = parser.next(); // skip start of document
        	
    		while (eventType != XmlPullParser.END_DOCUMENT) {
    			if (eventType == XmlPullParser.START_TAG) {
    				String name = parser.getName();
    				if (name.equals("ACHIEVEMENTS")) {
    					Log.d("BBTH", "Entering root node");
    					eventType = parser.next();
    				} else if (name.equals("ACHIEVEMENT")) {
    					Log.d("BBTH", "Entering ACHIEVEMENT node");
    					AchievementInfo achievementInfo = parseAchievementInfo(parser);
    					if (achievementInfo != null)
    						achievementInfos.add(achievementInfo);
    					eventType = parser.getEventType();
    				} else {
    					eventType = parser.next();
    				}
    			} else {
    				eventType = parser.next();
    			}
    		}
    	} catch (Exception e) {
    		Log.e("BBTH", "Error parsing achievements list", e);
    	} finally {
    		parser.close();
    	}
		
		return achievementInfos.toArray(new AchievementInfo[achievementInfos.size()]);
	}
	
	private static AchievementInfo parseAchievementInfo(XmlResourceParser parser) throws Exception {
		
		int id = parser.getAttributeIntValue(null, "id", -1);
		if (id < 0) {
			Log.d("BBTH", "Missing or invalid achievement id!");
			return null;
		}
		
		parser.next();
		String name = "<unnamed>";
		String description = "<no description>";
		String icon = "icon";
		
		int eventType;
		while ((eventType = parser.getEventType()) != XmlPullParser.END_TAG) {
			if (eventType == XmlPullParser.START_TAG) {
				String elementName = parser.getName();
				if (elementName.equals("NAME")) {
					String achievementName = getText(parser, elementName);
					if (achievementName == null)
						return null;
					name = achievementName;
				} else if (elementName.equals("DESC")) {
					String desc = getText(parser, elementName);
					if (desc == null)
						return null;
					description = desc;
				} else if (elementName.equals("ICON")) {
					String achievementIcon = getText(parser, elementName);
					if (achievementIcon == null)
						return null;
					icon = achievementIcon;
				}
			}
		}
		
		Resources resources = GameActivity.instance.getResources();
		int imageId = resources.getIdentifier(icon, "drawable", GameActivity.instance.getPackageName());
		Bitmap image = BitmapFactory.decodeResource(resources, imageId);
		
		return new AchievementInfo(id, name, description, image);
	}
	
	private static String getText(XmlResourceParser parser, String name) throws Exception {
		parser.next();
		
		if (parser.getEventType() != XmlPullParser.TEXT) {
			Log.d("BBTH", "Text expected in "+name+" element");
			return null;
		}
		
		String text = parser.getText();
		parser.next();
		parser.next(); // get rid of close tag
		
		return text;
	}
}
