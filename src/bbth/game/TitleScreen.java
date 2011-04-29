package bbth.game;

import android.graphics.Color;
import bbth.ui.Anchor;
import bbth.ui.UILabel;
import bbth.ui.UIView;

public class TitleScreen extends UIView {
	
	private UILabel greeting;
	
	public TitleScreen()
	{
		greeting = new UILabel("Hello, World!", null);
		greeting.setAnchor(Anchor.CENTER_CENTER);
		greeting.setTextColor(Color.WHITE);
		greeting.setPosition(160.f, 90.f);
		greeting.setTextSize(30.f);
		
		addSubview(greeting);
	}
}
