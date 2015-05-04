import gui.MainFrame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class HeuristicMyopic2Algorithm extends Algorithm {
	private int nodeZ;
	private ArrayList[] routes;
	private int K;
	private int k;
	private int[] setOfAttributesAppearance;
	private int[] Ma; 
	private HashSet visitedNodes;
	private ArrayList S; 
	private ArrayList SM;
	private ArrayList considerable3Nodes;
	private int tempZ;
		

	public HeuristicMyopic2Algorithm() {
		this.nodeZ = 0;
		this.k = 1;
		this.tempZ = 0;
	}

	@Override
	public void execute() {
		init();
		int i = 0;
		while(k <= K){
			step1();
			step2();
			step3();
			step4();
		}		
		
	}

	private void step4() {
		if(tempZ == 0){
			step5();
		}else{
			routes[(k - 1)].add(tempZ);//adding item temprarily to route
			if (isRouteFeasible(routes[(k - 1)])) { // Checking the route
				//If yes, insert node z' to the route. Update node z as z=z'. 
				nodeZ = tempZ;
				updateMATable();
				visitedNodes.add(nodeZ);
				
			} else {
				routes[(k - 1)].remove(routes[(k - 1)].size() - 1);//if not feasible, remove item from route
				step5();
			}
		}
	}

	private void updateMATable() {
		for(int i = 0; i<numberOfAttributes; i++){
			Ma[i] = Ma[i] - attributes[nodeZ][i];
			attributes[nodeZ][i] = 0;
		}
	}

	private void step5() {
		//Update k=k+1 (consider a new vehicle). If k>K, STOP.
		nodeZ = 0;
		k++;
	}

	private void step3() {
		selectTheNodeWithHighestSMValue();
	}

	private void selectTheNodeWithHighestSMValue() {
				
		int highest = (int) SM.get(0);
		HashSet items = new HashSet();
		int max = (int)considerable3Nodes.get(0);
		
	    for (int index = 1; index < SM.size(); index++) {
	        if ((int)SM.get(index) > highest) {
	            highest = (int) SM.get(index);
	            max = (int) considerable3Nodes.get(index);
	        }
	    }
	    items.add(max);
	    
	    for(int i = 0; i<3; i++){
	    	if(items.contains((int)considerable3Nodes.get(i))){
	    		continue;
	    	}else if((int)SM.get(i) == highest){
	    		items.add((int)considerable3Nodes.get(i));
	    	}	    	
	    }
	    
	    if(items.size() == 1){
	    	tempZ = (int) (items.toArray())[0];
	    }else{
	    	Object[] selectedNodes = items.toArray();
	    	double min = timeMatrix[nodeZ][(int) selectedNodes[0]];
			int node = (int)selectedNodes[0];
			for (int i = 1; i < selectedNodes.length; i++) {
				if (timeMatrix[nodeZ][(int) selectedNodes[i]] < min) {
					min = timeMatrix[nodeZ][(int)selectedNodes[i]];
					node = (int)selectedNodes[0];
				}
			}
			tempZ = node;
	    }	    
	}

	private void step2() {
		calculateSMValues();
	}

	private void calculateSMValues() {
		SM.clear();
		for(int i = 0; i<3; i++){			
			int sum = 0;
			for(int j = 0; j<numberOfAttributes; j++){
				if (attributes[(int) considerable3Nodes.get(i)][j] == 1){sum+=Ma[j];}
			}		
			SM.add(sum);
		}		
	}

	private void step1() {
		calculateSetOfAttributesAppearance();
		findConsiderable3Nodes();
	}

	private void findConsiderable3Nodes() {
		considerable3Nodes.clear();
		HashSet items = new HashSet();
		for (int j = 0; j < 3; j++) {
			double smallest = Double.MAX_VALUE;
			int node = 0;			         
			
			for(int i = 0; i<timeMatrix[0].length; i++){								
				if(smallest > timeMatrix[nodeZ][i] && timeMatrix[nodeZ][i] != 0 && !items.contains(i) && !visitedNodes.contains(i)) {					
	                smallest = timeMatrix[nodeZ][i];	
	                node = i;
	            }				
			}
            items.add(node);
            considerable3Nodes.add(node);		
		}
	}

	private void init() {
		K = super.numberOfVehicles;
		Ma = new int[super.numberOfAttributes];
		visitedNodes = new HashSet();
		S = new ArrayList();
		SM = new ArrayList();
		routes = new ArrayList[numberOfVehicles];		
		for (int i = 0; i < routes.length; i++) {routes[i] = new ArrayList();}
		considerable3Nodes = new ArrayList();
	}

	private void calculateSetOfAttributesAppearance() {
		for (int i = 0; i < attributes[0].length; i++) {
			int sum = 0;
			for (int j = 0; j < attributes.length; j++) {
				sum += attributes[j][i];
			}
			Ma[i] = sum;
		}
	}

	@Override
	public void showResults() {
		System.out.println("Results of Myopic 2");
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

	/**
	 * 
	 * @param route
	 * @return route cost
	 */
	private double calculateRouteCost(ArrayList route) {
		double cost = 0;
		if (route.size() > 1) {
			double firstEdge = timeMatrix[0][(int) route.get(0)];
			double lastEdge = timeMatrix[(int) route.get(route.size() - 1)][0];
			cost = firstEdge + lastEdge;
			for (int i = 0; i < route.size() - 1; i++) {
				cost += timeMatrix[(int) route.get(i)][(int) route.get(i + 1)];
			}
		}
		return cost;
	}
	
	/*
	 * Checking the route's feasibility by tMax 
	 */
	private boolean isRouteFeasible(ArrayList route) {
		double routeTime = calculateRouteCost(route);
		return (routeTime <= tMax);
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
