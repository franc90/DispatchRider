package dtp.jade.agentcalendar;

import java.util.Iterator;
import java.util.LinkedList;

// dla LinkedList glowa ma index 0
// tu index 0 ma ostatnie zlecenie do wykonania
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ActionQueueWithGraph<E> extends LinkedList {

	private static final long serialVersionUID = -7062551746329575942L;

	public CalendarActionWithGraph getPreviousAction(
			CalendarActionWithGraph action) {

		if (size() <= 1)
			return null;

		if (indexOf(action) < size() - 1) {
			return (CalendarActionWithGraph) get(indexOf(action) + 1);
		}

		return null;
	}

	public CalendarActionWithGraph getPrevAction(CalendarActionWithGraph action) {

		if (size() <= 1)
			return null;

		if (indexOf(action) < this.size() - 1) {
			return (CalendarActionWithGraph) get(indexOf(action) + 1);
		}

		return null;
	}

	public CalendarActionWithGraph getNextAction(CalendarActionWithGraph action) {

		if (size() <= 1)
			return null;

		if (indexOf(action) > 0) {
			return (CalendarActionWithGraph) get(indexOf(action) - 1);
		}

		return null;
	}

	// wklada akcje actionToPut za akcje actionToPutAfter (blizej indexu 0)
	public void putActionAfter(CalendarActionWithGraph actionToPut,
			CalendarActionWithGraph actionToPutAfter) {

		add(indexOf(actionToPutAfter), actionToPut);
	}

	// usuwa akcje actionToRemove
	public void removeAction(CalendarActionWithGraph actionToRemove) {

		remove(actionToRemove);
	}

	public void print() {

		Iterator iter = iterator();
		CalendarActionWithGraph action;

		while (iter.hasNext()) {

			action = (CalendarActionWithGraph) iter.next();
			action.print();
		}
	}

	public ActionQueueWithGraph backup() {

		ActionQueueWithGraph tmpActionQueue;
		CalendarActionWithGraph tmpAction;
		Iterator iter;

		tmpActionQueue = new ActionQueueWithGraph();
		iter = this.iterator();

		while (iter.hasNext()) {

			tmpAction = (CalendarActionWithGraph) iter.next();
			tmpActionQueue.add(tmpAction.clone());
		}

		return tmpActionQueue;
	}
}
