package bbth.engine.achievements;

import java.util.Map;

import android.graphics.Paint;
import bbth.engine.ui.UIButton;
import bbth.engine.ui.UIButtonDelegate;
import bbth.engine.ui.UILabel;
import bbth.engine.ui.UINavigationController;
import bbth.engine.ui.UIScrollView;
import bbth.engine.ui.UIView;

/**
 * A list of achievements with locked / unlocked status and descriptions
 * @author Justin
 *
 */
public class AchievementScreen extends UIScrollView implements UIButtonDelegate {

	private final static int X_OFFSET = 10;
	private final static int ACHIEVEMENT_HEIGHT = 100;
	private final static int SCREEN_WIDTH = 800;
	private final static int SCREEN_HEIGHT = 600;
	private final static String LOCKED_TEXT = "LOCKED";

	private float _yOffset;
	private Map<String, Boolean> _achievements;
	private Map<String, String> _descriptions;
	private Paint _paint;
	private UIView _prevScreen;
	private AchievementView[] _views;
	private UIButton _backButton;

	// descriptions maps achievement names to full descriptions for unlocked achievements
	public AchievementScreen(UINavigationController navController, Map<String, String> descriptions) {
		super(null);
		_achievements = Achievements.INSTANCE.getAll();
		//_labels = new UIView[_achievements.size()];
		//for (int i = 0; i < _labels.length; ++i) {
			//_labels[i] = new UILabel()
		//}
		//_maxYOffset = ACHIEVEMENT_HEIGHT * 0.5f;
		//_minYOffset = _maxYOffset - ACHIEVEMENT_HEIGHT * (_achievements.size() - Math.min(_achievements.size(), SCREEN_HEIGHT / ACHIEVEMENT_HEIGHT));
		//_yOffset = _maxYOffset;
		assert(descriptions != null);
		_descriptions = descriptions;
	}

	@Override
	public void onClick(UIButton button) {
		if (button == _backButton) {
			nextScreen = _prevScreen;
		}
	}
/*
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
*/

	@Override
	public void onTouchUp(UIView sender) {
		// do nothing
	}

	@Override
	public void onTouchDown(UIView sender) {
		// do nothing
	}

	@Override
	public void onTouchMove(UIView sender) {
		// do nothing
	}
}
