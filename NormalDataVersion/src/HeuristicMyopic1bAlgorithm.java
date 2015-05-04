import gui.MainFrame;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class HeuristicMyopic1bAlgorithm extends Algorithm {
	private HashSet[] reachableNodesByAttributesTable;
	private ArrayList[] routes;
	private double nodeAtrributeTable[][];
	private ArrayList chosenAttributes;
	private ArrayList setA;
	public 	static double totalRouteCosts;


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
		for (int i = 0; i < routes.length; i++) {
			routes[i] = new ArrayList();
		}

		// Creating and initializing the node/attribute table
		reachableNodesByAttributesTable = new HashSet[numberOfAttributes];
		nodeAtrributeTable = new double[3][numberOfAttributes];
		findAttributesTotalNumberOfAppearences();

		// Creating the set of unvisited nodes by attributes
		generateReachableNodeSetsbyAttributes();

		// Chosen attributes by ratios
		chosenAttributes = new ArrayList();

		// The set of nodes that carry an attribute
		setA = new ArrayList();
	}

	/**
	 * Initially,all vehicles visit a node
	 */
	private void initAllVehicles() {
		// Find max appeared attribute and assign initially -> a
		int selectedAtrribute = findMaxAppearedAttribute();

		// First vehicle - inital step
		int selectedNode = findClosestNodeToDepot(reachableNodesByAttributesTable[selectedAtrribute]);
		addToTable(selectedNode);
		routes[0].add(selectedNode);
		removeNodeFromTableAfterInsertion(selectedNode);
		calculateRatios();
		chooseSetOfAttributes();
		generateSASet();

		// Other vehicles
		for (int i = 1; i < numberOfVehicles; i++) {
			HashSet tempSASet = new HashSet();
			tempSASet.addAll(setA);
			int closestToDepot = findClosestNodeToDepot(tempSASet);
			addToTable(closestToDepot);
			routes[i].add(closestToDepot);
			removeNodeFromTableAfterInsertion(closestToDepot);
			calculateRatios();
			chooseSetOfAttributes();
			generateSASet();
		}
	}

	private void generateSASet() {
		this.setA.clear();
		for (int i = 0; i < chosenAttributes.size(); i++) {
			Collection nodes = reachableNodesByAttributesTable[(int) chosenAttributes.get(i)];
			setA.addAll(nodes);
		}
		// Removing duplicated nodes
		HashSet tempSet = new HashSet();
		tempSet.addAll(setA);
		setA.clear();
		setA.addAll(tempSet);
		chosenAttributes.clear();
	}

	/**
	 * Execution of algorithm after initial steps completed
	 */
	private void executeAlgorithm() {

		int position;
		int route;
		int selectedNodeToRoute;
		double min;
		
		while (true) {
			position = 0;
			route = 0;
			selectedNodeToRoute = 0;
			min = Double.MAX_VALUE;
			
			for (int i = 0; i < setA.size(); i++) {
				int node = (int) setA.get(i);
				// Checking all routes to find best insertion
				for (int j = 0; j < routes.length; j++) {
					ArrayList tempRoute = routes[j];				
					for(int n = 0; n<tempRoute.size()+1; n++){
						tempRoute.add(n, node);				
						double cost = (calculateRouteCost(tempRoute));
						if( min >  cost && cost <= tMax){
							min = cost;
							position = n;
							route = j;
							selectedNodeToRoute = node;									
						}
						tempRoute.remove(n);						
					}
				}				
			}
			
			if(selectedNodeToRoute == 0){break;}			
			addToTable(selectedNodeToRoute);
			routes[route].add(position,selectedNodeToRoute);
			removeNodeFromTableAfterInsertion(selectedNodeToRoute);
			calculateRatios();
			chooseSetOfAttributes();
			generateSASet();		
		}
		
		for (int i = 0; i < routes.length; i++) {
			totalRouteCosts += calculateRouteCost(routes[i]);			
		}	
	}

	private void chooseSetOfAttributes() {
		int tempSelected = 0;
		double min = nodeAtrributeTable[2][0];

		for (int i = 1; i < numberOfAttributes; i++) {
			if (min > nodeAtrributeTable[2][i]) {
				tempSelected = i;
				min = nodeAtrributeTable[2][i];
			}
		}

		chosenAttributes.add(tempSelected);

		for (int i = 0; i < numberOfAttributes; i++) {
			if (i == tempSelected) {
				continue;
			} else if (nodeAtrributeTable[2][tempSelected] == nodeAtrributeTable[2][i]) {
				chosenAttributes.add(i);
			}
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
	 * 
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
	 * 
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
	private void generateReachableNodeSetsbyAttributes() {
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
	 * This method is for finding the closest node to depot for initial
	 * situations
	 * 
	 * @param selectedNodes
	 * @return
	 */
	private int findClosestNodeToDepot(HashSet selectedNodes) {
		Object[] items = selectedNodes.toArray();
		int closest = (int) items[0];
		double min = calculateDistanceBetween(0, (int) items[0]);
		for (int i = 1; i < items.length; i++) {
			double tempValue = calculateDistanceBetween(0, (int) items[i]);
			if (tempValue < min) {
				min = tempValue;
				closest = (int) items[i];
			}
		}
		return closest;
	}

	/**
	 * Distance Calculator
	 * 
	 * @param to
	 * @param from
	 * @return
	 */
	private double calculateDistanceBetween(int to, int from) {
		return timeMatrix[to][from];
	}

	/**
	 * After the ratio calculations, selecting the suitable attribute by ratio
	 * 
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
	 * 
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
		System.out.println("Results of Myopic 1b");
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
