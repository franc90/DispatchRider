package dtp.gui;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListModel;

import dtp.commission.Commission;
import dtp.commission.CommissionHandler;
import dtp.commission.TxtFileReader;
import dtp.jade.ProblemType;
import dtp.jade.gui.GUIAgent;
import dtp.simmulation.SimInfo;
import dtp.util.DirectoriesResolver;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI Builder, which is free for non-commercial
 * use. If Jigloo is being used commercially (ie, by a corporation, company or business for any purpose whatever) then
 * you should purchase a license for each developer using Jigloo. Please visit www.cloudgarden.com for details. Use of
 * Jigloo implies acceptance of these licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR THIS MACHINE, SO
 * JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
/**
 * @author kony.pl
 */
public class CommissionsTab extends JPanel {

    private static final long serialVersionUID = -5288686200465270880L;

    private SimLogic gui;

    private GUIAgent guiAgent;

    // //////// GUI components //////////

    private JCheckBox checkBoxMarkSentCommisssions;
    
    private JCheckBox checkBoxIsProblemDynamic;

    private JList listCommissions;
    
    private JScrollPane listCommissionsWrapper;

    private JButton buttonAddSingleCommission;

    private JButton buttonAddCommissionsGroupTxt;

    private JButton buttonEditCommission;

    private JButton buttonRemoveCommission;
    
    private JButton buttonTruckProperties;
    
    private JButton buttonTrailersProperties;

    // Constrains

    private JLabel labelDepot;

    private JLabel labelDeadline;

    private JLabel labelMaxLoad;

    private JTextField textDepotX;

    private JTextField textDepotY;

    private JTextField textDeadline;

    private JTextField textMaxLoad;

    private JButton buttonSetConstrains;

    public CommissionsTab(SimLogic gui, GUIAgent guiAgent) {

        this.gui = gui;

        this.guiAgent = guiAgent;

        initGui();
    }

    private void initGui() {

        setLayout(null);
        this.setPreferredSize(new java.awt.Dimension(945, 623));

        checkBoxMarkSentCommisssions = new JCheckBox();
        add(checkBoxMarkSentCommisssions);
        checkBoxMarkSentCommisssions.setText("Show sent commissions");
        checkBoxMarkSentCommisssions.setBounds(7, 150, 133, 21);
        checkBoxMarkSentCommisssions.setSelected(false);
        checkBoxMarkSentCommisssions.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                checkBoxMarkSentCommissionsActionPerformed(evt);
            }
        });

        listCommissions = new JList();
        listCommissionsWrapper = new JScrollPane(listCommissions);
        add(listCommissionsWrapper);
        
        listCommissions.setBounds(0, 0, 732, 567);
        listCommissionsWrapper.setBounds(147, 7, 732, 567);
        listCommissions.setToolTipText("Zawiera list\u0119 zlece\u0144 maj\u0105cych"
                + " pojawi\u0107 si\u0119 w systemie w trakcie symulacji");

        buttonAddSingleCommission = new JButton();
        add(buttonAddSingleCommission);
        buttonAddSingleCommission.setText("Add commission");
        buttonAddSingleCommission.setBounds(7, 14, 133, 21);
        buttonAddSingleCommission.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                buttonAddSingleCommissionActionPerformed(evt);
            }
        });

        buttonAddCommissionsGroupTxt = new JButton();
        add(buttonAddCommissionsGroupTxt);
        buttonAddCommissionsGroupTxt.setText("Add coms group");
        buttonAddCommissionsGroupTxt.setBounds(7, 42, 133, 21);
        buttonAddCommissionsGroupTxt.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                buttonAddCommissionsGroupTxtActionPerformed(evt);
            }
        });
        
        checkBoxIsProblemDynamic = new JCheckBox();
        add(checkBoxIsProblemDynamic);
        checkBoxIsProblemDynamic.setText("Dynamic problem");
        checkBoxIsProblemDynamic.setBounds(17, 68, 133, 21);
        checkBoxIsProblemDynamic.setSelected(false);
        
        

        buttonEditCommission = new JButton();
        add(buttonEditCommission);
        buttonEditCommission.setText("Edit");
        buttonEditCommission.setBounds(7, 94, 133, 21);
        buttonEditCommission.setToolTipText("Modyfikuje wybrane zlecenie");
        buttonEditCommission.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                buttonEditCommissionActionPerformed(evt);
            }
        });

        buttonRemoveCommission = new JButton();
        add(buttonRemoveCommission);
        buttonRemoveCommission.setText("Remove");
        buttonRemoveCommission.setBounds(7, 122, 133, 21);
        buttonRemoveCommission.setToolTipText("Usuwa wybrane zlecenie");
        buttonRemoveCommission.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                buttonRemoveCommissionActionPerformed(evt);
            }
        });
        
        
        buttonTruckProperties = new JButton();
        add(buttonTruckProperties);
        buttonTruckProperties.setText("Truck properties");
        buttonTruckProperties.setBounds(7, 422, 133, 21);
        buttonTruckProperties.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
            	buttonTruckPropertiesActionPerformed();
            }
        });
        
        
        buttonTrailersProperties = new JButton();
        add(buttonTrailersProperties);
        buttonTrailersProperties.setText("Trailers properties");
        buttonTrailersProperties.setBounds(7, 450, 133, 21);
        buttonTrailersProperties.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
            	buttonTrailersPropertiesActionPerformed();
            }
        });

        labelDepot = new JLabel();
        this.add(labelDepot);
        labelDepot.setText("Depot: (x, y)");
        labelDepot.setBounds(21, 168, 63, 28);

        textDepotX = new JTextField();
        this.add(textDepotX);
        textDepotX.setText("0");
        textDepotX.setBounds(21, 196, 42, 21);

        textDepotY = new JTextField();
        this.add(textDepotY);
        textDepotY.setText("0");
        textDepotY.setBounds(70, 196, 42, 21);

        labelDeadline = new JLabel();
        this.add(labelDeadline);
        labelDeadline.setText("Deadline:");
        labelDeadline.setBounds(21, 217, 63, 28);

        textDeadline = new JTextField();
        this.add(textDeadline);
        textDeadline.setText("1500");
        textDeadline.setBounds(21, 245, 91, 21);

        labelMaxLoad = new JLabel();
        this.add(labelMaxLoad);
        labelMaxLoad.setText("Capactiy:");
        labelMaxLoad.setBounds(21, 266, 63, 28);

        textMaxLoad = new JTextField();
        this.add(textMaxLoad);
        textMaxLoad.setText("200");
        textMaxLoad.setBounds(21, 294, 91, 21);

        buttonSetConstrains = new JButton();
        this.add(buttonSetConstrains);
        buttonSetConstrains.setText("Set constrains");
        buttonSetConstrains.setBounds(7, 322, 133, 21);
        buttonSetConstrains.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                buttonSetConstrainsActionPerformed(evt);
            }
        });
    }

    protected void buttonTrailersPropertiesActionPerformed() {
    	new TrailersProperties(gui);
	}

	protected void buttonTruckPropertiesActionPerformed() {
		new TruckProperties(gui);
	}

	private void checkBoxMarkSentCommissionsActionPerformed(ActionEvent evt) {

        if (checkBoxMarkSentCommisssions.isSelected()) {

            listCommissions.setSelectionBackground(java.awt.Color.GREEN.darker());
            markSentCommissions();

        } else {

            listCommissions.setSelectionBackground(java.awt.Color.BLUE.darker());
            listCommissions.clearSelection();
        }
    }

    private void buttonAddSingleCommissionActionPerformed(ActionEvent evt) {

        new AddEditCommission(this);
    }
    
    public void addCommisionGroup(String filename, boolean dynamic) {
        Commission[] commissions = TxtFileReader.getCommissions(filename);

        gui.displayMessage("GUI - commissions read from .txt file [" + filename + "]");

        int incomeTime[] = new int[commissions.length];
        if (dynamic){
        	
        	incomeTime = TxtFileReader.getIncomeTimes(filename +".income_times", commissions.length);
        	if (incomeTime == null){
        		gui.displayMessage("GUI - error reading commission's income times");
        		return;
        	}
        }

        double farthestPickupLocation = TxtFileReader.getFarthestPickupLocation(filename);
        int farthestPickupLocation2int = (int) farthestPickupLocation;

        gui.displayMessage("GUI - distance from depot to farthest pickup location = " + farthestPickupLocation + " ("
                + farthestPickupLocation2int + ")");

        // Random rand = new Random(System.nanoTime());

        for (int i = 0; i < commissions.length; i++) {

            // Commission tempCom;
            // int tempPickupTime1;
            //
            // tempCom = commissions[i];
            // tempPickupTime1 = tempCom.getPickupTime1();
            // if (tempPickupTime1 >= farthestPickupLocation2int) {
            //			
            // incomeTime = tempPickupTime1 - farthestPickupLocation2int;
            //			
            // } else {
            //			
            // incomeTime = 0;
            // // incomeTime = rand.nextInt(10);
            // }

//            incomeTime = 0;

            // incomeTime = commissions[i].getID() % 10;

            addCommissionHandler(new CommissionHandler(commissions[i], incomeTime[i]));
        }

        gui.refreshComsWaiting();

        // set sim constrins read from .txt file
        textDepotX.setText(String.valueOf((int) TxtFileReader.getDepot(filename)
                .getX()));
        textDepotY.setText(String.valueOf((int) TxtFileReader.getDepot(filename)
                .getY()));
        textDeadline.setText(String.valueOf(TxtFileReader.getDeadline(filename)));
       
        textMaxLoad
                .setText(String.valueOf(TxtFileReader.getTruckCapacity(filename)));

        gui.displayMessage("GUI - simmulation constrains " + "read form .txt file ["
                + filename + "]");
    }

    public void buttonAddCommissionsGroupTxtActionPerformed(ActionEvent evt) {

        JFileChooser chooser = new JFileChooser(DirectoriesResolver.getTxtCommisionsDir());
        
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
        	addCommisionGroup(chooser.getSelectedFile().getAbsolutePath(), checkBoxIsProblemDynamic.isSelected());
        }
    }

    private void buttonEditCommissionActionPerformed(ActionEvent evt) {

        Object[] toEdit = listCommissions.getSelectedValues();
        if (toEdit.length == 1)
            new AddEditCommission(this, listCommissions, (CommissionHandler) toEdit[0]);
    }

    private void buttonRemoveCommissionActionPerformed(ActionEvent evt) {

        Object[] toRemove = listCommissions.getSelectedValues();
        for (int i = 0; i < toRemove.length; i++)
            removeCommissionHandler(((CommissionHandler) toRemove[i]));
    }

    public void buttonSetConstrainsActionPerformed(ActionEvent evt) {

        Point2D.Double depot = null;
        double deadline = 0;
        double maxLoad = 0;

        try {

            depot = new Point2D.Double(Integer.valueOf(textDepotX.getText()), Integer.valueOf(textDepotY.getText()));
            deadline = Double.valueOf(textDeadline.getText());
            maxLoad = Double.valueOf(textMaxLoad.getText());

        } catch (NumberFormatException e) {

            textDeadline.setText("         ???     ");
            return;
        }
        
        setSimConstrains(depot, deadline, maxLoad,false);
    }

    public void setConstraintsTestMode() {

        Point2D.Double depot = null;
        double deadline = 0;
        double maxLoad = 0;

        try {

            depot = new Point2D.Double(Integer.valueOf(textDepotX.getText()), Integer.valueOf(textDepotY.getText()));
            deadline = Double.valueOf(textDeadline.getText());
            maxLoad = Double.valueOf(textMaxLoad.getText());

        } catch (NumberFormatException e) {

            textDeadline.setText("         ???     ");
            return;
        }
        
        setSimConstrains(depot, deadline, maxLoad,true);
    }
    
    public void addCommissionHandler(CommissionHandler commissionHandler) {

        ListModel listModel = listCommissions.getModel();
        CommissionHandler[] content = new CommissionHandler[listModel.getSize() + 1];
        for (int i = 0; i < listModel.getSize(); i++)
            content[i] = (CommissionHandler) listModel.getElementAt(i);
        content[listModel.getSize()] = commissionHandler;
        listCommissions.setModel(new DefaultComboBoxModel(content));

        guiAgent.addCommissionHandler(commissionHandler);
        gui.displayMessage("GUI - commission added " + commissionHandler.toString());
    }

    public void removeCommissionHandler(CommissionHandler comHandler) {

        guiAgent.removeCommissionHandler(comHandler);

        ListModel listModel = listCommissions.getModel();
        CommissionHandler[] content = new CommissionHandler[listModel.getSize()];
        CommissionHandler[] contentNew;
        CommissionHandler temp = null;
        boolean removed = false;
        for (int i = 0; i < listModel.getSize(); i++) {

            temp = (CommissionHandler) listModel.getElementAt(i);
            if (temp.equals(comHandler))
                removed = true;
            else {
                if (removed)
                    content[i - 1] = temp;
                else
                    content[i] = temp;
            }
        }

        if (removed) {

            contentNew = new CommissionHandler[content.length - 1];
            for (int i = 0; i < content.length - 1; i++)
                contentNew[i] = content[i];
            listCommissions.setModel(new DefaultComboBoxModel(contentNew));
        } else
            listCommissions.setModel(new DefaultComboBoxModel(content));

    }

    public void setSimConstrains(Point2D.Double depot, double deadline, double maxLoad,boolean testMode) {

        SimInfo simConstrains;

        simConstrains = new SimInfo(depot, deadline, maxLoad);
        gui.setSimInfo(simConstrains);
        this.depot=depot;
        this.deadline=deadline;
        this.maxLoad=maxLoad;
        
        if(testMode==false) setConstraints();
    }

    private Point2D.Double depot;
    private double deadline;
    private double maxLoad;
    
    public void setConstraints() {

        textDepotX.setText(new Integer((int) depot.getX()).toString());
        textDepotY.setText(new Integer((int) depot.getY()).toString());
        textDeadline.setText(new Integer((int) deadline).toString());
        textMaxLoad.setText(new Integer((int) maxLoad).toString());

        textDepotX.setEnabled(false);
        textDepotY.setEnabled(false);
        textDeadline.setEnabled(false);
        textMaxLoad.setEnabled(false);
        buttonSetConstrains.setEnabled(false);

        gui.displayMessage("GUI - constrains set: depot = (" + depot.getX() + ", " + depot.getY() + ") deadline = "
                + deadline + " capacity = " + maxLoad);

        if (gui.getProblemType() == ProblemType.WITHOUT_GRAPH) {

            gui.enableSimStartButton();

        } else if (gui.getProblemType() == ProblemType.WITH_GRAPH && gui.getNetworkGraph() != null) {

            gui.enableSimStartButton();
        }
    }
    
    // Podswietla juz wyslane do Dystrybutora zadania
    // getIncomeTime() <= simTime
    public void markSentCommissions() {

        int[] indicesToSelect;
        int indicesToSelectNo = 0;
        
        if (checkBoxMarkSentCommisssions.isSelected()) {

            ListModel listModel = listCommissions.getModel();
            CommissionHandler[] content = new CommissionHandler[listModel.getSize()];
            for (int i = 0; i < listModel.getSize(); i++)
                content[i] = (CommissionHandler) listModel.getElementAt(i);

            for (int i = 0; i < content.length; i++)
                if (content[i].getIncomeTime() <= gui.getTimestamp())
                    indicesToSelectNo++;

            indicesToSelect = new int[indicesToSelectNo];

            int j = 0;
            for (int i = 0; i < content.length; i++)
                if (content[i].getIncomeTime() <= gui.getTimestamp())
                    indicesToSelect[j++] = i;

            listCommissions.setSelectedIndices(indicesToSelect);
        }
    }

    public int getCommisionsCount() {
    	return listCommissions.getModel().getSize();
    }
    
    public int newCommissions() {
        int newCommissions = 0;
       
        ListModel listModel = listCommissions.getModel();
        CommissionHandler[] content = new CommissionHandler[listModel.getSize()];
        for (int i = 0; i < listModel.getSize(); i++)
        	content[i] = (CommissionHandler) listModel.getElementAt(i);

        for (int i = 0; i < content.length; i++)
        	if(content[i].getIncomeTime() == gui.getTimestamp())
        		newCommissions++;
        return newCommissions;
    }
    
    public void refreshComsWaiting() {

        gui.refreshComsWaiting();
    }

    public Point getDepotLocation() {

        return new Point(Integer.valueOf(textDepotX.getText()), Integer.valueOf(textDepotY.getText()));
    }
}
