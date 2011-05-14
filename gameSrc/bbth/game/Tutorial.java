package bbth.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;

public class Tutorial {

	private float frame;
	private Paint paint;
	private boolean isServer;

	public Tutorial(boolean isServer) {
		this.isServer = isServer;
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setTextSize(16);
	}

	public boolean isFinished() {
		return (frame > 3);
	}

	public void update(float seconds) {
		frame += seconds * 0.2f;
	}

	private void drawArrow(Canvas canvas, float startX, float startY, float endX, float endY) {
		float dx = endX - startX;
		float dy = endY - startY;
		float a = 6;
		float b = 10;
		canvas.drawLine(endX, endY, endX - dx / a - dy / b, endY - dy / a + dx / b, paint);
		canvas.drawLine(endX, endY, endX - dx / a + dy / b, endY - dy / a - dx / b, paint);
		canvas.drawLine(endX - dx / a - dy / b, endY - dy / a + dx / b, endX - dx / a + dy / b, endY - dy / a - dx / b, paint);
		canvas.drawLine(startX, startY, endX, endY, paint);
	}

	public void draw(Canvas canvas) {
		float fraction = frame - (int) frame;
		int alpha = (int) (255 * 4 * (fraction - fraction * fraction));
		paint.setColor(Color.argb(alpha, 255, 255, 255));

		if (frame < 1) {
			paint.setTextAlign(Align.LEFT);
			canvas.drawText("Tap anywhere with the beat ", 80, 100, paint);
			canvas.drawText("to make a unit there!", 80, 130, paint);
		} else if (frame < 2) {
			paint.setTextAlign(Align.LEFT);
			canvas.drawText("Drag finger during ", 80, 100, paint);
			canvas.drawText("long notes for walls", 80, 130, paint);
		} else {
			paint.setTextAlign(Align.CENTER);
			canvas.drawText("Don't let enemies ", 150, 90, paint);
			canvas.drawText("get to your base!", 150, 120, paint);
			if (isServer) {
				drawArrow(canvas, 150, 70, 150, 10);
			} else {
				drawArrow(canvas, 150, 130, 150, 160);
			}
		}
	}

	public void touchDown(float x, float y) {
		if (BBTHGame.DEBUG) {
			// skip the tutorial
			frame = 4;
		}
	}

	public void touchMove(float x, float y) {
	}

	public void touchUp(float x, float y) {
	}
}
