import java.io.FileNotFoundException;


public class Router {

	public static void main(String[] args) throws FileNotFoundException {
		//Read file
		FileReader reader = new FileReader("data.txt");
		reader.readFile();
		
		Algorithm myopic1a = new HeuristicMyopic1aAlgorithm();
		myopic1a.getData(reader);
		myopic1a.execute();
		myopic1a.showResults();
		
	}
}
