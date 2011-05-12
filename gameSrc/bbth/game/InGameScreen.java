package bbth.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Paint.Align;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.FloatMath;
import bbth.engine.fastgraph.Wall;
import bbth.engine.net.bluetooth.Bluetooth;
import bbth.engine.net.bluetooth.State;
import bbth.engine.net.simulation.LockStepProtocol;
import bbth.engine.particles.ParticleSystem;
import bbth.engine.sound.Beat.BeatType;
import bbth.engine.sound.MusicPlayer;
import bbth.engine.sound.MusicPlayer.OnCompletionListener;
import bbth.engine.ui.UILabel;
import bbth.engine.ui.UIScrollView;
import bbth.engine.util.MathUtils;
import bbth.engine.util.Timer;
import bbth.game.BeatTrack.Song;
import bbth.game.units.Unit;

public class InGameScreen extends UIScrollView implements OnCompletionListener {
	private BBTHSimulation sim;
	private UILabel label;
	private Bluetooth bluetooth;
	private Team team;
	private BeatTrack beatTrack;
	private Wall currentWall;
	private ParticleSystem particles;
	private Paint paint, serverHealthPaint, clientHealthPaint;
	private final RectF minimapRect, serverHealthRect, clientHealthRect;
	private static final float HEALTHBAR_HEIGHT = 10;

	private Timer entireUpdateTimer = new Timer();
	private Timer simUpdateTimer = new Timer();
	private Timer entireDrawTimer = new Timer();
	private Timer drawParticleTimer = new Timer();
	private Timer drawSimTimer = new Timer();
	private Timer drawUITimer = new Timer();
	
	public ComboCircle combo_circle;

	public InGameScreen(Team playerTeam, Bluetooth bluetooth, Song song, LockStepProtocol protocol) {
		super(null);

		MathUtils.resetRandom(0);

		this.team = playerTeam;

		combo_circle = new ComboCircle(this.team);
		
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		serverHealthPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		clientHealthPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

		serverHealthPaint.setColor(Team.SERVER.getBaseColor());
		clientHealthPaint.setColor(Team.CLIENT.getBaseColor());

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

		this.setContentRect(0, 0, BBTHSimulation.GAME_WIDTH, BBTHSimulation.GAME_HEIGHT);

		this.bluetooth = bluetooth;
		sim = new BBTHSimulation(playerTeam, protocol, team == Team.SERVER);
		sim.setupSubviews(this);

		if (this.team == Team.SERVER) {
			this.scrollTo(0, BBTHSimulation.GAME_HEIGHT / 2 - BBTHGame.HEIGHT);
		} else {
			this.scrollTo(0, BBTHSimulation.GAME_HEIGHT / 2);
		}

		// Set up sound stuff
		beatTrack = new BeatTrack(song, this);
		beatTrack.startMusic();

		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStrokeWidth(2.0f);
		paint.setStrokeJoin(Join.ROUND);
		paint.setTextSize(20);
		paint.setAntiAlias(true);

		paint.setStrokeWidth(2.f);
		particles = new ParticleSystem(200, 0.5f);

		minimapRect = new RectF(BBTHGame.WIDTH - 50, BBTHGame.HEIGHT / 2 + HEALTHBAR_HEIGHT, BBTHGame.WIDTH, BBTHGame.HEIGHT - HEALTHBAR_HEIGHT);
		serverHealthRect = new RectF(minimapRect.left, minimapRect.top - HEALTHBAR_HEIGHT, minimapRect.right, minimapRect.top);
		clientHealthRect = new RectF(minimapRect.left, minimapRect.bottom, minimapRect.right, minimapRect.bottom + HEALTHBAR_HEIGHT);
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
		canvas.translate(-this.pos_x, -this.pos_y);
		drawSimTimer.start();
		sim.draw(canvas);
		drawSimTimer.stop();

		paint.setColor(team.getTempWallColor());
		paint.setStrokeCap(Cap.ROUND);
		if (currentWall != null) {
			canvas.drawLine(currentWall.a.x, currentWall.a.y, currentWall.b.x, currentWall.b.y, paint);
		}
		paint.setStrokeCap(Cap.BUTT);

		drawParticleTimer.start();
		
		particles.draw(canvas, paint);

		drawParticleTimer.stop();

		combo_circle.onDraw(canvas);		

		canvas.translate(this.pos_x, this.pos_y);

		drawUITimer.start();
		// Overlay the beat track
		beatTrack.draw(canvas);

		// Overlay the unit selector
		sim.getMyUnitSelector().draw(canvas);

		// Draw minimap
		paint.setARGB(127, 0, 0, 0);
		canvas.drawRect(this.minimapRect, paint);

		paint.setColor(Color.WHITE);
		float scaleX = minimapRect.width() / BBTHSimulation.GAME_WIDTH;
		float scaleY = minimapRect.height() / BBTHSimulation.GAME_HEIGHT;
		canvas.save();
		canvas.translate(minimapRect.left, minimapRect.top);
		canvas.scale(scaleX, scaleY);
		sim.drawForMiniMap(canvas);

		paint.setColor(Color.GRAY);
		paint.setStyle(Style.STROKE);
		canvas.drawRect(0, 0, BBTHSimulation.GAME_WIDTH, BBTHSimulation.GAME_HEIGHT, paint);
		paint.setColor(Color.WHITE);
		canvas.drawRect(this.pos_x, this.pos_y, BBTHGame.WIDTH + this.pos_x, BBTHGame.HEIGHT + this.pos_y, paint);
		paint.setStyle(Style.FILL);
		canvas.restore();
		drawUITimer.stop();

		// Draw health bars
		canvas.drawRect(serverHealthRect, serverHealthPaint);
		canvas.drawRect(clientHealthRect, clientHealthPaint);

		// Draw timing information
		paint.setColor(Color.argb(63, 255, 255, 255));
		paint.setTextSize(8);
		int x = 50;
		int y = 20;
		int jump = 11;
		canvas.drawText("Entire update: " + entireUpdateTimer.getMilliseconds() + " ms", x, y += jump, paint);
		canvas.drawText("- Sim update: " + simUpdateTimer.getMilliseconds() + " ms", x, y += jump, paint);
		canvas.drawText("  - Sim tick: " + sim.entireTickTimer.getMilliseconds() + " ms", x, y += jump, paint);
		canvas.drawText("    - AI tick: " + sim.aiTickTimer.getMilliseconds() + " ms", x, y += jump, paint);
		canvas.drawText("      - Controller: " + sim.aiControllerTimer.getMilliseconds() + " ms", x, y += jump, paint);
		canvas.drawText("      - Server player: " + sim.serverPlayerTimer.getMilliseconds() + " ms", x, y += jump, paint);
		canvas.drawText("      - Client player: " + sim.clientPlayerTimer.getMilliseconds() + " ms", x, y += jump, paint);
		canvas.drawText("Entire draw: " + entireDrawTimer.getMilliseconds() + " ms", x, y += jump * 2, paint);
		canvas.drawText("- Sim: " + drawSimTimer.getMilliseconds() + " ms", x, y += jump, paint);
		canvas.drawText("- Particles: " + drawParticleTimer.getMilliseconds() + " ms", x, y += jump, paint);
		canvas.drawText("- UI: " + drawUITimer.getMilliseconds() + " ms", x, y += jump, paint);
		entireDrawTimer.stop();
	}

	@Override
	public void onUpdate(float seconds) {
		entireUpdateTimer.start();

		// Show the timestep for debugging
		label.setText("" + sim.getTimestep());

		// Stop the music if we disconnect
		if (bluetooth.getState() != State.CONNECTED) {
			beatTrack.stopMusic();
			nextScreen = new GameStatusMessageScreen.DisconnectScreen();
		}

		// Update the game
		simUpdateTimer.start();
		sim.onUpdate(seconds);
		simUpdateTimer.stop();

		// Update healths
		clientHealthRect.right = MathUtils.scale(0, 100, minimapRect.left + 1, minimapRect.right - 1, sim.localPlayer.getHealth());
		serverHealthRect.right = MathUtils.scale(0, 100, minimapRect.left + 1, minimapRect.right - 1, sim.remotePlayer.getHealth());

		// See whether we won or lost
		if (sim.localPlayer.getHealth() <= 0.f) {
			// We lost the game!
			// this.nextScreen = BBTHGame.LOSE_SCREEN;
		}

		if (sim.remotePlayer.getHealth() <= 0.f) {
			// We won the game!
			// this.nextScreen = BBTHGame.WIN_SCREEN;
		}

		// Get new beats, yo
		beatTrack.refreshBeats();

		// Center the scroll on the most advanced enemy
		Unit mostAdvanced = sim.getOpponentsMostAdvancedUnit();
		if (mostAdvanced != null) {
			this.scrollTo(mostAdvanced.getX(), mostAdvanced.getY() - BBTHGame.HEIGHT / 2);
		}

		// Shinies
		particles.tick(seconds);
		entireUpdateTimer.stop();
	}

	@Override
	public void onTouchDown(float x, float y) {
		super.onTouchDown(x, y);

		int unitType = sim.getMyUnitSelector().checkUnitChange(x, y);
		if (unitType >= 0) {
			sim.recordCustomEvent(unitType);
			return;
		}

		float worldX = x + this.pos_x;
		float worldY = y + this.pos_y;

		BeatType beatType = beatTrack.checkTouch(sim, worldX, worldY);

		// Unpack!
		boolean isHold = (beatType == BeatType.HOLD);
		boolean isOnBeat = (beatType != BeatType.REST);

		if (isOnBeat && isHold) {
			currentWall = new Wall(worldX, worldY, worldX, worldY);
		}
		
		// Update player combos.
		if (isOnBeat) {
			if (beatTrack.getCombo() >= BBTHSimulation.UBER_CIRCLE_THRESHOLD) {
				float radius = BBTHSimulation.UBER_CIRCLE_SIZE_MOD * beatTrack.getCombo() + BBTHSimulation.UBER_CIRCLE_INIT_SIZE;
				setComboCircle(MathUtils.randInRange(pos_x + (100 + radius), pos_x + BBTHGame.WIDTH-(100 + radius)), MathUtils.randInRange(pos_y + (100 + radius), pos_y + BBTHGame.HEIGHT-(100 + radius)), radius);
			}
		} else {
			clearComboCircle();
		}

		sim.recordTapDown(worldX, worldY, isHold, isOnBeat);
	}

	@Override
	public void onTouchMove(float x, float y) {
		super.onTouchMove(x, y);

		float worldX = x + this.pos_x;
		float worldY = y + this.pos_y;

		if (currentWall != null) {
			currentWall.b.set(worldX, worldY);
		}

		sim.recordTapMove(worldX, worldY);
	}

	@Override
	public void onTouchUp(float x, float y) {
		super.onTouchUp(x, y);

		float worldX = x + this.pos_x;
		float worldY = y + this.pos_y;

		if (currentWall != null) {
			currentWall.b.set(worldX, worldY);
			currentWall.updateLength();
			if (currentWall.length >= BBTHSimulation.MIN_WALL_LENGTH) {
				// Spawn particles
				int numParticles = 40;

				for (int i = 0; i < numParticles; i++) {
					float posX = currentWall.a.x * i / numParticles + currentWall.b.x * (numParticles - i) / numParticles;
					float posY = currentWall.a.y * i / numParticles + currentWall.b.y * (numParticles - i) / numParticles;
					float angle = MathUtils.randInRange(0, 2 * MathUtils.PI);
					float xVel = MathUtils.randInRange(25.f, 50.f) * FloatMath.cos(angle);
					float yVel = MathUtils.randInRange(25.f, 50.f) * FloatMath.sin(angle);

					particles.createParticle().circle().velocity(xVel, yVel).shrink(0.1f, 0.15f).radius(3.0f).position(posX, posY).color(team.getRandomShade());
				}
			}

			currentWall = null;
		}

		sim.recordTapUp(worldX, worldY);
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
			nextScreen = new GameStatusMessageScreen.LoseScreen(); 
		} else if (myHealth > theirHealth) {
			nextScreen = new GameStatusMessageScreen.WinScreen();
		} else {
			nextScreen = new GameStatusMessageScreen.TieScreen();
		}
	}
	
	public void setComboCircle(float center_x, float center_y, float _uber_circle_radius) {
		this.combo_circle.setPosition(center_x, center_y);
		this.combo_circle.radius = _uber_circle_radius;
	}
	
	public void setComboCircle(PointF _uber_circle_center, float _uber_circle_radius) {
		setComboCircle(_uber_circle_center.x, _uber_circle_center.y, _uber_circle_radius);
	}
	
	public void clearComboCircle() {
		this.combo_circle.radius = -1.f;
	}
}
