package bbth.game;

import android.graphics.*;
import android.graphics.Paint.*;
import android.util.FloatMath;
import bbth.engine.fastgraph.Wall;
import bbth.engine.net.bluetooth.*;
import bbth.engine.net.simulation.LockStepProtocol;
import bbth.engine.particles.ParticleSystem;
import bbth.engine.sound.Beat.BeatType;
import bbth.engine.ui.*;
import bbth.engine.util.MathUtils;
import bbth.game.BeatTrack.Song;
import bbth.game.units.Unit;

public class InGameScreen extends UIScrollView {
	private BBTHSimulation sim;
	private UILabel label;
	private Bluetooth bluetooth;
	private Team team;
	private BeatTrack beatTrack;
	private Wall currentWall;
	private ParticleSystem particles;
	private Paint paint, serverHealthPaint, clientHealthPaint;
	private final RectF minimapRect, serverHealthRect, clientHealthRect;
	private final float HEALTHBAR_HEIGHT = 10;

	public InGameScreen(Team playerTeam, Bluetooth bluetooth, Song song,
			LockStepProtocol protocol) {
		super(null);

		MathUtils.resetRandom(0);

		this.team = playerTeam;

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

		this.setContentRect(0, 0, BBTHSimulation.GAME_WIDTH,
				BBTHSimulation.GAME_HEIGHT);

		this.bluetooth = bluetooth;
		sim = new BBTHSimulation(playerTeam, protocol, team == Team.SERVER);
		sim.setupSubviews(this);

		if (this.team == Team.SERVER) {
			this.scrollTo(0, BBTHSimulation.GAME_HEIGHT / 2 - BBTHGame.HEIGHT);
		} else {
			this.scrollTo(0, BBTHSimulation.GAME_HEIGHT / 2);
		}

		// Set up sound stuff
		beatTrack = new BeatTrack(song);
		beatTrack.startMusic();

		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStrokeWidth(2.0f);
		paint.setStrokeJoin(Join.ROUND);
		paint.setTextSize(20);
		paint.setAntiAlias(true);

		paint.setStrokeWidth(2.f);
		particles = new ParticleSystem(200, 0.5f);

		minimapRect = new RectF(BBTHGame.WIDTH - 50, BBTHGame.HEIGHT / 2
				+ HEALTHBAR_HEIGHT, BBTHGame.WIDTH, BBTHGame.HEIGHT
				- HEALTHBAR_HEIGHT);
		serverHealthRect = new RectF(minimapRect.left, minimapRect.top
				- HEALTHBAR_HEIGHT, minimapRect.right, minimapRect.top);
		clientHealthRect = new RectF(minimapRect.left, minimapRect.bottom,
				minimapRect.right, minimapRect.bottom + HEALTHBAR_HEIGHT);
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

		paint.setColor(team.getTempWallColor());
		paint.setStrokeCap(Cap.ROUND);
		if (currentWall != null) {
			canvas.drawLine(currentWall.a.x, currentWall.a.y, currentWall.b.x,
					currentWall.b.y, paint);
		}
		paint.setStrokeCap(Cap.BUTT);

		particles.draw(canvas, paint);
		canvas.translate(this.pos_x, this.pos_y);

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
		canvas.drawRect(0, 0, BBTHSimulation.GAME_WIDTH,
				BBTHSimulation.GAME_HEIGHT, paint);
		paint.setColor(Color.WHITE);
		canvas.drawRect(this.pos_x, this.pos_y, BBTHGame.WIDTH + this.pos_x,
				BBTHGame.HEIGHT + this.pos_y, paint);
		paint.setStyle(Style.FILL);
		canvas.restore();

		// Draw health bars
		canvas.drawRect(serverHealthRect, serverHealthPaint);
		canvas.drawRect(clientHealthRect, clientHealthPaint);
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

		// Update the game
		sim.onUpdate(seconds);
		
		// Update healths
		clientHealthRect.right = MathUtils.scale(0, 100, minimapRect.left + 1,
				minimapRect.right - 1, sim.localPlayer.getHealth());
		serverHealthRect.right = MathUtils.scale(0, 100, minimapRect.left + 1,
				minimapRect.right - 1, sim.remotePlayer.getHealth());

		// See whether we won or lost
		if (sim.localPlayer.getHealth() <= 0.f) {
			// We lost the game!
			this.nextScreen = BBTHGame.LOSE_SCREEN;
		} 
		
		if (sim.remotePlayer.getHealth() <= 0.f) {
			// We won the game!
			this.nextScreen = BBTHGame.WIN_SCREEN;
		}
		
		// Get new beats, yo
		beatTrack.refreshBeats();

		// Center the scroll on the most advanced enemy
		Unit mostAdvanced = sim.getOpponentsMostAdvancedUnit();
		if (mostAdvanced != null) {
			this.scrollTo(mostAdvanced.getX(), mostAdvanced.getY()
					- BBTHGame.HEIGHT / 2);
		}

		// Shinies
		particles.tick(seconds);
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
					float posX = currentWall.a.x * i / numParticles
							+ currentWall.b.x * (numParticles - i)
							/ numParticles;
					float posY = currentWall.a.y * i / numParticles
							+ currentWall.b.y * (numParticles - i)
							/ numParticles;
					float angle = MathUtils.randInRange(0, 2 * MathUtils.PI);
					float xVel = MathUtils.randInRange(25.f, 50.f)
							* FloatMath.cos(angle);
					float yVel = MathUtils.randInRange(25.f, 50.f)
							* FloatMath.sin(angle);

					particles.createParticle().circle().velocity(xVel, yVel)
							.shrink(0.1f, 0.15f).radius(3.0f)
							.position(posX, posY).color(team.getRandomShade());
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
}
