import java.io.Console;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Orbiting {

	public static MultiPolygon generateNFP(MultiPolygon statPoly, MultiPolygon orbPoly) {

		Coordinate bottomCoord = statPoly.findBottomCoord();
		Coordinate topCoord = orbPoly.findTopCoord();
		
		statPoly.isStationary();
		orbPoly.translate(bottomCoord.getxCoord() - topCoord.getxCoord(),
				bottomCoord.getyCoord() - topCoord.getyCoord());

		NoFitPolygon nfp = new NoFitPolygon(orbPoly.getOuterPolygon()[0], statPoly, orbPoly);
		// we need to choose a vector to translate with an angle that is closest
		// to the last angle chosen to translate
		double previousEdge = 0;
		List<Vector> usedTranslationVectorList = new ArrayList<>();
		Coordinate startPoint = new Coordinate(orbPoly.getOuterPolygon()[0]);
		Coordinate currentPoint = orbPoly.getOuterPolygon()[0];

		int aantalStappen = 50;
		int stap = 0;
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

			
//			System.out.println("touching edges: " + touchingEdgeList.size());
//			for (TouchingEdgePair tEP : touchingEdgeList) {
//				tEP.print();
//			}
//			System.out.println();

			// ---------------------------------------------------------------------------------------------------------------------
			// create potential translation vectors

			Set<Vector> potentialVectorList = new HashSet<>();
			Vector potVector;

			for (TouchingEdgePair tEP : touchingEdgeList) {
				potVector = tEP.getPotentialVector();
				if (potVector != null && !potentialVectorList.contains(potVector)) {
					potentialVectorList.add(potVector);
				}
			}
			
			// ---------------------------------------------------------------------------------------------------------------------
			// printing potential vectors
			
//			System.out.println();
//			System.out.println("Potential vectors: " + potentialVectorList.size());
//			for (Vector vect : potentialVectorList) {
//				vect.printVector();
//			}
//			System.out.println();
			
			// ----------------------------------------------------------------------------------------------------------------------
			// find the feasible vectors
			boolean feasibleVector;
			List<Vector> feasibleVectorList = new ArrayList<>();

			for (Vector vector : potentialVectorList) {
				int i = 0;
				feasibleVector = true;
//				System.out.println("vector angle being tested: " + Math.toDegrees(vector.getVectorAngle()));
				while (feasibleVector && i < touchingEdgeList.size()) {
					TouchingEdgePair tEP = touchingEdgeList.get(i);
					
					//we use rounded angles to avoid rounding errors
					if (!tEP.isFeasibleVectorWithRounding(vector)){
						feasibleVector = false;
//						System.out.println("infeasible Vector");
					}
						
					i++;

				}
				if (feasibleVector) {
					feasibleVectorList.add(vector);
				}

			}
			
			// ---------------------------------------------------------------------------------------------------------------------
			//print feasible vectors
			
			
//			System.out.println("Feasible vectors: " + feasibleVectorList.size());
//			for (Vector vect : feasibleVectorList) {
//				vect.printVector();
//			}
//			System.out.println();
			
			//-------------------------------------------------------------------------------------------------------------------------
			//look for the translation vector
			Collections.sort(feasibleVectorList, new EdgeComparator());
			Vector translationVector;
			if (feasibleVectorList.size() > 1) {
				//sort the vectors from smallest to largest angle
				
				
				int i = 0;
				// look for the vector that is closest in angle to the one
				// previously translated by
				while (i < feasibleVectorList.size() && feasibleVectorList.get(i).getEdgeNumber() < previousEdge ) {
					i++;
				}
				
				
				///look if the point after translation with this vector is the same as the previously visited point, if this is true, we skip this vector to not get stuck
				if(i < feasibleVectorList.size()&&usedTranslationVectorList.size()>0&&nfp.getActiveList().size()> 1){
					Coordinate previousPoint = nfp.getActiveList().get(nfp.getActiveList().size()-2);
					
					Coordinate nextPoint = currentPoint.translatedTo(feasibleVectorList.get(i));
					
					if(nextPoint.equals(previousPoint)){
						i++;
					}
					
				}
				
				//check if the vector found isn't in the same direction as the previous one, if it is we may skip concavities if we use it
				if(usedTranslationVectorList.size()>1&&i < feasibleVectorList.size()){
					if(usedTranslationVectorList.get(usedTranslationVectorList.size()-1).getVectorAngle()==feasibleVectorList.get(i).getVectorAngle()){
						i++;
					}
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

			// -----------------------------------------------------------------------------------------------------------------------
			// trimming the feasible vectors

			// the testEdge will be the edge that starts in a coordinate of the
			// polygon and ends in the translation of that coordinate
//			Edge testEdge;
//			Coordinate intersectionCoord;
			
			
				
				translationVector.trimFeasibleVector(orbPoly, statPoly, true);
				translationVector.trimFeasibleVector(statPoly, orbPoly, false);
				/*
				for (Coordinate coord : orbPoly.getOuterPolygon()) {
					//this is a testEdge and does not have a real number
					testEdge = new Edge(coord, coord.add(vector), -1);

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
								
								intersectionCoord.printCoordinate();
								// trim the vector with
								// endpoint = intersectionCoordinate
								vector.trimTo(intersectionCoord,coord);
								
								//because the vector gets trimmed the testEdge changes, this will result in less intersection because of the shorter vector
								//also the Vector will not be overwritten by every new intersection if the testEdge is changed, only when it has to be shorter
								testEdge = new Edge(coord, coord.add(vector), -1);
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
					testEdge = new Edge(coord, coord.subtract(vector), -1);

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

				}*/
			//}
			
			
			
			//-------------------------------------------------------------------------------------------------------------------------
			//Print the trimmed vectors
			
//			System.out.println();
//			System.out.println("Trimmed vectors: " + feasibleVectorList.size());
//			for (Vector vect : feasibleVectorList) {
//				vect.printVector();
//			}
//			System.out.println();
			
			
			

			//-------------------------------------------------------------------------------------------------------------------------
			//translating the polygon and storing the data in the nfp
			orbPoly.translate(translationVector);
			usedTranslationVectorList.add(translationVector);
			nfp.addTranslation(orbPoly.getOuterPolygon()[0]);
			//store this angle as the previous angle
			previousEdge = translationVector.getEdgeNumber();
			
			//-------------------------------------------------------------------------------------------------------------------------
			//print translation data
			
//			System.out.println("start point: "+startPoint.toString());
//			System.out.println("current point: "+currentPoint.toString());
//			System.out.println("translation over: " + translationVector.toString());
//			System.out.println();
			
			//Storing data for drawing
//			NoFitPolygonStages.addNFP(new NoFitPolygon(nfp));
			stap++;
		}
		while(!currentPoint.equalValuesRounded(startPoint) && stap < aantalStappen);
		
		NoFitPolygonStages.addNFP(new NoFitPolygon(nfp));
		
		return null;// TODO resultaat hier zetten
	}
}
