package bbth.ui;

import bbth.util.MathUtils;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class UIRadioButton extends UIControl {
	private static final int DEFAULT_BG = Color.LTGRAY, DEFAULT_FG = Color.GRAY;
	private static final float DEFAULT_BORDER_WIDTH = 3.f, DEFAULT_INNER_RADIUS_RATIO = 0.8f;

	private Paint _bg_paint, _fg_paint;
	private boolean _selected;
	private float _inner_radius_ratio, _outer_radius;

	public UIRadioButton(Object tag) {
		super(tag);

		_bg_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		_bg_paint.setStrokeWidth(DEFAULT_BORDER_WIDTH);

		_fg_paint = new Paint(Paint.ANTI_ALIAS_FLAG);

		_outer_radius = Math.min(_rect.width(), _rect.height());
		_inner_radius_ratio = DEFAULT_INNER_RADIUS_RATIO;
		_selected = false;
		_bg_paint.setColor(DEFAULT_BG);
		_fg_paint.setColor(DEFAULT_FG);
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

	public void setBounds(float left, float top, float right, float bottom) {
		super.setBounds(left, top, right, bottom);

		_outer_radius = Math.min(_rect.width(), _rect.height());
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		canvas.drawCircle(_rect.centerX(), _rect.centerY(), _outer_radius, _bg_paint);

		if (_selected) {
			canvas.drawCircle(_rect.centerX(), _rect.centerY(), _outer_radius * _inner_radius_ratio, _fg_paint);
		}
	}
}
