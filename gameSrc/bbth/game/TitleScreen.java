package bbth.game;

import java.util.Map;

import android.graphics.*;
import bbth.engine.achievements.AchievementInfo;
import bbth.engine.core.GameActivity;
import bbth.engine.net.bluetooth.Bluetooth;
import bbth.engine.net.simulation.LockStepProtocol;
import bbth.engine.ui.*;

public class TitleScreen extends UIView implements UIButtonDelegate {
	private UILabel titleBar;
	private float animDelay = 1.0f;
	private UIButton singleplayerButton, multiplayerButton, achievementsButton;
	private InfiniteCombatView combatView;
	private UINavigationController controller;
	private Paint paint = new Paint();
	private Map<String, AchievementInfo> achievements;
	
	public TitleScreen(UINavigationController controller, Map<String, AchievementInfo> achievements) {
		setSize(BBTHGame.WIDTH, BBTHGame.HEIGHT);
		
		this.controller = controller;
		this.achievements = achievements;
		
		combatView = new InfiniteCombatView();
		
		titleBar = new UILabel("Beat Back the Horde!", this);
		titleBar.setAnchor(Anchor.TOP_CENTER);
		titleBar.setTextSize(30.f);
		titleBar.setPosition(BBTHGame.WIDTH / 2.f, BBTHGame.HEIGHT + 60);
		titleBar.animatePosition(BBTHGame.WIDTH / 2.f, 80, 3);
		this.addSubview(titleBar);
		
		singleplayerButton = new UIButton("Single Player", this);
		singleplayerButton.setSize(BBTHGame.WIDTH * 0.75f, 45);
		singleplayerButton.setAnchor(Anchor.CENTER_CENTER);
		singleplayerButton.setPosition(BBTHGame.WIDTH / 2.f, BBTHGame.HEIGHT / 2 - 65);
		singleplayerButton.setButtonDelegate(this);
		
		multiplayerButton = new UIButton("Multi Player", this);
		multiplayerButton.setSize(BBTHGame.WIDTH * 0.75f, 45);
		multiplayerButton.setAnchor(Anchor.CENTER_CENTER);
		multiplayerButton.setPosition(BBTHGame.WIDTH / 2.f, BBTHGame.HEIGHT / 2);
		multiplayerButton.setButtonDelegate(this);
		
		achievementsButton = new UIButton("Achievements", this);
		achievementsButton.setSize(BBTHGame.WIDTH * 0.75f, 45);
		achievementsButton.setAnchor(Anchor.CENTER_CENTER);
		achievementsButton.setPosition(BBTHGame.WIDTH / 2.f, BBTHGame.HEIGHT / 2 + 65);
		achievementsButton.setButtonDelegate(this);
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
		if (animDelay <= 0) {
			addSubview(singleplayerButton);
			addSubview(multiplayerButton);
			addSubview(achievementsButton);
		}
	}

	@Override
	public void onClick(UIButton button) {
		if (button == singleplayerButton) {
			controller.push(new SongSelectionScreen(controller, Team.SERVER, new Bluetooth(GameActivity.instance, new LockStepProtocol()), new LockStepProtocol(), true), BBTHGame.MOVE_LEFT_TRANSITION);
		} else if (button == multiplayerButton) {
			controller.push(new GameSetupScreen(controller), BBTHGame.MOVE_LEFT_TRANSITION);
		} else if (button == achievementsButton) {
			controller.push(new AchievementsScreen(controller, achievements));
		}
	}
}
