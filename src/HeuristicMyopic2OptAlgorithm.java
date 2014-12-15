import java.util.ArrayList;
import java.util.HashSet;

public class HeuristicMyopic2OptAlgorithm extends Algorithm {
	private HashSet[] reachableNodesByAttributesTable;
	private ArrayList[] routes;
	private double nodeAtrributeTable[][];
	private int selectedAtrribute;
	
	
	
	@Override
	public void execute() {
		init();
		initAllVehicles();
		executeAlgorithm();
	}
	
	/**
	 * Initial step of algorithm
	 */
	private void init() {
		// Creating and initializing the empty routes
		routes = new ArrayList[numberOfVehicles];
		for (int i = 0; i < routes.length; i++) {routes[i] = new ArrayList();}

		// Creating and initializing the node/attribute table
		reachableNodesByAttributesTable = new HashSet[numberOfAttributes];
		nodeAtrributeTable = new double[3][numberOfAttributes];
		findAttributesTotalNumberOfAppearences();

		// Creating the set of unvisited nodes by attributes
		generateReachableNodesSetsbyAttributes();
	}

	/**
	 * Initially,all vehicles visit a node
	 */
	private void initAllVehicles() {
		// Find max appeared attribute and assign initially -> a
		selectedAtrribute = findMaxAppearedAttribute();

		for (int i = 0; i < numberOfVehicles; i++) {
			if (routes[i].isEmpty()) {
				int selectedNode = findClosestNodeToDepot(reachableNodesByAttributesTable[selectedAtrribute]);
				addToTable(selectedNode);
				routes[i].add(selectedNode);
				removeNodeFromTableAfterInsertion(selectedNode);
				calculateRatios();
				selectedAtrribute = chooseNewAttribute();
			}
		}
	}
	
	/**
	 * Execution of algorithm after initial steps completed
	 */
	private void executeAlgorithm() {
		Object[] nodes = null;
		int route = 0;
		int tempNode = 0;
		double min = Double.MAX_VALUE;
		int position = 0;
		ArrayList tempRoute;

		while (true) {
			nodes = reachableNodesByAttributesTable[selectedAtrribute].toArray();
			route = 0;
			tempNode = 0;
			min = Double.MAX_VALUE;
			position = 0;
			
			for (int i = 0; i < nodes.length; i++) {
				int node = (int) nodes[i];
				for (int r = 0; r < routes.length; r++) {
					for (int n = 0; n < routes[r].size() + 1; n++) {
						tempRoute = routes[r];
						tempRoute.add(n, node);
						double cost = calculateRouteCost(tempRoute);
						if (min > cost && cost <= tMax) {
							min = cost;
							tempNode = node;
							position = n;
							route = r;
						}
						tempRoute.remove(n);
					}
				}
			}
			if(tempNode == 0){break;}
			addToTable(tempNode);
			routes[route].add(position, tempNode);
			removeNodeFromTableAfterInsertion(tempNode);
			calculateRatios();
			selectedAtrribute = chooseNewAttribute();
		}
	}
	
	/**
	 * Finding the max appeared attribute 
	 */
	private int findMaxAppearedAttribute() {
		int max = 0;
		for (int i = 0; i < this.numberOfAttributes; i++) {
			max = (nodeAtrributeTable[1][i] > max) ? i : max;
		}
		return max;
	}
	
	/**
	 * Finding # of appearances by attributes
	 */
	private void findAttributesTotalNumberOfAppearences() {
		for (int i = 0; i < numberOfAttributes; i++) {
			nodeAtrributeTable[1][i] = findSumOfAttribute(i);
		}
	}

	/**
	 * @param attribute
	 * @return sum of attribute
	 */
	private double findSumOfAttribute(int attribute) {
		double total = 0;

		for (int i = 0; i < attributes.length; i++) {
			total += attributes[i][attribute];
		}
		return total;
	}

	/**
	 * After insertion, remove the node from reachable node list
	 * @param id
	 */
	private void removeNodeFromTableAfterInsertion(int id) {
		for (int i = 0; i < this.reachableNodesByAttributesTable.length; i++) {
			if (this.reachableNodesByAttributesTable[i].contains(id)) {
				this.reachableNodesByAttributesTable[i].remove(id);
			}
		}
	}

	/**
	 * A node is visited or not
	 * @param id
	 * @return true/false
	 */
	private boolean isVisited(int id) {
		for (int i = 0; i < this.reachableNodesByAttributesTable.length; i++) {
			if (this.reachableNodesByAttributesTable[i].contains(id)) {
				return false;
			}
		}
		return true;
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
	 * This method is for finding the closest node to depot for initial situations
	 * @param selectedNodes
	 * @return
	 */
	private int findClosestNodeToDepot(HashSet selectedNodes) {
		Object[] items = selectedNodes.toArray();
		int sum = Integer.MAX_VALUE;
		int min = Integer.MIN_VALUE;

		for (int i = 0; i < items.length; i++) {
			if (min < calculateDistanceBetween(0, (int) items[i])) {
				sum = (int) calculateDistanceBetween(0, (int) items[i]);
				min = (int) items[i];
			}
		}
		return min;
	}

	/**
	 * Distance Calculator
	 * @param to
	 * @param from
	 * @return
	 */
	private double calculateDistanceBetween(int to, int from) {
		return Math.sqrt(Math.pow((coordinates[to][0] - coordinates[from][0]), 2)
				+ Math.pow((coordinates[to][1] - coordinates[from][1]), 2));
	}
	
	/**
	 * After the ratio calculations, selecting the suitable attribute by ratio
	 * @return selected attribute
	 */
	private int chooseNewAttribute() {
		int tempSelected = 0;
		double min = nodeAtrributeTable[2][0];

		for (int i = 1; i < numberOfAttributes; i++) {
			if (min > nodeAtrributeTable[2][i]) {
				tempSelected = i;
				min = nodeAtrributeTable[2][i];
			} else if (min == nodeAtrributeTable[2][i]) {
				if (nodeAtrributeTable[1][tempSelected] < nodeAtrributeTable[1][i]) {
					tempSelected = i;
					min = nodeAtrributeTable[2][i];
				}
			}
		}
		return tempSelected;
	}
	
	/**
	 * Calculate ratios of node/attribute table
	 */
	private void calculateRatios() {
		for (int i = 0; i < numberOfAttributes; i++) {
			nodeAtrributeTable[2][i] = nodeAtrributeTable[0][i]
					/ nodeAtrributeTable[1][i];
		}
	}
	
	/**
	 * Adding selected not to Node/Attribute table
	 * @param selectedNode
	 */
	private void addToTable(int selectedNode) {
		for (int i = 0; i < numberOfAttributes; i++) {
			nodeAtrributeTable[0][i] += attributes[selectedNode][i];
		}
	}

	/**
	 * 
	 * @param route
	 * @return route cost
	 */
	private double calculateRouteCost(ArrayList route) {
		double cost = 0;
		double firstEdge = calculateDistanceBetween(0, (int) route.get(0));
		double lastEdge = calculateDistanceBetween(
				(int) route.get(route.size() - 1), 0);
		cost = firstEdge + lastEdge;

		if (route.size() > 1) {
			for (int i = 0; i < route.size() - 1; i++) {
				cost += calculateDistanceBetween((int) route.get(i),
						(int) route.get(i + 1));
			}
		}
		return cost;
	}

	@Override
	public void showResults() {
		for (int i = 0; i < routes.length; i++) {
			System.out.println("Route "+ (i+1));
			System.out.println("Route " + (i+1) + " time : " + calculateRouteCost(routes[i]));
			System.out.println("Route " +  (i+1) +" :  0 -> " + routes[i].toString() +" -> 0");
			System.out.println();
		}
	}
}
