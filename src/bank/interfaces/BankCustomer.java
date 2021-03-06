package bank.interfaces;

/** 
 * A basic BankCustomer interface containing the basic messaging calls
 * 
 * @author Joseph
 *
 */
public interface BankCustomer {	
	public void msgGoToBank(String type, double money);
	
	public void msgNewLocation(String location);
	
	public void msgAtDestination();
	
	public void msgHereIsTeller(BankTeller bt, String location);
	
	public void msgAccountMade(int accountNumber);
	
	public void msgDepositDone(double balance, double money);
	
	public void msgWithdrawDone(double balance, double money);
	
	public void msgLoanGranted(double money, double debt);
	
	public void msgRequestFailed(String type);
	
	public void msgCallingCops();

	public void msgHereIsMoney(double amount);
}
