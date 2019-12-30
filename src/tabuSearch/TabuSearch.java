package tabuSearch;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import tabuSearch.PossibleSolution;
import utils.Pair;
import utils.Reader;
import info.Aircraft;
import info.DATA;
import info.Date;
import info.Flight;

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
	
	private static LinkedList<AbstractMap.SimpleEntry<Integer, Integer>> tabuList;
	
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
		
		tabuList = null;
	
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
		
		tabuList = new LinkedList<>();
		
		rand = new Random();
	}
	
	private static void tsAlgorithm() {
		
		/* create a random solution */
		PossibleSolution sBest = randomSolution();
		
		for(iteration=1; iteration <= MAX_CYCLE_NUMBER; iteration++) {
			/* create solution candidates array*/
			ArrayList<PossibleSolution> candidateList = new ArrayList<>();

			// TODO:
			/* generate all sBest neighborhood solutions */
			/*
		    For ( Scandidate : Sbest-neighborhood )
		        If ( ! ContainsAnyFeatures( Scandidate , TabuList))
		            CandidateList .add (Scandidate)
		        End
		    End
		    */
			
			/* get best candidate */
		    // sCandidate = LocateBestCandidate(CandidateList)
			
			/*
		    If (Cost(Scandidate) <= Cost(sBest))
		          sBest = Scandidate
		        TabuList .add FeatureDifferences(Scandidate , sBest)
		        While (TabuListSize > TabuListMaxSize)
		            DeleteFeature(TabuList)
		        End
		    End
		    */
		}
	}

	private static PossibleSolution randomSolution() {
		PossibleSolution ps = new PossibleSolution(FLIGHTS_COUNT, aircrafts);
		
		// dataset for checking feasibility
		HashMap<Aircraft, ArrayList<Date>> operationalDates = new HashMap<Aircraft, ArrayList<Date>>();
		for (Aircraft ac : aircrafts) {
			operationalDates.put(ac, new ArrayList<Date>());
		}
		
		// create random solution -> contains set of pairs <flight, aircraft>, it's fitness, ...
		ArrayList<Pair> tempPairs = new ArrayList<Pair>();
		for (int j = 0; j < FLIGHTS_COUNT; j++) {
			int flightIndex = j;
			int aircraftIndex = rand.nextInt(AIRCRAFTS_COUNT);

			Pair newPair = new Pair();
			Flight flight = DATA.getFlights().get(flightIndex);
			newPair.setFlight(flight);
			newPair.setAircraft(aircrafts.get(aircraftIndex), aircraftIndex);
			
			while(!isFeasible(operationalDates, aircrafts.get(aircraftIndex), flight.getFlight_date())) {
				aircraftIndex = rand.nextInt(AIRCRAFTS_COUNT);
				newPair.setAircraft(aircrafts.get(aircraftIndex), aircraftIndex);
			}
			tempPairs.add(newPair);
			operationalDates.get(aircrafts.get(aircraftIndex)).add(flight.getFlight_date());
		}
		
		// pairing possibleSolution -> list<flight, aircraft>
		if (ps.setPair(tempPairs) != FLIGHTS_COUNT)
			System.out.println("Wrong flight-aircraft pairing! - init phase");
		
		// computing the value of objective function for the food source
		ps.setMap(operationalDates);
		ps.computeObjectiveFunction();
		
		return ps;
	}

	/**
	 * Checks if aircraft already operates on the certain date in same solution
	 * @param operationalDates
	 * @param aircraft
	 * @param flight_date
	 * @return true if aircraft is free on flight_date
	 */
	private static boolean isFeasible(HashMap<Aircraft, ArrayList<Date>> operationalDates, Aircraft aircraft,
			Date flight_date) {
		if (operationalDates.get(aircraft).contains(flight_date)) return false;
		return true;
	}
}
