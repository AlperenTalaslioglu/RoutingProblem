import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class Router {
	private static ArrayList[] routes;
	private static double totalRouteCost;

	public static void main(String[] args) throws FileNotFoundException {
		
		/**
		 * MYOPIC 1A
		 */
		HeuristicMyopic1aAlgorithm myopic1a = new HeuristicMyopic1aAlgorithm();
		myopic1a.getData(new FileReader("data.txt"));
		myopic1a.execute();
		myopic1a.showResults();
		routes = myopic1a.getRoutes();

		/**
		 * MYOPIC 1B
		 */
		HeuristicMyopic1bAlgorithm myopic1b = new HeuristicMyopic1bAlgorithm();
		myopic1b.getData(new FileReader("data.txt"));
		myopic1b.execute();
		myopic1b.showResults();
		if(myopic1b.totalRouteCosts > totalRouteCost){
			routes = myopic1b.getRoutes();
			totalRouteCost = myopic1b.totalRouteCosts;
		}

		/**
		 * MYOPIC 1C
		 */
		HeuristicMyopic1cAlgorithm myopic1c = new HeuristicMyopic1cAlgorithm();
		myopic1c.getData(new FileReader("data.txt"));
		myopic1c.execute();
		myopic1c.showResults();
		if(myopic1c.totalRouteCosts > totalRouteCost){
			routes = myopic1c.getRoutes();
			totalRouteCost = myopic1c.totalRouteCosts;
		}
		
		/**
		 * MYOPIC 2
		 */
		HeuristicMyopic2Algorithm myopic2 = new HeuristicMyopic2Algorithm();
		myopic2.getData(new FileReader("data.txt"));
		myopic2.execute();
		myopic2.showResults();
		if(myopic2.totalRouteCosts > totalRouteCost){
			routes = myopic2.getRoutes();
			totalRouteCost = myopic2.totalRouteCosts;
		}		
		// IMPROVEMENTS
				
		
		for(int i = 0; i<routes.length; i++){
			System.out.println(routes[i].toString());
		}
		
		
		/**
		 * 2-OPT
		 */
		TwoOptAlgorithm twoOPT = new TwoOptAlgorithm(routes);
		twoOPT.getData(new FileReader("data.txt"));
		twoOPT.execute();
		twoOPT.showResults();
		if(twoOPT.totalRouteCosts < totalRouteCost){
			routes = twoOPT.getRoutes();
			totalRouteCost = twoOPT.totalRouteCosts;
		}
				

		/**
		 * SWAP
		 */
		SwapAlgorithm swap = new SwapAlgorithm(routes);
		swap.getData(new FileReader("data.txt"));
		swap.execute();
		swap.showResults();
		System.out.println(swap.totalRouteCosts < totalRouteCost);
		if(swap.totalRouteCosts < totalRouteCost){
			routes = swap.getRoutes();
			totalRouteCost = swap.totalRouteCosts;
		}

	}

}
