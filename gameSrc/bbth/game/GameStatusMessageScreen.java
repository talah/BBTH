package bbth.game;

import android.graphics.*;
import android.graphics.Paint.Align;
import android.util.FloatMath;
import bbth.engine.particles.ParticleSystem;
import bbth.engine.ui.*;
import bbth.engine.util.MathUtils;

public class GameStatusMessageScreen extends UIView implements UIButtonDelegate {
	public static class DisconnectScreen extends GameStatusMessageScreen {
		public DisconnectScreen(UINavigationController controller) {
			super("You have been disconnected.", controller);
		}
	}

	public static class WinScreen extends GameStatusMessageScreen {
		private static final int NUM_PARTICLES = 1000;
		private static final float PARTICLE_THRESHOLD = 0.5f;
		private static final ParticleSystem PARTICLES = new ParticleSystem(NUM_PARTICLES, PARTICLE_THRESHOLD);
		private static final Paint PARTICLE_PAINT = new Paint();
		static {
			PARTICLE_PAINT.setStrokeWidth(2.f);
			PARTICLE_PAINT.setAntiAlias(true);
		}
		private float secondsUntilNext;
		
		public WinScreen(UINavigationController controller) {
			super("Congratulations! You win!", controller);
			PARTICLES.reset();
		}

		@Override
		public void onDraw(Canvas canvas) {
			PARTICLES.draw(canvas, PARTICLE_PAINT);
			super.onDraw(canvas);
		}

		@Override
		public void onUpdate(float seconds) {
			PARTICLES.tick(seconds);
			PARTICLES.gravity(150 * seconds);
			PARTICLES.updateAngles();

			secondsUntilNext -= seconds;
			while (secondsUntilNext < 0) {
				secondsUntilNext += 0.5f;
				float x = MathUtils.randInRange(0, BBTHGame.WIDTH);
				float y = MathUtils.randInRange(0, BBTHGame.HEIGHT);
				for (int i = 0; i < 100; i++) {
					float angle = MathUtils.randInRange(0, MathUtils.TWO_PI);
					float radius = MathUtils.randInRange(0, 150);
					float vx = FloatMath.cos(angle) * radius;
					float vy = FloatMath.sin(angle) * radius;
					PARTICLES.createParticle().position(x, y).velocity(vx, vy).angle(angle).shrink(0.1f, 0.2f).line().radius(10).color(Color.YELLOW);
				}
			}
		}
	}

	public static class LoseScreen extends GameStatusMessageScreen {
		public LoseScreen(UINavigationController controller) {
			super("Oh noes, you lost the game :(", controller);
		}
	}

	public static class TieScreen extends GameStatusMessageScreen {
		public TieScreen(UINavigationController controller) {
			super("It's a tie!", controller);
		}
	}

	private UILabel message;
	private UIButton playAgain, quit;
	UINavigationController controller;

	public GameStatusMessageScreen(String text, UINavigationController controller) {
		this.controller = controller;
		
		message = new UILabel(text, tag);
		message.setAnchor(Anchor.TOP_CENTER);
		message.setTextSize(20.f);
		message.setPosition(BBTHGame.WIDTH / 2.f, 80);
		message.setTextAlign(Align.CENTER);
		this.addSubview(message);

		playAgain = new UIButton("Play Again", tag);
		playAgain.setSize(BBTHGame.WIDTH * 0.75f, 45);
		playAgain.setAnchor(Anchor.CENTER_CENTER);
		playAgain.setPosition(BBTHGame.WIDTH / 2.f, BBTHGame.HEIGHT / 2 - 65);
		playAgain.setButtonDelegate(this);
		this.addSubview(playAgain);

		quit = new UIButton("Quit", tag);
		quit.setSize(BBTHGame.WIDTH * 0.75f, 45);
		quit.setAnchor(Anchor.CENTER_CENTER);
		quit.setPosition(BBTHGame.WIDTH / 2.f, BBTHGame.HEIGHT / 2);
		quit.setButtonDelegate(this);
		this.addSubview(quit);

		this.setSize(BBTHGame.WIDTH, BBTHGame.HEIGHT);
	}

	@Override
	public void onTouchUp(UIView sender) {
		// Do nothing here
	}

	@Override
	public void onTouchDown(UIView sender) {
		// Do nothing here
	}

	@Override
	public void onTouchMove(UIView sender) {
		// Do nothing here
	}

	@Override
	public void onClick(UIButton button) {
		if (button == playAgain) {
//			if (BBTHGame.IS_SINGLE_PLAYER) {
//				nextScreen = new InGameScreen(Team.SERVER, new Bluetooth(
//						GameActivity.instance, new LockStepProtocol()),
//						BBTHGame.SONG, new LockStepProtocol());
//			} else {
//				nextScreen = new GameSetupScreen();
//			}
			controller.pop(BBTHGame.FADE_OUT_FADE_IN_TRANSITION);
		} else if (button == quit) {
			System.exit(0);
		}
	}

	public void setText(String text) {
		message.setText(text);
	}
}
