import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class HeuristicMyopic2Algorithm extends Algorithm {
	private int nodeZ;
	private ArrayList[] routes;
	private HashSet[] reachableNodesByAttributesTable;
	private int[] maximumCoverageTable;
	private int K;
	private ArrayList considered3Nodes;
	private int[] SMValues;
	private int vehicle;
	private int tempZ;

	public HeuristicMyopic2Algorithm() {
		this.nodeZ = 0;	
		this.considered3Nodes = new ArrayList();
		this.SMValues = new int[3];
		this.vehicle = 1;
	}

	@Override
	public void execute() {
		init();
		while(vehicle <= K){
			step1();
			step2();
			step3();
			step4();
		}		
	}
	
	private void step5() {
		//Update k=k+1 (consider a new vehicle). If k>K, STOP.
		nodeZ = 0;
		vehicle++;
		if(vehicle > K ){return;}
		Arrays.fill(maximumCoverageTable, 0);
		Arrays.fill(SMValues, 0);
		createMaximumCoverageTable();
		generateReachableNodesSetsbyAttributes();
		considered3Nodes.clear();
	}
	
	private void step4() {
		routes[(vehicle - 1)].add(tempZ);//adding item temprarily to route
		if (isRouteFeasible(routes[(vehicle - 1)])) { // Checking the route
			//If yes, insert node z' to the route. Update node z as z=z'. 	
			nodeZ = tempZ;
			removeNodeFromTableAfterInsertion(tempZ);
			removeNodeFromAttributeTableAfterInsertion(nodeZ);
			createMaximumCoverageTable();
			generateReachableNodesSetsbyAttributes();
			considered3Nodes.clear();
			Arrays.fill(SMValues, 0);
		} else {
			routes[(vehicle - 1)].remove(routes[(vehicle - 1)].size() - 1);//if not feasible, remove item from route
			step5();
		}		
	}

	private void step3() {
		tempZ = checkSMValues();
	}

	private void step2() {
		findSMValues();
	}
	
	public void step1(){
		findConsiderable3Nodes();
	}
	
	/*
	 * Checking the route's feasibility by tMax 
	 */
	private boolean isRouteFeasible(ArrayList route) {
		double routeTime;
		if (route.size() < 2) {
			routeTime = getDistanceBetween(nodeZ, (int) route.get(0));
			routeTime += getDistanceBetween(nodeZ,(int) route.get(route.size() - 1));
		} else {
			routeTime = getDistanceBetween(nodeZ, (int) route.get(0));
			routeTime += getDistanceBetween(nodeZ,(int) route.get(route.size() - 1));
			for (int i = 0; i < route.size() - 1; i++) {
				routeTime += getDistanceBetween((int) route.get(i),(int) route.get(i + 1));
			}
		}

		return (routeTime <= tMax);
	}
	
	/*
	 * This method controls the 3 nodes's SM values
	 * Compares them and find the accurate node to select
	 */
	private int checkSMValues() {
		int node = 0;
		Object[] items = considered3Nodes.toArray();
				
		if (SMValues[0] == SMValues[1] && SMValues[1] == SMValues[2]) {
			node = findClosestNodeToNodeZ();
		} else if (SMValues[0] == SMValues[1] && SMValues[0] > SMValues[2]) {
			node = (int) (getDistanceBetween(nodeZ, (int) items[0]) < (getDistanceBetween(
					nodeZ, (int) items[1])) ? items[0] : items[1]);
		} else if (SMValues[1] == SMValues[2]&& SMValues[1] > SMValues[0]) {
			node = (int) (getDistanceBetween(nodeZ, (int) items[1]) < (getDistanceBetween(
					nodeZ, (int) items[2])) ? items[1] : items[2]);
		} else if (SMValues[0] == SMValues[2]&& SMValues[0] > SMValues[1]) {
			node = (int) (getDistanceBetween(nodeZ, (int) items[0]) < (getDistanceBetween(
					nodeZ, (int) items[2])) ? items[0] : items[2]);
		} else {
			node = findHighestValuedSM();
		}	
		return node;
	}
	
	/*
	 * This method is for finding the closest node between 3 nodes 
	 * in the situation that all of them have same SM values
	 */
	private int findClosestNodeToNodeZ() {
		Object[] items = considered3Nodes.toArray();
		int sum = Integer.MAX_VALUE;
		int min = Integer.MIN_VALUE;
		
		for (int i = 0; i < items.length; i++) {
			if (sum > getDistanceBetween(nodeZ, (int) items[i])) {
				sum = (int) getDistanceBetween(nodeZ, (int) items[i]);
				min = (int) items[i];
			}
		}
		return min;
	}
	
	/*
	 * Distance calculator
	 */
	private double getDistanceBetween(int to, int from) {
		return Math.sqrt(Math.pow((coordinates[to][0] - coordinates[from][0]), 2)
				+				+ Math.pow((coordinates[to][1] - coordinates[from][1]), 2));
	}
	
	/*
	 * This method is for finding the closest node between 3 nodes 
	 * in the situation that all of them have different SM values
	 */
	private int findHighestValuedSM() {
		Object[] items = considered3Nodes.toArray();
		int max = (int) items[0];
		int indice = 0;

		for (int i = 1; i < items.length; i++) {
			if ((int) items[i] < max) {
				max = (int) items[i];
				indice = i;
			}
		}

		return (int) items[indice];
	}
	
	
	/**
	 * For each node i in S(z), calculate the sum of M(a)
	 * values for the attributes that the nodes carry;
	 * let this value be denoted by SM (i).
	 */
	private void findSMValues() {
		Object[] items = considered3Nodes.toArray();
		for (int i = 0; i < items.length; i++) {
			for (int j = 0; j < reachableNodesByAttributesTable.length; j++) {
				if (reachableNodesByAttributesTable[j].contains(items[i])) {
					SMValues[i] += maximumCoverageTable[j];
				}
			}
		}
	}

	/**
	 * Initial step of algorithm
	 */
	private void init() {
		// Creating and initializing the empty routes
		routes = new ArrayList[numberOfVehicles];		
		for (int i = 0; i < routes.length; i++) {routes[i] = new ArrayList();}
				
		// Creating maximum # of times that attribute a can be covered
		this.maximumCoverageTable = new int[numberOfAttributes];
		
		// Number of vehicles
		this.K = numberOfVehicles;
		
		//Creating Maximum # of times that an attribute can be covered
		createMaximumCoverageTable();
		
		// Creating and initializing the node/attribute table
		reachableNodesByAttributesTable = new HashSet[numberOfAttributes];

		// Creating the set of unvisited nodes by attributes
		generateReachableNodesSetsbyAttributes();
	}

	private void createMaximumCoverageTable() {
		for (int i = 0; i < numberOfAttributes; i++) {
			int sum = 0;
			for (int j = 0; j < attributes.length; j++) {
				sum += attributes[j][i];
			}
			this.maximumCoverageTable[i] = sum;
		}	
	}

	/**
	 * This method generates sets of nodes that are categorized by their
	 * attributes
	 */
	private void generateReachableNodesSetsbyAttributes() {
		// initialize the table by sets
		for (int i = 0; i < this.reachableNodesByAttributesTable.length; i++) {
			this.reachableNodesByAttributesTable[i] = new HashSet();
		}

		// Adding the nodes to sets
		for (int i = 0; i < super.attributes[0].length; i++) {
			for (int j = 0; j < super.attributes.length; j++) {
				if (super.attributes[j][i] == 1) {
					this.reachableNodesByAttributesTable[i].add(j);
				}
			}
		}
	}
	
	/**
	 * Finding the closest 3 nodes to Z that are not visited yet
	 */
	private void findConsiderable3Nodes() {
		for (int i = 0; i < 3; i++) {
			double sum = Double.MAX_VALUE;
			int indice = Integer.MIN_VALUE;

			for (int j = 1; j < distances[nodeZ].length; j++) {
				if (sum > distances[nodeZ][j] && !considered3Nodes.contains(j)	&& !isVisited(j)) {
					sum = distances[nodeZ][j];
					indice = j;
				}
			}
			considered3Nodes.add(indice);
		}
	}

	@Override
	public void showResults(){	
		System.out.println("Results of Myopic 2");
		for (int i = 0; i < routes.length; i++) {
			System.out.println("Route " + (i + 1) + " time : "+ calculateRouteCost(routes[i]));
			System.out.println("Route " + (i + 1) + " :  0 -> " + routes[i].toString() + " -> 0");
			System.out.println();
		}
	}
	
	/**
	 * Checking the node is visited or not
	 */
	private boolean isVisited(int nodeID) {
		for (int i = 0; i < reachableNodesByAttributesTable.length; i++) {
			if (reachableNodesByAttributesTable[i].contains(nodeID)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * After insertion, remove the node from reachable node list
	 */
	private void removeNodeFromTableAfterInsertion(int id) {
		for (int i = 0; i < reachableNodesByAttributesTable.length; i++) {
			if (reachableNodesByAttributesTable[i].contains(id)) {
				reachableNodesByAttributesTable[i].remove(id);
			}
		}
	}
	
	/**
	 * After insertion, removing the attributes of this node from table
	 */
	private void removeNodeFromAttributeTableAfterInsertion(int id) {
		for (int i = 0; i < attributes[0].length; i++) {
			attributes[id][i] = 0;
		}
	}
	
	/**
	 * 
	 * @param route
	 * @return route cost
	 */
	private double calculateRouteCost(ArrayList route) {
		double cost = 0;
		double firstEdge = getDistanceBetween(0, (int) route.get(0));
		double lastEdge = getDistanceBetween(
				(int) route.get(route.size() - 1), 0);
		cost = firstEdge + lastEdge;

		if (route.size() > 1) {
			for (int i = 0; i < route.size() - 1; i++) {
				cost += getDistanceBetween((int) route.get(i),
						(int) route.get(i + 1));
			}
		}
		return cost;
	}
}
