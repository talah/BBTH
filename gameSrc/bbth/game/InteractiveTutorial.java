package bbth.game;

import static bbth.game.BBTHSimulation.GAME_HEIGHT;
import static bbth.game.BBTHSimulation.GAME_WIDTH;
import static bbth.game.BBTHSimulation.GAME_X;
import static bbth.game.BBTHSimulation.GAME_Y;
import static bbth.game.BeatTrack.BEAT_CIRCLE_RADIUS;
import static bbth.game.BeatTrack.BEAT_LINE_X;
import static bbth.game.BeatTrack.BEAT_LINE_Y;
import static bbth.game.BeatTrack.BEAT_TRACK_WIDTH;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.FloatMath;
import bbth.engine.ai.Pathfinder;
import bbth.engine.fastgraph.FastGraphGenerator;
import bbth.engine.fastgraph.Wall;
import bbth.engine.sound.Beat;
import bbth.engine.ui.Anchor;
import bbth.engine.ui.UIButton;
import bbth.engine.ui.UIButtonDelegate;
import bbth.engine.ui.UIView;
import bbth.engine.util.Bag;
import bbth.engine.util.MathUtils;
import bbth.game.ai.AIController;
import bbth.game.units.Unit;
import bbth.game.units.UnitManager;

/**
 * This reimplements a large part of BBTHSimulation because our code is all
 * mixed up instead of being factored into MVC.
 */
public class InteractiveTutorial extends Tutorial implements UIButtonDelegate, UnitManager {

	private static final Bag<Unit> emptyUnitBag = new Bag<Unit>();
	private static final Path path = new Path();
	private static final Paint paint = new Paint();
	public static final float MIN_SONG_TIME = -6;
	public static final boolean USE_OK_BUTTONS = false;
	static {
		paint.setAntiAlias(true);
	}

	private abstract class Step extends UIView implements UIButtonDelegate {
		@Override
		public boolean containsPoint(float x, float y) {
			return true; // >_>
		}

		public boolean isPaused() {
			return false;
		}

		protected final void addOKButton(float x, float y) {
			if (USE_OK_BUTTONS) {
				UIButton button = new UIButton("OK");
				button.setAnchor(Anchor.TOP_CENTER);
				button.setSize(50, 30);
				button.setPosition(x, y + 28);
				button.setButtonDelegate(this);
				button.expandForHitTesting(20, 20);
				addSubview(button);
			} else {
				onClick(null);
			}
		}

		@Override
		public void onClick(UIButton button) {
		}
	}

	private class PlaceUnitStep extends Step {
		@Override
		public void onDraw(Canvas canvas) {
			float x = GAME_X + GAME_WIDTH / 2;
			float y = GAME_Y + GAME_HEIGHT * 0.8f;
			paint.setColor(Color.WHITE);
			paint.setTextSize(15);
			paint.setTextAlign(Align.CENTER);
			canvas.drawText("When the beat is between", x, y - 17, paint);
			canvas.drawText("the two lines, tap on the", x, y, paint);
			canvas.drawText("grid to create a unit", x, y + 17, paint);
		}

		@Override
		public void onUpdate(float seconds) {
			if (songTime > 1) {
				songTime = MIN_SONG_TIME;
			}
		}

		@Override
		public void onTouchDown(float x, float y) {
			x -= GAME_X;
			y -= GAME_Y;
			if (x >= 0 && beat.onTouchDown((int) (songTime * 1000))) {
				localPlayer.spawnUnit(x, y);
				transition(new UnitsUpAndDownStep());
			}
		}
	}

	private class UnitsUpAndDownStep extends Step {
		private static final float x = GAME_X + GAME_WIDTH / 2;
		private static final float y = GAME_Y + GAME_HEIGHT * 0.33f;
		private boolean wasPaused;
		private float time;

		@Override
		public void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			paint.setColor(Color.WHITE);
			paint.setTextSize(15);
			paint.setTextAlign(Align.CENTER);
			canvas.drawText("Your units travel up and the", x, y - 8, paint);
			canvas.drawText("other player's units travel down", x, y + 8, paint);
		}

		@Override
		public void onUpdate(float seconds) {
			time += seconds;
			if (!wasPaused && isPaused()) {
				addOKButton(x, y);
				wasPaused = true;
			}
		}

		@Override
		public boolean isPaused() {
			return time > 6;
		}

		@Override
		public void onClick(UIButton button) {
			transition(new WavefrontStep());
		}
	}

	private class WavefrontStep extends Step {
		private static final float x = GAME_X + GAME_WIDTH / 2;
		private static final float y = GAME_Y + GAME_HEIGHT * 0.75f;
		private boolean wasPaused;
		private float time;

		@Override
		public void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			paint.setColor(Color.WHITE);
			paint.setTextSize(15);
			paint.setTextAlign(Align.CENTER);
			canvas.drawText("You can only place", x, y - 8, paint);
			canvas.drawText("units in the " + team.getColorName() + " area", x, y + 8, paint);
		}

		@Override
		public void onUpdate(float seconds) {
			time += seconds;
			if (!wasPaused && isPaused()) {
				addOKButton(x, y);
				wasPaused = true;
			}
		}

		@Override
		public boolean isPaused() {
			return time > 6;
		}

		@Override
		public void onClick(UIButton button) {
			transition(new WinConditionStep());
		}
	}

	private class WinConditionStep extends Step {
		private static final float x = GAME_X + GAME_WIDTH / 2;
		private static final float y = GAME_Y + GAME_HEIGHT / 2;
		private boolean wasPaused;
		private float time;

		@Override
		public void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			paint.setColor(Color.WHITE);
			paint.setTextSize(15);
			paint.setTextAlign(Align.CENTER);
			canvas.drawText("You win when your opponent's", x, y - 17, paint);
			canvas.drawText("health reaches 0, or when the song", x, y, paint);
			canvas.drawText("ends and your health is higher", x, y + 17, paint);
		}

		@Override
		public void onUpdate(float seconds) {
			time += seconds;
			if (!wasPaused && isPaused()) {
				addOKButton(x, y + 17);
				wasPaused = true;
			}
		}

		@Override
		public boolean isPaused() {
			return time > 6;
		}

		@Override
		public void onClick(UIButton button) {
			transition(new DrawWallStep());
		}
	}

	private class DrawWallStep extends Step {
		private boolean isTooShort;
		private boolean isDragging;

		public DrawWallStep() {
			beat = Beat.hold(1000);
		}

		@Override
		public void onDraw(Canvas canvas) {
			float x = GAME_X + GAME_WIDTH / 2;
			float y = GAME_Y + GAME_HEIGHT * 0.8f;
			paint.setColor(Color.WHITE);
			paint.setTextSize(15);
			paint.setTextAlign(Align.CENTER);
			if (isTooShort) {
				canvas.drawText("You need to drag your finger", x, y - 17, paint);
				canvas.drawText("away from where you tapped,", x, y, paint);
				canvas.drawText("please try making a longer wall", x, y + 17, paint);
			} else {
				canvas.drawText("When a beat has a tail,", x, y - 17, paint);
				canvas.drawText("you can drag on the", x, y, paint);
				canvas.drawText("grid to create a wall", x, y + 17, paint);
			}
		}

		@Override
		public void onUpdate(float seconds) {
			if (!beat.isTapped() && songTime > 2) {
				songTime = MIN_SONG_TIME;
			}
		}

		@Override
		public void onTouchDown(float x, float y) {
			if (x >= GAME_X && beat.onTouchDown((int) (songTime * 1000))) {
				isDragging = true;
				wallStartX = wallEndX = transformToGameSpaceX(x);
				wallStartY = wallEndY = transformToGameSpaceY(y);
			}
		}

		@Override
		public void onTouchMove(float x, float y) {
			if (isDragging) {
				wallEndX = transformToGameSpaceX(x);
				wallEndY = transformToGameSpaceY(y);
			}
		}

		@Override
		public void onTouchUp(float x, float y) {
			if (isDragging) {
				wallEndX = transformToGameSpaceX(x);
				wallEndY = transformToGameSpaceY(y);
				BBTHSimulation.generateParticlesForWall(new Wall(wallStartX, wallStartY, wallEndX, wallEndY), team);
				isTooShort = MathUtils.getDist(wallStartX, wallStartY, wallEndX, wallEndY) < 20;
				isDragging = false;
				if (isTooShort) {
					beat = Beat.hold(1000);
				} else {
					transition(new WallBlockStep());
				}
			}
		}
	}

	private class WallBlockStep extends Step {
		private static final float x = GAME_X + GAME_WIDTH / 2;
		private static final float y = GAME_Y + GAME_HEIGHT / 2;
		private boolean wasPaused;
		private float time;

		@Override
		public void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			paint.setColor(Color.WHITE);
			paint.setTextSize(15);
			paint.setTextAlign(Align.CENTER);
			canvas.drawText("Units take longer", x, y - 8, paint);
			canvas.drawText("to go around walls", x, y + 8, paint);
		}

		@Override
		public void onUpdate(float seconds) {
			time += seconds;
			if (!wasPaused && isPaused()) {
				addOKButton(x, y);
				wasPaused = true;
			}
		}

		@Override
		public boolean isPaused() {
			return time > 6;
		}

		@Override
		public void onClick(UIButton button) {
			transition(new FinishedStep());
		}
	}
	
	private class FinishedStep extends Step {
	}

	private Step step;
	private UIButton skipButton;
	private Team team;
	private Player serverPlayer;
	private Player clientPlayer;
	private Player localPlayer;
	private Player remotePlayer;
	private AIController aiController;
	private Beat beat = Beat.tap(0);
	private float songTime = MIN_SONG_TIME;
	private GridAcceleration accel;
	private FastGraphGenerator gen;
	private Pathfinder pathfinder;
	private FastLineOfSightTester tester;
	private float wallStartX;
	private float wallStartY;
	private float wallEndX;
	private float wallEndY;

	public InteractiveTutorial(Team localTeam) {
		skipButton = new UIButton("Skip Tutorial");
		skipButton.setAnchor(Anchor.TOP_RIGHT);
		skipButton.setSize(100, 30);
		skipButton.setPosition(BBTHGame.WIDTH - 20, Base.BASE_HEIGHT + 20);
		skipButton.setButtonDelegate(this);
		skipButton.expandForHitTesting(20, 20);
		addSubview(skipButton);

		team = localTeam;
		aiController = new AIController();
		serverPlayer = new Player(Team.SERVER, aiController, this, team == Team.SERVER);
		clientPlayer = new Player(Team.CLIENT, aiController, this, team == Team.CLIENT);
		localPlayer = (team == Team.SERVER) ? serverPlayer : clientPlayer;
		remotePlayer = (team == Team.SERVER) ? clientPlayer : serverPlayer;

		gen = new FastGraphGenerator(15.0f, GAME_WIDTH, GAME_HEIGHT);
		pathfinder = new Pathfinder(gen.graph);
		accel = new GridAcceleration(GAME_WIDTH, GAME_HEIGHT, GAME_WIDTH / 10);
		tester = new FastLineOfSightTester(15, accel);
		aiController.setPathfinder(pathfinder, gen.graph, tester, accel);

		transition(new PlaceUnitStep());
	}

	@Override
	public boolean isFinished() {
		return step instanceof FinishedStep;
	}

	@Override
	public void onClick(UIButton button) {
		transition(new FinishedStep());
	}

	protected void transition(Step newStep) {
		if (step != null) {
			removeSubview(step);
		}
		step = newStep;
		if (step != null) {
			addSubview(step);
		}
	}

	protected static void drawArrow(Canvas canvas, float ax, float ay, float bx, float by, float r) {
		final float s = 2.5f;
		float dx = bx - ax;
		float dy = by - ay;
		float d = r / FloatMath.sqrt(dx * dx + dy * dy);
		dx *= d;
		dy *= d;
		path.reset();
		path.moveTo(ax - dy, ay + dx);
		path.lineTo(bx - dy - s * dx, by + dx - s * dy);
		path.lineTo(bx - s * dy - s * dx, by + s * dx - s * dy);
		path.lineTo(bx - dy - dx, by + dx - dy);
		path.lineTo(bx, by);
		path.lineTo(bx + dy - dx, by - dx - dy);
		path.lineTo(bx + s * dy - s * dx, by - s * dx - s * dy);
		path.lineTo(bx + dy - s * dx, by - dx - s * dy);
		path.lineTo(ax + dy, ay - dx);
		canvas.drawPath(path, paint);
	}

	protected static void drawDashedLine(Canvas canvas, float ax, float ay, float bx, float by, float r, float percent) {
		float dx = bx - ax;
		float dy = by - ay;
		float d = FloatMath.sqrt(dx * dx + dy * dy);
		dx /= d;
		dy /= d;
		for (float t = percent * r * 2 - r; t < d; t += 2 * r) {
			float t1 = Math.max(0, t);
			float t2 = Math.min(d, t + r);
			canvas.drawLine(ax + dx * t1, ay + dy * t1, ax + dx * t2, ay + dy * t2, paint);
		}
	}

	protected void transformToGameSpace(Canvas canvas, Team team) {
		canvas.translate(GAME_X, GAME_Y);
		if (team == Team.SERVER) {
			canvas.translate(0, GAME_HEIGHT / 2);
			canvas.scale(1.f, -1.f);
			canvas.translate(0, -GAME_HEIGHT / 2);
		}
	}

	private void drawWavefronts(Canvas canvas) {
		Unit serverAdvUnit = serverPlayer.getMostAdvancedUnit();
		Unit clientAdvUnit = clientPlayer.getMostAdvancedUnit();
		float serverWavefrontY = serverAdvUnit != null ? serverAdvUnit.getY() + 10 : 0;
		float clientWavefrontY = clientAdvUnit != null ? clientAdvUnit.getY() - 10 : GAME_HEIGHT;
		paint.setStyle(Style.FILL);

		// server wavefront
		paint.setColor(Team.SERVER.getWavefrontColor());
		canvas.drawRect(0, 0, GAME_WIDTH, Math.min(clientWavefrontY, serverWavefrontY), paint);

		// client wavefront
		paint.setColor(Team.CLIENT.getWavefrontColor());
		canvas.drawRect(0, Math.max(clientWavefrontY, serverWavefrontY), GAME_WIDTH, GAME_HEIGHT, paint);

		// overlapped wavefronts
		if (serverWavefrontY > clientWavefrontY) {
			paint.setColor(Color.rgb(63, 0, 63));
			canvas.drawRect(0, clientWavefrontY, GAME_WIDTH, serverWavefrontY, paint);
		}

		// server wavefront line
		paint.setColor(Team.SERVER.getUnitColor());
		canvas.drawLine(0, serverWavefrontY, GAME_WIDTH, serverWavefrontY, paint);

		// client wavefront line
		paint.setColor(Team.CLIENT.getUnitColor());
		canvas.drawLine(0, clientWavefrontY, GAME_WIDTH, clientWavefrontY, paint);
	}

	private void drawGrid(Canvas canvas) {
		paint.setARGB(63, 255, 255, 255);
		for (float x = 0; x < GAME_WIDTH; x += 55) {
			canvas.drawLine(x, 0, x, GAME_HEIGHT, paint);
		}
		for (float y = 0; y < GAME_HEIGHT; y += 55) {
			canvas.drawLine(0, y, GAME_WIDTH, y, paint);
		}
	}

	private void drawBeatTrack(Canvas canvas) {
		paint.setStrokeWidth(2);
		paint.setARGB(127, 255, 255, 255);
		canvas.drawLine(BEAT_LINE_X, 0, BEAT_LINE_X, BEAT_LINE_Y - BEAT_CIRCLE_RADIUS, paint);
		canvas.drawLine(BEAT_LINE_X, BEAT_LINE_Y + BEAT_CIRCLE_RADIUS, BEAT_LINE_X, BBTHGame.HEIGHT, paint);

		beat.draw((int) (songTime * 1000), BEAT_LINE_X, BEAT_LINE_Y, canvas, paint);

		paint.setColor(Color.WHITE);
		canvas.drawLine(BEAT_LINE_X - BEAT_TRACK_WIDTH / 2, BEAT_LINE_Y - BEAT_CIRCLE_RADIUS, BEAT_LINE_X + BEAT_TRACK_WIDTH / 2, BEAT_LINE_Y
				- BEAT_CIRCLE_RADIUS, paint);
		canvas.drawLine(BEAT_LINE_X - BEAT_TRACK_WIDTH / 2, BEAT_LINE_Y + BEAT_CIRCLE_RADIUS, BEAT_LINE_X + BEAT_TRACK_WIDTH / 2, BEAT_LINE_Y
				+ BEAT_CIRCLE_RADIUS, paint);
		paint.setStrokeWidth(1);
	}

	@Override
	public void onUpdate(float seconds) {
		super.onUpdate(seconds);
		if (!step.isPaused()) {
			accel.clearUnits();
			accel.insertUnits(serverPlayer.units);
			accel.insertUnits(clientPlayer.units);
			aiController.update();
			serverPlayer.update(seconds);
			clientPlayer.update(seconds);
			serverPlayer.base.damageUnits(accel);
			clientPlayer.base.damageUnits(accel);
			songTime += seconds;
		}
	}

	@Override
	public void onDraw(Canvas canvas) {
		canvas.save();
		transformToGameSpace(canvas, team);
		drawWavefronts(canvas);
		drawGrid(canvas);
		localPlayer.draw(canvas, team == Team.SERVER);
		remotePlayer.draw(canvas, team == Team.SERVER);
		BBTHSimulation.PARTICLES.draw(canvas, BBTHSimulation.PARTICLE_PAINT);
		paint.setColor(team.getWallColor());
		canvas.drawLine(wallStartX, wallStartY, wallEndX, wallEndY, paint);
		canvas.restore();
		drawBeatTrack(canvas);
		super.onDraw(canvas);
	}

	@Override
	public boolean supressDrawing() {
		return true;
	}

	@Override
	public void notifyUnitDead(Unit unit) {
		serverPlayer.units.remove(unit);
		clientPlayer.units.remove(unit);
		aiController.removeEntity(unit);
	}

	@Override
	public Bag<Unit> getUnitsInCircle(float x, float y, float r) {
		return emptyUnitBag;
	}

	@Override
	public Bag<Unit> getUnitsIntersectingLine(float x, float y, float x2, float y2) {
		return emptyUnitBag;
	}

	@Override
	public void removeWall(Wall wall) {
	}

	private float transformToGameSpaceX(float x) {
		return x - GAME_X;
	}

	private float transformToGameSpaceY(float y) {
		y -= GAME_Y;
		if (team == Team.SERVER) {
			y = GAME_HEIGHT - y;
		}
		return y;
	}
}
