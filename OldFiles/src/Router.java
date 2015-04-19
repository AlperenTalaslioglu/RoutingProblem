import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class Router {
	public static double[][] linkDist;
	public static int[][] seq; // holds the order of nodes in each route (vehicle k visits node i in order x)
	public static double[] routetime; // total travel time of route k
	public static int[] routelength; // number of nodes in route k
	public static int[][] routed; // holds the nodes in each route (binary variable; 1 if route k includes node i; 0, otherwise)

	public static void main(String[] args) throws FileNotFoundException {

		/**
		 * MYOPIC 1A
		 */
		HeuristicMyopic1aAlgorithm myopic1a = new HeuristicMyopic1aAlgorithm();
		myopic1a.getData(new FileReader("data2.txt"));
		myopic1a.execute();
		myopic1a.showResults();
		myopic1a.drawGUI();
		createBuffer(myopic1a.getRoutes(), myopic1a.getDistances(), myopic1a.numberOfNodes, myopic1a.numberOfVehicles);

		
//		 /**
//		 * MYOPIC 1B
//		 */
//		 HeuristicMyopic1bAlgorithm myopic1b = new
//		 HeuristicMyopic1bAlgorithm();
//		 myopic1b.getData(new FileReader("data2.txt"));
//		 myopic1b.execute();
//		 myopic1b.showResults();
//		 myopic1b.drawGUI();
//		 createBuffer(myopic1b.getRoutes(), myopic1b.getDistances(), myopic1b.numberOfNodes, myopic1b.numberOfVehicles);


		
		
//		 /**
//		 * MYOPIC 1C
//		 */
//		 HeuristicMyopic1cAlgorithm myopic1c = new
//		 HeuristicMyopic1cAlgorithm();
//		 myopic1c.getData(new FileReader("data2.txt"));
//		 myopic1c.execute();
//		 myopic1c.showResults();
//		 myopic1c.drawGUI();
//		 createBuffer(myopic1c.getRoutes(), myopic1c.getDistances(), myopic1c.numberOfNodes, myopic1c.numberOfVehicles);


//		 /**
//		 * COPY MYOPIC 1C
//		 */
//		 CopyOfHeuristicMyopic1cAlgorithm cmyopic1c = new
//		 CopyOfHeuristicMyopic1cAlgorithm();
//		 cmyopic1c.getData(new CopyOfFileReader("data.txt"));
//		 cmyopic1c.execute();
//		 cmyopic1c.showResults();

//		 /**
//		 * MYOPIC 2
//		 */
//		 HeuristicMyopic2Algorithm myopic2 = new HeuristicMyopic2Algorithm();
//		 myopic2.getData(new FileReader("data2.txt"));
//		 myopic2.execute();
//		 myopic2.showResults();
//		 myopic2.drawGUI();
//		 createBuffer(myopic2.getRoutes(), myopic2.getDistances(), myopic2.numberOfNodes, myopic2.numberOfVehicles);
		

		// /**
		// * COPY MYOPIC 2
		// */
		// CopyOfHeuristicMyopic2Algorithm cmyopic2 = new
		// CopyOfHeuristicMyopic2Algorithm();
		// cmyopic2.getData(new CopyOfFileReader("data.txt"));
		// cmyopic2.execute();
		// cmyopic2.showResults();

		// // IMPROVEMENTS

//		 /**
//		 * 2-OPT
//		 */
//		 TwoOptAlgorithm twoOPT = new TwoOptAlgorithm(myopic1a.getRoutes());
//		 twoOPT.execute();
//		 twoOPT.showResults();

		// /**
		// * SWAP
		// */
		// SwapAlgorithm swap = new SwapAlgorithm(routes);
		// swap.execute();
		// swap.showResults();

	}

	private static void createBuffer(ArrayList[] routes,double[][] distances, int N, int K) {
		int speed = 1;
		linkDist = new double[N + 1][N + 1];
		seq = new int[K][N + 1]; // holds the order of nodes in each route (vehicle k visits node i in order x)
		routetime = new double[K]; // total travel time of route k
		routelength = new int[K]; // number of nodes in route k
		routed = new int[K][N]; // holds the nodes in each route (binary variable; 1 if route k includes node i; 0, otherwise)
		// Distances between nodes
		linkDist = distances;

		// Routes
		for (int i = 0; i < routes.length; i++) {
			routelength[i] = routes[i].size();
			System.out.println("routelength[" + i + "] = " + routes[i].size());
		}

		for (int i = 0; i < routes.length; i++) {
			seq[i][0] = 0;
			for (int j = 0; j < routes[i].size(); j++) {
				seq[i][(j + 1)] = (int) routes[i].get(j);
				routed[i][((int) routes[i].get(j)) - 1] = 1;
			}
		}

		for (int k = 0; k < K; k++) {
			int origin = 0;
			int next = 0;
			for (int indexi = 1; indexi <= routelength[k]; indexi++) {
				for (int i = 1; i <= N; i++) {
					if (seq[k][indexi] == i) {
						next = i;
						routetime[k] = routetime[k] + linkDist[origin][next];
					}
				}
				origin = next;
			}
			routetime[k] = routetime[k] + linkDist[origin][0] / speed;
		}
	}

}
