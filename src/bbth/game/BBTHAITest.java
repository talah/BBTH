package bbth.game;

import java.util.ArrayList;
import java.util.Random;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.util.FloatMath;
import bbth.ai.FlockRulesCalculator;
import bbth.core.GameScreen;
import bbth.entity.*;
import bbth.util.MathUtils;

public class BBTHAITest extends GameScreen {
	ArrayList<Movable> m_entities;
	ArrayList<PointF> m_entity_wanted;
	private Paint m_paint;
	private Random m_rand;
	private BBTHGame m_parent;
	private FlockRulesCalculator m_flock;
	
	long m_last_time = 0;
	
	private PointF m_result;
	private float max_vel_change = 0.3f;
	
	public BBTHAITest(BBTHGame bbthGame) {
		m_parent = bbthGame;
		
		m_flock = new FlockRulesCalculator();
		
		m_last_time = System.currentTimeMillis();
		
		m_paint = new Paint();
		m_paint.setColor(Color.WHITE);
        m_paint.setStrokeWidth(2.0f);
        m_paint.setStrokeJoin(Join.ROUND);
        m_paint.setStyle(Style.STROKE);
        m_paint.setTextSize(20);
        m_paint.setAntiAlias(true);
        
    	m_entities = new ArrayList<Movable>();
    	m_entity_wanted = new ArrayList<PointF>();
    	m_rand = new Random();
    	
    	m_result = new PointF();
    	
        randomize_entities();
	}
	
	private void randomize_entities() {
		for (int i = 0; i < 15; i++) {
			BasicMovable e = new BasicMovable();
			e.set_position(m_rand.nextFloat() * m_parent.getWidth(), m_rand.nextFloat() * m_parent.getHeight());
			e.set_velocity(m_rand.nextFloat() * .01f, m_rand.nextFloat() * MathUtils.TWO_PI);
			m_entities.add(e);
			
			m_entity_wanted.add(new PointF());
			
			m_flock.add_object(e);
		}
		
	}

	@Override
	public void onDraw(Canvas canvas) {
		long curr_time = System.currentTimeMillis();
		long timediff = curr_time - m_last_time;
		
		for (int i = 0; i < m_entities.size(); i++) {
			float angle = 0;
			float xcomp = 0;
			float ycomp = 0;
			
			Movable entity = m_entities.get(i);
			
			// Calculate flocking.
			m_flock.get_cohesion_component(entity, m_result);
			
			xcomp = m_result.x * .2f / m_parent.getWidth();
			ycomp = m_result.y * .2f / m_parent.getWidth();
			
			m_flock.get_alignment_component(entity, m_result);
			
			xcomp += m_result.x;
			ycomp += m_result.y;
			
			m_flock.get_separation_component(entity, 20.0f, m_result);
			
			xcomp += m_result.x;
			ycomp += m_result.y;
			
			if (m_rand.nextInt(100) == 0 || (m_entity_wanted.get(i).x == 0 && m_entity_wanted.get(i).y == 0)) {
				float desired_x = m_rand.nextInt((int) m_parent.getWidth());
				float desired_y = m_rand.nextInt((int) m_parent.getHeight());
				m_entity_wanted.get(i).set(desired_x, desired_y);
			}
			
			xcomp /= 3;
			ycomp /= 3;
			
			// Calculate somewhere to go if we're a leader.
			if (!m_flock.has_leader(entity)) {
				angle = MathUtils.get_angle(entity.get_x(), entity.get_y(), m_entity_wanted.get(i).x, m_entity_wanted.get(i).y);
				xcomp += 0.05f * FloatMath.cos(angle);
				ycomp += 0.05f * FloatMath.sin(angle);
			}
			
			//float power = MathUtils.get_dist(0, 0, xcomp, ycomp);
			float wanteddir = MathUtils.get_angle(0, 0, xcomp, ycomp);
			
			float wantedchange = MathUtils.normalize_angle(wanteddir, entity.get_heading()) - entity.get_heading();
			
			float actualchange = wantedchange;
			if (actualchange > max_vel_change) {
				actualchange = max_vel_change;
			}
			
			if (actualchange < -1.0f * max_vel_change) {
				actualchange = -1.0f * max_vel_change;
			}
			
			entity.set_velocity(0.05f, entity.get_heading() + actualchange);
			
			entity.set_position(entity.get_x() + entity.get_speed() * FloatMath.cos(entity.get_heading()) * timediff, entity.get_y() + entity.get_speed() * FloatMath.sin(entity.get_heading()) * timediff);
			
			canvas.drawCircle(entity.get_x(), entity.get_y(), 3, m_paint);
		}
		
		m_last_time = curr_time;
	}

}
