package ch.schule;

public class PromoYouthSavingsAccount extends SavingsAccount
{
	public PromoYouthSavingsAccount(String id)
	{
		super(id);
	}

	public boolean deposit(int date, long amount)
	{
	    long bonus = amount / 100;

	    return super.deposit(date, amount + bonus);
	}
}
