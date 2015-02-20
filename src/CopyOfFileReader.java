import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
/**
 * 
 * This file reader is edited for 
 * new Van data
 * 
 * This file reader is only working 
 * with this data
 *
 */
public class CopyOfFileReader {
	private String fileName;
	private int maxTimeOfVehicle;
	private int velocityOfVehicle;
	private int numberOfVehicles;
	private int numberOfNodes;
	private int numberOfAttributes;
	private int[][] attributes;
	private double[][] timeMatrix;

	public CopyOfFileReader(String fileName) throws FileNotFoundException {
		this.fileName = fileName;
		readFile();
	}

	public void readFile() throws FileNotFoundException {
		Scanner fileScanner = new Scanner(new File(fileName));
		maxTimeOfVehicle = fileScanner.nextInt();
		numberOfVehicles = fileScanner.nextInt();
		numberOfNodes = fileScanner.nextInt();
		numberOfAttributes = fileScanner.nextInt();
		
		attributes = new int[numberOfNodes+1][numberOfAttributes];		

		int itemIndex = 0;
		while (fileScanner.hasNextInt()) {			

			// Second getting the attributes of current indexed item and putting
			// to array
			for (int i = 0; i < numberOfAttributes; i++) {
				attributes[itemIndex][i] = fileScanner.nextInt();
			}
			
			if(itemIndex == numberOfNodes){break;}
			itemIndex++;
		}		
				
		timeMatrix = new double[numberOfNodes+1][numberOfNodes+1];
		
		int timeMatrixIndex = 0;
		while (fileScanner.hasNextInt()) {			

			// Second getting the distances of current indexed item and putting
			// to array
			for (int i = 0; i < numberOfNodes+1; i++) {
//				timeMatrix[timeMatrixIndex][i] = (fileScanner.nextInt()) + 3600;
				timeMatrix[timeMatrixIndex][i] = (fileScanner.nextInt());
			}

			if(timeMatrixIndex == numberOfNodes){break;}
			timeMatrixIndex++;
		}		
	}

	public double[][] getTimeMatrix(){return timeMatrix;}

	public int getNumberOfVehicles() {return numberOfVehicles;}
	
	public int getNumberOfNodes(){return numberOfNodes;}

	public int getMaxTimeOfVehicle() {return maxTimeOfVehicle;}

	public int[][] getAttributes() {return attributes;}

	public int getNumberOfAttributes() {return numberOfAttributes;}
	
}
