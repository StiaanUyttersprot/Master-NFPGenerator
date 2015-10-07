
/**
 *
 * @author Stiaan
 */
public class Edge {
    private boolean stationary = true;
    private Coordinate startPoint;
    private Coordinate endPoint;
    
    Edge(Coordinate s, Coordinate e, boolean stat){
        startPoint = s;
        endPoint = e;
        stationary = stat;
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
	
	public void print(){
		System.out.println(startPoint.toString() + ";" + endPoint.toString());
	}

	public TouchingEdgePair touching(Edge orbEdge) {
		
		if(startPoint.dFunction(orbEdge.getStartPoint(), orbEdge.getEndPoint())==0){
			//TODO: the point has to lie between the lowest and highest x and y coordinates of the edge to be sure it's touching the edge
				if(orbEdge.contains(startPoint)){
					TouchingEdgePair tEP = new TouchingEdgePair(this, orbEdge, startPoint);
					return tEP;
				}
		}
		if(endPoint.dFunction(orbEdge.getStartPoint(), orbEdge.getEndPoint())==0){
			if(orbEdge.contains(endPoint)){
				TouchingEdgePair tEP = new TouchingEdgePair(this, orbEdge, endPoint);
				return tEP;
			}
		}
		if(orbEdge.getStartPoint().dFunction(startPoint, endPoint)==0){
			if(contains(orbEdge.getStartPoint())){
				TouchingEdgePair tEP = new TouchingEdgePair(this, orbEdge, orbEdge.getStartPoint());
				return tEP;
			}
		}
		if(orbEdge.getEndPoint().dFunction(startPoint, endPoint)==0){
			if(contains(orbEdge.getEndPoint())){
				TouchingEdgePair tEP = new TouchingEdgePair(this, orbEdge, orbEdge.getEndPoint());
				return tEP;
			}
		}
		
		return null;
	}

	private boolean contains(Coordinate coord) {
		//TODO: kijken of het sneller is eerst de waarde van x op te slaan in een int, of gewoon altijd in coord er naar te callen
		boolean containsX = false;
		boolean containsY = false;
		//check x coordinate-----------------------------------------------------------------------------------------------------
		if(startPoint.getxCoord()<endPoint.getxCoord()){
			if(startPoint.getxCoord()<=coord.getxCoord()&&
					endPoint.getxCoord()>=coord.getxCoord())containsX =  true;
		}
		else if(startPoint.getxCoord()>=coord.getxCoord()&&
				endPoint.getxCoord()<=coord.getxCoord())containsX =  true;
		
		//check y-coordinate-----------------------------------------------------------------------------------------------------
		if(startPoint.getyCoord()<endPoint.getyCoord()){
			if(startPoint.getyCoord()<=coord.getyCoord()&&
					endPoint.getyCoord()>=coord.getyCoord())containsY =  true;
		}
		else if(startPoint.getyCoord()>=coord.getyCoord()&&
				endPoint.getyCoord()<=coord.getyCoord())containsY =  true;
		
		return containsX && containsY;
	}

	//if the vector will be created from the whole edge
	public Coordinate makeFullVector() {

		Coordinate vector;
		//if the orbiting edge is being used for the vector, it needs to be inversed
		// this means startPoint-endPoint in stead of endPoint-startPoint
		if(!stationary)vector = startPoint.substract(endPoint);
		else{
			vector = endPoint.substract(startPoint);
		}
		return vector;
		
	}
	
	public Coordinate makePartialVector(Coordinate touchPoint){
		Coordinate vector;
		//if the orbiting edge is being used for the vector, it needs to be inversed
		// this means startPoint-endPoint in stead of endPoint-startPoint
		if(!stationary)vector = touchPoint.substract(endPoint);
		else{
			vector = endPoint.substract(touchPoint);
		}
		return vector;
	}

	public double getAngle(){
		//we can't use the method makeFullVector, this will reverse the vector if it's from the orbiting polygon
		Coordinate vector = endPoint.substract(startPoint);
		
		return vector.calculateVectorAngle();
	}
}
