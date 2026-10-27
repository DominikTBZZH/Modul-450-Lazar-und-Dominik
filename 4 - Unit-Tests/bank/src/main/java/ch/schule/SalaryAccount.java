package ch.schule;

public class SalaryAccount extends Account
{
	private long creditLimit;

	public SalaryAccount(String id, long creditLimit)
	{
		super(id);

		this.creditLimit = creditLimit;
	}

	public boolean withdraw(int date, long amount)
	{
		long finalBalance = getBalance() - amount;

	    if (finalBalance < creditLimit)
			return false;

	    return super.withdraw(date, amount);
	}
}
