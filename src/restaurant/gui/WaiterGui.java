package restaurant.gui;


import restaurant.Restaurant1HostRole;
import restaurant.Restaurant1WaiterRole;
import restaurant.interfaces.Customer;
import simcity.gui.*;

import java.awt.*;

import agent.Gui;

public class WaiterGui implements Gui {
	

    private Restaurant1WaiterRole agent = null;
    private Restaurant1HostRole host;
    public boolean isPresent = false;
    private int countNumber = 0;
    private int customernumber = 0;
    private int xPos = -20, yPos = -20;//default waiter position
    private int xDestination = 50, yDestination = 50;//default start position
    private int origin = 40;
    private int chomeposition = 20;
    private int distance= 20;
	private int xfood = 540;
	private int yfood = 250;
    private String order = "";
    private String food = "";
    private boolean go = false;
    private static final int cookX = 520;
    private static final int cookY = 230;
    public static final int xTable = 200;
    public static final int yTable = 250;
    public static final int x1Table = 300;
    public static final int y1Table = 150;
    
    public WaiterGui(Restaurant1WaiterRole agent, Restaurant1HostRole host) {
        this.agent = agent;
        this.host = host;
    }
    
    public void setOrigion(int number){
    	countNumber = number;
    	yPos = origin;
    	xPos = countNumber * origin;
    	xDestination = countNumber * origin;
    	yDestination = origin;
    }
    
    public void stop(){
    	
    }
    
    public void zou(){
    	
    }

    public void updatePosition() {
        if (xPos < xDestination)
            xPos++;
        else if (xPos > xDestination)
            xPos =xPos - 2;

        if (yPos < yDestination)
            yPos++;
        else if (yPos > yDestination)
            yPos = yPos - 2;
        
        if (xPos == (customernumber * origin + distance) && yPos == chomeposition){
        	agent.msgAtTable();
        }
        
        if (xPos == xDestination &&go && yPos == yDestination
        		& (xDestination == xTable + distance) & (yDestination == yTable - distance)) {
        	
           agent.msgAtTable();
           go = false;

        }
        
        if (xPos == xDestination  &&go && yPos == yDestination
        		& (xDestination == x1Table + distance) & (yDestination == yTable - distance)) {

           agent.msgAtTable();
           go = false;

        }
        
        if (xPos == xDestination &&go&& yPos == yDestination
        		& (xDestination == x1Table + distance) & (yDestination == y1Table - distance)) {

           agent.msgAtTable();
           go = false;

        } 
        
        if (xPos == cookX && yPos == cookY) {
        	agent.msgatCook();
        }
        

        if(xPos == countNumber * origin && yPos == origin){
        	agent.msgIsback();
        }
        
        if(xPos == 520 && yPos == 230){

        	agent.msgAtTable();
        	
        }
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.MAGENTA);
        g.fillRect(xPos, yPos, distance, distance);
        g.drawString(order, xPos, yPos);
        g.drawString(food, xfood, yfood);
    }
    
    public void animationBringFood(String a){
    	order = a;
    }

    public void bringFoodDone(){
    	order = "";
    }
    
    public boolean isPresent() {
        return isPresent;
    }
    public void setPresent(boolean tf){
    	isPresent = tf;
    }
    public void DoGotoCHomePosition(int number){
    	customernumber = number;
    	xDestination = number * origin + distance;
    	yDestination = chomeposition;
    }
    
    public void DoBringToTable(int seattable) {
    	if(seattable == 1){
        xDestination = xTable + distance;
        yDestination = yTable - distance;
    	}
    	else if(seattable ==2 ){
    		xDestination = x1Table + distance;
            yDestination = yTable - distance;	
    	}
    	else if(seattable == 3){
    		xDestination = x1Table + distance;
            yDestination = y1Table - distance;	
    	}
    	go = true;

    }

    public void DoGoToTakeOrder(Customer c, int seattable) {
    	//customer = c;
    	if(seattable == 1){
        xDestination = xTable + distance;
        yDestination = yTable - distance;
    	}
    	else if(seattable ==2 ){
    		xDestination = x1Table + distance;
            yDestination = yTable - distance;	
    	}
    	else if(seattable == 3){
    		xDestination = x1Table + distance;
            yDestination = y1Table - distance;	
    	}
    	go = true;

    }
    
    public void DoBackToTable(Customer c,int seattable) {
    	if(seattable == 1){
        xDestination = xTable + distance;
        yDestination = yTable - distance;
    	}
    	else if(seattable ==2 ){
    		xDestination = x1Table + distance;
            yDestination = yTable - distance;	
    	}
    	else if(seattable == 3){
    		xDestination = x1Table + distance;
            yDestination = y1Table - distance;	
    	}
    	go = true;
    }


    
    
    public void DoLeaveCustomer() {
    	xDestination = countNumber * origin;
    	yDestination = origin;
    }
    
    public void Dogotocook(){
    	xDestination = cookX;
    	yDestination = cookY;
    }
    
    public void showfood(String order){
    	food = order;
    }
    
    public void hidefood(){
    	food = "";
    }
    
    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
}
