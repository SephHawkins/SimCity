package simcity.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.ImageIcon;

import person.PersonAgent;
import simcity.PassengerRole; 
import simcity.gui.BusGui.GuiState;
import utilities.Gui;

public class PassengerGui implements Gui {

	private enum GuiState {atStop,gotoStop,onBus,leaving}
	private GuiState guistate; 
	private PassengerRole passenger; 
	public int xPos, yPos, xDestination, yDestination; 
	public ImageIcon img = new ImageIcon(this.getClass().getResource("person.png"));
	public Image pImg = img.getImage();

	public boolean isPresent; 

	public PassengerGui(PassengerRole mp, int x, int y){
		passenger = mp; 
		xPos = x;
		yPos = y;
		xDestination = x; 
		yDestination = y; 
	}
	
	public void draw(Graphics2D g) {  
		
		if (guistate != GuiState.onBus){
	       g.drawImage(pImg, xPos, yPos, 10, 10, null);
	       g.setColor(Color.BLUE);
			g.drawString(((PersonAgent)passenger.person).getName(), xPos-14, yPos-5);
		}
    }
	
    public boolean isPresent() {
        return isPresent;
    }
    
    public void updatePosition() {
    	if (xPos < xDestination && (yPos == 170 || yPos == 280))
            xPos++;
        else if (xPos > xDestination && (yPos == 170 || yPos == 280))
            xPos--;

        if (yPos < yDestination && (xPos == 330 || xPos == 440))
            yPos++;
        else if (yPos > yDestination && (xPos == 330 || xPos == 440))
            yPos--;
    	
        
        if (xPos == xDestination && yPos == yDestination && guistate == GuiState.gotoStop){
        	
        	passenger.msgAtBusStop();
        	guistate = GuiState.atStop; 
        }
    	
    }
    
    public void GoToBusStop(int x, int y){
    	xDestination = x;
    	yDestination = y; 
    	guistate = GuiState.gotoStop; 
    }
    
    public void GoOnBus(){
    	guistate = GuiState.onBus; 
    }
    
    public void LeaveBus(int busx, int busy, int Destinationx, int Destinationy){
    	guistate = GuiState.leaving; 
    	xPos = busx; 
    	yPos = busy; 
    	xDestination = Destinationx; 
    	yDestination = Destinationy; 
    }

    	
    public void setPresent(boolean b){
    	isPresent = b; 
    }
}
