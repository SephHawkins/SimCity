package person.test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import person.Bank;
import person.SimEvent;
import person.PersonAgent;
import person.Position;
import person.SimEvent.EventType;
import person.gui.PersonGui;
import person.Location.LocationType;
import person.Location;
import simcity.CityMap;
import utilities.TimeCard;
import bank.*;
/*
 * Tests the entrance role handoff between the TimeCard and the Person
 * sends a role on work and the person will begin calling the role's 
 * scheduler
 * 
 * @author Grant Collins
 */
public class BankCustomerEntrance extends TestCase{
	
	PersonAgent person;
	BankHostRole host;
	SimEvent goToBank;
	Bank bank;
	Bank bank2;
	Position p = new Position(10, 10);
	Position p2 = new Position(40, 40);
	List<Location> locs = new ArrayList<Location>();
	public void setUp() throws Exception{
		
		super.setUp();	
		host = new BankHostRole(new PersonAgent(), "Gil");
		TimeCard tc = new TimeCard();
		tc.startThread();
		bank = new Bank("Banco Popular", tc, host, p, LocationType.Bank);
		bank2 = new Bank("Banco Popular 2", tc, host, p2, LocationType.Bank);
		bank.isClosed = false;
		bank2.isClosed = false;
		goToBank = new SimEvent("deposit", bank, EventType.CustomerEvent);
		locs.add(bank);
		locs.add(bank2);
		CityMap cm = new CityMap(locs);
		person = new PersonAgent("Grant", cm, 0);
		PersonGui pgui = new PersonGui(person, null);
		person.gui = pgui;
		person.testMode = true;
	}
	
	@Test
	public void testCustomerEntrance() {
		
		//Pre : Check event queue and activeRole
		
		assertTrue("The person we are testing (person) should have no events at creation, it does", person.toDo.size() == 0);
		assertTrue("person should have no active roles at creation, activeRole is true", !person.active());
		
		person.msgNewHour(9); 
		assertTrue("person's time should be 9, it is not", person.getTime() == 9);
		
		//Add the goToRestaurant event
		person.toDo.add(goToBank);
		assertTrue("person's toDo should now contain goToRestaurant, it does not", person.toDo.get(0) == goToBank);
		assertTrue("person's scheduler should return true because we have added one event to his queue", person.pickAndExecuteAnAction());
		
		//Check customer role creation is correct
		assertTrue("person should now have a customer role in his roles list, he does not", person.roles.get(0).role instanceof BankCustomerRole);
		assertTrue("the customer's person pointer should be equivalent to person it is not", person.roles.get(0).role.getPerson() == person);
		
		//the activity beyond the entrance up until exit is up to the person in charge of said role so we needn't test that
		
		//Now test whether the person scheduler runs or blocks
		assertTrue("person's activeRole should be true, it is not", person.active());
		
		person.msgFinishedEvent(person.roles.get(0).role);
		assertTrue("The person's activeRole should return false, it is still true", person.getActiveRole() == null);
		assertTrue("The person's customer role should be false, it is not it is active still", person.roles.get(0).isActive == false);
	}

}
