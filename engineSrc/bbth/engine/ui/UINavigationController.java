package bbth.engine.ui;

import java.util.LinkedList;
import java.util.NoSuchElementException;

import android.graphics.Canvas;

public class UINavigationController extends UIView {
	Transition instantTransition = new Transition() {
		@Override
		public boolean isDone() {
			return true;
		}
		
		@Override
		public void setTime(float transitionTime) {}

		@Override
		public void draw(Canvas canvas, UIView oldView, UIView newView) {
			newView.onDraw(canvas);
		}
	};
	
	private LinkedList<UIView> screens;
	private UIView currentView;

	private UIView newView;
	private Transition transition;
	private boolean transitioning;
	private float transitionTime; 

	public UINavigationController(Object tag) {
		super(tag);
		screens = new LinkedList<UIView>();
	}

	@Override
	public void onUpdate(float seconds) {
		if (currentView != null)
			currentView.onUpdate(seconds);
		if (transitioning) {
			if (newView != null)
				newView.onUpdate(seconds);
			if (transitionTime < 0f)
				transitionTime = 0f;
			else
				transitionTime += seconds;
			transition.setTime(transitionTime);
			if (transition.isDone()) {
				transitioning = false;
				currentView = newView;
				transitionTime = -1f;
				
				// let gc do its work
				transition = null;
				newView = null;
			}
		}
	}

	@Override
	public void onDraw(Canvas canvas) {
		if (transitioning) {
			transition.draw(canvas, currentView, newView);
		} else if (currentView != null)
			currentView.onDraw(canvas);
	}
	
	@Override
	public boolean containsPoint(float x, float y) {
		return (currentView != null && !transitioning && currentView.containsPoint(x, y));
	}
	
	@Override
	public void onTouchDown(float x, float y) {
		if (currentView != null && !transitioning && currentView.containsPoint(x, y))
			currentView.onTouchDown(x, y);
	}

	@Override
	public void onTouchUp(float x, float y) {
		if (currentView != null && !transitioning && currentView.containsPoint(x, y))
			currentView.onTouchUp(x, y);
	}

	@Override
	public void onTouchMove(float x, float y) {
		if (currentView != null && !transitioning && currentView.containsPoint(x, y))
			currentView.onTouchMove(x, y);

	}

	@Override
	public void setBounds(float left, float top, float right, float bottom) {
		for (UIView screen : screens)
			screen.setBounds(left, top, right, bottom);
	}
	
	private void startTransition(Transition transition, UIView newView) {
		this.transition = transition;
		this.newView = newView;
		transitioning = true;
	}

	// returns true on success, false on failure
	public boolean pop() {
		if (screens.size() <= 1) {
			return false;
		}
		pop(instantTransition);
		return true;
	}
	
	public void pop(Transition transition) {
		try {
			if (currentView != null)
				currentView.willHide(true);
			screens.removeFirst(); // remove current view
			UIView newView = screens.getFirst();
			newView.willAppear(true);
			startTransition(transition, newView);
		} catch (NoSuchElementException e) {
			currentView = null;
		}
	}

		
	public void push(UIView screen) {
		push(screen, instantTransition);
	}
	
	public void push(UIView screen, Transition transition) {
		if (currentView != null)
			currentView.willHide(true);

		screens.addFirst(screen);
		screen.willAppear(true);
		startTransition(transition, screen);
	}
	
	public void pushUnder(UIView screen) {
		screens.add(1, screen);
	}

	public void pushBack(UIView screen) {
		screens.addLast(screen);
	}

	public void clear() {
		if (currentView != null)
			currentView.willHide(true);

		screens.clear();
		currentView = null;
	}

}
