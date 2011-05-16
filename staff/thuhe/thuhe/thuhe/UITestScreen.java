package thuhe.thuhe.thuhe;

import bbth.engine.ui.Anchor;
import bbth.engine.ui.UILabel;
import bbth.engine.ui.UISlider;
import bbth.engine.ui.UIView;
import bbth.game.BBTHGame;

public class UITestScreen extends UIView {
	private UISlider slider;
	private UILabel label;
	
	public UITestScreen() {
		this.setAnchor(Anchor.TOP_LEFT);
		this.setSize(BBTHGame.WIDTH, BBTHGame.HEIGHT);
		this.setPosition(0, 0);
		
		slider = new UISlider(1.f, 5.f, 2.f);
		slider.setAnchor(Anchor.CENTER_CENTER);
		slider.setPosition(BBTHGame.WIDTH / 2, BBTHGame.HEIGHT / 2);
		slider.setSize(BBTHGame.HEIGHT / 3, BBTHGame.WIDTH / 3);
		this.addSubview(slider);
		
		label = new UILabel("", slider);
		label.setAnchor(Anchor.CENTER_CENTER);
		label.setPosition(BBTHGame.WIDTH / 2, BBTHGame.HEIGHT / 2 + 80);
		label.setSize(100, 20);
		label.setTextSize(20.f);
		this.addSubview(label);
	}
	
	@Override
	public void onUpdate(float seconds) {
		super.onUpdate(seconds);
		
		label.setText("Value: " + slider.getValue());
	}
}
