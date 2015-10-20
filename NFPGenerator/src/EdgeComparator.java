import java.util.Comparator;

public class EdgeComparator implements Comparator<Vector> {

	@Override
	public int compare(Vector vect1, Vector vect2) {
		
		return  vect1.getEdgeNumber()-vect2.getEdgeNumber();
	}

}