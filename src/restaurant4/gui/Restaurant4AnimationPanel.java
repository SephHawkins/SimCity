package restaurant4.gui;

import javax.swing.*;

import utilities.Gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

/** 
 * The panel where all of the animation takes places
 * Handles movement and other animation things
 */
public class Restaurant4AnimationPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 2L;
	private final int WINDOWX = 540;
    private final int WINDOWY = 480;
    static final int TIMERCOUNT = 20;
    static final int TABLEDIST = 100;
    static final int TABLEX = 100;
    static final int TABLEY = 300;
    static final int TABLEDIM = 50;

    public List<Gui> guis = Collections.synchronizedList(new ArrayList<Gui>());
    
    ImageIcon flImg = new ImageIcon(this.getClass().getResource("restaurantfloor.png"));
    Image floorimg = flImg.getImage();
    ImageIcon table = new ImageIcon(this.getClass().getResource("resttable2.png"));
    Image timg = table.getImage();

    public Restaurant4AnimationPanel() {
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
        this.setBorder(BorderFactory.createTitledBorder(" Restaurant 4 "));
 
   // 	Timer timer = new Timer(TIMERCOUNT, this );
   // 	timer.start();
    }

	public void actionPerformed(ActionEvent e) {
		repaint();  //Will have paintComponent called
	}

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;

        //Clear the screen by painting a rectangle the size of the frame
        g2.setColor(getBackground());
        g2.fillRect(0, 0, WINDOWX, WINDOWY );
        
        //draw floor
        g2.drawImage(floorimg, 0, 0, WINDOWX, WINDOWY, null);

        //Here is the table
        g2.drawImage(timg, TABLEX, TABLEY, TABLEDIM, TABLEDIM, null);
        
        g2.drawImage(timg, TABLEX+TABLEDIST, TABLEY, TABLEDIM, TABLEDIM, null);
        
        g2.drawImage(timg, TABLEX+(2*TABLEDIST), TABLEY, TABLEDIM, TABLEDIM, null);

        //Customer Waiting Area
        g2.setColor(Color.GRAY);
        g2.fillRect(88, 114, 298, 24);
        
        //Waiter Home Area
        g2.setColor(Color.LIGHT_GRAY);
        g2.fillRect(50, 110, 24, 234);
        
        //Grills
        g2.setColor(Color.CYAN);
        g2.fillRect(460, 138, 24, 234);
        
        //Plating Area
        g2.setColor(Color.YELLOW);
        g2.fillRect(405, 138, 24, 234);
        
        //Text for Areas
        g2.setColor(Color.BLACK);
        g2.drawString("Customer Waiting Area", 170, 110);
        g2.drawString("Waiters", 50, 110);
        g2.drawString("Plating", 400, 384);
        g2.drawString("Cooking", 444, 384);

        synchronized(guis){
	        for(Gui gui : guis) {
	            if (gui.isPresent()) {
	                gui.draw(g2);
	            }
	        }
        }
    }

    public void addGui(Gui gui) {
        guis.add(gui);
    }
    
    public void removeGui(Gui gui){
    	guis.remove(gui);
    }
}
