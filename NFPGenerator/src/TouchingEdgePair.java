
/**
 *
 * @author Stiaan
 * 
 *         Pairs of edges that are touching used in the orbiting method are
 *         stored with the coordinates of the touching point
 */
public class TouchingEdgePair {

	
	private Edge statEdge;
	private Edge orbEdge;
	private Coordinate touchPoint;

	// booleans saying if the touching point equals a start or end point from an
	// edge
	private boolean touchStatStart = false;
	private boolean touchStatEnd = false;

	private boolean touchOrbStart = false;
	private boolean touchOrbEnd = false;

	private double startAngle;
	private double endAngle;

	public TouchingEdgePair(Edge statEdge, Edge orbEdge, Coordinate touchPoint) {

		this.statEdge = statEdge;
		this.orbEdge = orbEdge;
		this.touchPoint = touchPoint;

		if (statEdge.getStartPoint().equals(touchPoint)) {
			touchStatStart = true;
		} else if (statEdge.getEndPoint().equals(touchPoint)) {
			touchStatEnd = true;
		}

		if (orbEdge.getStartPoint().equals(touchPoint)) {
			touchOrbStart = true;
		} else if (orbEdge.getEndPoint().equals(touchPoint)) {
			touchOrbEnd = true;
		}
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

	public void print() {
		System.out.println("touching edge pair: ");
		statEdge.print();
		orbEdge.print();
		touchPoint.printCoordinate();
		System.out.println("start angle: " + Math.toDegrees(startAngle));
		System.out.println("end angle: " + Math.toDegrees(endAngle));
	}

	public Coordinate getPotentialVector() {

		/*
		 * there are four possible ways that end or start points can be
		 * touching: stat orb ------------- end end start start start end end
		 * start
		 */
		// ---------------------------------------------------------------------------------------------------------------------
		// if the touching point is at the end of both edges, there will be no
		// potential vector
		if (touchStatEnd && touchOrbEnd)
			return null;

		// ---------------------------------------------------------------------------------------------------------------------
		// if both startpoints are touching, the translationvector will be the
		// orbiting edge if the relative position
		// of the orbiting edge is left to the stationary edge (can be
		// determined with the D-function)
		// this by looking if the endpoint of the orbiting edge is located left
		// or right
		if (touchStatStart && touchOrbStart) {
			// if Dfunction returns value > 0 the orbiting edge is left of the
			// stationary edge, and the translation
			// vector will be derived from the orbiting edge
			if (orbEdge.getEndPoint().dFunction(statEdge.getStartPoint(), statEdge.getEndPoint()) > 0) {
				return orbEdge.makeFullVector();
			} else {
				// if the D-function returns 0, edges are parallel, either edge
				// can be used.
				return statEdge.makeFullVector();
			}
		}
		// ---------------------------------------------------------------------------------------------------------------------
		if (touchStatStart && touchOrbEnd) {
			// in this case, if the orbiting edge is located left of the
			// stationary edge, no vector will be possible
			// if it is on the right, the stationary edge will provide the
			// vector.
			if (orbEdge.getStartPoint().dFunction(statEdge.getStartPoint(), statEdge.getEndPoint()) > 0) {
				return null;
			} else {
				return statEdge.makeFullVector();
			}
		}
		// ---------------------------------------------------------------------------------------------------------------------
		if (touchStatEnd && touchOrbStart) {
			if (orbEdge.getEndPoint().dFunction(statEdge.getStartPoint(), statEdge.getEndPoint()) > 0) {
				return null;
			} else {
				return orbEdge.makeFullVector();
			}
		}
		// ---------------------------------------------------------------------------------------------------------------------
		// the two other cases left are when one of the edges is touching the
		// other somewhere in between start and end point

		if (touchStatStart || touchStatEnd) {
			return orbEdge.makePartialVector(touchPoint);
		}
		// ---------------------------------------------------------------------------------------------------------------------
		if (touchOrbStart || touchOrbEnd) {
			return statEdge.makePartialVector(touchPoint);
		}

		return null;
	}

	public void calcFeasibleAngleRange() {

		double stationaryAngle = statEdge.getAngle();
		if (stationaryAngle < 0)
			stationaryAngle = stationaryAngle + Math.PI * 2;
		// System.out.println("stationary angle: " +
		// Math.toDegrees(stationaryAngle));

		double orbitingAngle = orbEdge.getAngle();
		if (orbitingAngle < 0)
			orbitingAngle = orbitingAngle + Math.PI * 2;
		// System.out.println("orbiting angle: " +
		// Math.toDegrees(orbitingAngle));

		//Situation 7
		if (stationaryAngle == orbitingAngle || stationaryAngle == orbitingAngle - Math.PI || stationaryAngle == orbitingAngle + Math.PI) {
			startAngle = stationaryAngle - Math.PI;
			endAngle = stationaryAngle;

			return;
		}
		
		
		// situation 1
		if (!touchStatStart && !touchStatEnd) {
			startAngle = stationaryAngle - Math.PI;
			endAngle = stationaryAngle;

			return;
		}

		// situation 2
		if (!touchOrbStart && !touchOrbEnd) {
			// stationary edge is located to the right of orbiting edge
			//we have to check the D-function for the start and end of the stationary edge to see if it is left or right, one of them will be zero, 
			//the other one will be smaller or bigger then zero
			if (statEdge.getEndPoint().dFunction(orbEdge.getStartPoint(), orbEdge.getEndPoint()) <= 0 
					&& statEdge.getStartPoint().dFunction(orbEdge.getStartPoint(), orbEdge.getEndPoint()) <= 0) {
				startAngle = orbitingAngle;
				endAngle = orbitingAngle + Math.PI;
			}
			// stationary edge is located to the right of orbiting edge
			else {
				startAngle = orbitingAngle - Math.PI;
				endAngle = orbitingAngle;
			}
			return;
		}

		// both angles are positive
		// situation 3
		if (touchStatStart && touchOrbStart) {

			// orbEdge is right of statEdge
			if (orbEdge.getEndPoint().dFunction(statEdge.getStartPoint(), statEdge.getEndPoint()) < 0) {

				// I work with the negative angle of orbiting edge
				orbitingAngle -= 2 * Math.PI;

				startAngle = orbitingAngle - Math.PI;
				endAngle = stationaryAngle;

				while (endAngle - 2 * Math.PI >= startAngle)
					startAngle += 2 * Math.PI;
				while (endAngle < startAngle)
					endAngle += 2 * Math.PI;
			}
			// orbEdge left of statEdge
			else {
				startAngle = stationaryAngle;
				endAngle = orbitingAngle + Math.PI;

				while (endAngle < startAngle)
					startAngle -= 2 * Math.PI;
				while (endAngle - 2 * Math.PI >= startAngle)
					startAngle += 2 * Math.PI;

			}
			return;
		}

		// situation 4
		if (touchStatStart && touchOrbEnd) {

			// orbEdge is right of statEdge
			if (orbEdge.getStartPoint().dFunction(statEdge.getStartPoint(), statEdge.getEndPoint()) < 0) {

				startAngle = orbitingAngle;
				endAngle = stationaryAngle;

				while (endAngle < startAngle)
					startAngle -= 2 * Math.PI;
				while (endAngle - 2 * Math.PI >= startAngle)
					startAngle += 2 * Math.PI;
			}
			// orbEdge is left of statEdge
			else {
				startAngle = stationaryAngle;
				endAngle = orbitingAngle;

				while (endAngle < startAngle)
					startAngle -= 2 * Math.PI;
				while (endAngle - 2 * Math.PI >= startAngle)
					startAngle += 2 * Math.PI;
			}
			return;
		}

		// situation 5
		if (touchStatEnd && touchOrbEnd) {

			// orbEdge is right of statEdge
			if (orbEdge.getStartPoint().dFunction(statEdge.getStartPoint(), statEdge.getEndPoint()) < 0) {

				startAngle = stationaryAngle - Math.PI;
				endAngle = orbitingAngle;

				while (endAngle < startAngle)
					startAngle -= 2 * Math.PI;
				while (endAngle - 2 * Math.PI >= startAngle)
					startAngle += 2 * Math.PI;
			}
			// orbEdge is left of statEdge
			else {
				startAngle = orbitingAngle;
				endAngle = stationaryAngle + Math.PI;

				while (endAngle < startAngle)
					startAngle -= 2 * Math.PI;
				while (endAngle - 2 * Math.PI >= startAngle)
					startAngle += 2 * Math.PI;
			}
			return;
		}

		// situation 6
		if (touchStatEnd && touchOrbStart) {

			// orbEdge is right of statEdge
			if (orbEdge.getEndPoint().dFunction(statEdge.getStartPoint(), statEdge.getEndPoint()) < 0) {

				startAngle = stationaryAngle - Math.PI;
				endAngle = orbitingAngle + Math.PI;

				while (endAngle < startAngle)
					startAngle -= 2 * Math.PI;
				while (endAngle - 2 * Math.PI >= startAngle)
					startAngle += 2 * Math.PI;
			}
			// orbEdge is left of statEdge
			else {
				startAngle = orbitingAngle - Math.PI;
				endAngle = stationaryAngle + Math.PI;

				while (endAngle < startAngle)
					startAngle -= 2 * Math.PI;
				while (endAngle - 2 * Math.PI >= startAngle)
					startAngle += 2 * Math.PI;
			}
			return;
		}
	}

	public boolean isFeasibleVector(Coordinate vector) {

		// test all possible ranges
		double vectorAngle = vector.getVectorAngle();
		System.out.println(Math.toDegrees(startAngle) + " -> " +
		Math.toDegrees(endAngle));
		
		if (startAngle <= vectorAngle && vectorAngle <= endAngle)
			return true;

		double rotatedVectorAngle = vectorAngle + 2 * Math.PI;
		if (startAngle <= rotatedVectorAngle && rotatedVectorAngle <= endAngle)
			return true;

		double negativeRotatedVectorAngle = vectorAngle - 2 * Math.PI;
		if (startAngle <= negativeRotatedVectorAngle && negativeRotatedVectorAngle <= endAngle)
			return true;

		return false;
	}

}
