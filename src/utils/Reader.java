package utils;
import info.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Reader {
	public static void readAll() {
		try {
			readAM();
			readA();
			readAC();
			readCP();
			readE();
			readF();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void readAM() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("aircraft_models"));
		ArrayList<String> lines = new ArrayList<String>();
		try {
			String line = br.readLine();
			while (line != null) {
				// aircraft_model
				line = line.substring(line.indexOf('(')+1, line.indexOf(')'));
				lines.add(line.substring(line.indexOf('\'')+1, line.indexOf(",")-1));
				// description
				line = line.substring(line.indexOf(",")+1);
				lines.add(line.substring(line.indexOf('\'')+1, line.indexOf(",")-1));
				// fleet
				line = line.substring(line.indexOf(",")+1);
				lines.add(line.substring(line.indexOf('\'')+1, line.indexOf(",")-1));
				// capacity
				line = line.substring(line.indexOf(",")+1);
				lines.add(line.substring(0, line.indexOf(",")));
				// mtow
				line = line.substring(line.indexOf(",")+1);
				lines.add(line.substring(0, line.indexOf(",")));
				// atc_avg_cost_nautical_mile
				line = line.substring(line.indexOf(",")+2);
				lines.add(line.substring(0, line.indexOf('\'')));
				// maintenance_avg_cost_minute
				line = line.substring(line.indexOf(",")+2);
				lines.add(line.substring(0, line.indexOf('\'')));
				// fuel_avg_cost_minute
				line = line.substring(line.indexOf(",")+2);
				lines.add(line.substring(0, line.indexOf('\'')));
				// airport_handling_cost
				line = line.substring(line.indexOf(",")+1);
				lines.add(line.substring(0, line.indexOf(",")));
				// cpt
				line = line.substring(line.indexOf(",")+1);
				lines.add(line.substring(0, line.indexOf(",")));
				// opt
				line = line.substring(line.indexOf(",")+1);
				lines.add(line.substring(0, line.indexOf(",")));
				// scb
				line = line.substring(line.indexOf(",")+1);
				lines.add(line.substring(0, line.indexOf(",")));
				// ccb
				line = line.substring(line.indexOf(",")+1);
				lines.add(line.substring(0, line.indexOf(",")));
				// cab
				line = line.substring(line.indexOf(",")+1);
				lines.add(line);
				
				line = br.readLine();
			}
		} finally {
			br.close();
		}
		while(!lines.isEmpty()) {
			DATA.addAircraftModel(new AircraftModel(lines.get(0), lines.get(1), lines.get(2), Integer.valueOf(lines.get(3)), Integer.valueOf(lines.get(4)), Double.valueOf(lines.get(5)), Double.valueOf(lines.get(6)), Double.valueOf(lines.get(7)), Integer.valueOf(lines.get(8)), Integer.valueOf(lines.get(9)), Integer.valueOf(lines.get(10)),Integer.valueOf(lines.get(11)), Integer.valueOf(lines.get(12)), Integer.valueOf(lines.get(13))));
			lines.remove(0);lines.remove(0);lines.remove(0);lines.remove(0);lines.remove(0);
			lines.remove(0);lines.remove(0);lines.remove(0);lines.remove(0);lines.remove(0);
			lines.remove(0);lines.remove(0);lines.remove(0);lines.remove(0);
		}
	}
	
	public static void readA() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("aircrafts"));
		ArrayList<String> lines = new ArrayList<String>();
		try {
			String line = br.readLine();
			while (line != null) {
				// tail_number
				line = line.substring(line.indexOf('(')+1, line.indexOf(')'));
				lines.add(line.substring(line.indexOf('\'')+1, line.indexOf(",")-1));
				// aircraft_model_code
				line = line.substring(line.indexOf(",")+1);
				lines.add(line.substring(line.indexOf('\'')+1, line.indexOf(",")-1));
				// aircraft_model
				line = line.substring(line.indexOf(",")+2);
				lines.add(line.substring(0, line.length()-1));
				
				line = br.readLine();
			}
		} finally {
			br.close();
		}
		while(!lines.isEmpty()) {
			DATA.addAircraft(new Aircraft(lines.get(0), lines.get(1), lines.get(2)));
			lines.remove(0);lines.remove(0);lines.remove(0);
		}
	}
	
	public static void readAC() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("airport_charges"));
		ArrayList<String> lines = new ArrayList<String>();
		try {
			String line = br.readLine();
			while (line != null) {
				// iata_code
				line = line.substring(line.indexOf('(')+1, line.indexOf(')'));
				lines.add(line.substring(line.indexOf('\'')+1, line.indexOf(",")-1));
				// fleet
				line = line.substring(line.indexOf(",")+1, line.length());
				lines.add(line.substring(line.indexOf('\'')+1, line.indexOf(",")-1));
				// charge_type
				line = line.substring(line.indexOf(",")+1, line.length());
				lines.add(line.substring(line.indexOf('\'')+1, line.indexOf(",")-1));
				// charge
				line = line.substring(line.indexOf(",")+1, line.length());
				lines.add(line.substring(1, line.length()-1));
				
				line = br.readLine();
			}
		} finally {
			br.close();
		}
		while(!lines.isEmpty()) {
			DATA.addAirportCharge(new AirportCharge(lines.get(0), lines.get(1), lines.get(2), Double.valueOf(lines.get(3))));
			lines.remove(0);lines.remove(0);lines.remove(0);lines.remove(0);
		}
	}
	
	public static void readCP() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("city_pairs"));
		ArrayList<String> lines = new ArrayList<String>();
		try {
			String line = br.readLine();
			while (line != null) {
				// origin
				if(line.indexOf("),") < line.indexOf(");"))
					line = line.substring(line.indexOf('(')+1, line.indexOf(");"));
				else
					line = line.substring(line.indexOf('(')+1, line.indexOf("),"));
				lines.add(line.substring(line.indexOf('\'')+1, line.indexOf(",")-1));
				// origin_description
				line = line.substring(line.indexOf(",")+1, line.length());
				lines.add(line.substring(line.indexOf('\'')+1, line.indexOf(",")-1));
				// origin_latitude
				line = line.substring(line.indexOf(",")+1, line.length());
				lines.add(line.substring(line.indexOf('\'')+1, line.indexOf(",")-1));
				// origin_longitude
				line = line.substring(line.indexOf(",")+1, line.length());
				lines.add(line.substring(line.indexOf('\'')+1, line.indexOf(",")-1));
				// destination
				line = line.substring(line.indexOf(",")+1, line.length());
				lines.add(line.substring(line.indexOf('\'')+1, line.indexOf(",")-1));
				// destination_description
				line = line.substring(line.indexOf(",")+1, line.length());
				lines.add(line.substring(line.indexOf('\'')+1, line.indexOf(",")-1));
				// destination_latitude
				line = line.substring(line.indexOf(",")+1, line.length());
				lines.add(line.substring(line.indexOf('\'')+1, line.indexOf(",")-1));
				// destination_longitude
				line = line.substring(line.indexOf(",")+1, line.length());
				lines.add(line.substring(line.indexOf('\'')+1, line.indexOf(",")-1));
				// distance_in_nautical_miles
				line = line.substring(line.indexOf(",")+1, line.length());
				lines.add(line.substring(line.indexOf('\'')+1, line.indexOf(",")-1));
				// distance_in_km
				line = line.substring(line.indexOf(",")+1, line.length());
				lines.add(line.substring(line.indexOf('\'')+1, line.length()-1));
				
				line = br.readLine();
			}
		} finally {
			br.close();
		}
		while(!lines.isEmpty()) {			
			DATA.addCityPair(new CityPair(lines.get(0), lines.get(1), Double.valueOf(lines.get(2)), Double.valueOf(lines.get(3)), lines.get(4), lines.get(5), Double.valueOf(lines.get(6)), Double.valueOf(lines.get(7)), Double.valueOf(lines.get(8)), Double.valueOf(lines.get(9))));
			lines.remove(0);lines.remove(0);lines.remove(0);lines.remove(0);lines.remove(0);
			lines.remove(0);lines.remove(0);lines.remove(0);lines.remove(0);lines.remove(0);
		}
	}
	
	public static void readE() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("events"));
		ArrayList<String> lines = new ArrayList<String>();
		try {
			String line = br.readLine();
			while (line != null) {
				// event_id
				line = line.substring(line.indexOf('(')+1, line.indexOf(')'));
				lines.add(line.substring(0, line.indexOf(",")));
				// event_time
				line = line.substring(line.indexOf(",")+2);
				lines.add(line.substring(0, line.indexOf(",")-1));
				// event_type
				line = line.substring(line.indexOf(",")+2);
				lines.add(line.substring(0, line.indexOf(",")-1));
				// event_cause
				line = line.substring(line.indexOf(",")+2);
				lines.add(line.substring(0, line.indexOf(",")-1));
				// resource_affected
				line = line.substring(line.indexOf(",")+2);
				lines.add(line.substring(0, line.indexOf(",")-1));
				// resource_type
				line = line.substring(line.indexOf(",")+2);
				lines.add(line.substring(0, line.indexOf(",")-1));
				// resource_cap
				line = line.substring(line.indexOf(",")+1);
				lines.add(line.substring(0, line.indexOf(",")));
				// crew_res_type
				line = line.substring(line.indexOf(",")+2);
				lines.add(line.substring(0, line.indexOf(",")-1));
				// estimated_time_to_solve
				line = line.substring(line.indexOf(",")+1);
				lines.add(line.substring(0, line.indexOf(",")));
				// flight_date
				line = line.substring(line.indexOf(",")+2);
				lines.add(line.substring(0, line.indexOf(",")-1));
				// flight_number
				line = line.substring(line.indexOf(",")+2);
				lines.add(line.substring(0, line.indexOf(",")-1));
				// scheduled_time_of_departure
				line = line.substring(line.indexOf(",")+2);
				lines.add(line.substring(0, line.indexOf(",")-1));
				// scheduled_time_of_arrival
				line = line.substring(line.indexOf(",")+2);
				lines.add(line.substring(0, line.indexOf(",")-1));
				// estimated_time_of_departure
				line = line.substring(line.indexOf(",")+2);
				lines.add(line.substring(0, line.indexOf(",")-1));
				// estimated_time_of_arrival
				line = line.substring(line.indexOf(",")+2);
				lines.add(line.substring(0, line.indexOf(",")-1));
				// origin
				line = line.substring(line.indexOf(",")+2);
				lines.add(line.substring(0, line.indexOf(",")-1));
				// destination
				line = line.substring(line.indexOf(",")+2);
				lines.add(line.substring(0, line.indexOf(",")-1));
				// departure_delay_in_minutes
				line = line.substring(line.indexOf(",")+1);
				lines.add(line.substring(0, line.indexOf(",")));
				// bus_pax
				line = line.substring(line.indexOf(",")+1);
				lines.add(line.substring(0, line.indexOf(",")));
				// econ_pax
				line = line.substring(line.indexOf(",")+1);
				lines.add(line.substring(0, line.indexOf(",")));
				// total_pax
				line = line.substring(line.indexOf(",")+1);
				lines.add(line.substring(0, line.indexOf(",")));
				// scheduled_trip_time
				line = line.substring(line.indexOf(",")+2);
				lines.add(line.substring(0, line.indexOf(",")-1));
				// estimated_trip_time
				line = line.substring(line.indexOf(",")+2);
				lines.add(line.substring(0, line.indexOf(",")-1));
				// scheduled_cost_aircraft
				line = line.substring(line.indexOf(",")+2);
				lines.add(line.substring(0, line.indexOf(",")-1));
				// scheduled_cost_crew
				line = line.substring(line.indexOf(",")+2);
				lines.add(line.substring(0, line.indexOf(",")-1));
				// scheduled_cost_passenger
				line = line.substring(line.indexOf(",")+2);
				lines.add(line.substring(0, line.length()-1));
				
				line = br.readLine();
			}
		} finally {
			br.close();
		}
		while(!lines.isEmpty()) {
			DATA.addEvent(new Event(Integer.valueOf(lines.get(0)), new Date(lines.get(1)), lines.get(2), lines.get(3), lines.get(4), lines.get(5), Integer.valueOf(lines.get(6)), lines.get(7), Integer.valueOf(lines.get(8)), new Date(lines.get(9)), lines.get(10), new Date(lines.get(11)), new Date(lines.get(12)), new Date(lines.get(13)), new Date(lines.get(14)), lines.get(15), lines.get(16), Integer.valueOf(lines.get(17)), Integer.valueOf(lines.get(18)), Integer.valueOf(lines.get(19)), Integer.valueOf(lines.get(20)), new Date(lines.get(21)), new Date(lines.get(22)), Double.valueOf(lines.get(23)), Double.valueOf(lines.get(24)), Double.valueOf(lines.get(25))));
			lines.remove(0);lines.remove(0);lines.remove(0);lines.remove(0);lines.remove(0);
			lines.remove(0);lines.remove(0);lines.remove(0);lines.remove(0);lines.remove(0);
			lines.remove(0);lines.remove(0);lines.remove(0);lines.remove(0);lines.remove(0);
			lines.remove(0);lines.remove(0);lines.remove(0);lines.remove(0);lines.remove(0);
			lines.remove(0);lines.remove(0);lines.remove(0);lines.remove(0);lines.remove(0);
			lines.remove(0);
		}
	}

	public static void readF() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("flights"));
		ArrayList<String> lines = new ArrayList<String>();
		try {
			String line = br.readLine();
			while (line != null) {
				// flight_date
				line = line.substring(line.indexOf('(')+2, line.indexOf(')'));
				lines.add(line.substring(0, line.indexOf('\'')));
				// flight_number
				line = line.substring(line.indexOf(",")+2, line.length());
				lines.add(line.substring(0, line.indexOf('\'')));
				// origin
				line = line.substring(line.indexOf(",")+2, line.length());
				lines.add(line.substring(0, line.indexOf('\'')));
				// destination
				line = line.substring(line.indexOf(",")+2, line.length());
				lines.add(line.substring(0, line.indexOf('\'')));
				// flight_carrier
				line = line.substring(line.indexOf(",")+2, line.length());
				lines.add(line.substring(0, line.indexOf('\'')));
				// schedule_time_of_departure
				line = line.substring(line.indexOf(",")+2, line.length());
				lines.add(line.substring(0, line.indexOf('\'')));
				// schedule_time_of_arrival
				line = line.substring(line.indexOf(",")+2, line.length());
				lines.add(line.substring(0, line.indexOf('\'')));
				// bus_seats
				line = line.substring(line.indexOf(",")+1, line.length());
				lines.add(line.substring(0, line.indexOf(',')));
				// econ_seats
				line = line.substring(line.indexOf(",")+1, line.length());
				lines.add(line.substring(0, line.indexOf(',')));
				// total_seats
				line = line.substring(line.indexOf(",")+1, line.length());
				lines.add(line.substring(0, line.indexOf(',')));
				// tail_number
				line = line.substring(line.indexOf(",")+2, line.length());
				lines.add(line.substring(0, line.indexOf('\'')));
				// bus_seats_sold
				line = line.substring(line.indexOf(",")+1, line.length());
				lines.add(line.substring(0, line.indexOf(',')));
				// econ_seats_sold
				line = line.substring(line.indexOf(",")+1, line.length());
				lines.add(line.substring(0, line.indexOf(',')));
				// total_seats_sold
				line = line.substring(line.indexOf(",")+1, line.length());
				lines.add(line);
				
				line = br.readLine();
			}
		} finally {
			br.close();
		}
		while(!lines.isEmpty()) {
			DATA.addFlight(new Flight(new Date(lines.get(0)), lines.get(1), lines.get(2), lines.get(3), lines.get(4), new Date(lines.get(5)), new Date(lines.get(6)), Integer.valueOf(lines.get(7)), Integer.valueOf(lines.get(8)), Integer.valueOf(lines.get(9)), lines.get(10), Integer.valueOf(lines.get(11)), Integer.valueOf(lines.get(12)), Integer.valueOf(lines.get(13))));
			lines.remove(0);lines.remove(0);lines.remove(0);lines.remove(0);lines.remove(0);
			lines.remove(0);lines.remove(0);lines.remove(0);lines.remove(0);lines.remove(0);
			lines.remove(0);lines.remove(0);lines.remove(0);lines.remove(0);
		}
	}
}
