package resident;

import java.util.ArrayList; 
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import market.Food;
import person.*;
import person.Location.LocationType;
import person.interfaces.Person;
import resident.gui.HomeOwnerGui;
import resident.interfaces.HomeOwner;
import resident.test.mock.EventLog;
import resident.test.mock.LoggedEvent;
import agent.*;

public class HomeOwnerRole extends Role implements HomeOwner {

	// For the purposes of JUnit testing
	public EventLog log = new EventLog();
	
	/**
	 * Data for Homeowner
	 * @author jenniezhou
	 *
	 */
	// Constructor
	public HomeOwnerRole(Person p, String n, int hn) {
		super(p);
		roleName = "Home Owner";
		name = n;
		houseNumber = hn;
		this.person = p;
		state = MyState.Awake;
		
		myFridge.add(new Food("Chicken", 2));
		myFridge.add(new Food("Salad", 1));
	}
	
	// States for the home owner 
	public enum MyState {Sleeping, Awake, Cooking};
	private MyState state;
	
	// Returns the name of the home owner
	public String getName() {
		return name;
	}
	
	// Returns the house number of the home owner
	public int getHouseNumber() {
		return houseNumber;
	}
	
	// Sets the amount of money for the home owner
	public void setMoney(double amt) {
		myMoney = amt;
	}
	
	// Returns the amount of money for the home owner
	public double getMoney() {
		return myMoney;
	}
	
	public static class MyPriority {
		public enum Task {NeedToEat, Cooking, Eating, WashDishes, CheckPerson, Washing, MaintainHome, GoToMarket, RestockFridge, GoToRestaurant, NoFood}
		public Task task;
		public enum Type {Hunger, Cleaning, Maintainance, Shop, Bored}
		public Type type;
		public int timeDuration;

		public MyPriority(Task t, Type type) {
			task = t;
			this.type = type;
		}
	}

	public List<MyPriority> toDoList = Collections.synchronizedList(new ArrayList<MyPriority>());
	public List<Food> myFridge = Collections.synchronizedList(new ArrayList<Food>());
	private Timer cookingTimer = new Timer(); // Times the food cooking
	private Timer eatingTimer = new Timer();
	private Timer washingDishesTimer = new Timer();
	private Timer sleepingTimer = new Timer();
	private int houseNumber;
	private String name;
	private double myMoney;
	private static int minRestaurantMoney = 2; // Amount you need to go to a restaurant
	private static int hungerThreshold = 3;

	public HomeOwnerGui homeGui;
	
	// All the gui semaphores
	private Semaphore atFridge = new Semaphore(0, true);
	public Semaphore atFrontDoor = new Semaphore(0, true);
	private Semaphore atStove = new Semaphore(0, true);
	private Semaphore atTable = new Semaphore(0, true);
	private Semaphore atSink = new Semaphore(0, true);
	private Semaphore atBed = new Semaphore(0, true);
	
	// Hack to establish connection between GUI and role
	public void setGui(HomeOwnerGui gui) {
		homeGui = gui;
	}
	
	/**
	 * Messages for Homeowner
	 * @author jenniezhou
	 *
	 */
	public void updateVitals(int hunger, int timer) {
		if (hunger >= hungerThreshold) {
			if(state == MyState.Cooking)
				return;
			for(MyPriority mp : toDoList){
				if(mp.type == MyPriority.Type.Hunger)
					return;
			}
			// Add eating to the list of priorities that the resident has
			toDoList.add(new MyPriority(MyPriority.Task.NeedToEat, MyPriority.Type.Hunger));
			
			// Log that the message has been received
			log.add(new LoggedEvent("I'm hungry."));
						
			stateChanged();
		}
	}

	public void msgFoodDone() {
		// Add getting cooked food to the list of priorities 
		toDoList.add(new MyPriority(MyPriority.Task.Eating, MyPriority.Type.Hunger));
		
		log.add(new LoggedEvent("My food is ready! I can eat now."));
				
		stateChanged();
	}

	public void msgDoneEating() {
		// Add washing dishes to the list of priorities
		toDoList.add(new MyPriority(MyPriority.Task.WashDishes, MyPriority.Type.Cleaning));
		
		log.add(new LoggedEvent("Done eating. I'm going to wash dishes now."));
		
		stateChanged();
	}

	public void msgDoneWashing(MyPriority p) {
		// Removes washing from the list of priorities
		toDoList.remove(p);
		
		log.add(new LoggedEvent("Done washing dishes!"));
				
		toDoList.add(new MyPriority(MyPriority.Task.CheckPerson, MyPriority.Type.Bored));
		
		stateChanged();
	}

	public void msgDoneGoingToMarket(List<Food> groceries) {		
		//waitForReturn.release();
		
		// If the customer has just finished going to the market, restock the fridge and then cook
		log.add(new LoggedEvent("I just finished going to the market. Time to put all my groceries in the fridge."));
				
		// Add restocking fridge to the to do list
		toDoList.add(new MyPriority(MyPriority.Task.RestockFridge, MyPriority.Type.Shop));		

		for (Food f : groceries) {
			myFridge.add(new Food(f.choice, f.amount));
		}

		stateChanged();
	}

	public void msgDoneEatingOut() {
		//waitForReturn.release();
		
		log.add(new LoggedEvent("I just finished eating out. I'm full now!"));
				
		stateChanged();
	}
	
	public void msgMaintainHome() {
		// Adds calling housekeeper to the list of priorities
		toDoList.add(new MyPriority(MyPriority.Task.MaintainHome, MyPriority.Type.Maintainance));
		
		log.add(new LoggedEvent("It's been a day. I need to clean my house!"));
				
		stateChanged();
	}
	
	// GUI semaphore release messages
	// For when the home owner is at the fridge
	public void msgAtFridge() {
		atFridge.release();
	}
	
	// For when home owner is at the door
	public void msgAtDoor() {
		atFrontDoor.release();
	}
	
	// For when the home owner is at the stove
	public void msgAtStove() {
		atStove.release();
	}
	
	// For when the home owner has reached the dining table
	public void msgAtTable() {
		atTable.release();
	}
	
	// For when the home owner has reached the sink
	public void msgAtSink() {
		atSink.release();
	}
	
	// For when the home owner has reached the bed
	public void msgAtBed() {
		atBed.release();
	}

	/**
	 * Scheduler for Homeowner
	 * @author jenniezhou
	 *
	 */
	public boolean pickAndExecuteAnAction() {
		if (person.getTime() >= 22 && state == MyState.Awake) {
			sleep();
			return true;
		}
		if (!toDoList.isEmpty() && state != MyState.Sleeping) {
			for (MyPriority p : toDoList) { // Eating is the most important
				if (p.task == MyPriority.Task.NeedToEat) {
					checkFridge(p);
					return true;
				}
			}
			for (MyPriority p : toDoList) { // If fridge is empty
				if (p.task == MyPriority.Task.NoFood) {
					decideMarketOrGoOut(p);
					return true;
				}
			}
			for (MyPriority p : toDoList) {
				if (p.task == MyPriority.Task.GoToRestaurant) {
					goToRestaurant(p);
					return true;
				}
			}
			for (MyPriority p : toDoList) {
				if (p.task == MyPriority.Task.GoToMarket) {
					goToMarket(p);
					return true;
				}
			}
			for (MyPriority p : toDoList) { // Assuming house needs to be maintained every day
				if (p.task == MyPriority.Task.MaintainHome) {
					maintainHome(p);
					return true;
				}
			}
			for (MyPriority p : toDoList) {
				if (p.task == MyPriority.Task.RestockFridge) {
					restockFridge(p);
					return true;
				}
			}
			for (MyPriority p : toDoList) {
				if (p.task == MyPriority.Task.Cooking) {
					cookFood(p);
					return true;
				}
			}			
			for (MyPriority p : toDoList) {
				if (p.task == MyPriority.Task.Eating) {
					eatFood(p);
					return true;
				}
			}
			for (MyPriority p : toDoList) {
				if (p.task == MyPriority.Task.WashDishes) {
					washDishes(p);
					return true;
				}
			}
			for (MyPriority p : toDoList) {
				if (p.task == MyPriority.Task.CheckPerson) {
					checkPerson(p);
					return true;
				}
			}
		}
		//homeGui.DoGoToHome();
		return false;
	}
		
	/**
	 * Actions for Homeowner
	 */
	private void checkPerson(MyPriority p) {
		toDoList.remove(p);
		
		// Checks to see if there's something else for the person to do that is a one time event (aka not work)
		for (SimEvent e : this.person.getToDo()) {
			if (e.importance == SimEvent.EventImportance.OneTimeEvent) {
				DoGoToFrontDoor();
				
				try {
					atFrontDoor.acquire();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				this.person.msgFinishedEvent(this);
			}
		}
	}
	
	private void sleep() {
		// Set home owner's state to sleeping
		state = MyState.Sleeping;
		
		// Gui goes to bed and timer begins to start sleeping		
		DoGoToBed();
		
		sleepingTimer.schedule(new TimerTask() 
        {
            public void run() 
            {
            	updateVitals(3, 23);
            	msgMaintainHome();
            	state = MyState.Awake;
            }
        }, 540000);
	}
		
	private void checkFridge(MyPriority p) {
	
		DoGoToFridge();

		if (myFridge.isEmpty()) { // Checks to see if the list is empty
			// Adds going to the market or restaurant to the list
			toDoList.add(new MyPriority(MyPriority.Task.NoFood, MyPriority.Type.Hunger));
			log.add(new LoggedEvent("My fridge has no food. I must now decide if I should go to the market or go out to eat."));
		}
		else { // Cook the food
			toDoList.add(new MyPriority(MyPriority.Task.Cooking, MyPriority.Type.Hunger));
			log.add(new LoggedEvent("My fridge has food. I can cook now!"));
		}
		toDoList.remove(p);
	}

	private void decideMarketOrGoOut(MyPriority p) {
		if (person.msgCheckWallet() < minRestaurantMoney) { 
			toDoList.add(new MyPriority(MyPriority.Task.GoToMarket, MyPriority.Type.Hunger)); 
			toDoList.add(new MyPriority(MyPriority.Task.Cooking, MyPriority.Type.Hunger));
			
			log.add(new LoggedEvent("I'm going to go to the market. I have enough money to go and come home."));
		}
		else { 
			toDoList.add(new MyPriority(MyPriority.Task.GoToRestaurant, MyPriority.Type.Hunger));
			toDoList.add(new MyPriority(MyPriority.Task.GoToMarket, MyPriority.Type.Shop));
			
			log.add(new LoggedEvent("I have enough money to go to the restaurant, and go to the market when I have time."));
			
		}
		toDoList.remove(p);
	}
	
	private void goToRestaurant(MyPriority p) {
		toDoList.remove(p);
		
		DoGoToFrontDoor();
		
		if (person.getMap() != null) {
			Restaurant location = (Restaurant)person.getMap().chooseByType(LocationType.Restaurant1);//FIX

			// GUI goes to restaurant, lets person agent know that no longer going to be a resident role
			person.msgAddEvent(new SimEvent("Go to restaurant", location, SimEvent.EventType.CustomerEvent));
		}
		
		person.msgFinishedEvent(this);
	}
	
	private void goToMarket(MyPriority p) {
		toDoList.remove(p);
		
		DoGoToFrontDoor();
		
		if (person.getMap() != null) {
			Market location = (Market)person.getMap().chooseByType(LocationType.Market);
			
			// Lets person agent know that no longer going to be a resident role
			person.msgAddEvent(new SimEvent("Go To Market", location, SimEvent.EventType.CustomerEvent));
		}
		
		person.msgFinishedEvent(this);
	}

	private void restockFridge(MyPriority p) {
		toDoList.remove(p);

		DoGoToFridge();
	}

	private void cookFood(MyPriority p) {
		DoGoToFridge();

		int max = -1;
		String maxChoice = null;
		int index = -1;

		for (Food f : myFridge) { // Searches for the food item with the most inventory
			if (f.amount > max) {
				max = f.amount;
				maxChoice = f.choice;
			}
		}

		for (Food f : myFridge) { // Searches for and decreases the amount of food for the one with the most inventory
			if (f.choice == maxChoice) {
				--f.amount;
				++index;
				log.add(new LoggedEvent("I'm going to cook " + f.choice + ". My inventory of it is now " + f.amount + "."));
				break;
			}
		}
		
		// If there's no more of an item after the resident has removed it, then 
		if (myFridge.get(index).amount == 0) {
			myFridge.remove(index);
			log.add(new LoggedEvent("My fridge has no more " + maxChoice + "."));
		}
		toDoList.remove(p);
		state = MyState.Cooking;
		DoCookFood(maxChoice);
	}
	

	private void eatFood(MyPriority p) {
		person.setHungerLevel(0);
		state = MyState.Awake;
		toDoList.remove(p);
		DoGetCookedFood();
	}

	private void washDishes(MyPriority p) {
		toDoList.remove(p);

		final MyPriority prior = new MyPriority(MyPriority.Task.Washing, MyPriority.Type.Cleaning);
		toDoList.add(prior);

		DoWashDishes();

		// Timer to wash dishes
        washingDishesTimer.schedule(new TimerTask() 
        {
            public void run() 
            {
            	msgDoneWashing(prior);
            	homeGui.DoGoToHome();
//            	person.msgFinishedEvent(temp);
            }
        }, 2000);
	}

	private void maintainHome(MyPriority p) {
		toDoList.remove(p);
		
		DoMaintainHome();
		
		log.add(new LoggedEvent("Done maintaining home!"));
	}
	
	
	/**
	 * GUI ACTIONS
	 */
	private void DoGoToBed() {
		if (homeGui != null) {
			homeGui.DoGoToBed();
		}
	}
	
	private void DoGoToFridge() {
		if (homeGui != null) {
			// GUI goes to the fridge
			homeGui.DoGoToFridge();
	
			atFridge.drainPermits();
			
			// Semaphore to see if the GUI gets to the fridge
			try {
				atFridge.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void DoGoToFrontDoor() {
		if (homeGui != null) {
			// GUI goes to market 
			homeGui.DoGoToFrontDoor();
			
			atFrontDoor.drainPermits();
			
			try {
				atFrontDoor.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			homeGui.isPresent = false;
		}
	}
	
	private void DoCookFood(String s) {
		if (homeGui != null) {
			homeGui.setState(HomeOwnerGui.HomeCookingState.GettingIngredients, s);

			// GUI animation to go to the stove and start cooking
			homeGui.DoGoToStove(); 
			
			atStove.drainPermits();

			// Semaphore to determine if the GUI has gotten to the stove location
			try {
				atStove.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			homeGui.state = HomeOwnerGui.HomeCookingState.Cooking;
			
	        // Timer to cook the food
	        cookingTimer.schedule(new TimerTask() 
	        {
	            public void run() 
	            {
	            	msgFoodDone();
	            }
	        }, 5000);
	        
	        homeGui.DoGoToHome();
		}
	}
	
	private void DoGetCookedFood() {
		if (homeGui != null) {
			// GUI animation to go to the stove and start cooking
			homeGui.DoGoToStove(); 

			atStove.drainPermits();
			
			// Semaphore to determine if the GUI has gotten to the stove location
			try {
				atStove.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			homeGui.state = HomeOwnerGui.HomeCookingState.GettingCookedFood;

			homeGui.DoGoToTable(); // GUI animation to go to the dining table

			atTable.drainPermits();
			
			// Semaphore to determine if the GUI has gotten to the table location
			try {
				atTable.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// Timer to eat the food
	        eatingTimer.schedule(new TimerTask() 
	        {
	            public void run() 
	            {
	            	msgDoneEating();
	            }
	        }, 8000);
		}
	}
	
	private void DoWashDishes() {
		if (homeGui != null) {
			homeGui.DoGoToSink(); // GUI animation to go to the sink
			
			atSink.drainPermits();

			// Semaphore to determine if the GUI has arrived at sink location
			try {
				atSink.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			homeGui.setState(HomeOwnerGui.HomeCookingState.Nothing, null);
		}
	}
	
	private void DoMaintainHome() {
		if (homeGui != null) {
			homeGui.DoGoToBed();
			
			atBed.drainPermits();
			
			try {
				atBed.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			homeGui.DoGoToFridge();
			
			atFridge.drainPermits();
			
			try {
				atFridge.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			homeGui.DoGoToSink();
			
			atSink.drainPermits();
			
			try {
				atSink.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			homeGui.DoGoToStove();
			
			atStove.drainPermits();
			
			try {
				atStove.acquire(); 
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			homeGui.DoGoToTable();
			
			atTable.drainPermits();
			
			try {
				atTable.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public String getRoleName(){
		return roleName;
	}
	
	public utilities.Gui getGui(){
		return homeGui; 
	}
	
}
