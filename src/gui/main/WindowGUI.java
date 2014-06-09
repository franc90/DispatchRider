package gui.main;

import gui.commissions.CommissionTableModel;
import gui.holon.HolonTableModel;
import gui.holonstats.HolonStatsTableModel;
import gui.map.MapHolder;
import gui.parameters.DRParams;
import gui.parameters.ParametersTableModel;
import gui.path.PathTableModel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import xml.elements.SimmulationData;
import dtp.graph.Graph;
import dtp.simmulation.SimInfo;


/**
 * 
 * @author Jakub Tyrcha, Tomasz Put
 * 
 * Podstawowa klasa modulu GUI, reprezentujaca glowne okno,
 * spinajaca wszystkie zakladki, przekazujaca im dane do wyswietlenia,
 * obslugujaca slider czasowy
 *
 */
public class WindowGUI implements ChangeListener, ActionListener {
	protected JFrame frame;
	private JTabbedPane tabbedPane;
	
	private JSlider timestampSlider;
	private JPanel mainPane, mapPanel, holonPanel, commissionPanel, holonStatsPanel, paramsPanel, pathPanel;

	private JTable holonTable, commissionTable, holonStatsTable, paramsTable, pathTable;
	JComboBox holonList;
	
	private MapHolder mapHolder;
	
	private Vector<Integer> timestamps = new Vector<Integer>();
	
	/**
	 * wyswietlanie wartosci zgodnie z ustawieniem na sliderze
	 */
	public void stateChanged(ChangeEvent e) {
		
		//int val = Arrays.binarySearch(timestamps.toArray(), timestampSlider.getValue());
		if(timestamps.size()==0) return;
		
		int i = 1;
		while(i < timestamps.size() && timestamps.get(i)<=timestampSlider.getValue())
			i++;
		int val = timestamps.get(i-1);
		timestampSlider.setValue(val);
		((HolonTableModel)holonTable.getModel()).setDrawnTimestamp(val);
		((CommissionTableModel)commissionTable.getModel()).setDrawnTimestamp(val);
		((HolonStatsTableModel)holonStatsTable.getModel()).setDrawnTimestamp(val);
		((PathTableModel)pathTable.getModel()).setDrawnTimestamp(val);
		mapHolder.setDrawnTimestamp(val);
	}
	
	private void resetList() {
		if(((PathTableModel)pathTable.getModel()).getHolonIds() == null) return;
		if(holonList.getItemCount() == ((PathTableModel)pathTable.getModel()).getHolonIds().size()) return;
		holonList.removeAllItems();
		for(Integer i : ((PathTableModel)pathTable.getModel()).getHolonIds()){
			holonList.addItem(i);
		}
	}
	
	
	/**
	 * poprawne wyswietlanie slidera
	 */
	private void regenerateSlider() {
        timestampSlider.setMinimum(timestamps.firstElement());
        timestampSlider.setMaximum(timestamps.lastElement());
        timestampSlider.setPaintTicks(true);
        timestampSlider.setPaintLabels(true);
        timestampSlider.setSnapToTicks(true);
        Hashtable<Integer, JLabel> table = new Hashtable<Integer, JLabel>();
        table.put(timestamps.firstElement(), new JLabel("" + timestamps.firstElement()));
        int currentVal = ((HolonTableModel)holonTable.getModel()).getDrawnTimestamp();
        table.put(currentVal, new JLabel("" + currentVal));
        table.put(timestamps.get(Math.max(0,timestamps.size() - 2)), new JLabel("" + timestamps.get(Math.max(0,timestamps.size() - 2))));
        timestampSlider.setLabelTable(table);
	}
	
	protected WindowGUI() {
		
		mainPane = new JPanel();
		tabbedPane = new JTabbedPane();
		frame = new JFrame("Dispatch Rider");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        
        //slider
        timestampSlider = new JSlider();
		timestampSlider.addChangeListener(this);
        
        // mapa
        mapPanel = new JPanel();
        mapPanel.setLayout(new BorderLayout());
        mapPanel.setPreferredSize(new Dimension(900, 600));
        mapHolder = new MapHolder();
        
        // tabela holonów
        holonPanel = new JPanel();
        holonPanel.setLayout(new BorderLayout());
        holonTable = new JTable(new HolonTableModel());
        holonTable.setAutoCreateRowSorter(true);
        holonTable.setFillsViewportHeight(true);
        holonPanel.add(new JScrollPane(holonTable),BorderLayout.CENTER);
        
        // trasy
        pathPanel = new JPanel();
        pathPanel.setLayout(new BorderLayout());
        pathTable = new JTable(new PathTableModel());
        pathTable.setAutoCreateRowSorter(true);
        pathTable.setFillsViewportHeight(true);
        pathPanel.add(new JScrollPane(pathTable),BorderLayout.CENTER);
        holonList = new JComboBox();
        holonList.addActionListener(this);
        pathPanel.add(holonList,BorderLayout.NORTH);
        
        //tabela zlecen
        commissionPanel=new JPanel();
        commissionPanel.setLayout(new BorderLayout());
        commissionTable=new JTable(new CommissionTableModel());
        commissionTable.setAutoCreateRowSorter(true);
        commissionTable.setFillsViewportHeight(true);
        commissionPanel.add(new JScrollPane(commissionTable),BorderLayout.CENTER);
        
        // tabela statystyk holonów
        holonStatsPanel = new JPanel();
        holonStatsPanel.setLayout(new BorderLayout());
        holonStatsTable = new JTable(new HolonStatsTableModel());
        holonStatsTable.setAutoCreateRowSorter(true);
        holonStatsTable.setFillsViewportHeight(true);
        holonStatsPanel.add(new JScrollPane(holonStatsTable),BorderLayout.CENTER);
        
        // parametry
        paramsPanel = new JPanel();
        paramsPanel.setLayout(new BorderLayout());
        paramsTable = new JTable(new ParametersTableModel());
        paramsTable.setAutoCreateRowSorter(true);
        paramsTable.setFillsViewportHeight(true);
        paramsPanel.add(new JScrollPane(paramsTable),BorderLayout.CENTER);
        
        // ustawienie zak³adek
		tabbedPane.addTab("Mapa",mapPanel);
		tabbedPane.addTab("Holony",holonPanel);
		tabbedPane.addTab("Trasy",pathPanel);
		tabbedPane.addTab("Zlecenia", commissionPanel);
		tabbedPane.addTab("Statystyki holonów",holonStatsPanel);
		tabbedPane.addTab("Parametry algorytmu",paramsPanel);
		
		frame.add(mainPane);
		mainPane.setLayout(new BoxLayout(mainPane,BoxLayout.Y_AXIS));
		mainPane.add(tabbedPane);
		mainPane.add(timestampSlider);
		

		frame.pack(); 
        frame.setVisible(true);
        
	}
	
	
	/**
	 * Aktualizacja gui o przychodzace dane
	 * @param data
	 */
	public void update(SimmulationData data) {
		if(data == null) {
			return;
		}
		HolonTableModel model = (HolonTableModel) holonTable.getModel();
		model.update(data);
		holonTable.repaint();

		((CommissionTableModel) commissionTable.getModel()).update(data);
		commissionTable.repaint();
		
		((HolonStatsTableModel)holonStatsTable.getModel()).update(data);
		holonStatsTable.repaint();
		
		((PathTableModel)pathTable.getModel()).update(data);
		pathTable.repaint();
		
		mapHolder.update(data);
	}
	
	/**
	 * Metoda wywolywana z kazdym nowym timestamp symulacji,
	 * informuje o nadejsciu nowego kroku czasowego
	 * @param val
	 */
	public void newTimestamp(int val) {
		HolonTableModel model = (HolonTableModel) holonTable.getModel();
		model.newTimestampUpdate(val);
		mapHolder.newTimestampUpdate(val);
		
		timestamps.add(val);
		timestampSlider.setValue(model.getDrawnTimestamp());
		mapHolder.repaint();
		holonTable.repaint();
		
		((CommissionTableModel) commissionTable.getModel()).newTimestampUpdate(val);
		commissionTable.repaint();
		
		((HolonStatsTableModel)holonStatsTable.getModel()).newTimestampUpdate(val);
		holonStatsTable.repaint();
		
		((PathTableModel)pathTable.getModel()).newTimestampUpdate(val);
		pathTable.repaint();
		
		regenerateSlider();
		resetList();
	}
	
	/**
	 * Metoda wywolywana w celu przekazaniu do GUI grafu do wyswietlenia
	 * @param graph
	 */
	public void update(Graph graph) {
		if(graph == null) {
			System.out.println("null w grafie");
			return;
		}
	}
	
	/**
	 * Metoda sluzaca do przekazania do gui informacji o parametrach algorytmu
	 * @param params
	 */
	public void update(DRParams params) {
		ParametersTableModel model = (ParametersTableModel) paramsTable.getModel();
		model.update(params);
	}
	
	/**
	 * Metoda wywolywana tylko raz, majaca na celu przekazanie do gui danych z poczatku symulacji
	 * @param info
	 */
	public void update(SimInfo info) {
		if(info == null) {
			System.out.println("null w siminfo");
			return;
		}
		try{
			HolonTableModel model = (HolonTableModel) holonTable.getModel();
			model.setSimInfo(info);
			mapHolder.setSimInfo(info);
			mapPanel.add(mapHolder.getMap(), BorderLayout.CENTER);
			((HolonStatsTableModel)holonStatsTable.getModel()).setSimInfo(info);
		}
		catch(Exception e){
			e.printStackTrace();
		}

	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		JComboBox cb = (JComboBox)e.getSource();
		if(cb.getSelectedItem()==null) return;
		int holonId = (Integer)cb.getSelectedItem();
		((PathTableModel)pathTable.getModel()).setHolon(holonId);
	}
	
}
