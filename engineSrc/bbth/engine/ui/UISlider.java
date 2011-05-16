package bbth.engine.ui;

import bbth.engine.util.MathUtils;
import android.graphics.Canvas;
import android.graphics.Paint;

public class UISlider extends UIControl {
	private Paint paint;
	private float minValue, maxValue, currValue, range;

	/**
	 * Given the current value, where that value is located relative to the
	 * RectF that this shape takes up.
	 */
	private float valueLocation;

	public UISlider() {
		this(0.f, 1.f, 0.f);
	}

	public UISlider(float defaultValue) {
		this(0.f, 1.f, defaultValue);
	}

	public UISlider(float minValue, float maxValue, float defaultValue) {
		this.setRange(minValue, maxValue);
		this.setValue(defaultValue);

		this.paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	}

	public void setRange(float minValue, float maxValue) {
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.range = maxValue - minValue;
	}

	public void setValue(float value) {
		this.currValue = MathUtils.clamp(this.minValue, this.maxValue, value);
		this.valueLocation = _rect.left + (currValue - this.minValue) / range * (_rect.width());
	}

	public float getValue() {
		return this.currValue;
	}

	@Override
	protected void setBounds(float left, float top, float right, float bottom) {
		super.setBounds(left, top, right, bottom);
		this.recomputeValueLocation();
	}

	@Override
	public void setPosition(float x, float y) {
		super.setPosition(x, y);
		this.recomputeValueLocation();
	}

	@Override
	public void setSize(float width, float height) {
		super.setSize(width, height);
		this.recomputeValueLocation();
	}

	private void recomputeValueLocation() {
		this.valueLocation = _rect.left + (currValue - this.minValue) / range * (_rect.width());
	}
		
	private void recomputeValueWithTouch(float touchLoc) {
		float computedValue = this.minValue + (touchLoc - _rect.left) / (_rect.width()) * (this.range);
		this.setValue(computedValue);
	}

	@Override
	public void onTouchDown(float x, float y) {
		super.onTouchDown(x, y);

		this.recomputeValueWithTouch(x);
	}

	@Override
	public void onTouchMove(float x, float y) {
		super.onTouchMove(x, y);

		this.recomputeValueWithTouch(x);
	}

	@Override
	public void onTouchUp(float x, float y) {
		super.onTouchUp(x, y);

		this.recomputeValueWithTouch(x);
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		paint.setColor(UIDefaultConstants.BACKGROUND_COLOR);
		canvas.drawRect(_rect, paint);

		paint.setColor(UIDefaultConstants.FOREGROUND_COLOR);
		canvas.drawLine(this.valueLocation, _rect.top, this.valueLocation, _rect.bottom, paint);
	}
}
