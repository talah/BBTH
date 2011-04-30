package bbth.game;

import android.graphics.Color;
import bbth.ui.Anchor;
import bbth.ui.UILabel;
import bbth.ui.UIProgressBar;
import bbth.ui.UIView;

public class TitleScreen extends UIView {
	
	private UILabel greeting;
	private UIProgressBar progressBar;
	
	public TitleScreen(Object tag)
	{
		super(tag);
		greeting = new UILabel("Hello, World!", null);
		greeting.setAnchor(Anchor.CENTER_CENTER);
		greeting.setTextColor(Color.WHITE);
		greeting.setPosition(160.f, 90.f);
		greeting.setTextSize(30.f);
		addSubview(greeting);
		
		//
		progressBar = new UIProgressBar(greeting, greeting, tag);
		progressBar.setAnchor(Anchor.CENTER_CENTER);
		progressBar.setSize(30, 20);
		progressBar.setPosition(160, 90);
		addSubview(progressBar);
	}
}
