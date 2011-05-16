package bbth.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import bbth.engine.achievements.Achievements;
import bbth.engine.fastgraph.Wall;
import bbth.engine.net.bluetooth.Bluetooth;
import bbth.engine.net.bluetooth.State;
import bbth.engine.net.simulation.LockStepProtocol;
import bbth.engine.particles.ParticleSystem;
import bbth.engine.sound.Beat.BeatType;
import bbth.engine.sound.MusicPlayer;
import bbth.engine.sound.MusicPlayer.OnCompletionListener;
import bbth.engine.ui.Anchor;
import bbth.engine.ui.UIView;
import bbth.engine.util.Timer;
import bbth.game.BBTHSimulation.GameState;
import bbth.game.ai.PlayerAI;

public class InGameScreen extends UIView implements OnCompletionListener {
	private BBTHSimulation sim;
	private Bluetooth bluetooth;
	private Team team;
	private BeatTrack beatTrack;
	private Wall currentWall;
	private ParticleSystem particles;
	private Paint paint;
	private static final boolean USE_UNIT_SELECTOR = false;
	private static final long TAP_HINT_DISPLAY_LENGTH = 3000;
	private static final long PLACEMENT_HINT_DISPLAY_LENGTH = 3000;
	private static final long DRAG_HINT_DISPLAY_LENGTH = 3000;

	// Timers for profiling while debugging
	private Timer entireUpdateTimer = new Timer();
	private Timer simUpdateTimer = new Timer();
	private Timer entireDrawTimer = new Timer();
	private Timer drawParticleTimer = new Timer();
	private Timer drawSimTimer = new Timer();
	private Timer drawUITimer = new Timer();

	private Path arrowPath;
	public ComboCircle combo_circle;
	private boolean userScrolling;
	private Tutorial tutorial;
	private long tap_location_hint_time;
	private long drag_tip_start_time;
	private PlayerAI player_ai;
	
	private boolean setSong;

	// TODO: Make a way to set the difficulty.
	private float aiDifficulty = 1.0f;

	public InGameScreen(Team playerTeam, Bluetooth bluetooth, Song song, LockStepProtocol protocol) {
		this.team = playerTeam;
		tutorial = new Tutorial(this);
		tutorial.setSize(BBTHGame.WIDTH * 0.75f, BBTHGame.HEIGHT / 2.f);
		tutorial.setAnchor(Anchor.CENTER_CENTER);
		tutorial.setPosition(BBTHGame.WIDTH / 2.f, BBTHGame.HEIGHT / 2.f);
		addSubview(tutorial);

		paint = new Paint(Paint.ANTI_ALIAS_FLAG);

		tap_location_hint_time = 0;

		this.bluetooth = bluetooth;
		sim = new BBTHSimulation(playerTeam, protocol, team == Team.SERVER);
		BBTHSimulation.PARTICLES.reset();
		
		if (team == Team.SERVER) {
			// Magic numbers
			sim.recordCustomEvent(0.f, 0.f, song.id);

			// Set up sound stuff
			beatTrack = new BeatTrack(song, this);
			setSong = true;
		} else {
			beatTrack = new BeatTrack(Song.DERP, this);
			setSong = false;
		}

		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStrokeWidth(2.0f);
		paint.setStrokeJoin(Join.ROUND);
		paint.setTextSize(20);
		paint.setAntiAlias(true);
		paint.setTextAlign(Align.CENTER);


		paint.setStrokeWidth(2.f);
		particles = new ParticleSystem(200, 0.5f);

		if (BBTHGame.IS_SINGLE_PLAYER) {
			player_ai = new PlayerAI(sim, sim.remotePlayer, sim.localPlayer, beatTrack, aiDifficulty);
		}

		arrowPath = new Path();
		arrowPath.moveTo(BBTHGame.WIDTH / 2 + 30, BBTHGame.HEIGHT * .75f + 55);
		arrowPath.lineTo(BBTHGame.WIDTH / 2 + 40, BBTHGame.HEIGHT * .75f + 65);
		arrowPath.lineTo(BBTHGame.WIDTH / 2 + 30, BBTHGame.HEIGHT * .75f + 75);
		arrowPath.close();
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

		// Draw the game
		drawSimTimer.start();
		canvas.save();
		canvas.translate(BBTHSimulation.GAME_X, BBTHSimulation.GAME_Y);

		if (team == Team.SERVER) {
			canvas.translate(0, BBTHSimulation.GAME_HEIGHT / 2);
			canvas.scale(1.f, -1.f);
			canvas.translate(0, -BBTHSimulation.GAME_HEIGHT / 2);
		}

		sim.draw(canvas);

		paint.setColor(team.getTempWallColor());
		paint.setStrokeCap(Cap.ROUND);
		if (currentWall != null) {
			canvas.drawLine(currentWall.a.x, currentWall.a.y, currentWall.b.x, currentWall.b.y, paint);
		}
		paint.setStrokeCap(Cap.BUTT);

		drawParticleTimer.start();
		particles.draw(canvas, paint);
		drawParticleTimer.stop();

		canvas.restore();
		drawSimTimer.stop();

		drawUITimer.start();
		// Overlay the beat track
		beatTrack.draw(canvas);

		// Overlay the unit selector
		if (USE_UNIT_SELECTOR) {
			sim.getMyUnitSelector().draw(canvas);
		}
		drawUITimer.stop();

		if (!tutorial.isFinished()) {
			tutorial.onDraw(canvas);
		} else if (!sim.isReady()) {
			paint.setColor(Color.WHITE);
			paint.setTextSize(20);
			canvas.drawText("Waiting for other player...", BBTHSimulation.GAME_X + BBTHSimulation.GAME_WIDTH / 2, BBTHSimulation.GAME_Y
					+ BBTHSimulation.GAME_HEIGHT / 2, paint);
		}

		long time_since_hint_start = System.currentTimeMillis() - tap_location_hint_time;
		if (time_since_hint_start < TAP_HINT_DISPLAY_LENGTH) {
			paint.setColor(Color.WHITE);
			paint.setStyle(Style.FILL);
			paint.setTextSize(18.0f);
			paint.setStrokeCap(Cap.ROUND);
			// paint.setAlpha((int) (255 - (time_since_hint_start / 4 % 255)));
			canvas.drawText("Tap further right ", BBTHGame.WIDTH / 2.0f, BBTHGame.HEIGHT * .75f + 20, paint);
			canvas.drawText("to make units!", BBTHGame.WIDTH / 2.0f, BBTHGame.HEIGHT * .75f + 45, paint);

			canvas.drawRect(BBTHGame.WIDTH / 2, BBTHGame.HEIGHT * .75f + 60, BBTHGame.WIDTH / 2 + 30, BBTHGame.HEIGHT * .75f + 70, paint);
			canvas.drawPath(arrowPath, paint);
		}

		// Draw unit placement hint if necessary.
		time_since_hint_start = System.currentTimeMillis() - sim.placement_tip_start_time;
		if (time_since_hint_start < PLACEMENT_HINT_DISPLAY_LENGTH) {
			paint.setColor(Color.WHITE);
			paint.setStyle(Style.FILL);
			paint.setTextSize(18.0f);
			// paint.setAlpha((int) (255 - (time_since_hint_start / 4 % 255)));
			canvas.drawText("Tap inside your zone of ", BBTHGame.WIDTH / 2.0f, BBTHGame.HEIGHT * .25f + 20, paint);
			canvas.drawText("influence to make units!", BBTHGame.WIDTH / 2.0f, BBTHGame.HEIGHT * .25f + 45, paint);
		}

		// Draw wall drag hint if necessary.
		time_since_hint_start = System.currentTimeMillis() - drag_tip_start_time;
		if (time_since_hint_start < DRAG_HINT_DISPLAY_LENGTH) {
			paint.setColor(Color.WHITE);
			paint.setStyle(Style.FILL);
			paint.setTextSize(18.0f);
			// paint.setAlpha((int) (255 - (time_since_hint_start / 4 % 255)));
			canvas.drawText("Drag finger further ", BBTHGame.WIDTH / 2.0f, BBTHGame.HEIGHT * .5f + 20, paint);
			canvas.drawText("to draw a longer wall!", BBTHGame.WIDTH / 2.0f, BBTHGame.HEIGHT * .5f + 45, paint);
		}

		if (BBTHGame.DEBUG) {
			// Draw timing information
			paint.setColor(Color.argb(63, 255, 255, 255));
			paint.setTextSize(8);
			paint.setStyle(Style.FILL);
			int x = 80;
			int y = 30;
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
		}

		if (!sim.isSynced()) {
			paint.setColor(Color.RED);
			paint.setTextSize(40);
			paint.setTextAlign(Align.CENTER);
			canvas.drawText("NOT SYNCED!", BBTHSimulation.GAME_X + BBTHSimulation.GAME_WIDTH / 2, BBTHSimulation.GAME_Y + BBTHSimulation.GAME_HEIGHT / 2 + 40,
					paint);
		}
		super.onDraw(canvas);
		entireDrawTimer.stop();

		// draw achievement stuff
		Achievements.INSTANCE.draw(canvas, BBTHGame.WIDTH, BBTHGame.HEIGHT / 15.f);
	}

	@Override
	public void onUpdate(float seconds) {
		entireUpdateTimer.start();

		if (!setSong && team == Team.CLIENT && sim.song != null) {
			// Set up sound stuff
			beatTrack.setSong(sim.song);
			setSong = true;
		}
		
		if (!BBTHGame.IS_SINGLE_PLAYER) {	
			// Stop the music if we disconnect
			if (bluetooth.getState() != State.CONNECTED) {
				beatTrack.stopMusic();
				nextScreen = new GameStatusMessageScreen.DisconnectScreen();
			}
		}

		// Update the single-player AI
		if (BBTHGame.IS_SINGLE_PLAYER && sim.getGameState() == GameState.IN_PROGRESS) {
			player_ai.update(seconds);
		}

		// Update the game
		simUpdateTimer.start();
		if (BBTHGame.IS_SINGLE_PLAYER) {
			sim.update(seconds);
		} else {
			sim.onUpdate(seconds);
		}
		simUpdateTimer.stop();

		// Update the tutorial
		if (!tutorial.isFinished()) {
			tutorial.onUpdate(seconds);
		}

		// Start the music
		if (setSong && sim.isReady() && !beatTrack.isPlaying()) {
			beatTrack.startMusic();
		}

		// Get new beats, yo
		beatTrack.refreshBeats();

		// Shinies
		particles.tick(seconds);
		entireUpdateTimer.stop();

		// End the game when the time comes
		GameState gameState = sim.getGameState();
		if (gameState != GameState.WAITING_TO_START && gameState != GameState.IN_PROGRESS) {
			// moveToNextScreen();
		}

		// Update achievement stuff
		Achievements.INSTANCE.tick(seconds);
	}

	private void moveToNextScreen() {
		beatTrack.stopMusic();

		// Move on to the next screen
		GameState gameState = sim.getGameState();
		if (gameState == GameState.TIE) {
			nextScreen = new GameStatusMessageScreen.TieScreen();
		} else if (sim.isServer == (gameState == GameState.SERVER_WON)) {
			nextScreen = new GameStatusMessageScreen.WinScreen();
		} else {
			nextScreen = new GameStatusMessageScreen.LoseScreen();
		}
	}

	@Override
	public void onTouchDown(float x, float y) {

		// We don't want to interact with the game if the tutorial is running!
		if (!tutorial.isFinished()) {
			tutorial.onTouchDown(x, y);
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

		BeatType beatType = beatTrack.onTouchDown(x, y);

		boolean isHold = (beatType == BeatType.HOLD);
		boolean isOnBeat = (beatType != BeatType.REST);

		if (isHold) {
			Achievements.INSTANCE.unlock("Test Success");
		}

		x -= BBTHSimulation.GAME_X;
		y -= BBTHSimulation.GAME_Y;
		if (team == Team.SERVER)
			y = BBTHSimulation.GAME_HEIGHT - y;

		if (x < 0) {
			// Display a message saying they should tap in-bounds
			tap_location_hint_time = System.currentTimeMillis();
		}

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

		// We don't want to interact with the game if the tutorial is running!
		if (!tutorial.isFinished()) {
			tutorial.onTouchMove(x, y);
			return;
		}

		// We moved offscreen!
		x -= BBTHSimulation.GAME_X;
		y -= BBTHSimulation.GAME_Y;
		if (team == Team.SERVER)
			y = BBTHSimulation.GAME_HEIGHT - y;

		if (currentWall != null) {
			if (x < 0 || y < 0) {
				simulateWallGeneration();
			} else {
				currentWall.b.set(x, y);
			}
		}

		if (BBTHGame.IS_SINGLE_PLAYER) {
			sim.simulateTapMove(x, y, true);
		} else {
			sim.recordTapMove(x, y);
		}
	}

	@Override
	public void onTouchUp(float x, float y) {

		// We don't want to interact with the game if the tutorial is running!
		if (!tutorial.isFinished()) {
			tutorial.onTouchUp(x, y);
			return;
		}

		beatTrack.onTouchUp(x, y);

		if (userScrolling) {
			userScrolling = false;
			return;
		}

		x -= BBTHSimulation.GAME_X;
		y -= BBTHSimulation.GAME_Y;
		if (team == Team.SERVER)
			y = BBTHSimulation.GAME_HEIGHT - y;

		if (currentWall != null) {
			simulateWallGeneration();
		}

		if (BBTHGame.IS_SINGLE_PLAYER) {
			sim.simulateTapUp(x, y, true);
		} else {
			sim.recordTapUp(x, y);
		}
	}

	public void simulateWallGeneration() {
		currentWall.updateLength();

		if (currentWall.length <= BBTHSimulation.MIN_WALL_LENGTH) {
			// Display a tip about dragging!
			drag_tip_start_time = System.currentTimeMillis();
		}

		if (currentWall.length >= BBTHSimulation.MIN_WALL_LENGTH) {
			BBTHSimulation.generateParticlesForWall(currentWall, this.team);
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
		// End both games at the same time with a synced event
		if (BBTHGame.IS_SINGLE_PLAYER) {
			sim.simulateCustomEvent(0, 0, BBTHSimulation.MUSIC_STOPPED_EVENT, true);
		} else {
			sim.recordCustomEvent(0, 0, BBTHSimulation.MUSIC_STOPPED_EVENT);
		}
	}

	public void startGame() {
		removeSubview(tutorial);
		sim.recordCustomEvent(0, 0, BBTHSimulation.TUTORIAL_DONE_EVENT);
		if (BBTHGame.IS_SINGLE_PLAYER)
			sim.setBothPlayersReady();
	}
}
