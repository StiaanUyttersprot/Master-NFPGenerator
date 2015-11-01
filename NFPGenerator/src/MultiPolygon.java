
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javafx.scene.shape.Polygon;

/**
 *
 * @author Stiaan
 * 
 *         this class contains a polygon to be used to generate the no-fit
 *         polygon the polygon exists of coordinates and can have holes
 */
public class MultiPolygon {

	private int nHoles; // the number of holes

	private Coordinate[] outerPolygon; // the polygon that envelops the holes
	private Edge[] outerPolygonEdges;

	private Coordinate[][] holes;
	private Edge[][] holeEdges;

	private Scanner input;

	private double biggestX = 0;
	private double biggestY = 0;
	private double smallestX = Double.MAX_VALUE;
	private double smallestY = Double.MAX_VALUE;
	// constructor reads file to create a polygon
	MultiPolygon(File file) throws FileNotFoundException {

		// TODO: catch wrong input file format error

		input = new Scanner(file);
		nHoles = input.nextInt();
		int nPoints = input.nextInt();// number of points of the polygon that is
										// currently being read (this value
										// changes when holes are read too)

		// used for autoscaling in drawtool
		double readX;
		double readY;

		outerPolygon = new Coordinate[nPoints];
		holes = new Coordinate[nHoles][];

		for (int i = 0; i < nPoints; i++) {

			readX = input.nextDouble();
			readY = input.nextDouble();
			outerPolygon[i] = new Coordinate(readX, readY);
			
			//-----------------------------------------------------
			//values used for scaling in gui
			if (readX > biggestX)
				biggestX = readX;
			else if(readX < smallestX)smallestX = readX;
			if (readY > biggestY)
				biggestY = readY;
			else if(readY < smallestY)smallestY = readY;
			
			
			//-----------------------------------------------------
			
		}
		
		if(checkClockwise(outerPolygon)){
			
			changeClockOrientation(outerPolygon);

		}

		for (int i = 0; i < nHoles; i++) {

			nPoints = input.nextInt();
			holes[i] = new Coordinate[nPoints];

			for (int j = 0; j < nPoints; j++) {
				holes[i][j] = new Coordinate(input.nextDouble(), input.nextDouble());
			}
			
			if(!checkClockwise(holes[i])){
				changeClockOrientation(holes[i]);
			}
		}
		input.close();

		// now we will make the edge arrays
		// this array contains the same information but in pairs of coordinates
		// to allow an easier way to use edges
		createEdges();

	}

	MultiPolygon(MultiPolygon mp){
		nHoles = mp.getnHoles();

		// used for autoscaling in drawtool
		double readX;
		double readY;

		outerPolygon = new Coordinate[mp.getOuterPolygon().length];
		holes = new Coordinate[nHoles][];
		
		for (int i = 0; i < mp.getOuterPolygon().length ; i++) {
			
			readX = mp.getOuterPolygon()[i].getxCoord();
			readY = mp.getOuterPolygon()[i].getyCoord();
			outerPolygon[i] = new Coordinate(mp.getOuterPolygon()[i]);
			
			biggestX = mp.getBiggestX();
			smallestX = mp.getSmallestX();
			biggestY = mp.getBiggestY();
			smallestY = mp.getSmallestY();

		}

		for (int i = 0; i < nHoles; i++) {

			holes[i] = new Coordinate[mp.getHoles()[i].length];
			int j = 0;
			for (Coordinate coord : mp.getHoles()[i]) {
				holes[i][j] = new Coordinate(coord);
				j++;
			}
		}

		// now we will make the edge arrays
		// this array contains the same information but in pairs of coordinates
		// to allow an easier way to use edges
		createEdges();
	}
	
	private void createEdges() {
		outerPolygonEdges = new Edge[outerPolygon.length];
		holeEdges = new Edge[nHoles][];

		for (int i = 0; i < outerPolygon.length; i++) {
			if (i == outerPolygon.length - 1) {
				outerPolygonEdges[i] = new Edge(outerPolygon[i], outerPolygon[0], i);
			} else {
				outerPolygonEdges[i] = new Edge(outerPolygon[i], outerPolygon[i + 1], i);
			}
		}

		for (int i = 0; i < nHoles; i++) {
			holeEdges[i] = new Edge[holes[i].length];
			for (int j = 0; j < holes[i].length; j++) {

				if (j == holes[i].length - 1) {
					holeEdges[i][j] = new Edge(holes[i][j], holes[i][0], i);
				} else {
					holeEdges[i][j] = new Edge(holes[i][j], holes[i][j + 1], i);
				}

			}
		}
	}

	public double getSmallestX() {
		return smallestX;
	}

	public void setSmallestX(double smallestX) {
		this.smallestX = smallestX;
	}

	public double getSmallestY() {
		return smallestY;
	}

	public void setSmallestY(double smallestY) {
		this.smallestY = smallestY;
	}

	public double getBiggestX() {
		return biggestX;
	}

	public void setBiggestX(double biggestX) {
		this.biggestX = biggestX;
	}

	public double getBiggestY() {
		return biggestY;
	}

	public void setBiggestY(double biggestY) {
		this.biggestY = biggestY;
	}

	public Coordinate[] getOuterPolygon() {
		return outerPolygon;
	}

	public void setOuterPolygon(Coordinate[] outerPolygon) {
		this.outerPolygon = outerPolygon;
	}

	public Coordinate[][] getHoles() {
		return holes;
	}

	public void setHoles(Coordinate[][] holes) {
		this.holes = holes;
	}

	public int getnHoles() {
		return nHoles;
	}

	public void setnHoles(int nHoles) {
		this.nHoles = nHoles;
	}

	public Edge[] getOuterPolygonEdges() {
		return outerPolygonEdges;
	}

	public void setOuterPolygonEdges(Edge[] outerPolygonEdges) {
		this.outerPolygonEdges = outerPolygonEdges;
	}

	public Edge[][] getHoleEdges() {
		return holeEdges;
	}

	public void setHoleEdges(Edge[][] holeEdges) {
		this.holeEdges = holeEdges;
	}

	// methods for
	// GUI----------------------------------------------------------------------------------------------------------------
	// converting outer polygon into polygon for UI
	public Polygon makeOuterPolygon(double xSize, double ySize, double sizeFactor) {

		Polygon polygon = new Polygon();
		for (Coordinate coord : outerPolygon) {
			// add 300 to coord to move axis
			polygon.getPoints().add(sizeFactor * coord.getxCoord() + xSize / 2);
			// yCoord*-1 to invert to normal axis and add 700 to move axis
			polygon.getPoints().add(-1 * sizeFactor * coord.getyCoord() + ySize / 2);
		}

		return polygon;
	}

	// converting holes into polygons for UI
	public Polygon[] makeHoles(double xSize, double ySize, double sizeFactor) {
		Polygon[] polyHoles = new Polygon[nHoles];
		for (int i = 0; i < nHoles; i++) {
			polyHoles[i] = new Polygon();
			for (Coordinate coord : holes[i]) {
				polyHoles[i].getPoints().add(sizeFactor * coord.getxCoord() + xSize / 2);
				// yCoord*-1 to invert to normal axis
				polyHoles[i].getPoints().add(-1 * sizeFactor * coord.getyCoord() + ySize / 2);
			}
		}
		return polyHoles;
	}

	public void translate(double x, double y) {

		for (Coordinate coord : outerPolygon) {
			coord.move(x, y);
		}

		for (Coordinate[] hole : holes) {
			for (Coordinate coord : hole) {
				coord.move(x, y);
			}
		}
		//for the edges the new minimum and maximum values need to be recalculated
		for (Edge edge : outerPolygonEdges){
			edge.changeRangeValues(x, y);
		}
		for (Edge[] edgeList : holeEdges){
			for(Edge edge : edgeList){
				edge.changeRangeValues(x, y);
			}
		}
	}
	
	//translate with vector
	public void translate(Vector vect) {
		double x = vect.getxCoord();
		double y = vect.getyCoord();
		
		for (Coordinate coord : outerPolygon) {
			coord.move(x, y);
		}

		for (Coordinate[] hole : holes) {
			for (Coordinate coord : hole) {
				coord.move(x, y);
			}
		}
		
		//for the edges the new minimum and maximum values need to be recalculated
		for (Edge edge : outerPolygonEdges){
			edge.changeRangeValues(x, y);
		}
		for (Edge[] edgeList : holeEdges){
			for(Edge edge : edgeList){
				edge.changeRangeValues(x, y);
			}
		}
	}
	// ---------------------------------------------------------------------------------------------------------------------------------------

	// print out the data of a polygon
	public void printPolygonData() {
		System.out.println("outer polygon number of points: " + outerPolygon.length);
		for (Coordinate coord : outerPolygon) {
			coord.printCoordinate();
		}
		for (int i = 0; i < nHoles; i++) {
			System.out.println("hole " + (i + 1) + " number of points: " + holes[i].length);
			for (Coordinate holeCoord : holes[i]) {
				holeCoord.printCoordinate();
			}
		}
	}

	public Coordinate findBottomCoord() {
		Coordinate bottomCoord = outerPolygon[0];
		for (Coordinate coord : outerPolygon) {
			// if the y-value of coord is lower then the current bottomCoord,
			// replace bottomCoord
			if (coord.getyCoord() < bottomCoord.getyCoord())
				bottomCoord = coord;
		}
		return bottomCoord;
	}

	public Coordinate findTopCoord() {
		Coordinate topCoord = outerPolygon[0];
		for (Coordinate coord : outerPolygon) {
			// if the y-value of coord is higher then the current topCoord,
			// replace topCoord
			if (coord.getyCoord() > topCoord.getyCoord())
				topCoord = coord;
		}
		return topCoord;
	}

	public List<TouchingEdgePair> findTouchingEdges(MultiPolygon orbPoly) {

		List<TouchingEdgePair> touchingEdges = new ArrayList<>();
		TouchingEdgePair tEP;
		// the outer polygon of the orbiting multipolygon
		Edge[] orbOuterPolygonEdges = orbPoly.getOuterPolygonEdges();
		// check for every point of orb if it touches an edge of stat
		for (Edge orbEdge : orbOuterPolygonEdges) {

			for (Edge statEdge : outerPolygonEdges) {

				tEP = statEdge.touching(orbEdge);
				if (tEP != null){
					touchingEdges.add(tEP);
					statEdge.markTraversed();
					orbEdge.markTraversed();
				}	
			}
		}
		return touchingEdges;
	}

	public void isStationary() {
		for (Edge e : outerPolygonEdges) {
			e.setStationary(true);
		}
		for (Edge[] eA : holeEdges) {
			for (Edge e : eA) {
				e.setStationary(true);
			}
		}
	}
	//the next method returns true if the polygon is clockwise
	private boolean checkClockwise(Coordinate[] polygon) {
		double clockwiseValue = 0;
		
		double xDiff;
		double ySum;
		
		//If the result is positive the curve is clockwise, if it's negative the curve is counter-clockwise.
		for(int i = 0; i < polygon.length; i++){
			if(i < polygon.length-1){
				//Sum over the edges, (x2-x1)(y2+y1). If the result is positive the curve is clockwise, if it's negative the curve is counter-clockwise.
				xDiff = polygon[i+1].getxCoord() - polygon[i].getxCoord();
				ySum = polygon[i+1].getyCoord() + polygon[i].getyCoord();
				clockwiseValue += xDiff*ySum;
				
			}
		}
		
		if(clockwiseValue > 0) return true;
		else return false;
	}
	
	private void changeClockOrientation(Coordinate[] polygon){
		Coordinate[] changedPolygon = new Coordinate[polygon.length];
		changedPolygon[0] = polygon[0];
		for(int i = 1; i < polygon.length; i++){
			changedPolygon[i] = polygon[polygon.length-i];
			System.out.println("placing " + polygon[polygon.length-i].toString() + "to location " + i);
		}
		for(int i = 0; i < polygon.length; i++){
			polygon[i] = changedPolygon[i];
		}
		
	}

	public void printEdges() {
		
		for(Edge e: outerPolygonEdges){
			System.out.println(e.toString());
		}
		System.out.println();
	}

	public boolean allEdgesTraversed() {
		for(Edge e : outerPolygonEdges){
			if(!e.isTraversed())return false;
		}
		
		for(Edge[] hole: holeEdges){
			for(Edge h: hole){
				if(!h.isTraversed())return false;
			}
		}
			
		return true;
	}

	public Edge findUntraversedEdge() {
		//this is true when an untraversed edge is found
		Edge untraversedEdge = null;
		
		int i =0;
		while(untraversedEdge == null && i< outerPolygonEdges.length){
			if(!outerPolygonEdges[i].isTraversed()) untraversedEdge = outerPolygonEdges[i];
			//System.out.println(1);
			i++;
		}
		i = 0;
		int j;
		while(untraversedEdge == null && i < holeEdges.length){
			j = 0;
			//System.out.println(2);
			while(untraversedEdge == null && j < holeEdges[i].length){
				if(!holeEdges[i][j].isTraversed()) untraversedEdge = holeEdges[i][j];
				//System.out.println(3);
				j++;
			}
			i++;
		}
		return untraversedEdge;
	}

	public Coordinate searchStartPoint(Edge possibleStartEdge, MultiPolygon orbPoly) {
		
		Coordinate currentStartPoint = new Coordinate(possibleStartEdge.getStartPoint());
		System.out.println(currentStartPoint);
		//for every point of the orbiting polygon that can be placed at that spot
		int orbPointIndex = 0;
		boolean startPointPossible = false;
		Vector placeOrbPolyVector;
		
		Vector nextPossibleSpotVector;
		
		for(int i=0; i< orbPoly.getOuterPolygon().length; i++){
			Coordinate orbPoint = new Coordinate(orbPoly.getOuterPolygon()[i]);
			orbPoly.printPolygonData();
			//translation to the startpoint from the current orbiting point
			placeOrbPolyVector = new Vector(orbPoint, currentStartPoint);
			
			orbPoly.translate(placeOrbPolyVector);

			//PolygonPairStages.addPolygonPair(this, orbPoly);
			
			if(!polygonsIntersectPointInPolygon(this, orbPoly, orbPoint)){
				System.out.println(currentStartPoint);
				return currentStartPoint;
			}
			else{//test 1, check if it is ever possible
				
				if(orbPointIndex == 0){
					startPointPossible = possibleStartEdge.edgesOrientatedRight(orbPoly.getOuterPolygonEdges()[orbPoly.getOuterPolygonEdges().length-1],
							orbPoly.getOuterPolygonEdges()[orbPointIndex]);
				}
				else{
					startPointPossible = possibleStartEdge.edgesOrientatedRight(orbPoly.getOuterPolygonEdges()[orbPointIndex-1],
							orbPoly.getOuterPolygonEdges()[orbPointIndex]);
				}
			}
			//keep looking for a place where the polygons don't overlap, till the end of the line has been reached
			if(startPointPossible){
				//trim the vector made by the possible startEdge and the current start point
				while(!currentStartPoint.equals(possibleStartEdge.getEndPoint())&&polygonsIntersectPointInPolygon(this, orbPoly, orbPoint)){
					nextPossibleSpotVector = new Vector(currentStartPoint, possibleStartEdge.getEndPoint());
					
					nextPossibleSpotVector.trimFeasibleVector(orbPoly, this, true);
					nextPossibleSpotVector.trimFeasibleVector(this, orbPoly, false);
					
					orbPoly.translate(nextPossibleSpotVector);
					//PolygonPairStages.addPolygonPair(this, orbPoly);
					currentStartPoint.translate(nextPossibleSpotVector);
				}
				if(currentStartPoint.equals(possibleStartEdge.getEndPoint())&&!polygonsIntersectPointInPolygon(this, orbPoly, orbPoint)){
					currentStartPoint.printCoordinate();
					return currentStartPoint;
				}
				else if(!currentStartPoint.equals(possibleStartEdge.getEndPoint())){
					currentStartPoint.printCoordinate();
					PolygonPairStages.addPolygonPair(this, orbPoly);
					return currentStartPoint;
				}
			}
			else System.out.println("startPoint not possible");
			orbPointIndex++;
		}
		return null;
		
	}
	
	public boolean polygonsIntersectEdgeIntersect(MultiPolygon statPoly, MultiPolygon orbPoly, Coordinate orbCoord) {
		
//		Vector placeOrbPolyVector;//the vector used to place the orbiting polygon in its starting position
		
			
//		orbPoly.translate(placeOrbPolyVector);
		//TODO kijken of het niet sneller is met een methode om te kijken of een punt binnen het polygon valt (sowieso)
		//outside check 
		System.out.println("testing next location");
		for(Edge outerOrbEdge: statPoly.getOuterPolygonEdges()){
			for(Edge outerStatEdge : orbPoly.getOuterPolygonEdges()){
				
				if(outerOrbEdge.testIntersect(outerStatEdge)){
					System.out.println("intersection");
					return true;
				}
				if(outerStatEdge.containsIntersectionPoint(outerOrbEdge.getStartPoint())||outerStatEdge.containsIntersectionPoint(outerOrbEdge.getEndPoint())
						||outerOrbEdge.containsIntersectionPoint(outerStatEdge.getStartPoint())||outerOrbEdge.containsIntersectionPoint(outerStatEdge.getEndPoint())){
					Coordinate middlePoint = outerOrbEdge.getMiddlePointEdge();
					System.out.println("testing " + middlePoint);
					if(!outerStatEdge.containsIntersectionPoint(middlePoint)&&statPoly.pointInPolygon(middlePoint)){
						System.out.println("middlepoint in polygon" );
						return true;
					}
				}
			}
		}
		//check orb outer with stat holes
		for(Edge outerOrbEdge: orbPoly.getOuterPolygonEdges()){
			for(Edge[] statHoles : statPoly.getHoleEdges()){
				for(Edge statHoleEdge: statHoles){
					if(outerOrbEdge.testIntersect(statHoleEdge)){
						return true;
					}
				}
				
			}
		}
		//check stat outer with orb holes
		for(Edge outerStatEdge: statPoly.getOuterPolygonEdges()){
			for(Edge[] orbHoles : orbPoly.getHoleEdges()){
				for(Edge orbHoleEdge: orbHoles){
					if(outerStatEdge.testIntersect(orbHoleEdge)){
						return true;
					}
				}
			}
		}
		//check holes
		for(Edge[] orbHoles : orbPoly.getHoleEdges()){
			for(Edge orbHoleEdge: orbHoles){
				for(Edge[] statHoles : statPoly.getHoleEdges()){
					for(Edge statHoleEdge: statHoles){
						if(orbHoleEdge.testIntersect(statHoleEdge)){
							return true;
						}
					}
				}
			}
		}
		return false;

	}
	

	
	public boolean polygonsIntersectPointInPolygon(MultiPolygon statPoly, MultiPolygon orbPoly, Coordinate orbCoord) {
		System.out.println("testing next location");
		boolean isOnEdge;
		boolean middlePointOnEdge;
		int i= 1;
		orbPoly.printPolygonData();
		for(Coordinate coord: orbPoly.getOuterPolygon()){
			System.out.println(coord);
			isOnEdge = false;
			for(Edge statEdge: statPoly.getOuterPolygonEdges()){
				if(statEdge.containsIntersectionPoint(coord))isOnEdge = true;
			}
			//our method for seeing if a point is in the polygon does not give a certain result for points that fall on the edge
			if(statPoly.pointInPolygon(coord)&&!isOnEdge){
				return true;
			}
			if(isOnEdge){
				middlePointOnEdge = false;
				Edge edgeToTest = new Edge(coord, orbPoly.getOuterPolygon()[i]);
				for(Edge statEdge: statPoly.getOuterPolygonEdges()){
					if(statEdge.testIntersect(edgeToTest)) return true;
				}
				
				Coordinate middlePoint = edgeToTest.getMiddlePointEdge();
				System.out.println("calc the midpoint "+ middlePoint);
				for(Edge statEdge: statPoly.getOuterPolygonEdges()){
					if(statEdge.containsIntersectionPoint(middlePoint)) middlePointOnEdge = true;
				}
				if(statPoly.pointInPolygon(middlePoint)&&!middlePointOnEdge)return true;
			}
			i++;
			if(i > orbPoly.getOuterPolygon().length-1)i = 0;
		}
		return false;
	}
	
	
//	http://alienryderflex.com/polygon/

//  The function will return YES if the point x,y is inside the polygon, or
//  NO if it is not.  If the point is exactly on the edge of the polygon,
//  then the function may return YES or NO.
//
//  Note that division by zero is avoided because the division is protected
//  by the "if" clause which surrounds it.

	public boolean pointInPolygon(Coordinate coord) {

		System.out.println("is point in polygon? " + coord);
		int polyCorners = outerPolygon.length;
		int i, j = polyCorners - 1;
		boolean oddNodes = false;

		for (i = 0; i < polyCorners; i++) {
			if ((outerPolygon[i].getyCoord() < coord.getyCoord() && outerPolygon[j].getyCoord() >= coord.getyCoord()
					|| outerPolygon[j].getyCoord() < coord.getyCoord()
							&& outerPolygon[i].getyCoord() >= coord.getyCoord())
					&& (outerPolygon[i].getxCoord() <= coord.getxCoord()
							|| outerPolygon[j].getxCoord() <= coord.getxCoord())) {
				// ^= is bitwise XOR assignement
				oddNodes ^= (outerPolygon[i].getxCoord() + (coord.getyCoord()) - outerPolygon[i].getyCoord())
						/ (outerPolygon[j].getyCoord() - outerPolygon[i].getyCoord())
						* (outerPolygon[j].getxCoord() - outerPolygon[i].getxCoord()) < coord.getxCoord();
			}
			j = i;
		}
		boolean inHole = false;
		//if a hole contains the point it isn't contained by the polygon
		if(nHoles != 0){
			for (Coordinate[] hole : holes){
				polyCorners = hole.length;
				i = polyCorners - 1;
				j = polyCorners - 1;
				for (i = 0; i < polyCorners; i++) {
					if ((hole[i].getyCoord() < coord.getyCoord() && hole[j].getyCoord() >= coord.getyCoord()
							|| hole[j].getyCoord() < coord.getyCoord()
									&& hole[i].getyCoord() >= coord.getyCoord())
							&& (hole[i].getxCoord() <= coord.getxCoord()
									|| hole[j].getxCoord() <= coord.getxCoord())) {
						// ^= is bitwise XOR assignement
						inHole ^= (hole[i].getxCoord() + (coord.getyCoord()) - hole[i].getyCoord())
								/ (hole[j].getyCoord() - hole[i].getyCoord())
								* (hole[j].getxCoord() - hole[i].getxCoord()) < coord.getxCoord();
					}
					j = i;
				}
				if(inHole) return false;
			}
		}

		return oddNodes;
	}
}
