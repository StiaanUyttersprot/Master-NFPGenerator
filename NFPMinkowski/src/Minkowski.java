import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Minkowski {

	public static NoFitPolygon generateMinkowskiNFP(MultiPolygon polyA, MultiPolygon polyB) {
		
		NoFitPolygon nfp = null;
		
		//---------------------------------------------------------------------------------------------
		//Generate Minkowski sum edge list
		
		//polyA has to be counterclockwise
		if(polyA.checkClockwise(polyA.getOuterPolygon()))polyA.changeClockOrientation(polyA.getOuterPolygon());
		//polyB has to be counterclockwise
		if(polyB.checkClockwise(polyB.getOuterPolygon()))polyB.changeClockOrientation(polyB.getOuterPolygon());
	
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
		
		printEdgeList(polyASortList);
		System.out.println();
		printEdgeList(polyBSortList);
		
		//divide B in groups by turningpoint
		List<List<Edge>> dividedListB = new ArrayList<>();
		List<Edge> turningGroupB = new ArrayList<>();
		
		boolean isConcaveB = false;
		
		for(Edge e: polyBSortList){
			if(e.isTurningPoint()){
				isConcaveB = true;
				break;
			}
		}
		System.out.println(isConcaveB);
		int j = 0;
		if(isConcaveB){
			for(int i = 0; i< polyBSortList.size();i++){
				if(polyBSortList.get(i).isTurningPoint()){
					if(turningGroupB.size()!=0){
//						System.out.println("dividedListB:");
//						printEdgeList(turningGroupB);
						dividedListB.add(turningGroupB);
					}
					turningGroupB = new ArrayList<>();
					
					turningGroupB.add(polyBSortList.get(i));
					
					j = (i+1)%polyBSortList.size();
					//weet niet of het volgende moet in dit algoritme-----------------------------------------
					//if edge with index i+1 is also a turning point, it means these two both are part of a new list
					if(polyBSortList.get(j).isTurningPoint()){
						//increment i, otherwise the first for lus will also make a group for this second edge
						i++;
						turningGroupB.add(polyBSortList.get(j));
						
						//increment j to check the next edge, if that is a turning point, it's a new group
						j = (j+1)%polyBSortList.size();
					}
					//---------------------------------------------------------------------------------------
					while(!polyBSortList.get(j).isTurningPoint()){
						turningGroupB.add(polyBSortList.get(j));
						j = (j+1)%polyBSortList.size();
					}
				}
			}
			dividedListB.add(turningGroupB);
		}
		else{
			for(Edge e: polyBSortList){
				turningGroupB.add(e);
			}
			dividedListB.add(turningGroupB);
		}
		
//		for(List<Edge> edgeList: dividedListB){
//			System.out.println("group: ");
//			for(Edge e: edgeList){
//				System.out.println(e);
//			}
//		}
		List <List<Edge>> seqList = new ArrayList<>();
		List<Edge> msEdgeList = new ArrayList<>();
		
		Edge helpEdge;
		List<Edge> helpEdgeList;
		
		int i = 0;
		System.out.println(dividedListB.size());
		
		for(List<Edge> edgeListB: dividedListB){
//			if(clockwiseB(edgeListB)){
//				helpEdgeList = Mink(polyASortList, edgeListB, false);
//				seqList.add(helpEdgeList);
//				if(seqList.size()==1){
//					msEdgeList.addAll(helpEdgeList);
//				}
//				else{
//					helpEdge = msEdgeList.get(msEdgeList.size()-1);
//					while(!helpEdgeList.get(i).isPolygonA()){
//						i++;
//					}
//					
//				}
//			}
//			else{
				helpEdgeList = Mink2(polyASortList, edgeListB, true);
				seqList.add(helpEdgeList);
				if(seqList.size()==1){
					msEdgeList.addAll(helpEdgeList);
					
				}
				else{
					i = 0;
					while(!helpEdgeList.get(i).isPolygonA()){
						i++;
					}
					System.out.println(helpEdgeList.get(i));
					int edgeAFinishNumber = helpEdgeList.get(i).getEdgeNumber();
					System.out.println(msEdgeList.get(msEdgeList.size()-1));
					int edgeAStartNumber = msEdgeList.get(msEdgeList.size()-1).getEdgeNumber();
					
					i = polyASortList.size()-1;
					//if(countDownNumber < 0)countDownNumber = polyASortList.size() -1;
					while(polyASortList.get(i).getEdgeNumber()> edgeAStartNumber){
						i--;
					}
					boolean isReached = false;
					while(!isReached){
						
						helpEdge = new Edge(polyASortList.get(i));
						if(helpEdge.getEdgeNumber()== edgeAFinishNumber)isReached = true;
						helpEdge.setEdgeNumber(helpEdge.getEdgeNumber()*-1);
						msEdgeList.add(helpEdge);
						i--;
						if(i<0)i = polyASortList.size()-1;
					}
					msEdgeList.addAll(helpEdgeList);
				}
//			}
		}
		System.out.println("msEdgeList: ");
		printSimpleEdgeList(msEdgeList);
		
//		List<Edge> complexPolygonEdgeList = makeIntoPolygon(msEdgeList);
//		System.out.println("complexPolygonEdgeList: ");
//		printEdgeList(complexPolygonEdgeList);
		//---------------------------------------------------------------------------------------------------------------------------------------
//		//algorithm 2
//		List<List<Edge>> trackLineTripList;
//		trackLineTripList = makeTrackLineTrips(msEdgeList);
//		
//		for(List<Edge> trackLineTrip: trackLineTripList){
//			System.out.println("trackLineTrip");
//			for(Edge e: trackLineTrip){
//				System.out.println(e);
//			}
//		}
//		
//		//algorithm 3
//		List<List<Edge>> cycleList;
//		System.out.println("algorithm 3");
//		
//		cycleList = boundarySearch(trackLineTripList);
//		
//		for(List<Edge> cycle: cycleList){
//			System.out.println("cycle");
//			for(Edge e: cycle){
//				System.out.println(e);
//			}
//		}
		
		return nfp;
		
	}

	private static List<Edge> makeIntoPolygon(List<Edge> msEdgeList) {
		List<Vector> msVectorList;
		List<Edge> complexPolygonEdges = new ArrayList<>();
		msVectorList = getVectorList(msEdgeList);
		int startIndex = 0;
		Coordinate startCoord;
		for(int i = 0; i< msEdgeList.size();i++){
			if(msEdgeList.get(i).getEdgeNumber() == 1 && !msEdgeList.get(i).isPolygonA()){
				startIndex = i;
				break;
			}
		}
		startCoord = new Coordinate(msEdgeList.get(startIndex).getStartPoint());
		startCoord.replaceByNegative();
		System.out.println("startCoord: " + startCoord);
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
			System.out.println();
			System.out.println("edge: " + msEdgeList.get(index));
			System.out.println("vector: " + msVectorList.get(index).toString());
			System.out.println("startPoint: " + startPoint);
			
			complexEdge = startPoint.getEdgeWithTranslation(msVectorList.get(index), msEdgeList.get(index));
			
			System.out.println("new edge: " + complexEdge);
			complexPolygonEdges.add(complexEdge);
			index = (index+1)%msEdgeList.size();
		}
		return complexPolygonEdges;
	}

	private static List<Vector> getVectorList(List<Edge> msEdgeList) {
		List<Vector> vectorList = new ArrayList<Vector>();
		for(Edge e: msEdgeList){
			vectorList.add(e.makeFullVector(e.getEdgeNumber()));
		}
		return vectorList;
	}

	private static List<Edge> Mink(List<Edge> qList, List<Edge> rList, boolean positive) {
		
		System.out.println();
		System.out.println("qList");
		printSimpleEdgeList(qList);
		System.out.println("rList");
		printSimpleEdgeList(rList);
		
		List<Edge> mergeList = new ArrayList <> ();
		mergeList.addAll(qList);
		mergeList.addAll(rList);
		Collections.sort(mergeList, new EdgeAngleComparator());
	
		System.out.println("mergelist:");
		printSimpleEdgeList(mergeList);
		
		List<Edge> sList = new ArrayList<>();
		
		int i = 0;
		int k = 0;
		int direction = 1;
		
		Edge helpEdge;
		Edge qi;
		if(qList.get(0).isTurningPoint()){
			direction = -1;
		}
		sList.add(qList.get(0));
		int position = 0;
		
		do{
			i = (i+1)%qList.size();
			qi = qList.get(i);
			if(direction>0){
				//moving forward through mergeList looking for Qi
				System.out.println("position: " + position);
				for(int j = position; j < mergeList.size();j++){
					//if from R
					if(!mergeList.get(j).isPolygonA()){
						k = k+1;
						helpEdge = new Edge(mergeList.get(j));
						helpEdge.changeEdgeNumber(direction);
						sList.add(helpEdge);
					}
					else{//if from Q
						if(mergeList.get(j).getEdgeNumber() == qi.getEdgeNumber()){
							if(i==0){
//								System.out.println("go to step 4");
								position = j;
								break;
							}
							else{
								k = k+1;
								sList.add(qi);
								if(qi.isTurningPoint()){
									direction = -1*direction;
//									System.out.println("changing direction");
//									System.out.println(direction);
									
								}
//								System.out.println("repeat step 3");
								position = j;
								break;
							}
						}
					}
					position = j;
				}
				
			}
			else if(direction<0){
				System.out.println("position: " + position);
				//moving backwards through mergeList looking for Qi
				for(int j = position; j >=0 ;j--){
					if(!mergeList.get(j).isPolygonA()){
						k = k+1;
						helpEdge = new Edge(mergeList.get(j));
						helpEdge.changeEdgeNumber(direction);
						sList.add(helpEdge);
					}
					else{//if from Q
						if(mergeList.get(j).getEdgeNumber() == qi.getEdgeNumber()){
							if(i==0){
//								System.out.println("go to step 4");
								position = j;
								break;
							}
							else{
								k = k+1;
								sList.add(qi);
								if(qi.isTurningPoint()){
									
									direction= -1*direction;
//									System.out.println("changing direction");
//									System.out.println(direction);
								}
//								System.out.println("repeat step 3");
								position = j;
								break;
							}
						}
					}
					position = j;
				}
				if(position == 0){
					position = mergeList.size()-1;
				}
				
				
			}
			
		}while(i!=0);
		
		Edge startingEdge = rList.get(0);
		
		i = 0;
		
		System.out.println("rList");
		printSimpleEdgeList(rList);
		
		System.out.println("sList");
		printSimpleEdgeList(sList);
		
		
		for(Edge e: sList){
			if(!e.isPolygonA()&&e.getEdgeNumber()==startingEdge.getEdgeNumber()){
				break;
			}
			i++;
		}
		//i is the position of r0 in the mergeList
		
		int j = 0;
		int next = 1;
		
		direction = 1;
		List<Edge> seqList = new ArrayList<>();
		Edge si = sList.get(i);
		seqList.add(si);
		
		while(j<sList.size()){
			i = i+1;
			if(i>k)i=0;
			
			si = sList.get(i);
			
			if(si.isPolygonA()){
				j = j+1;
				seqList.add(si);
				
				if(si.isTurningPoint()){
					direction*=-1;
					next = next+direction;
					if(next<0){
						System.out.println("next is: " + next);
						next = rList.size()-1;
						System.out.println("changed to: " + next);
					}
					if(next>rList.size()-1){
						System.out.println("next is: " + next);
						next = 0;
						System.out.println("changed to: " + next);
					}
				}
				if(si.getEdgeNumber() == rList.get(next).getEdgeNumber()){
					j = j+1;
					seqList.add(si);
					next = next+direction;
					if(next<0){
						System.out.println("next is: " + next);
						next = rList.size()-1;
						System.out.println("changed to: " + next);
					}
					if(next>rList.size()-1){
						System.out.println("next is: " + next);
						next = 0;
						System.out.println("changed to: " + next);
					}
				}
			}
			
		}
		System.out.println("seqList");
		printSimpleEdgeList(seqList);
		return seqList;
		
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
		
		double clockwiseValue = 0;
		
		double xDiff;
		double ySum;
		
		Coordinate endCoord;
		Coordinate beginCoord;
		//If the result is positive the curve is clockwise, if it's negative the curve is counter-clockwise.
		for(Edge e: edgeList){
			//Sum over the edges, (x2-x1)(y2+y1). If the result is positive the curve is clockwise, if it's negative the curve is counter-clockwise.
			endCoord = e.getStartPoint();
			beginCoord = e.getEndPoint();
			xDiff = endCoord.getxCoord() - beginCoord.getxCoord();
			ySum = endCoord.getyCoord() + beginCoord.getyCoord();
			clockwiseValue += xDiff*ySum;
		}
		
		if(clockwiseValue < 0) return true;
		else return false;

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
	
private static List<Edge> Mink2(List<Edge> qList, List<Edge> rList, boolean positive) {
		
		System.out.println();
		System.out.println("qList");
		printSimpleEdgeList(qList);
		System.out.println("rList");
		printSimpleEdgeList(rList);
		
		List<Edge> mergeList = new ArrayList <> ();
		mergeList.addAll(qList);
		mergeList.addAll(rList);
		Collections.sort(mergeList, new EdgeAngleComparator());
	
		System.out.println("mergelist:");
		printSimpleEdgeList(mergeList);
		
		List<Edge> sList = new ArrayList<>();
		
		int i = 0;
		int direction = 1;
		
		Edge helpEdge;
		Edge qi;
		boolean qiFound = false;
		int position = 0;
//		if(qList.get(0).isTurningPoint()){
//			direction = -1;
//		}
		sList.add(qList.get(0));
		
		
		do{
			i = (i+1)%qList.size();
			qi = qList.get(i);
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
						sList.add(helpEdge);
					}
					else{//if from Q
						if(mergeList.get(position).getEdgeNumber() == qi.getEdgeNumber()){
							qiFound = true;
							if(i==0){
//								System.out.println("go to step 4");
								
							}
							else{
								
								
								if(qi.isTurningPoint()){
									boolean sameAngleB = false;
									if(Math.round(Math.toDegrees(mergeList.get((position+1)%mergeList.size()).getEdgeAngle()))==
											Math.round(Math.toDegrees(mergeList.get(position).getEdgeAngle()))){
										if(!mergeList.get((position+1)%mergeList.size()).isPolygonA()){
											helpEdge = new Edge(mergeList.get((position+1)%mergeList.size()));
											helpEdge.changeEdgeNumber(direction);
											sList.add(helpEdge);
//											System.out.println("extra add");
//											System.out.println("inserting edge in sList: b" + helpEdge.getEdgeNumber());
											sameAngleB = true;
										}
									}
									
//									System.out.println("inserting edge in sList: a" + qi.getEdgeNumber());
									sList.add(qi);
									
									direction = -1*direction;
									
//									System.out.println("qi is turningpoint: " + direction );
									if(sameAngleB){
										helpEdge = new Edge(mergeList.get((position+1)%mergeList.size()));
										helpEdge.changeEdgeNumber(direction);
										sList.add(helpEdge);
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
					}
					else{//if from Q
						if(mergeList.get(position).getEdgeNumber() == qi.getEdgeNumber()){
							qiFound = true;
							if(i==0){
//								System.out.println("go to step 4");
							}
							else{
								
								if(qi.isTurningPoint()){
									
									boolean sameAngleB = false;
									if(position-1 > 0){
										if(Math.round(Math.toDegrees(mergeList.get(position-1).getEdgeAngle()))==
												Math.round(Math.toDegrees(mergeList.get(position).getEdgeAngle()))){
											if(!mergeList.get(position-1).isPolygonA()){

												helpEdge = new Edge(mergeList.get(position-1));
												helpEdge.changeEdgeNumber(direction);
//												System.out.println("extra add");
//												System.out.println("inserting edge in sList: b" + helpEdge.getEdgeNumber());
												sList.add(helpEdge);
												sameAngleB = true;
											}
										}
									}
									else if(Math.round(Math.toDegrees(mergeList.get(mergeList.size()-1).getEdgeAngle()))==
										Math.round(Math.toDegrees(mergeList.get(position).getEdgeAngle()))){
										if(!mergeList.get(mergeList.size()-1).isPolygonA()){
											
											helpEdge = new Edge(mergeList.get(position-1));
											helpEdge.changeEdgeNumber(direction);
//											System.out.println("extra add");
//											System.out.println("inserting edge in sList: b" + helpEdge.getEdgeNumber());
											sList.add(helpEdge);
											sameAngleB = true;
										}
									}
									
//									System.out.println("inserting edge in sList: a" + qi.getEdgeNumber());
									sList.add(qi);
									
									direction= -1*direction;
									
//									System.out.println("qi is turningpoint: " + direction );
									if(sameAngleB){
										if(position-1 > 0){
											helpEdge = new Edge(mergeList.get(position-1));
											helpEdge.changeEdgeNumber(direction);
//											System.out.println("extra add");
//											System.out.println("inserting edge in sList: b" + helpEdge.getEdgeNumber());
											sList.add(helpEdge);
										}
										else{
											helpEdge = new Edge(mergeList.get(mergeList.size()-1));
											helpEdge.changeEdgeNumber(direction);
//											System.out.println("extra add");
//											System.out.println("inserting edge in sList: b" + helpEdge.getEdgeNumber());
											sList.add(helpEdge);
										}
										
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
			
		}while(i!=0);
		
		Edge startingEdge = rList.get(0);
		
		i = 0;
		
		System.out.println("rList");
		printSimpleEdgeList(rList);
		
		System.out.println("sList");
		printSimpleEdgeList(sList);
		
		
		for(Edge e: sList){
			if(!e.isPolygonA()&&e.getEdgeNumber()==startingEdge.getEdgeNumber()){
				break;
			}
			i++;
		}
		//i is the position of r0 in the mergeList
		
		int j = 0;
		int next = 1;
		
		direction = 1;
		List<Edge> seqList = new ArrayList<>();
		Edge si = sList.get(i);
		seqList.add(si);
		
		while(j<sList.size()-1){
			i = (i+1)%sList.size();
			
			si = sList.get(i);
			
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
				seqList.add(si);
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
				
			}
			
		}
		System.out.println("seqList");
		printSimpleEdgeList(seqList);
		return seqList;
		
	}

	private static List<List<Edge>> makeTrackLineTrips(List<Edge> mList) {
		
		int i = 0; //number of Minkowski sums obtained
		int j = 0; //number of track line trips with nj number of edges in track line trip j;
		int k = 0; //index of each track line trip
		List<List<Edge>> trackLineTripList = new ArrayList <>();
		List<Edge> trackLineTrip = new ArrayList<>();
		trackLineTripList.add(trackLineTrip);
		Edge tjk;
		boolean positiveFound = true;
		
		while(positiveFound){
			while(i<mList.size() && mList.get(i).getEdgeNumber()<0){
				i++;
			}
			if(i<mList.size()){//gevonden
				trackLineTrip = new ArrayList<>();
				trackLineTripList.add(trackLineTrip);
				tjk = mList.get(i);
				trackLineTrip.add(tjk);
				i++;
				k++;
				
				while(i< mList.size() && mList.get(i).getEdgeNumber()>0 && correspondsToTrackLine(mList.get(i), trackLineTripList)){
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

	private static boolean correspondsToTrackLine(Edge edge, List<List<Edge>> trackLineTripList) {
		for(List<Edge> trip: trackLineTripList){
			for(Edge e: trip){
				if(e.getEdgeNumber()==edge.getEdgeNumber() && e.isPolygonA() == edge.isPolygonA()){
					return true;
				}
			}
		}
		return false;
	}

	private static List<List<Edge>> boundarySearch(List<List<Edge>> trackLineTripList){
		List<List<Coordinate>> tripIntersectionPoints = new ArrayList<>();
		List<Coordinate> intersectionPoints;
		
		List<List<Edge>> tripIntersectionEdges = new ArrayList<>();
		List<Edge> intersectionEdges;
		
		List<List<Boolean>> tripIntersectionSigns = new ArrayList<>();
		List<Boolean> intersectionSigns;
		
		List<Edge>trackLineTrip;
		List<Edge>trackLineTripI;
		List<Edge>trackLineTripJ;

		for(int j = 0; j < trackLineTripList.size(); j++){
			intersectionPoints = new ArrayList<>();
			intersectionEdges = new ArrayList<>();
			intersectionSigns = new ArrayList<>();
			for(int i = 0; i< trackLineTripList.size();i++){
				if(i!=j){
					
					trackLineTripJ = trackLineTripList.get(j);
					trackLineTripI = trackLineTripList.get(i);
					
					for(Edge edgeJ: trackLineTripJ){
						for(Edge edgeI: trackLineTripI){
							System.out.println("looking for intersection");
							if(edgeJ.testIntersect(edgeI)){
								intersectionPoints.add(edgeJ.calcIntersection(edgeI));
								intersectionEdges.add(edgeJ);
								System.out.println("edge added to intersectionEdges");
								if(edgeI.getStartPoint().dFunction(edgeJ)>0){
									intersectionSigns.add(true);
								}
								else{
									intersectionSigns.add(false);
								}
							}
						}
					}
				}
			}
			tripIntersectionPoints.add(intersectionPoints);
			tripIntersectionEdges.add(intersectionEdges);
			tripIntersectionSigns.add(intersectionSigns);
		}
		
		//step 2
		List<List<Edge>> fragmentList = new ArrayList<>();
		List<Edge> fragment;
		int k = 0;
		for(int i = 0; i<tripIntersectionSigns.size(); i++){
			fragment = new ArrayList<>();
			trackLineTrip = trackLineTripList.get(i);
			intersectionSigns = tripIntersectionSigns.get(i);
			intersectionEdges = tripIntersectionEdges.get(i);
			intersectionPoints = tripIntersectionPoints.get(i);
			for(int j = 0; j< intersectionSigns.size()-1;j++){
				if(intersectionSigns.get(j)==false){
					if(intersectionSigns.get((j+1))==true){
						System.out.println("track line trip closed");
						Edge trimmedEdge = new Edge(intersectionEdges.get(j));
						trimmedEdge.setStartPoint(intersectionPoints.get(j));
						fragment.add(trimmedEdge);
						k=0;
						while(!trackLineTrip.get(k).equals(intersectionEdges.get(j))){
							//TODO: modulo may not be necessary
							k = (k+1);
							
						}
						k = (k+1)%trackLineTrip.size();
						while(!trackLineTrip.get(k).equals(intersectionSigns.get((j+1)%intersectionSigns.size()))){
							fragment.add(trackLineTrip.get(k));
							k = (k+1);
						}
						trimmedEdge = new Edge(trackLineTrip.get(k));
						trimmedEdge.setEndPoint(intersectionPoints.get(j+1));
						fragment.add(trimmedEdge);
					}
				}
			}
			fragmentList.add(fragment);
		}
		System.out.println("starting step 3");
		//step 3
		int numberOfFragments = fragmentList.size();
		List<Edge> fragI;
		List<Edge> fragJ;
		List<List<Edge>> cycleList = new ArrayList<>();
		List<Edge> cycle;
		while(numberOfFragments>0){
//			System.out.println(numberOfFragments);
			for (int i = 0; i < fragmentList.size(); i++) {
				for (int j = 0; j < fragmentList.size(); j++) {
					if(i!=j){
						fragI = fragmentList.get(i);
						fragJ = fragmentList.get(j);
						if(fragI.size()!=0&&fragJ.size()!=0){
							System.out.println("beiden verschillend van 0");
							if(fragI.get(0).equals(fragJ.get(fragJ.size()-1))){
								if(fragI.get(fragI.size()-1).equals(fragJ.get(0))){
									
									fragJ.remove(0);
									fragJ.remove(fragJ.size()-1);
									
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
									fragI.remove(0);
									fragJ.addAll(fragI);
									
									fragI.clear();
									numberOfFragments--;
								}
							}
							else if(fragI.get(fragI.size()-1).equals(fragJ.get(0))){
								fragJ.remove(0);
								fragI.addAll(fragJ);
								
								fragJ.clear();
								numberOfFragments--;
							}
						}
					}
				}
			}
		}
		return cycleList;
	}
}
