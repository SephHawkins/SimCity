package restaurant3.gui;

import java.awt.Color;
import java.awt.Graphics2D;

import restaurant3.Restaurant3WaiterRole;

public class Restaurant3WaiterGui implements Gui {
	
	//Dimensions of gui
		private int width = 20;
		private int height = 20;

		Restaurant3WaiterRole agent = null;
		int home = 0;
		int xDestination = home;
		int yDestination = home;
		int xPos, yPos;
		private enum Command {noCommand, GoToHomePosition, TakeCustomerToTable, 
			GoToTable, GoToCook, TakeFoodToCustomer};	//EDIT
		private Command command=Command.noCommand;
		private boolean isPresent = false;
		
		//Positions
		private int cookPosX = Restaurant3AnimationPanel.oStandX - width;
		private int cookPosY = Restaurant3AnimationPanel.kitchenY - height;

	public Restaurant3WaiterGui(Restaurant3WaiterRole w) {
		agent = w;
		xPos = xDestination;
		yPos = yDestination;
	}

	@Override
	public void updatePosition() {
		if (xPos < xDestination)
			xPos++;
		else if (xPos > xDestination)
			xPos--;

		if (yPos < yDestination)
			yPos++;
		else if (yPos > yDestination)
			yPos--;

		if (xPos == xDestination && yPos == yDestination) {
			if (command==Command.GoToHomePosition) {
				agent.msgAtTableRelease();
			}
			else if (command==Command.TakeCustomerToTable) {
				agent.msgAtTableRelease();
			}
			else if (command==Command.GoToTable) {
				agent.msgAtTableRelease();
			}
			else if(command == Command.GoToCook){
				agent.msgAtCookRelease();
			}
			else if(command == Command.TakeFoodToCustomer){
				agent.msgAtTableRelease();
			}
			command=Command.noCommand;
		}
		
	}

	@Override
	public void draw(Graphics2D g) {
		g.setColor(Color.pink);
		g.fillRect(xPos, yPos, width, height);
	}

	@Override
	public boolean isPresent() {
		return isPresent;
	}
	
	public void setPresent(boolean b){
		isPresent = b;
	}
	
	public void DoGoToHomePosition(){
		xDestination = home;
		yDestination = home;
		command = Command.GoToHomePosition;
	}

	public void DoTakeCustomerToTable(int table){
		int row;
     	if(table%3 !=0){
     		row = table/3 + 1;
		}
		else {
			row = table/3;
		}
        xDestination = (((table-1)%3)+1)*100 - 20;
        yDestination = row*100 - 20;
        
        command = Command.TakeCustomerToTable;
	}
	
	public void DoLeaveCustomer(){
		xDestination = home;
		yDestination = home;
		command = Command.noCommand;
	}
	
	public void DoGoToTable(int table){
		int row;
     	if(table%3 !=0){
     		row = table/3 + 1;
		}
		else {
			row = table/3;
		}
        xDestination = (((table-1)%3)+1)*100 - 20;
        yDestination = row*100 - 20;
        
        command = Command.GoToTable;
	}
	
	public void DoGoToCook(){
		xDestination = cookPosX;
		yDestination = cookPosY;
		
		command = Command.GoToCook;
	}
	
	public void DoTakeFoodToCustomer(int table){
		int row;
     	if(table%3 !=0){
     		row = table/3 + 1;
		}
		else {
			row = table/3;
		}
        xDestination = (((table-1)%3)+1)*100 - 20;
        yDestination = row*100 - 20;
        
        command = Command.TakeFoodToCustomer;
	}
}
