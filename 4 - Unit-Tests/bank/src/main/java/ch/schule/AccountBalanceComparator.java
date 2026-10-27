package ch.schule;

import java.util.*;

public class AccountBalanceComparator implements Comparator<Object>
{
	public int compare(Object o1, Object o2)
	{
		Account a1 = (Account) o1;
		Account a2 = (Account) o2;

	  long b1 = a1.getBalance();
		long b2 = a2.getBalance();

	    return b1 < b2 ? 1 : (b1 > b2 ? -1 : 0);

	}
}
