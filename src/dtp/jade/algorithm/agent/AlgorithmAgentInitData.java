package dtp.jade.algorithm.agent;

import java.io.Serializable;
import java.util.Map;

public class AlgorithmAgentInitData implements Serializable {

	private static final long serialVersionUID = 9197388279480107216L;
	private String agentName;
	private Class<?> agentClass;
	private Map<String, String> initParams;

	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	public Class<?> getAgentClass() {
		return agentClass;
	}

	public void setAgentClass(Class<?> agentClass) {
		this.agentClass = agentClass;
	}

	public Map<String, String> getInitParams() {
		return initParams;
	}

	public void setInitParams(Map<String, String> initParams) {
		this.initParams = initParams;
	}

}
