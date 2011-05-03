package bbth.game;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Random;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.util.FloatMath;
import bbth.ai.FlockRulesCalculator;
import bbth.entity.Movable;
import bbth.game.Unit.Team;
import bbth.util.MathUtils;

public class AIController {
	EnumMap<Team, ArrayList<Unit>> m_entities;
	private BBTHGame m_parent;
	EnumMap<Team, FlockRulesCalculator> m_flocks;
	
	DefensiveAI m_aggressive;
	
		
	public AIController(BBTHGame bbthGame) {
		m_parent = bbthGame;
		
		m_aggressive = new DefensiveAI();
		
		m_flocks = new EnumMap<Team, FlockRulesCalculator>(Team.class);
		
		for (Team t : Team.values()) {
			m_flocks.put(t, new FlockRulesCalculator());
		}
        
    	m_entities = new EnumMap<Team, ArrayList<Unit>>(Team.class);
    	
    	for (Team t : Team.values()) {
			m_entities.put(t, new ArrayList<Unit>());
		}
   	}
	
	public void add_entity(Unit u) {
		m_entities.get(u.getTeam()).add(u);
		m_flocks.get(u.getTeam()).add_object(u);
	}
	
	public void remove_entity(Unit u) {
		m_entities.get(u.getTeam()).add(u);
		m_flocks.get(u.getTeam()).add_object(u);
	}
	
	public ArrayList<Unit> get_enemies(Unit u) {
		return m_entities.get(u.getTeam().get_opposite_team());
	}
	
	public void update() {
		for (Team t : Team.values()) {
			update(m_entities.get(t), m_flocks.get(t));
		}
	}
	
	private void update(ArrayList<Unit> entities, FlockRulesCalculator flock) {
		for (int i = 0; i < entities.size(); i++) {
			
			Unit entity = entities.get(i);
			
			// TODO: Use the correct AI for the individual unit.
			m_aggressive.update(entity, this, flock);
		}
	}

	public float getWidth() {
		return m_parent.getWidth();
	}
	
	public float getHeight() {
		return m_parent.getHeight();
	}
}
