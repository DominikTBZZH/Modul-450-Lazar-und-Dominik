package ch.schule;

public class Booking
{
	private int date;

	private long amount;

	public Booking(int date, long amount)
	{
		this.date = date;
		this.amount = amount;
	}

	public int getDate()
	{
		return date;
	}

	public long getAmount()
	{
		return amount;
	}

	public void print(long balance)
	{
		System.out.println(BankUtils.formatBankDate(date)
			+ " " + BankUtils.formatAmount(amount)
			+ " " + BankUtils.formatAmount(balance + amount));
	}
}
