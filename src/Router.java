import java.io.FileNotFoundException;


public class Router {

	public static void main(String[] args) throws FileNotFoundException {
		//Read file
		FileReader reader = new FileReader("data.txt");
		reader.readFile();
		
		//Creating Algorithm
		HeuristicAlgorithm algorithm = new HeuristicAlgorithm();
		algorithm.getData(reader);
		algorithm.execute();
		algorithm.showResults();		
	}
}
