package bbth.game;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.res.Resources;
import bbth.engine.achievements.AchievementInfo;
import bbth.engine.achievements.Achievements;
import bbth.engine.core.Game;
import bbth.engine.core.GameActivity;
import bbth.engine.ui.DefaultTransition;
import bbth.engine.ui.Transition;
import bbth.engine.ui.UINavigationController;
import bbth.engine.ui.UISwipeTransition;
import bbth.engine.ui.UISwipeTransition.Direction;
import bbth.engine.util.Envelope;
import bbth.engine.util.Envelope.OutOfBoundsHandler;

public class BBTHGame extends Game {
	// This is the viewport width and height
	public static final float WIDTH = 320;
	public static final float HEIGHT = 530;
	public static final boolean DEBUG = false;
	private UINavigationController navController;

	// get achievement descriptions from XML descriptions
	private Map<String, AchievementInfo> initializeAchievements(Activity activity) { 
		Achievements.INSTANCE.initialize(activity);

		Map<String, Boolean> savedAchievements = Achievements.INSTANCE.getAll();
		Map<String, AchievementInfo> achievements = new HashMap<String, AchievementInfo>();

		Resources resources = GameActivity.instance.getResources();
		String []names = resources.getStringArray(R.array.achievementNames);
		String []descriptions = resources.getStringArray(R.array.achievementDescriptions);
		String []images = resources.getStringArray(R.array.achievementImages);
		assert names.length == descriptions.length;
		assert names.length == images.length;
		
		String packageName = activity.getPackageName();
		for (int i = 0; i < names.length; ++i) {
			int imageId = resources.getIdentifier(images[i], "drawable", packageName);
			achievements.put(names[i], new AchievementInfo(descriptions[i], imageId));
			if (!savedAchievements.containsKey(names[i])) {
				Achievements.INSTANCE.lock(names[i]);
			}
		}
		
		return achievements;
	}
	
	public BBTHGame(Activity activity) {
		navController = new UINavigationController();
		currentScreen = navController;
		
		Map<String, AchievementInfo> achievements = initializeAchievements(activity);
		Achievements.INSTANCE.unlock("Test Success");
		Achievements.INSTANCE.unlock("Test2");
		navController.push(new TitleScreen(navController, achievements));
		
//		controller.push(new BBTHAITest(this));
//		controller.push(new MusicTestScreen(activity));
//		controller.push(new NetworkTestScreen());
//		controller.push(new TransitionTest());
//		controller.push(new GameSetupScreen());
//		controller.push(new CombatTest(this));
	}

	@Override
	public float getWidth() {
		return WIDTH;
	}

	@Override
	public float getHeight() {
		return HEIGHT;
	}
	

	@Override
	public void onBackPressed() {
		if (!navController.pop(FROM_LEFT_TRANSITION)) {
			GameActivity.instance.finish();
		}
	}
	
	public static final UISwipeTransition FROM_RIGHT_TRANSITION = new UISwipeTransition(WIDTH, Direction.FROM_RIGHT, 0.5f);
	public static final UISwipeTransition FROM_LEFT_TRANSITION = new UISwipeTransition(WIDTH, Direction.FROM_LEFT, 0.5f);

	public static final Transition MOVE_LEFT_TRANSITION;
	public static final Transition MOVE_RIGHT_TRANSITION;
	public static final Transition FADE_OUT_FADE_IN_TRANSITION;
	static {
		
		
		DefaultTransition transition = new DefaultTransition(.5f);
		{
			Envelope newAlpha = new Envelope(0f, OutOfBoundsHandler.RETURN_FIRST_OR_LAST);
			newAlpha.addLinearSegment(.5f, 255f);
			transition.setNewAlpha(newAlpha);
			
			Envelope oldAlpha = new Envelope(255f, OutOfBoundsHandler.RETURN_FIRST_OR_LAST);
			oldAlpha.addLinearSegment(.5f, 0f);
			transition.setOldAlpha(oldAlpha);
			
			Envelope newX = new Envelope(BBTHGame.WIDTH, OutOfBoundsHandler.RETURN_FIRST_OR_LAST);
			newX.addLinearSegment(.5f, 0f);
			transition.setNewX(newX);
			
			Envelope oldX = new Envelope(0f, OutOfBoundsHandler.RETURN_FIRST_OR_LAST);
			oldX.addLinearSegment(.5f, -BBTHGame.WIDTH);
			transition.setOldX(oldX);
		}
		MOVE_LEFT_TRANSITION = transition;
		
		transition = new DefaultTransition(.5f);
		{
			Envelope newAlpha = new Envelope(0f, OutOfBoundsHandler.RETURN_FIRST_OR_LAST);
			newAlpha.addLinearSegment(.5f, 255f);
			transition.setNewAlpha(newAlpha);
			
			Envelope oldAlpha = new Envelope(255f, OutOfBoundsHandler.RETURN_FIRST_OR_LAST);
			oldAlpha.addLinearSegment(.5f, 0f);
			transition.setOldAlpha(oldAlpha);
			
			Envelope newX = new Envelope(-BBTHGame.WIDTH, OutOfBoundsHandler.RETURN_FIRST_OR_LAST);
			newX.addLinearSegment(.5f, 0f);
			transition.setNewX(newX);
			
			Envelope oldX = new Envelope(0f, OutOfBoundsHandler.RETURN_FIRST_OR_LAST);
			oldX.addLinearSegment(.5f, BBTHGame.WIDTH);
			transition.setOldX(oldX);
		}
		MOVE_RIGHT_TRANSITION = transition;
		
		transition = new DefaultTransition(.5f);
		{
			Envelope newAlpha = new Envelope(0f, OutOfBoundsHandler.RETURN_FIRST_OR_LAST);
			newAlpha.addFlatSegment(.25f);
			newAlpha.addLinearSegment(.25f, 255f);
			transition.setNewAlpha(newAlpha);
			
			Envelope oldAlpha = new Envelope(255f, OutOfBoundsHandler.RETURN_FIRST_OR_LAST);
			oldAlpha.addLinearSegment(.25f, 0f);
			transition.setOldAlpha(oldAlpha);
		}
		FADE_OUT_FADE_IN_TRANSITION = transition;
		
	}
}
