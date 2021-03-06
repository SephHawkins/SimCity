package restaurant1.gui;

import person.PersonAgent;
import restaurant1.*;
import restaurant1.gui.*;
import restaurant1.shareddata.*;
import utilities.Gui;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

public class Restaurant1AnimationPanel extends JPanel implements ActionListener {
    private final int WINDOWX = 540;
    private final int WINDOWY = 480;
    private final int lec = 200, lec1 = 300;
    private final int wid = 250, wid1 = 150, res = 50, origin = 0;
    private Image bufferImage;
    private Dimension bufferSize;
    
    private int xc = 0;
    private int yc = 0;
    private int checkx = 0;
    private int checky = 0;
    
    
    public List<Gui> guis = new ArrayList<Gui>();
    ImageIcon flImg = new ImageIcon(this.getClass().getResource("restaurantfloor.png"));
    Image floorimg = flImg.getImage();
    ImageIcon table = new ImageIcon(this.getClass().getResource("resttable.png"));
    Image timg = table.getImage();

    public Restaurant1AnimationPanel() {
    	
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
		this.setBorder(BorderFactory.createTitledBorder(" Restaurant 1 "));
        
        bufferSize = this.getSize();
 
  //  	Timer timer = new Timer(20, this );
   // 	timer.start();
    }

	public void actionPerformed(ActionEvent e) {
		repaint();  //Will have paintComponent called
	}

	
	public void jia(int a, int b){
		xc = a;
		yc = b;
	}
	
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;

        //Clear the screen by painting a rectangle the size of the frame
        g2.setColor(getBackground());
        g2.fillRect(origin, origin, WINDOWX, WINDOWY );
        
        //draw floor
        g2.drawImage(floorimg, 0, 0, WINDOWX, WINDOWY, null);

        //Here is the table
        g2.drawImage(timg, lec, wid, res, res, null);
        
        g2.drawImage(timg, lec1, wid, res, res, null);

        g2.drawImage(timg, lec1, wid1, res, res, null);
        
        // Drawing the fridge
        g2.setColor(Color.BLUE);
        g2.fillRect(430, 160, 20, 20);
        
		g2.setColor(Color.cyan);
		g2.fillRect(470, 230, 30, 30);
		g2.fillRect(470, 130, 30, 30);
     
        if(checkx != xc || checky!= yc ){
            g2.setColor(Color.ORANGE);
            g2.fillRect(xc, yc, res, res);    
        }

        for(Gui gui : guis) {
            if (gui.isPresent()) {
                gui.draw(g2);
            }
        }
    }
    
    public void addGui(Restaurant1CustomerGui gui) {
    	//System.err.println("CustomerGui Added");
        guis.add(gui);
    }

    public void addGui(WaiterGui gui) {
        guis.add(gui);
    }
    
    public void addGui(CookGui gui){
    	guis.add(gui);
    }

	public void setCustomerEnabled(Restaurant1CustomerRole agent) {
		// TODO Auto-generated method stub
		
	}

}
