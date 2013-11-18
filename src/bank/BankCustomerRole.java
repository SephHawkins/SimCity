package bank;

import bank.interfaces.*;
import agent.*;
import bank.test.mock.*;
import java.util.*;

public class BankCustomerRole extends Agent implements BankCustomer {
	//Data
	String name;
	public EventLog log;
	List<Task> tasks;
	int accountNumber;
	double balance;
	public BankTeller bt;
	state s;
	
	public BankCustomerRole(String name){
		this.name = name;
		tasks = new ArrayList<Task>();
		log = new EventLog();
		accountNumber = -1;
	}
	
	//Messages
	public void msgGoToBank(String task, double amount){
		tasks.add(new Task(task, amount));
		s = state.needTeller;
		stateChanged();
	}
	
	public void msgHereIsTeller(BankTeller bt){
		this.bt = bt;
		s = state.haveTeller;
		stateChanged();
	}
	
	public void msgAccountMade(int accountNumber){
		log.add(new LoggedEvent("Received msgAccountMade from BankTeller"));
		Do("Received account with account number " + accountNumber);
		this.accountNumber = accountNumber;
		this.balance = 0;
		this.s = state.atTeller;
		stateChanged();
	}
	
	public void msgDepositDone(double balance){
		log.add(new LoggedEvent("Received msgDepositDone from BankTeller"));
		this.balance = balance;
		this.s = state.atTeller;
		stateChanged();
	}
	//Scheduler
	protected boolean pickAndExecuteAnAction(){
		if(s == state.needTeller){
			informHost();
			return true;
		}
		if(s == state.haveTeller){
			//goToLocation("Teller");
			s = state.atTeller;
			return true;
		}
		if(s == state.atTeller && accountNumber == -1){
			openAccount();
			return true;
		}
		if(s == state.atTeller && !tasks.isEmpty()){
			bankingAction(tasks.get(0));
			return true;
		}
		return false;
	}
	//Actions
	private void informHost(){
		//goToLocation("Host");
		//bh.iNeedTeller();
		s = state.atTeller;
	}
	
	private void openAccount(){
		Do("Requesting account");
		bt.msgINeedAccount(this);
		s = state.waiting;
	}
	
	private void bankingAction(Task t){
		Do("Requesting deposit");
		bt.msgDepositMoney(this, t.amount, accountNumber);
		tasks.remove(t);
		s = state.waiting;
	}
	//Utilities
	public String toString(){
		return name;
	}
	
	class Task{
		String type;
		double amount;
		Task(String type, double amount){
			this.type = type;
			this.amount = amount;
		}
	}
	
	enum state {needTeller, waiting, haveTeller, atTeller, runningAway, none}
}
