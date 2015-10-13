import java.io.File;
import java.io.FileNotFoundException;

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
		File convex2Data2 = new File("Convex2.txt");

		File concave1Data = new File("Concave1.txt");
		File concave2Data2 = new File("Concave2.txt");

		MultiPolygon mPolygonConvex1 = new MultiPolygon(convex1Data);
		MultiPolygon mPolygonConvex2 = new MultiPolygon(convex2Data2);
		
		MultiPolygon mPolygonConcave1 = new MultiPolygon(concave1Data);
		MultiPolygon mPolygonConcave2 = new MultiPolygon(concave2Data2);

		// mPolygon.printPolygonData();

//		MultiPolygon[] mPolygons = new MultiPolygon[2];
//		mPolygons[0] = mPolygon;
//		mPolygons[1] = mPolygon2;

		// ------------------------------------------------------------------------------
		// checking methods of calculations with coordinates
		// testCoordinateMethods();
		// testEdgeMethods();

		// -------------------------------------------------------------------------------------
		// orbiting method
		 Orbiting.generateNFP(mPolygonConcave1, mPolygonConvex1);

		// ------------------------------------------------------------------------------------
		// graphical representation
		 
		 drawTool.launchDrawer(args);
	}

	private static void testCoordinateMethods() {
		Coordinate coord1 = new Coordinate(-2, -2);
		Coordinate coord2 = new Coordinate(4, 3);
		Coordinate coord3 = new Coordinate(5, 7);
		Coordinate coord4 = new Coordinate(-2, -5);

		double dist = coord1.distanceTo(coord2);
		System.out.println(dist);
		double angle = coord2.calculateAngle(coord1, coord3);

		angle += coord3.calculateAngle(coord1, coord2);
		angle += coord1.calculateAngle(coord3, coord2);// sum 180� correct

		coord1.calculateVectorAngle();

		System.out.println(Math.toDegrees(coord1.getVectorAngle()));
		System.out.println(Math.toDegrees(angle));

		double dVal = coord2.dFunction(coord1, coord3);
		System.out.println(dVal);
	}

	private static void testEdgeMethods() {
		Coordinate coord1 = new Coordinate(-2, -2);
		Coordinate coord2 = new Coordinate(4, 3);
		Coordinate coord3 = new Coordinate(5, 7);
		Coordinate coord4 = new Coordinate(-2, -5);

		Edge edge1 = new Edge(coord1, coord2);
		Edge edge2 = new Edge(coord3, coord4);

		System.out.println(edge1.boundingBoxIntersect(edge2));
		System.out.println(edge1.lineIntersect(edge2));
		System.out.println(edge1.calcIntersection(edge2).toString());
	}

}
