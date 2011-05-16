package bbth.engine.ui;

import java.util.ArrayList;

import android.graphics.*;
import android.graphics.Paint.Align;
import bbth.game.BBTHActivity;

public class UILabel extends UIControl {
	
	public static enum VAlign {
		TOP,
		MIDDLE,
		BOTTOM
	}
    
    Paint _paint;

	private String text;
	public boolean shouldAutoresizeText;
	private boolean wrapText, lineHeightSet;
	private ArrayList<String> wrapped_text;
	private float _y_offset;
	private VAlign v_align;
	private float text_size, line_height;

	public UILabel(String text, Object tag) {
	    super(tag);

	    
	    this.text = text;
		_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		_paint.setTextAlign(Align.CENTER);
		setTextSize(14);

		_paint.setColor(UIDefaultConstants.UI_LABEL_TEXT_COLOR);

		_paint.setStrokeWidth(3);
		
		v_align = VAlign.MIDDLE;

		this.tag = tag;
		sizeToFit();
	}

	@Override
	public void onDraw(Canvas canvas) {
	    super.onDraw(canvas);
	    _y_offset = 0;
	    if(wrapText)
	    	for(int i = 0; i < wrapped_text.size(); i++)
	    	{
	    		drawText(canvas, wrapped_text.get(i));
	    		_y_offset++;
	    	}
	    else
	    	drawText(canvas, text);
	    
	}
	
	private void drawText(Canvas canvas, String t)
	{
		float y = 0;
		switch (v_align) {
		case TOP:
			y = _rect.top + text_size / 2.f;
			break;
		case MIDDLE:
			y = center.y;
			break;
		case BOTTOM:
			if(!wrapText)
				y = _rect.bottom - text_size / 2.f;
			else
				y = _rect.bottom - (wrapped_text.size() -1) * text_size - text_size / 2.f;
			break;
		default:
			break;
		}
		switch (_paint.getTextAlign()) {
		case CENTER:
			canvas.drawText(t, center.x,  y + text_size / 3.f + _y_offset * line_height, _paint);
			break;
		case LEFT:
			canvas.drawText(t, _rect.left, y + text_size / 3.f + _y_offset * line_height, _paint);
			break;
		case RIGHT:
			canvas.drawText(t, _rect.right, y + text_size / 3.f + _y_offset * line_height, _paint);
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
		if(wrapText)
		{
			wrapText();
			return;
		}
		
		float width = _paint.measureText(text);
		if(width > _width && shouldAutoresizeText)
		{
			text_size = _width / width * text_size;
			_paint.setTextSize(text_size);
			if(!lineHeightSet)
				line_height = text_size;
		}
	}
	
	public void setText(int id)
	{
		setText(BBTHActivity.instance.getString(id));
	}

	public void setTextSize(float size)
	{
		text_size = size;
		_paint.setTextSize(size);
		
		if(!lineHeightSet)
			line_height = text_size;
		
		if(wrapText)
		{
			wrapText();
			return;
		}
		sizeToFit();
	}
	
	public void setLineHeight(float h)
	{
		if(h == 0)
		{
			lineHeightSet = false;
			line_height = text_size;
			return;
		}
		
		lineHeightSet = true;
		line_height = h;
	}

	public void setTextColor(int color)
	{
		_paint.setColor(color);
	}
	
	public void sizeToFit()
	{
		setSize(_paint.measureText(this.text), text_size);	
	}
	
	public void setBold(boolean bold)
	{
		_paint.setFakeBoldText(bold);
	}
	
	public void setTextAlign(Align align)
	{
		_paint.setTextAlign(align);
	}
	
	public void setVerticalAlign(VAlign align)
	{
		v_align = align;
	}
	
	public void setWrapText(boolean value)
	{
		if(value == true)
		{
			wrapText();
		}
		wrapText = value;
	}
	
	private void wrapText()
	{
		if(text == null || text.equals(""))
			return;
		if(wrapped_text == null)
			wrapped_text = new ArrayList<String>();
		else
			wrapped_text.clear();
		
		String[] words = text.split(" ");
		String lastPhrase = "";

		for (int i = 0; i < words.length; ++i)
		{
			String word = words[i];
			if(_paint.measureText(lastPhrase + " " + word) < _width)
				lastPhrase += " " + word;
			else{
				if(lastPhrase.length() > 0) wrapped_text.add(lastPhrase.trim());
				lastPhrase = word;
			}
			if( i== words.length -1)
			{
				wrapped_text.add(lastPhrase.trim());
				break;
			}
				
		}
	}

	@Override
	public boolean isDraggable() {
		return true;
	}
}
