import gui.MainFrame;

import java.util.ArrayList;
import java.util.List;


public class SwapAlgorithm extends Algorithm{
	public double totalRouteCosts;
	private ArrayList[] routes;
	int N;	// numberOfNodes
	int K; 	// numberOfVehicles;	
	int tMax;
	
	double speed = 1;
	
	double [][] linkDist;
	
	int [][] seq;    //holds the order of nodes in each route (vehicle k visits node i in order x) 
	double [] routetime; // total travel time of route k
	int [] routelength;  // number of nodes in route k		
	int [][] routed;   //holds the nodes in each route (binary variable; 1 if route k includes node i; 0, otherwise)
	
	
	
	int [][] new_seq;   //updated seq
	double [] new_routetime; // updated new_routetime	
	int [] new_routelength;  // new routelength (actually, it is not affected by the 1-swap move; but defined to be general)	
	int [][] new_routed;  // new routed 
	

	public SwapAlgorithm(ArrayList[] routes) {
		this.routes = routes;
	}

	@Override
	public void execute() {
		init();
		executeSWAP();	
	}

	private void executeSWAP() {
		System.out.println("<< Apply 1-swap >>");
		System.out.println();
		
		double [] temp_routetime= new double [K];  
		
		//initialize
		double best_diff=1000; //set it to a large number
		
		int best_k1=1000; //a large number 
		int best_k2=1000;    // a large number
		int best_i=0;		
		int best_j=0;		
		int best_indexi=0;		
		int best_indexj=0;
		
		//we want to consider swapping each pair of nodes across the routes
		for (int k1=0; k1<K-1; k1++){     //first route: considers each route one by one
			System.out.println(" k1: " + k1);	
			for (int indexi=1; indexi<=routelength[k1]; indexi++){    //consider each node in the first route
				int i=seq[k1][indexi];
				System.out.println("      i: " + i);
				int i0=seq[k1][indexi-1];
				int i1=seq[k1][indexi+1];
			
				for (int k2=k1+1; k2<K; k2++){    //second route: considers each route one by one to make a pair with the first route
					if (k1!=k2){
						System.out.println("         k2: " + k2);	
						for (int indexj=1; indexj<=routelength[k2]; indexj++){   //consider each node in the second route
							int j=seq[k2][indexj];
							System.out.print("                 j: " + j);
							int j0=seq[k2][indexj-1];
							int j1=seq[k2][indexj+1];
						
							
							temp_routetime[k1]=routetime[k1] - linkDist[i0][i] - linkDist[i][i1] + linkDist[i0][j] + linkDist[j][i1];
							temp_routetime[k2]=routetime[k2] - linkDist[j0][j] - linkDist[j][j1] + linkDist[j0][i] + linkDist[i][j1];
							
							double diff = temp_routetime[k1] + temp_routetime[k2] - routetime[k1] - routetime[k2];
							
							System.out.print ("   temp_routetime[k1] = " + (double)Math.round(temp_routetime[k1]*100)/100 ) ;
							System.out.print ("  temp_routetime[k2] = " + (double)Math.round(temp_routetime[k2]*100)/100 + "     diff = " + (double)Math.round(diff*100)/100) ;
							
											
							if ((temp_routetime[k1] <tMax) && (temp_routetime[k2] <tMax)){
							if (diff <= best_diff) {
							
								best_diff=diff;
								
								best_k1=k1;
								best_k2=k2;
								
								best_i=i;								
								best_j=j;
								
								best_indexi=indexi;
								best_indexj=indexj;

										
								System.out.println("                      >>>  Found a better feasible swap; nodes: " + best_i + " and " + best_j);
								
								
							} else System.out.println ( "    Feasible, but not better or not improving");
							
							} else System.out.println (  "   Infeasible route(s)");
							
						}

					}
				}
			
			}
		}
		
		System.out.println();
		System.out.println(">>> Best swap: " + best_i + " and " + best_j + "    indices of the swapped nodes: " + best_indexi + " (route " + best_k1 + ") & " + best_indexj + "(route " + best_k2 + ") " );
		
		
		System.out.println();
		
		// form the new routes (update the solution) if there is a improving feasible 2-opt move
		
		if (best_k1<1000){  //if an improving swap is found
			System.out.println( "Update routes:");
			System.out.println();
			// create new route best_k1 (with node best_j):
			for (int i=0; i<best_indexi; i++){
				new_seq[best_k1][i]= seq[best_k1][i];				
			}
			new_seq[best_k1][best_indexi]= best_j;
			
			
			for (int i=best_indexi+1; i<=routelength[best_k1]; i++){
				new_seq[best_k1][i]= seq[best_k1][i];				
			}			

			
			// create new route best_k2 (with node best_i):
			for (int j=0; j<best_indexj; j++){
				new_seq[best_k2][j]= seq[best_k2][j];	
				
			}
			new_seq[best_k2][best_indexj]= best_i;
			
			
			
			for (int j=best_indexj+1; j<=routelength[best_k2]; j++){
				new_seq[best_k2][j]= seq[best_k2][j];
				
			}
			
			//copy the unaffected routes as well
			for (int k=0; k<K; k++){
				if ((k!=best_k1) && (k!=best_k2)){
					for (int j=0; j<=routelength[k]; j++){
						new_seq[k][j]= seq[k][j];						
					}
				}
			}
			

			
			////update route time and check
			for (int k=0; k<K; k++){			
	        	new_routetime[k]=0;
	        	this.routes[k].clear();
	        	int origin = 0;
	        	int next = 0;
	        	for (int indexi=0; indexi<=routelength[k]; indexi++){        		
	        		for (int i=1; i<=N; i++){
	        			if (new_seq[k][indexi]==i){
	        				next=i;  
	        				this.routes[k].add(next);
	        				System.out.print(next + " ");
	        				new_routetime[k]=new_routetime[k]+linkDist[origin][next]/speed;
	        			}
	        		}        		
	        		origin=next;
	        	}
	        	new_routetime[k]=new_routetime[k]+ linkDist[origin][0]/speed;
	        	System.out.println("   new_routetime = " + (double)Math.round(new_routetime[k]*100)/100); 
			}
			
			System.out.println();
			//check routes again
			for (int k=0; k<K; k++){
				for (int j=1; j<=routelength[k]; j++){				
					System.out.print(new_seq[k][j] + " ");
				}
				System.out.println();		
			}
			
			
			System.out.println();
			//updated the new_routed[][]
			for (int k=0; k<K; k++){
				for (int j=1; j<=routelength[k]; j++){
					for (int i=0; i<N; i++){
					   if (new_seq[k][j]==i) {
						   new_routed[k][i]=1;
						   System.out.println("k=" + k + " i=" + i +  " routed? " + new_routed[k][i] + " ");
					   }
					}
				}
				
			}
		}
		
		for (int i = 0; i < this.routes.length; i++) {
			totalRouteCosts += calculateRouteCost(this.routes[i]);			
		}	

									
	}

	private void init() {
		this.K = numberOfVehicles;
		this.N = (numberOfNodes-1);	
		this.tMax = super.tMax;
		
		speed = 1;
		
		linkDist = new double [N+1][N+1];
		
		seq = new int [K][N+1];    //holds the order of nodes in each route (vehicle k visits node i in order x) 
		routetime=  new double [K]; // total travel time of route k
		routelength= new int [K];  // number of nodes in route k		
		routed = new int [K][N];   //holds the nodes in each route (binary variable; 1 if route k includes node i; 0, otherwise)
		
		
		
		new_seq = new int [K][N+1];   //updated seq
		new_routetime=  new double [K]; // updated new_routetime	
		new_routelength= new int [K];    // new routelength (actually, it is not affected by the 1-swap move; but defined to be general)	
		new_routed = new int [K][N];   // new routed 
		
		
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
	public void showResults() {
		System.out.println("Results of SWAP");
		System.out.println();		
		System.out.println("K : " + numberOfVehicles);
		System.out.println("Tmax : " + tMax);
		double sum = 0;
		int visitedNodes = 0;
		double[] routeLengths = new double[routes.length];
		double[] routeCosts = new double[routes.length];		
		for (int i = 0; i < routes.length; i++) {
			sum += calculateRouteCost(routes[i]);
			visitedNodes += routes[i].size();
			routeLengths[i] = routes[i].size();
			routeCosts[i] = calculateRouteCost(routes[i]);
			System.out.println("Route " + (i + 1) + " :  " + routes[i].toString());
		}
		System.out.println("Avg : " + findAverage(routeLengths));
		System.out.println("Max : " +  findMax(routeLengths));
		System.out.println("Min : " +  findMin(routeLengths));
		System.out.println("Total number of nodes visited : " + (int)visitedNodes);
		System.out.println("Number of routes : " + routes.length);
		for(int i = 0; i<routeCosts.length; i++){
			System.out.println("r"+(i+1) + " : " + routeCosts[i]);
		}
		System.out.println("Avg : " + findAverage(routeCosts));
		System.out.println("Max : " +  findMax(routeCosts));
		System.out.println("Min : " +  findMin(routeCosts));
		System.out.println("Total  : " + sum);		
		System.out.println("Attributes");
		System.out.println(getSumOfAttributes());		
	}
	
	private String getSumOfAttributes() {
		int[] sumOfAttributes = new int[numberOfAttributes];
		for(int i = 0; i<routes.length; i++){
			for(int j = 0; j<routes[i].size(); j++){				
				for(int k = 0; k<numberOfAttributes; k++){					
					if(attributes[(int) routes[i].get(j)][k] == 1){sumOfAttributes[k]++;}
				}
			}
		}
		
		String sum = "";
		for(int i = 0; i<sumOfAttributes.length; i++){sum += " " + sumOfAttributes[i];}
		
		return sum;
	}

	private double findMin(double[] routeLengths) {
		double min = routeLengths[0];
		for (int i = 1; i < routeLengths.length; i++) {
		      if (routeLengths[i] < min) {min = routeLengths[i];}
		}
		return min;
	}

	private double findMax(double[] routeLengths) {
		double max = routeLengths[0];
		for (int i = 1; i < routeLengths.length; i++){
		      if (routeLengths[i] > max){max = routeLengths[i];}
		}
		return max;
	}

	private double findAverage(double[] routeLengths) {
		int N = routeLengths.length;
		double sum = 0; 
		for (int i = 0; i < N; i++){sum += routeLengths[i];}
		return sum / N;
	}
	
	public void drawGUI(int i) {
		new MainFrame(routes,coordinates,i);		
	}

	public ArrayList[] getRoutes() {
		return routes;
	}

	public double[][] getDistances() {
		return distances;
	}

}
