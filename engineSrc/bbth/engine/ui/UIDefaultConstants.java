package bbth.engine.ui;

import android.graphics.Color;

public interface UIDefaultConstants {
	// General stuff
	public static final int BACKGROUND_COLOR = Color.LTGRAY;
	public static final int FOREGROUND_COLOR = Color.GRAY;
	public static final float BORDER_WIDTH = 3.f;
	public static final int CORNER_RADIUS = 6;

	// UILabel
	public static final int UI_LABEL_TEXT_COLOR = Color.WHITE;
	
	// UIButton
	public static final int UI_BUTTON_TEXT_COLOR = Color.DKGRAY;
	public static final int UI_BUTTON_DISABLED_COLOR = Color.LTGRAY;
	public static final int UI_BUTTON_DISABLED_TEXT_COLOR = Color.GRAY;

	// UIProgressBar
	public static final float UI_PROGRESS_BAR_CANDYCANE_SPEED = 100.f;
	public static final int UI_PROGRESS_BAR_NUM_GRADIENT_COLORS = 8;
	public static final UIProgressBar.Mode UI_PROGRESS_BAR_DEFAULT_MODE = UIProgressBar.Mode.FINITE;

	// UIRadioButton
	public static final float UI_RADIO_BUTTON_INNER_RADIUS_RATIO = 0.6f;
	public static final float UI_RADIO_BUTTON_WIDTH = 10;
	public static final float UI_RADIO_BUTTON_LABEL_HEIGHT = 10;
}
