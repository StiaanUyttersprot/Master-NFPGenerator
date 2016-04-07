import java.io.Console;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Orbiting {

	public static int numberOfFails = 0;
	public static int numberOfSecFails = 0;
	public static int numberStuckInfinite = 0;
	public static NoFitPolygon generateNFP(MultiPolygon statPoly, MultiPolygon orbPoly) {

		Coordinate bottomCoord = statPoly.findBottomCoord();
		Coordinate topCoord = orbPoly.findTopCoord();
		
		statPoly.isStationary();
		orbPoly.translate(bottomCoord.getxCoord() - topCoord.getxCoord(),
				bottomCoord.getyCoord() - topCoord.getyCoord());

		NoFitPolygon nfp = new NoFitPolygon(orbPoly.getOuterPolygon()[0], statPoly, orbPoly);
		// we need to choose a vector to translate with an angle that is closest
		// to the last angle chosen to translate
//		NoFitPolygonStages.addNFP(new NoFitPolygon(nfp));
		
		orbitPolygon(nfp, statPoly, orbPoly, true);
		
		//System.out.println(stap);
		
		//check if right edges are traversed-----------------------------------------------------------------------
//		statPoly.printEdges();
//		orbPoly.printEdges();
		//----------------------------------------------------------------------------------------------------------------------------------
		//nfp steps for interlocking concavities and holes----------------------------------------------------------------------------------
		///---------------------------------------------------------------------------------------------------------------------------------
		//----------------------------------------------------------------------------------------------------------------------------------
		Edge possibleStartEdge;
		
		List<Coordinate[]> startPointList;
		Vector placeOrbPoly;
		
		while(!statPoly.allEdgesTraversed()){
			
			possibleStartEdge = statPoly.findUntraversedEdge();
			possibleStartEdge.markTraversed();
			startPointList = statPoly.searchStartPointList(possibleStartEdge, orbPoly);
			for(Coordinate[] startPoint: startPointList){
				
				if(!nfp.containsPoint(startPoint[1])){
					
					placeOrbPoly = new Vector(orbPoly.getOuterPolygon()[0], startPoint[1]);
					orbPoly.translate(placeOrbPoly);
					//check for perfectly overlapping polygons
					if(!perfectOverlap(statPoly, orbPoly)){
						nfp.startNewActiveList(orbPoly.getOuterPolygon()[0]);
						//startpoint has been found, now to start orbiting here
						
						// start the orbiting
						orbitPolygon(nfp, statPoly, orbPoly, false);
						//NoFitPolygonStages.addNFP(new NoFitPolygon(nfp));
					}
				}
				
			}
			//System.out.println("heeeyooooooooo");
		}
		while(!orbPoly.allEdgesTraversed()){
			
			possibleStartEdge = orbPoly.findUntraversedEdge();
			possibleStartEdge.markTraversed();
			startPointList = statPoly.searchOrbStartPointList(possibleStartEdge, orbPoly);
			for(Coordinate[] startPoint: startPointList){
				if(!nfp.containsPoint(startPoint[1])){
					
					placeOrbPoly = new Vector(orbPoly.getOuterPolygon()[0], startPoint[1]);
					orbPoly.translate(placeOrbPoly);
					if(!perfectOverlap(statPoly, orbPoly)){
						nfp.startNewActiveList(orbPoly.getOuterPolygon()[0]);
						//startpoint has been found, now to start orbiting here
						
						// start the orbiting
						orbitPolygon(nfp, statPoly, orbPoly, false);
						//NoFitPolygonStages.addNFP(new NoFitPolygon(nfp));
					}
				}
			}
		}
		
		//only draw the final result after placing the orbiting polygon back to the startPosition
//		orbPoly.translate(bottomCoord.getxCoord() - topCoord.getxCoord(),
//				bottomCoord.getyCoord() - topCoord.getyCoord());
//		NoFitPolygonStages.addNFP(new NoFitPolygon(nfp));
		
//		nfp.removeExcessivePoints();
//		System.out.println(nfp);
		return nfp;// TODO resultaat hier zetten
	}

	private static boolean perfectOverlap(MultiPolygon statPoly, MultiPolygon orbPoly) {
		boolean perfectOverlap = true;
		int startIndex = 0;
		if(statPoly.getOuterPolygon().length==orbPoly.getOuterPolygon().length){
			while(startIndex<statPoly.getOuterPolygon().length && !statPoly.getOuterPolygon()[0].equalValuesRounded(orbPoly.getOuterPolygon()[startIndex])){
				startIndex++;
			}
			int j = startIndex;
			if(j == statPoly.getOuterPolygon().length)return false;
			for(int i = 0; i<statPoly.getOuterPolygon().length; i++){
				if(statPoly.getOuterPolygon()[i].equalValuesRounded(orbPoly.getOuterPolygon()[j])){
					j = (j+1)%orbPoly.getOuterPolygon().length;
				}
				else{
					perfectOverlap = false;
					break;
				}
			}
			return perfectOverlap;
		}
		else return false;
	}

	private static void orbitPolygon(NoFitPolygon nfp, MultiPolygon statPoly, MultiPolygon orbPoly, boolean outer) {
		// TODO Auto-generated method stub
		double previousEdge = 0;
		List<Vector> usedTranslationVectorList = new ArrayList<>();
		Coordinate startPoint = new Coordinate(orbPoly.getOuterPolygon()[0]);
		Coordinate currentPoint = orbPoly.getOuterPolygon()[0];

		int aantalStappen = 2000;
		int stap = 0;
		// start the orbiting
		do{
			//Storing data for drawing step by step----------------------------------------------------------------------------------------
//			NoFitPolygonStages.addNFP(new NoFitPolygon(nfp));
			
			// ---------------------------------------------------------------------------------------------------------------------
			// detecting touching edges
			
			List<TouchingEdgePair> touchingEdgeList = statPoly.findTouchingEdges(orbPoly);
			
			//List<TouchingEdgePair> touchingEdgeList = statPoly.findTouchingEdgesWithoutTravMark(orbPoly);

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
			//sorting by edgenumber and vectors made from stationary edges get privileges
			Collections.sort(feasibleVectorList, new VectorComparator());
			// ---------------------------------------------------------------------------------------------------------------------
			//print feasible vectors
			
		
//			System.out.println("Feasible vectors: " + feasibleVectorList.size());
//			for (Vector vect : feasibleVectorList) {
//				vect.printVector();
//			}
//			System.out.println();

			//-------------------------------------------------------------------------------------------------------------------------
			//look for the translation vector

			
			Vector translationVector;
			if (feasibleVectorList.size() > 1) {
				
				int i = 0;
				// look for the vector that is closest in edgenumber to the one
				// previously translated by
				while (i < feasibleVectorList.size() && feasibleVectorList.get(i).getEdgeNumber() < previousEdge ) {
					i++;
				}
				
				
				///look if the point after translation with this vector is the same as the previously visited point, if this is true, we skip this vector to not get stuck
				if(i < feasibleVectorList.size()&&usedTranslationVectorList.size()>0&&nfp.getActiveList().size()> 1){
					
					double lastAngle = usedTranslationVectorList.get(usedTranslationVectorList.size()-1).getVectorAngle();
					
					double nextAngle = feasibleVectorList.get(i).getVectorAngle();
					
					if(Math.abs(nextAngle - lastAngle)==Math.PI){
						int nFeas = feasibleVectorList.size()-1;
						i = (i+1)%feasibleVectorList.size();
						nextAngle = feasibleVectorList.get(i).getVectorAngle();
						while(nFeas>0 && Math.abs(nextAngle - lastAngle)==Math.PI){
							i = (i+1)%feasibleVectorList.size();
							nextAngle = feasibleVectorList.get(i).getVectorAngle();
							nFeas--;
						}
						
					}
					
//					int nFeas = feasibleVectorList.size();
//					int usedIndex = usedTranslationVectorList.size()-1;
//					
//					while(nFeas>0 && usedIndex >= 0 && usedTranslationVectorList.size() >1){
//						
//						if(feasibleVectorList.get(i).getVectorAngle() == usedTranslationVectorList.get(usedIndex).getVectorAngle()){
//							usedIndex--;
//						}
//						else if(Math.abs(feasibleVectorList.get(i).getVectorAngle() - usedTranslationVectorList.get(usedIndex).getVectorAngle()) == Math.PI){
//							i = (i+1)%feasibleVectorList.size();
//							nFeas--;
//						}
//						else break;
//						
//					}
//					Coordinate previousPoint = nfp.getActiveList().get(nfp.getActiveList().size()-2);
//					
//					Coordinate nextPoint = currentPoint.translatedTo(feasibleVectorList.get(i));
//					nFeas = feasibleVectorList.size();
//					while(nFeas>0 && nextPoint.equals(previousPoint)){
//						i = (i+1)%feasibleVectorList.size();
//						nFeas--;
//						nextPoint = currentPoint.translatedTo(feasibleVectorList.get(i));		
//					}
					
				}
				

				// if the value of i is smaller then the listsize, a next vector
				// is found, if it reaches the end, it means the next vector is
				// the one with the smallest edgenumber
				if (i < feasibleVectorList.size()) {
					translationVector = feasibleVectorList.get(i);
				} else
					translationVector = feasibleVectorList.get(0);
			} else if(feasibleVectorList.size()==0){
				if(outer){
					System.out.println("outer RIP");
					numberOfFails++;
				}
				else{
//					System.out.println("inner RIP, perfect fit");
					numberOfSecFails++;
				}
				break;
			}
			else translationVector = feasibleVectorList.get(0);
			
			translationVector.trimFeasibleVector(orbPoly, statPoly, true);
			translationVector.trimFeasibleVector(statPoly, orbPoly, false);
			
			//-------------------------------------------------------------------------------------------------------------------------
			//translating the polygon and storing the data in the nfp
			orbPoly.translate(translationVector);
			usedTranslationVectorList.add(translationVector);
			nfp.addTranslation(orbPoly.getOuterPolygon()[0]);
			//store this angle as the previous angle
			previousEdge = translationVector.getEdgeNumber();
			
			//-------------------------------------------------------------------------------------------------------------------------
			//mark traversed edge (if working with findTouchingEdgesWithoutTravMark)
			translationVector.getParentEdge().markTraversed();
			for(Vector vect :feasibleVectorList){
				if(vect.getVectorAngle()==translationVector.getVectorAngle()){
					vect.getParentEdge().markTraversed();
				}
			}
			//-------------------------------------------------------------------------------------------------------------------------
			//print translation data
			
//			System.out.println("start point: "+startPoint.toString());
//			System.out.println("current point: "+currentPoint.toString());
//			System.out.println("translation over: " + translationVector.toString());
//			System.out.println(stap);
//			System.out.println();
			
			
			
			
			//check if right edges are traversed---------------------------------------------------------------
//			statPoly.printEdges();
//			orbPoly.printEdges();
			stap++;
		}
		while(!currentPoint.equalValuesRounded(startPoint) && stap < aantalStappen);
		if(!currentPoint.equalValuesRounded(startPoint) && stap == aantalStappen){
			System.out.println("stuck");
			numberOfFails++;
			numberStuckInfinite++;
//			NoFitPolygonStages.addNFP(new NoFitPolygon(nfp));
		}
		//for graphics don't remove it
//		nfp.removeLastDoubleCoordinate();
		
	}
}
