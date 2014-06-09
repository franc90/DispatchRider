package measure.configuration;

import jade.core.AID;

import java.util.Map;

import measure.printer.MeasureData;

public class MeasureConfigurationChangerImpl implements
		MeasureConfigurationChanger {

	@SuppressWarnings("unused")
	private MeasureData data;

	@Override
	public void setMeasureData(MeasureData data) {
		this.data = data;
	}

	@Override
	public Map<AID, HolonConfiguration> getNewHolonsConfigurations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GlobalConfiguration getNewGlobalConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}
}
