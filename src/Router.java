import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class Router {
	private static ArrayList[] routes;
	private static double totalRouteCost;

	public static void main(String[] args) throws FileNotFoundException {
		
//		/**
//		 * MYOPIC 1A
//		 */
//		HeuristicMyopic1aAlgorithm myopic1a = new HeuristicMyopic1aAlgorithm();
//		myopic1a.getData(new FileReader("data2.txt"));
//		myopic1a.execute();
//		myopic1a.showResults();
//
//		/**
//		 * MYOPIC 1B
//		 */
//		HeuristicMyopic1bAlgorithm myopic1b = new HeuristicMyopic1bAlgorithm();
//		myopic1b.getData(new FileReader("data2.txt"));
//		myopic1b.execute();
//		myopic1b.showResults();
//
//
//		/**
//		 * MYOPIC 1C
//		 */
//		HeuristicMyopic1cAlgorithm myopic1c = new HeuristicMyopic1cAlgorithm();
//		myopic1c.getData(new CopyOfFileReader("data2.txt"));
//		myopic1c.execute();
//		myopic1c.showResults();
		
		/**
		 * COPY MYOPIC 1C
		 */
		CopyOfHeuristicMyopic1cAlgorithm cmyopic1c = new CopyOfHeuristicMyopic1cAlgorithm();
		cmyopic1c.getData(new CopyOfFileReader("data.txt"));
		cmyopic1c.execute();
		cmyopic1c.showResults();

		
//		/**
//		 * MYOPIC 2
//		 */
//		HeuristicMyopic2Algorithm myopic2 = new HeuristicMyopic2Algorithm();
//		myopic2.getData(new FileReader("data2.txt"));
//		myopic2.execute();
//		myopic2.showResults();
//		
		
//		/**
//		 * COPY MYOPIC 2
//		 */
//		CopyOfHeuristicMyopic2Algorithm cmyopic2 = new CopyOfHeuristicMyopic2Algorithm();
//		cmyopic2.getData(new CopyOfFileReader("data.txt"));
//		cmyopic2.execute();
//		cmyopic2.showResults();

		
		
//		// IMPROVEMENTS
//				
//		
//		for(int i = 0; i<routes.length; i++){
//			System.out.println(routes[i].toString());
//		}
	
		
//		/**
//		 * 2-OPT
//		 */
//		TwoOptAlgorithm twoOPT = new TwoOptAlgorithm(routes);
//		twoOPT.execute();
//		twoOPT.showResults();

		
		
//		/**
//		 * SWAP
//		 */
//		SwapAlgorithm swap = new SwapAlgorithm(routes);
//		swap.execute();
//		swap.showResults();


	}

}
