package bbth.engine.achievements;

import java.util.Map;

import bbth.engine.ui.UIButton;
import bbth.engine.ui.UIButtonDelegate;
import bbth.engine.ui.UINavigationController;
import bbth.engine.ui.UIScrollView;

/**
 * A list of achievements with locked / unlocked status and descriptions
 * @author Justin
 *
 */
public class AchievementScreen extends UIScrollView implements UIButtonDelegate {

	private final static int ACHIEVEMENT_HEIGHT = 100;
	private final static int WIDTH = 100;
	private final static String LOCKED_TEXT = "LOCKED";

	private UINavigationController _navController;
	private UIButton _backButton;

	// descriptions maps achievement names to full descriptions for unlocked achievements
	public AchievementScreen(UINavigationController navController, Map<String, String> descriptions) {
		super(null);
		assert(descriptions != null);

		_navController = navController;
		
		Map<String, Boolean> achievements = Achievements.INSTANCE.getAll();
		setSize(WIDTH, ACHIEVEMENT_HEIGHT * achievements.size());
		
		float y = 0;
		for (Map.Entry<String, Boolean> entry : achievements.entrySet()) {
			String name = entry.getKey();
			String description;
			if (Boolean.TRUE.equals(entry.getValue())) {
				description = LOCKED_TEXT;
			} else {
				description = descriptions.get(name);
			}
			AchievementView view = new AchievementView(name, description);
			view.setPosition(0, y);
			view.setSize(WIDTH, ACHIEVEMENT_HEIGHT);
			addSubview(view);
			y += ACHIEVEMENT_HEIGHT;
		}
	}

	@Override
	public void onClick(UIButton button) {
		if (button == _backButton) {
			_navController.pop();
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
}
