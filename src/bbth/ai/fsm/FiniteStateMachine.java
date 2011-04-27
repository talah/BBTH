package bbth.ai.fsm;

import java.util.HashMap;

public class FiniteStateMachine {

	private HashMap<String, FiniteState> m_states;

	private FiniteState m_current_state;
	
	public FiniteStateMachine() {
		m_states = new HashMap<String, FiniteState>();
		m_current_state = null;
	}
	
	public void add_state(String name) {
		FiniteState s = new FiniteState(name);
		m_states.put(name, s);
		if (m_current_state == null) {
			m_current_state = s;
		}
	}
	
	public String get_state_name() {
		return m_current_state.get_name();
	}
	
	public FiniteState get_state_by_name(String name) {
		return m_states.get(name);
	}
	
	public FiniteState get_curr_state() {
		return m_current_state;
	}
	
	public boolean update(HashMap<String, Float> inputs) {
		if (m_current_state == null) {
			return false;
		}
		FiniteState new_state = m_current_state.check_transitions(inputs);
		if (new_state != m_current_state) {
			m_current_state = new_state;
			return true;
		}
		return false;
	}
	
	public void apply_action(HashMap<String, Float> inputs) {
		if (m_current_state == null) {
			return;
		}
		
		m_current_state.apply_action(inputs);
	}
}
