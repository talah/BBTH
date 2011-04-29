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
		_paint.setTextSize(14);

		_paint.setColor(DEFAULT_TEXT_COLOR);

		_paint.setStrokeWidth(3);

		this.text = text;
		this.tag = tag;
		sizeToFit();
	}

	@Override
	public void onDraw(Canvas canvas) {
	    super.onDraw(canvas);
	    switch (_paint.getTextAlign()) {
		case CENTER:
			canvas.drawText(text, center.x, center.y + _paint.getTextSize() / 3.f, _paint);
			break;
		case LEFT:
			canvas.drawText(text, _rect.left, center.y + _paint.getTextSize() / 3.f, _paint);
			break;
		case RIGHT:
			canvas.drawText(text, _rect.right, center.y + _paint.getTextSize() / 3.f, _paint);
			break;
		}
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
		sizeToFit();
	}

	public void setTextColor(int color)
	{
		_paint.setColor(color);
	}
	
	public void sizeToFit()
	{
		setSize(_paint.measureText(this.text), _paint.getTextSize());	
	}
	
	public void setBold(boolean bold)
	{
		_paint.setFakeBoldText(bold);
	}
	
	public void setTextAlign(Align align)
	{
		_paint.setTextAlign(align);
	}
    
}