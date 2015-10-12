import java.util.Comparator;

public class AngleComparator implements Comparator<Coordinate> {

	@Override
	public int compare(Coordinate vect1, Coordinate vect2) {
		
		return (int) (vect1.getVectorAngle()-vect2.getVectorAngle());
	}

}
