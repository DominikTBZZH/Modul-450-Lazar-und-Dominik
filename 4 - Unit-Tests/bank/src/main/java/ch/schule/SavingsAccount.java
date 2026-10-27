package ch.schule;

public class SavingsAccount extends Account
{
	public SavingsAccount(String id)
	{
		super(id);
	}

	public boolean withdraw(int date, long amount)
	{
		if (getBalance() < amount)
			return false;

		return super.withdraw(date, amount);
	}
}
