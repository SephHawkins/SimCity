package resident.gui;

import resident.ApartmentTenantRole; 
import utilities.Gui;

import java.awt.*;

import javax.swing.ImageIcon;

import person.PersonAgent;

public class ApartmentTenantGui implements Gui {

	public boolean isPresent;
	
    private ApartmentTenantRole aptTenant = null;

    private int xPos = 20, yPos = 50; // Default apartment tenant position
    private int xDestination = 20, yDestination = 50; // Default start position
    
    private int homeX = 50;
    private int homeY = 50;
    private int fridgeX = 90;
    private int fridgeY = 100;
    private int stoveX = 90;
    private int stoveY = 130;
    private int tableX = 100;
    private int tableY = 300;
    private int sinkX = 90;
    private int sinkY = 160;
    private int doorX = 400;
    private int doorY = 50;
    private int bedX = 300;
    private int bedY = 200;
    
    public enum AptCookingState {GettingIngredients, Cooking, GettingCookedFood, Nothing};
    public AptCookingState state;
    
    private String choice;
    
    //Images
    ImageIcon img = new ImageIcon(this.getClass().getResource("person.png"));
    Image image = img.getImage();
    
    public ApartmentTenantGui(ApartmentTenantRole c) {
    	aptTenant = c;
    }
    
    public void setState(AptCookingState st, String ch) {
    	state = st;
    	choice = ch;
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
        
        if (xPos == fridgeX && yPos == fridgeY) {
        	aptTenant.msgAtFridge();
        }
        
        if (xPos == doorX && yPos == doorY) {
        	aptTenant.msgAtDoor();
        }
        
        if (xPos == stoveX && yPos == stoveY) {
        	aptTenant.msgAtStove();
        }
        
        if (xPos == tableX && yPos == tableY) {
        	aptTenant.msgAtTable();
        }
        
        if (xPos == sinkX && yPos == sinkY) {
        	aptTenant.msgAtSink();
        }
	}
	
	public void draw(Graphics2D g) {
		g.setColor(Color.BLUE);
        g.drawImage(image, xPos, yPos, 20, 20, null);
		g.drawString(((PersonAgent)aptTenant.getPerson()).getName(), xPos-14, yPos+30);
        
        String foodChoice = null;
        
        if (state == AptCookingState.GettingIngredients) {
        	g.setColor(Color.BLUE);
    		
    		foodChoice = choice.substring(0, 2) + "?";
    		
    		g.fillRect(xPos, yPos, 20, 20);
    		g.drawString(foodChoice, xPos, yPos);
        }
        
        else if (state == AptCookingState.Cooking) {
        	g.setColor(Color.LIGHT_GRAY);
    		
    		foodChoice = choice.substring(0, 2) + "..";
    		
    		g.fillRect(stoveX-20, stoveY, 20, 20);
    		g.drawString(foodChoice, stoveX, stoveY+20);
        }
        
        else if (state == AptCookingState.GettingCookedFood) {
        	g.setColor(Color.BLUE);
    		
    		foodChoice = choice.substring(0, 2);
    		
    		g.fillRect(xPos, yPos, 20, 20);
    		g.drawString(foodChoice, xPos, yPos);
        }
	}
	
	public boolean isPresent() {
		return isPresent;
	}
	
	public void DoGoToFridge() {
		xDestination = fridgeX;
		yDestination = fridgeY;
	}
	
	public void DoGoToFrontDoor() {
		xDestination = doorX;
		yDestination = doorY;
	}
	
	public void DoGoToStove() {
		xDestination = stoveX;
		yDestination = stoveY;
	}
	
	public void DoGoToHome() {
		xDestination = homeX;
		yDestination = homeY;
	}
	
	public void DoGoToTable() {
		xDestination = tableX;
		yDestination = tableY;
	}
	
	public void DoGoToSink() {
		xDestination = sinkX;
		yDestination = sinkY;
	}
	
	public void DoGoToBed() {
		xDestination = bedX;
		yDestination = bedY;
	}

	public void setPresent(boolean b) {
		isPresent = b;
	}
  
}
