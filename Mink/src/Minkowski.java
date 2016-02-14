import java.awt.Checkbox;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Minkowski {
	public static int numberOfFails = 0;
	public static int numberStuckInfinite = 0;
	
	private static boolean printMinkData = false;
	private static boolean printEdgeListData = false;
	private static boolean printBoundaryData = false;
	private static boolean drawFigures = false;
	private static boolean handleError = true;
	
	static Boolean clockwiseContainsTurningpoints;
	public static NoFitPolygon generateMinkowskiNFP(MultiPolygon polyA, MultiPolygon polyB) {
		
		NoFitPolygon nfp = null;
		clockwiseContainsTurningpoints = null;

		//---------------------------------------------------------------------------------------------
		//Generate Minkowski sum edge list
		
		//polyA has to be counterclockwise
		if(polyA.checkClockwise(polyA.getOuterPolygon()))
			polyA.changeClockOrientation(polyA.getOuterPolygon());
		//polyB has to be counterclockwise
		if(polyB.checkClockwise(polyB.getOuterPolygon()))
			polyB.changeClockOrientation(polyB.getOuterPolygon());
	
		polyA.labelCounterClockwise();
		polyB.labelCounterClockwise();

		polyB.replaceByNegative();
		
//		polyA.calcDeltaAngles();
//		polyB.calcDeltaAngles();
		
		//TODO: is het met de hoeken in de zin van de edge(die in wijzerzin staat) of tegen die zin in
//		polyB.changeEdgeAnglesCounterClockwise();
		
		List <Edge> polyASortList = new ArrayList<Edge>();
		List <Edge> polyBSortList = new ArrayList<Edge>();
		
		for(Edge e: polyA.getOuterPolygonEdges()){
			e.setPolygonA(true);
			polyASortList.add(e);
		}
		
		Collections.sort(polyASortList, new EdgeNumberComparator());
		
		for(Edge e: polyB.getOuterPolygonEdges()){
			e.setPolygonA(false);
			polyBSortList.add(e);
		}
		Collections.sort(polyBSortList, new EdgeNumberComparator());
		
		calcDeltaAngles(polyASortList);
		calcDeltaAngles(polyBSortList);
		
		if(printMinkData){
			printEdgeList(polyASortList);
			System.out.println();
			printEdgeList(polyBSortList);
		}
		
		//divide B in groups by turningpoint
		List<List<Edge>> dividedListB = new ArrayList<>();
		List<Edge> turningGroupB = new ArrayList<>();
		
		boolean isConcaveB = false;
		boolean isConcaveA = false;
		
		for(Edge e: polyBSortList){
			if(e.isTurningPoint()){
				isConcaveB = true;
				break;
			}
		}
		for(Edge e: polyASortList){
			if(e.isTurningPoint()){
				isConcaveA = true;
				break;
			}
		}
		int i = 0;
		int aantalToegevoegd = 0;
		if(isConcaveB){
			dividedListB = divideList(polyBSortList);
			
			
		}
		else{
			for(Edge e: polyBSortList){
				turningGroupB.add(e);
			}
			dividedListB.add(turningGroupB);
		}
		
//		for(List<Edge> edgeList: dividedListB){
//			System.out.println("group: ");
//			printSimpleEdgeList(edgeList);	
//		}
		
		List<Edge> msEdgeList = new ArrayList<>();
		
		boolean firstSequence = true;
		Edge helpEdge;
		List<Edge> nextEdgeList;
		
		i = 0;
		
		for(List<Edge> edgeListB: dividedListB){
			if(clockwiseB(edgeListB)&&isConcaveB){
				nextEdgeList = MinkNeg(polyASortList, edgeListB);
			}
			else{
				nextEdgeList = MinkPos(polyASortList, edgeListB);
			}

			if(firstSequence){
				msEdgeList.addAll(nextEdgeList);
				firstSequence = false;
			}
			else{
				linkEdgeLists(msEdgeList, nextEdgeList, polyASortList, false);
			}
		}
		//link the last edge to the first
		linkEdgeLists(msEdgeList, msEdgeList, polyASortList, true);
		if(printMinkData){
			System.out.println("msEdgeList: ");
			printSimpleEdgeList(msEdgeList);
		}
		
		List<Edge> complexPolygonEdgeList = makeIntoPolygon(msEdgeList);
//		System.out.println("complexPolygonEdgeList: ");
//		printEdgeList(complexPolygonEdgeList);
		if(drawFigures){
			ComplexPolygonStage.addComplexPolygon(complexPolygonEdgeList);
		}
		
		//---------------------------------------------------------------------------------------------------------------------------------------
		//algorithm 2
		List<List<Edge>> trackLineTripList;
		trackLineTripList = makeTrackLineTrips(complexPolygonEdgeList);
		
		if(drawFigures){
			ComplexPolygonStage.addTrackLineTrips(trackLineTripList);
		}
		
		if(printMinkData){
			for(List<Edge> trackLineTrip: trackLineTripList){
				System.out.println("trackLineTrip");
				printEdgeList(trackLineTrip);
	//			printSimpleEdgeList(trackLineTrip);
			}
		}
		
		
		//---------------------------------------------------------------------------------------------------------------------------------------
//		
//		//algorithm 3
		List<List<Edge>> cycleList;
//		System.out.println("algorithm 3");

		cycleList = boundarySearch(trackLineTripList);
//		ComplexPolygonStage.addTrackLineTrips(cycleList);
//		
		if(printMinkData){
			for(List<Edge> cycle: cycleList){
				System.out.println("cycle");
				for(Edge e: cycle){
					System.out.println(e);
				}
			}
		}
		
		//--------------------------------------------------------------------------------------------------------------------------------------
		//TODO:check validity of cycles
		
		//--------------------------------------------------------------------------------------------------------------------------------------
		//make NFP and place it at right coordinates
		
		if(cycleList.size()==0 && handleError){
			System.out.println("failed");
			return null;
		}
		
		polyB.replaceByNegative();
		Coordinate bottomCoord = polyA.findBottomCoord();
		Coordinate topCoord = polyB.findTopCoord();
		Vector translationVector = new Vector(bottomCoord.getxCoord() - topCoord.getxCoord(),
				bottomCoord.getyCoord() - topCoord.getyCoord());
		Coordinate startCoord = new Coordinate(polyB.getOuterPolygon()[0].translatedTo(translationVector));
		nfp = makeNFP(cycleList, startCoord);
		nfp.removeExcessivePoints();
//		System.out.println(nfp);
		
		
		
		
		//draw nfp------------------------------------------------
		polyB.translate(bottomCoord.getxCoord() - topCoord.getxCoord(),
				bottomCoord.getyCoord() - topCoord.getyCoord());
		nfp.setOrbitingPolygon(polyB);
		nfp.setStationaryPolygon(polyA);
		if(drawFigures){
			NoFitPolygonStages.addNFP(nfp);
		}

		
		return nfp;
		
	}

	private static List<List<Edge>> divideList(List<Edge> polyBSortList) {
		int aantalToegevoegd = 0;
		int i = polyBSortList.size()-1;
		i = 0;
		//divide B in groups by turningpoint
		List<List<Edge>> dividedListB = new ArrayList<>();
		List<Edge> turningGroupB = new ArrayList<>();
		
		
		List<Edge> checkCounterClockwiseGroup = new ArrayList<>();
		int firstTurningPoint;
		while(!polyBSortList.get(i).isTurningPoint()){

			i--;
			if(i<0)i = polyBSortList.size()-1;
		}
		firstTurningPoint = i;
		checkCounterClockwiseGroup.add(polyBSortList.get(i));
		i = (i+1)%polyBSortList.size();
		while(!polyBSortList.get(i).isTurningPoint()){
			checkCounterClockwiseGroup.add(polyBSortList.get(i));

			i = (i+1)%polyBSortList.size();
		}
		//stop at next turning point
		checkCounterClockwiseGroup.add(polyBSortList.get(i));
		if(clockwiseB(checkCounterClockwiseGroup)){
			i = firstTurningPoint-1;
			if(i<0)i = polyBSortList.size()-1;
		}
		else{
			i = firstTurningPoint;
		}
		
		clockwiseContainsTurningpoints = null;
		
		
		while(aantalToegevoegd<polyBSortList.size()){
			//find starting turning point
			while(!polyBSortList.get(i).isTurningPoint()){

				i--;
				if(i<0)i = polyBSortList.size()-1;
			}
			turningGroupB.add(polyBSortList.get(i));
			aantalToegevoegd++;
			i = (i+1)%polyBSortList.size();
			while(!polyBSortList.get(i).isTurningPoint()){
				turningGroupB.add(polyBSortList.get(i));
				aantalToegevoegd++;
				i = (i+1)%polyBSortList.size();
			}
			//stop at next turning point
			turningGroupB.add(polyBSortList.get(i));
			aantalToegevoegd++;
			dividedListB.add(turningGroupB);
			i = (i+1)%polyBSortList.size();
			
			if(!polyBSortList.get(i).isTurningPoint()){
				turningGroupB = new ArrayList<>();
				while(!polyBSortList.get(i).isTurningPoint()){
					turningGroupB.add(polyBSortList.get(i));
					aantalToegevoegd++;
					i = (i+1)%polyBSortList.size();
				}
				dividedListB.add(turningGroupB);
			}
			if(aantalToegevoegd<polyBSortList.size()){
				turningGroupB = new ArrayList<>();
			}
		}
		if(aantalToegevoegd>polyBSortList.size())System.err.println("teveel toegevoegd aan de dividedListB");
		return dividedListB;
	}

	private static List<Edge> makeIntoPolygon(List<Edge> msEdgeList) {
		List<Vector> msVectorList;
		List<Edge> complexPolygonEdges = new ArrayList<>();
		msVectorList = getVectorList(msEdgeList);
		int startIndex = 0;
		Coordinate startCoord;
//		for(int i = 0; i< msEdgeList.size();i++){
//			if(msEdgeList.get(i).getEdgeNumber() == 1 && !msEdgeList.get(i).isPolygonA()){
//				startIndex = i;
//				break;
//			}
//		}
//		startCoord = new Coordinate(msEdgeList.get(startIndex).getStartPoint());
//		if(!msEdgeList.get(startIndex).isPolygonA())startCoord.replaceByNegative();
//		System.out.println("startCoord: " + startCoord);
		
		startCoord = new Coordinate(0,0);
		
		Edge complexEdge;// = bStart.getEdgeWithTranslation(msVectorList.get(startIndex), msEdgeList.get(0));
//		complexPolygonEdges.add(complexEdge);
		Coordinate startPoint;
		int index = startIndex;
		
		while(complexPolygonEdges.size()<msEdgeList.size()){
			if(complexPolygonEdges.size()==0){
				startPoint = startCoord;
			}
			else{
				startPoint = complexPolygonEdges.get(complexPolygonEdges.size()-1).getEndPoint();
			}
//			System.out.println();
//			System.out.println("edge: " + msEdgeList.get(index));
//			System.out.println("vector: " + msVectorList.get(index).toString());
//			System.out.println("startPoint: " + startPoint);
			
			complexEdge = startPoint.getEdgeWithTranslation(msVectorList.get(index), msEdgeList.get(index));
			
//			System.out.println("new edge: " + complexEdge);
			complexPolygonEdges.add(complexEdge);
			index = (index+1)%msEdgeList.size();
		}
		return complexPolygonEdges;
	}

	private static List<Vector> getVectorList(List<Edge> msEdgeList) {
		List<Vector> vectorList = new ArrayList<Vector>();
		Vector vect;
		for(Edge e: msEdgeList){
			vect = e.makeFullVector(e.getEdgeNumber());
			vectorList.add(vect);
		}
//		for(Vector vector: vectorList){
//			System.out.println(vector);
//		}
		return vectorList;
	}

	private static void printEdgeList(List<Edge> list) {
		for(Edge e: list){
			System.out.println(e);
		}
		
	}
	
	private static void printSimpleEdgeList(List<Edge> list) {
		for(Edge e: list){
			if(e.isPolygonA()){
				System.out.print("a"+e.getEdgeNumber());
			}
			else System.out.print("b"+e.getEdgeNumber());
			System.out.print(", ");
		}
		System.out.println();
	}

	//	calculate if polygon B is clockwise or not (can differ if you take the edge order as clockwise values, or the direction of the vectors)
	private static boolean clockwiseB(List<Edge> edgeList) {
		
//		double clockwiseValue = 0;
//		
//		double xDiff;
//		double ySum;
//		
//		Coordinate endCoord;
//		Coordinate beginCoord;
//		//If the result is positive the curve is clockwise, if it's negative the curve is counter-clockwise.
//		for(Edge e: edgeList){
//			//Sum over the edges, (x2-x1)(y2+y1). If the result is positive the curve is clockwise, if it's negative the curve is counter-clockwise.
//			endCoord = e.getStartPoint();
//			beginCoord = e.getEndPoint();
//			xDiff = endCoord.getxCoord() - beginCoord.getxCoord();
//			ySum = endCoord.getyCoord() + beginCoord.getyCoord();
//			clockwiseValue += xDiff*ySum;
//		}
//		
//		if(clockwiseValue < 0) return true;
//		else return false;
		
		if(clockwiseContainsTurningpoints == null){//the first time we calculate the value, the following edges can be deducted from this first one
			
			double clockwiseValue = 0;
			
			double xDiff;
			double ySum;
			
			Coordinate endCoord;
			Coordinate beginCoord;
			//If the result is positive the curve is clockwise, if it's negative the curve is counter-clockwise.
			for(Edge e: edgeList){
				//Sum over the edges, (x2-x1)(y2+y1). If the result is positive the curve is clockwise, if it's negative the curve is counter-clockwise.
				endCoord = e.getEndPoint();
				beginCoord = e.getStartPoint();
				xDiff = endCoord.getxCoord() - beginCoord.getxCoord();
				ySum = endCoord.getyCoord() + beginCoord.getyCoord();
				clockwiseValue += xDiff*ySum;
			}
			//use a helpedge to close the figure to be sure of the direction
			beginCoord = edgeList.get(edgeList.size()-1).getEndPoint();
			endCoord = edgeList.get(0).getStartPoint();
			xDiff = endCoord.getxCoord() - beginCoord.getxCoord();
			ySum = endCoord.getyCoord() + beginCoord.getyCoord();
			clockwiseValue += xDiff*ySum;
			
			if(clockwiseValue > 0) clockwiseContainsTurningpoints = true;
			else clockwiseContainsTurningpoints = false;
			
		}
		//if the edges that contain turningpoints are clockwise, a next edge that contains a turningpoint will also be clockwise
		//the edges that don't contain turningpoints will be counterclockwise
		if(clockwiseContainsTurningpoints){
			if(edgeList.get(0).isTurningPoint()){
				return true;
			}
			else return false;
		}
		else{
			if(edgeList.get(0).isTurningPoint()){
				return false;
			}
			else return true;
		}
		
	}

	private static void calcDeltaAngles(List<Edge> polySortList) {
		
		for(int i = 0; i< polySortList.size();i++){
			
			if(i > 0){
				polySortList.get(i).setDeltaAngle(polySortList.get(i).getEdgeAngle() - polySortList.get(i-1).getEdgeAngle());
			}
			else{
				polySortList.get(i).setDeltaAngle(polySortList.get(i).getEdgeAngle() - polySortList.get(polySortList.size()-1).getEdgeAngle());
			}
			if(polySortList.get(i).getDeltaAngle() > Math.PI){
				polySortList.get(i).setDeltaAngle(polySortList.get(i).getDeltaAngle() - 2* Math.PI);
			}
			else if(polySortList.get(i).getDeltaAngle() < -Math.PI){
				
				polySortList.get(i).setDeltaAngle(polySortList.get(i).getDeltaAngle() + 2* Math.PI);
			}
		}
		for(int i = 0; i< polySortList.size();i++){
//			if(i > 0){
//				if(Math.signum(polySortList.get(i).getDeltaAngle())!= Math.signum(polySortList.get(i-1).getDeltaAngle())){
//					polySortList.get(i).setTurningPoint(true);
//				}
//			}
//			else{
//				if(Math.signum(polySortList.get(i).getDeltaAngle())!= Math.signum(polySortList.get(polySortList.size()-1).getDeltaAngle())){
//					polySortList.get(i).setTurningPoint(true);
//				}
//			}
			if(Math.signum(polySortList.get(i).getDeltaAngle())!= Math.signum(polySortList.get((i+1)%polySortList.size()).getDeltaAngle())){
				polySortList.get(i).setTurningPoint(true);
			}
		}
	}
	
	private static List<Edge> MinkPos(List<Edge> qList, List<Edge> rList) {
		
		List<Edge> mergeList = new ArrayList <> ();
		mergeList.addAll(qList);
		mergeList.addAll(rList);
		Collections.sort(mergeList, new EdgeAngleComparator());
	
		if(printEdgeListData){
			System.out.println();
			System.out.println("qList");
			printSimpleEdgeList(qList);
			System.out.println("rList");
			printSimpleEdgeList(rList);
			System.out.println("mergelist:");
			printSimpleEdgeList(mergeList);
		}
		
		List<Edge> sList = new ArrayList<>();
		
		int i = 0;
		int direction = 1;
		int bCount = 0;
		if(qList.get(0).isTurningPoint()){
			direction = -1;
		}
		
		Edge helpEdge;
		Edge qi;
		boolean qiFound = false;
		
//		if(qList.get(0).isTurningPoint()){
//			direction = -1;
//		}
		sList.add(new Edge(qList.get(0)));
		int mergeListStartPosition = 0;
		while(!mergeList.get(mergeListStartPosition).isPolygonA() || mergeList.get(mergeListStartPosition).getEdgeNumber() != qList.get(0).getEdgeNumber()){
			mergeListStartPosition++;
		}
		int checkPos;
		int position = mergeListStartPosition;
//		System.out.println(mergeListStartPosition);
		boolean toStep4 = false;
		do{
			i = (i+1)%qList.size();
			qi = new Edge(qList.get(i));
			qiFound = false;
			
			if(direction>0){
//				System.out.println("positive direction");
				//moving forward through mergeList looking for Qi
				
				while(!qiFound){
//					System.out.println("position: " + position);
					//if from R
					if(!mergeList.get(position).isPolygonA()){
						
						helpEdge = new Edge(mergeList.get(position));
						helpEdge.changeEdgeNumber(direction);
//						System.out.println("inserting edge in sList: b" + helpEdge.getEdgeNumber());
//						System.out.println("edgeOrigin: b" + mergeList.get(position).getEdgeNumber());
						bCount++;
						sList.add(helpEdge);
					}
					else{//if from Q
						if(mergeList.get(position).getEdgeNumber() == qi.getEdgeNumber()){
							qiFound = true;
							if(i==0){
//								System.out.println("go to step 4");
								toStep4 = true;
							}
							else{
								
								
								if(qi.isTurningPoint()){
									boolean sameAngle = true;
									boolean hasSameAngleB = false;
									int z = 0;
									do{
										z++;
										checkPos = (position+z)%mergeList.size();
										if(Math.round(Math.toDegrees(mergeList.get(checkPos).getEdgeAngle()))==
												Math.round(Math.toDegrees(mergeList.get(position).getEdgeAngle()))){
											if(!mergeList.get(checkPos).isPolygonA()){
												helpEdge = new Edge(mergeList.get(checkPos));
												helpEdge.changeEdgeNumber(direction);
												sList.add(helpEdge);
												bCount++;
	//											System.out.println("extra add");
	//											System.out.println("inserting edge in sList: b" + helpEdge.getEdgeNumber());
												hasSameAngleB = true;
											}
										}
										else sameAngle = false;
									}while(sameAngle);
									
//									System.out.println("inserting edge in sList: a" + qi.getEdgeNumber());
									sList.add(qi);
									
									direction = -1*direction;
									
//									System.out.println("qi is turningpoint: " + direction );
									if(hasSameAngleB){
										z = 0;
										sameAngle = true;
										do{
											z++;
											checkPos = (position+z)%mergeList.size();
											if(Math.round(Math.toDegrees(mergeList.get(checkPos).getEdgeAngle()))==
													Math.round(Math.toDegrees(mergeList.get(position).getEdgeAngle()))){
												if(!mergeList.get(checkPos).isPolygonA()){
													helpEdge = new Edge(mergeList.get(checkPos));
													helpEdge.changeEdgeNumber(direction);
													sList.add(helpEdge);
													bCount++;
		//											System.out.println("extra add");
		//											System.out.println("inserting edge in sList: b" + helpEdge.getEdgeNumber());
													hasSameAngleB = true;
												}
											}
											else sameAngle = false;
										}while(sameAngle);
									}
								}
								else{
//									System.out.println("inserting edge in sList: a" + qi.getEdgeNumber());
									sList.add(qi);
								}
//								System.out.println("repeat step 3");
								
							}
						}
					}
					
					position = (position+direction)%mergeList.size();
					if(position<0){
						position = mergeList.size()-1;
					}
				}
				
			}
			else if(direction<0){
//				System.out.println("negative direction");
				//moving backwards through mergeList looking for Qi
				while(!qiFound){
//					System.out.println("position: " + position);
					if(!mergeList.get(position).isPolygonA()){
						helpEdge = new Edge(mergeList.get(position));
						helpEdge.changeEdgeNumber(direction);
//						System.out.println("direction: " + direction);
//						System.out.println("inserting edge in sList: b" + helpEdge.getEdgeNumber());
//						System.out.println("inserting edge in sList: b" + helpEdge.getEdgeNumber());
//						System.out.println("edgeOrigin: b" + mergeList.get(position).getEdgeNumber());
						sList.add(helpEdge);
						bCount++;
					}
					else{//if from Q
						if(mergeList.get(position).getEdgeNumber() == qi.getEdgeNumber()){
							qiFound = true;
							if(i==0){
//								System.out.println("go to step 4");
								toStep4 = true;
							}
							else{
								
								if(qi.isTurningPoint()){
									int z = 0;
									
									boolean sameAngle = true;
									boolean hasSameAngleB = false;
									do{
										z++;
										if(position-z<0){
											checkPos = mergeList.size()+(position-z);
										}
										else{
											checkPos = (position-z);
										}
										if(Math.round(Math.toDegrees(mergeList.get(checkPos).getEdgeAngle()))==
												Math.round(Math.toDegrees(mergeList.get(position).getEdgeAngle()))){
											if(!mergeList.get(checkPos).isPolygonA()){

												helpEdge = new Edge(mergeList.get(checkPos));
												helpEdge.changeEdgeNumber(direction);
//												System.out.println("extra add");
//												System.out.println("inserting edge in sList: b" + helpEdge.getEdgeNumber());
												sList.add(helpEdge);
												bCount++;
												hasSameAngleB = true;
											}
										}
										else{
											sameAngle = false;
										}
									}while(sameAngle);
									
//									System.out.println("inserting edge in sList: a" + qi.getEdgeNumber());
									sList.add(qi);
									
									direction= -1*direction;
									
//									System.out.println("qi is turningpoint: " + direction );
									if(hasSameAngleB){
										z = 0;
										sameAngle = true;
										do{
											z++;
											if(position-z<0){
												checkPos = mergeList.size()+(position-z);
											}
											else{
												checkPos = (position-z);
											}
											
											if(Math.round(Math.toDegrees(mergeList.get(checkPos).getEdgeAngle()))==
													Math.round(Math.toDegrees(mergeList.get(position).getEdgeAngle()))){
												if(!mergeList.get(checkPos).isPolygonA()){

													helpEdge = new Edge(mergeList.get(checkPos));
													helpEdge.changeEdgeNumber(direction);
//													System.out.println("extra add");
//													System.out.println("inserting edge in sList: b" + helpEdge.getEdgeNumber());
													sList.add(helpEdge);
													bCount++;
													hasSameAngleB = true;
												}
											}
											else{
												sameAngle = false;
											}
										}while(sameAngle);
										
										
									}
								}
								else{
//									System.out.println("inserting edge in sList: a" + qi.getEdgeNumber());
									sList.add(qi);
								}
//								System.out.println("repeat step 3");
							}
						}
					}
					position= (position + direction)%mergeList.size();
//					System.out.println("direction: " + direction);
//					System.out.println("new position: " + position);
					if(position < 0){
						position = mergeList.size()-1;
					}
				}
			}
			
		}while(i!=0&&!toStep4);
		
		Edge startingEdge = rList.get(0);
		
		i = 0;
		
		int start = 0;
		boolean startFound = false;
		for(Edge e: sList){
			if(!e.isPolygonA()&&e.getEdgeNumber()==startingEdge.getEdgeNumber()){
				start = i;
				startFound = true;
			}
			if(startFound && e.getEdgeNumber() == startingEdge.getEdgeNumber()*-1){
				break;
			}
			i++;
		}
		
		//i is the position of r0 in the mergeList
		i =start;
		int j = 0;
		int next = 1;
		
		direction = 1;
		List<Edge> seqList = new ArrayList<>();
		Edge si = new Edge(sList.get(i));
		seqList.add(si);
		bCount--;
//		countRList.set(0, countRList.get(next)+1);//r0 is added => count+1
		while(bCount>0 || j<sList.size()-1){
//		while(j<sList.size()-1){
			i = (i+1)%sList.size();
			
			si = new Edge(sList.get(i));
			
			if(si.isPolygonA()){
				j = j+1;
				seqList.add(si);
				
				if(si.isTurningPoint()){
					direction*=-1;
					next = next+direction;
					if(next<0){
//						System.out.println("next is: " + next);
						next = rList.size()-1;
//						System.out.println("next is " + next);
					}
					if(next>rList.size()-1){
//						System.out.println("next is: " + next);
						next = 0;
						
					}
//					System.out.println("next is " + next + " in direction " + direction);
				}
			}
			else if(si.getEdgeNumber() == direction * rList.get(next).getEdgeNumber()){
				j = j+1;
				bCount--;
				seqList.add(si);
//				System.out.println("added b" + si.getEdgeNumber());
				next = next+direction;
				if(next<0){
//						System.out.println("next is: " + next);
					next = rList.size()-1;
//						System.out.println("changed to: " + next);
				}
				if(next>rList.size()-1){
//						System.out.println("next is: " + next);
					next = 0;
//						System.out.println("changed to: " + next);
				}
//				System.out.println("next is " + next + " in direction " + direction);
			}
			
		}

		if(printEdgeListData){
			System.out.println("seqList");
			printSimpleEdgeList(seqList);
		}
		
		return seqList;
		
	}
	
	//we work with aList and bList in this method because we don't want to reverse the original (MinkPos doesn't need reversal)
	private static List<Edge> MinkNeg(List<Edge> aList, List<Edge> bList) {
		
		List<Edge> mergeList = new ArrayList <> ();
		mergeList.addAll(aList);
		mergeList.addAll(bList);
		
		List<Edge> qList = new ArrayList<>();
		qList.addAll(aList);
		
		List<Edge> rList = new ArrayList<>();
		rList.addAll(bList);
		
		Collections.sort(mergeList, new EdgeAngleComparator());
		Collections.reverse(mergeList);
		Collections.reverse(qList);
		
//		Collections.reverse(rList);//danger zone
		if(printEdgeListData){
			System.out.println();
			System.out.println("qList");
			printSimpleEdgeList(aList);
			System.out.println("rList");
			printSimpleEdgeList(bList);
			
			System.out.println("mergelist:");
			printSimpleEdgeList(mergeList);
		}
		
		List<Edge> sList = new ArrayList<>();
		
		int i = 0;
		int direction = 1;
		
		direction = 1;
		
		Edge helpEdge;
		Edge qi;
		boolean qiFound = false;
		
		if(qList.get(0).isTurningPoint()){
			direction = -1;
		}
		
		sList.add(new Edge(qList.get(0)));
		int mergeListStartPosition = 0;
		while(!mergeList.get(mergeListStartPosition).isPolygonA() || mergeList.get(mergeListStartPosition).getEdgeNumber() != qList.get(0).getEdgeNumber()){
			mergeListStartPosition++;
		}
		int checkPos;
		int position = mergeListStartPosition;
//		System.out.println(mergeListStartPosition);
		boolean toStep4 = false;
		int bCount = 0;
		do{
			i = (i+1)%qList.size();
			qi = new Edge(qList.get(i));
			qiFound = false;
			
			if(direction>0){
//				System.out.println("positive direction");
				//moving forward through mergeList looking for Qi
				
				while(!qiFound){
//					System.out.println("position: " + position);
					//if from R
					if(!mergeList.get(position).isPolygonA()){
						
						helpEdge = new Edge(mergeList.get(position));
						helpEdge.changeEdgeNumber(direction);
//						System.out.println("inserting edge in sList: b" + helpEdge.getEdgeNumber());
//						System.out.println("edgeOrigin: b" + mergeList.get(position).getEdgeNumber());
						bCount++;
						sList.add(helpEdge);
					}
					else{//if from Q
						if(mergeList.get(position).getEdgeNumber() == qi.getEdgeNumber()){
							qiFound = true;
							if(i==0){
//								System.out.println("go to step 4");
								toStep4 = true;
							}
							else{
								
								
								if(qi.isTurningPoint()){
									boolean sameAngle = true;
									boolean hasSameAngleB = false;
									int z = 0;
									do{
										z++;
										checkPos = (position+z)%mergeList.size();
										if(Math.round(Math.toDegrees(mergeList.get(checkPos).getEdgeAngle()))==
												Math.round(Math.toDegrees(mergeList.get(position).getEdgeAngle()))){
											if(!mergeList.get(checkPos).isPolygonA()){
												helpEdge = new Edge(mergeList.get(checkPos));
												helpEdge.changeEdgeNumber(direction);
												sList.add(helpEdge);
												bCount++;
	//											System.out.println("extra add");
	//											System.out.println("inserting edge in sList: b" + helpEdge.getEdgeNumber());
												hasSameAngleB = true;
											}
										}
										else sameAngle = false;
									}while(sameAngle);
									
//									System.out.println("inserting edge in sList: a" + qi.getEdgeNumber());
									sList.add(qi);
									
									direction = -1*direction;
									
//									System.out.println("qi is turningpoint: " + direction );
									if(hasSameAngleB){
										z = 0;
										sameAngle = true;
										do{
											z++;
											checkPos = (position+z)%mergeList.size();
											if(Math.round(Math.toDegrees(mergeList.get(checkPos).getEdgeAngle()))==
													Math.round(Math.toDegrees(mergeList.get(position).getEdgeAngle()))){
												if(!mergeList.get(checkPos).isPolygonA()){
													helpEdge = new Edge(mergeList.get(checkPos));
													helpEdge.changeEdgeNumber(direction);
													sList.add(helpEdge);
													bCount++;
		//											System.out.println("extra add");
		//											System.out.println("inserting edge in sList: b" + helpEdge.getEdgeNumber());
													hasSameAngleB = true;
												}
											}
											else sameAngle = false;
										}while(sameAngle);
									}
								}
								else{
//									System.out.println("inserting edge in sList: a" + qi.getEdgeNumber());
									sList.add(qi);
								}
//								System.out.println("repeat step 3");
								
							}
						}
					}
					
					position = (position+direction)%mergeList.size();
					if(position<0){
						position = mergeList.size()-1;
					}
				}
				
			}
			else if(direction<0){
//				System.out.println("negative direction");
				//moving backwards through mergeList looking for Qi
				while(!qiFound){
//					System.out.println("position: " + position);
					if(!mergeList.get(position).isPolygonA()){
						helpEdge = new Edge(mergeList.get(position));
						helpEdge.changeEdgeNumber(direction);
//						System.out.println("direction: " + direction);
//						System.out.println("inserting edge in sList: b" + helpEdge.getEdgeNumber());
//						System.out.println("inserting edge in sList: b" + helpEdge.getEdgeNumber());
//						System.out.println("edgeOrigin: b" + mergeList.get(position).getEdgeNumber());
						sList.add(helpEdge);
						bCount++;
					}
					else{//if from Q
						if(mergeList.get(position).getEdgeNumber() == qi.getEdgeNumber()){
							qiFound = true;
							if(i==0){
//								System.out.println("go to step 4");
								toStep4 = true;
							}
							else{
								
								if(qi.isTurningPoint()){
									int z = 0;
									
									boolean sameAngle = true;
									boolean hasSameAngleB = false;
									do{
										z++;
										if(position-z<0){
											checkPos = mergeList.size()+(position-z);
										}
										else{
											checkPos = (position-z);
										}
										if(Math.round(Math.toDegrees(mergeList.get(checkPos).getEdgeAngle()))==
												Math.round(Math.toDegrees(mergeList.get(position).getEdgeAngle()))){
											if(!mergeList.get(checkPos).isPolygonA()){

												helpEdge = new Edge(mergeList.get(checkPos));
												helpEdge.changeEdgeNumber(direction);
//												System.out.println("extra add");
//												System.out.println("inserting edge in sList: b" + helpEdge.getEdgeNumber());
												sList.add(helpEdge);
												bCount++;
												hasSameAngleB = true;
											}
										}
										else{
											sameAngle = false;
										}
									}while(sameAngle);
									
//									System.out.println("inserting edge in sList: a" + qi.getEdgeNumber());
									sList.add(qi);
									
									direction= -1*direction;
									
//									System.out.println("qi is turningpoint: " + direction );
									if(hasSameAngleB){
										z = 0;
										sameAngle = true;
										do{
											z++;
											if(position-z<0){
												checkPos = mergeList.size()-(position-z);
											}
											else{
												checkPos = (position-z);
											}
											
											if(Math.round(Math.toDegrees(mergeList.get(checkPos).getEdgeAngle()))==
													Math.round(Math.toDegrees(mergeList.get(position).getEdgeAngle()))){
												if(!mergeList.get(checkPos).isPolygonA()){

													helpEdge = new Edge(mergeList.get(checkPos));
													helpEdge.changeEdgeNumber(direction);
//													System.out.println("extra add");
//													System.out.println("inserting edge in sList: b" + helpEdge.getEdgeNumber());
													sList.add(helpEdge);
													bCount++;
													hasSameAngleB = true;
												}
											}
											else{
												sameAngle = false;
											}
										}while(sameAngle);
										
										
									}
								}
								else{
//									System.out.println("inserting edge in sList: a" + qi.getEdgeNumber());
									sList.add(qi);
								}
//								System.out.println("repeat step 3");
							}
						}
					}
					position= (position + direction)%mergeList.size();
//					System.out.println("direction: " + direction);
//					System.out.println("new position: " + position);
					if(position < 0){
						position = mergeList.size()-1;
					}
				}
			}
			
		}while(i!=0&&!toStep4);
		
		Edge startingEdge = rList.get(0);
		
		i = 0;
		
//		System.out.println("rList");
//		printSimpleEdgeList(rList);
//		
//		System.out.println("sList");
//		printSimpleEdgeList(sList);
		
		
//		for(Edge e: sList){
//			if(!e.isPolygonA()&&e.getEdgeNumber()==startingEdge.getEdgeNumber()){
//				break;
//			}
//			i++;
//		}
		
		int start = 0;
		boolean startFound = false;
		for(Edge e: sList){
			if(!e.isPolygonA()&&e.getEdgeNumber()==startingEdge.getEdgeNumber()){
				start = i;
				startFound = true;
			}
			if(startFound && e.getEdgeNumber() == startingEdge.getEdgeNumber()*-1){
				break;
			}
			i++;
		}
		
		//i is the position of r0 in the mergeList
		i =start;
		int j = 0;
		int next = 1;
		
		direction = 1;
		List<Edge> seqList = new ArrayList<>();
		Edge si = new Edge(sList.get(i));
		seqList.add(si);
		bCount--;
		while(bCount>0 || j<sList.size()-1){
//		while(j<sList.size()-1){
			i = (i+1)%sList.size();
			
			si = new Edge(sList.get(i));
			
			if(si.isPolygonA()){
				j = j+1;
				seqList.add(si);
				
				if(si.isTurningPoint()){
					direction*=-1;
					next = next+direction;
					if(next<0){
//						System.out.println("next is: " + next);
						next = rList.size()-1;
//						System.out.println("next is " + next);
					}
					if(next>rList.size()-1){
//						System.out.println("next is: " + next);
						next = 0;
						
					}
//					System.out.println("next is " + next + " in direction " + direction);
				}
			}
			else if(si.getEdgeNumber() == direction * rList.get(next).getEdgeNumber()){
				j = j+1;
				bCount--;
				seqList.add(si);
//				System.out.println("added b" + si.getEdgeNumber());
				next = next+direction;
				if(next<0){
//						System.out.println("next is: " + next);
					next = rList.size()-1;
//						System.out.println("changed to: " + next);
				}
				if(next>rList.size()-1){
//						System.out.println("next is: " + next);
					next = 0;
//						System.out.println("changed to: " + next);
				}
//				System.out.println("next is " + next + " in direction " + direction);
			}
			
		}
		for(Edge e: seqList){
			if(e.isPolygonA()){
				e.changeEdgeNumber(-1);
			}
		}

		if(printEdgeListData){
			System.out.println("seqList");
			printSimpleEdgeList(seqList);
		}
		
		return seqList;
		
	}
	
	private static void linkEdgeLists(List<Edge> msEdgeList, List<Edge> nextEdgeList, List<Edge> polyASortList, boolean closePolygon) {
		
		boolean printData = false;
		
		int i = 0;
		Edge helpEdge;
		
		while(!nextEdgeList.get(i).isPolygonA()){
			i++;
		}
		int endLinkEdgeANumber = nextEdgeList.get(i).getEdgeNumber();
		
		boolean excessiveEdgesAPositive = true;//these edges will be removed but their sign is important for linking
		int firstExcessiveEdgeANumber = 0;
		i = msEdgeList.size()-1;
		
		if(msEdgeList.get(i).getEdgeNumber()<0&&msEdgeList.get(i).isPolygonA())excessiveEdgesAPositive = false;
		while(msEdgeList.get(i).isPolygonA() && !closePolygon){//remove the excessive a edges after the last b edge
			firstExcessiveEdgeANumber = msEdgeList.get(i).getEdgeNumber();
			msEdgeList.remove(i);
			i--;
		}
		while(i>=0 && !msEdgeList.get(i).isPolygonA()){
			i--;
		}
		int startLinkEdgeANumber;
		if(i==-1){//if msEdgeList doesn't contain any A edges that aren't excessive we have to deduct the edgenumber from the excessive values
			if(excessiveEdgesAPositive){
				startLinkEdgeANumber = firstExcessiveEdgeANumber-1;
				if(startLinkEdgeANumber<1)startLinkEdgeANumber = polyASortList.size();
			}
			else{
				startLinkEdgeANumber = Math.abs(firstExcessiveEdgeANumber)+1;
				if(startLinkEdgeANumber>polyASortList.size())startLinkEdgeANumber = 1;
				startLinkEdgeANumber*=-1;
			}
		}
		else startLinkEdgeANumber = msEdgeList.get(i).getEdgeNumber();
		
		if(excessiveEdgesAPositive){//linking edges are negative if the excessive edges were positive
			
			if(startLinkEdgeANumber>0 && endLinkEdgeANumber>0 ){
				//last edgelist had postive a values, and new edgeList too
				/*
				 * f.e. first ends with a11 and next starts with a7 
				 * link: -a11 -a10 -a9 -a8 -a7
				 * 
				 */
				i = polyASortList.size()-1;
				//find the place of the start edgeA in the polyAlist
				while(polyASortList.get(i).getEdgeNumber()> startLinkEdgeANumber){
					i--;
				}
				boolean isReached = false;
				//descending the edges of A untill the finish edge is reached
				while(!isReached){
					
					helpEdge = new Edge(polyASortList.get(i), true);
					if(helpEdge.getEdgeNumber()== endLinkEdgeANumber)isReached = true;
					helpEdge.setEdgeNumber(helpEdge.getEdgeNumber()*-1);
					msEdgeList.add(helpEdge);
					i--;
					if(i<0)i = polyASortList.size()-1;
				}
				if(!closePolygon)msEdgeList.addAll(nextEdgeList);
			}
			
			else if( startLinkEdgeANumber>0 && endLinkEdgeANumber < 0 ){
				/*
				 * f.e. first ends  with a10 next starts with -a4
				 * link: -a10 -a9 -a8 -a7 -a6 -a5
				 */
				
				i = polyASortList.size()-1;
				//find the place of the start edgeA in the polyAlist
				while(polyASortList.get(i).getEdgeNumber()> startLinkEdgeANumber){
					i--;
				}
				boolean isReached = false;
				//descending the edges of A untill the finish edge is reached
				while(!isReached){
					
					helpEdge = new Edge(polyASortList.get(i), true);
					if(helpEdge.getEdgeNumber()== endLinkEdgeANumber*-1)isReached = true;
					if(!isReached){
						helpEdge.setEdgeNumber(helpEdge.getEdgeNumber()*-1);
						msEdgeList.add(helpEdge);
					}
					i--;
					if(i<0)i = polyASortList.size()-1;
				}
				if(!closePolygon)msEdgeList.addAll(nextEdgeList);
			}
			//-----------------------------------------------------------------------------------
			//these next situations are only used when the previously linked list had no A edges
			//(this means the startLinkEdge is from an older list)
			else if(startLinkEdgeANumber < 0 && endLinkEdgeANumber > 0){
				/*
				 * f.e. first ends  with -a10 next starts with a4
				 * link: -a9 -a8 -a7 -a6 -a5 -a4
				 */
				
				i = polyASortList.size()-1;
				//find the place of the start edgeA in the polyAlist
				while(polyASortList.get(i).getEdgeNumber()> startLinkEdgeANumber*-1){
					i--;
				}
				i--;
				if(i<0)i = polyASortList.size()-1;
				
				boolean isReached = false;
				//descending the edges of A untill the finish edge is reached
				while(!isReached){
					
					helpEdge = new Edge(polyASortList.get(i), true);
					if(helpEdge.getEdgeNumber()== endLinkEdgeANumber)isReached = true;
					helpEdge.setEdgeNumber(helpEdge.getEdgeNumber()*-1);
					msEdgeList.add(helpEdge);
					i--;
					if(i<0)i = polyASortList.size()-1;
				}
				if(!closePolygon)msEdgeList.addAll(nextEdgeList);
			}
			else if(startLinkEdgeANumber < 0 && endLinkEdgeANumber < 0){
				/*
				 * f.e. first ends  with -a10 next starts with -a4
				 * link: -a9 -a8 -a7 -a6 -a5
				 */
				
				i = polyASortList.size()-1;
				//find the place of the start edgeA in the polyAlist
				while(polyASortList.get(i).getEdgeNumber()> startLinkEdgeANumber*-1){
					i--;
				}
				i--;
				if(i<0)i = polyASortList.size()-1;
				
				boolean isReached = false;
				//descending the edges of A untill the finish edge is reached
				while(!isReached){
					
					helpEdge = new Edge(polyASortList.get(i), true);
					if(helpEdge.getEdgeNumber()== endLinkEdgeANumber*-1)isReached = true;
					if(!isReached){
						helpEdge.setEdgeNumber(helpEdge.getEdgeNumber()*-1);
						msEdgeList.add(helpEdge);
					}
					i--;
					if(i<0)i = polyASortList.size()-1;
				}
				if(!closePolygon)msEdgeList.addAll(nextEdgeList);
			}
		}
		else{ //linking edges are positive if the excessive edges were negative
			
			if(startLinkEdgeANumber < 0 && endLinkEdgeANumber > 0){
				/*
				 * f.e. first ends  with -a2 next starts with a6
				 * link: a2 a3 a4 a5
				 */
				
				i = 0;

				//find the place of the start edgeA in the polyAlist
				while(polyASortList.get(i).getEdgeNumber() < startLinkEdgeANumber*-1){
					i++;
				}
				boolean isReached = false;
				//descending the edges of A untill the finish edge is reached
				while(!isReached){
					
					helpEdge = new Edge(polyASortList.get(i), true);
					if(helpEdge.getEdgeNumber()== endLinkEdgeANumber)isReached = true;
					if(!isReached){
						helpEdge.setEdgeNumber(helpEdge.getEdgeNumber());
						msEdgeList.add(helpEdge);
					}
					i = (i+1)%polyASortList.size();
				}
				if(!closePolygon)msEdgeList.addAll(nextEdgeList);
			}
			else if(startLinkEdgeANumber < 0 && endLinkEdgeANumber < 0){
				/*
				 * f.e. first ends  with -a2 next starts with -a6
				 * link: a2 a3 a4 a5 a6
				 */
				
				i = 0;
				
				//find the place of the start edgeA in the polyAlist
				while(polyASortList.get(i).getEdgeNumber() < startLinkEdgeANumber*-1){
					i++;
				}
				boolean isReached = false;
				//descending the edges of A untill the finish edge is reached
				while(!isReached){
					
					helpEdge = new Edge(polyASortList.get(i), true);
					if(helpEdge.getEdgeNumber()== endLinkEdgeANumber*-1)isReached = true;
					helpEdge.setEdgeNumber(helpEdge.getEdgeNumber());
					msEdgeList.add(helpEdge);
					i = (i+1)%polyASortList.size();
					
				}
				if(!closePolygon)msEdgeList.addAll(nextEdgeList);
			}
			//-----------------------------------------------------------------------------------
			//these next situations are only used when the previously linked list had no A edges
			//(this means the startLinkEdge is from an older list)
			else if(startLinkEdgeANumber > 0 && endLinkEdgeANumber > 0){
				/*
				 * f.e. first ends  with a2 next starts with a5
				 * link: a3 a4
				 */
				i = 0;

				//find the place of the start edgeA in the polyAlist
				while(polyASortList.get(i).getEdgeNumber() < startLinkEdgeANumber){
					i++;
				}
				i = (i+1)%polyASortList.size();
				boolean isReached = false;
				//descending the edges of A untill the finish edge is reached
				while(!isReached){
					
					helpEdge = new Edge(polyASortList.get(i), true);
					if(helpEdge.getEdgeNumber()== endLinkEdgeANumber)isReached = true;
					if(!isReached){
						helpEdge.setEdgeNumber(helpEdge.getEdgeNumber());
						msEdgeList.add(helpEdge);
					}
					i = (i+1)%polyASortList.size();
				}
				if(!closePolygon)msEdgeList.addAll(nextEdgeList);
			}
			else if(startLinkEdgeANumber > 0 && endLinkEdgeANumber < 0){
				/*
				 * f.e. first ends  with a2 next starts with -a5
				 * link: a3 a4 a5
				 */
				i = 0;

				//find the place of the start edgeA in the polyAlist
				while(polyASortList.get(i).getEdgeNumber() > startLinkEdgeANumber){
					i++;
				}
				i = (i+1)%polyASortList.size();
				boolean isReached = false;
				//descending the edges of A untill the finish edge is reached
				while(!isReached){
					
					helpEdge = new Edge(polyASortList.get(i), true);
					if(helpEdge.getEdgeNumber()== endLinkEdgeANumber*-1)isReached = true;
					helpEdge.setEdgeNumber(helpEdge.getEdgeNumber());
					msEdgeList.add(helpEdge);
					i = (i+1)%polyASortList.size();
				}
				if(!closePolygon)msEdgeList.addAll(nextEdgeList);
			}
		}
		
		
	}
	
	//this method removes all negative edges and splits the polygon in track line trips
	private static List<List<Edge>> makeTrackLineTrips(List<Edge> mList) {
		
		int i = 0; //number of Minkowski sums obtained
		int j = 0; //number of track line trips with nj number of edges in track line trip j;
		int k = 0; //index of each track line trip
		
		List<List<Edge>> trackLineTripList = new ArrayList <>();
		List<Edge> trackLineTrip = new ArrayList<>();
		
		Edge tjk;
		
		boolean positiveFound = true;
		
		while(positiveFound){

			while(i < mList.size() && (mList.get(i).isAdditional() || mList.get(i).getEdgeNumber()<0)){
				i++;
			}
			if(i < mList.size()){//gevonden
				trackLineTrip = new ArrayList<>();
				trackLineTripList.add(trackLineTrip);
				tjk = mList.get(i);
				trackLineTrip.add(tjk);
				i++;
				k++;
				
				while(i< mList.size() && !mList.get(i).isAdditional() && mList.get(i).getEdgeNumber()>0){
					tjk = mList.get(i);
					trackLineTrip.add(tjk);
					i++;
					k++;
				}
				
			}
			else{
				positiveFound = false; 
			}
		}
		return trackLineTripList;
		
	}
	
	private static List<List<Edge>> boundarySearch(List<List<Edge>> trackLineTripList){
		
		int stuckIterator = 100;
		
		List<List<TripIntersection>> allTripIntersectionList = new ArrayList<>();
		List<TripIntersection> intersectionList;
		
		List<Edge>trackLineTrip;
		List<Edge>trackLineTripJ;
		List<Edge>trackLineTripI;
		
		Coordinate intersectionPoint;
		
		//if we have 1 fragment for calculating the NFP
		if(trackLineTripList.size()==1){
			List<Edge> trip = trackLineTripList.get(0);
			int s = 0;
			for(Edge e: trip){
				e.setTripSequenceNumber(s);
				s++;
			}
			
			if(trip.get(0).getStartPoint().equals(trip.get(trip.size()-1).getEndPoint())){
				return trackLineTripList;
			}
		}
			
		for(List<Edge> tl: trackLineTripList){
			int i = 0;
			for(Edge e: tl){
				e.setTripSequenceNumber(i);
				i++;
			}
		}

		for(int i = 0; i < trackLineTripList.size(); i++){
			intersectionList = new ArrayList<>();

			for(int j = 0; j< trackLineTripList.size();j++){
					
				trackLineTripI = trackLineTripList.get(i);
				trackLineTripJ = trackLineTripList.get(j);

//					System.out.println("J");
//					printEdgeList(trackLineTripJ);
//					System.out.println("I");
//					printEdgeList(trackLineTripI);
				
				Edge edgeK;
				Edge edgeR;
				
				for(int k = 0; k < trackLineTripI.size(); k++){
					for(int r = 0; r< trackLineTripJ.size(); r++){
						edgeK = trackLineTripI.get(k);
						edgeR = trackLineTripJ.get(r);
						
						/*
						if(edgeK.testIntersect(edgeR)){
							
							intersectionPoint = edgeK.calcIntersection(edgeR);
							
							if(edgeR.getStartPoint().dFunction(edgeK)>0){
								intersectionList.add(new TripIntersection(intersectionPoint,edgeK, true ));
							}
							else {
								intersectionList.add(new TripIntersection(intersectionPoint,edgeK, false ));
							}
							
						}
						
						else if(edgeK.getStartPoint().equals(edgeR.getEndPoint())){
							if(r == trackLineTripJ.size()-1){
								intersectionList.add(new TripIntersection(edgeK.getStartPoint(), edgeK, false));
							}
							else{
								if(trackLineTripJ.get(r+1).getEndPoint().dFunction(edgeK)>0){
									intersectionList.add(new TripIntersection(edgeK.getStartPoint(), edgeK, false));
								}
							}
						}
						else if(edgeK.getEndPoint().equals(edgeR.getStartPoint())){
							if(k==trackLineTripI.size()-1){	
								intersectionList.add(new TripIntersection(edgeK.getEndPoint(), edgeK, true));
							}
							else{
								if(trackLineTripJ.get(r).getEndPoint().dFunction(trackLineTripI.get(k+1))<0){
									intersectionList.add(new TripIntersection(edgeK.getEndPoint(), edgeK, true));
								}
							}
						}
						else if(!edgeR.getStartPoint().equals(edgeK.getStartPoint())&&!edgeR.getStartPoint().equals(edgeK.getEndPoint())
								&&!edgeR.getEndPoint().equals(edgeK.getStartPoint())&&!edgeR.getEndPoint().equals(edgeK.getEndPoint())){
							if(edgeR.containsPoint(edgeK.getStartPoint())){
								if(edgeR.getStartPoint().dFunction(edgeK)>0){
									intersectionList.add(new TripIntersection(edgeK.getStartPoint(),edgeK, true ));
								}
								else if(edgeR.getStartPoint().dFunction(edgeK)<0) {
									intersectionList.add(new TripIntersection(edgeK.getStartPoint(),edgeK, false ));
								}
							}
//							else if(edgeI.containsPoint(edgeJ.getEndPoint())){
//								if(edgeI.getStartPoint().dFunction(edgeJ)>0){
//									intersectionList.add(new TripIntersection(edgeJ.getEndPoint(),edgeJ, true ));
//								}
//								else{
//									intersectionList.add(new TripIntersection(edgeJ.getEndPoint(),edgeJ, false ));
//								}
//							}
							else if(edgeK.containsPoint(edgeR.getStartPoint())){
								if(edgeR.getEndPoint().dFunction(edgeK)<0){
									intersectionList.add(new TripIntersection(edgeR.getStartPoint(),edgeK, false ));
								}
								else if(edgeR.getEndPoint().dFunction(edgeK)>0) {
									intersectionList.add(new TripIntersection(edgeR.getStartPoint(),edgeK, true ));
								}
							}
							else if(edgeK.containsPoint(edgeR.getEndPoint())){
								if(edgeR.getStartPoint().dFunction(edgeK)<0){
									intersectionList.add(new TripIntersection(edgeR.getEndPoint(),edgeK, false ));
								}
								else if(edgeR.getStartPoint().dFunction(edgeK)>0) {
									intersectionList.add(new TripIntersection(edgeR.getEndPoint(),edgeK, true ));
								}
							}
						}
						*/
						if(edgeK.getStartPoint().equals(edgeR.getStartPoint())){
							if(i!=j || k!=r){//if they are from the same trip, they may not be the same edge
								if(edgeK.getEndPoint().dFunction(edgeR)<0){
									intersectionList.add(new TripIntersection(edgeK.getStartPoint(),edgeK, false ));
								}
								else if(edgeK.getEndPoint().dFunction(edgeR)>0);
							}
						}
						else if(edgeK.getStartPoint().equals(edgeR.getEndPoint())){
							if((i!=j || k!=r) && r == trackLineTripJ.size()-1){
								intersectionList.add(new TripIntersection(edgeK.getStartPoint(),edgeK, false ));
							}
						}
						else if(edgeK.getEndPoint().equals(edgeR.getStartPoint())){
							if(i!=j ||(k!= r-1)){//if they are of the same trip, edgeK may not be the one before edgeR
								intersectionList.add(new TripIntersection(edgeK.getEndPoint(),edgeK, true ));
							}	
						}
						else if(edgeK.getEndPoint().equals(edgeR.getEndPoint()));
						
						else if(edgeR.containsPoint(edgeK.getStartPoint())){
							if(edgeK.getEndPoint().dFunction(edgeR)>0);
							else if(edgeK.getEndPoint().dFunction(edgeR)<0){
								intersectionList.add(new TripIntersection(edgeK.getStartPoint(),edgeK, false ));
							}
							else intersectionList.add(new TripIntersection(edgeR.getEndPoint(),edgeK, false ));
						}
						else if(edgeR.containsPoint(edgeK.getEndPoint())){
							if(edgeK.getStartPoint().dFunction(edgeR)>0);	
							else if(edgeK.getStartPoint().dFunction(edgeR)<=0){
								intersectionList.add(new TripIntersection(edgeK.getEndPoint(),edgeK, true ));
							}
						}
						else if(edgeK.containsPoint(edgeR.getStartPoint())){
							if(edgeR.getEndPoint().dFunction(edgeK)>0);
							else if(edgeR.getEndPoint().dFunction(edgeK)<0){
								intersectionList.add(new TripIntersection(edgeR.getStartPoint(),edgeK, true ));
							}
						}
						else if(edgeK.containsPoint(edgeR.getEndPoint())){
							if(edgeR.getStartPoint().dFunction(edgeK)>0);
							else if(edgeR.getStartPoint().dFunction(edgeK)<0){
								intersectionList.add(new TripIntersection(edgeR.getEndPoint(),edgeK, false ));
							}
						}
						else if(edgeK.testIntersect(edgeR)){
							
							intersectionPoint = edgeK.calcIntersection(edgeR);
							
							if(edgeR.getStartPoint().dFunction(edgeK)>0){
								intersectionList.add(new TripIntersection(intersectionPoint,edgeK, true ));
							}
							else if(edgeR.getStartPoint().dFunction(edgeK)<0){
								intersectionList.add(new TripIntersection(intersectionPoint,edgeK, false ));
							}
						}
					}
				}
			}
			Collections.sort(intersectionList, new TripIntersectionComparator());
			
			if(printBoundaryData){
				System.out.println("intersectionList:");
				for(TripIntersection in: intersectionList){
					System.out.println(in);
				}
			}
			
			allTripIntersectionList.add(intersectionList);
			
		}
		
		//step 2
		List<List<Edge>> fragmentList = new ArrayList<>();
		List<Edge> fragment;
		int k = 0;
		for(int i = 0; i<allTripIntersectionList.size(); i++){
//			fragment = new ArrayList<>();
			trackLineTrip = trackLineTripList.get(i);
			intersectionList = allTripIntersectionList.get(i);
			
			for(int j = 0; j< intersectionList.size()-1;j++){
				if(intersectionList.get(j).getIntersectionSign()==false){
					if(intersectionList.get((j+1)%intersectionList.size()).getIntersectionSign()==true){
						
						fragment = new ArrayList<>();
						
						Edge trimmedEdge = new Edge(intersectionList.get(j).getIntersectionEdge());
						trimmedEdge.setStartPoint(intersectionList.get(j).getIntersectionPoint());
						fragment.add(trimmedEdge);
						if(intersectionList.get(j+1).getIntersectionEdge().getTripSequenceNumber()==intersectionList.get(j).getIntersectionEdge().getTripSequenceNumber()){
							trimmedEdge.setEndPoint(intersectionList.get((j+1)%intersectionList.size()).getIntersectionPoint());
						}
						else{
							k=0;
							while(!trackLineTrip.get(k).equalsComplexPolyEdge(intersectionList.get(j).getIntersectionEdge())){
								k = (k+1)%trackLineTrip.size();
							}
							while(!trackLineTrip.get(k).equalsComplexPolyEdge(intersectionList.get((j+1)%intersectionList.size()).getIntersectionEdge())){
								if(!trackLineTrip.get(k).equalsComplexPolyEdge(intersectionList.get(j).getIntersectionEdge())) fragment.add(trackLineTrip.get(k));
								k = (k+1)%trackLineTrip.size();
							}
							if(intersectionList.get(j+1).getIntersectionEdge().getTripSequenceNumber()!=intersectionList.get(j).getIntersectionEdge().getTripSequenceNumber()){
								trimmedEdge = new Edge(trackLineTrip.get(k));
								trimmedEdge.setEndPoint(intersectionList.get((j+1)%intersectionList.size()).getIntersectionPoint());
								if(!trimmedEdge.getEndPoint().equals(trimmedEdge.getStartPoint()))fragment.add(trimmedEdge);
							}
						}
		
						fragmentList.add(fragment);
					}
				}
			}
			
		}
		
		if(printBoundaryData){
			int aantalFragmentEdges = 0;
			for(List<Edge>frag: fragmentList){
				System.out.println("fragment");
				printEdgeList(frag);
				aantalFragmentEdges += frag.size();
			}
			System.out.println(aantalFragmentEdges);
		}
		
		//step 3
//		System.out.println("starting step 3");
		
		int numberOfFragments = fragmentList.size();
		List<Edge> fragI;
		List<Edge> fragJ;
		List<List<Edge>> cycleList = new ArrayList<>();
		List<Edge> cycle;
		
		while(numberOfFragments>0 && stuckIterator>0){
			stuckIterator--;
//			System.out.println("number of fragments: " + numberOfFragments);
			for (int i = 0; i < fragmentList.size(); i++) {
				for (int j = 0; j < fragmentList.size(); j++) {						
					fragI = fragmentList.get(i);
					fragJ = fragmentList.get(j);
					if(fragI.size()!=0&&fragJ.size()!=0){
//							System.out.println("beiden verschillend van 0");
						if(fragI.get(0).getStartPoint().equalValuesRounded(fragJ.get(fragJ.size()-1).getEndPoint())){
//								System.out.println("fragments match!");
							if(fragI.get(fragI.size()-1).getEndPoint().equalValuesRounded(fragJ.get(0).getStartPoint())){
								
//									fragJ.remove(0);
//									fragJ.remove(fragJ.size()-1);
								
								cycle = new ArrayList<>();
								cycle.addAll(fragI);
								cycle.addAll(fragJ);
								
								cycleList.add(cycle);
								
								fragI.clear();
								numberOfFragments--;
								fragJ.clear();	
								numberOfFragments--;
							}
							else{
//									fragI.remove(0);
								fragJ.addAll(fragI);
								
								fragI.clear();
								numberOfFragments--;
							}
						}
						else if(fragI.get(fragI.size()-1).getEndPoint().equalValuesRounded(fragJ.get(0).getStartPoint())){
//								fragJ.remove(0);
							fragI.addAll(fragJ);
							
							fragJ.clear();
							numberOfFragments--;
						}
					}
				}
			}
		}
		if(cycleList.size()==0) numberOfFails++;
		else if(stuckIterator==0){
			System.out.println("stuck");
			numberStuckInfinite++;
		}
		return cycleList;
	}
	
	private static NoFitPolygon makeNFP(List<List<Edge>> cycleList, Coordinate startCoord) {
		
		//find the lowest coordinate of the cycle
		Coordinate translateFrom = cycleList.get(0).get(0).getStartPoint();
		for(List<Edge> edgeList: cycleList){
			for(Edge edge: edgeList){
				if(edge.getStartPoint().getyCoord()< translateFrom.getyCoord()){
					translateFrom = edge.getStartPoint();
				}
				else if(edge.getStartPoint().getyCoord() == translateFrom.getyCoord()){
					if(edge.getStartPoint().getxCoord()<translateFrom.getxCoord()){
						translateFrom = edge.getStartPoint();
					}
				}
			}
		}
	
		Vector translationVector = new Vector(translateFrom, startCoord);
		
		NoFitPolygon nfp = new NoFitPolygon(cycleList, translationVector);
		
		return nfp;
		
	}
	
}
