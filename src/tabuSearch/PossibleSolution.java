package tabuSearch;

import info.Aircraft;
import info.DATA;
import info.Flight;

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
	}
	/*
	public void computeObjectiveFunction(){
		double value = 0.0;
		for(int i = 0; i < DIMENSION; i++){
			/*Check if Aircraft is null. Flight canceled!*
			if(pair.get(i).getAircraft() == null){
				value += 100000;
				break;
			}
			
			String model = pair.get(i).getAircraft().getAircraft_model();
			/*Time Difference for Schedule*
			int timeSDif = pair.get(i).getFlight().getSchedule_time_of_arrival().difWithMinutes(pair.get(i).getFlight().getSchedule_time_of_departure());
			/*Time Difference for Schedule*
			
			/*Distance Between Airports*
			String flightOrigin = pair.get(i).getFlight().getOrigin();
			String flightDestination = pair.get(i).getFlight().getDestination();
			int cityPairSize = DATA.getCity_pairs().size();
			double distanceNauticalMiles = 0;
			for(int k = 0; k < cityPairSize; k++){
				if(DATA.getCity_pairs().get(k).validateOriDest(flightOrigin, flightDestination)){
					distanceNauticalMiles = DATA.getCity_pairs().get(k).getDistance_in_nautical_miles();
					break;
				}
			}
			/*Distance Between Airports*
			
			int aircraftModelSize = DATA.getAircraft_models().size();
			for(int j = 0; j < aircraftModelSize; j++){
				if(DATA.getAircraft_models().get(j).getAircraft_model().equals(model)){
					/*Handling*
					value += (DATA.getAircraft_models().get(j).getAirport_handling_cost() * 2);
					/*Handling*/
					
					/*Fuel Cost Using Schedule Time Difference*
					value += (DATA.getAircraft_models().get(j).getFuel_avg_cost_minute() * timeSDif);
					/*Fuel Cost Using Schedule Time Difference*/
					
					/*Maintenance Cost Using Schedule Time Difference*
					value += (DATA.getAircraft_models().get(j).getMaintenance_avg_cost_minute() * timeSDif);
					/*Maintenance Cost Using Schedule Time Difference*/
					
					/*ATC Cost Using Distance Between Airports*
					value += (DATA.getAircraft_models().get(j).getAtc_avg_cost_nautical_mile() * distanceNauticalMiles);
					/*ATC Cost Using Distance Between Airports*/
					
					/*Get charges from Airports (TkOff, Land and Park)*
					String modelFleet = DATA.getAircraft_models().get(j).getFleet();
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
					/*Get charges from Airports (TkOff, Land and Park)*
					break;
				}
			}
		}
		fitness = (int) value;
	}
	*/
	public int compareTo(PossibleSolution fs) {
		return this.fitness - fs.getFitness();
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
}
