package bbth.engine.achievements;

import org.xmlpull.v1.XmlPullParser;

import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import bbth.engine.core.GameActivity;
import bbth.engine.util.Bag;
import bbth.game.R;

public class AchievementInfoParser {
	public static AchievementInfo[] parseAchievementInfos(int resourceID) {
		XmlResourceParser parser = GameActivity.instance.getResources().getXml(resourceID);
		Bag<AchievementInfo> achievementInfos = new Bag<AchievementInfo>();
		
		try {
        	int eventType = parser.next(); // skip start of document
        	
    		while (eventType != XmlPullParser.END_DOCUMENT) {
    			if (eventType == XmlPullParser.START_TAG) {
    				String name = parser.getName();
    				if (name.equals("ACHIEVEMENTS")) { //$NON-NLS-1$
//    					Log.d("BBTH", "Entering root node");
    					eventType = parser.next();
    				} else if (name.equals("ACHIEVEMENT")) { //$NON-NLS-1$
//    					Log.d("BBTH", "Entering ACHIEVEMENT node");
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
    		Log.e("BBTH", "Error parsing achievements list", e); //$NON-NLS-1$ //$NON-NLS-2$
    	} finally {
    		parser.close();
    	}
		
		return achievementInfos.toArray(new AchievementInfo[achievementInfos.size()]);
	}
	
	private static AchievementInfo parseAchievementInfo(XmlResourceParser parser) throws Exception {
		
		int id = parser.getAttributeIntValue(null, "id", -1); //$NON-NLS-1$
		if (id < 0) {
			Log.d("BBTH", "Missing or invalid achievement id!"); //$NON-NLS-1$ //$NON-NLS-2$
			return null;
		}
		
		parser.next();
		String name = GameActivity.instance.getString(R.string.defaultachievementname);
		String description = GameActivity.instance.getString(R.string.defaultachievementdesc);
		String icon = "icon"; //$NON-NLS-1$
		int activations = 1;
		
		int eventType;
		while ((eventType = parser.getEventType()) != XmlPullParser.END_TAG) {
			if (eventType == XmlPullParser.START_TAG) {
				String elementName = parser.getName();
				if (elementName.equals("NAME")) { //$NON-NLS-1$
					String achievementName = getText(parser, elementName);
					if (achievementName == null)
						return null;
					name = achievementName;
				} else if (elementName.equals("DESC")) { //$NON-NLS-1$
					String desc = getText(parser, elementName);
					if (desc == null)
						return null;
					description = desc;
				} else if (elementName.equals("ICON")) { //$NON-NLS-1$
					String achievementIcon = getText(parser, elementName);
					if (achievementIcon == null)
						return null;
					icon = achievementIcon;
				} else if (elementName.equals("ACTIVATIONS")) { //$NON-NLS-1$
					String activationStr = getText(parser, elementName);
					if (activationStr == null)
						return null;
					activations = Integer.parseInt(activationStr);
				}
			}
		}
		
		Resources resources = GameActivity.instance.getResources();
		int imageId = resources.getIdentifier(icon, "drawable", GameActivity.instance.getPackageName()); //$NON-NLS-1$
		Bitmap image = BitmapFactory.decodeResource(resources, imageId);
		
		return new AchievementInfo(id, activations, name, description, image);
	}
	
	private static String getText(XmlResourceParser parser, String name) throws Exception {
		parser.next();
		
		if (parser.getEventType() != XmlPullParser.TEXT) {
			Log.d("BBTH", "Text expected in "+name+" element"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			return null;
		}
		
		String text = parser.getText();
		parser.next();
		parser.next(); // get rid of close tag
		
		return text;
	}
}
