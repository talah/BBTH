package bbth.game;

import android.graphics.Color;
import bbth.ui.Anchor;
import bbth.ui.UILabel;
import bbth.ui.UIScrollView;
import bbth.ui.UIView;

public class TitleScreen extends UIScrollView {
	
	private UILabel greeting;
	
	public TitleScreen(Object tag)
	{
		super(tag);
		greeting = new UILabel("Hello, World!", null);
		greeting.setAnchor(Anchor.CENTER_CENTER);
		greeting.setTextColor(Color.WHITE);
		greeting.setPosition(160.f, 90.f);
		greeting.setTextSize(30.f);
		
		addSubview(greeting);
	}
}
