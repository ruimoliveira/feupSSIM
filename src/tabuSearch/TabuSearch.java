package tabuSearch;

import java.util.ArrayList;
import java.util.Random;

import tabuSearch.PossibleSolution;
import utils.Reader;
import info.Aircraft;
import info.DATA;

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
	private static int MAX_CYCLE_NUMBER;
	private static int TABU_LIST_SIZE;
	
	private static ArrayList<PossibleSolution> possibleSolutions;
	private static PossibleSolution gBest;
	
	private static int firstBest;
	private static int gBestValue;
	
	private static Random rand;

	/**
	 * Executes the Tabu Search algorithm with given parameters. 
	 * @param max_cycle_number
	 * @param tabu_list_size
	 */
	public static void execute(int max_cycle_number, int tabu_list_size) {
		long timeline = System.currentTimeMillis();
		System.out.println("Tabu Search - initializing...");
		Reader.readAll();
		
		if (ALLOWED_TO_CANCEL) {
			DATA.addAircraft(new Aircraft()); 
		}
		
		System.out.println("\t- All data was read and initialized. \n\t- Time elapsed: "
						+ (System.currentTimeMillis() - timeline) + "ms.\n");
		clean();
		System.out.println("Initializing.");
		
		/* Initializing ABC parameters */
		initialize(max_cycle_number, tabu_list_size);

		/* Executing algorithm */
		System.out.println("Tabu Search Algorithm - Starting now...");
		tsAlgorithm();
		
		runtime = System.currentTimeMillis() - timeline;
		System.out.println("\nDone running. " 
				+ "\n\t- Time elapsed: "
				+ (System.currentTimeMillis() - timeline) + "ms.\n"
				+ "\t- First best: " + firstBest 
				+ "\n\t- Last best: " + gBestValue);
	}

	public static void clean() {
		runtime = 0;
		iteration = 0;
		iterationsData = null;
		
		FLIGHTS_COUNT = 0;
		AIRCRAFTS_COUNT = 0;
		aircrafts = null;
		ALLOWED_TO_CANCEL = false; // Allow to cancel flights, assigning NULL aircraft
		
		MAX_CYCLE_NUMBER = 0;
		TABU_LIST_SIZE = 0;
		
		possibleSolutions = null;
	
		gBest = null;
		gBestValue = Integer.MAX_VALUE;
		firstBest = Integer.MAX_VALUE;
	}

	/**
	 * Initialization of the parameters of ABC algorithm.
	 */
	public static void initialize(int max_cycle_number, int tabu_list_size) {
		aircrafts = DATA.getAircrafts();
		FLIGHTS_COUNT = DATA.getFlights().size();
		AIRCRAFTS_COUNT = aircrafts.size();
		
		MAX_CYCLE_NUMBER = max_cycle_number;
		TABU_LIST_SIZE = tabu_list_size;
		
		firstBest = 0;
		gBest = null;
		possibleSolutions = new ArrayList<PossibleSolution>();
		
		rand = new Random();
	}
	
	private static void tsAlgorithm() {
		// TODO Auto-generated method stub
		
	}
}
