package bbth.game;

import android.graphics.Color;
import bbth.ui.Anchor;
import bbth.ui.UILabel;
import bbth.ui.UIProgressBar;
import bbth.ui.UIView;

public class TitleScreen extends UIView {
	private UILabel greeting;
	private UIProgressBar progressBar;
	
	private float elapsedTime;
	
	public TitleScreen(Object tag)
	{
		super(tag);
		greeting = new UILabel("Hello, World!", null);
		greeting.setAnchor(Anchor.CENTER_CENTER);
		greeting.setTextColor(Color.WHITE);
		greeting.setPosition(160.f, 90.f);
		greeting.setTextSize(30.f);
		addSubview(greeting);
		
		// testing progress bar
		progressBar = new UIProgressBar(tag);
		progressBar.setAnchor(Anchor.CENTER_CENTER);
		progressBar.setSize(60, 20);
		progressBar.setPosition(120, 90);
		progressBar.setBorderRadius(5);
		progressBar.setProgress(1.f);
		addSubview(progressBar);

	}
	
	public void onUpdate(float seconds) {
		this.elapsedTime += seconds;
		
		progressBar.setProgress(elapsedTime / 10);
	}
}
