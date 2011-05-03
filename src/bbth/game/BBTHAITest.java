package bbth.game;

import java.util.ArrayList;
import java.util.Random;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.util.FloatMath;
import bbth.core.GameScreen;
import bbth.util.MathUtils;

public class BBTHAITest extends GameScreen {
	
	ArrayList<Unit> m_entities;
	
	private Paint m_paint_0;
	private Paint m_paint_1;
	private Random m_rand;
	private BBTHGame m_parent;
	
	//******** SETUP FOR AI *******//
	private AIController m_controller;
	//******** SETUP FOR AI *******//
	
	long m_last_time = 0;
	
	public BBTHAITest(BBTHGame bbthGame) {
		m_parent = bbthGame;
		
		//******** SETUP FOR AI *******//
		m_controller = new AIController();
		//******** SETUP FOR AI *******//

		m_last_time = System.currentTimeMillis();
		
		m_paint_0 = new Paint();
		m_paint_0.setColor(Color.BLUE);
		m_paint_0.setStrokeWidth(2.0f);
		m_paint_0.setStrokeJoin(Join.ROUND);
		m_paint_0.setStyle(Style.STROKE);
		m_paint_0.setTextSize(20);
		m_paint_0.setAntiAlias(true);
		
		m_paint_1 = new Paint();
		m_paint_1.setColor(Color.RED);
		m_paint_1.setStrokeWidth(2.0f);
		m_paint_1.setStrokeJoin(Join.ROUND);
		m_paint_1.setStyle(Style.STROKE);
		m_paint_1.setTextSize(20);
		m_paint_1.setAntiAlias(true);
        
    	m_entities = new ArrayList<Unit>();
    	m_rand = new Random();
    	
        randomizeEntities();
	}
	
	private void randomizeEntities() {
		for (int i = 0; i < 7; i++) {
			Unit e = new Unit(Team.TEAM_0);
			e.setTeam(Team.TEAM_0);
			e.setPosition(m_rand.nextFloat() * m_parent.getWidth()/4, m_rand.nextFloat() * m_parent.getHeight());
			e.setVelocity(m_rand.nextFloat() * .01f, m_rand.nextFloat() * MathUtils.TWO_PI);
			m_entities.add(e);
			
			//******** SETUP FOR AI *******//
			m_controller.addEntity(e);
			//******** SETUP FOR AI *******//
		}
		
		for (int i = 0; i < 7; i++) {
			Unit e = new Unit(Team.TEAM_1);
			e.setTeam(Team.TEAM_1);
			e.setPosition(m_rand.nextFloat() * m_parent.getWidth()/4 + m_parent.getWidth()*.75f, m_rand.nextFloat() * m_parent.getHeight());
			e.setVelocity(m_rand.nextFloat() * .01f, m_rand.nextFloat() * MathUtils.TWO_PI);
			m_entities.add(e);
			
			//******** SETUP FOR AI *******//
			m_controller.addEntity(e);
			//******** SETUP FOR AI *******//
		}
	}

	@Override
	public void onDraw(Canvas canvas) {
		long curr_time = System.currentTimeMillis();
		long timediff = curr_time - m_last_time;
		
		//******** SETUP FOR AI *******//
		m_controller.update();
		//******** SETUP FOR AI *******//
		
		for (int i = 0; i < m_entities.size(); i++) {
			Unit entity = m_entities.get(i);
			
			//******** PHYSICS AFTER AI *******//
			entity.setPosition(entity.getX() + entity.getSpeed() * FloatMath.cos(entity.getHeading()) * timediff, entity.getY() + entity.getSpeed() * FloatMath.sin(entity.getHeading()) * timediff);
			//******** PHYSICS AFTER AI *******//

			if (entity.getTeam() == Team.TEAM_0) {
				canvas.drawCircle(entity.getX(), entity.getY(), 3, m_paint_0);
			}
			
			if (entity.getTeam() == Team.TEAM_1) {
				canvas.drawCircle(entity.getX(), entity.getY(), 3, m_paint_1);
			}
		}
		
		m_last_time = curr_time;
	}

}
