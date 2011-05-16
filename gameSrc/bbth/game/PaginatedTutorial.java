package bbth.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Shader;
import bbth.engine.ui.Anchor;
import bbth.engine.ui.UIButton;
import bbth.engine.ui.UIButtonDelegate;
import bbth.engine.ui.UIDefaultConstants;
import bbth.engine.ui.UIImageView;
import bbth.engine.ui.UILabel;
import bbth.engine.ui.UILabel.VAlign;

public class PaginatedTutorial extends Tutorial implements UIButtonDelegate {

	private int frame;
	private Paint paint;
	private boolean isFinished;
	private LinearGradient background;
	private int background_start_color, background_end_color;
	private UILabel heading, messageBox;
	private UIButton close, next, previous;
	private UIImageView track, unitcreation, wallcreation, basehealth, abouttowin;

	public PaginatedTutorial() {
		this.isFinished = false;
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setTextSize(16);
		paint.setStrokeWidth(UIDefaultConstants.BORDER_WIDTH);
		
		setBackgroundColor(UIDefaultConstants.BACKGROUND_COLOR);
		
		heading = new UILabel("Instructions", this);
		heading.setTextAlign(Align.CENTER);
		heading.setTextColor(Color.BLACK);
		heading.setTextSize(20);
		heading.setBold(true);
		heading.sizeToFit();
		heading.setAnchor(Anchor.TOP_CENTER);
		heading.setPosition(center.x, _rect.top + 8);
		addSubview(heading);
		
		messageBox = new UILabel("", this);
		messageBox.setAnchor(Anchor.TOP_LEFT);
		messageBox.setTextColor(Color.BLACK);
		messageBox.setSize(60, 20);
		messageBox.setWrapText(true);
		messageBox.setTextAlign(Align.LEFT);
		messageBox.setVerticalAlign(VAlign.TOP);
		messageBox.setLineHeight(18);
		addSubview(messageBox);
		
		
		close = new UIButton("Start Game", this);
		close.setAnchor(Anchor.BOTTOM_CENTER);
		close.setSize(100, 30);
		close.setButtonDelegate(this);
		addSubview(close);
		
		next = new UIButton(">", this);
		next.setAnchor(Anchor.BOTTOM_CENTER);
		next.setSize(30, 30);
		next.setButtonDelegate(this);
		next.setBold(true);
		addSubview(next);
		
		previous = new UIButton("<", this);
		previous.setAnchor(Anchor.BOTTOM_CENTER);
		previous.setSize(30, 30);
		previous.setButtonDelegate(this);
		previous.setBold(true);
		addSubview(previous);
		
		track = new UIImageView(R.drawable.track);
		unitcreation = new UIImageView(R.drawable.unitcreation);
		wallcreation = new UIImageView(R.drawable.wallcreation);
		basehealth = new UIImageView(R.drawable.basehealth);
		abouttowin = new UIImageView(R.drawable.abouttowin);
		
		loadFrame(frame);
		
	}
	
	public void setBackgroundColor(int color)
	{
		background_start_color = color;
		float[] hsv = new float[3];
		Color.colorToHSV(color, hsv);
		hsv[2] *= 0.5f; // value component
		background_end_color = Color.HSVToColor(hsv);
		background = new LinearGradient(_rect.left, _rect.top, _rect.left, _rect.bottom, background_start_color, background_end_color, Shader.TileMode.MIRROR);
	}

	public boolean isFinished() {
		return isFinished;
	}

	@Override
	public void setBounds(float left, float top, float right, float bottom) {
		super.setBounds(left, top, right, bottom);
		setBackgroundColor(background_start_color);
		
		heading.setPosition(center.x, top+4);
		
		close.setPosition(center.x, bottom - 4);
		previous.setPosition(left + 20, bottom - 4);
		next.setPosition(right - 20, bottom - 4);
		
		loadFrame(frame);
	}

	@Override
	public void onDraw(Canvas canvas) {
		//Draw Overlay
		paint.setARGB(128, 0, 0, 0);
		paint.setShader(null);
		canvas.drawRect(0, 0, BBTHGame.WIDTH, BBTHGame.HEIGHT, paint);
		
		// Draw Border
		paint.setColor(background_start_color);
		paint.setShader(null);
		paint.setStyle(Style.STROKE);
		canvas.drawRoundRect(_rect, UIDefaultConstants.CORNER_RADIUS, UIDefaultConstants.CORNER_RADIUS, paint);
		
		//Draw Background
		paint.setShader(background);
		paint.setStyle(Style.FILL);
		canvas.drawRoundRect(_rect, UIDefaultConstants.CORNER_RADIUS, UIDefaultConstants.CORNER_RADIUS, paint);
		
		super.onDraw(canvas);
		
		if(frame == 0)
		{
			track.onDraw(canvas);
		}
		
		if(frame == 1)
		{
			unitcreation.onDraw(canvas);
		}

		if(frame == 2)
		{
			wallcreation.onDraw(canvas);
		}
		
		if(frame == 3)
		{
			basehealth.onDraw(canvas);
		}
		
		if(frame == 4)
		{
			abouttowin.onDraw(canvas);
		}
	}

	@Override
	public void willAppear(boolean animated) {
		return;
	}

	@Override
	public void onClick(UIButton button) {
		if(button == close)
		{
			isFinished = true;
		}else if(button == next)
		{
			frame++;
			loadFrame(frame);
		}else if(button == previous)
		{
			frame--;
			loadFrame(frame);
		}
	}
	
	private void loadFrame(int frame)
	{
		previous.isDisabled = false;
		next.isDisabled = false;
		if (!BBTHGame.DEBUG) {
			close.isDisabled = true;
		}
		switch (frame) {
		case 0:
			previous.isDisabled = true;
			messageBox.setSize(_width - 45, _height - 75);
			messageBox.setPosition(_rect.left + 50, _rect.top + 35);
			messageBox.setText(R.string.beat_track);
			track.setPosition(_rect.left + 10, _rect.top + 35);
			track.setSize(30, _height- 75);
			break;
			
		case 1:
			messageBox.setSize(_width - 150, _height - 75);
			messageBox.setPosition(_rect.left + 140, _rect.top + 35);
			messageBox.setText(R.string.field);
			unitcreation.setPosition(_rect.left + 10, _rect.top + 35);
			unitcreation.setSize(124, 163);
			break;
			
		case 2:
			messageBox.setSize(_width - 20, 100);
			messageBox.setPosition(_rect.left + 15, _rect.top + 35);
			messageBox.setText(R.string.walls);
			wallcreation.setPosition(_rect.left + 10, _rect.top + 110);
			wallcreation.setSize(214, 92);
			break;
		
		case 3:
			messageBox.setSize(_width - 20, _height - 75);
			messageBox.setPosition(_rect.left + 15, _rect.top + 35);
			messageBox.setText(R.string.bases);
			basehealth.setPosition(_rect.left + 60, _rect.bottom - 120);
			basehealth.setSize(118, 58);
			break;
		
		case 4:
			next.isDisabled = true;
			close.isDisabled = false;
			messageBox.setSize(_width - 20, _height - 75);
			messageBox.setPosition(_rect.left + 15, _rect.top + 35);
			messageBox.setText(R.string.victory);
			abouttowin.setPosition(_rect.left + 55, _rect.bottom - 120);
			abouttowin.setSize(134, 59);
			break;

		default:
			break;
		}
	}
}
