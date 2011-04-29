package bbth.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;

public class UILabel extends UIControl {
    
    Paint _paint;

	private String text;

	private final int DEFAULT_TEXT_COLOR = Color.BLACK;

	public UILabel(String text, Object tag) {
	    super();

		_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		_paint.setTextAlign(Align.CENTER);

		_paint.setColor(DEFAULT_TEXT_COLOR);

		_paint.setStrokeWidth(3);

		this.text = text;
		this.tag = tag;
		sizeToFit();
	}

	@Override
	public void onDraw(Canvas canvas) {
	    super.onDraw(canvas);
		canvas.drawText(text, center.x, center.y + _paint.getTextSize() / 3.f, _paint);
	}

	@Override
	public void onTouchDown(float x, float y) {
		return;
	}

	@Override
	public void onTouchUp(float x, float y) {
		return;
	}

	@Override
	public void onTouchMove(float x, float y) {
        return;
	}

	@Override
	public void setBounds(float left, float top, float right, float bottom) {
        super.setBounds(left, top, right, bottom);
		setTextSize(_height / 2.5f);
		setText(text);
	}

	public void setText(String text)
	{
		this.text = text;
		float width = _paint.measureText(text);
		if(width > _width)
			_paint.setTextSize(_width / width * _paint.getTextSize());
	}

	public void setTextSize(float size)
	{
		_paint.setTextSize(size);
	}

	public void setTextColor(int color)
	{
		_paint.setColor(color);
	}
	
	public void sizeToFit()
	{
		this._height = _paint.getTextSize();
		this._h_height = _height / 2.f;
		this._width = _paint.measureText(this.text);
		this._h_width = _width / 2.f;
		
		switch(this.anchor)
        {
            case TOP_LEFT: super.setBounds(_rect.left, _rect.top, _rect.left + _width, _rect.top + _height ); break;
            case TOP_CENTER: super.setBounds(center.x - _h_width, _rect.top, center.x + _h_width, _rect.top + _height); break;
            case TOP_RIGHT: super.setBounds(_rect.left - _width, _rect.top, _rect.left, _rect.top + _height); break;
            case CENTER_LEFT: super.setBounds(_rect.left, center.y - _h_height, _rect.left + _width, center.y + _h_height); break;
            case CENTER_CENTER: super.setBounds(center.x - _h_width, center.y - _h_height, center.x + _h_width, center.y + _h_height); break;
            case CENTER_RIGHT: super.setBounds(_rect.left - _width, center.y - _h_height, _rect.left, center.y + _h_height); break;
            case BOTTOM_LEFT: super.setBounds(_rect.left, _rect.top - _height, _rect.left + _width, _rect.top); break;
            case BOTTOM_CENTER: super.setBounds(center.x - _h_width, _rect.top - _height, center.x + _h_width, _rect.top); break;
            case BOTTOM_RIGHT: super.setBounds(_rect.left - _width, _rect.top - _height, _rect.left, _rect.top); break;
        }
        center.x = _rect.left + _h_width;
        center.y = _rect.top + _h_height;
	}
	
	public void setBold(boolean bold)
	{
		_paint.setFakeBoldText(bold);
	}
    
}