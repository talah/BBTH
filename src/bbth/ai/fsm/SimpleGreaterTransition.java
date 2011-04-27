package bbth.ai.fsm;

import java.util.HashMap;

public class SimpleGreaterTransition extends StateTransition {
	private String m_input_name;
	private Float m_val;

	public SimpleGreaterTransition(FiniteState start_state, FiniteState end_state) {
		super(start_state, end_state);
	}
	
	public void set_input_name(String inputname) {
		m_input_name = inputname;
	}
	
	public void set_val(float value) {
		m_val = value;
	}
	
	@Override
	public boolean check_conditions(HashMap<String, Float> inputs) {
		if (!inputs.containsKey(m_input_name)) {
			return false;
		}
		return inputs.get(m_input_name) > m_val;
	}

}
