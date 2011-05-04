package bbth.game.ai;

import java.util.ArrayList;
import java.util.EnumMap;

import bbth.engine.ai.FlockRulesCalculator;
import bbth.game.BBTHGame;
import bbth.game.Team;
import bbth.game.Unit;

public class AIController {
	EnumMap<Team, ArrayList<Unit>> m_entities;
	EnumMap<Team, FlockRulesCalculator> m_flocks;
	
	DefensiveAI m_aggressive;
	
	private float m_fraction_to_update = 0.33f;
	
	int m_last_updated = 0;

	public AIController() {
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
	
	public void addEntity(Unit u) {
		m_entities.get(u.getTeam()).add(u);
		m_flocks.get(u.getTeam()).addObject(u);
	}
	
	public void removeEntity(Unit u) {
		m_entities.get(u.getTeam()).add(u);
		m_flocks.get(u.getTeam()).addObject(u);
	}
	
	public ArrayList<Unit> getEnemies(Unit u) {
		return m_entities.get(u.getTeam().getOppositeTeam());
	}
	
	public void update() {
		for (Team t : Team.values()) {
			update(m_entities.get(t), m_flocks.get(t));
		}
	}
	
	private void update(ArrayList<Unit> entities, FlockRulesCalculator flock) {
		int size = entities.size();
		int num_to_update = (int) ((size * m_fraction_to_update)+1);
		int i = m_last_updated;
		while (num_to_update > 0) {			
			Unit entity = entities.get(i);
			
			// TODO: Use the correct AI for the individual unit.
			m_aggressive.update(entity, this, flock);
			
			num_to_update--;

			if (i >= size-1) {
				i = 0;
			} else {
				i++;
			}
		}
		
		if (i >= size-1) {
			m_last_updated = 0;
		} else {
			m_last_updated = i+1;
		}
	}

	public float getWidth() {
		return BBTHGame.WIDTH; 
	}
	
	public float getHeight() {
		return BBTHGame.HEIGHT;
	}
	
	public void setUpdateFraction(float fraction) {
		if (fraction < 0) {
			fraction = 0;
		}
		m_fraction_to_update = fraction;
	}
}
