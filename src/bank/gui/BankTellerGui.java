package bank.gui;

import bank.interfaces.*;

import java.awt.*;
import java.util.*;

/**
 * This class represents the 
 * customers in the bank
 */
public class BankTellerGui implements Gui{

	private BankTeller bt = null;
	private boolean isPresent = true;
	private boolean atDestination = true;
	private Map<String, Dimension> locations = new HashMap<String, Dimension>();

	BankAnimationPanel gui;

	private int xPos, yPos;
	private int xDestination, yDestination;

	public BankTellerGui(BankTeller t, BankAnimationPanel gui){ //HostAgent m) {
		bt = t;
		xPos = -20;
		yPos = 300;
		xDestination = -20;
		yDestination = 300;
		this.gui = gui;
		//locations.put("Host", new Dimension(320,80));
		locations.put("Teller1", new Dimension(40, 340));
		locations.put("Teller2", new Dimension(100, 340));
		locations.put("Teller3", new Dimension(160, 340));
		locations.put("Teller4", new Dimension(220, 340));
		locations.put("Teller5", new Dimension(280, 340));
		locations.put("Teller6", new Dimension(340, 340));
		locations.put("Teller7", new Dimension(400, 340));
		locations.put("Teller8", new Dimension(460, 340));
		locations.put("Teller9", new Dimension(520, 340));
		locations.put("Teller10", new Dimension(580, 340));
		locations.put("Outside", new Dimension(640, 250));
	}

	public void updatePosition() {
		if (xPos < xDestination)
			xPos++;
		else if (xPos > xDestination)
			xPos--;

		if (yPos < yDestination)
			yPos++;
		else if (yPos > yDestination)
			yPos--;

		if (!atDestination && xPos == xDestination && yPos == yDestination) {
			bt.msgAtDestination();
			atDestination = true;
		}
	}

	public void draw(Graphics2D g) {
		g.setColor(Color.BLUE);
		g.fillRect(xPos, yPos, 20, 20);
	}

	public boolean isPresent() {
		return isPresent;
	}
	
	public void setPresent(boolean p) {
		isPresent = p;
	}
	
	public void DoGoToLocation(String location){
		atDestination = false;
		Dimension d = locations.get(location);
		xDestination = d.width;
		yDestination = d.height;
	}
	public void DoGoToPos(int x, int y){
		xDestination = x;
		yDestination = y;
	}

}
