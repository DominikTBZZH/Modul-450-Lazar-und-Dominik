package ch.schule;

import java.text.*;

public class BankUtils
{
	public static final DecimalFormat TWO_DIGIT_FORMAT
		= new DecimalFormat("00");

	public static final DecimalFormat AMOUNT_FORMAT
		= new DecimalFormat("#,##0.00");

	public static String formatBankDate(int date)
	{
	    int year = 1970 + date / 360;

	    date %= 360;

	    int month = 1 + date / 30;
		int day = 1 + date % 30;

	    String s =
			TWO_DIGIT_FORMAT.format(day) + "."
			+ TWO_DIGIT_FORMAT.format(month) + "."
			+ year;

		return s;
	}

	public static String formatAmount(long amount)
	{
		String s = AMOUNT_FORMAT.format(amount / 100000.0);

		while (s.length() < 10)
			s = " " + s;

		return s;
	}
}
