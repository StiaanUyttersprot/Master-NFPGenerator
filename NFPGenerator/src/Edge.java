
/**
 *
 * @author Stiaan
 */
public class Edge {
	private boolean stationary = false;
	private Coordinate startPoint;
	private Coordinate endPoint;
	private int edgeNumber;

	// values to be used for bounding box intersection
	private double smallX;
	private double bigX;
	private double smallY;
	private double bigY;

	Edge(Coordinate s, Coordinate e, int eN) {
		startPoint = s;
		endPoint = e;
		edgeNumber = eN;
		calculateRanges();
	}

	public Edge(Edge edge) {
		startPoint = new Coordinate(edge.getStartPoint());
		endPoint = new Coordinate(edge.getEndPoint());
		edgeNumber = edge.getEdgeNumber();
		calculateRanges();
	}

	public boolean isStationary() {
		return stationary;
	}

	public void setStationary(boolean stationary) {
		this.stationary = stationary;
	}

	public Coordinate getStartPoint() {
		return startPoint;
	}

	public void setStartPoint(Coordinate startPoint) {
		this.startPoint = startPoint;
	}

	public Coordinate getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(Coordinate endPoint) {
		this.endPoint = endPoint;
	}

	public double getSmallX() {
		return smallX;
	}

	public void setSmallX(double smallX) {
		this.smallX = smallX;
	}

	public double getBigX() {
		return bigX;
	}

	public void setBigX(double bigX) {
		this.bigX = bigX;
	}

	public double getSmallY() {
		return smallY;
	}

	public void setSmallY(double smallY) {
		this.smallY = smallY;
	}

	public double getBigY() {
		return bigY;
	}

	public void setBigY(double bigY) {
		this.bigY = bigY;
	}

	public int getEdgeNumber() {
		return edgeNumber;
	}

	public void setEdgeNumber(int edgeNumber) {
		this.edgeNumber = edgeNumber;
	}

	@Override
	public String toString() {
		return "Edge [ startPoint=" + startPoint + ", endPoint=" + endPoint + ", smallX="
				+ smallX + ", bigX=" + bigX + ", smallY=" + smallY + ", bigY=" + bigY + "]";
	}

	public void print() {
		System.out.println(startPoint.toString() + ";" + endPoint.toString());
	}

	private void calculateRanges() {

		Coordinate start = getStartPoint();
		Coordinate end = getEndPoint();

		if (start.getxCoord() < end.getxCoord()) {
			smallX = start.getxCoord();
			bigX = end.getxCoord();
		} else {
			smallX = end.getxCoord();
			bigX = start.getxCoord();
		}

		if (start.getyCoord() < end.getyCoord()) {
			smallY = start.getyCoord();
			bigY = end.getyCoord();
		} else {
			smallY = end.getyCoord();
			bigY = start.getyCoord();
		}
	}

	public TouchingEdgePair touching(Edge orbEdge) {

		if (startPoint.dFunctionCheck(orbEdge.getStartPoint(), orbEdge.getEndPoint())) {
			if (orbEdge.contains(startPoint)) {
				TouchingEdgePair tEP = new TouchingEdgePair(this, orbEdge, startPoint);
				return tEP;
			}
		}
		if (endPoint.dFunctionCheck(orbEdge.getStartPoint(), orbEdge.getEndPoint())) {
			if (orbEdge.contains(endPoint)) {
				TouchingEdgePair tEP = new TouchingEdgePair(this, orbEdge, endPoint);
				return tEP;
			}
		}
		if (orbEdge.getStartPoint().dFunctionCheck(startPoint, endPoint)) {
			if (contains(orbEdge.getStartPoint())) {
				TouchingEdgePair tEP = new TouchingEdgePair(this, orbEdge, orbEdge.getStartPoint());
				return tEP;
			}
		}
		if (orbEdge.getEndPoint().dFunctionCheck(startPoint, endPoint)) {
			if (contains(orbEdge.getEndPoint())) {
				TouchingEdgePair tEP = new TouchingEdgePair(this, orbEdge, orbEdge.getEndPoint());
				return tEP;
			}
		}

		return null;
	}
	
	
	
	public TouchingEdgePair touchingV2(Edge orbEdge){
		//first look if the start or end points are equal, if this is the case they are certainly touching
		if (startPoint.equals(orbEdge.startPoint)){
			TouchingEdgePair tEP = new TouchingEdgePair(this, orbEdge, startPoint);
			return tEP;
		}
		if (startPoint.equals(orbEdge.endPoint)){
			TouchingEdgePair tEP = new TouchingEdgePair(this, orbEdge, startPoint);
			return tEP;
		}
		if (endPoint.equals(orbEdge.startPoint)){
			TouchingEdgePair tEP = new TouchingEdgePair(this, orbEdge, endPoint);
			return tEP;
		}
		if (endPoint.equals(orbEdge.endPoint)){
			TouchingEdgePair tEP = new TouchingEdgePair(this, orbEdge, endPoint);
			return tEP;
		}
		//after checking those points, check if it is somewhere in between start and end
		
		if (startPoint.dFunction(orbEdge.getStartPoint(), orbEdge.getEndPoint()) == 0) {
			if (orbEdge.contains(startPoint)) {
				TouchingEdgePair tEP = new TouchingEdgePair(this, orbEdge, startPoint);
				return tEP;
			}
		}
		
		if (endPoint.dFunction(orbEdge.getStartPoint(), orbEdge.getEndPoint()) == 0) {
			if (orbEdge.contains(endPoint)) {
				TouchingEdgePair tEP = new TouchingEdgePair(this, orbEdge, endPoint);
				return tEP;
			}
		}
		
		if (orbEdge.getStartPoint().dFunction(startPoint, endPoint) == 0) {
			if (contains(orbEdge.getStartPoint())) {
				TouchingEdgePair tEP = new TouchingEdgePair(this, orbEdge, orbEdge.getStartPoint());
				return tEP;
			}
		}
		
		if (orbEdge.getEndPoint().dFunction(startPoint, endPoint) == 0) {
			if (contains(orbEdge.getEndPoint())) {
				TouchingEdgePair tEP = new TouchingEdgePair(this, orbEdge, orbEdge.getEndPoint());
				return tEP;
			}
		}

		return null;
	}

	private boolean contains(Coordinate coord) {
		// TODO: kijken of het sneller is eerst de waarde van x op te slaan in
		// een int, of gewoon altijd in coord er naar te callen
		boolean containsX = false;
		boolean containsY = false;
		// check x
		// coordinate-----------------------------------------------------------------------------------------------------
		if (startPoint.getxCoord() < endPoint.getxCoord()) {
			if (startPoint.getxCoord() <= coord.getxCoord() && endPoint.getxCoord() >= coord.getxCoord())
				containsX = true;
		} else if (startPoint.getxCoord() >= coord.getxCoord() && endPoint.getxCoord() <= coord.getxCoord())
			containsX = true;

		// check
		// y-coordinate-----------------------------------------------------------------------------------------------------
		if (startPoint.getyCoord() < endPoint.getyCoord()) {
			if (startPoint.getyCoord() <= coord.getyCoord() && endPoint.getyCoord() >= coord.getyCoord())
				containsY = true;
		} else if (startPoint.getyCoord() >= coord.getyCoord() && endPoint.getyCoord() <= coord.getyCoord())
			containsY = true;

		return containsX && containsY;
	}

	// if the vector will be created from the whole edge
	public Vector makeFullVector(int eN) {

		Vector vector;
		// if the orbiting edge is being used for the vector, it needs to be
		// inversed
		// this means startPoint-endPoint in stead of endPoint-startPoint
		if (!stationary)
			vector = new Vector(startPoint.subtract(endPoint), eN);
		else {
			vector = new Vector(endPoint.subtract(startPoint), eN);
		}
		
		return vector;

	}

	public Vector makePartialVector(Coordinate touchPoint, int eN) {
		Vector vector;
		// if the orbiting edge is being used for the vector, it needs to be
		// inversed
		// this means startPoint-endPoint in stead of endPoint-startPoint
		if (!stationary)
			//TODO:the edgenumber from the orbiting edge may be wrong and cause errors
			vector = new Vector(touchPoint.subtract(endPoint), eN);
		else {
			vector = new Vector(endPoint.subtract(touchPoint), eN);
		}

		return vector;
	}

	public double getAngle() {
		// we can't use the method makeFullVector, this will reverse the vector
		// if it's from the orbiting polygon
		Vector vector = new Vector(endPoint.subtract(startPoint), edgeNumber);
		
		return vector.getVectorAngle();
	}

	public boolean boundingBoxIntersect(Edge edge) {

		boolean intersect = true;

		if (edge.getBigX() <= smallX-1e-4 || edge.getSmallX() >= bigX+1e-4 || edge.getBigY() <= smallY-1e-4
				|| edge.getSmallY() >= bigY+1e-4)
			intersect = false;

		return intersect;
	}

	public boolean lineIntersect(Edge testEdge) {
		boolean intersect = true;
		// the lines intersect if the start coordinate and the end coordinate
		// of one of the edges are not both on the same side

		if (testEdge.getStartPoint().dFunction(startPoint, endPoint) <= 1e-4
				&& testEdge.getEndPoint().dFunction(startPoint, endPoint) <= 1e-4) {
			intersect = false;
		} else if (testEdge.getStartPoint().dFunction(startPoint, endPoint) >= -1e-4
				&& testEdge.getEndPoint().dFunction(startPoint, endPoint) >= -1e-4) {
			intersect = false;
		}

		return intersect;
	}

	public Coordinate calcIntersection(Edge testEdge) {
		/*
		 * the used formula is
		 * x=((x1*y2-y1*x2)*(x3-x4)-(x1-x2)*(x3*y4-y3*x4))/((x1-x2)*(y3-y4)-(y1-
		 * y2)*(x3-x4));
		 * y=((x1*y2-y1*x2)*(y3-y4)-(y1-y2)*(x3*y4-y3*x4))/((x1-x2)*(y3-y4)-(y1-
		 * y2)*(x3-x4));
		 */

		double x1 = startPoint.getxCoord();
		double x2 = endPoint.getxCoord();
		double y1 = startPoint.getyCoord();
		double y2 = endPoint.getyCoord();

		double x3 = testEdge.getStartPoint().getxCoord();
		double x4 = testEdge.getEndPoint().getxCoord();
		double y3 = testEdge.getStartPoint().getyCoord();
		double y4 = testEdge.getEndPoint().getyCoord();

		// x1 - x2
		double dx1 = x1 - x2;
		// x3 - x4
		double dx2 = x3 - x4;
		// y1 - y2
		double dy1 = y1 - y2;
		// y3 - y4
		double dy2 = y3 - y4;

		// (x1*y2-y1*x2)
		double pd1 = x1 * y2 - y1 * x2;
		// (x3*y4-y3*x4)
		double pd2 = x3 * y4 - y3 * x4;

		// (x1*y2-y1*x2)*(x3-x4)-(x1-x2)*(x3*y4-y3*x4)
		double xNumerator = pd1 * dx2 - dx1 * pd2;
		// (x1*y2-y1*x2)*(y3-y4)-(y1-y2)*(x3*y4-y3*x4)
		double yNumerator = pd1 * dy2 - dy1 * pd2;

		// (x1-x2)*(y3-y4)-(y1-y2)*(x3-x4)
		double denominator = dx1 * dy2 - dy1 * dx2;

		double xCoord = xNumerator / denominator;
		double yCoord = yNumerator / denominator;

		return new Coordinate(xCoord, yCoord);
	}

	//when a translation is taking place, the values of min and max have to be adjusted
	public void changeRangeValues(double x, double y) {
		smallX += x;
		bigX += x;
		smallY += y;
		bigY += y;
		
	}

}
