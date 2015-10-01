
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

	public TouchingEdgePair touching(Edge orbEdge) {
		
		if(startPoint.dFunction(orbEdge.getStartPoint(), orbEdge.getEndPoint())==0){
			TouchingEdgePair tEP = new TouchingEdgePair(this, orbEdge, startPoint);
			return tEP;
		}
		if(endPoint.dFunction(orbEdge.getStartPoint(), orbEdge.getEndPoint())==0){
			TouchingEdgePair tEP = new TouchingEdgePair(this, orbEdge, endPoint);
			return tEP;
		}
		if(orbEdge.getStartPoint().dFunction(startPoint, endPoint)==0){
			TouchingEdgePair tEP = new TouchingEdgePair(this, orbEdge, orbEdge.getStartPoint());
			return tEP;
		}
		if(orbEdge.getEndPoint().dFunction(startPoint, endPoint)==0){
			TouchingEdgePair tEP = new TouchingEdgePair(this, orbEdge, orbEdge.getEndPoint());
			return tEP;
		}
		
		return null;
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

}
