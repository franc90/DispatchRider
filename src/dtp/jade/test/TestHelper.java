package dtp.jade.test;

import dtp.gui.SimLogic;
import dtp.jade.gui.GUIAgent;


public class TestHelper extends SimLogic {

	private static final long serialVersionUID = 9007838144802469684L;

	public TestHelper(GUIAgent agent) {

        super(agent);
    }

    public void displayMessage(String txt) {
    	System.out.println(txt);
    }

}
