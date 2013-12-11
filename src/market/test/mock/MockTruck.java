package market.test.mock;

import java.util.List;

import restaurant1.Restaurant1CookRole;
import utilities.restaurant.RestaurantCook;
import market.Food;
import market.interfaces.MarketTruck;

public class MockTruck extends Mock implements MarketTruck{

	public EventLog log = new EventLog();
	
	public MockTruck(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void msgGoBack() {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void gotoPosition(RestaurantCook c, List<Food> food,
			int restaurantnum) {
		log.add(new LoggedEvent("Got it"));
		
	}

}
