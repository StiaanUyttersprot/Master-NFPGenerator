import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Stiaan
 */
public class Main {

	/**
	 * @param args
	 *            the command line arguments
	 * @throws java.io.FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException {

		DrawJavaFX drawTool = new DrawJavaFX();

		File convex1Data = new File("Convex1.txt");
		File convex2Data = new File("Convex2.txt");

		File concave1Data = new File("Concave1.txt");
		File concave2Data = new File("Concave2.txt");
		
		File rectangle1Data = new File("Rectangle1.txt");
		File block1Data = new File("Block1.txt");
		
		File puzzle1Data = new File("Puzzle4.txt");
		File puzzle2Data = new File("Puzzle2.txt");
		File puzzle3Data = new File("Puzzle3.txt");
		
		File sawtooth1Data = new File("Sawtooth1.txt");
		File sawtooth2Data = new File("Sawtooth2.txt");
		
		File clockwiseData = new File("clockwise.txt");

		List<MultiPolygon> randomList = new ArrayList<>();
		
		randomList.add(new MultiPolygon(convex1Data));
		randomList.add(new MultiPolygon(convex2Data));
		
		randomList.add(new MultiPolygon(concave1Data));
		randomList.add(new MultiPolygon(concave2Data));

		
		// mPolygon.printPolygonData();

		

		// ------------------------------------------------------------------------------
		// checking methods of calculations with coordinates
		// testCoordinateMethods();
		// testEdgeMethods();

		//------------------------------------------------------------------------------
		//testing clockwise and counterclockwise detection and fixing
		
		//MultiPolygon clockwisePolygon = new MultiPolygon(clockwiseData);
		//clockwisePolygon.printPolygonData();
		
		// -------------------------------------------------------------------------------------
		// orbiting method
		 
//		Orbiting.generateNFP(new MultiPolygon(multiPolyList.get(3)), new MultiPolygon(multiPolyList.get(0)));
		
		
		long startTime;
		long endTime;

		long duration;  //divide by 1000000 to get milliseconds.
		int scaleOfTime = 1000000;
		List<Long> durations = new ArrayList<>();
		
		int numberOfIterations = 100;
		
//		for(int j = 0; j<11; j++){
			startTime = System.currentTimeMillis();
//			for(int i = 0; i< numberOfIterations; i++){
			int i = 0;
				for(MultiPolygon stat : randomList){
					int j = 0;
					
					for(MultiPolygon orb : randomList){
						j++;
						Orbiting.generateNFP(new MultiPolygon(stat), new MultiPolygon(orb));
						System.out.println("["+i+"]["+j+"]");
					}
					i++;
				}
//				
				Orbiting.generateNFP(new MultiPolygon(rectangle1Data), new MultiPolygon(rectangle1Data));
		
				Orbiting.generateNFP(new MultiPolygon(puzzle1Data), new MultiPolygon(puzzle2Data));
		
				Orbiting.generateNFP(new MultiPolygon(puzzle3Data), new MultiPolygon(block1Data));
		
				Orbiting.generateNFP(new MultiPolygon(sawtooth1Data), new MultiPolygon(sawtooth2Data));
			
//			}
			endTime = System.currentTimeMillis();
			duration = (endTime - startTime);
			
			System.out.println(duration);
			
//		}
		// ------------------------------------------------------------------------------------
		// graphical representation
		 
		 drawTool.launchDrawer(args);
	}
/*
	private static void testCoordinateMethods() {
		Coordinate coord1 = new Coordinate(-2, -2);
		Coordinate coord2 = new Coordinate(4, 3);
		Coordinate coord3 = new Coordinate(5, 7);
		Coordinate coord4 = new Coordinate(-2, -5);

		double dist = coord1.distanceTo(coord2);
		System.out.println(dist);
		double angle = coord2.calculateAngle(coord1, coord3);

		angle += coord3.calculateAngle(coord1, coord2);
		angle += coord1.calculateAngle(coord3, coord2);// sum 180° correct

		Vector vect = new Vector(coord1,0, true);
		

		System.out.println(Math.toDegrees(vect.getVectorAngle()));
		System.out.println(Math.toDegrees(angle));

		double dVal = coord2.dFunction(coord1, coord3);
		System.out.println(dVal);
	}

	private static void testEdgeMethods() {
		Coordinate coord1 = new Coordinate(-2, -2);
		Coordinate coord2 = new Coordinate(4, 3);
		Coordinate coord3 = new Coordinate(5, 7);
		Coordinate coord4 = new Coordinate(-2, -5);

		Edge edge1 = new Edge(coord1, coord2, 0);
		Edge edge2 = new Edge(coord3, coord4, 5);

		System.out.println(edge1.boundingBoxIntersect(edge2));
		System.out.println(edge1.lineIntersect(edge2));
		System.out.println(edge1.calcIntersection(edge2).toString());
	}
*/
}
