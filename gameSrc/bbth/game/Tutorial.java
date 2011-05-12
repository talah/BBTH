package bbth.game;

import bbth.engine.ui.UIView;

public class Tutorial extends UIView {

	private long start_time;
	
	public Tutorial() {
		start_time = System.currentTimeMillis();
	}
	
	public boolean isFinished() {
		if (System.currentTimeMillis() > start_time + 5000) {
			return true;
		}
		return false;
	}

	public void update() {
		// TODO Auto-generated method stub
		
	}

	public void displayWait() {
		// TODO Auto-generated method stub
		
	}

	public void hide() {
		// TODO Auto-generated method stub
		
	}
	
}
