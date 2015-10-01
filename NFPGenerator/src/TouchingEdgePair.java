
/**
 *
 * @author Stiaan
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
		statEdge.print();
		orbEdge.print();
		touchPoint.printCoordinate();
	}

	public Coordinate getPotentialVector() {
		// TODO Auto-generated method stub
		return null;
	}
	
    
}
