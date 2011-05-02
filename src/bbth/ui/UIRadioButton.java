package bbth.ui;

import bbth.util.MathUtils;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Paint.Style;

public class UIRadioButton extends UIControl {
	private static final int DEFAULT_BG = Color.LTGRAY, DEFAULT_FG = Color.GRAY;
	private static final float DEFAULT_BORDER_WIDTH = 3.f, DEFAULT_INNER_RADIUS_RATIO = 0.6f;

	private Paint _bg_paint, _fg_paint;
	private int _bg_color, _fg_color;
	private LinearGradient _bg_gradient, _fg_gradient;

	private boolean _selected;
	private float _inner_radius_ratio, _outer_radius;

	public UIRadioButton(Object tag) {
		super(tag);

		_bg_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		_bg_paint.setStrokeWidth(DEFAULT_BORDER_WIDTH);

		_fg_paint = new Paint(Paint.ANTI_ALIAS_FLAG);

		_outer_radius = Math.min(_rect.width(), _rect.height()) / 2;
		_inner_radius_ratio = DEFAULT_INNER_RADIUS_RATIO;
		_selected = false;

		// _bg_paint.setColor(DEFAULT_BG);
		setBackgroundColor(DEFAULT_BG);
		//_fg_paint.setColor(DEFAULT_FG);
		setForegroundColor(DEFAULT_FG);
	}

	@Override
	public void onTouchDown(float x, float y) {
		// TODO animation
	}

	@Override
	public void onTouchUp(float x, float y) {
		if (MathUtils.get_dist(x, y, _rect.centerX(), _rect.centerY()) <= _outer_radius) {
			_selected = !_selected;
		}
	}

	private static LinearGradient generateVerticalGradient(RectF boundary, int color, boolean startColor) {
		float[] hsv = new float[3];
		Color.colorToHSV(color, hsv);
		hsv[2] *= 0.8f; // value component
		int c2 = Color.HSVToColor(hsv);

		if (startColor) {
			return new LinearGradient(boundary.left, boundary.top, boundary.left, boundary.bottom, color, c2, Shader.TileMode.MIRROR);
		} else {
			return new LinearGradient(boundary.left, boundary.top, boundary.left, boundary.bottom, c2, color, Shader.TileMode.MIRROR);
		}
	}

	public void setForegroundColor(int color) {
		_fg_color = color;
		_fg_gradient = generateVerticalGradient(_rect, color, true);
		_fg_paint.setColor(color);
	}
	
	public void setBackgroundColor(int color) {
		_bg_color = color;
		_bg_gradient = generateVerticalGradient(_rect, color, false);
		_bg_paint.setColor(color);
	}

	public void setBounds(float left, float top, float right, float bottom) {
		super.setBounds(left, top, right, bottom);

		_outer_radius = Math.min(_rect.width(), _rect.height()) / 2;
		setBackgroundColor(_bg_color);
		setForegroundColor(_fg_color);
	}

	@Override
	public void onDraw(Canvas canvas) {
		_bg_paint.setStyle(Style.STROKE);
		canvas.drawCircle(_rect.centerX(), _rect.centerY(), _outer_radius, _bg_paint);
		_bg_paint.setStyle(Style.FILL);

		_bg_paint.setShader(_bg_gradient);
		canvas.drawCircle(_rect.centerX(), _rect.centerY(), _outer_radius, _bg_paint);
		_bg_paint.setShader(null);

		if (_selected) {
			_fg_paint.setShader(_fg_gradient);
			canvas.drawCircle(_rect.centerX(), _rect.centerY(), _outer_radius * _inner_radius_ratio, _fg_paint);
			_fg_paint.setShader(null);
		}
	}
}
