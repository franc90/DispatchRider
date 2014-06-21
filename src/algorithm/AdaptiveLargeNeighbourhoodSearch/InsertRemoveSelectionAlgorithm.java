package algorithm.AdaptiveLargeNeighbourhoodSearch;

import util.selector.BalancedItemSelector;
import util.selector.DefaultItemSelector;
import util.selector.Item;
import util.selector.ItemSelector;
import util.selector.LearningItemSelector;
import util.selector.RandomItemSelector;
import algorithm.AdaptiveLargeNeighbourhoodSearch.insert.InsertMethod;
import algorithm.AdaptiveLargeNeighbourhoodSearch.insert.item.GreedyForbiddenInsertItem;
import algorithm.AdaptiveLargeNeighbourhoodSearch.insert.item.GreedyInsertItem;
import algorithm.AdaptiveLargeNeighbourhoodSearch.insert.item.GreedyPerturbedInsertItem;
import algorithm.AdaptiveLargeNeighbourhoodSearch.insert.item.RegretInsertItem;
import algorithm.AdaptiveLargeNeighbourhoodSearch.removal.RemovalMethod;
import algorithm.AdaptiveLargeNeighbourhoodSearch.removal.item.RandomRemovalItem;
import algorithm.AdaptiveLargeNeighbourhoodSearch.removal.item.ShawRemovalBalancedItem;
import algorithm.AdaptiveLargeNeighbourhoodSearch.removal.item.ShawRemovalDistanceItem;
import algorithm.AdaptiveLargeNeighbourhoodSearch.removal.item.ShawRemovalTimeItem;
import algorithm.AdaptiveLargeNeighbourhoodSearch.removal.item.ShawRemovalCapacityItem;
import algorithm.AdaptiveLargeNeighbourhoodSearch.removal.item.WorstRemovalItem;

public class InsertRemoveSelectionAlgorithm {
	
	private static boolean initialized = false;

	private static ItemSelector<InsertMethod> insertMethodSelector;

	private static ItemSelector<RemovalMethod> removalMethodSelector;

	public static void setSelectionAlgorithm(String selectionAlgorithm){
		if (!initialized){
			System.err.println("=== SELECTION ALGORITHM === ");
			if (selectionAlgorithm.equals("default")){
				System.err.println("=== DEFAULT === ");
				setDefaultSelectionAlgorithm();
			}			
			if (selectionAlgorithm.equals("random")){
				System.err.println("=== RANDOM === ");
				setRandomSelectionAlgorithm();
			}
			
			if (selectionAlgorithm.equals("balanced")){
				System.err.println("=== BALANCED === ");
				setBalancedSelectionAlgorithm();
			}
			
			if (selectionAlgorithm.equals("learning")){
				System.err.println("=== LEARNING === ");
				setLearningSelectionAlgorithm();
			}
			initialized = true;
		}
	}
	
	private static void setDefaultSelectionAlgorithm(){
		insertMethodSelector = new DefaultItemSelector<InsertMethod>();
		removalMethodSelector = new DefaultItemSelector<RemovalMethod>();
		addMethodSelectors();
	}
	
	private static void setRandomSelectionAlgorithm(){
		insertMethodSelector = new RandomItemSelector<InsertMethod>();
		removalMethodSelector = new RandomItemSelector<RemovalMethod>();
		addMethodSelectors();
	}
	
	private static void setBalancedSelectionAlgorithm(){
		insertMethodSelector = new BalancedItemSelector<InsertMethod>();
		removalMethodSelector = new BalancedItemSelector<RemovalMethod>();
		addMethodSelectors();
	}
	
	private static void setLearningSelectionAlgorithm(){
		insertMethodSelector = new LearningItemSelector<InsertMethod>();
		removalMethodSelector = new LearningItemSelector<RemovalMethod>();
		addMethodSelectors();
	}
	
	private static void addMethodSelectors(){
		insertMethodSelector.addItem(new RegretInsertItem(3));
		insertMethodSelector.addItem(new GreedyInsertItem(1));
		insertMethodSelector.addItem(new GreedyPerturbedInsertItem(1));
		insertMethodSelector.addItem(new GreedyForbiddenInsertItem(1));

		removalMethodSelector.addItem(new ShawRemovalBalancedItem(3));
		removalMethodSelector.addItem(new ShawRemovalTimeItem(2));
		removalMethodSelector.addItem(new ShawRemovalDistanceItem(2));
		removalMethodSelector.addItem(new ShawRemovalCapacityItem(2));
		removalMethodSelector.addItem(new WorstRemovalItem(1));
		removalMethodSelector.addItem(new RandomRemovalItem(1));		
	}

	public static Item<InsertMethod> getInsertMethod() {
		return insertMethodSelector.getItem();
	}

	public static Item<RemovalMethod> getRemovalMethod() {
		return removalMethodSelector.getItem();
	}

}
