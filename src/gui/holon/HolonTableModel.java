package gui.holon;


import gui.common.Updateable;

import java.util.HashMap;

import javax.swing.table.AbstractTableModel;

import xml.elements.SimmulationData;
import dtp.simmulation.SimInfo;

public class HolonTableModel extends AbstractTableModel implements Updateable{

	private static final long serialVersionUID = 1L;
	
	HolonUpdateable updateable = new HolonUpdateable();


	@SuppressWarnings("unchecked")
	@Override
	public int getRowCount() {
		if(updateable.visualisedRecord.getData() == null) return 0;
		return ((HashMap<Object,Object>)updateable.visualisedRecord.getData()).size();
	}

	@Override
	public int getColumnCount() {
		return updateable.getColumnNames().length;
	}
	
	public String getColumnName(int col) {
        return updateable.getColumnNames()[col];
    }

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if(updateable.visualisedRecord.getData() == null) return 0;
		@SuppressWarnings("unchecked")
		HashMap<Integer,HashMap<String,Object>> extracted = (HashMap<Integer, HashMap<String, Object>>) updateable.visualisedRecord.getData();
		return extracted.get((Integer) updateable.getHolonIds().toArray()[rowIndex]).get(updateable.getColumnNames()[columnIndex]);
	}
	
	public void setSimInfo(SimInfo simInfo) {
		updateable.setSimInfo(simInfo);
	}
	
	/**
	 * Do aktualizowania danych w tabelach
	 * @param data
	 * @param simInfo
	 */
	public void update(SimmulationData data) {
		updateable.update(data);
	}
	
	/**
	 * Wskazuje ze update'y przychodza juz na nowy timestamp
	 * @param val
	 */
	public void newTimestampUpdate(int val){
		updateable.newTimestampUpdate(val);
	}
	
	/**
	 * Wyswietla dane z wybranego timestamp
	 * @param val
	 */
	public void setDrawnTimestamp(int val) {
		updateable.setDrawnTimestamp(val);
	}
	
	public int getDrawnTimestamp() {
		return updateable.getDrawnTimestamp();
	}
}
