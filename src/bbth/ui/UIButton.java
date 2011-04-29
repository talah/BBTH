package bbth.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Shader;

public class UIButton extends UIControl {

	Paint _paint;
	
	private String text;
	private int bg_start_color, bg_end_color, txt_color, txt_disabled_color, bg_down_start_color, bg_down_end_color, stroke_color, bg_disabled_start_color, bg_disabled_end_color;
	public boolean isDown, isDisabled;
	private Shader normal_state, down_state, disabled_state;
	public float padding, corner_radius;
	
	private final int DEFAULT_BG_START = Color.LTGRAY, DEFAULT_TEXT_COLOR = Color.DKGRAY, DEFAULT_DISABLED_COLOR = Color.LTGRAY,
	DEFUALT_DISABLED_TEXT_COLOR = Color.LTGRAY;
	
	public UIButton(String text, Object tag) {
	    super();
		padding = 8;
		corner_radius = 6;
		
		_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		_paint.setTextAlign(Align.CENTER);
		_paint.setFakeBoldText(true);
		
		setButtonColor(DEFAULT_BG_START);
		setButtonDownColor(bg_end_color);
		setDisabledColor(DEFAULT_DISABLED_COLOR);
		txt_color = DEFAULT_TEXT_COLOR;
		txt_disabled_color = DEFUALT_DISABLED_TEXT_COLOR;
		stroke_color = bg_down_end_color;
		
		_paint.setStrokeWidth(3);
		
		this.text = text;
		this.tag = tag;
	}

	@Override
	public void onDraw(Canvas canvas) {
	    super.onDraw(canvas);
	    
		_paint.setStyle(Style.STROKE);
		_paint.setColor(stroke_color);
		canvas.drawRoundRect(_rect, corner_radius, corner_radius, _paint);
		_paint.setStyle(Style.FILL);
		
		if(isDisabled)
		{
			_paint.setShader(disabled_state);
			canvas.drawRoundRect(_rect, corner_radius, corner_radius, _paint);
			_paint.setShader(null);
			
			_paint.setColor(txt_disabled_color);
			canvas.drawText(text, center.x, center.y + _paint.getTextSize() / 3.f, _paint);
			return;
		}
		
		if(isDown)
		{
			_paint.setShader(down_state);
			canvas.drawRoundRect(_rect, corner_radius, corner_radius, _paint);
			_paint.setShader(null);
			
			_paint.setColor(txt_color);
			canvas.drawText(text, center.x, center.y + _paint.getTextSize() / 3.f, _paint);
		}else{
			_paint.setShader(normal_state);
			canvas.drawRoundRect(_rect, corner_radius, corner_radius, _paint);
			_paint.setShader(null);
			_paint.setColor(txt_color);
			canvas.drawText(text, center.x, center.y + _paint.getTextSize() / 3.f, _paint);
		}
	}

	@Override
	public void onTouchDown(float x, float y) {
		if(isDisabled)
			return;
		isDown = true;
		if(delegate != null)
			delegate.onTouchDown(this);
	}

	@Override
	public void onTouchUp(float x, float y) {
		if(isDisabled)
			return;
		if(isDown && delegate != null)
			delegate.onTouchUp(this);
		isDown = false;
	}

	@Override
	public void onTouchMove(float x, float y) {
		if(isDisabled)
			return;
		if(!containsPoint(x, y))
			isDown = false;

	}

	@Override
	public void setBounds(float left, float top, float right, float bottom) {
        super.setBounds(left, top, right, bottom);
		normal_state = new LinearGradient(left, top, left, bottom, bg_start_color, bg_end_color, Shader.TileMode.MIRROR);
		down_state = new LinearGradient(left, top, left, bottom, bg_down_start_color, bg_down_end_color, Shader.TileMode.MIRROR);
		disabled_state = new LinearGradient(left, top, left, bottom, bg_down_start_color, bg_down_end_color, Shader.TileMode.MIRROR);
		setTextSize(_height / 2.5f);
		setText(text);
	}
	
	public void setText(String text)
	{
		this.text = text;
		float width = _paint.measureText(text);
		if(width > _width)
			_paint.setTextSize((_width - padding) / width * _paint.getTextSize());
	}

	
	public void setButtonColor(int color)
	{
		bg_start_color = color;
		float[] hsv = new float[3];
		Color.colorToHSV(color, hsv);
		hsv[2] *= 0.8f; // value component
		bg_end_color = Color.HSVToColor(hsv);
		normal_state = new LinearGradient(_rect.left, _rect.top, _rect.left, _rect.bottom, bg_start_color, bg_end_color, Shader.TileMode.MIRROR);
	}
	
	public void setButtonDownColor(int color)
	{
		bg_down_end_color = color;
		float[] hsv = new float[3];
		Color.colorToHSV(color, hsv);
		hsv[2] *= 0.8f; // value component
		bg_down_start_color = Color.HSVToColor(hsv);
		down_state = new LinearGradient(_rect.left, _rect.top, _rect.left, _rect.bottom, bg_down_start_color, bg_down_end_color, Shader.TileMode.MIRROR);
	}
	
	public void setDisabledColor(int color)
	{
		bg_disabled_end_color = color;
		float[] hsv = new float[3];
		Color.colorToHSV(color, hsv);
		hsv[2] *= 1.2f; // value component
		bg_disabled_start_color = Color.HSVToColor(hsv);
		disabled_state = new LinearGradient(_rect.left, _rect.top, _rect.left, _rect.bottom, bg_down_start_color, bg_down_end_color, Shader.TileMode.MIRROR);
	}

	public void setTextSize(float size)
	{
		_paint.setTextSize(size);
	}
	
	public void setTextColor(int color)
	{
		txt_color = color;
	}
}