package ch.schule;

import java.util.*;

public abstract class Account
{
	private String id;

	private long balance;

	private ArrayList<Booking> bookings;

	public Account(String id)
	{
		this.id = id;
		this.balance = 0;
		this.bookings = new ArrayList<Booking>();
	}

	public String getId()
	{
		return id;
	}

	public long getBalance()
	{
		return balance;
	}

	public boolean canTransact(int date)
	{
		if (bookings.isEmpty())
			return true;

	    Booking b = (Booking) bookings.get(bookings.size() - 1);

	    return date >= b.getDate();
	}

	public boolean deposit(int date, long amount)
	{
		if (amount < 0)
			return false;

	    if (!canTransact(date))
			return false;

	    balance += amount;
		bookings.add(new Booking(date, amount));
		return true;
	}

	public boolean withdraw(int date, long amount)
	{
		if (amount < 0)
			return false;

		if (!canTransact(date))
			return false;

	    balance -= amount;
		bookings.add(new Booking(date, -amount));
		return true;
	}

	public void print()
	{
		System.out.println("Kontoauszug '" + id + "'");
		System.out.println("Datum          Betrag      Saldo");

	    long balance = 0;

	    for (int i = 0; i < bookings.size(); ++i)
		{
			Booking b = (Booking) bookings.get(i);

		    b.print(balance);
			balance += b.getAmount();
		}
	}

	public void print(int year, int month)
	{
		System.out.println("Kontoauszug '" + id + "' "
			+ "Monat: " + month + "." + year);
	    System.out.println("Datum          Betrag      Saldo");

	    int startDate = (year - 1970) * 360
			+ (month - 1) * 30;
		int endDate = startDate + 30;
		long balance = 0;

		for (int i = 0; i < bookings.size(); ++i)
		{
			Booking b = (Booking) bookings.get(i);

			if (b.getDate() >= endDate)
				break;

			if (b.getDate() >= startDate)
				b.print(balance);

		    balance += b.getAmount();
		}
	}

	private Booking booking;

	public Booking getBooking() {
		return booking;
	}

	public void setBooking(Booking booking) {
		this.booking = booking;
	}
}
