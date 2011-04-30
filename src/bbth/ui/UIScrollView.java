package bbth.ui;

import android.graphics.Canvas;

public class UIScrollView extends UIView {
	
	private float dx, dy, _x, _y;
	private boolean isScrolling, isDown;
	
	public UIScrollView(Object tag) {
		super(tag);
		
	}

	@Override
	public void onDraw(Canvas canvas) {
		if(!_hasAppeared)
			willAppear(true);
		canvas.translate(-dx, -dy);
		for(UIView e : subviews) 
	    	  e.onDraw(canvas);
		canvas.translate(dx, dy);
	}

	@Override
	public void onTouchDown(float x, float y) {
		isDown = true;
		_x = x;
		_y = y;
		super.onTouchDown(x, y);
	}

	@Override
	public void onTouchUp(float x, float y) {
		isDown = false;
		dx = _x - x;
		dy = _y - y;
		super.onTouchUp(x, y);
	}

	@Override
	public void onTouchMove(float x, float y) {
		if(isDown)
		{
			dx = _x - x;
			dy = _y - y;
		}
		super.onTouchMove(x, y);
	}

}
