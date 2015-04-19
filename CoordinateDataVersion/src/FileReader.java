import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class FileReader {
	private String fileName;
	private int maxTimeOfVehicle;
	private int velocityOfVehicle;
	private int numberOfVehicles;
	private int numberOfNodes;
	private int numberOfAttributes;
	private int[][] coordinates;
	private int[][] attributes;
	private double[][] distances;
	private double[][] timeMatrix;
	private double[][] geoCoordinates;

	public FileReader(String fileName) throws FileNotFoundException {
		this.fileName = fileName;
		readFile();
	}

	public void readFile() throws FileNotFoundException {
		@SuppressWarnings("resource")
		Scanner fileScanner = new Scanner(new File(fileName));
		maxTimeOfVehicle = fileScanner.nextInt();
		numberOfVehicles = fileScanner.nextInt();
		numberOfNodes = fileScanner.nextInt();
		numberOfAttributes = fileScanner.nextInt();

		distances = new double[numberOfNodes+1][numberOfNodes+1];
		timeMatrix = new double[numberOfNodes+1][numberOfNodes+1];
		coordinates = new int[numberOfNodes + 1][2]; // +1 is for depot
		geoCoordinates = new double[numberOfNodes + 1][2]; // +1 is for depot
		attributes = new int[numberOfNodes + 1][numberOfAttributes]; // +1 is for depot		

		int itemIndex = 0;
		while (fileScanner.hasNext() && itemIndex < (numberOfNodes+1)) {
			// First getting the geo coordinates of current indexed item		
			String[] latLot = (fileScanner.next()).split(",");
			geoCoordinates[itemIndex][0] = Double.parseDouble(latLot[0]);
			geoCoordinates[itemIndex][1] = Double.parseDouble(latLot[1]);
			
			coordinates[itemIndex][0] = getX(geoCoordinates[itemIndex][0],geoCoordinates[itemIndex][1]);
			coordinates[itemIndex][1] = getY(geoCoordinates[itemIndex][1],geoCoordinates[itemIndex][0]);
			
			// Second getting the attributes of current indexed item and putting
			// to array
			for (int i = 1; i < numberOfAttributes + 1; i++) {
				attributes[itemIndex][(i-1)] = fileScanner.nextInt();
			}

			itemIndex++;
		}
		
		int timeMatrixIndex = 0;
		while (fileScanner.hasNextInt()) {		
			
			// Second getting the distances of current indexed item and putting
			// to array
			for (int i = 0; i < numberOfNodes+1; i++) {
				timeMatrix[timeMatrixIndex][i] = (fileScanner.nextInt()) + 3600;
				distances[timeMatrixIndex][i] = timeMatrix[timeMatrixIndex][i];
			}

			if(timeMatrixIndex == numberOfNodes){break;}
			timeMatrixIndex++;
		}		
	}

	public int getMaxTimeOfVehicle() {
		return maxTimeOfVehicle;
	}

	public int getVelocityOfVehicle() {
		return velocityOfVehicle;
	}

	public int getNumberOfVehicles() {
		return numberOfVehicles;
	}

	public int[][] getCoordinates() {
		return coordinates;
	}

	public int[][] getAttributes() {
		return attributes;
	}

	public int getNumberOfAttributes() {
		return numberOfAttributes;
	}
	

	public double[][] getDistances() {
		return distances;
	}

	public double[][] getTimeMatrix() {
		return timeMatrix;
	}

	@Override
	public String toString() {
		return "FileReader [fileName=" + fileName + ", maxTimeOfVehicle="
				+ maxTimeOfVehicle + ", velocityOfVehicle=" + velocityOfVehicle
				+ ", numberOfVehicles=" + numberOfVehicles + ", numberOfNodes="
				+ numberOfNodes + ", numberOfAttributes=" + numberOfAttributes
				+ "]";
	}
	
	public int getX(double lat,double lot){		
		double Re = 6378137;		
		int x = (int) (Re * Math.cos(lat) * Math.cos(lot));
		return x % 800;
	}
	
	public int getY(double lot, double lat){
		double Re = 6378137;		
		int y = (int) (Re * Math.cos(lat) * Math.sin(lot));
		return (-1) * (y % 600);
	}

	public int getNumberOfNodes() {
		return numberOfNodes;
	}

}
