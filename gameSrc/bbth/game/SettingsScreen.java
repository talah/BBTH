package bbth.game;

import thuhe.thuhe.thuhe.UITestScreen;
import android.content.SharedPreferences;
import android.graphics.Color;
import bbth.engine.ui.Anchor;
import bbth.engine.ui.UILabel;
import bbth.engine.ui.UINavigationController;
import bbth.engine.ui.UISlider;
import bbth.engine.ui.UISwitch;
import bbth.engine.ui.UIView;

public class SettingsScreen extends UIView {
	
	private UISwitch tutorialSwitch;
	private UISlider aiDifficulty;
	private UILabel tutorial, ai, title;
	private UINavigationController controller;
	private boolean showTutorial;
	private float ai_level;
	private SharedPreferences _settings;
	private SharedPreferences.Editor _editor;
	
	public SettingsScreen(UINavigationController controller) {
		this.controller = controller;
		_settings = BBTHActivity.instance.getSharedPreferences("game_settings", 0);
		_editor = _settings.edit();
		setSize(BBTHGame.WIDTH, BBTHGame.HEIGHT);
		
		title = new UILabel("Settings");
		title.setAnchor(Anchor.CENTER_CENTER);
		title.setTextSize(30);
		title.sizeToFit();
		title.setPosition(BBTHGame.WIDTH / 2, 80);
		
		tutorial = new UILabel("Show tutorial");
		tutorial.setAnchor(Anchor.CENTER_LEFT);
		tutorial.setTextSize(22);
		tutorial.sizeToFit();
		tutorial.setPosition(15, BBTHGame.HEIGHT / 2 - 65);
		
		tutorialSwitch = new UISwitch();
		tutorialSwitch.setAnchor(Anchor.CENTER_RIGHT);
		tutorialSwitch.setSize(100, 30);
		tutorialSwitch.setOnBackgroundColor(UITestScreen.AWESOME_GREEN);
		tutorialSwitch.setOnTextColor(Color.BLACK);
		tutorialSwitch.setPosition(BBTHGame.WIDTH - 25, BBTHGame.HEIGHT / 2 - 65);
		tutorialSwitch.setOn(BBTHGame.SHOW_TUTORIAL);
		
		ai = new UILabel("AI Difficulty");
		ai.setAnchor(Anchor.CENTER_LEFT);
		ai.setTextSize(22);
		ai.sizeToFit();
		ai.setPosition(15, BBTHGame.HEIGHT / 2);
		
		aiDifficulty = new UISlider(0.5f, 1.f, 0.75f);
		aiDifficulty.setAnchor(Anchor.CENTER_RIGHT);
		aiDifficulty.setSize(100, 24);
		aiDifficulty.setFillColor(UITestScreen.AWESOME_GREEN);
		aiDifficulty.setPosition(BBTHGame.WIDTH - 25, BBTHGame.HEIGHT / 2);
		aiDifficulty.setValue(BBTHGame.AI_DIFFICULTY);

		addSubview(title);
		addSubview(ai);
		addSubview(aiDifficulty);
		addSubview(tutorial);
		addSubview(tutorialSwitch);
	}
	
	@Override
	public void onUpdate(float seconds) {
		super.onUpdate(seconds);
		
		if(showTutorial != tutorialSwitch.isOn())
		{
			showTutorial = tutorialSwitch.isOn();
		    _editor.putBoolean("showTutorial", showTutorial);
		    BBTHGame.SHOW_TUTORIAL = showTutorial;
		    _editor.commit();
		}
		
		if(ai_level != aiDifficulty.getValue())
		{
			ai_level = aiDifficulty.getValue();
		    _editor.putFloat("aiDifficulty", ai_level);
		    BBTHGame.AI_DIFFICULTY = ai_level;
		    System.out.println(ai_level);
		    _editor.commit();
		}
	}
	
	

}
