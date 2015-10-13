import java.io.Console;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Orbiting {

	public static MultiPolygon generateNFP(MultiPolygon statPoly, MultiPolygon orbPoly) {

		
		Scanner sc = new Scanner(System.in);
		Coordinate bottomCoord = statPoly.findBottomCoord();
		Coordinate topCoord = orbPoly.findTopCoord();
		
		statPoly.isStationary();
		orbPoly.translate(bottomCoord.getxCoord() - topCoord.getxCoord(),
				bottomCoord.getyCoord() - topCoord.getyCoord());

		NoFitPolygon nfp = new NoFitPolygon(orbPoly.getOuterPolygon()[0], statPoly, orbPoly);
		// we need to choose a vector to translate with an angle that is closest
		// to the last angle chosen to translate
		double previousAngle = -Math.PI / 2;
		Coordinate startPoint = new Coordinate(orbPoly.getOuterPolygon()[0]);
		Coordinate currentPoint = orbPoly.getOuterPolygon()[0];

		// start the orbiting
		do{
			// ---------------------------------------------------------------------------------------------------------------------
			// detecting touching edges
			List<TouchingEdgePair> touchingEdgeList = statPoly.findTouchingEdges(orbPoly);

			for (TouchingEdgePair tEP : touchingEdgeList) {
				tEP.calcFeasibleAngleRange();
			}
			
			// ---------------------------------------------------------------------------------------------------------------------
			// printing touching edges

			
			System.out.println("touching edges: " + touchingEdgeList.size());
			for (TouchingEdgePair tEP : touchingEdgeList) {
				tEP.print();
			}
			

			// ---------------------------------------------------------------------------------------------------------------------
			// create potential translation vectors

			Set<Coordinate> potentialVectorList = new HashSet<>();
			Coordinate potVector;

			for (TouchingEdgePair tEP : touchingEdgeList) {
				potVector = tEP.getPotentialVector();
				if (potVector != null && !potentialVectorList.contains(potVector)) {
					potVector.calculateVectorAngle();
					potentialVectorList.add(potVector);
				}
			}
			
			// ---------------------------------------------------------------------------------------------------------------------
			// printing potential vectors
			
			System.out.println();
			System.out.println("Potential vectors: " + potentialVectorList.size());
			for (Coordinate vect : potentialVectorList) {
				vect.printCoordinate();
			}
			
			
			// ----------------------------------------------------------------------------------------------------------------------
			// find the feasible vectors
			boolean feasibleVector;
			List<Coordinate> feasibleVectorList = new ArrayList<>();

			for (Coordinate vector : potentialVectorList) {
				int i = 0;
				feasibleVector = true;
				System.out.println("vector angle being tested: " + Math.toDegrees(vector.getVectorAngle()));
				while (feasibleVector && i < touchingEdgeList.size()) {
					TouchingEdgePair tEP = touchingEdgeList.get(i);
					
					if (!tEP.isFeasibleVector(vector)){
						feasibleVector = false;
						System.out.println("infeasible Vector");
					}
						
					i++;

				}
				if (feasibleVector) {
					feasibleVectorList.add(vector);
				}

			}
			
			// ---------------------------------------------------------------------------------------------------------------------
			//print feasible vectors
			
			System.out.println();
			System.out.println("Feasible vectors: " + feasibleVectorList.size());
			for (Coordinate vect : feasibleVectorList) {
				vect.printCoordinate();
			}
			

			// -----------------------------------------------------------------------------------------------------------------------
			// trimming the feasible vectors

			// the testEdge will be the edge that starts in a coordinate of the
			// polygon and ends in the translation of that coordinate
			Edge testEdge;
			Coordinate intersectionCoord;
			
			for (Coordinate vector : feasibleVectorList) {
				for (Coordinate coord : orbPoly.getOuterPolygon()) {
					testEdge = new Edge(coord, coord.add(vector));

					// checking the coordinates with the outer edges of the
					// stationary polygon
					for (Edge edge : statPoly.getOuterPolygonEdges()) {

						// if the bounding boxes intersect, line intersection
						// has to
						// be checked and the vector may need to be trimmed
						if (edge.boundingBoxIntersect(testEdge)) {
							// TODO: line intersection, trim vector to that
							// distance
							if (edge.lineIntersect(testEdge)) {
								intersectionCoord = edge.calcIntersection(testEdge);
								// trim the vector by creating a new vector with
								// endpoint = intersectionCoordinate
								vector.trimTo(intersectionCoord,coord);
							}

						}

					}
					// checking the coordinates with the hole edges of the
					// stationary polygon
					for (Edge[] edgeArray : statPoly.getHoleEdges()) {
						for (Edge edge : edgeArray) {
							// if the bounding boxes intersect, line
							// intersection
							// has to
							// be checked and the vector may need to be trimmed
							if (edge.boundingBoxIntersect(testEdge)) {
								// TODO: line intersection, trim vector to that
								// distance
								
								if (edge.lineIntersect(testEdge)) {
									intersectionCoord = edge.calcIntersection(testEdge);
									// trim the vector by creating a new vector
									// with
									// endpoint = intersectionCoordinate
									vector.trimTo(intersectionCoord,coord);
								}

							}
						}
					}

				}
				// now we have to test all the points of the stationary polygon
				// with
				// the edges of the orbiting polygon
				for (Coordinate coord : statPoly.getOuterPolygon()) {
					// the translation will be in the other direction, so we
					// subtract the vector to get the edge
					testEdge = new Edge(coord, coord.subtract(vector));

					// checking the coordinates with the outer edges of the
					// stationary polygon
					for (Edge edge : orbPoly.getOuterPolygonEdges()) {

						// if the bounding boxes intersect, line intersection
						// has to
						// be checked and the vector may need to be trimmed
						if (edge.boundingBoxIntersect(testEdge)) {
							// TODO: line intersection, trim vector to that
							// distance
							if (edge.lineIntersect(testEdge)) {
//								System.out.println("intersecting lines: " +edge.toString() + " " + testEdge.toString());
								intersectionCoord = edge.calcIntersection(testEdge);
								// trim the vector by creating a new vector with
								// endpoint = intersectionCoordinate
								// in this case the new vector will be in the
								// wrong
								// direction because we are using coordinates of
								// the
								// stationary polygon to translate, so we need
								// to
								// reflect the vector
								vector.trimTo(intersectionCoord,coord);
								vector.reflect();
							}

						}

					}
					// checking the coordinates with the hole edges of the
					// stationary polygon
					for (Edge[] edgeArray : orbPoly.getHoleEdges()) {
						for (Edge edge : edgeArray) {
							// if the bounding boxes intersect, line
							// intersection
							// has to
							// be checked and the vector may need to be trimmed
							if (edge.boundingBoxIntersect(testEdge)) {
								// TODO: line intersection, trim vector to that
								// distance
								if (edge.lineIntersect(testEdge)) {

									intersectionCoord = edge.calcIntersection(testEdge);
									// trim the vector by creating a new vector
									// with
									// endpoint = intersectionCoordinate
									// reflection is needed
									vector.trimTo(intersectionCoord,coord);
									vector.reflect();
								}

							}
						}
					}

				}
			}
			
			//-------------------------------------------------------------------------------------------------------------------------
			//Print the trimmed vectors
			
			System.out.println();
			System.out.println("Trimmed vectors: " + feasibleVectorList.size());
			for (Coordinate vect : feasibleVectorList) {
				vect.printCoordinate();
			}
			
			
			//-------------------------------------------------------------------------------------------------------------------------
			//look for the translation vector

			Coordinate translationVector;
			if (feasibleVectorList.size() > 1) {
				//sort the vectors from smallest to largest angle
				Collections.sort(feasibleVectorList, new AngleComparator());
				
				int i = 0;
				// look for the vector that is closest in angle to the one
				// previously translated by
				while (feasibleVectorList.get(i).getVectorAngle() < previousAngle && i < feasibleVectorList.size()) {
					i++;
				}
				// if the value of i is smaller then the listsize, a next vector
				// is found, if it reaches the end, it means the next vector is
				// the one with the smallest angle
				if (i < feasibleVectorList.size()) {
					translationVector = feasibleVectorList.get(i);
				} else
					translationVector = feasibleVectorList.get(0);
			} else if(feasibleVectorList.size()==0){
				System.out.println("RIP");
				break;
			}
			else translationVector = feasibleVectorList.get(0);

			//-------------------------------------------------------------------------------------------------------------------------
			//translating the polygon and storing the data in the nfp
			orbPoly.translate(translationVector);
			nfp.addTranslation(orbPoly.getOuterPolygon()[0]);
			
			//-------------------------------------------------------------------------------------------------------------------------
			//print translation data
			
			System.out.println("translation over: " + translationVector.toString());
			
			System.out.println("start point: "+startPoint.toString());
			
			//currentPoint = nfp.addTranslation(translationVector);
			System.out.println("current point: "+currentPoint.toString());
			
			System.out.println("translation over: " + translationVector.toString());
			
			System.out.println();
			
			//Storing data for drawing
			NoFitPolygonStages.addNFP(new NoFitPolygon(nfp));
			
		}
		while(!currentPoint.equals(startPoint)/* && sc.nextInt() ==0*/);
		
		return null;// TODO resultaat hier zetten
	}
}
