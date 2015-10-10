/**
 *
 * @author Stiaan
 * 
 * this class is used for storing points of a polygon
 */
public class Coordinate {
    private double xCoord;
    private double yCoord;
    private double vectorAngle;
    Coordinate(double x, double y){
        xCoord = x;
        yCoord = y;
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

    public void printCoordinate() {
        System.out.println("( "+xCoord+" , "+yCoord+" ) ");
    }
    
    public String toString(){
    	return "( "+xCoord+" , "+yCoord+" ) ";
    }
    
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		Coordinate other = (Coordinate) obj;
		if (Double.doubleToLongBits(vectorAngle) != Double.doubleToLongBits(other.vectorAngle))
			return false;
		if (Double.doubleToLongBits(xCoord) != Double.doubleToLongBits(other.xCoord))
			return false;
		if (Double.doubleToLongBits(yCoord) != Double.doubleToLongBits(other.yCoord))
			return false;
		return true;
	}
    
//    double distanceTo(Coordinate coord){
//        
//        double distance = Math.sqrt((xCoord-coord.getxCoord())*(xCoord-coord.getxCoord())
//                + (yCoord-coord.getyCoord())*(yCoord-coord.getyCoord()));
//        
//        return distance;
//    }
    
    

	

	public double distanceTo(Coordinate coord){
        double dX = xCoord-coord.getxCoord();
        double dY = yCoord-coord.getyCoord();
        double distance = Math.sqrt(dX*dX+dY*dY);
        return distance;
    }
    
    //calculating the angle: the coordinate that calls the method is the one where the angle needs to be calculated
    public double calculateAngle(Coordinate coord2, Coordinate coord3){
        
        double distA = coord2.distanceTo(coord3);
        //System.out.println(distA);
        double distB = this.distanceTo(coord3);
        //System.out.println(distB);
        double distC = this.distanceTo(coord2);
        //System.out.println(distC);
        
        double cosAngle = (distB*distB + distC*distC - distA*distA)/(2*distB*distC);
        //System.out.println(cosAngle);
        double angle = Math.acos(cosAngle);
        //System.out.println(angle);
        return angle;
    }
    
    //D-function is used to calculate where a point is located in reference to a vector
    //if the value is larger then 0 the point is on the left
    //Dabp = ((Xa-Xb)*(Ya-Yp)-(Ya-Yb)*(Xa-Xp))
    public double dFunction(Coordinate startPoint, Coordinate endPoint){
        
        double dValue = (startPoint.getxCoord()-endPoint.getxCoord())*(startPoint.getyCoord()-yCoord)
                - (startPoint.getyCoord()-endPoint.getyCoord())*(startPoint.getxCoord()-xCoord);
        
        return dValue;
    }
    
    public void move(double x, double y){
        xCoord += x;
        yCoord += y;
    }

	public boolean equals(Coordinate coord) {

		if (xCoord != coord.getxCoord()) 
			return false;
		if (yCoord != coord.getyCoord()) 
			return false;
		return true;
	}
	//this coordinate minus the given coordinate
	public Coordinate substract(Coordinate point) {
		
		return new Coordinate(xCoord-point.getxCoord(), yCoord - point.getyCoord());
	}

	public boolean isBiggerThen(Coordinate biggestCoord) {
		
		
		return false;
	}
    
    public void calculateVectorAngle(){
    	
    	vectorAngle = Math.atan2(yCoord, xCoord);
    	
    }
}

