package bbth.engine.achievements;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import bbth.engine.ui.Anchor;
import bbth.engine.ui.UIImageView;
import bbth.engine.ui.UILabel;
import bbth.engine.ui.UIView;
import bbth.game.R;

/**
 * A view for a single achievement
 * @author jardini
 *
 */
public class AchievementView extends UIView {

	public static final float NAME_SIZE = 22.f;
	public static final float DESCRIPTION_SIZE = 15.f;
	private static final float PADDING = 5;
	
	private UILabel _nameLabel;
	private UILabel _descriptionLabel;
	private UIImageView _image;
	private Paint _paint;
	
	
	public AchievementView(String name, String description) {	
		_nameLabel = new UILabel(name, null);
		_nameLabel.setTextSize(NAME_SIZE);
		_nameLabel.setTextAlign(Align.LEFT);
		_nameLabel.sizeToFit();
		addSubview(_nameLabel);
		
		_descriptionLabel = new UILabel(description, null);
		_descriptionLabel.setTextSize(DESCRIPTION_SIZE);
		_descriptionLabel.setTextAlign(Align.LEFT);
		_descriptionLabel.setWrapText(true);
		addSubview(_descriptionLabel);
		
		_image = new UIImageView(R.drawable.icon);
		_image.setSize(32, 32);
		_image.setAnchor(Anchor.CENTER_LEFT);
		addSubview(_image);
		
		_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		_paint.setColor(Color.WHITE);
		_paint.setStyle(Style.STROKE);
	}
	
	@Override
	public void setBounds(float left, float top, float right, float bottom) {
		super.setBounds(left, top, right, bottom);
		
		_image.setPosition(left, center.y);
		float imageRight = _image.getPosition().x + 32;//_image.getWidth();
		_nameLabel.setBounds(left + imageRight + PADDING, top + PADDING, right, NAME_SIZE);
		_descriptionLabel.setBounds(left + imageRight + PADDING, top + NAME_SIZE + PADDING * 2, right, bottom);
		_descriptionLabel.wrapText();
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		canvas.drawRect(_descriptionLabel.getRect(), _paint);
		canvas.drawLine(_rect.left, _rect.bottom, _rect.right, _rect.bottom, _paint);
	}
}
