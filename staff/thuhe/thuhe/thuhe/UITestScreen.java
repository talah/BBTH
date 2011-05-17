package thuhe.thuhe.thuhe;

import bbth.engine.ui.Anchor;
import bbth.engine.ui.UILabel;
import bbth.engine.ui.UIProgressBar;
import bbth.engine.ui.UISlider;
import bbth.engine.ui.UISwitch;
import bbth.engine.ui.UIView;
import bbth.game.BBTHGame;

public class UITestScreen extends UIView {
	private UISlider slider;
	private UILabel label;
	private UISwitch uiSwitch;
	private UIProgressBar progressBar;
	
	public UITestScreen() {
		this.setAnchor(Anchor.TOP_LEFT);
		this.setSize(BBTHGame.WIDTH, BBTHGame.HEIGHT);
		this.setPosition(0, 0);
		
		progressBar = new UIProgressBar();
		progressBar.setAnchor(Anchor.CENTER_CENTER);
		progressBar.setPosition(BBTHGame.WIDTH / 2, BBTHGame.HEIGHT / 2 - 160);
		progressBar.setSize(150, 20);
		this.addSubview(progressBar);
		
		slider = new UISlider(1.f, 5.f, 2.f);
		slider.setAnchor(Anchor.CENTER_CENTER);
		slider.setPosition(BBTHGame.WIDTH / 2, BBTHGame.HEIGHT / 2);
		slider.setSize(150, 20);
		this.addSubview(slider);
		
		label = new UILabel("", slider);
		label.setAnchor(Anchor.CENTER_CENTER);
		label.setPosition(BBTHGame.WIDTH / 2, BBTHGame.HEIGHT / 2 + 80);
		label.setSize(100, 20);
		label.setTextSize(20.f);
		this.addSubview(label);
		
		uiSwitch = new UISwitch();
		uiSwitch.setAnchor(Anchor.CENTER_CENTER);
		uiSwitch.setPosition(BBTHGame.WIDTH / 2, BBTHGame.HEIGHT / 2 - 80);
		uiSwitch.setSize(100, 30);
		this.addSubview(uiSwitch);
	}
	
	@Override
	public void onUpdate(float seconds) {
		super.onUpdate(seconds);
		
//		label.setText("Value: " + slider.getValue());
//		label.setText("Value: " + (uiSwitch.isOn() ? "ON" : "OFF"));
		label.setText("Value: " + progressBar.getProgress());
		
		progressBar.setProgress(progressBar.getProgress() + 0.01f);
	}
}
