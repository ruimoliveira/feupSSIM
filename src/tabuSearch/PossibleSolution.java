package tabuSearch;

import info.Aircraft;
import info.DATA;
import info.Flight;
import info.Date;

import java.util.ArrayList;
import java.util.HashMap;
import tabuSearch.PossibleSolution;
import utils.Pair;

/**
 * Class represents one possible solution for flight scheduling.
 */
public class PossibleSolution implements Comparable<PossibleSolution> {
	private int DIMENSION;
	private int fitness;
	private HashMap<Aircraft, ArrayList<Flight>> map;
	private ArrayList<Integer> solution;
	private ArrayList<ArrayList<Flight>> newFlightPaths;
	private Integer[] tabuListEntrie = new Integer[2];

	
	/**
	 * @param flightIndices array containing on position i the 
	 * index of aircraft for flight i; size of array is the number 
	 * of flights
	 */
	public PossibleSolution(int dimension, ArrayList<Aircraft> aircrafts) {
		this.DIMENSION = dimension;
		this.fitness = 0; // objective function
		this.map = new HashMap<Aircraft, ArrayList<Flight>>();
		for (Aircraft ac : aircrafts) {
			map.put(ac, new ArrayList<Flight>());
		}
		this.solution = new ArrayList<Integer>();
		this.newFlightPaths = new ArrayList<ArrayList<Flight>>();
		this.tabuListEntrie = new Integer[2];
	}
	
	public void buildSolution(ArrayList<Aircraft> aircrafts) {
		for (int flightPathIndex=0; flightPathIndex<solution.size(); flightPathIndex++) {
			ArrayList<Flight> newFlightPath = new ArrayList<Flight>();
			int aircraftIndex = solution.get(flightPathIndex);
			newFlightPath.addAll(map.get(aircrafts.get(aircraftIndex)));
			newFlightPath.addAll(newFlightPaths.get(flightPathIndex));
			this.map.put(aircrafts.get(aircraftIndex), newFlightPath);
		}
	}
	
	public void computeFitness(ArrayList<Aircraft> aircrafts){
		double value = 0.0;
		
		for (Aircraft a : aircrafts) {
			Flight previousFlight = null;
			ArrayList<Flight> flightPath = new ArrayList<Flight>(map.get(a));
			
			/* Aircraft info */
			String model = a.getAircraft_model();

			int aircraftModelSize = DATA.getAircraft_models().size(),
				airport_handling_cost = 0;
			double fuel_per_minute = 0.0,
				maintenance_perminute = 0.0,
				atc_per_mile = 0.0;
			String fleet_model = null;
			for(int j = 0; j < aircraftModelSize; j++){
				if(DATA.getAircraft_models().get(j).getAircraft_model().equals(model)){
					/*Handling modifier*/
					airport_handling_cost = DATA.getAircraft_models().get(j).getAirport_handling_cost();
					
					/*Fuel Cost modifier*/
					fuel_per_minute = DATA.getAircraft_models().get(j).getFuel_avg_cost_minute();
					
					/*Maintenance Cost modifier*/
					maintenance_perminute = DATA.getAircraft_models().get(j).getMaintenance_avg_cost_minute();

					/*ATC Cost modifier*/
					atc_per_mile = DATA.getAircraft_models().get(j).getAtc_avg_cost_nautical_mile();
					
					/*Fleet Model*/
					fleet_model = DATA.getAircraft_models().get(j).getFleet();
				}
			}
			/* Aircraft info */
			
			/* Compute each flight cost */
			for (int flightIndex=0; flightIndex<flightPath.size(); flightIndex++) {
				Flight nextFlight = flightPath.get(flightIndex);
				
				double distanceKM = 0.0,
						distanceMiles = 0.0,
						parkTime = 0.0;
				
				if (previousFlight != null) {
					/* verify if flight is departing the airport it arrived on */
					if (!previousFlight.getDestination().equals(nextFlight.getOrigin())) {
						/* if not: make new flight to that airport */
						
						/*Distance Between Airports*/
						String flightOrigin = nextFlight.getOrigin();
						String flightDestination = previousFlight.getDestination();
						int cityPairSize = DATA.getCity_pairs().size();
						for(int k = 0; k < cityPairSize; k++){
							if(DATA.getCity_pairs().get(k).validateOriDest(flightOrigin, flightDestination)){
								distanceKM = DATA.getCity_pairs().get(k).getDistance_in_km();
								distanceMiles = DATA.getCity_pairs().get(k).getDistance_in_nautical_miles();
								break;
							}
						}
						/*Distance Between Airports*/
						
						/*Time to get there*/
						int flightTime = nextFlight.getSchedule_time_of_arrival().difWithMinutes(previousFlight.getSchedule_time_of_departure());
						/*Time to get there*/
						
						/* if not possible to fly there: apply major penalty cost 
						 * (arbitrarily, it takes at least 10 minutes per km for a plane to get somewhere)
						 * */
						if (distanceKM/flightTime > 10.0) {
							value += 100000.0;
						}
						/* introduce new flight cost */
						else {
							value += computeFlightCost(fleet_model, flightOrigin, flightDestination, flightTime, 0, airport_handling_cost, fuel_per_minute, maintenance_perminute, atc_per_mile, distanceMiles);
						}

						/* No park time because */
						parkTime = 0.0;
						
					} else {
						/* Computes park time from previous flight */
						parkTime = previousFlight.getSchedule_time_of_departure().difWithMinutes(nextFlight.getSchedule_time_of_arrival());
					}
					
				} else {
					/* No park time from previous flight because first */
					parkTime = 0.0;
					
				}
				
				/*Distance Between Airports*/
				String flightOrigin = nextFlight.getOrigin();
				String flightDestination = nextFlight.getDestination();
				int cityPairSize = DATA.getCity_pairs().size();
				for(int k = 0; k < cityPairSize; k++){
					if(DATA.getCity_pairs().get(k).validateOriDest(flightOrigin, flightDestination)){
						distanceKM = DATA.getCity_pairs().get(k).getDistance_in_km();
						distanceMiles = DATA.getCity_pairs().get(k).getDistance_in_nautical_miles();
						break;
					}
				}
				/*Distance Between Airports*/

				/*Time to get there*/
				int flightTime = nextFlight.getSchedule_time_of_arrival().difWithMinutes(nextFlight.getSchedule_time_of_departure());
				/*Time to get there*/
				
				value += computeFlightCost(fleet_model, flightOrigin, flightDestination, flightTime, parkTime, airport_handling_cost, fuel_per_minute, maintenance_perminute, atc_per_mile, distanceMiles);
				
				previousFlight = nextFlight;
			}
			/* Compute each flight cost */
		}
		
		fitness = (int) value;
	}
	
	public double computeFlightCost(String modelFleet, String flightOrigin, String flightDestination, int flightTime, double parkTime,
			int airport_handling_cost, double fuel_per_minute, double maintenance_perminute, double atc_per_mile, double distanceMiles) {
		double value = 0.0;

		/*Get charges from Airports (TkOff, Land and Park)*/
		int airportChargeSize = DATA.getAirport_charges().size();
		for(int k = 0, kk = 0; k < airportChargeSize; k++){
			if(DATA.getAirport_charges().get(k).getFleet().equals(modelFleet)
					&& DATA.getAirport_charges().get(k).getIata_code().equals(flightOrigin)
					&& DATA.getAirport_charges().get(k).getCharge_type().equals("LND")){
				value += DATA.getAirport_charges().get(k).getCharge();
				kk++;
			}
			else if(DATA.getAirport_charges().get(k).getFleet().equals(modelFleet)
					&& DATA.getAirport_charges().get(k).getIata_code().equals(flightDestination)
					&& DATA.getAirport_charges().get(k).getCharge_type().equals("LND")){
				value += DATA.getAirport_charges().get(k).getCharge();
				kk++;
			}
			else if(DATA.getAirport_charges().get(k).getFleet().equals(modelFleet)
					&& DATA.getAirport_charges().get(k).getIata_code().equals(flightDestination)
					&& DATA.getAirport_charges().get(k).getCharge_type().equals("PRK")){
				value += DATA.getAirport_charges().get(k).getCharge();
				kk++;
			}
			if(kk==3)
				break;
		}
		/*Get charges from Airports (TkOff, Land and Park)*/
		
		/* Flight costs */
		/*Handling*/
		value += airport_handling_cost * 2;
		/*Handling*/
		
		/*Fuel Cost Using Schedule Time Difference*/
		value += fuel_per_minute * flightTime;
		/*Fuel Cost Using Schedule Time Difference*/
		
		/*Maintenance Cost Using Schedule Time Difference*/
		value += maintenance_perminute * parkTime;
		/*Maintenance Cost Using Schedule Time Difference*/
		
		/*ATC Cost Using Distance Between Airports*/
		value += atc_per_mile * distanceMiles;
		/*ATC Cost Using Distance Between Airports*/
				
		return value;
	}

	public int compareTo(PossibleSolution fs) {
		// positive means fs worse
		if (this.fitness != fs.getFitness()) {
			System.out.println("different values!");
		}
		return (int)(this.fitness - fs.getFitness());
	}
	
	public int getFitness() {
		return this.fitness;
	}
	
	public ArrayList<Integer> getSolution() {
		return this.solution;
	}
	
	public void setSolution(ArrayList<Integer> s, ArrayList<ArrayList<Flight>> possibleFlightPaths) {
		this.solution = s;
		this.newFlightPaths = possibleFlightPaths;
	}
	
	public ArrayList<String> print() {
		ArrayList<String> list = new ArrayList<String>();
		
		for (int i=0; i<DATA.getAircrafts().size(); i++) {
			ArrayList<Flight> flights = this.map.get(DATA.getAircrafts().get(i));
			for (int j=0; j<flights.size(); j++) {
				Pair p = new Pair();
				p.setFlight(flights.get(j));
				p.setAircraft(DATA.getAircrafts().get(i), i);
				String s = "";
				s += "\n" + p.toString();
				list.add(s);
			}
		}

		return list;
	}

	public HashMap<Aircraft, ArrayList<Flight>> getMap() {
		return this.map;
	}

	public void setMap(HashMap<Aircraft, ArrayList<Flight>> map2) {
		this.map = map2;
	}
	
	public PossibleSolution manualCopy(ArrayList<Aircraft> aircrafts) {
		PossibleSolution newSolution = new PossibleSolution(this.DIMENSION, aircrafts);
		
		for (Aircraft a : aircrafts) {
			for (Flight f : this.map.get(a)) {
				newSolution.getMap().get(a).add(f);
			}
		}
		
		return newSolution;
	}
	
	public Integer[] getTabuListEntrie() {
		return this.tabuListEntrie;
	}
	
	public void setTabuListEntrie(Integer[] tabuListEntrie) {
		this.tabuListEntrie = tabuListEntrie;
	}
	
	public ArrayList<PossibleSolution> generateNeighborhood(ArrayList<Aircraft> aircrafts, PossibleSolution prototypeSolution) {
		ArrayList<PossibleSolution> neighborhood = new ArrayList<PossibleSolution>();

		for (int positionIndex1=0; positionIndex1<solution.size()-1; positionIndex1++) {
			for (int positionIndex2=positionIndex1+1; positionIndex2<solution.size(); positionIndex2++) {
				/* solution replacer*/
				ArrayList<Integer> newSolution = new ArrayList<Integer>(solution);
				int tempAircraftIndex1 = solution.get(positionIndex1);
				int tempAircraftIndex2 = solution.get(positionIndex2);
				newSolution.set(positionIndex1, tempAircraftIndex2);
				newSolution.set(positionIndex2, tempAircraftIndex1);
				
				/* flight paths replacer */
				ArrayList<ArrayList<Flight>> possibleFlightPaths = new ArrayList<>();
				for (ArrayList<Flight> flightPath : this.newFlightPaths) {
					ArrayList<Flight> possibleFlightPath = new ArrayList<Flight>(flightPath);
					possibleFlightPaths.add(possibleFlightPath);
				}
				possibleFlightPaths.set(positionIndex1, this.newFlightPaths.get(positionIndex2));
				possibleFlightPaths.set(positionIndex2, this.newFlightPaths.get(positionIndex1));
				
				/* makes tabu list entrie */
				Integer[] newTabuListEntrie = new Integer[]{positionIndex1, positionIndex2};
				
				/* makes possible solution */
				PossibleSolution candidate = prototypeSolution.manualCopy(aircrafts);
				candidate.setSolution(newSolution, possibleFlightPaths);
				candidate.setTabuListEntrie(newTabuListEntrie);
				
				/* build */
				candidate.buildSolution(aircrafts);
				
				/* compute fitness */
				candidate.computeFitness(aircrafts);
				
				neighborhood.add(candidate);
			}
		}
		
		return neighborhood;
	}
}
