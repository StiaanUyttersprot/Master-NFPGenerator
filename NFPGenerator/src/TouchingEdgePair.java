
/**
 *
 * @author Stiaan
 * 
 * Pairs of edges that are touching used in the orbiting method are stored 
 * with the coordinates of the touching point
 */
public class TouchingEdgePair {

	private Edge statEdge;
	private Edge orbEdge;
	private Coordinate touchPoint;

	public TouchingEdgePair(Edge statEdge, Edge orbEdge, Coordinate touchPoint) {
		
		this.statEdge = statEdge;
		this.orbEdge = orbEdge;
		this.touchPoint = touchPoint;
	}
	
	public Edge getStatEdge() {
		return statEdge;
	}

	public void setStatEdge(Edge statEdge) {
		this.statEdge = statEdge;
	}

	public Edge getOrbEdge() {
		return orbEdge;
	}

	public void setOrbEdge(Edge orbEdge) {
		this.orbEdge = orbEdge;
	}

	public Coordinate getTouchPoint() {
		return touchPoint;
	}

	public void setTouchPoint(Coordinate touchPoint) {
		this.touchPoint = touchPoint;
	}
	
	public void print(){
		System.out.println("touching edge pair: ");
		statEdge.print();
		orbEdge.print();
		touchPoint.printCoordinate();
	}

	public Coordinate getPotentialVector() {
		
		//booleans saying if the touching point equals a start or end point from an edge
		boolean touchStatStart = false;
		boolean touchStatEnd = false;
		
		boolean touchOrbStart = false;
		boolean touchOrbEnd = false;

		if(statEdge.getStartPoint().equals(touchPoint)){
			touchStatStart = true;
		}
		else if(statEdge.getEndPoint().equals(touchPoint)){
			touchStatEnd = true;
		}
		
		if(orbEdge.getStartPoint().equals(touchPoint)){
			touchOrbStart = true;
		}
		else if(orbEdge.getEndPoint().equals(touchPoint)){
			touchOrbEnd = true;
		}
		
		/*there are four possible ways that end or start points can be touching:
		*stat	orb
		*-------------
		*end	end
		*start 	start
		*start	end
		*end	start
		*/
		//---------------------------------------------------------------------------------------------------------------------
		//if the touching point is at the end of both edges, there will be no potential vector
		if(touchStatEnd && touchOrbEnd)return null;
		
		//---------------------------------------------------------------------------------------------------------------------
		//if both startpoints are touching, the translationvector will be the orbiting edge if the relative position
		//of the orbiting edge is left to the stationary edge (can be determined with the D-function)
		//this by looking if the endpoint of the orbiting edge is located left or right
		if(touchStatStart && touchOrbStart){
			//if Dfunction returns value > 0 the orbiting edge is left of the stationary edge, and the translation
			//vector will be derived from the orbiting edge
			if(orbEdge.getEndPoint().dFunction(statEdge.getStartPoint(),statEdge.getEndPoint())>0){
				return orbEdge.makeFullVector();
			}
			else{
				//if the D-function returns 0, edges are parallel, either edge can be used.
				return statEdge.makeFullVector();
			}
		}
		//---------------------------------------------------------------------------------------------------------------------
		if(touchStatStart && touchOrbEnd){
			//in this case, if the orbiting edge is located left of the stationary edge, no vector will be possible
			//if it is on the right, the stationary edge will provide the vector.
			if(orbEdge.getStartPoint().dFunction(statEdge.getStartPoint(),statEdge.getEndPoint())>0){
				return null;
			}
			else{
				return statEdge.makeFullVector();
			}
		}
		//---------------------------------------------------------------------------------------------------------------------
		if(touchStatEnd && touchOrbStart){
			if(orbEdge.getEndPoint().dFunction(statEdge.getStartPoint(),statEdge.getEndPoint())>0){
				return null;
			}
			else{
				return orbEdge.makeFullVector();
			}
		}
		//---------------------------------------------------------------------------------------------------------------------
		//the two other cases left are when one of the edges is touching the other somewhere in between start and end point
		
		if(touchStatStart || touchStatEnd){
			return orbEdge.makePartialVector(touchPoint);
		}
		//---------------------------------------------------------------------------------------------------------------------
		if(touchOrbStart || touchOrbEnd){
			return statEdge.makePartialVector(touchPoint);
		}

		return null;
	}
    
}
