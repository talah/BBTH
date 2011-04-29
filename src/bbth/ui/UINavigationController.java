package bbth.ui;

import java.util.LinkedList;
import java.util.NoSuchElementException;

import android.graphics.Canvas;

public class UINavigationController extends UIView {
	
	private LinkedList<UIView> screens;
	private UIView current_screen;

	public UINavigationController() {
		screens = new LinkedList<UIView>();
	}
	
	@Override
	public void onUpdate(float seconds)
	{
		if(current_screen != null)
			current_screen.onUpdate(seconds);
	}

	@Override
	public void onDraw(Canvas canvas) {
		if(current_screen != null)
			current_screen.onDraw(canvas);
	}
	
	public boolean containsPoint(float x, float y)
	{
		return (current_screen != null && current_screen.containsPoint(x, y));
	}

	@Override
	public void onTouchDown(float x, float y) {
		if(current_screen != null && current_screen.containsPoint(x, y))
			current_screen.onTouchDown(x, y);

	}

	@Override
	public void onTouchUp(float x, float y) {
		if(current_screen != null && current_screen.containsPoint(x, y))
			current_screen.onTouchUp(x, y);
	}

	@Override
	public void onTouchMove(float x, float y) {
		if(current_screen != null && current_screen.containsPoint(x, y))
			current_screen.onTouchMove(x, y);

	}

	@Override
	public void setBounds(float left, float top, float right, float bottom) {
		for(UIView screen : screens)
			screen.setBounds(left, top, right, bottom);

	}
	
	public void pop()
	{
		try{
		    if(current_screen != null)
		        current_screen.willHide(true);
			current_screen = screens.removeFirst();
			current_screen.willAppear(true);
			
		}catch(NoSuchElementException e)
		{
			current_screen = null;
		}
	}
	
	public void push(UIView screen)
	{
	    if(current_screen != null)
	        current_screen.willHide(true);
	        
		screens.addFirst(screen);
		current_screen = screen;
		current_screen.willAppear(true);
	}
	
	public void pushBack(UIView screen)
	{
		screens.addLast(screen);
	}
	
	public void clear()
	{
	    if(current_screen != null)
	        current_screen.willHide(true);
	        
		screens.clear();
		current_screen = null;
	}

}
