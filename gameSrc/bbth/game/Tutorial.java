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
		paint.setTextSize(12);
	}

	public boolean isFinished() {
		return (frame > 5);
	}

	public void update(float seconds) {
		frame += seconds * 0.25f;
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
			canvas.drawText("Tap with beat to make a unit", 80, 100, paint);
			drawArrow(canvas, 80, 100, BeatTrack.BEAT_LINE_X + BeatTrack.BEAT_CIRCLE_RADIUS, BeatTrack.BEAT_LINE_Y - BeatTrack.BEAT_CIRCLE_RADIUS);
		} else if (frame < 2) {
			paint.setTextAlign(Align.LEFT);
			canvas.drawText("Hold long notes for walls", 80, 100, paint);
			drawArrow(canvas, 80, 100, BeatTrack.BEAT_LINE_X + BeatTrack.BEAT_CIRCLE_RADIUS, BeatTrack.BEAT_LINE_Y - BeatTrack.BEAT_CIRCLE_RADIUS);
		} else if (frame < 3) {
			paint.setTextAlign(Align.RIGHT);
			canvas.drawText("Switch between attacking and defending", 260, 50, paint);
			drawArrow(canvas, 270, 45, 300, 40);
		} else if (frame < 4) {
			paint.setTextAlign(Align.RIGHT);
			canvas.drawText("Scroll around with the minimap", 240, 100, paint);
			drawArrow(canvas, 240, 100, 300, 130);
		} else {
			paint.setTextAlign(Align.CENTER);
			canvas.drawText("Don't let enemies get to your base!", 150, 90, paint);
			if (isServer) {
				drawArrow(canvas, 150, 70, 150, 10);
			} else {
				drawArrow(canvas, 150, 100, 150, 160);
			}
		}
	}

	public void touchDown(float x, float y) {
		// skip the tutorial
		frame = 5;
	}

	public void touchMove(float x, float y) {
	}

	public void touchUp(float x, float y) {
	}
}
