package bbth.engine.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import bbth.engine.util.MathUtils;
import bbth.engine.util.Point;

public class UISlider extends UIControl {
	/**
	 * Values related to the logical slider.
	 */
	private float minValue, maxValue, currValue, range;
	private boolean isMoving;

	/**
	 * Stuff that has to do with drawing. This is probably all entangled up and
	 * whatnot. >_<
	 */
	private Point circleLocation;
	private Paint paint;
	private float cornerRadius;
	private RectF barRect;
	private float barHeight;
	private float circleRadius;

	public UISlider() {
		this(0.f, 1.f, 0.f);
	}

	public UISlider(float defaultValue) {
		this(0.f, 1.f, defaultValue);
	}

	public UISlider(float minValue, float maxValue, float defaultValue) {
		this.paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		this.barRect = new RectF();
		this.circleLocation = new Point();
		
		this.cornerRadius = UIDefaultConstants.CORNER_RADIUS;
		this.barHeight = UIDefaultConstants.UI_SLIDER_BAR_HEIGHT;
		this.circleRadius = UIDefaultConstants.UI_SLIDER_CIRCLE_RADIUS;
		this.isMoving = false;

		this.setRange(minValue, maxValue);
		this.setValue(defaultValue);
	}

	public void setRange(float minValue, float maxValue) {
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.range = maxValue - minValue;
		// TODO Call recomputeDrawingLocations()
	}

	public void setValue(float value) {
		this.currValue = MathUtils.clamp(this.minValue, this.maxValue, value);
		this.recomputeDrawingLocations();
	}

	public float getValue() {
		return this.currValue;
	}

	@Override
	protected void setBounds(float left, float top, float right, float bottom) {
		super.setBounds(left, top, right, bottom);
		this.recomputeDrawingLocations();
	}

	@Override
	public void setPosition(float x, float y) {
		super.setPosition(x, y);
		this.recomputeDrawingLocations();
	}

	@Override
	public void setSize(float width, float height) {
		super.setSize(width, height);
		this.recomputeDrawingLocations();
	}

	private void recomputeDrawingLocations() {
		float center = _rect.centerY();
		this.barRect.set(_rect.left + circleRadius, center - barHeight, _rect.right - circleRadius, center + barHeight);

		this.circleLocation.set(this.barRect.left + (currValue - this.minValue) / range * (this.barRect.width()), _rect.centerY());
	}

	private void recomputeValueWithTouch(float touchLoc) {
		float computedValue = this.minValue + (touchLoc - _rect.left) / (_rect.width()) * (this.range);
		this.setValue(computedValue);
	}

	@Override
	public void onTouchDown(float x, float y) {
		super.onTouchDown(x, y);

		if (MathUtils.getDistSqr(x, y, circleLocation.x, circleLocation.y) < this.circleRadius * this.circleRadius * 2) {
			this.isMoving = true;
			this.recomputeValueWithTouch(x);
		}
	}

	@Override
	public void onTouchMove(float x, float y) {
		super.onTouchMove(x, y);

		if (this.isMoving) {
			this.recomputeValueWithTouch(x);
		}
	}

	@Override
	public void onTouchUp(float x, float y) {
		super.onTouchUp(x, y);

		if (this.isMoving) {
			this.isMoving = false;
			this.recomputeValueWithTouch(x);
		}
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		paint.setColor(Color.RED);
		canvas.drawRect(_rect, paint);

		paint.setColor(UIDefaultConstants.BACKGROUND_COLOR);
		canvas.drawRoundRect(this.barRect, this.cornerRadius, this.cornerRadius, paint);

		paint.setColor(UIDefaultConstants.FOREGROUND_COLOR);
		canvas.drawCircle(this.circleLocation.x, this.circleLocation.y, this.circleRadius, paint);
	}
}
