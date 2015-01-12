import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class TwoOptAlgorithm extends Algorithm {
	int N; // numberOfNodes
	int K; // numberOfVehicles;
	public static double totalRouteCosts;
	
	double speed = 1;

	double[][] linkDist;

	int[][] seq; // holds the order of nodes in each route
										// (vehicle k visits node i in order x)
	double[] routetime; // total travel time of route k
	int[] routelength; // number of nodes in route k
	int[][] routed; // holds the nodes in each route (binary
									// variable; 1 if route k includes node i;
									// 0, otherwise)

	int[][] new_seq; // updated seq
	double[] new_routetime; // updated new_routetime
	int[] new_routelength; // new routelength (actually, it is not
										// affected by the 2-opt move; but
										// defined to be general)
	int[][] new_routed; // new routed (actually, it is not
										// affected by the 2-opt move; but just
										// defined to be general)
	private ArrayList[] routes;

	public TwoOptAlgorithm(ArrayList[] routes) {
		this.routes = routes;
	}

	@Override
	public void execute() {
		init();
		execute2OPT();	
	}


	private void init() {
		//Initialization
		this.K = numberOfVehicles;
		this.N = (numberOfNodes-1);				
		linkDist = new double[N + 1][N + 1];
		seq = new int[K][N + 1];
		routetime = new double[K]; 
		routelength = new int[K]; 
		routed = new int[K][N];
		new_seq = new int[K][N + 1]; 
		new_routetime = new double[K]; 
		new_routelength = new int[K]; 
		new_routed = new int[K][N]; 
		
		System.out.println(K + " " + N);
		
		//Distances between nodes
		linkDist = distances;
		
		//Routes
		for(int i = 0; i<routes.length; i++){
			routelength[i] = routes[i].size();
			System.out.println("routelength["+i+"] = " + routes[i].size());
		}
		
		
		for(int i = 0; i < routes.length; i++){			
			seq[i][0] = 0;
			for(int j = 0; j<routes[i].size(); j++){
				seq[i][(j+1)] = (int) routes[i].get(j);
				routed[i][((int) routes[i].get(j))-1] = 1;
			}			
		}		

				// print routes & calculate route distances

				for (int k = 0; k < K; k++) {
					int origin = 0;
					int next = 0;
					for (int indexi = 1; indexi <= routelength[k]; indexi++) {
						for (int i = 1; i <= N; i++) {
							if (seq[k][indexi] == i) {
								next = i;
								System.out.print(next + " ");
								routetime[k] = routetime[k] + linkDist[origin][next];
							}
						}
						origin = next;
					}
					routetime[k] = routetime[k] + linkDist[origin][0] / speed;
					System.out.println("   routetime = "+ (double) Math.round(routetime[k] * 100) / 100);
				}

				System.out.println();
		
	}
	
	
	private void execute2OPT() {
		// TODO Auto-generated method stub
		

		System.out.println("Apply 2-opt");
		double[] temp_routetime = new double[K];

		// initialize
		double best_diff = 1000; // set it to a large number
		int best_k = 1000; // just a large number
		int best_i = 0;
		int best_i1 = 0;
		int best_j = 0;
		int best_j1 = 0;

		int best_indexi = 0;
		int best_indexi1 = 0;
		int best_indexj = 0;
		int best_indexj1 = 0;

		for (int k = 0; k < K; k++) { // considers each route one by one;
										// calculates the cost of each move;
										// selects the best one
			if (routelength[k] > 2) { // continue if the route includes at least
										// 3 nodes
				System.out.println("route " + k);

				// break all possible unconsecutive pair of links in the route
				// and reconnect them; break links [i, i1] and [j, j1]; and
				// reconnect them as [i, j] [i1, j1].
				// then calculate the distance of the new route if the move is
				// performed, and select the move which leads to the largest
				// decrease in route distancr
				for (int indexi = 0; indexi < routelength[k]; indexi++) {
					for (int indexj = indexi + 2; indexj <= routelength[k]; indexj++) {
						int i = seq[k][indexi];
						int i1 = seq[k][indexi + 1];
						int j = seq[k][indexj];
						int j1 = seq[k][indexj + 1];

						if (i != j1) { // for avoiding the consecutive links
										// adjacent to the depot
							System.out.print(" break links: [" + i + " - " + i1	+ "] & [" + j + " - " + j1 + "]");
							System.out.print("     make links: [" + i + " - "+ j + "] & [" + i1 + " - " + j1 + "]");

							temp_routetime[k] = routetime[k] - linkDist[i][i1]- linkDist[j][j1] + linkDist[i][j] + linkDist[i1][j1];

							double diff = temp_routetime[k] - routetime[k];

							System.out.print("       temp_routetime = "+ (double) Math.round(temp_routetime[k] * 100)/ 100 + "       diff = "+ (double) Math.round(diff * 100) / 100);

							if ((diff < best_diff)&& (temp_routetime[k] < tMax)) {

								best_k = k;
								best_diff = diff;
								best_i = i;
								best_i1 = i1;
								best_j = j;
								best_j1 = j1;

								best_indexi = indexi;
								best_indexi1 = indexi + 1;
								best_indexj = indexj;
								best_indexj1 = indexj + 1;

								System.out.print("    BETTER ==> break: ["+ best_i + " - " + best_i1 + "] & ["+ best_j + " - " + best_j1 + "]");
								System.out.println("   make: [" + best_i+ " - " + best_j + "] & [" + best_i1+ " - " + best_j1 + "]");

							} else
								System.out.println();
						}
					}
				}
			}
		}

		System.out.println();

		// form the new route and update everything, if there is a improving
		// feasible 2-opt move

		if (best_k < 1000) { // if a new route has been found
			for (int j = 0; j <= best_indexi; j++) {
				new_seq[best_k][j] = seq[best_k][j];
				// System.out.println(new_seq[best_k][j]);
			}

			new_seq[best_k][best_indexi1] = seq[best_k][best_indexj];
			// System.out.println(new_seq[best_k][best_indexi1]);
			new_seq[best_k][best_indexj] = seq[best_k][best_indexi1];
			// System.out.println(new_seq[best_k][best_indexj]);

			int t = 1;
			for (int j = best_indexi1 + 1; j < best_indexj; j++) {
				new_seq[best_k][j] = seq[best_k][best_indexj - t];
				t++;
				// System.out.println(new_seq[best_k][j]);
			}
			for (int j = best_indexj1; j <= routelength[best_k]; j++) {
				new_seq[best_k][j] = seq[best_k][j];
				// System.out.println(new_seq[best_k][j]);
			}

			// copy the unaffected routes as well
			for (int k = 0; k < K; k++) {
				if (k != best_k) {
					for (int j = 0; j <= routelength[k]; j++) {
						new_seq[k][j] = seq[k][j];
					}
				}
			}

			// print all routes
			for (int k = 0; k < K; k++) {
				for (int j = 1; j <= routelength[k]; j++) {
					System.out.print(new_seq[k][j] + " ");
				}
				System.out.println();

				// update route time
				
				routes[k].clear();
				new_routetime[k] = 0;
				int origin = 0;
				int next = 0;
				for (int indexi = 0; indexi <= routelength[k]; indexi++) {
					for (int i = 1; i <= N; i++) {
						if (new_seq[k][indexi] == i) {
							next = i;
							routes[k].add(next);
							System.out.print(next + " ");
							new_routetime[k] = new_routetime[k]	+ linkDist[origin][next] / speed;
						}
					}
					origin = next;
				}
				new_routetime[k] = new_routetime[k] + linkDist[origin][0]/ speed;
				System.out.println("   new_routetime = "+ (double) Math.round(new_routetime[k] * 100) / 100);
			}

		}
		
		for (int i = 0; i < routes.length; i++) {
			totalRouteCosts += calculateRouteCost(routes[i]);			
		}	

	}
	

	/**
	 * 
	 * @param rt
	 * @return route cost
	 */
	private double calculateRouteCost(List rt) {
		double cost = 0;
		if (rt.size() != 0) {
			double firstEdge = calculateDistanceBetween(0, (int) rt.get(0));
			double lastEdge = calculateDistanceBetween(
					(int) rt.get(rt.size() - 1), 0);
			cost = firstEdge + lastEdge;

			if (rt.size() > 1) {
				for (int i = 0; i < rt.size() - 1; i++) {
					cost += calculateDistanceBetween((int) rt.get(i),
							(int) rt.get(i + 1));
				}
			}
		}
		return cost;
	}

	private double calculateDistanceBetween(int to, int from) {
		return Math.sqrt(Math.pow((coordinates[to][0] - coordinates[from][0]),
				2) + +Math.pow((coordinates[to][1] - coordinates[from][1]), 2));
	}
	
	@Override
	public ArrayList[] getRoutes() {
		return this.routes;
	}

	@Override
	public void showResults() {
		System.out.println();
		System.out.println("Results of 2-OPT");
		for (int i = 0; i < routes.length; i++) {
			System.out.println("Route " + (i + 1) + " time : "+ calculateRouteCost(routes[i]));
			System.out.println("Route " + (i + 1) + " :  0 -> "+ routes[i].toString() + " -> 0");
			System.out.println();
		}
	}
}
