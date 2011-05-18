package bbth.game;

import android.content.SharedPreferences;
import android.graphics.Color;
import bbth.engine.ui.Anchor;
import bbth.engine.ui.UILabel;
import bbth.engine.ui.UINavigationController;
import bbth.engine.ui.UISlider;
import bbth.engine.ui.UISwitch;
import bbth.engine.ui.UIView;

public class SettingsScreen extends UIView {
	
	private UISwitch tutorialSwitch, titleScreenMusicSwitch;
	private UISlider aiDifficulty;
	private UILabel tutorial, ai, title, titleScreenMusic;
	private UINavigationController controller;
	private boolean showTutorial, playTitleScreenMusic;
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
		
		tutorial = new UILabel("Show Tutorial");
		tutorial.setAnchor(Anchor.CENTER_LEFT);
		tutorial.setTextSize(16);
		tutorial.sizeToFit();
		tutorial.setPosition(10, BBTHGame.HEIGHT / 2 - 65);
		
		tutorialSwitch = new UISwitch();
		tutorialSwitch.setAnchor(Anchor.CENTER_RIGHT);
		tutorialSwitch.setSize(100, 30);
		tutorialSwitch.setPosition(BBTHGame.WIDTH - 25, BBTHGame.HEIGHT / 2 - 65);
		tutorialSwitch.setOn(BBTHGame.SHOW_TUTORIAL);
		
		ai = new UILabel("AI Difficulty");
		ai.setAnchor(Anchor.CENTER_LEFT);
		ai.setTextSize(16);
		ai.sizeToFit();
		ai.setPosition(10, BBTHGame.HEIGHT / 2 + 65);
		
		aiDifficulty = new UISlider(0.5f, 1.f, 0.75f);
		aiDifficulty.setAnchor(Anchor.CENTER_RIGHT);
		aiDifficulty.setSize(100, 24);
		aiDifficulty.setPosition(BBTHGame.WIDTH - 25, BBTHGame.HEIGHT / 2 + 65);
		aiDifficulty.setValue(BBTHGame.AI_DIFFICULTY);
		
		titleScreenMusic = new UILabel("Title Screen Music");
		titleScreenMusic.setAnchor(Anchor.CENTER_LEFT);
		titleScreenMusic.setTextSize(16);
		titleScreenMusic.sizeToFit();
		titleScreenMusic.setPosition(10, BBTHGame.HEIGHT / 2);
		
		titleScreenMusicSwitch = new UISwitch();
		titleScreenMusicSwitch.setAnchor(Anchor.CENTER_RIGHT);
		titleScreenMusicSwitch.setSize(100, 30);
		titleScreenMusicSwitch.setPosition(BBTHGame.WIDTH - 25, BBTHGame.HEIGHT / 2);
		titleScreenMusicSwitch.setOn(BBTHGame.TITLE_SCREEN_MUSIC);

		addSubview(title);
		addSubview(ai);
		addSubview(aiDifficulty);
		addSubview(tutorial);
		addSubview(tutorialSwitch);
		addSubview(titleScreenMusic);
		addSubview(titleScreenMusicSwitch);
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
		    _editor.commit();
		}
		
		if(playTitleScreenMusic != titleScreenMusicSwitch.isOn())
		{
			playTitleScreenMusic = titleScreenMusicSwitch.isOn();
		    _editor.putBoolean("titleScreenMusic", playTitleScreenMusic);
		    BBTHGame.TITLE_SCREEN_MUSIC = playTitleScreenMusic;
		    _editor.commit();
		}
	}
	
	

}
