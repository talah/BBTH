package bbth.game;

import java.util.Map;

import android.graphics.*;
import bbth.engine.achievements.AchievementInfo;
import bbth.engine.core.GameActivity;
import bbth.engine.net.bluetooth.Bluetooth;
import bbth.engine.net.simulation.LockStepProtocol;
import bbth.engine.ui.*;

public class TitleScreen extends UIView implements UIButtonDelegate {
	private UIImageView titleBar;
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
		
		titleBar = new UIImageView(R.drawable.logo);
		titleBar.setAnchor(Anchor.TOP_CENTER);
		titleBar.setPosition(BBTHGame.WIDTH/2, -150);
		titleBar.setSize(300, 140);
		titleBar.animatePosition(BBTHGame.WIDTH/2, 20, 3);
		this.addSubview(titleBar);
		
		singleplayerButton = new UIButton("Single Player", this);
		singleplayerButton.setSize(BBTHGame.WIDTH * 0.75f, 45);
		singleplayerButton.setAnchor(Anchor.CENTER_CENTER);
		singleplayerButton.setPosition(-BBTHGame.WIDTH, BBTHGame.HEIGHT / 2 - 65);
		singleplayerButton.animatePosition(BBTHGame.WIDTH / 2.f, BBTHGame.HEIGHT / 2 - 65, 0.5f);
		singleplayerButton.setButtonDelegate(this);
		
		multiplayerButton = new UIButton("Multi Player", this);
		multiplayerButton.setSize(BBTHGame.WIDTH * 0.75f, 45);
		multiplayerButton.setAnchor(Anchor.CENTER_CENTER);
		multiplayerButton.setPosition(-BBTHGame.WIDTH * 2.0f, BBTHGame.HEIGHT / 2);
		multiplayerButton.animatePosition(BBTHGame.WIDTH / 2.f, BBTHGame.HEIGHT / 2, 1.0f);
		multiplayerButton.setButtonDelegate(this);
		
		achievementsButton = new UIButton("Achievements", this);
		achievementsButton.setSize(BBTHGame.WIDTH * 0.75f, 45);
		achievementsButton.setAnchor(Anchor.CENTER_CENTER);
		achievementsButton.setPosition(-BBTHGame.WIDTH * 3.0f, BBTHGame.HEIGHT / 2 + 65);
		achievementsButton.animatePosition(BBTHGame.WIDTH / 2.f, BBTHGame.HEIGHT / 2 + 65, 1.5f);

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
		endAnimations();
	}
	
	@Override
	public void willHide(boolean animating) {
		super.willHide(animating);
		endAnimations();
	}
	
	private void endAnimations() {
		if (titleBar.isAnimatingPosition) {
			animDelay = 0f;
			titleBar.isAnimatingPosition = false;
			titleBar.setPosition(BBTHGame.WIDTH / 2.f, 20);
		}
		
		if (singleplayerButton.isAnimatingPosition) {
			animDelay = 0f;
			singleplayerButton.isAnimatingPosition = false;
			singleplayerButton.setPosition(BBTHGame.WIDTH / 2.f, BBTHGame.HEIGHT / 2 - 65);
		}
		
		if (multiplayerButton.isAnimatingPosition) {
			animDelay = 0f;
			multiplayerButton.isAnimatingPosition = false;
			multiplayerButton.setPosition(BBTHGame.WIDTH / 2.f, BBTHGame.HEIGHT / 2);
		}
		
		if (achievementsButton.isAnimatingPosition) {
			animDelay = 0f;
			achievementsButton.isAnimatingPosition = false;
			achievementsButton.setPosition(BBTHGame.WIDTH / 2.f, BBTHGame.HEIGHT / 2 + 65);
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
			LockStepProtocol protocol = new LockStepProtocol();
			controller.push(new SongSelectionScreen(controller, Team.SERVER, new Bluetooth(GameActivity.instance, protocol), protocol, true), BBTHGame.FROM_RIGHT_TRANSITION);
		} else if (button == multiplayerButton) {
			controller.push(new GameSetupScreen(controller), BBTHGame.FROM_RIGHT_TRANSITION);
		} else if (button == achievementsButton) {
			controller.push(new AchievementsScreen(controller, achievements), BBTHGame.FROM_RIGHT_TRANSITION);
		}
	}
}
