
public class Algorithm {
	protected int numberOfVehicles;
	protected int tMax;
	protected int[][] attributes;
	protected int numberOfNodes;
	protected double[][] distances;
	protected int[][] coordinates;
	protected int numberOfAttributes;
	protected double[][] timeMatrix;

	
	/*
	 * Getting data from file object
	 */
	public void getData(FileReader reader) {
		this.numberOfVehicles = reader.getNumberOfVehicles();
		this.tMax = reader.getMaxTimeOfVehicle();
		this.attributes = reader.getAttributes();
		this.numberOfNodes = reader.getAttributes().length;
		this.distances = reader.getDistances();
		this.coordinates = reader.getCoordinates();
		this.numberOfAttributes = reader.getNumberOfAttributes();
		this.timeMatrix = reader.getTimeMatrix();
	}
	
	public void execute(){
		System.out.println("Algorithm Execution!");		
	}

	public void showResults() {
		System.out.println("Algorithm Results!");
		System.out.println(numberOfAttributes);
	}
}
