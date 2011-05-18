package bbth.game;

import android.app.Activity;
import android.graphics.Color;
import bbth.engine.achievements.Achievements;
import bbth.engine.core.Game;
import bbth.engine.core.GameActivity;
import bbth.engine.ui.UINavigationController;
import bbth.engine.ui.UISwipeTransition;
import bbth.engine.ui.UISwipeTransition.Direction;
import bbth.game.achievements.BBTHAchievementManager;

public class BBTHGame extends Game {
	// This is the viewport width and height
	public static final float WIDTH = 320;
	public static final float HEIGHT = 530;
	public static final boolean DEBUG = true;
	public static boolean SHOW_TUTORIAL = true;
	public static boolean TITLE_SCREEN_MUSIC = true;
	public static float AI_DIFFICULTY = 0.75f;
	public static final int AWESOME_GREEN = Color.rgb(159, 228, 74);
	private UINavigationController navController;
	
	public BBTHGame(Activity activity) {
		navController = new UINavigationController();
		currentScreen = navController;
		
		SHOW_TUTORIAL = activity.getSharedPreferences("game_settings", 0).getBoolean("showTutorial", true);
		TITLE_SCREEN_MUSIC = activity.getSharedPreferences("game_settings", 0).getBoolean("titleScreenMusic", true);
		AI_DIFFICULTY = activity.getSharedPreferences("game_settings", 0).getFloat("aiDifficulty", 0.75f);
		
		
		Achievements.INSTANCE.initialize(activity);
		BBTHAchievementManager.INSTANCE.initialize();
		navController.push(new TitleScreen(navController));
		
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
	
	@Override
	public void onStop() {
		Achievements.INSTANCE.commit();
		navController.onStop();
	}

	public static final UISwipeTransition FROM_RIGHT_TRANSITION = new UISwipeTransition(WIDTH, Direction.FROM_RIGHT, 0.5f);
	public static final UISwipeTransition FROM_LEFT_TRANSITION = new UISwipeTransition(WIDTH, Direction.FROM_LEFT, 0.5f);
}
