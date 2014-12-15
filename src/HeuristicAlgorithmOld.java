import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class HeuristicAlgorithmOld {
	private int[][] attributes;
	private int[][] coordinates;
	private double[][] distances;
	private int nodeZ;
	private int vehicle;
	private int numberOfVehicles;
	private int numberOfNodes;
	private int tMax;
	private int[] maximumAttributeCoverage;
	private HashSet[] reachableNodesByAttributesTable;
	private HashSet selectedNodes;
	private int[] SMValues;
	private ArrayList[] routes;
	private int tempZ;

	public HeuristicAlgorithmOld() {
		this.nodeZ = 0;
		this.vehicle = 1;
		this.selectedNodes = new HashSet(); // Considered 3 nodes
		this.SMValues = new int[3];
	}

	/*
	 * Getting data from file object
	 */
	public void getData(FileReader reader) {
		this.numberOfVehicles = reader.getNumberOfVehicles();
		this.tMax = reader.getMaxTimeOfVehicle();
		this.attributes = reader.getAttributes();
		this.numberOfNodes = reader.getAttributes().length;
		this.distances = new double[numberOfNodes][numberOfNodes];
		this.coordinates = reader.getCoordinates();
		this.maximumAttributeCoverage = new int[reader.getNumberOfAttributes()];
		this.reachableNodesByAttributesTable = new HashSet[reader.getNumberOfAttributes()];
		this.routes = new ArrayList[this.numberOfVehicles];
	}

	/*
	 * Algorithm executer
	 */
	public void execute() {
		init();
		routes[(vehicle - 1)] = new ArrayList();
		while (vehicle <= numberOfVehicles) {
			step1();
			step2();
			step3();
			step4();
		}		
	}

	/*
	 * Checking the route's feasibility by tMax 
	 */
	private boolean isRouteFeasible(ArrayList route) {
		double routeTime;
		if (route.size() < 2) {
			routeTime = calculateDistanceBetween(nodeZ, (int) route.get(0));
			routeTime += calculateDistanceBetween(nodeZ,
					(int) route.get(route.size() - 1));
		} else {
			routeTime = calculateDistanceBetween(nodeZ, (int) route.get(0));
			routeTime += calculateDistanceBetween(nodeZ,(int) route.get(route.size() - 1));
			for (int i = 0; i < route.size() - 1; i++) {
				routeTime += calculateDistanceBetween((int) route.get(i),(int) route.get(i + 1));
			}
		}

		if (routeTime <= tMax) {
			return true;
		} else {
			return false;
		}
	}
	
	/*
	 * Update z=0 (vehicle k returns to the depot)
	 */
	private void step5() {
		//Update k=k+1 (consider a new vehicle). If k>K, STOP.
		this.nodeZ = 0;
		this.vehicle += 1;
		if(vehicle > numberOfVehicles){
			//STOP
			return;
		}else{
			//GOTO STEP1
			this.routes[(vehicle - 1)] = new ArrayList();
			Arrays.fill(maximumAttributeCoverage, 0);
			Arrays.fill(maximumAttributeCoverage, 0);
			Arrays.fill(SMValues, 0);
			generateMaximumAttributeCoverageMatrix();
			generateReachableNodesbyAttributes();
			selectedNodes.clear();
		}		
	}
	
	/*
	 * Can we visit nodeZ after nodeZ by vehicle k feasibly? That is, is routeTime <=Tmax?
	 */
	private void step4() {
		routes[(vehicle - 1)].add(tempZ);//adding item temprarily to route
		if (isRouteFeasible(routes[(vehicle - 1)])) { // Checking the route
			//If yes, insert node z' to the route. Update node z as z=z'. 	
			nodeZ = tempZ;
			removeNodeFromTableAfterInsertion(tempZ);
			removeNodeFromAttributeTableAfterInsertion(tempZ);
			generateReachableNodesbyAttributes();
			generateMaximumAttributeCoverageMatrix();
			selectedNodes.clear();
			Arrays.fill(SMValues, 0);
		} else {
			routes[(vehicle - 1)].remove(routes[(vehicle - 1)].size() - 1);//if not feasible, remove item from route
			step5();
		}
	}
	/*
	 * Select the node with the highest "SMValues[i]" value. 
	 * If there is a tie, select the node that is closest to "nodeZ".
	 */
	private void step3() {
		tempZ = checkSMValues();
	}

	/*
	 * For each node i in "selectedNodes",
	 * calculate the sum of "maximumAttributeCoverage" values for the attributes that the nodes carry; 
	 * let this value be denoted by "SMValues[i]"
	 */
	private void step2() {
		findSMValues();
	}

	/*
	 * This method controls the 3 nodes's SM values
	 * Compares them and find the accurate node to select
	 */
	private int checkSMValues() {
		int node = 0;
		Object[] items = selectedNodes.toArray();
		
		for(int i = 0; i<items.length; i++){
			System.out.println(items[i]);
		}		
		
		if (SMValues[0] == SMValues[1] && SMValues[1] == SMValues[2]) {
			node = findClosestNodeToNodeZ();
		} else if (SMValues[0] == SMValues[1] && SMValues[0] > SMValues[2]) {
			node = (int) (calculateDistanceBetween(nodeZ, (int) items[0]) < (calculateDistanceBetween(
					nodeZ, (int) items[1])) ? items[0] : items[1]);
		} else if (SMValues[1] == SMValues[2]&& SMValues[1] > SMValues[0]) {
			node = (int) (calculateDistanceBetween(nodeZ, (int) items[1]) < (calculateDistanceBetween(
					nodeZ, (int) items[2])) ? items[1] : items[2]);
		} else if (SMValues[0] == SMValues[2]&& SMValues[0] > SMValues[1]) {
			node = (int) (calculateDistanceBetween(nodeZ, (int) items[0]) < (calculateDistanceBetween(
					nodeZ, (int) items[2])) ? items[0] : items[2]);
		} else {
			node = findHighestValuedSM();
		}

//		System.out.println(selectedNodes.toString() + " ->" + node);
//		System.out.println(maximumAttributeCoverage[0] + " " + maximumAttributeCoverage[1] + " " + maximumAttributeCoverage[2]+ " " + maximumAttributeCoverage[3]);
//		System.out.println(SMValues[0] + " " + SMValues[1] + " " + SMValues[2]);
		
		return node;
	}
	
	/*
	 * This method is for finding the closest node between 3 nodes 
	 * in the situation that all of them have different SM values
	 */
	private int findHighestValuedSM() {
		Object[] items = selectedNodes.toArray();
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

	/*
	 * This method is for finding the closest node between 3 nodes 
	 * in the situation that all of them have same SM values
	 */
	private int findClosestNodeToNodeZ() {
		Object[] items = selectedNodes.toArray();
		int sum = Integer.MAX_VALUE;
		int min = Integer.MIN_VALUE;
		


		for (int i = 0; i < items.length; i++) {
			if (sum > calculateDistanceBetween(nodeZ, (int) items[i])) {
				sum = (int) calculateDistanceBetween(nodeZ, (int) items[i]);
				min = (int) items[i];
			}
		}
		return min;
	}

	/*
	 * For each node i in S(z), calculate the sum of M(a)
	 * values for the attributes that the nodes carry;
	 * let this value be denoted by SM (i).
	 */
	private void findSMValues() {
		Object[] items = selectedNodes.toArray();
		for (int i = 0; i < items.length; i++) {
			for (int j = 0; j < reachableNodesByAttributesTable.length; j++) {
				if (reachableNodesByAttributesTable[j].contains(items[i])) {
					SMValues[i] += maximumAttributeCoverage[j];
				}
			}
		}
	}
	/*
	 * Consider the 3 nodes that are not visited by any vehicles yet and are closest to node z.
	 * Denote this set as set "selectedNodes"
	 */
	private void step1() {
		findConsiderable3Nodes();
	}

	/*
	 * Finding the closest 3 nodes to Z that are not visited yet
	 */
	private void findConsiderable3Nodes() {
		for (int i = 0; i < 3; i++) {
			double sum = Double.MAX_VALUE;
			int indice = Integer.MIN_VALUE;

			for (int j = 1; j < distances[nodeZ].length; j++) {
				if (sum > distances[nodeZ][j] && !selectedNodes.contains(j)
						&& !isVisited(j)) {
					sum = distances[nodeZ][j];
					indice = j;
				}
			}
			selectedNodes.add(indice);
		}
	}

	/*
	 * Initialize
	 */
	private void init() {
		generateDistanceMatrix();
		generateMaximumAttributeCoverageMatrix();
		generateReachableNodesbyAttributes();
	}

	/*
	 * This method generates sets of nodes that are
	 * categorized by their attributes
	 */
	private void generateReachableNodesbyAttributes() {
		// initialize the table by sets
		for (int i = 0; i < reachableNodesByAttributesTable.length; i++) {
			reachableNodesByAttributesTable[i] = new HashSet();
		}

		// Adding the nodes to sets
		for (int i = 0; i < attributes[0].length; i++) {
			for (int j = 0; j < attributes.length; j++) {
				if (attributes[j][i] == 1) {
					reachableNodesByAttributesTable[i].add(j);
				}
			}
		}

	}
	

	/*
	 *  maximum # of times that attribute a can be covered
	 */
	private void generateMaximumAttributeCoverageMatrix() {
		int index = 0;
		for (int i = 0; i < attributes[0].length; i++) {
			int sum = 0;
			for (int j = 0; j < attributes.length; j++) {
				sum += attributes[j][i];
			}
			this.maximumAttributeCoverage[index] = sum;
			index++;
		}
	}

	/*
	 * Initial distance matrix generation
	 */
	private void generateDistanceMatrix() {
		for (int i = 0; i < numberOfNodes; i++) {
			for (int j = 0; j < numberOfNodes; j++) {
				distances[i][j] = calculateDistanceBetween(i, j);
			}
		}
	}

	/*
	 * Distance calculator
	 */
	private double calculateDistanceBetween(int i, int j) {
		return Math.sqrt(Math.pow((coordinates[i][0] - coordinates[j][0]), 2)+ Math.pow((coordinates[i][1] - coordinates[j][1]), 2));
	}

	/*
	 * Shows the routes
	 */
	public void showResults() {
		for(int i = 0; i<routes.length; i++){
			System.out.println("0 -> " + routes[i].toString() + " -> 0");
		}
	}
	
	/*
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

	/*
	 * After insertion, remove the node from reachable node list
	 */
	private void removeNodeFromTableAfterInsertion(int id) {
		for (int i = 0; i < reachableNodesByAttributesTable.length; i++) {
			if (reachableNodesByAttributesTable[i].contains(id)) {
				reachableNodesByAttributesTable[i].remove(id);
			}
		}
	}
	
	/*
	 * After insertion, removing the attributes of this node from table
	 */
	private void removeNodeFromAttributeTableAfterInsertion(int id) {
		for (int i = 0; i < attributes[0].length; i++) {
			attributes[id][i] = 0;
		}
	}

}
