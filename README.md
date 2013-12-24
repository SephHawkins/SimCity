##SimCity v1 Team 36 Repository

###Team Information
  + [SimCity Team 36 Wiki](https://github.com/usc-csci201-fall2013/team36/wiki)

###Running the Program
  1.  To run the program, open Eclipse.
  2.  Select 'File', then 'Other', then 'New Java Project from Existing Ant Buildfile.'
  3.  Click the 'Next' button, and then select Browse. Find the build.xml file in whatever folder you cloned into. (default name is team36)
  4.  Hit Finish.
  5.  May need to include JUnit4 into libraries.
  5.  Go into the project folder, into src, into the gui.main package, and select SimCityGUI.java.
  6.  In the drop down menu for run, select run as, and click Java Application.
  7.  SimCity should run
  8.  While running, select a scenario from the right panel and click run.
  9.  Some scenarios must be started only after a global scenario has been started.

###Team Members Contributions
  1. Jennie Zhou - 
	+ Houses and Apartments
	+ Residents, Landlords, Apartment Tenants
	+ Person Interaction Panel
	+ Some of Add Person Panel
	+ Team Management
	+ Fixes of other code and integration
	+ Upgrade of restaurant6
	+ Worked on scenario panel
	+ Overall restaurant fixes for producer consumer
	+ Market delivery scenario
	+ GUI additions
  2. Lizhi Fan -
	+ Transportation
	+ Busses and Cars
	+ A* for Busses and Cars
	+ City Layout
	+ Person Initialization in Main Class
	+ Fixes of other code and integration
	+ Upgrade of restaurant5
	+ Vehicle-person collision
	+ Vehicle-vehicle collision
	+ Traffic light
  3. Joseph Boman - 
	+ Bank
	+ Clickable buildings in City View
	+ Time Card and Role Classes
	+ Git (Merging Issues, Commits, Everything else)
	+ Person Initialization in Main Class
	+ Person and Person scheduler
	+ Individual Location GUIs
	+ Upgrade of restaurant4
	+ GUI radial button upgrade upon right click
	+ Bank robbery scenario
	+ Market delivery scenario
	+ Open and close scenario
	+ Fixed logic errors
	+ Debugged the code
  4. Grant Collins -
	+ Person Agent
	+ Person Initialization in Main Class
	+ CityMap Class
	+ Global Clock and Locations
	+ Integration
	+ Upgrade of restaurant3
	+ Weekend scenario
	+ Upgraded restaurant 2
	+ Traffic light
	+ Fixes and debugging
  5. Rocky Luo -
	+ Market
	+ Market Trucks
	+ Integration
	+ Upgrade of restaurant2
	+ Traffic light
	+ Helped with images in CityAnimationPanel 
	+ Market delivery fail scenario
  6. Mikhail Bhuta -
	+ Person Interaction Panel
	+ Trace Panel
	+ All gui panel layout and formatting
	+ Integration
	+ Upgrade of restaurant 1 and 2
	+ Created images and integrated them for all agent and panel animations
	+ Some fixes and debugging
	+ Fixes to panel looks and formatting 

###Issues and Known Bugs 
  + Null pointer exceptions in the 50-person scenario, causing people to freeze after the first shift is over
