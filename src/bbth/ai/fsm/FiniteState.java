package bbth.ai.fsm;

import java.util.ArrayList;
import java.util.HashMap;

public class FiniteState {
	private ArrayList<StateTransition> m_transitions;
	private String m_name;
	
	private FSMAction m_action;
	
	public FiniteState(String name) {
		m_name = name;
		m_action = null;
		m_transitions = new ArrayList<StateTransition>();
	}
	
	public FiniteState check_transitions(HashMap<String, Float> inputs) {
		int size = m_transitions.size();
		for (int i = 0; i < size; i++) {
			StateTransition s = m_transitions.get(i);
			if (s.check_conditions(inputs)) {
				return s.get_new_state();
			}
		}
		
		return this;
	}
	
	public void add_transition(StateTransition t) {
		m_transitions.add(t);
	}
	
	public void set_action(FSMAction action) {
		m_action = action;
	}

	public String get_name() {
		return m_name;
	}

	public void apply_action(HashMap<String, Float> inputs) {
		if (m_action == null) {
			return;
		}
		
		m_action.apply(inputs);
	}
}
