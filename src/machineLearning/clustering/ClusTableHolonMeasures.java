package machineLearning.clustering;

import jade.core.AID;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import machineLearning.aggregator.AggregatorsManager;
import measure.Measure;

public class ClusTableHolonMeasures extends ClusTableMeasures {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2706935516346509154L;
	private AggregatorsManager aggregatorManager = new AggregatorsManager();

	@Override
	public Map<String, Double> getCurrentMeasuresVector(
			Map<String, Measure> measures, AID aid) {
		aggregatorManager.setMeasures(measures);
		Map<String, Double> result = new TreeMap<String, Double>();

		String sValue = null;
		for (String measure : this.values.keySet()) {
			sValue = aggregatorManager.insertAggregateValues(this.values
					.get(measure));
			sValue = insertHolonMeasures(sValue, measures, aid);
			result.put(measure, Double.valueOf(sValue));
		}
		aggregatorManager.aggregationFinished();

		return result;
	}

	private String insertHolonMeasures(String fun,
			Map<String, Measure> measures, AID aid) {
		
		for (String measure : measures.keySet()) {
			if (measures.get(measure).getValues().get(aid.getLocalName()) != null) {
				fun = fun.replace(measure, measures.get(measure).getValues()
						.get(aid.getLocalName()).toString());
			}
		}

		return fun;
	}

	public static void main(String[] args) {
		ClusTableHolonMeasures mes = new ClusTableHolonMeasures();
		mes.addMeasure("M1", "WaitTime");
		Map<String, Measure> measures = new HashMap<String, Measure>();

		Measure m1 = new Measure();
		m1.put("a1", 12.0);
		m1.put("a2", 8.0);

		measures.put("WaitTime", m1);

		System.out.println(mes.getCurrentMeasuresVector(measures, new AID("a2",
				true)));

	}

}
