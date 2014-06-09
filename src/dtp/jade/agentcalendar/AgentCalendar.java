package dtp.jade.agentcalendar;

import java.util.List;

import dtp.commission.Commission;

public interface AgentCalendar {
	public List<CalendarAction> getSchedule();
	public double addCommission(Commission com, int timestamp);
}
