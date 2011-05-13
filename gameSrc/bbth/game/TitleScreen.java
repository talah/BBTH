package bbth.game;

import bbth.engine.ui.Anchor;
import bbth.engine.ui.UIButton;
import bbth.engine.ui.UIButtonDelegate;
import bbth.engine.ui.UILabel;
import bbth.engine.ui.UIView;

// TODO Pretty graphics
public class TitleScreen extends UIView implements UIButtonDelegate {
	private UILabel titleBar;
	private float animDelay = 1.0f;
	private UIButton singleplayer, multiplayer;

	public TitleScreen() {
		titleBar = new UILabel("Beat Back the Horde!", this);
		titleBar.setAnchor(Anchor.TOP_CENTER);
		titleBar.setTextSize(30.f);
		titleBar.setPosition(BBTHGame.WIDTH / 2.f, BBTHGame.HEIGHT + 60);
		titleBar.animatePosition(BBTHGame.WIDTH / 2.f, 80, 3);
		this.addSubview(titleBar);
		
		singleplayer = new UIButton("Single Player", this);
		singleplayer.setSize(BBTHGame.WIDTH * 0.75f, 45);
		singleplayer.setAnchor(Anchor.CENTER_CENTER);
		singleplayer.setPosition(BBTHGame.WIDTH / 2.f, BBTHGame.HEIGHT / 2 - 65);
		singleplayer.setButtonDelegate(this);
		
		multiplayer = new UIButton("Multi Player", this);
		multiplayer.setSize(BBTHGame.WIDTH * 0.75f, 45);
		multiplayer.setAnchor(Anchor.CENTER_CENTER);
		multiplayer.setPosition(BBTHGame.WIDTH / 2.f, BBTHGame.HEIGHT / 2);
		multiplayer.setButtonDelegate(this);
	}

	@Override
	public void onUpdate(float seconds) {
		super.onUpdate(seconds);

		if (!titleBar.isAnimatingPosition)
			animDelay -= seconds;
		if (animDelay <= 0)
		{
			addSubview(singleplayer);
			addSubview(multiplayer);
		}
	}

	@Override
	public void onTouchUp(UIView sender) {
		
	}

	@Override
	public void onTouchDown(UIView sender) {
		
	}

	@Override
	public void onTouchMove(UIView sender) {
		
	}

	@Override
	public void onClick(UIButton button) {
		if(button == singleplayer)
		{
			//TODO Single player
		}
		else if(button == multiplayer)
		{
			nextScreen = new GameSetupScreen();
		}
		
	}
}
