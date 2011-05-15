package bbth.engine.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import bbth.game.BBTHActivity;

public class UIImageView extends UIView {
	
	private Bitmap _image;
	private Paint _paint;
	
	public UIImageView(int id, Object tag)
	{
		super(tag);
		
		_paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
		
		Options options = new Options();
		options.inTargetDensity = DisplayMetrics.DENSITY_DEFAULT;
		_image = BitmapFactory.decodeResource(BBTHActivity.instance.getResources(), id);
	}
	
	public UIImageView(int id)
	{
		this(id, null);
	}
	
	@Override
	public void setBounds(float left, float top, float right, float bottom) {
		super.setBounds(left, top, right, bottom);
		if(_width > 0 && _height > 0)
			_image = Bitmap.createScaledBitmap(_image, (int)_width, (int)_height, true);
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		canvas.drawBitmap(_image, _rect.left, _rect.top, _paint);
	}

}
