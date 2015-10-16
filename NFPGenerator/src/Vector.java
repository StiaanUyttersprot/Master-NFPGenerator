/**
 *
 * @author Stiaan
 * 
 *         this class is used for storing points of a polygon
 */
public class Vector {
	private double xCoord;
	private double yCoord;
	private double vectorAngle;
	//the number of the edge that the vector slides over
	private int edgeNumber;

	Vector(double x, double y) {
		xCoord = x;
		yCoord = y;
	}

	public Vector(Vector vect) {
		xCoord = vect.getxCoord();
		yCoord = vect.getyCoord();
		edgeNumber = vect.getEdgeNumber();
		calculateVectorAngle();
	}

	public Vector(Coordinate coord, int eN) {
		xCoord = coord.getxCoord();
		yCoord = coord.getyCoord();
		calculateVectorAngle();
		edgeNumber = eN;
	}

	public double getxCoord() {
		return xCoord;
	}

	public void setxCoord(double xCoord) {
		this.xCoord = xCoord;
	}

	public double getyCoord() {
		return yCoord;
	}

	public void setyCoord(double yCoord) {
		this.yCoord = yCoord;
	}

	public double getVectorAngle() {
		return vectorAngle;
	}

	public void setVectorAngle(double vectorAngle) {
		this.vectorAngle = vectorAngle;
	}

	public int getEdgeNumber() {
		return edgeNumber;
	}

	public void setEdgeNumber(int edgeNumber) {
		this.edgeNumber = edgeNumber;
	}

	public void printCoordinate() {
		System.out.println("( " + xCoord + " , " + yCoord + " ) ");
	}
	
	public void printVector() {
		System.out.println("( " + xCoord + " , " + yCoord + " ) " + " " + Math.toDegrees(vectorAngle) + " EdgeNumber: " + edgeNumber);
	}

	public String toString() {
		return "( " + xCoord + " , " + yCoord + " ) EdgeNumber: " + edgeNumber;
	}

	

	// double distanceTo(Coordinate vect){
	//
	// double distance =
	// Math.sqrt((xCoord-vect.getxCoord())*(xCoord-vect.getxCoord())
	// + (yCoord-vect.getyCoord())*(yCoord-vect.getyCoord()));
	//
	// return distance;
	// }

	

	public double distanceTo(Vector vect) {
		double dX = xCoord - vect.getxCoord();
		double dY = yCoord - vect.getyCoord();
		double distance = Math.sqrt(dX * dX + dY * dY);
		return distance;
	}

	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + edgeNumber;
		long temp;
		temp = Double.doubleToLongBits(vectorAngle);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(xCoord);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(yCoord);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vector other = (Vector) obj;
		if (edgeNumber != other.edgeNumber)
			return false;
		if (Double.doubleToLongBits(vectorAngle) != Double.doubleToLongBits(other.vectorAngle))
			return false;
		if (Double.doubleToLongBits(xCoord) != Double.doubleToLongBits(other.xCoord))
			return false;
		if (Double.doubleToLongBits(yCoord) != Double.doubleToLongBits(other.yCoord))
			return false;
		return true;
	}
	
	public boolean equals(Vector vect) {
		if(xCoord != vect.getxCoord()) return false;
		if(yCoord != vect.getyCoord()) return false;
		return true;
	}

	// D-function is used to calculate where a point is located in reference to
	// a vector
	// if the value is larger then 0 the point is on the left
	// Dabp = ((Xa-Xb)*(Ya-Yp)-(Ya-Yb)*(Xa-Xp))
	public double dFunction(Vector startPoint, Vector endPoint) {

		double dValue = (startPoint.getxCoord() - endPoint.getxCoord()) * (startPoint.getyCoord() - yCoord)
				- (startPoint.getyCoord() - endPoint.getyCoord()) * (startPoint.getxCoord() - xCoord);

		return dValue;
	}
	
	//check if the value is zero or not (trying to cope with very small deviation values)
	public boolean dFunctionCheck(Vector startPoint, Vector endPoint) {
		boolean touching = false;
		double dValue = (startPoint.getxCoord() - endPoint.getxCoord()) * (startPoint.getyCoord() - yCoord)
				- (startPoint.getyCoord() - endPoint.getyCoord()) * (startPoint.getxCoord() - xCoord);
		if(dValue < 1e-4 && dValue > -1e-4)touching = true;
		return touching;
	}

	public void move(double x, double y) {
		xCoord += x;
		yCoord += y;
	}

	//check if two vectinates are equal (use round to make sure mistakes by rounding in the calculations are ignored
	public boolean equalValuesRounded(Vector vect) {

		if (Math.round(xCoord*10000)/10000 != Math.round(vect.getxCoord()*10000)/10000)
			return false;
		if (Math.round(yCoord*10000)/10000 != Math.round(vect.getyCoord()*10000)/10000)
			return false;
		return true;
	}

	// this vectinate minus the given vectinate
	public Vector subtract(Vector point) {

		return new Vector(xCoord - point.getxCoord(), yCoord - point.getyCoord());
	}

	public Vector add(Vector point) {

		return new Vector(xCoord + point.getxCoord(), yCoord + point.getyCoord());
	}

	public boolean isBiggerThen(Vector biggestCoord) {

		return false;
	}

	private void calculateVectorAngle() {

		vectorAngle = Math.atan2(yCoord, xCoord);

	}

	public Vector reflect() {
		xCoord = 0-xCoord;
		yCoord = 0-yCoord;
		return this;
	}

	public void trimTo(Coordinate intersectionCoord, Coordinate startPoint) {
		xCoord = intersectionCoord.getxCoord()-startPoint.getxCoord();
		yCoord = intersectionCoord.getyCoord()-startPoint.getyCoord();
	}

	public double getLengthSquared() {
		
		return xCoord*xCoord + yCoord*yCoord;
	}

//	public boolean dFunctionTouchCheck(Vector startPoint, Vector endPoint) {
//		// TODO Auto-generated method stub
//		return false;
//	}
}
