package testPackage;

import java.util.ArrayList;
import java.util.Random;

import tabuSearch.TabuSearch;
import utils.Writer;

public class MainClass {

	public static void main(String[] args) {
		int max_cycle_number = 100;
		int tabu_list_size = 10;
		int flight_to_delay = 5820;
		
		run_experiment(max_cycle_number, tabu_list_size, flight_to_delay);
	}

	private static void run_experiment(int max_cycle_number, int tabu_list_size, int flight_to_delay) {
		TabuSearch.execute(max_cycle_number, tabu_list_size, flight_to_delay);
	}
}
