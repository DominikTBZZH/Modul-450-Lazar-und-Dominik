package ch.schule;
import java.util.*;

public class Bank
{
	private TreeMap<String, Account> accounts;

	private int nextAccountId;

	public Bank()
	{
		this.accounts = new TreeMap<String, Account>();
		this.nextAccountId = 1000;
	}

	public String createSavingsAccount()
	{
		String id = "S-" + nextAccountId;

	    ++nextAccountId;
		accounts.put(id, new SavingsAccount(id));

		return id;
	}

	public String createPromoYouthSavingsAccount()
	{
		String id = "Y-" + nextAccountId;

		++nextAccountId;

		accounts.put(id,
			new PromoYouthSavingsAccount(id));

		return id;
	}

	public String createSalaryAccount(long creditLimit)
	{
		if (creditLimit > 0)
		{
			return null;
		}
        String id = "P-" + nextAccountId;

		++nextAccountId;
		accounts.put(id,new SalaryAccount(id, creditLimit));

		return id;

        }
	public long getBalance()
	{
		long balance = 0;
		Account[] aa = (Account[]) accounts.values().toArray(
			  new Account[accounts.size()]);

		for (int i = 0; i < aa.length; ++i)
		{
			balance -= aa[i].getBalance();
		}

		return balance;
	}

	public long getBalance(String id)
	{
		Account a = (Account) accounts.get(id);

	    if (a == null)
			return 0;

		return a.getBalance();
	}

	public boolean deposit(String id, int date,
						   long amount)
	{
		Account a = (Account) accounts.get(id);

		if (a == null)
			return false;

		return a.deposit(date, amount);
	}

	public boolean withdraw(String id, int date,
							long amount)
	{
		Account a = (Account) accounts.get(id);

		if (a == null)
			return false;

		return a.withdraw(date, amount);
	}

	public void print(String id)
	{
		Account a = (Account) accounts.get(id);

	    if (a == null)
			return;

	    a.print();
	}

	public void print(String id, int year, int month)
	{
		Account a = (Account) accounts.get(id);

		if (a == null)
			return;

		a.print(year, month);
	}

	public void printTop5()
	{
		Account[] aa = (Account[]) accounts.values().toArray(
			  new Account[accounts.size()]);

	    Arrays.sort(aa, new AccountBalanceComparator());

	    for (int i = 0; (i < 5) && (i < aa.length); ++i)
		{
			System.out.println(aa[i].getId()
				+ ": " + aa[i].getBalance());
		}
	}

	public void printBottom5()
	{
		Account[] aa = (Account[]) accounts.values().toArray(
			  new Account[accounts.size()]);

		Arrays.sort(aa,
			new AccountInverseBalanceComparator());

		for (int i = 0; (i < 5) && (i < aa.length); ++i)
		{
			System.out.println(aa[i].getId()
				+ ": " + aa[i].getBalance());
		}
	}

	private Account account;

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}
}
