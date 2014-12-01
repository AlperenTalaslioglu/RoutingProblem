import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class HeuristicAlgorithm {
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

	public HeuristicAlgorithm() {
		this.nodeZ = 0;
		this.vehicle = 1;
		this.selectedNodes = new HashSet();
		this.SMValues = new int[3];
	}

	public void getData(FileReader reader) {
		this.numberOfVehicles = reader.getNumberOfVehicles();
		this.tMax = reader.getMaxTimeOfVehicle();
		this.attributes = reader.getAttributes();
		this.numberOfNodes = reader.getAttributes().length;
		this.distances = new double[numberOfNodes][numberOfNodes];
		this.coordinates = reader.getCoordinates();
		this.maximumAttributeCoverage = new int[reader.getNumberOfAttributes()];
		this.reachableNodesByAttributesTable = new HashSet[reader
				.getNumberOfAttributes()];
		this.routes = new ArrayList[this.numberOfVehicles];
	}

	public void execute() {
		init();
		routes[(vehicle - 1)] = new ArrayList();
		while (vehicle <= numberOfVehicles) {
			step1();
			step2();
			step3();
			step4();
		}
		//
		//
		//
		// System.out.println(routes[0].toString());
		//
		//
		// for(int i = 0; i<maximumAttributeCoverage.length; i++){
		// System.out.print(maximumAttributeCoverage[i] + " ");
		// }

	}

	private boolean isRouteFeasible(ArrayList route) {
		double routeTime;
		if (route.size() < 2) {
			routeTime = calculateDistanceBetween(nodeZ, (int) route.get(0));
			routeTime += calculateDistanceBetween(nodeZ,
					(int) route.get(route.size() - 1));
		} else {
			routeTime = calculateDistanceBetween(nodeZ, (int) route.get(0));
			routeTime += calculateDistanceBetween(nodeZ,
					(int) route.get(route.size() - 1));
			for (int i = 0; i < route.size() - 1; i++) {
				routeTime += calculateDistanceBetween((int) route.get(i),
						(int) route.get(i + 1));
			}
		}

		if (routeTime <= tMax) {
			return true;
		} else {
			return false;
		}
	}

	private void step4() {
		routes[(vehicle - 1)].add(tempZ);
		if (isRouteFeasible(routes[(vehicle - 1)])) {
			nodeZ = tempZ;
			removeNodeFromTableAfterInsertion(tempZ);
			removeNodeFromAttributeTableAfterInsertion(tempZ);
			generateReachableNodesbyAttributes();
			generateMaximumAttributeCoverageMatrix();
			selectedNodes.clear();
			Arrays.fill(SMValues, 0);
		} else {
			routes[(vehicle - 1)].remove(routes[(vehicle - 1)].size() - 1);
			step5();
		}
	}

	private void step5() {
		System.out.println("New Route!");
		this.nodeZ = 0;
		this.vehicle += 1;
		if(vehicle > numberOfVehicles){
			return;
		}else{
			this.routes[(vehicle - 1)] = new ArrayList();
			generateMaximumAttributeCoverageMatrix();
			generateReachableNodesbyAttributes();
			selectedNodes.clear();
		}
		
	}

	private void step3() {
		tempZ = checkSMValues();
		System.out.println(tempZ);
	}

	private void step2() {
		findSMValues();
	}

	private int checkSMValues() {
		int node = 0;
		Object[] items = selectedNodes.toArray();
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

		System.out.println(selectedNodes.toString() + " ->" + node);
		System.out.println(maximumAttributeCoverage[0] + " " + maximumAttributeCoverage[1] + " " + maximumAttributeCoverage[2]+ " " + maximumAttributeCoverage[3]);
		System.out.println(SMValues[0] + " " + SMValues[1] + " " + SMValues[2]);
		
		return node;
	}

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

	private void step1() {
		findConsiderable3Nodes();
	}

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

	private void init() {
		generateDistanceMatrix();
		generateMaximumAttributeCoverageMatrix();
		generateReachableNodesbyAttributes();
	}

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

	// maximum # of times that attribute a can be covered

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

	private void generateDistanceMatrix() {
		for (int i = 0; i < numberOfNodes; i++) {
			for (int j = 0; j < numberOfNodes; j++) {
				distances[i][j] = calculateDistanceBetween(i, j);
			}
		}
	}

	private double calculateDistanceBetween(int i, int j) {
		return Math.sqrt(Math.pow((coordinates[i][0] - coordinates[j][0]), 2)
				+ Math.pow((coordinates[i][1] - coordinates[j][1]), 2));
	}

	public void showResults() {
	}

	private boolean isVisited(int nodeID) {
		for (int i = 0; i < reachableNodesByAttributesTable.length; i++) {
			if (reachableNodesByAttributesTable[i].contains(nodeID)) {
				return false;
			}
		}
		return true;
	}

	private void removeNodeFromTableAfterInsertion(int id) {
		for (int i = 0; i < reachableNodesByAttributesTable.length; i++) {
			if (reachableNodesByAttributesTable[i].contains(id)) {
				reachableNodesByAttributesTable[i].remove(id);
			}
		}
	}

	private void removeNodeFromAttributeTableAfterInsertion(int id) {
		for (int i = 0; i < attributes[0].length; i++) {
			attributes[id][i] = 0;
		}
	}

}
