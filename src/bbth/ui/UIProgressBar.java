package bbth.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Shader;
import bbth.util.MathUtils;

public class UIProgressBar extends UIControl {
	private static final int DEFAULT_BG = Color.LTGRAY, DEFAULT_FG = Color.GREEN;
	private static final float DEFAULT_BORDER_WIDTH = 3.f;
	private static final int DEFAULT_CORNER_RADIUS = 6;
	
	private int _bg_start_color, _bg_end_color;
	private LinearGradient _bg_gradient;
	private Paint _bg_paint;
	private float _border_radius;

	private int _fg_start_color, _fg_end_color;
	private LinearGradient _fg_gradient;
	private Paint _fg_paint;

	private float _progress;
	private RectF _progress_bar;
	
	public UIProgressBar(Object tag) {
		// super(tag);
		super(tag);

		_border_radius = DEFAULT_CORNER_RADIUS;
		
		_bg_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		setBackgroundColor(DEFAULT_BG);
		_bg_paint.setStrokeWidth(DEFAULT_BORDER_WIDTH);
		
		_fg_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		setForegroundColor(DEFAULT_FG);
		
		_progress_bar = new RectF();
		setProgress(0.f);
	}

	public void setBorderRadius(float radius) {
		this._border_radius = radius;
	}

	public void setProgress(float amount) {
		amount = MathUtils.clamp(0.f, 1.f, amount);
		
		this._progress = amount;
		this._progress_bar.set(_rect.left, _rect.top, _rect.left + _rect.width() * amount, _rect.bottom);
	}
	
	public void setBounds(float left, float top, float right, float bottom) {
		super.setBounds(left, top, right, bottom);
		_bg_gradient = new LinearGradient(left, top, left, bottom, _bg_start_color, _bg_end_color, Shader.TileMode.MIRROR);
		_fg_gradient = new LinearGradient(left, top, left, bottom, _fg_start_color, _fg_end_color, Shader.TileMode.MIRROR);
	}

	public void setForegroundColor(int color) {
		_fg_start_color = color;
		float[] hsv = new float[3];
		Color.colorToHSV(color, hsv);
		hsv[2] *= 0.8f; // value component
		_fg_end_color = Color.HSVToColor(hsv);
		_fg_gradient = new LinearGradient(_rect.left, _rect.top, _rect.left, _rect.bottom, _fg_start_color, _fg_end_color, Shader.TileMode.MIRROR);

		_fg_paint.setColor(_fg_start_color);
	}

	public void setBackgroundColor(int color) {
		_bg_end_color = color;
		float[] hsv = new float[3];
		Color.colorToHSV(color, hsv);
		hsv[2] *= 0.8f; // value component
		_bg_start_color = Color.HSVToColor(hsv);
		_bg_gradient = new LinearGradient(_rect.left, _rect.top, _rect.left, _rect.bottom, _bg_start_color, _bg_end_color, Shader.TileMode.MIRROR);

		_bg_paint.setColor(_bg_start_color);
	}

	public void onDraw(Canvas canvas) {
		_bg_paint.setStyle(Style.STROKE);
		canvas.drawRoundRect(_rect, _border_radius, _border_radius, _bg_paint);
		_bg_paint.setStyle(Style.FILL);

		_bg_paint.setShader(_bg_gradient);
		canvas.drawRoundRect(_rect, _border_radius, _border_radius, _bg_paint);
		_bg_paint.setShader(null);

		_fg_paint.setShader(_fg_gradient);
		canvas.drawRoundRect(_progress_bar, _border_radius, _border_radius, _fg_paint);
		_fg_paint.setShader(null);
	}
}
