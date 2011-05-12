package bbth.game;

import bbth.engine.ui.Anchor;
import bbth.engine.ui.UILabel;
import bbth.engine.ui.UIView;

// TODO Pretty graphics
public class TitleScreen extends UIView {
	private UILabel titleBar;  
	
	public TitleScreen() {
		titleBar = new UILabel("Beat Back the Horde!", this);
		titleBar.setAnchor(Anchor.CENTER_CENTER);
		titleBar.setTextSize(20.f);
		titleBar.setPosition(160, 90);
		this.addSubview(titleBar);
	}
}
