import java.io.FileNotFoundException;


public class Router {

	public static void main(String[] args) throws FileNotFoundException {
		//Read file
		FileReader reader = new FileReader("data.txt");
		
		Algorithm myopic1a = new HeuristicMyopic1aAlgorithm();
		myopic1a.getData(new FileReader("data.txt"));
		myopic1a.execute();
		myopic1a.showResults();
		
		Algorithm myopic2 = new HeuristicMyopic2Algorithm();
		myopic2.getData(new FileReader("data.txt"));
		myopic2.execute();
		myopic2.showResults();
		
//		HeuristicAlgorithmOld algorithm = new HeuristicAlgorithmOld();
//		algorithm.getData(reader);
//		algorithm.execute();
//		algorithm.showResults();
		
		
	}
}
