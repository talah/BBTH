package bbth.game;

import bbth.ui.Anchor;
import bbth.ui.UIButton;
import bbth.ui.UIButtonDelegate;
import bbth.ui.UIProgressBar;
import bbth.ui.UIProgressBar.Mode;
import bbth.ui.UIRadioButton;
import bbth.ui.UIScrollView;
import bbth.ui.UIView;

public class TitleScreen extends UIScrollView implements UIButtonDelegate {
	private UIButton greeting;
	private UIProgressBar progressBar;
	
	private float elapsedTime;
	
	public TitleScreen(Object tag)
	{
		super(tag);
		setSize(BBTHGame.WIDTH, BBTHGame.HEIGHT);
		setPosition(0, 0);
		
		greeting = new UIButton("Scroll To Bottom", null);
		greeting.setAnchor(Anchor.CENTER_CENTER);
		greeting.setSize(160, 20);
		greeting.setPosition(160.f, 90.f);
		greeting.delegate = this;
		addSubview(greeting);
		
		// testing progress bar
		progressBar = new UIProgressBar(tag);
		progressBar.setAnchor(Anchor.CENTER_CENTER);
		progressBar.setSize(160, 20);
		progressBar.setPosition(160, 1020);
		progressBar.setBorderRadius(5);
		progressBar.setProgress(1.f);
		progressBar.setMode(Mode.INFINTE);
		addSubview(progressBar);
		
		UIRadioButton testButton = new UIRadioButton("Hello", null);
		testButton.setAnchor(Anchor.CENTER_CENTER);
		testButton.setPosition(160, 120);
		addSubview(testButton);
	}
	
	public void onUpdate(float seconds) {
		super.onUpdate(seconds);

		this.elapsedTime += seconds;
		progressBar.setProgress(elapsedTime / 10);
		
		if (this.progressBar.getProgress() == 1.f) {
			this.progressBar.setMode(Mode.INFINTE);
		}
	}

	@Override
	public void onTouchUp(UIView sender) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTouchDown(UIView sender) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTouchMove(UIView sender) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(UIButton button) {
		if(button == greeting)
			scrollTo(0, 1020);
		
	}
}
