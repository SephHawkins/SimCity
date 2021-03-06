package bank;

import java.util.*;
import java.util.concurrent.Semaphore;

import person.interfaces.*;
import bank.gui.*;
import bank.test.mock.*;
import bank.interfaces.*;
import agent.*;

/**
 * This role is the bank teller, who manages requests from people to access and 
 * change their current bank account info
 * 
 * @author Joseph Boman
 *
 */
public class BankTellerRole extends Role implements BankTeller {

	//Data
	public EventLog log;//Used for testing
	String name;//The name of the bank teller
	public int cowardice = 0;
	public boolean endOfDay = false; //Used at the end of the day
	public List<Task> tasks;//A list of tasks that the teller needs to perform
	public BankDatabase bd;//The bank database. Given to the teller upon creation
	public BankHost bh;//The bank host. Given to the teller upon creation
	public BankCustomer bc;//The current customer. Sent to the teller by the customer
	public BankTellerGui gui;//The bank teller gui. Given to the teller upon creation
	String destination;//Used for animation
	Semaphore movement = new Semaphore(0, true);//Used for animation
	public state s;//The state of the bank teller
	public enum state {working, backToWork, none, haveDestination}
	
	/**
	 * The constructor of the bank teller
	 * 
	 * @param person the person who is the bank teller
	 * @param name the name of the bank teller
	 */
	public BankTellerRole(Person person, String name){
		super(person);
		roleName = "Bank Teller";
		this.name = name;
		log = new EventLog();
		tasks = Collections.synchronizedList(new ArrayList<Task>());
		s = state.none;
	}
	
	//Messages
	/**
	 * Received when he needs to go to a new location
	 * 
	 * @param location the location that the teller needs to go to
	 */
	public void msgNewDestination(String location){
		log.add(new LoggedEvent("Received msgNewDestination from BankHost"));
		s = state.haveDestination;
		destination = location;
		stateChanged();
	}
	
	/**
	 * Received from a bank customer when they need an account
	 * 
	 * @param bc the customer who needs an account
	 */
	public void msgINeedAccount(BankCustomer bc){
		log.add(new LoggedEvent("Received msgINeedAccount from BankCustomer"));
		this.bc = bc;
		synchronized(tasks){
			tasks.add(new Task("openAccount"));
		}
		stateChanged();
	}
	
	/**
	 * Received from a bank customer who wants to deposit money in their account
	 * 
	 * @param bc the customer who wants to make a deposit
	 * @param amount the amount of money to be deposited
	 * @param accountNumber the account number of the bank customer
	 */
	public void msgDepositMoney(BankCustomer bc, double amount, int accountNumber){
		log.add(new LoggedEvent("Received msgDepositMoney from BankCustomer"));
		this.bc = bc;
		synchronized(tasks){
			tasks.add(new Task("deposit", amount, accountNumber));
		}
		stateChanged();
	}
	
	/**
	 * Received from a bank customer who wants to withdraw money from their account
	 * 
	 * @param bc the customer who wants to withdraw money
	 * @param amount the amount of money to be withdrawn
	 * @param accountNumber the account number of the bank customer
	 */
	public void msgWithdrawMoney(BankCustomer bc, double amount, int accountNumber){
		log.add(new LoggedEvent("Received msgWithdrawMoney from BankCustomer"));
		this.bc = bc;
		synchronized(tasks){
			tasks.add(new Task("withdraw", amount, accountNumber));
		}
		stateChanged();
	}
	
	/**
	 * Received from a bank customer who wants a loan
	 * 
	 * @param bc the customer who wants a loan
	 * @param amount the amount of money the customer wants to get as a loan
	 * @param accountNumber the account number of the bank customer
	 */
	public void msgINeedLoan(BankCustomer bc, double amount, int accountNumber){
		log.add(new LoggedEvent("Received msgINeedLoan from BankCustomer"));
		this.bc = bc;
		synchronized(tasks){
			tasks.add(new Task("getLoan", amount, accountNumber));
		}	
		stateChanged();
	}
	
	/**
	 * Received from a bank customer when he is robbing the bank.
	 * 
	 * @param bc the customer who is the robber
	 * @param amount the amount of money he is demanding
	 */
	public void msgThisIsAHoldup(BankCustomer bc, double amount){
		log.add(new LoggedEvent("Received msgThisIsAHoldUp from BankCustomer"));
		this.bc = bc;
		synchronized(tasks){
			tasks.add(new Task("robBank", amount, -1));
		}
		stateChanged();
	}
	
	/**
	 * Received from the bank database when it has created an account for a customer
	 * 
	 * @param accountNumber the newly created account number for the customer
	 * @param bc the customer who the account is for
	 */
	public void msgAccountCreated(int accountNumber, BankCustomer bc){
		log.add(new LoggedEvent("Received msgAccountCreated from BankDatabase"));
		synchronized(tasks){
			for(Task t : tasks){
				if(t.type.equals("openAccount")){
					t.accountNumber = accountNumber;
					t.ts = taskState.completed;
					stateChanged();
					return;
				}
			}
		}
	}
	
	/**
	 * Received from the bank database when a deposit has been completed
	 * 
	 * @param balance the new balance of the bank account
	 * @param bc the customer who made the deposit
	 */
	public void msgDepositDone(double balance, BankCustomer bc){
		log.add(new LoggedEvent("Received msgDepositDone from BankDatabase"));
		synchronized(tasks){
			for(Task t : tasks){
				if(t.type.equals("deposit")){
					t.balance = balance;
					t.ts = taskState.completed;
					stateChanged();
					return;
				}
			}
		}
	}
	
	/**
	 * Received from the bank database when a withdrawal has been completed
	 * 
	 * @param balance the new balance of the bank account
	 * @param money the money that was withdrawn
	 * @param bc the customer who made the withdrawal
	 */
	public void msgWithdrawDone(double balance, double money, BankCustomer bc){
		log.add(new LoggedEvent("Received msgWithdrawDone from BankDatabase"));
		synchronized(tasks){
			for(Task t : tasks){
				if(t.type.equals("withdraw")){
					t.balance = balance;
					t.amount = money;
					t.ts = taskState.completed;
					stateChanged();
					return;
				}
			}
		}
	}
	
	/**
	 * Received from the bank database when a loan has been granted
	 * 
	 * @param money the amount of money that was granted as a loan
	 * @param debt the debt that the customer now has
	 * @param bc the customer who wanted the loan
	 */
	public void msgLoanGranted(double money, double debt, BankCustomer bc){
		log.add(new LoggedEvent("Received msgLoanGranted from BankDatabase"));
		synchronized(tasks){
			for(Task t : tasks){
				if(t.type.equals("getLoan")){
					t.amount = money;
					t.ts = taskState.completed;
					t.balance = debt;
					stateChanged();
					return;
				}
			}
		}
	}
	
	/**
	 * Received from the bank database whenever a request fails for any reason
	 * 
	 * @param bc the customer who submitted the request
	 * @param type the type of request that failed
	 */
	public void msgRequestFailed(BankCustomer bc, String type){
		log.add(new LoggedEvent("Received msgRequestFailed from BankDatabase"));
		synchronized(tasks){
			for(Task t : tasks){
				if(t.type.equals(type)){
					t.ts = taskState.failed;
					stateChanged();
				}
			}
		}
	}
	/**
	 * Received from the Bank Database when it is giving him 
	 * the amount demanded by the robber
	 * 
	 * @param amount the amount of money demanded
	 */
	public void msgHereIsMoney(double amount){
		log.add(new LoggedEvent("Received msgHereIsMoney from BankDatabase"));
		synchronized(tasks){
			for(Task t : tasks){
				if(t.type.equals("robBank")){
					t.amount = amount;
					t.ts = taskState.completed;
					stateChanged();
					return;
				}
			}
		}
	}
	
	/**
	 * Received from the bank customer when it is leaving the bank
	 * 
	 * @param bc the customer who is leaving
	 */
	public void msgLeavingBank(BankCustomer bc){
		log.add(new LoggedEvent("Received msgLeavingBank from BankCustomer"));
		this.bc = null;
		s = state.backToWork;
		stateChanged();		
	}
	
	/**
	 * Received from the bank teller gui when it arrives at some destination
	 */
	public void msgAtDestination(){
		log.add(new LoggedEvent("Received msgAtDestination from BankTellerGui"));
		movement.release();
		stateChanged();
	}
	
	/**
	 * Received from the bank host at the end of the day when you're another day older
	 */
	public void msgWorkDayOver(){
		log.add(new LoggedEvent("Received msgWorkDayOver from Bank Teller"));
		endOfDay = true;
		stateChanged();
	}
	/**
	 * The scheduler for the teller
	 * 
	 * @return true if it picks an action
	 * @return false if it doesn't have something to do
	 */
	public boolean pickAndExecuteAnAction(){
		//If it needs to go somewhere		
		if(s == state.haveDestination){
			goToLocation(destination);
			s = state.working;
			return true;
		}
		//If there is a task that failed
		synchronized(tasks){
			for(Task t : tasks){
				if(t.ts == taskState.failed){
					RequestFailed(t);
					return true;
				}
			}
		}
		//If there is a task that has been completed
		synchronized(tasks){
			for(Task t : tasks){
				if(t.ts == taskState.completed){
					switch(t.type){
					case "openAccount": accountMade(t); return true;
					case "deposit": depositMade(t); return true;
					case "withdraw": withdrawMade(t); return true;
					case "getLoan": loanMade(t); return true;
					case "robBank": bankRobbed(t); return true;
					}
				}
			}
		}
		//If there is a task that was requested
		synchronized(tasks){
			for(Task t : tasks){
				if(t.ts == taskState.requested){
					switch(t.type){
					case "openAccount": openAccount(t); return true;
					case "deposit": deposit(t); return true;
					case "withdraw": withdraw(t); return true;
					case "getLoan": getLoan(t); return true;
					case "robBank": robbery(t); return true;
					}
				}
			}
		}
		//If the teller has no customer, it's the end of the day, and he's another day older
		if(bc == null && endOfDay == true){
			goOffWork();
			return true;
		}
		//If the teller has no customer and is working
		if(s == state.backToWork){
			informHost();
			return true;
		}
		//If no action was selected
		return false;
	}
	
	/**
	 * Sends a message to the bank database, requesting a 
	 * new account
	 * 
	 * @param t the task that needs to be performed
	 */
	private void openAccount(Task t){
		bd.msgOpenAccount(bc, this);
		t.ts = taskState.waiting;
	}
	
	/**
	 * Sends a message to the customer informing him of his new account
	 * 
	 * @param t the task that was completed
	 */
	private void accountMade(Task t){
		bc.msgAccountMade(t.accountNumber);
		tasks.remove(t);
	}
	
	/**
	 * Sends a message to the bank database requesting a deposit
	 * 
	 * @param t the task that needs to be performed
	 */
	private void deposit(Task t){
		bd.msgDepositMoney(bc, t.amount, t.accountNumber, this);
		t.ts = taskState.waiting;
	}
	
	/**
	 * Sends a message to the customer informing him of his deposit
	 * 
	 * @param t the task that was completed
	 */
	private void depositMade(Task t){
		bc.msgDepositDone(t.balance, t.amount);
		tasks.remove(t);
	}
	
	/**
	 * Sends a message to the bank database requesting a withdrawal
	 * 
	 * @param t the task that needs to be performed
	 */
	private void withdraw(Task t){
		bd.msgWithdrawMoney(bc, t.amount, t.accountNumber, this);
		t.ts = taskState.waiting;
	}
	
	/**
	 * Sends a message to the customer informing him of his withdrawal
	 * 
	 * @param t the task that was completed
	 */
	private void withdrawMade(Task t){
		bc.msgWithdrawDone(t.balance, t.amount);
		tasks.remove(t);
	}
	
	/**
	 * Sends a message to the bank database requesting a loan
	 * 
	 * @param t the task that needs to be performed
	 */
	private void getLoan(Task t){
		bd.msgLoanPlease(bc, t.amount, t.accountNumber, this);
		t.ts = taskState.waiting;
	}
	
	/**
	 * Sends a message to the customer telling him that his request failed
	 * 
	 * @param t the task that failed to be completed
	 */
	private void RequestFailed(Task t){
		bc.msgRequestFailed(t.type);
		tasks.remove(t);
	}
	
	/**
	 * Sends a message to the customer informing them of their new loan
	 * 
	 * @param t the task that was completed
	 */
	private void loanMade(Task t){
		bc.msgLoanGranted(t.amount, t.balance);
		tasks.remove(t);
	}
	
	/**
	 * Has a 30 percent chance of calling the cops and scaring 
	 * off the robber, and a 70 percent chance of giving in to the 
	 * robber's demands
	 * 
	 * @param t the task that was requested
	 */
	private void robbery(Task t){
		if(cowardice == 0)
			cowardice = (int)(Math.random() * (100 - 1) + 1);
		if(cowardice > 30){
			bd.msgGiveAllMoney(this, t.amount);
			t.ts = taskState.waiting;
		}
		else{
			bc.msgCallingCops();
			tasks.remove(t);
		}
		cowardice = 0;
	}
	
	/**
	 * Messages the bank robber to let him know that the 
	 * money he demanded is here
	 * 
	 * @param t the task that was completed
	 */
	private void bankRobbed(Task t){
		bc.msgHereIsMoney(t.amount);
		tasks.remove(t);
	}
	
	/**
	 * Sends a message to the host telling them that the teller
	 * is back to work
	 */
	private void informHost(){
		bh.msgBackToWork(this);
		s = state.working;
	}
	
	/**
	 * Used for animation. Sends the gui to a new location
	 * 
	 * @param location the location to go to
	 */
	private void goToLocation(String location){
		if(gui != null){
			gui.DoGoToLocation(location);
			try{
				movement.acquire();
			}
			catch(InterruptedException e){
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Sends a message to the person to go off work
	 */
	private void goOffWork(){
		endOfDay = false;
		goToLocation("Outside");
		if(gui!=null)
			gui.setPresent(false);
		getPerson().msgGoOffWork(this, 0.00);
		s = state.haveDestination;
	}
	
	//Utilities
	/**
	 * Returns the name of the teller
	 */
	public String toString(){
		return name;
	}
	
	/**
	 * Sets the host of the teller to be a specific thing
	 * 
	 * @param bh the host
	 */
	public void setHost(BankHost bh){
		this.bh = bh;
	}
	//Grant's addition
	public void setGui(BankTellerGui btg){
		this.gui = btg;
	}
	/**
	 * The Task class. Contains a type, an amount, a balance, an account number, 
	 * and a taskState. Used to track things that the teller needs to do.
	 * 
	 * @author Joseph Boman
	 */
	public class Task{
		public String type;
		public double amount;
		public double balance;
		public int accountNumber;
		public taskState ts;
		/**
		 * The constructor used for deposits, withdrawals, and loans
		 * 
		 * @param type the type of the task
		 * @param amount the amount of money for that task
		 * @param accountNumber the account number for that task
		 */
		Task(String type, double amount, int accountNumber){
			this.type = type;
			this.amount = amount;
			this.accountNumber = accountNumber;
			ts = taskState.requested;
		}
		/**
		 * The constructor used for creating accounts
		 * 
		 * @param type the type of the task (should be openAccount)
		 */
		Task(String type){
			this.type = type;
			accountNumber = 0;
			ts = taskState.requested;
		}
	}
	//The various states of tasks
	public enum taskState {requested, waiting, completed, failed}
	
	public String getRoleName(){
		return roleName;
	}
	
	public utilities.Gui getGui(){
		return this.gui; 
	}
	
	
}
