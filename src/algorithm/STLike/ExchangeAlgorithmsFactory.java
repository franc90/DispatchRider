package algorithm.STLike;

import java.io.Serializable;
import java.util.Map;

public class ExchangeAlgorithmsFactory implements Serializable {

	private static final long serialVersionUID = -7769195943408920804L;
	private ExchangeAlgorithm algAfterComAdd;
	private ExchangeAlgorithm algWhenCantAdd;

	private final String packageName = "algorithm.STLike.";

	private ExchangeAlgorithm getAlgorithm(String name,
			Map<String, String> params) {
		try {
			Class<?> algClass = Class.forName(packageName + name);
			ExchangeAlgorithm alg = (ExchangeAlgorithm) algClass.newInstance();
			alg.setParameters(params);
			return alg;
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}

	public void setAlgAfterComAdd(String name, Map<String, String> params) {
		this.algAfterComAdd = getAlgorithm(name, params);
	}

	public void setAlgWhenCantAdd(String name, Map<String, String> params) {
		this.algWhenCantAdd = getAlgorithm(name, params);
	}

	public ExchangeAlgorithm getAlgAfterComAdd() {
		return algAfterComAdd;
	}

	public ExchangeAlgorithm getAlgWhenCantAdd() {
		return algWhenCantAdd;
	}

}
