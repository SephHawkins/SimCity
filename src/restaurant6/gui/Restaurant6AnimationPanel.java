package restaurant6.gui;

import javax.swing.*;  

import restaurant6.Restaurant6CookRole;
import utilities.Gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

public class Restaurant6AnimationPanel extends JPanel implements ActionListener {

    private final int WINDOWX = 1000;
    private final int WINDOWY = 1000;
    private Image bufferImage;
    private Dimension bufferSize;
    public int tableXPos = 200;
    public int tableYPos = 50;
    public final int tableWidth = 50;
    public final int tableHeight = 50;
    public int grillXPos = 400;
    public int grillYPos = 170;
    public int grill2XPos = 440;
    public int grill2YPos = 170;
    public int grill3XPos = 480;
    public int grill3YPos = 170;
    public int grill4XPos = 520;
    public int grill4YPos = 170;
    public final int grillWidth = 20;
    public final int grillHeight = 20;    
    public int fridgeXPos = 560;
    public int fridgeYPos = 170;
    public final int fridgeWidth = 50;
    public final int fridgeHeight = 20;
    public int plateXPos = 320;
    public int plateYPos = 170;
    public final int plateWidth = 50;
    public final int plateHeight = 20;
    public int kitchenXPos = 320;
    public int kitchenYPos = 120;
    public final int kitchenWidth = 290;
    public final int kitchenHeight = 90;
    public int waitingXPos = 40;
    public int waitingYPos = 40;
    public final int waitingWidth = 150;
    public final int waitingHeight = 200;
    public final int winX = 0;
    public final int winY = 0;
    public final int numTables = 3;
    public boolean custOrdered = false;
    public boolean custEating = false;
    
    private List<Gui> guis = new ArrayList<Gui>();
    public List<Shape> tables;

    public Restaurant6AnimationPanel() {
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
        
        bufferSize = this.getSize();
 
    	Timer timer = new Timer(7, this);
    	timer.start();
    	
    	tables = new ArrayList<Shape>();
    	
    	for (int i = 1; i <= numTables; i++)
    	{
    		tables.add(new Rectangle.Float(tableXPos, tableYPos, tableWidth, tableHeight));
    		tableXPos = tableXPos + 200;
    	}
    }

	public void actionPerformed(ActionEvent e) {
		repaint();  //Will have paintComponent called
	}

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;

        //Clear the screen by painting a rectangle the size of the frame
        g2.setColor(getBackground());
        g2.fillRect(winX, winY, WINDOWX, WINDOWY);
        
        tableXPos = 200; // Reinitializes the table x position to 50 
        
        //Here is the table when the customer has just sat down
    	for (Shape s : tables) {
    		g2.setColor(Color.ORANGE);
        	g2.fillRect(tableXPos, tableYPos, tableWidth, tableHeight);
        	tableXPos = tableXPos + 200;
        	g2.draw(s);
        }           
    	
    	// Drawing the kitchen area
    	g2.setColor(Color.lightGray);
    	g2.fillRect(kitchenXPos, kitchenYPos, kitchenWidth, kitchenHeight);
    	
    	// Drawing the customer waiting area
    	g2.setColor(Color.lightGray);
    	g2.drawString("WAITING", waitingXPos+40, waitingYPos+210);
    	g2.fillRect(waitingXPos, waitingYPos, waitingWidth, waitingHeight);
    	
    	// Drawing the grill
    	g2.setColor(Color.DARK_GRAY);
    	g2.fillRect(grillXPos, grillYPos, grillWidth, grillHeight);
    	
    	// Drawing the grill
    	g2.setColor(Color.DARK_GRAY);
    	g2.fillRect(grill2XPos, grill2YPos, grillWidth, grillHeight);
    	
    	// Drawing the grill
    	g2.setColor(Color.DARK_GRAY);
    	g2.fillRect(grill3XPos, grill3YPos, grillWidth, grillHeight);
    	
    	// Drawing the grill
    	g2.setColor(Color.DARK_GRAY);
    	g2.fillRect(grill4XPos, grill4YPos, grillWidth, grillHeight);
    	
    	// Drawing the fridge
    	g2.setColor(Color.BLUE);
    	g2.drawString("FRIDGE", fridgeXPos, fridgeYPos+30);
    	g2.fillRect(fridgeXPos, fridgeYPos, fridgeWidth, fridgeHeight);
    	
    	// Drawing the plating area
    	g2.setColor(Color.BLACK);
    	g2.drawString("FOOD", plateXPos, plateYPos+30);
    	g2.fillRect(plateXPos, plateYPos, plateWidth, plateHeight);
        
        for(Gui gui : guis) {
            if (gui.isPresent()) {
                gui.updatePosition();
            }
        }

        for(Gui gui : guis) {
        	if (gui instanceof Restaurant6CustomerGui) {
        		Restaurant6CustomerGui a = (Restaurant6CustomerGui) gui; 
        		if (a.hasOrdered()) { // If the customer has ordered, draw event
        			a.drawOrdered(g2);
        		}
        	}
        	
        	if (gui instanceof Restaurant6CustomerGui) {
        		Restaurant6CustomerGui a = (Restaurant6CustomerGui) gui;
        		if (a.isEating()) { // If customer is eating, draw event
        			a.drawEating(g2);
        		}
        	}
        	
        	if (gui instanceof Restaurant6WaiterGui) {
        		Restaurant6WaiterGui w = (Restaurant6WaiterGui) gui;
        		if (w.isDelivering()) { // If waiter is delivering food, draw event
        			w.drawDelivering(g2);
        		}
        	}
        	
        	if (gui instanceof Restaurant6CookGui) {
        		Restaurant6CookGui c = (Restaurant6CookGui) gui;  
        		c.drawCook(g2);
    		}
        	
            if (gui.isPresent()) {
                gui.draw(g2);
            }
        }
    }

    public void addGui(Restaurant6CustomerGui gui) {
        guis.add(gui);
    }

    public void addGui(Restaurant6WaiterGui gui) {
        guis.add(gui);
    }
    
    public void addGui(Restaurant6CookGui gui) {
    	guis.add(gui);
    }
}
