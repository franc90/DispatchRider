package dtp.gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import dtp.jade.transport.TransportElementInitialDataTruck;

public class TruckProperties extends JFrame{

	private static final long serialVersionUID = 8814328612490490440L;
	
	public TruckProperties(SimLogic gui){
		super("Truck properties");
		
		ArrayList<TransportElementInitialDataTruck> truckProperties = gui.getTrucksProperties();
		
		String data[][] = new String[truckProperties.size()][5];
		int index = 0;
		for (TransportElementInitialDataTruck truckData : truckProperties){
			data[index][0] = String.valueOf( truckData.getPower() );
			data[index][1] = String.valueOf( truckData.getReliability() );
			data[index][2] = String.valueOf( truckData.getComfort() );
			data[index][3] = String.valueOf( truckData.getFuelConsumption() );
			data[index][4] = String.valueOf( truckData.getConnectorType() );
			index++;
		}

		String fields[] = {"Power [BHP]", "Reliability [1-4]", "Comfort [1-4]", "Fuel consumption [l/100km]", "Connector type [int]"};
		
		JTable jTable = new JTable( data, fields );
	    JScrollPane pane = new JScrollPane( jTable );
	    getContentPane().add( pane );

        addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent we ) {
              dispose();
            }
          } );
        setSize(800,600);
        pack();
		setVisible(true);
	}
	
}
