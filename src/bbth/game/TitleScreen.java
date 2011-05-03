package bbth.game;

import bbth.ui.Anchor;
import bbth.ui.UIButton;
import bbth.ui.UIProgressBar;
import bbth.ui.UIScrollView;

public class TitleScreen extends UIScrollView {
	private UIButton greeting;
	private UIProgressBar progressBar;
	
	private float elapsedTime;
	
	public TitleScreen(Object tag)
	{
		super(tag);
		
		setSize(BBTHGame.WIDTH, BBTHGame.HEIGHT);
		setPosition(0, 0);
		
		greeting = new UIButton("Hello, World!", null);
		greeting.setAnchor(Anchor.CENTER_CENTER);
		greeting.setSize(160, 20);
		//greeting.setTextColor(Color.WHITE);
		greeting.setPosition(160.f, 90.f);
		//greeting.setTextSize(30.f);
		//greeting.sizeToFit();
		addSubview(greeting);
		
		// testing progress bar
		progressBar = new UIProgressBar(tag);
		progressBar.setAnchor(Anchor.CENTER_CENTER);
		progressBar.setSize(160, 20);
		progressBar.setPosition(160, 120);
		progressBar.setBorderRadius(5);
		progressBar.setProgress(1.f);
		addSubview(progressBar);

	}
	
	public void onUpdate(float seconds) {
		this.elapsedTime += seconds;
		
		progressBar.setProgress(elapsedTime / 10);
	}
}
