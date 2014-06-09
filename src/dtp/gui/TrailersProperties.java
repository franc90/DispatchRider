package dtp.gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import dtp.jade.transport.TransportElementInitialDataTrailer;

public class TrailersProperties extends JFrame{

	private static final long serialVersionUID = 8814328612490490440L;
	
	public TrailersProperties(SimLogic gui){
		super("Trailer properties");
		
		ArrayList<TransportElementInitialDataTrailer> trailerProperties = gui.getTrailersProperties();
		
		String data[][] = new String[trailerProperties.size()][5];
		int index = 0;
		
		for (TransportElementInitialDataTrailer trailerData : trailerProperties){
			data[index][0] = String.valueOf( trailerData.getMass());
			data[index][1] = String.valueOf( trailerData.getCapacity_() );
			data[index][2] = String.valueOf( trailerData.getCargoType() );
			data[index][3] = String.valueOf( trailerData.getUniversality() );
			data[index][4] = String.valueOf( trailerData.getConnectorType() );
			index++;
		}

		String fields[] = {"Mass [kg]", "Capacity [int]", "Cargo type [int]", "Universality [1-4]", "Connector type [int]"};
		
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
