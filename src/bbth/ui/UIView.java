package bbth.ui;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

import android.graphics.Canvas;
import android.graphics.RectF;
import bbth.collision.Point;
import bbth.core.GameScreen;

public class UIView extends GameScreen {

	protected CopyOnWriteArrayList<UIView> subviews;
	private boolean _hasAppeared;
	public Object tag;
	protected RectF _rect;
	protected float _width, _height, _h_width, _h_height;
	protected Point center;
	protected UIDelegate delegate;
	
	protected Anchor anchor;

	public UIView() {
		subviews = new CopyOnWriteArrayList<UIView>();
		this.anchor = Anchor.TOP_LEFT;
		_rect = new RectF(0,0,0,0);
		center = new Point(0,0);
	}

    @Override
	public void onUpdate(float seconds)
	{
		for(UIView e : subviews) 
	    	  e.onUpdate(seconds);
	}


	@Override
	public void onDraw(Canvas canvas) {
		if(!_hasAppeared)
			willAppear(true);
		for(UIView e : subviews) 
	    	  e.onDraw(canvas);
	}

	@Override
	public void onTouchDown(float x, float y) {
		for(UIView e : subviews) 
	    	  if(e.containsPoint(x, y))
					e.onTouchDown(x, y);
	}

	@Override
	public void onTouchUp(float x, float y) {
		for(UIView e : subviews) 
    	  if(e.containsPoint(x, y))
				e.onTouchUp(x, y);
	}

	@Override
	public void onTouchMove(float x, float y) {
		for(UIView e : subviews)
			e.onTouchMove(x, y);
	}

	public void setBounds(float left, float top, float right, float bottom) {
		_rect.left = left;
		_rect.top = top;
		_rect.right = right;
		_rect.bottom = bottom;
	}

	public void addSubview(UIView subview)
	{
		if(!subviews.contains(subview))
			subviews.add(subview);

	}

	public void removeSubview(UIView subview)
	{
	    subview.willHide(true);
		subviews.remove(subview);
	}

	public void removeSubviews(Collection<UIView> views)
	{
		subviews.removeAll(views);
	}

	public void willAppear(boolean animated)
	{
		_hasAppeared = true;
	}

	public void willHide(boolean animated)
	{
		_hasAppeared = false;
		for(UIView e : subviews)
		    e.willHide(animated);
	}
    
    public void setAnchor(Anchor anchor)
    {
        this.anchor = anchor;
    }
    
    public void setSize(float width, float height)
    {
        this.setBounds(_rect.left, _rect.top, _rect.left + width, _rect.top + height);
        this._width = width;
        this._height = height;
        this._h_width = _width / 2.f;
        this._h_height = _height / 2.f;
    }
    
    public void setPosition(float x, float y)
    {
        switch(this.anchor)
        {
            case TOP_LEFT: this.setBounds(x, y, x + _width, y + _height ); break;
            case TOP_CENTER: this.setBounds(x - _h_width, y, x + _h_width, y + _height); break;
            case TOP_RIGHT: this.setBounds(x - _width, y, x, y + _height); break;
            case CENTER_LEFT: this.setBounds(x, y - _h_height, x + _width, y + _height / 2.f); break;
            case CENTER_CENTER: this.setBounds(x - _h_width, y - _h_height, x + _h_width, y + _height / 2.f); break;
            case CENTER_RIGHT: this.setBounds(x - _width, y - _h_height, x, y + _height / 2.f); break;
            case BOTTOM_LEFT: this.setBounds(x, y - _height, x + _width, y); break;
            case BOTTOM_CENTER: this.setBounds(x - _h_width, y - _height, x + _h_width, y); break;
            case BOTTOM_RIGHT: this.setBounds(x - _width, y - _height, x, y); break;
        }
        center.x = _rect.left + _h_width;
        center.y = _rect.top + _h_height;
    }
    
    public boolean containsPoint(float x, float y)
    {
        return this._rect.contains(x, y);
    }
    
    public void setDelegate(UIDelegate delegate)
    {
        this.delegate = delegate;
    }
    
}