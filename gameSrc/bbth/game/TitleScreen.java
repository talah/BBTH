package bbth.game;

import java.util.Map;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.FloatMath;
import bbth.engine.achievements.AchievementInfo;
import bbth.engine.core.GameActivity;
import bbth.engine.net.bluetooth.Bluetooth;
import bbth.engine.net.simulation.LockStepProtocol;
import bbth.engine.particles.ParticleSystem;
import bbth.engine.ui.Anchor;
import bbth.engine.ui.UIButton;
import bbth.engine.ui.UIButtonDelegate;
import bbth.engine.ui.UIImageView;
import bbth.engine.ui.UILabel;
import bbth.engine.ui.UINavigationController;
import bbth.engine.ui.UIView;
import bbth.engine.util.MathUtils;

public class TitleScreen extends UIView implements UIButtonDelegate {
	private UIImageView titleBar;
	private float animDelay = 1.0f;
	private UIButton singleplayerButton, multiplayerButton, achievementsButton;
	private InfiniteCombatView combatView;
	private UINavigationController controller;
	private Paint paint = new Paint();
	private Map<String, AchievementInfo> achievements;
	private ParticleSystem particles;
	
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
		
		particles = new ParticleSystem(150, 0.5f);
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		combatView.onDraw(canvas);
		
		paint.setARGB(128, 0, 0, 0);
		canvas.drawRect(0, 0, BBTHGame.WIDTH, BBTHGame.HEIGHT, paint);
		
		super.onDraw(canvas);
		particles.draw(canvas, paint);
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
		
		for (int i = 0; i < 2; ++i) {
			float angle = MathUtils.randInRange(0, 2 * MathUtils.PI);
			float xVel = MathUtils.randInRange(25.f, 50.f) * FloatMath.cos(angle);
			float yVel = MathUtils.randInRange(25.f, 50.f) * FloatMath.sin(angle);
			particles.createParticle().circle().color(Color.BLUE).position(BBTHGame.WIDTH / 2.f, -150).angle(angle).velocity(xVel, yVel);
		}
		particles.tick(seconds);
	}

	@Override
	public void onClick(UIButton button) {
		if (button == singleplayerButton) {
			LockStepProtocol protocol = new LockStepProtocol();
			controller.push(new SongSelectionScreen(controller, Team.SERVER, new Bluetooth(GameActivity.instance, protocol), protocol, true), BBTHGame.MOVE_LEFT_TRANSITION);
		} else if (button == multiplayerButton) {
			controller.push(new GameSetupScreen(controller), BBTHGame.MOVE_LEFT_TRANSITION);
		} else if (button == achievementsButton) {
			controller.push(new AchievementsScreen(controller, achievements));
		}
	}
}
