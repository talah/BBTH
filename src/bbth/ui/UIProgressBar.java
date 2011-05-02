package bbth.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Shader;
import bbth.util.MathUtils;

public class UIProgressBar extends UIControl {
	public enum Mode {
		FINITE, INFINTE;
	}

	private static final int DEFAULT_BG = Color.LTGRAY, DEFAULT_FG = Color.GRAY;
	private static final int DEFAULT_NUM_GRADIENT_COLORS = 8;
	private static final float DEFAULT_BORDER_WIDTH = 3.f;
	private static final int DEFAULT_CORNER_RADIUS = 6;
	private static final Mode DEFAULT_MODE = Mode.FINITE;

	private int _bg_start_color, _bg_end_color;
	private LinearGradient _bg_gradient;
	private Paint _bg_paint;
	private float _border_radius;

	private int _fg_start_color, _fg_end_color;
	private LinearGradient _fg_gradient;
	private Paint _fg_paint;

	// Nonsense relating to the candy cane
	private int _num_gradient_colors;
	private Matrix _tr_matrix;

	private Mode _mode;

	private float _progress;
	private RectF _progress_bar;

	public UIProgressBar(Object tag) {
		super(tag);

		_num_gradient_colors = DEFAULT_NUM_GRADIENT_COLORS;
		_mode = DEFAULT_MODE;
		_border_radius = DEFAULT_CORNER_RADIUS;

		_bg_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		_bg_paint.setStrokeWidth(DEFAULT_BORDER_WIDTH);
		_fg_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		_progress_bar = new RectF();
		_tr_matrix = new Matrix();

		setBackgroundColor(DEFAULT_BG);
		setForegroundColor(DEFAULT_FG);
		setProgress(0.f);
	}

	public void setMode(Mode mode) {
		this._mode = mode;
		this.setForegroundColor(_fg_start_color);

		switch (mode) {
		case INFINTE:
			this._progress = 1.f;
			this.recomputeProgressRect();
			break;

		case FINITE:
			// this.setProgress(this._progress);
			this.setProgress(0.f);
			break;
		}
	}

	public void setBorderRadius(float radius) {
		this._border_radius = radius;
	}

	private void recomputeProgressRect() {
		this._progress_bar.set(_rect.left, _rect.top, _rect.left + _rect.width() * _progress, _rect.bottom);
	}

	public void setProgress(float amount) {
		if (_mode == Mode.FINITE) {
			amount = MathUtils.clamp(0.f, 1.f, amount);
			this._progress = amount;

			this.recomputeProgressRect();
		}
	}

	public float getProgress() {
		return this._progress;
	}

	public void setBounds(float left, float top, float right, float bottom) {
		super.setBounds(left, top, right, bottom);

		setForegroundColor(_fg_start_color);
		setBackgroundColor(_bg_end_color);
		recomputeProgressRect();
	}

	public void setForegroundColor(int color) {
		_fg_start_color = color;
		float[] hsv = new float[3];
		Color.colorToHSV(color, hsv);
		hsv[2] *= 0.8f; // value component
		_fg_end_color = Color.HSVToColor(hsv);

		switch (this._mode) {
		case FINITE:
			_fg_gradient = new LinearGradient(_rect.left, _rect.top, _rect.left, _rect.bottom, _fg_start_color, _fg_end_color, Shader.TileMode.MIRROR);
			break;

		case INFINTE:
			int[] colors = new int[_num_gradient_colors];

			for (int i = 0; i < _num_gradient_colors; i++) {
				if (i % 2 == 0) {
					colors[i] = _fg_start_color;
				} else {
					colors[i] = _fg_end_color;
				}
			}

			_fg_gradient = new LinearGradient(_rect.left, _rect.top, _rect.right, _rect.bottom, colors, null, Shader.TileMode.MIRROR);
			break;
		}
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

	public void onUpdate(float seconds) {
		float speed = 100.f;

		if (_mode == Mode.INFINTE) {
			_tr_matrix.preTranslate(0, speed * seconds);
			_fg_gradient.setLocalMatrix(_tr_matrix);
		}
	}
}
