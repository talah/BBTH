package bbth.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import bbth.engine.fastgraph.Wall;
import bbth.engine.net.bluetooth.Bluetooth;
import bbth.engine.net.bluetooth.State;
import bbth.engine.net.simulation.LockStepProtocol;
import bbth.engine.particles.ParticleSystem;
import bbth.engine.sound.Beat.BeatType;
import bbth.engine.sound.MusicPlayer;
import bbth.engine.sound.MusicPlayer.OnCompletionListener;
import bbth.engine.ui.UILabel;
import bbth.engine.ui.UIView;
import bbth.engine.util.Timer;
import bbth.game.BeatTrack.Song;

public class InGameScreen extends UIView implements OnCompletionListener {
	private BBTHSimulation sim;
	private Bluetooth bluetooth;
	private Team team;
	private BeatTrack beatTrack;
	private Wall currentWall;
	private ParticleSystem particles;
	private Paint paint, serverHealthPaint, clientHealthPaint;
	private UILabel label;
	private static final boolean USE_UNIT_SELECTOR = false;

	private Timer entireUpdateTimer = new Timer();
	private Timer simUpdateTimer = new Timer();
	private Timer entireDrawTimer = new Timer();
	private Timer drawParticleTimer = new Timer();
	private Timer drawSimTimer = new Timer();
	private Timer drawUITimer = new Timer();

	public ComboCircle combo_circle;
	private boolean userScrolling;
	private Tutorial tutorial;

	public InGameScreen(Team playerTeam, Bluetooth bluetooth, Song song,
			LockStepProtocol protocol) {
		this.team = playerTeam;
		tutorial = new Tutorial(team == Team.SERVER);

		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		serverHealthPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		clientHealthPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

		serverHealthPaint.setColor(Team.SERVER.getBaseColor());
		clientHealthPaint.setColor(Team.CLIENT.getBaseColor());

		// Test labels
		label = new UILabel("", null);
		label.setTextSize(10);
		label.setPosition(10, 10);
		label.setSize(BBTHGame.WIDTH - 20, 10);
		label.setTextAlign(Align.CENTER);
		addSubview(label);

		this.bluetooth = bluetooth;
		sim = new BBTHSimulation(playerTeam, protocol, team == Team.SERVER);
		BBTHSimulation.PARTICLES.reset();

		// Set up sound stuff
		beatTrack = new BeatTrack(song, this);

		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStrokeWidth(2.0f);
		paint.setStrokeJoin(Join.ROUND);
		paint.setTextSize(20);
		paint.setAntiAlias(true);

		paint.setStrokeWidth(2.f);
		particles = new ParticleSystem(200, 0.5f);
	}

	@Override
	public void onStop() {
		beatTrack.stopMusic();

		// Disconnect when we lose focus
		bluetooth.disconnect();
	}

	@Override
	public void onDraw(Canvas canvas) {
		entireDrawTimer.start();
		super.onDraw(canvas);

		// Draw the game
		drawSimTimer.start();
		canvas.save();
		canvas.translate(BBTHSimulation.GAME_X, BBTHSimulation.GAME_Y);
		sim.draw(canvas);
		canvas.restore();
		drawSimTimer.stop();

		paint.setColor(team.getTempWallColor());
		paint.setStrokeCap(Cap.ROUND);
		if (currentWall != null) {
			canvas.drawLine(currentWall.a.x, currentWall.a.y, currentWall.b.x,
					currentWall.b.y, paint);
		}
		paint.setStrokeCap(Cap.BUTT);

		drawParticleTimer.start();
		particles.draw(canvas, paint);
		drawParticleTimer.stop();

		drawUITimer.start();
		// Overlay the beat track
		beatTrack.draw(canvas);

		// Overlay the unit selector
		if (USE_UNIT_SELECTOR) {
			sim.getMyUnitSelector().draw(canvas);
		}
		drawUITimer.stop();

		if (!tutorial.isFinished()) {
			tutorial.draw(canvas);
		}

		if (BBTHGame.DEBUG) {
			// Draw timing information
			paint.setColor(Color.argb(63, 255, 255, 255));
			paint.setTextSize(8);
			int x = 50;
			int y = 20;
			int jump = 11;
			canvas.drawText(
					"Entire update: " + entireUpdateTimer.getMilliseconds()
							+ " ms", x, y += jump, paint);
			canvas.drawText("- Sim update: " + simUpdateTimer.getMilliseconds()
					+ " ms", x, y += jump, paint);
			canvas.drawText(
					"  - Sim tick: " + sim.entireTickTimer.getMilliseconds()
							+ " ms", x, y += jump, paint);
			canvas.drawText(
					"    - AI tick: " + sim.aiTickTimer.getMilliseconds()
							+ " ms", x, y += jump, paint);
			canvas.drawText(
					"      - Controller: "
							+ sim.aiControllerTimer.getMilliseconds() + " ms",
					x, y += jump, paint);
			canvas.drawText(
					"      - Server player: "
							+ sim.serverPlayerTimer.getMilliseconds() + " ms",
					x, y += jump, paint);
			canvas.drawText(
					"      - Client player: "
							+ sim.clientPlayerTimer.getMilliseconds() + " ms",
					x, y += jump, paint);
			canvas.drawText("Entire draw: " + entireDrawTimer.getMilliseconds()
					+ " ms", x, y += jump * 2, paint);
			canvas.drawText("- Sim: " + drawSimTimer.getMilliseconds() + " ms",
					x, y += jump, paint);
			canvas.drawText(
					"- Particles: " + drawParticleTimer.getMilliseconds()
							+ " ms", x, y += jump, paint);
			canvas.drawText("- UI: " + drawUITimer.getMilliseconds() + " ms",
					x, y += jump, paint);
			entireDrawTimer.stop();
		}
	}

	@Override
	public void onUpdate(float seconds) {
		entireUpdateTimer.start();

		// Show the timestep for debugging
		label.setText("" + sim.getTimestep());

		if (!BBTHGame.IS_SINGLE_PLAYER) {
			// Stop the music if we disconnect
			if (bluetooth.getState() != State.CONNECTED) {
				beatTrack.stopMusic();
				nextScreen = new GameStatusMessageScreen.DisconnectScreen();
			}
		}

		// Update the game
		simUpdateTimer.start();
		sim.onUpdate(seconds);
		simUpdateTimer.stop();

		// Update the tutorial
		if (!tutorial.isFinished()) {
			tutorial.update(seconds);
			if (tutorial.isFinished()) {
				sim.recordCustomEvent(0, 0, BBTHSimulation.TUTORIAL_DONE);
			}
		}

		// Start the music
		if (sim.isReady() && !beatTrack.isPlaying()) {
			beatTrack.startMusic();
		}

		// See whether we won or lost
		if (sim.localPlayer.getHealth() <= 0.f) {
			// We lost the game!
			beatTrack.stopMusic();
			this.nextScreen = new GameStatusMessageScreen.LoseScreen();
		}

		if (sim.remotePlayer.getHealth() <= 0.f) {
			// We won the game!
			beatTrack.stopMusic();
			this.nextScreen = new GameStatusMessageScreen.WinScreen();
		}

		// Get new beats, yo
		beatTrack.refreshBeats();

		// Shinies
		particles.tick(seconds);
		entireUpdateTimer.stop();

		sim.update(seconds);
	}

	@Override
	public void onTouchDown(float x, float y) {
		if (!tutorial.isFinished()) {
			tutorial.touchDown(x, y);
			return;
		}

		if (USE_UNIT_SELECTOR) {
			int unitType = sim.getMyUnitSelector().checkUnitChange(x, y);
			if (unitType >= 0) {
				if (BBTHGame.IS_SINGLE_PLAYER) {
					sim.simulateCustomEvent(0, 0, unitType, true);
				} else {
					sim.recordCustomEvent(0, 0, unitType);
				}
				return;
			}
		}

		BeatType beatType = beatTrack.checkTouch(sim, x, y);

		// Unpack!
		boolean isHold = (beatType == BeatType.HOLD);
		boolean isOnBeat = (beatType != BeatType.REST);

		x -= BBTHSimulation.GAME_X;
		y -= BBTHSimulation.GAME_Y;

		if (isOnBeat && isHold && x > 0 && y > 0) {
			currentWall = new Wall(x, y, x, y);
		}

		if (BBTHGame.IS_SINGLE_PLAYER) {
			sim.simulateTapDown(x, y, true, isHold, isOnBeat);
		} else {
			sim.recordTapDown(x, y, isHold, isOnBeat);
		}
	}

	@Override
	public void onTouchMove(float x, float y) {
		if (!tutorial.isFinished()) {
			tutorial.touchMove(x, y);
			return;
		}

		x -= BBTHSimulation.GAME_X;
		y -= BBTHSimulation.GAME_Y;

		// We moved offscreen!
		if (x < 0 || y < 0) {
			simulateWallGeneration();
		} else if (currentWall != null) {
			currentWall.b.set(x, y);
		}

		if (BBTHGame.IS_SINGLE_PLAYER) {
			sim.simulateTapMove(x, y, true);
		} else {
			sim.recordTapMove(x, y);
		}
	}

	@Override
	public void onTouchUp(float x, float y) {
		if (!tutorial.isFinished()) {
			tutorial.touchUp(x, y);
			return;
		}

		if (userScrolling) {
			userScrolling = false;
			return;
		}

		simulateWallGeneration();

		x -= BBTHSimulation.GAME_X;
		y -= BBTHSimulation.GAME_Y;

		if (BBTHGame.IS_SINGLE_PLAYER) {
			sim.simulateTapUp(x, y, true);
		} else {
			sim.recordTapUp(x, y);
		}
	}

	public void simulateWallGeneration() {
		if (currentWall == null)
			return;
		
		currentWall.updateLength();

		if (currentWall.length >= BBTHSimulation.MIN_WALL_LENGTH) {
			sim.generateParticlesForWall(currentWall, this.team);
		}

		currentWall = null;
	}

	/**
	 * Stupid method necessary because of android's weird context/activity mess.
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode) {
		bluetooth.onActivityResult(requestCode, resultCode);
	}

	@Override
	public void onCompletion(MusicPlayer mp) {
		float myHealth = sim.localPlayer.getHealth();
		float theirHealth = sim.remotePlayer.getHealth();

		if (myHealth < theirHealth) {
			beatTrack.stopMusic();
			nextScreen = new GameStatusMessageScreen.LoseScreen();
		} else if (myHealth > theirHealth) {
			beatTrack.stopMusic();
			nextScreen = new GameStatusMessageScreen.WinScreen();
		} else {
			beatTrack.stopMusic();
			nextScreen = new GameStatusMessageScreen.TieScreen();
		}
	}
}
