package TabuSearch;

import java.util.ArrayList;

import info.Aircraft;

/**
 * Class representing the Tabu Search algorithm.
 * @authors Rui Miguel Oliveira, Antonio Miguel Pereira
 *
 */
public class TabuSearch {
	/* Performance statistics */
	private static long runtime;
	private static int iteration;
	private static String iterationsData;
	
	/* Airport data */
	private static int FLIGHTS_COUNT;
	private static int AIRCRAFTS_COUNT;
	private static ArrayList<Aircraft> aircrafts;
	private static boolean ALLOWED_TO_CANCEL; // Allow to cancel flights, assigning NULL aircraft
	
	/* Tabu Search Parameters */
	private static int TABU_LIST_SIZE;
	
	
}
