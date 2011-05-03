package bbth.game;

import java.util.ArrayList;
import java.util.EnumMap;

import bbth.ai.FlockRulesCalculator;

public class AIController {
	EnumMap<Team, ArrayList<Unit>> m_entities;
	EnumMap<Team, FlockRulesCalculator> m_flocks;
	
	DefensiveAI m_aggressive;

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
		for (int i = 0; i < entities.size(); i++) {
			
			Unit entity = entities.get(i);
			
			// TODO: Use the correct AI for the individual unit.
			m_aggressive.update(entity, this, flock);
		}
	}

	public float getWidth() {
		return BBTHGame.WIDTH; 
	}
	
	public float getHeight() {
		return BBTHGame.HEIGHT;
	}
}
