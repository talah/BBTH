package bbth.game;

import java.util.Map;

import android.graphics.*;
import bbth.engine.core.GameActivity;
import bbth.engine.net.bluetooth.Bluetooth;
import bbth.engine.net.simulation.LockStepProtocol;
import bbth.engine.ui.*;

public class TitleScreen extends UIView implements UIButtonDelegate {
	private UILabel titleBar;
	private float animDelay = 1.0f;
	private UIButton singleplayer, multiplayer, achievements;
	private InfiniteCombatView combatView;
	private UINavigationController controller;
	private Paint paint = new Paint();
	private Map<String, String> achievementDescriptions;
	
	public TitleScreen(UINavigationController controller, Map<String, String> achievementDescriptions) {
		setSize(BBTHGame.WIDTH, BBTHGame.HEIGHT);
		
		this.controller = controller;
		this.achievementDescriptions = achievementDescriptions;
		
		combatView = new InfiniteCombatView();
		
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
		
		achievements = new UIButton("Achievements", this);
		achievements.setSize(BBTHGame.WIDTH * 0.75f, 45);
		achievements.setAnchor(Anchor.CENTER_CENTER);
		achievements.setPosition(BBTHGame.WIDTH / 2.f, BBTHGame.HEIGHT / 2 + 65);
		achievements.setButtonDelegate(this);
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		combatView.onDraw(canvas);
		
		paint.setARGB(128, 0, 0, 0);
		canvas.drawRect(0, 0, BBTHGame.WIDTH, BBTHGame.HEIGHT, paint);
		
		super.onDraw(canvas);
	}

	@Override
	public void onTouchDown(float x, float y) {
		super.onTouchDown(x, y);
		if (titleBar.isAnimatingPosition) {
			animDelay = 0f;
			titleBar.isAnimatingPosition = false;
			titleBar.setPosition(BBTHGame.WIDTH / 2.f, 80);
		}
	}

	@Override
	public void onUpdate(float seconds) {
		super.onUpdate(seconds);

		combatView.onUpdate(seconds);
		
		if (!titleBar.isAnimatingPosition)
			animDelay -= seconds;
		if (animDelay <= 0)
		{
			addSubview(singleplayer);
			addSubview(multiplayer);
			addSubview(achievements);
		}
	}

	@Override
	public void onClick(UIButton button) {
		if (button == singleplayer) {
			controller.push(new SongSelectionScreen(controller, Team.SERVER, new Bluetooth(GameActivity.instance, new LockStepProtocol()), new LockStepProtocol(), true));
		} else if (button == multiplayer) {
			controller.push(new GameSetupScreen(controller));
		} else if (button == achievements) {
			controller.push(new AchievementsScreen(controller, achievementDescriptions));
		}
	}
}
