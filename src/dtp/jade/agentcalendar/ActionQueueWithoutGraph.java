package dtp.jade.agentcalendar;

import java.util.Iterator;
import java.util.LinkedList;

// dla LinkedList glowa ma index 0
// tu index 0 ma ostatnie zlecenie do wykonania
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ActionQueueWithoutGraph<E> extends LinkedList {

	private static final long serialVersionUID = -7062551746329575942L;

	public CalendarActionWithoutGraph getPreviousAction(
			CalendarActionWithoutGraph action) {

		if (size() <= 1)
			return null;

		if (indexOf(action) < size() - 1) {
			return (CalendarActionWithoutGraph) get(indexOf(action) + 1);
		}

		return null;
	}

	public CalendarActionWithoutGraph getNextAction(
			CalendarActionWithoutGraph action) {

		if (size() <= 1)
			return null;

		if (indexOf(action) > 0) {
			return (CalendarActionWithoutGraph) get(indexOf(action) - 1);
		}

		return null;
	}

	// wklada akcje actionToPut za akcje actionToPutAfter (blizej indexu 0)
	public void putActionAfter(CalendarActionWithoutGraph actionToPut,
			CalendarActionWithoutGraph actionToPutAfter) {

		add(indexOf(actionToPutAfter), actionToPut);
	}

	// usuwa akcje actionToRemove
	public void removeAction(CalendarActionWithoutGraph actionToRemove) {

		remove(actionToRemove);
	}

	public void print() {

		Iterator iter = iterator();
		CalendarActionWithoutGraph action;

		while (iter.hasNext()) {

			action = (CalendarActionWithoutGraph) iter.next();
			action.print();
		}
	}

	public ActionQueueWithoutGraph backup() {

		ActionQueueWithoutGraph tmpActionQueue;
		CalendarActionWithoutGraph tmpAction;
		Iterator iter;

		tmpActionQueue = new ActionQueueWithoutGraph();
		iter = this.iterator();

		while (iter.hasNext()) {

			tmpAction = (CalendarActionWithoutGraph) iter.next();
			tmpActionQueue.add(tmpAction.clone());
		}

		return tmpActionQueue;
	}
}
