package bbth.game;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.FloatMath;
import bbth.engine.net.bluetooth.Bluetooth;
import bbth.engine.net.bluetooth.State;
import bbth.engine.net.simulation.LockStepProtocol;
import bbth.engine.particles.ParticleSystem;
import bbth.engine.ui.UILabel;
import bbth.engine.ui.UIScrollView;
import bbth.engine.util.ColorUtils;
import bbth.engine.util.MathUtils;
import bbth.game.units.Unit;

public class InGameScreen extends UIScrollView {
	private static final int NUM_PARTICLES = 200;
	private static final float PARTICLE_THRESHOLD = 0.5f;

	private BBTHSimulation sim;
	private UILabel label;
	private Bluetooth bluetooth;
	private Team team;
	private BeatTrack beatTrack;
	private ParticleSystem particles;
	private Paint particlePaint;

	public InGameScreen(Team playerTeam, Bluetooth bluetooth, LockStepProtocol protocol) {
		super(null);

		MathUtils.resetRandom(0);

		this.team = playerTeam;

		// Set up the scrolling!
		this.setSize(BBTHGame.WIDTH, BBTHGame.HEIGHT);
		this.setPosition(0, 0);
		this.setScrolls(false);

		// Test labels
		label = new UILabel("", null);
		label.setTextSize(10);
		label.setPosition(10, 10);
		label.setSize(BBTHGame.WIDTH - 20, 10);
		label.setTextAlign(Align.CENTER);
		addSubview(label);

		this.bluetooth = bluetooth;
		sim = new BBTHSimulation(playerTeam, protocol, team == Team.SERVER);
		sim.setupSubviews(this);

		particles = new ParticleSystem(NUM_PARTICLES, PARTICLE_THRESHOLD);
		particlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

		beatTrack = new BeatTrack();
		beatTrack.startMusic();
	}

	@Override
	public void onStop() {
		beatTrack.stopMusic();

		// Disconnect when we lose focus
		bluetooth.disconnect();
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// Draw the game
		canvas.translate(-this.pos_x, -this.pos_y);
		sim.draw(canvas);
		particles.draw(canvas, particlePaint);
		canvas.translate(this.pos_x, this.pos_y);

		// Overlay the beat track
		beatTrack.draw(canvas);
	}

	@Override
	public void onUpdate(float seconds) {
		// Show the timestep for debugging
		label.setText("" + sim.getTimestep());

		// Go back to the menu and stop the music if we disconnect
		if (bluetooth.getState() != State.CONNECTED) {
			beatTrack.stopMusic();
			nextScreen = new GameSetupScreen();
		}

		sim.onUpdate(seconds);
		beatTrack.refreshBeats();
		particles.tick(seconds);

		// Center the scroll on the most advanced enemy
		Unit mostAdvanced = sim.getOpponentsMostAdvancedUnit();
		if (mostAdvanced != null) {
			this.scrollTo(mostAdvanced.getX(), mostAdvanced.getY() - BBTHGame.HEIGHT / 2);
		}
	}

	@Override
	public void onTouchDown(float x, float y) {
		super.onTouchDown(x, y);
		boolean good = beatTrack.simulateTouch(sim, x + this.pos_x, y + this.pos_y);

		if (true || good) {
			for (int i = 0; i < 90; ++i) {
				float angle = MathUtils.randInRange(0, 2 * MathUtils.PI);
				float xVel = MathUtils.randInRange(25.f, 50.f) * FloatMath.cos(angle);
				float yVel = MathUtils.randInRange(25.f, 50.f) * FloatMath.sin(angle);
				particles.createParticle().circle().color(ColorUtils.randomHSV(0, 360, 0, 1, 0.5f, 1)).velocity(xVel, yVel).shrink(0.1f, 0.15f).radius(2.0f)
						.position(x + this.pos_x, y + this.pos_y).gravity(-150, -200);
			}
		}
	}

	@Override
	public void onTouchMove(float x, float y) {
		super.onTouchMove(x, y);
		sim.recordTapMove(x + this.pos_x, y + this.pos_y);
	}

	@Override
	public void onTouchUp(float x, float y) {
		super.onTouchUp(x, y);
		sim.recordTapUp(x + this.pos_x, y + this.pos_y);
	}

	/**
	 * Stupid method necessary because of android's weird context/activity mess.
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode) {
		bluetooth.onActivityResult(requestCode, resultCode);
	}
}
