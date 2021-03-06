package bank;

import bank.interfaces.*;
import person.PersonAgent;
import person.interfaces.*;
import agent.*;
import bank.test.mock.*;
import bank.gui.*;

import java.util.*;
import java.util.concurrent.*;

/**
 * This class is the customer of a bank, whether they are a business 
 * or a person
 * 
 * @author Joseph Boman
 *
 */
public class BankCustomerRole extends Role implements BankCustomer {
	//Data
	String name;
	public EventLog log;//Used for testing
	public List<Task> tasks;//A list of tasks to perform at the bank
	public String destination;//Used for movement inside the bank
	public int accountNumber;//The account number of the person/business
	public BankHost bh;//The host of the bank. Is known upon entering bank.
	public BankTeller bt;//The current teller. Is given by the host
	public BankCustomerGui gui = null;//The gui of the customer. Used in animation
	public state s;//The current state of the customer
	Semaphore movement = new Semaphore(0, true);//Used for animation
	
	/**
	 * The constructor for the customer
	 * 
	 * @param person the person who is behind the customer
	 * @param name the name of the customer
	 */
	public BankCustomerRole(Person person, String name){
		super(person);
		roleName = "Bank Customer";
		this.name = name;
		s = state.none;
		tasks = new ArrayList<Task>();
		log = new EventLog();
		accountNumber = -1;
	}
	
	//Messages
	/**
	 * Received from the customerGui upon arrival to a new location
	 * in animation
	 */
	public void msgAtDestination(){
		movement.release();
		destination = null;
		stateChanged();
	}
	
	/**
	 * Received from the person. Gives the customer a task and
	 * an amount for that task
	 * 
	 * @param task the task to be performed
	 * @param amount the amount of money to deposit, withdraw, etc.
	 */
	public void msgGoToBank(String task, double amount){
		log.add(new LoggedEvent("Received msgGoToBank from Person"));
		tasks.add(new Task(task, amount));
		if(task.equals("robBank"))
			accountNumber = 0;
		s = state.needTeller;
		stateChanged();
	}
	
	/**
	 * Received from the host. Gives the customer a teller
	 * and the location of that teller
	 * 
	 * @param bt the teller for the customer to interact with
	 * @param location the location of the teller
	 */
	public void msgHereIsTeller(BankTeller bt, String location){
		this.bt = bt;
		destination = location;
		s = state.haveTeller;
		stateChanged();
	}
	
	/**
	 * Received from the teller once an account has been created
	 * 
	 * @param accountNumber the account number of the newly created account
	 */
	public void msgAccountMade(int accountNumber){
		log.add(new LoggedEvent("Received msgAccountMade from BankTeller"));
		((PersonAgent)person).accountNumber = accountNumber;
		this.accountNumber = accountNumber;
		this.s = state.atTeller;
		stateChanged();
	}
	
	/**
	 * Received from the teller once a deposit has been finished
	 * 
	 * @param balance the new balance of the account
	 * @param money the amount of money that was deposited
	 */
	public void msgDepositDone(double balance, double money){
		log.add(new LoggedEvent("Received msgDepositDone from BankTeller"));
		getPerson().msgNewBalance(balance);
		getPerson().msgAddMoney((-money));
		this.s = state.atTeller;
		stateChanged();
	}
	
	/**
	 * Received from the teller once a withdraw has been finished
	 * 
	 * @param balance the new balance of the account
	 * @param money the amount of money that was withdrawn
	 */
	public void msgWithdrawDone(double balance, double money){
		log.add(new LoggedEvent("Received msgWithdrawDone from BankTeller"));
		getPerson().msgNewBalance(balance);
		getPerson().msgAddMoney(money);
		this.s = state.atTeller;
		stateChanged();
	}
	
	/**
	 * Received from the teller once a loan has been granted
	 * 
	 * @param money the amount of money of the loan
	 * @param debt the amount of debt that the person now has
	 */
	public void msgLoanGranted(double money, double debt){
		log.add(new LoggedEvent("Received msgLoanGranted from BankTeller"));
		getPerson().msgAddMoney(money);
		this.s = state.atTeller;
		stateChanged();
	}
	
	/**
	 * Received from the teller once a task has failed
	 * 
	 * @param type the type of task that failed
	 */
	public void msgRequestFailed(String type){
		log.add(new LoggedEvent("Received msgRequestFailed from BankTeller"));
		this.s = state.atTeller;
		stateChanged();
	}
	
	/**
	 * Received from the teller when the teller is scaring him off
	 */
	public void msgCallingCops(){
		log.add(new LoggedEvent("Received msgCallingCops from BankTeller"));
		PersonAgent.tracePanel.print("Cops Called! Run Away!", (PersonAgent)person);
		this.s = state.runAway;
		stateChanged();
	}
	
	/**
	 * Received from the teller when the teller gives him the money he is stealing
	 * @param amount the amount of money stolen
	 */
	public void msgHereIsMoney(double amount){
		log.add(new LoggedEvent("Received msgHereIsMoney from BankTeller"));
		PersonAgent.tracePanel.print("Just stole " + amount + "! Run Away!", (PersonAgent)person);
		this.s = state.runAway;
		person.msgAddMoney(amount);
		stateChanged();
	}
	
	/**
	 * Received from the host when the customer needs to go to a new location
	 * 
	 * @param location the new location to go to
	 */
	public void msgNewLocation(String location){
		destination = location;
		stateChanged();
	}
	
	/**
	 * The scheduler of the customer. Is called by the person.
	 * 
	 * @return true if the scheduler picked an action
	 * @return false if the scheduler failed to pick an action
	 */
	public boolean pickAndExecuteAnAction(){
		//At the end of the robber scenario, when he needs to run away
		if(s == state.runAway){
			escape();
			return true;
		}
		//If the customer has not gotten a teller
		if(s == state.needTeller){
			informHost();
			return true;
		}
		//If the customer has a teller, go to the teller's location
		if(s == state.haveTeller){
			goToLocation(destination);
			s = state.atTeller;
			destination = null;
			return true;
		}
		//If the customer has a teller, but no account
		if(s == state.atTeller && accountNumber == -1){
			openAccount();
			return true;
		}
		//If the customer has a teller, and some tasks to perform
		if(s == state.atTeller && !tasks.isEmpty()){
			bankingAction(tasks.get(0));
			return true;
		}
		//If the customer has a teller, but no tasks left to perform
		if(s == state.atTeller && tasks.isEmpty()){
			leaveBank();
			return true;
		}
		//If you have a destination to go to that isn't a teller
		if(destination != null && !destination.contains("Teller")){
			goToLocation(destination);
			destination = null;
			return true;
		}
		//If no action was selected
		return false;
	}
	
	//Actions
	
	/**
	 * Upon arrival to the bank, moves to the host, 
	 * sends a message to him asking for a teller, and waits
	 */
	private void informHost(){
		goToLocation("Host");
		bh.msgINeedTeller(this);
		s = state.waiting;
	}
	
	/**
	 * Sends a message to the teller asking him to open an account
	 */
	private void openAccount(){
		bt.msgINeedAccount(this);
		s = state.waiting;
	}
	
	/**
	 * Selects between making a deposit, withdrawal, or getting a loan, 
	 * and sends a message to the teller asking for that action
	 * 
	 * @param t the task to be performed
	 */
	private void bankingAction(Task t){
		if(t.type.equals("deposit")){
			bt.msgDepositMoney(this, t.amount, accountNumber);
		}
		if(t.type.equals("withdraw")){
			bt.msgWithdrawMoney(this, t.amount, accountNumber);
		}
		if(t.type.equals("getLoan")){
			bt.msgINeedLoan(this, t.amount, accountNumber);
		}
		if(t.type.equals("robBank")){
			bt.msgThisIsAHoldup(this, t.amount);
		}
		tasks.remove(t);
		s = state.waiting;
	}
	
	/**
	 * Tells the teller that the customer is leaving, and leaves the bank
	 * Then tells the person that the customer is finished
	 */
	private void leaveBank(){
		bt.msgLeavingBank(this);
		goToLocation("Outside");
		getPerson().msgFinishedEvent(this);
		s = state.none;
	}
	
	/**
	 * Sends a message to the gui telling it to go to a location, and then waits
	 * until it receives a confirmation upon arrival
	 * 
	 * @param location the location to go to
	 */
	private void goToLocation(String location){
		if(gui != null && location != null){
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
	 * Leaves the bank, and then leaves town completely
	 */
	private void escape(){
		goToLocation("Outside");
		bt.msgLeavingBank(this);
		s = state.none;
		if(gui != null)
			gui.setPresent(false);
		person.msgBanished();
	}
	//Utilities
	
	/**
	 * Returns the name of the customer
	 */
	public String toString(){
		return name;
	}
	//Grant's addition
	public void setGui(BankCustomerGui bcg){
		this.gui = bcg;
	}
	/**
	 * A class that contains a type and amount, used to allow the 
	 * customer to tell the teller what he needs
	 * 
	 * @author Joseph Boman
	 *
	 */
	public class Task{
		public String type;
		public double amount;
		Task(String type, double amount){
			this.type = type;
			this.amount = amount;
		}
	}
	
	//The various states of the customer
	public enum state {needTeller, waiting, haveTeller, atTeller, none, runAway}
	
	public String getRoleName(){
		return roleName;
	}
	
	public utilities.Gui getGui(){
		return this.gui; 
	}
}
