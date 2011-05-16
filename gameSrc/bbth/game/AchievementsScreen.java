package bbth.game;

import java.util.Map;

import android.graphics.Paint.Align;
import bbth.engine.achievements.AchievementInfo;
import bbth.engine.achievements.AchievementView;
import bbth.engine.achievements.Achievements;
import bbth.engine.ui.Anchor;
import bbth.engine.ui.UIButton;
import bbth.engine.ui.UIButtonDelegate;
import bbth.engine.ui.UILabel;
import bbth.engine.ui.UINavigationController;
import bbth.engine.ui.UIScrollView;

/**
 * A list of achievements with locked / unlocked status and descriptions
 * @author Justin
 *
 */
public class AchievementsScreen extends UIScrollView implements UIButtonDelegate {

	private final static int ACHIEVEMENT_HEIGHT = 65;
	private final static String LOCKED_TEXT = "LOCKED";

	private UINavigationController _navController;
	private UIButton _backButton;

	// descriptions maps achievement names to full descriptions for unlocked achievements
	public AchievementsScreen(UINavigationController navController, Map<String, AchievementInfo> achievementInfo) {
		super(null);
		assert(achievementInfo != null);

		setScrollsHorizontal(false);
		
		_navController = navController;
		
		Map<String, Boolean> achievements = Achievements.INSTANCE.getAll();
		setSize(BBTHGame.WIDTH, BBTHGame.HEIGHT);
		
		UILabel titleLabel = new UILabel("Achievements", null);
		titleLabel.setTextSize(30.f);
		titleLabel.setAnchor(Anchor.CENTER_CENTER);
		titleLabel.setPosition(BBTHGame.WIDTH / 2, 80);
		titleLabel.setTextAlign(Align.CENTER);
		addSubview(titleLabel);
		
		float y = BBTHGame.HEIGHT / 2 - 65;
		for (Map.Entry<String, Boolean> entry : achievements.entrySet()) {
			String name = entry.getKey();
			String description;
			int imageId;
			if (Boolean.TRUE.equals(entry.getValue())) {
				description = achievementInfo.get(name).description;
				imageId = achievementInfo.get(name).imageId;
			} else {
				description = LOCKED_TEXT;
				imageId = R.drawable.padlock;
			}
			AchievementView view = new AchievementView(name, description, imageId);
			view.setAnchor(Anchor.CENTER_LEFT);
			view.setPosition(0, y);
			view.setSize(BBTHGame.WIDTH, ACHIEVEMENT_HEIGHT);
			addSubview(view);
			y += ACHIEVEMENT_HEIGHT;
		}
		
		_backButton = new UIButton("Back", this);
		_backButton.setSize(BBTHGame.WIDTH * 0.75f, 45);
		_backButton.setAnchor(Anchor.TOP_CENTER);
		_backButton.setPosition(BBTHGame.WIDTH * 0.5f, y + 10);
		_backButton.setButtonDelegate(this);
		addSubview(_backButton);
	}

	@Override
	public void onClick(UIButton button) {
		if (button == _backButton) {
			_navController.pop(BBTHGame.FROM_LEFT_TRANSITION);
		}
	}

}
