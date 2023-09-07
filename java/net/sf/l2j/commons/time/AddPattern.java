package net.sf.l2j.commons.time;

import java.util.GregorianCalendar;
import java.util.TimeZone;

public class AddPattern implements NextTime
{
	private int monthInc = -1;
	private int monthSet = -1;
	private int dayOfMonthInc = -1;
	private int dayOfMonthSet = -1;
	private int hourOfDayInc = -1;
	private int hourOfDaySet = -1;
	private int minuteInc = -1;
	private int minuteSet = -1;

	public AddPattern(String pattern)
	{
		String[] parts = pattern.split("\\s+");
		if (parts.length == 2)
		{
			String datepartsstr = parts[0];
			String[] dateparts = datepartsstr.split(":");
			if (dateparts.length == 2) 
			{
				if (dateparts[0].startsWith("+")) 
					monthInc = Integer.parseInt(dateparts[0].substring(1));
				else 
					monthSet = (Integer.parseInt(dateparts[0]) - 1);
			}
			String datemodstr = dateparts[(dateparts.length - 1)];
			if (datemodstr.startsWith("+"))
				dayOfMonthInc = Integer.parseInt(datemodstr.substring(1));
			else
				dayOfMonthSet = Integer.parseInt(datemodstr);
		}
		String[] timeparts = parts[(parts.length - 1)].split(":");
		if (timeparts[0].startsWith("+"))
			hourOfDayInc = Integer.parseInt(timeparts[0].substring(1)); 
		else
			hourOfDaySet = Integer.parseInt(timeparts[0]);

		if (timeparts[1].startsWith("+")) 
			minuteInc = Integer.parseInt(timeparts[1].substring(1)); 
		else 
			minuteSet = Integer.parseInt(timeparts[1]);
	}

	@Override
	public long next(long millis)
	{
		GregorianCalendar gc = new GregorianCalendar(TimeZone.getDefault());
		gc.setTimeInMillis(millis);
		
		if (monthInc >= 0) 
			gc.add(2, monthInc);

		if (monthSet >= 0) 
			gc.set(2, monthSet);

		if (dayOfMonthInc >= 0) 
			gc.add(5, dayOfMonthInc);

		if (dayOfMonthSet >= 0)
			gc.set(5, dayOfMonthSet);

		if (hourOfDayInc >= 0)
			gc.add(11, hourOfDayInc);
		
		if (hourOfDaySet >= 0)
			gc.set(11, hourOfDaySet);
		
		if (minuteInc >= 0)
			gc.add(12, minuteInc);
		
		if (minuteSet >= 0)
			gc.set(12, minuteSet);

		return gc.getTimeInMillis();
	}
}
