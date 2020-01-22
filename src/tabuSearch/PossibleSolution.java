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

	public void computeScore() {
		ArrayList<Aircraft> aircrafts = DATA.getAircrafts();
		double value = 0.0;
		
		//System.out.println("POSSIBLE SOLUTION: <" + this.tabuListEntrie[0] + "," + this.tabuListEntrie[1] + ">");
		
		for (int aircraftIndex=0; aircraftIndex<aircrafts.size(); aircraftIndex++) {
			for (int flightPathIndex=0; flightPathIndex<solution.size(); flightPathIndex++) {
				if (aircraftIndex == solution.get(flightPathIndex)) {
					Aircraft a = aircrafts.get(aircraftIndex);
					
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
							break;
						}
					}
					/* Aircraft info */

					double distanceKM = 0.0,
							distanceMiles = 0.0,
							parkTime = 1.0;


					//System.out.print("Aircraft (" + a + " -- " + aircraftIndex + ") & ");
					
					/* gets the last flight before the delayed Flight arrives destination 
					 * and the first flight of the flightpaths to distribute*/
					Flight nextFlight = newFlightPaths.get(flightPathIndex).get(0);
					if (map.get(a).size() != 0) {
						Flight previousFlight = map.get(a).get(map.get(a).size()-1);

						//System.out.print(" Previous Flight (" + previousFlight + ") ----> ");

						/* if nextFlight departures before previousFlight arrives apply penalty */
						if (previousFlight.getSchedule_time_of_arrival().compareTo(nextFlight.getSchedule_time_of_departure()) > 0) {
							int penaltyMultiplier = 1000;
							value += penaltyMultiplier * newFlightPaths.size() * previousFlight.getSchedule_time_of_arrival().difWithMinutes(nextFlight.getSchedule_time_of_departure());
						}
						
						/* verify if flight is departing the airport it arrived on */
						if (!previousFlight.getDestination().equals(nextFlight.getOrigin())) {
							/* if not: try to make new flight to that airport */
								
							/*Distance Between Airports*/
							String flightOrigin = previousFlight.getDestination();
							String flightDestination = nextFlight.getOrigin();
							distanceKM = getDistance(flightOrigin, flightDestination, 0);
							distanceMiles = getDistance(flightOrigin, flightDestination, 1);
							
							/*Time to get there*/
							int flightTime = nextFlight.getSchedule_time_of_departure().difWithMinutes(previousFlight.getSchedule_time_of_arrival());
							
							/* if not possible to fly there: apply major penalty cost 
							 * (arbitrarily, it takes at least 10 minutes per km for a plane to get somewhere)
							 * */
							if (flightTime <= 0 || distanceKM/flightTime > 10.0) {
								value += 100000.0;
								//System.out.print(" no possible connection flight ----> ");
							}
							/* introduce new flight cost */
							else {
								value += computeFlightCost(fleet_model, flightOrigin, flightDestination, flightTime, 0, airport_handling_cost, fuel_per_minute, maintenance_perminute, atc_per_mile, distanceMiles);
								//System.out.print(" has connection flight ----> ");
							}
							/* No park time because... */
							parkTime = 1.0;
							
						} else {
							/* Computes park time from previous flight */
							/* verify if flights have time between them */
							if (previousFlight.getSchedule_time_of_arrival().compareTo(nextFlight.getSchedule_time_of_departure()) > 0)
								parkTime = 1.0;
							else
								parkTime = nextFlight.getSchedule_time_of_arrival().difWithMinutes(previousFlight.getSchedule_time_of_departure());
						}
						
						/* computes cost of previous flight */
						int flightTime = previousFlight.getSchedule_time_of_arrival().difWithMinutes(previousFlight.getSchedule_time_of_departure());
						
						/*Distance Between Airports*/
						String flightOrigin = previousFlight.getOrigin();
						String flightDestination = previousFlight.getDestination();
						distanceKM = getDistance(flightOrigin, flightDestination, 0);
						distanceMiles = getDistance(flightOrigin, flightDestination, 1);
						
						value += computeFlightCost(fleet_model, flightOrigin, flightDestination, flightTime, parkTime, airport_handling_cost, fuel_per_minute, maintenance_perminute, atc_per_mile, distanceMiles);
					} else {
						//System.out.print(" No Previous Flight () ----> ");
					}
					
					//System.out.println("Next Flight (" + nextFlight + ") -- " + flightPathIndex + ")");
					
					/* computes cost of next flight */
					int flightTime = nextFlight.getSchedule_time_of_arrival().difWithMinutes(nextFlight.getSchedule_time_of_departure());
					
					/*Distance Between Airports*/
					String flightOrigin = nextFlight.getOrigin();
					String flightDestination = nextFlight.getDestination();
					distanceKM = getDistance(flightOrigin, flightDestination, 0);
					distanceMiles = getDistance(flightOrigin, flightDestination, 1);
					
					value += computeFlightCost(fleet_model, flightOrigin, flightDestination, flightTime, parkTime, airport_handling_cost, fuel_per_minute, maintenance_perminute, atc_per_mile, distanceMiles);
				}
			}
		}
		
		//System.out.println();
		
		
		fitness = (int) value;
	}
	
	public double getDistance(String flightOrigin, String flightDestination, int km0_mile1) {
		double distance = 1.0;
		int cityPairSize = DATA.getCity_pairs().size();
		for(int k = 0; k < cityPairSize; k++){
			if(DATA.getCity_pairs().get(k).validateOriDest(flightOrigin, flightDestination)){
				if (km0_mile1 == 0)
					distance = DATA.getCity_pairs().get(k).getDistance_in_km();
				else
					distance = DATA.getCity_pairs().get(k).getDistance_in_nautical_miles();
				break;
			} else if(DATA.getCity_pairs().get(k).validateOriDest(flightDestination, flightOrigin)){
				if (km0_mile1 == 0)
					distance = DATA.getCity_pairs().get(k).getDistance_in_km();
				else
					distance = DATA.getCity_pairs().get(k).getDistance_in_nautical_miles();
				break;
			}
		}
		
		return distance;
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
	
	public ArrayList<ArrayList<Flight>> getNewFlightPaths() {
		return this.newFlightPaths;
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
				
				/* makes tabu list entrie */
				Integer[] newTabuListEntrie = new Integer[]{positionIndex1, positionIndex2};
				
				/* makes possible solution */
				PossibleSolution candidate = prototypeSolution.manualCopy(aircrafts);
				candidate.setSolution(newSolution, newFlightPaths);
				candidate.setTabuListEntrie(newTabuListEntrie);
				
				/* compute fitness */
				candidate.computeScore();
				
				neighborhood.add(candidate);
			}
		}
		
		return neighborhood;
	}
}
