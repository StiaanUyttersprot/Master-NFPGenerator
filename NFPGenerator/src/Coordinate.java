/**
 *
 * @author Stiaan
 * 
 * this class is used for storing points of a polygon
 */
public class Coordinate {
    private double xCoord;
    private double yCoord;
    
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
    

    void printCoordinate() {
        System.out.println("( "+xCoord+" , "+yCoord+" ) ");
    }
    
//    double distanceTo(Coordinate coord){
//        
//        double distance = Math.sqrt((xCoord-coord.getxCoord())*(xCoord-coord.getxCoord())
//                + (yCoord-coord.getyCoord())*(yCoord-coord.getyCoord()));
//        
//        return distance;
//    }
    
    double distanceTo(Coordinate coord){
        double dX = xCoord-coord.getxCoord();
        double dY = yCoord-coord.getyCoord();
        double distance = Math.sqrt(dX*dX+dY*dY);
        return distance;
    }
    
    //calculating the angle: the coordinate that calls the method is the one where the angle needs to be calculated
    double calculateAngle(Coordinate coord2, Coordinate coord3){
        
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
}

