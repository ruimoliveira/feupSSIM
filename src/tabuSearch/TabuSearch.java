package tabuSearch;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import tabuSearch.PossibleSolution;
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
	private static PossibleSolution initialSolution;
	
	/* Tabu Search Parameters */
	private static int MAX_CYCLE_NUMBER;
	private static int TABU_LIST_SIZE;
	
	private static LinkedList<AbstractMap.SimpleEntry<Integer, Integer>> tabuList;
	
	private static ArrayList<PossibleSolution> possibleSolutions;
	private static PossibleSolution gBest;
	
	private static int firstBest;
	private static int gBestValue;
	
	private static Random rand;
	
	/* Solution utilities */
	private static ArrayList<ArrayList<Flight>> possibleFlightPaths;
	private static ArrayList<Integer> aircraftIndexes;
	private static PossibleSolution prototypeSolution;
	private static Flight nextFlight;
	
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
		
		System.out.println("\t- All data was read. \n\t- Time elapsed: "
						+ (System.currentTimeMillis() - timeline) + "ms.\n");
		clean();
		System.out.println("Initializing.");
		
		/* Initializing Tabu Search parameters */
		initialize(max_cycle_number, tabu_list_size);
		
		System.out.println("\t- All data was initialized. \n\t- Time elapsed: "
						+ (System.currentTimeMillis() - timeline) + "ms.\n");
		System.out.println("Generating a delay on a random flight...");

		/* Generating random event */
		prepareSolutionPrototype();
		/* Generating random event */
		
		System.out.println("\t- Delay inserted, solution prototype prepared. \n\t- Time elapsed: "
						+ (System.currentTimeMillis() - timeline) + "ms.\n");
		
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
		
		possibleFlightPaths = null;
		prototypeSolution = null;
		nextFlight = null;
		aircraftIndexes = null;
	}

	/**
	 * Initialization of the parameters of ABC algorithm.
	 */
	public static void initialize(int max_cycle_number, int tabu_list_size) {
		aircrafts = DATA.getAircrafts();
		FLIGHTS_COUNT = DATA.getFlights().size();
		AIRCRAFTS_COUNT = aircrafts.size();
		
		/* initial Flights and Aircrafts */
		initialSolution = new PossibleSolution(FLIGHTS_COUNT, aircrafts);
		
		HashMap<Aircraft, ArrayList<Flight>> operationalDates = new HashMap<Aircraft, ArrayList<Flight>>();
		
		for (int index=0; index<aircrafts.size(); index++) {
			ArrayList<Flight> fl = new ArrayList<Flight>();
			
			for (Flight f : DATA.getFlights()) {
				if (f.getTail_number().equals(aircrafts.get(index).getTail_number())) {
					fl.add(f);
				}
			}

			DATA.bubbleSortFlights(fl);
			operationalDates.put(aircrafts.get(index), fl);
		}
		
		initialSolution.setMap(operationalDates);
		/* initial Flights and Aircrafts */
		
		MAX_CYCLE_NUMBER = max_cycle_number;
		TABU_LIST_SIZE = tabu_list_size;
		
		firstBest = 0;
		gBest = null;
		possibleSolutions = new ArrayList<PossibleSolution>();
		
		tabuList = new LinkedList<>();
		
		rand = new Random();
		
		possibleFlightPaths = new ArrayList<>();
		aircraftIndexes = new ArrayList<>();
	}
	
	private static void prepareSolutionPrototype() {
		/* if we wanted to delay a random flight: */
		//Flight delayedFlight = DATA.getFlights().get(rand.nextInt(DATA.getFlights().size()));
		
		/* we decided to pick one random flight on 2009-9-01 and then use the same for all the experiments */
		Flight delayedFlight = DATA.getFlights().get(5820);
		Aircraft delayedAircraft = new Aircraft();
		
		int aircraftIndex;
		for (aircraftIndex=0; aircraftIndex<aircrafts.size(); aircraftIndex++) {
			if (aircrafts.get(aircraftIndex).getTail_number().equals(delayedFlight.getTail_number())) {
				delayedAircraft = aircrafts.get(aircraftIndex);
				break;
			}
		}

		prototypeSolution = initialSolution.manualCopy(aircrafts);
		
		/* we give a manual 1h delay on the flight's departure and consequently arrival */
		ArrayList<Flight> flightPath = prototypeSolution.getMap().get(aircrafts.get(aircraftIndex));
		ArrayList<Flight> incompleteFlightPath = new ArrayList<>();
		ArrayList<Flight> flightPathToDistribute = new ArrayList<>();
		boolean hasDelayedFlight = false;
		boolean canMoveOn = false;
		for (Flight f : flightPath) {			
			/* give delay */
			if (f.getSchedule_time_of_departure().toString().equals(delayedFlight.getSchedule_time_of_departure().toString())) {
				f.setSchedule_time_of_departure(new Date("2009-09-01 09:05:00"));
				f.setSchedule_time_of_arrival(new Date("2009-09-01 10:20:00"));
				delayedFlight = f;
				hasDelayedFlight = true;
			}
			
			/* generate new flight paths to add to airplane schedule (hashmap) and to undistributedFlightPaths*/
			if (hasDelayedFlight && canMoveOn) {
				flightPathToDistribute.add(f);
				if (nextFlight == null) {
					nextFlight = f;
				}
			} else {
				incompleteFlightPath.add(f);
				if (hasDelayedFlight)
					canMoveOn = true;
			}
		}
		
		/*set flight paths*/
		ArrayList<ArrayList<Flight>> undistributedFlightPaths = new ArrayList<>();
		undistributedFlightPaths.add(flightPathToDistribute);
		prototypeSolution.getMap().put(aircrafts.get(aircraftIndex), incompleteFlightPath);
		aircraftIndexes.add(aircraftIndex);
		
		/* frees all aircrafts of the same model to re-distribute flight paths among them on the TS algorithm */
		for (int i=0; i<aircrafts.size(); i++) {
			incompleteFlightPath = new ArrayList<>();
			flightPathToDistribute = new ArrayList<>();
			
			Aircraft a = aircrafts.get(i);
			flightPath = prototypeSolution.getMap().get(a);
			if (a.getAircraft_model().equals(delayedAircraft.getAircraft_model()) && i != aircraftIndex) {
				for (Flight f : flightPath) {
					if (f.getSchedule_time_of_arrival().compareTo(nextFlight.getSchedule_time_of_departure()) == -1) {
						incompleteFlightPath.add(f);
					} else {
						flightPathToDistribute.add(f);
					}
				}
			
				undistributedFlightPaths.add(flightPathToDistribute);
				prototypeSolution.getMap().put(a, incompleteFlightPath);
				aircraftIndexes.add(i);
			}
		}
		
		possibleFlightPaths = undistributedFlightPaths;
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
		/* A solution is given by it's aircraftIndexes
		 * i.e.
		 * given the arraylist<Integer> aircraftIndexes
		 * it's values correspond to the index in this.aircrafts
		 * and it's indexes give the flightPathIndex in this.possibleFlightPaths
		 * 
		 * so shuffling them both gives a random solution
		 * */
		Collections.shuffle(possibleFlightPaths);
		Collections.shuffle(aircraftIndexes);
		
		/* From here on, it is easier to save only aircraftIndexes onto PossibleSolution
		 * because we can get all the solution information from that arraylist
		 * */
		PossibleSolution possibleSolution = prototypeSolution.manualCopy(aircrafts);
		ArrayList<Integer> solution = new ArrayList<>(aircraftIndexes);
		prototypeSolution.setSolution(aircraftIndexes, possibleFlightPaths);
		possibleSolution.setSolution(solution, possibleFlightPaths);

		return possibleSolution;
		/*
		Flight firstFlightInSchedule = availableFlightPaths.get(flightPathIndex).get(0);
		Aircraft a = aircrafts.get(aircraftIndex);
		ArrayList<Flight> aFlightPath = possibleSolution.getMap().get(a);
		Flight aircraftsLastFlight = aFlightPath.get(aFlightPath.size()-1);
		*/
		
		/*
		// dataset for checking feasibility
		HashMap<Aircraft, ArrayList<Flight>> operationalDates = new HashMap<Aircraft, ArrayList<Flight>>();
		for (Aircraft ac : aircrafts) {
			operationalDates.put(ac, new ArrayList<Flight>());
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
			
			while(!isFeasible(operationalDates, aircrafts.get(aircraftIndex), flight)) {
				aircraftIndex = rand.nextInt(AIRCRAFTS_COUNT);
				newPair.setAircraft(aircrafts.get(aircraftIndex), aircraftIndex);
			}
			tempPairs.add(newPair);
			operationalDates.get(aircrafts.get(aircraftIndex)).add(flight);
		}
		
		// pairing possibleSolution -> list<flight, aircraft>
		if (ps.setPair(tempPairs) != FLIGHTS_COUNT)
			System.out.println("Wrong flight-aircraft pairing! - init phase");
		
		// computing the value of objective function for the food source
		ps.setMap(operationalDates);
		ps.computeObjectiveFunction();
		*/
	}

	/**
	 * Checks if aircraft already operates on the certain date in same solution
	 * @param operationalDates
	 * @param aircraft
	 * @param flight
	 * @return true if aircraft is free on flight_date
	 */
	private static boolean isFeasible(HashMap<Aircraft, ArrayList<Flight>> operationalDates, Aircraft aircraft, Flight flight) {
		
		for (Flight f : operationalDates.get(aircraft)) {
			if ( ! ((f.getSchedule_time_of_departure().compareTo(flight.getSchedule_time_of_departure()) == -1 &&
					f.getSchedule_time_of_arrival().compareTo(flight.getSchedule_time_of_departure()) == -1) || 
					(f.getSchedule_time_of_departure().compareTo(flight.getSchedule_time_of_arrival()) == 1 &&
					f.getSchedule_time_of_arrival().compareTo(flight.getSchedule_time_of_arrival()) == 1)) )
				return false;
		}
		
		return true;
	}
}
