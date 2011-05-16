package bbth.engine.achievements;

/**
 * A list of achievements with locked / unlocked status and descriptions
 * TODO: Work into our UI system
 * @author Justin
 *
 */
public class AchievementScreen { // extends AbstractScreen {
/*
	private final static int X_OFFSET = 10;
	private final static int ACHIEVEMENT_HEIGHT = 100;
	private final static int SCREEN_WIDTH = 800;
	private final static int SCREEN_HEIGHT = 600;
	private final static String LOCKED_TEXT = "LOCKED";

	private Screen _prevScreen;
	private float _yOffset;
	private Map<String, Boolean> _achievements;
	private Map<String, String> _descriptions;
	private Paint _paint;
	private float _prevYTouch;
	private float _minYOffset, _maxYOffset;

	// descriptions maps achievement names to full descriptions for unlocked achievements
	public AchievementScreen(Screen prevScreen, Context context, Map<String, String> descriptions) {
		Achievements.INSTANCE.initialize(context, SCREEN_WIDTH, SCREEN_HEIGHT);
		_prevScreen = prevScreen;
		_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		_paint.setTextSize(36);
		_paint.setColor(Color.WHITE);
		_achievements = Achievements.INSTANCE.getAll();
		_maxYOffset = ACHIEVEMENT_HEIGHT * 0.5f;
		_minYOffset = _maxYOffset - ACHIEVEMENT_HEIGHT * (_achievements.size() - Math.min(_achievements.size(), SCREEN_HEIGHT / ACHIEVEMENT_HEIGHT));
		_yOffset = _maxYOffset;
		assert(descriptions != null);
		_descriptions = descriptions;
	}
	
	@Override
	public void tick(float seconds) {
		// nothing to do here!
	}

	@Override
	public void draw(Canvas canvas) {
		float y = _yOffset;
		for (Map.Entry<String, Boolean> entry : _achievements.entrySet()) {
			
			// draw achievement name
			canvas.drawText(entry.getKey(), X_OFFSET, y, _paint);
			y += ACHIEVEMENT_HEIGHT * 0.4f;
			_paint.setTextSize(30);
			
			// draw description / lock status
			if (Boolean.TRUE.equals(entry.getValue())) {
				_paint.setColor(Color.GRAY);
				canvas.drawText(_descriptions.get(entry.getKey()), X_OFFSET, y, _paint);
			} else {
				_paint.setColor(Color.RED);
				canvas.drawText(LOCKED_TEXT, X_OFFSET, y, _paint);
			}
			
			// draw divider line
			_paint.setColor(Color.WHITE);
			y += ACHIEVEMENT_HEIGHT * 0.2f;
			canvas.drawLine(0, y, SCREEN_WIDTH, y, _paint);
			y += ACHIEVEMENT_HEIGHT * 0.4f;
		}
	}
	
	@Override
	public void onDownAction(float x, float y) {
		_prevYTouch = y;
	}
	
	@Override
	public void onMoveAction(float x, float y) {
		_yOffset += y - _prevYTouch;
		if (_yOffset < _minYOffset) {
			_yOffset = _minYOffset;
		} else if (_yOffset > _maxYOffset) {
			_yOffset = _maxYOffset;
		}
		
		_prevYTouch = y;
	}

	@Override
	public int getWidth() {
		return SCREEN_WIDTH;
	}

	@Override
	public int getHeight() {
		return SCREEN_HEIGHT;
	}

	@Override
	public Screen getNextScreen() {
		return this;
	}
	
	@Override
	public Screen getPreviousScreen() {
		return _prevScreen;
	}
*/
}
