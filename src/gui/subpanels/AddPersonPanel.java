package gui.subpanels;

import gui.panels.*;

import java.awt.*;
import java.awt.event.*;

import java.util.*;
import java.util.List;

import javax.swing.*;

/**
 * This panel allows the user to add a person
 * to Sim City 201
 * Person can be customized ( framework to create a person has been implemented,
 * code to actually create the person needs to be added)
 * Random person can be created (To be implemented)
 * 
 */

public class AddPersonPanel extends JPanel implements ActionListener{
	
	private String title = " Add Person Panel ";
	private static final int WIDTH = 275;
	private static final int HEIGHT = 310;
	private Dimension size = new Dimension(WIDTH, HEIGHT);
	
	// Main control panel reference
	CityControlPanel cntrlPanel;
	
	// Layout
	GridLayout grid;
	
	// TextField for name
	JLabel name = new JLabel("  Name: ");
	JTextField nameText = new JTextField(10);
	
	// Checkbox list to select which role/roles
	List<JCheckBox> roles = new ArrayList<JCheckBox>(); 
	
	// Buttons
	JButton custom = new JButton("Custom Person");
	JButton random = new JButton("Random Person");
	
	public AddPersonPanel(CityControlPanel cp) {
		cntrlPanel = cp;
		
		// PANEL SETUP
		this.setLayout(new GridLayout(8, 2));
		this.setBorder(BorderFactory.createTitledBorder(title));
		this.setBackground(Color.GRAY);
		
		// Panel size initiations
		this.setPreferredSize(size);
		this.setMaximumSize(size);
		
		// ROLES TO ADD
		roles.add(new JCheckBox("Role 1"));
		roles.add(new JCheckBox("Role 2"));
		roles.add(new JCheckBox("Role 3"));
		roles.add(new JCheckBox("Role 4"));
		roles.add(new JCheckBox("Role 5"));
		roles.add(new JCheckBox("Role 6"));
		roles.add(new JCheckBox("Role 7"));
		roles.add(new JCheckBox("Role 8"));
		roles.add(new JCheckBox("Role 9"));
		roles.add(new JCheckBox("Role 10"));
		
		// ADD COMPONENTS
		// Name info
		this.add(name);
		this.add(nameText);
		
		// CheckBoxes
		for(JCheckBox role : roles){
			this.add(role);
		}
		
		// Buttons
		this.add(custom);
		custom.addActionListener(this);
		
		this.add(random);
		random.addActionListener(this);
	}
	
	public void actionPerformed(ActionEvent e){
		
	}
	
	public void addCustomPerson(){
		// ArrayList<Role> selectedRoles
		
		for(JCheckBox role : roles){
			if(role.isSelected()){
				if(role.getText().equals("Role 1")){
					// selectedRoles.add(Role 1)
				}
				if(role.getText().equals("Role 2")){
					// selectedRoles.add(Role 2)
				}
				if(role.getText().equals("Role 3")){
					// selectedRoles.add(Role 3)
				}
				if(role.getText().equals("Role 4")){
					// selectedRoles.add(Role 4)
				}
				if(role.getText().equals("Role 5")){
					// selectedRoles.add(Role 5)
				}
				if(role.getText().equals("Role 6")){
					// selectedRoles.add(Role 6)
				}
				if(role.getText().equals("Role 7")){
					// selectedRoles.add(Role 7)
				}
				if(role.getText().equals("Role 8")){
					// selectedRoles.add(Role 8)
				}
				if(role.getText().equals("Role 9")){
					// selectedRoles.add(Role 9)
				}
				if(role.getText().equals("Role 10")){
					// selectedRoles.add(Role 10)
				}
			}
		}
	}

}