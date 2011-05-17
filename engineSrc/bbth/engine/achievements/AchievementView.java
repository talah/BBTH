package bbth.engine.achievements;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import bbth.engine.ui.Anchor;
import bbth.engine.ui.UIImageView;
import bbth.engine.ui.UILabel;
import bbth.engine.ui.UILabel.VAlign;
import bbth.engine.ui.UIView;

/**
 * A view for a single achievement
 * @author jardini
 *
 */
public class AchievementView extends UIView {

	public static final float NAME_SIZE = 19.f;
	public static final float DESCRIPTION_SIZE = 13.f;
	private static final float PADDING = 5;
	private static final float TOP_PADDING = 6;
	
	private UILabel _nameLabel;
	private UILabel _descriptionLabel;
	private UIImageView _image;
	private float _unlockProgress;
	private Paint _paint;
	
	public AchievementView(AchievementInfo info, int activations, Bitmap image) {
		_nameLabel = new UILabel(info.name, null);
		_nameLabel.setTextSize(NAME_SIZE);
		_nameLabel.setTextAlign(Align.LEFT);
		_nameLabel.sizeToFit();
		addSubview(_nameLabel);
		
		_descriptionLabel = new UILabel(info.description, null);
		_descriptionLabel.setTextSize(DESCRIPTION_SIZE);
		_descriptionLabel.setTextAlign(Align.LEFT);
		_descriptionLabel.setWrapText(true);
		_descriptionLabel.setVerticalAlign(VAlign.MIDDLE);
		addSubview(_descriptionLabel);
		
		_image = new UIImageView(image);
		_image.setSize(32, 32);
		_image.setAnchor(Anchor.CENTER_LEFT);
		addSubview(_image);
		
		_unlockProgress = activations / (float) (info.maxActivations);
		
		_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		_paint.setColor(Color.WHITE);
	}
	
	@Override
	public void setBounds(float left, float top, float right, float bottom) {
		super.setBounds(left, top, right, bottom);
		
		_image.setPosition(left, center.y);
		float imageRight = _image.getRect().right;
		float textWidth = _width - _image.getWidth() - PADDING;
		_nameLabel.setPosition(left + imageRight + PADDING, top + TOP_PADDING);
		_nameLabel.setSize(textWidth, NAME_SIZE);
		_descriptionLabel.setPosition(left + imageRight + TOP_PADDING, top + NAME_SIZE + TOP_PADDING + 5);
		_descriptionLabel.setSize(textWidth, DESCRIPTION_SIZE);
	}
	
	@Override
	public boolean isDraggable() {
		return true;
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		_paint.setStyle(Style.FILL);
		_paint.setColor(Color.rgb(50, 50, 50));
		canvas.drawRect(_rect.left, _rect.top, _rect.left + _unlockProgress * _rect.width(), _rect.bottom, _paint);
		super.onDraw(canvas);
		
		_paint.setStyle(Style.STROKE);
		_paint.setColor(Color.BLACK);
		canvas.drawLine(_rect.left, _rect.top, _rect.right, _rect.top, _paint);
		canvas.drawLine(_rect.left, _rect.bottom, _rect.right, _rect.bottom, _paint);
	}
}
