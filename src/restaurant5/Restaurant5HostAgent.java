package restaurant5;

import agent.Role; 
import person.PersonAgent; 
import restaurant5.gui.Restaurant5HostGui;
import restaurant5.gui.Restaurant5Table;
import restaurant5.interfaces.Waiter5; 
import utilities.restaurant.RestaurantHost;

import java.util.*;

/**
 * Restaurant Host Agent
 */
//We only have 2 types of agents in this prototype. A customer and an agent that
//does all the rest. Rather than calling the other agent a waiter, we called him
//the HostAgent. A Host is the manager of a restaurant who sees that all
//is proceeded as he wishes.
public class Restaurant5HostAgent extends Role implements RestaurantHost {
	public boolean offWork = false; 
    LinkedList<myWaiter> waiterQ = new LinkedList<myWaiter>();
    public class myWaiter {
    	myWaiter(Waiter5 _w, WaiterState _s){
    		w=_w;
    		s=_s; 
    	}
    	
    	public Waiter5 w;
    	public WaiterState s; 
    }
    
    private enum WaiterState {ready,onBreak, denied,asked};
	private int NTABLES = 3; //hard coded table shit
	private List<myCustomer> customers;
	private List<Restaurant5Table> tables;
	public Restaurant5HostGui hostGui = null;
	private String name;

	private class myCustomer{
		Restaurant5CustomerAgent c;
		CustomerState s;
		myCustomer(Restaurant5CustomerAgent _c, CustomerState _s){
			c = _c;
			s = _s;
		}
	}
	private enum CustomerState {waiting, restaurantfull, gettingseated, eating, done, toserve, donePerson};
	

	
	
	public Restaurant5HostAgent(String name, PersonAgent p) {
		super(p);
		customers = Collections.synchronizedList(new ArrayList<myCustomer>());
		this.name = name; 
		// make some tables
		tables = Collections.synchronizedList(new ArrayList<Restaurant5Table>(NTABLES));
		for (int ix = 1; ix <= NTABLES; ix++) {
			tables.add(new Restaurant5Table(ix));//how you add to a collections
		}	
	}

	// Messages

	public void msgEndOfDay(){
		print("host received msg end of day");
		offWork = true; 
		stateChanged(); 
	}
	
	
	public void msgOffBreak(Waiter5 w){
		
		for (myWaiter _w:waiterQ){
			if (_w.w == w){
				_w.s = WaiterState.ready; 
			}
		}
	}
	
	public void msgIWantFood(Restaurant5CustomerAgent cust) {
		boolean added = false; 
		for (myCustomer m: customers){
			if (m.c == cust){
				added = true; 
				m.s = CustomerState.waiting;
			}
		}
		if (!added){
			customers.add(new myCustomer(cust, CustomerState.waiting));
		}
		stateChanged();
	}
	
	public void msgfirstWaiter(){
		stateChanged();
	}
	
	public void msgLeaving(Restaurant5CustomerAgent c){
		print("msgleaving");
		synchronized(customers){
			for (myCustomer m:customers){
				if (m.c == c){
					m.s = CustomerState.done; 
					stateChanged();
					//customers.remove(m);
				}
			}
		}
	}

	public void msgCanIGoOnBreak(Waiter5 w){
			for (myWaiter _w:waiterQ){
				if (_w.w == w){
					_w.s = WaiterState.asked; 
				}
			}
			stateChanged();
	}
	
	public void msgTableFree(Waiter5 w, int table) {
		print("msgTableFree");
		Restaurant5CustomerAgent c = null; 
		synchronized(tables){
			for (Restaurant5Table _table : tables) {
				if (_table.tableNumber == table) {
					c = _table.occupiedBy; 
					_table.occupiedBy = null;
				}
			}
		}
		synchronized(customers){
			for (myCustomer m:customers){
				if (m.c == c){
					m.s = CustomerState.done; 
					stateChanged();
					return; 
				}
			}
		}
	
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		System.out.println("host scheduler called");
		/* Think of this next rule as:
            Does there exist a table and customer,
            so that table is unoccupied and customer is waiting.
            If so seat him at the table.
		 */
		
		if (!customers.isEmpty()){
			synchronized(customers){
			for (myCustomer mc:customers){
				if (mc.s == CustomerState.done){
					notifyPerson(mc);
					return true; 
				}
			}
			}
		}
		
		
		for (myWaiter _w: waiterQ){
			if (_w.s == WaiterState.asked){
				handleBreak(_w);
				return true;
			}
		}
		
		if (!waiterQ.isEmpty()){
			if (!customers.isEmpty()){
				synchronized(customers){
			for (myCustomer mc: customers){
				if (mc.s == CustomerState.waiting || mc.s == CustomerState.toserve){
					for (Restaurant5Table _table: tables){
						if (_table.occupiedBy == null){
							_table.occupiedBy = mc.c; 
							seatCustomer(mc,_table);
							return true;
							}
						}
					}
				}
				}
				}
		}
		
		//means it hasn't hit the previous one. risk
		if (!waiterQ.isEmpty()){
			if (!customers.isEmpty()){
				synchronized(customers){
				for (myCustomer mc:customers){
					if (mc.s == CustomerState.restaurantfull || mc.s == CustomerState.waiting){
						tellCustomerFull(mc);
						return true; 
					}
				}
				}
			}
		}
		
		if (offWork){
			for (myCustomer mc: customers){
				if (mc.s != CustomerState.donePerson){
					return false; 
				}
			}
			goOffWork();
			return true; 
		}
		
		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}
	
	// Actions
	private void goOffWork(){
		//msgthewaiters
		offWork = false; 
		//send it to the waiters
		for (myWaiter mw: waiterQ){
			mw.w.msgGoOffWork();
		}
		
	//	this.person.msgFinishedEvent(this); 
		this.person.msgGoOffWork(this, 0);

	}
	
	
	private void tellCustomerFull(myCustomer mc){
		mc.s = CustomerState.toserve; 
		print ("Host Restaurant Full");
		mc.c.msgRestaurantFull();
	}
	
	
	private void handleBreak(myWaiter w){
		int temp = 0;
		for (myWaiter _w: waiterQ){
			if (_w.s != WaiterState.onBreak){
				temp += 1; 
			}
		}
		
		if (temp == 1){
			print("Host you can't go on Break");
			w.s = WaiterState.ready;
			w.w.msgcantgoOnBreak();
		}
		else {
			print ("Host you can go on Break");
			w.s = WaiterState.onBreak;
			w.w.msggoOnBreak();
			//yes Break
		}
	}
	
	private void seatCustomer(myCustomer mc, Restaurant5Table t){
		Waiter5 _w = pickaWaiter(); 
		print("Host "+ _w.getName() + " " + "Seat Customer " +mc.c.name + " at Table " + t.tableNumber);
		mc.s = CustomerState.gettingseated;
		_w.msgseatCustomer(mc.c, t.tableNumber); 
		stateChanged();
	}

	
	public void notifyPerson(myCustomer mc){
		print ("notifyPerson called");
		mc.s = CustomerState.donePerson; 
		mc.c.person.msgFinishedEvent(mc.c);
	}
	

	//Accessors 
	
	private Waiter5 pickaWaiter(){
		myWaiter temp; 
		temp = waiterQ.pop(); //pops out of front
		if (temp.s == WaiterState.onBreak){ //if he's on break don't assign him
			waiterQ.addLast(temp);
			temp = waiterQ.pop();
		}
		
		waiterQ.addLast(temp); //puts it in the back
		return temp.w;
	}
	
	public void addWaiter(Waiter5 w){
		myWaiter _w = new myWaiter(w,WaiterState.ready);
		waiterQ.addFirst(_w);
	}
	
	public List<Restaurant5Table> getTables() {
		return tables;
	}
	
	public void setGui(Restaurant5HostGui gui) {
		hostGui = gui;
	}
	
	public Restaurant5HostGui getGui() {
		return hostGui;
	}
	
	public String getName() {
		return name;
	}

	public String getRoleName(){
		return "Restaurant 5 Host";
	}
	
	public String toString(){
		return name; 
	}
	

	
}


