package testPackage;

import java.util.ArrayList;
import java.util.Random;

import tabuSearch.TabuSearch;
import utils.Writer;

public class MainClass {

	public static void main(String[] args) {
		int max_cycle_number = 20;
		int tabu_list_size = 3;
		
		run_experiment(max_cycle_number, tabu_list_size);
	}

	private static void run_experiment(int max_cycle_number, int tabu_list_size) {
		TabuSearch.execute(max_cycle_number, tabu_list_size);
		
		//log(max_cycle_number, tabu_list_size, limit_divider);
	}

	/*
	private static void log(int max_cycle_number, int tabu_list_size, int limit_divider) {
		Writer logWriter = new Writer();
		Random random = new Random();
		String filepath = "TEST-Tabu_Search-"+max_cycle_number+"-"+tabu_list_size+"-"+limit_divider+"("+random.nextInt(20)+").txt";
		
		logWriter.add("Tabu Search");
		logWriter.add("parameters:");
		logWriter.add("MAX_CYCLE_NUMBER: " + max_cycle_number);
		logWriter.add("TABU_LIST_SIZE: " + tabu_list_size);
		logWriter.add("LIMIT_TRIALS: " + max_cycle_number / limit_divider);
		logWriter.add("Run results:");
		logWriter.add("\t- flights: " + TabuSearch.getNumberOfFlights());
		logWriter.add("\t- aircrafts: " + TabuSearch.getNumberOfAircrafts());
		logWriter.add("\t- runtime in ms: " + TabuSearch.getRuntime() + "  ms");
		logWriter.add("\t- first best: " + TabuSearch.getFirstBest());
		logWriter.add("\t- last best: " + TabuSearch.getLastBest());
		logWriter.add("\t- improvement rate: " + TabuSearch.getImprovementRate() + " %");
		logWriter.add("\t- optimum found in cycle no. " + TabuSearch.getIterationGBest());
		
		logWriter.add("\n\nBEST SOLUTION: \n");
		ArrayList<String> bestSolution = TabuSearch.getBestSolution().print();
		for (String s : bestSolution) {
			logWriter.add(s); 
		}
		logWriter.writeFile(filepath);
	}
	*/
}
