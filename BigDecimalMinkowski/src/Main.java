import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class Main {

	private static final String directoryT1 = "Terashima1Polygons\\";
	private static final String directoryT2 = "Terashima2Polygons\\";
	
	public static void main(String[] args) throws FileNotFoundException {
		
		System.out.println("Big Decimal Mink");
		
		Minkowski.printMinkData = true;
		Minkowski.printEdgeListData = true;
		Minkowski.printBoundaryData = true;
		Minkowski.drawFigures = false;
		Minkowski.drawNFP = true;
		Minkowski.handleError = true;
		
		boolean testMass = true;
		
		boolean testSpecific = false;
		int findIDimensionPoly = 98;
		int findJDimensionPoly = 49;
		int numberOfPolys = 100;
		
		if(testSpecific){
			Minkowski.printMinkData = true;
			Minkowski.printEdgeListData = true;
			Minkowski.printBoundaryData = true;
			Minkowski.drawFigures = true;
			Minkowski.drawNFP = true;
		}
		if(testMass && !testSpecific){
			Minkowski.printMinkData = false;
			Minkowski.printEdgeListData = false;
			Minkowski.printBoundaryData = false;
			Minkowski.drawFigures = false;
			Minkowski.drawNFP = false;
		}
		
		
		DrawJavaFX drawTool = new DrawJavaFX();

		
		List<NoFitPolygon> nfpList = new ArrayList<>();
		
		File simple1Data = new File("SimpleFig1.txt");
		File simple2Data = new File("SimpleFig2.txt");
		
		File mink1Data = new File("Mink1.txt");
		File mink2Data = new File("Mink2.txt");
		
		File convex1Data = new File("Convex1.txt");
		File convex2Data = new File("Convex2.txt");
		
		File concave1Data = new File("Concave1.txt");
		File concave2Data = new File("Concave2.txt");
		
		File rectangle1Data = new File("Rectangle1.txt");
		File block1Data = new File("Block1.txt");
		File block2Data = new File("Block2.txt");
		
		File puzzle1Data = new File("Puzzle4.txt");
		File puzzle2Data = new File("Puzzle2.txt");
		File puzzle3Data = new File("Puzzle3.txt");
		
		File sawtooth1Data = new File("Sawtooth1.txt");
		File sawtooth2Data = new File("Sawtooth2.txt");
		
		File clockwiseData = new File("clockwise.txt");
		
		File interlockingConc1Data = new File("interCav1.txt");
		File triangleData = new File("triangle1.txt");

		File interlockingConc2Data = new File("interCav2.txt");
		File interlockingConc3Data = new File("interCav3.txt");
		
		File holes1Data = new File("Holes1.txt");
		File holes2Data = new File("Holes2.txt");
		
		File jigsaw1Data = new File("Jigsaw1.txt");
		File jigsaw2Data = new File("Jigsaw2.txt");
		
		List<MultiPolygon> polygonsT1 = new ArrayList<>();
		List<MultiPolygon> polygonsT2 = new ArrayList<>();
		
		File folderT1 = new File(directoryT1);
		File[] listOfFilesT1 = folderT1.listFiles();
		
		File folderT2 = new File(directoryT2);
		File[] listOfFilesT2 = folderT2.listFiles();
		if(testMass){
			System.out.println("T1 to multipolys");
			int n = 0;
			
			for(File polygonT1: listOfFilesT1){
				if(n == numberOfPolys)break;
				polygonsT1.add(new MultiPolygon(polygonT1));
				n++;
			}
			n = 0;
			System.out.println("T1 done");
			System.out.println("T2 to multipolys");
			for(File polygonT2: listOfFilesT2){
				if(n == numberOfPolys)break;
				polygonsT2.add(new MultiPolygon(polygonT2));
				n++;
			}
	//		
			System.out.println("T2 done");
		}
		long startTime;
		long endTime;

		long duration;

		List<Long> durations = new ArrayList<>();
		int totalIts = 0;

		

		startTime = System.currentTimeMillis();

		int i = 0;
		
		if(!testMass){
			Minkowski.generateMinkowskiNFP(new MultiPolygon(simple2Data), new MultiPolygon(simple1Data)); //correct
			totalIts++;
			Minkowski.generateMinkowskiNFP(new MultiPolygon(simple1Data), new MultiPolygon(simple2Data)); //correct
			totalIts++;
			Minkowski.generateMinkowskiNFP(new MultiPolygon(mink1Data), new MultiPolygon(mink2Data)); //correct
			totalIts++;
			Minkowski.generateMinkowskiNFP(new MultiPolygon(mink2Data), new MultiPolygon(mink1Data)); //correct
			totalIts++;
			Minkowski.generateMinkowskiNFP(new MultiPolygon(convex1Data), new MultiPolygon(convex2Data)); //correct
			totalIts++;
			Minkowski.generateMinkowskiNFP(new MultiPolygon(concave1Data), new MultiPolygon(concave2Data)); // correct
			totalIts++;
			Minkowski.generateMinkowskiNFP(new MultiPolygon(concave2Data), new MultiPolygon(concave1Data)); //correct
			totalIts++;
			Minkowski.generateMinkowskiNFP(new MultiPolygon(concave1Data), new MultiPolygon(convex2Data)); //correct
			totalIts++;
			Minkowski.generateMinkowskiNFP(new MultiPolygon(convex1Data), new MultiPolygon(concave2Data)); //correct
			totalIts++;
			Minkowski.generateMinkowskiNFP(new MultiPolygon(interlockingConc1Data), new MultiPolygon(triangleData));//correct
			totalIts++;
			Minkowski.generateMinkowskiNFP(new MultiPolygon(interlockingConc2Data),new MultiPolygon(interlockingConc3Data));//correct
			totalIts++;
			Minkowski.generateMinkowskiNFP(new MultiPolygon(holes2Data), new MultiPolygon(holes1Data)); //correct 2richtingen (zonder gaten)
			totalIts++;
			Minkowski.generateMinkowskiNFP(new MultiPolygon(rectangle1Data), new MultiPolygon(rectangle1Data));//correct
			totalIts++;
			Minkowski.generateMinkowskiNFP(new MultiPolygon(puzzle1Data), new MultiPolygon(puzzle2Data));
			totalIts++;
			Minkowski.generateMinkowskiNFP(new MultiPolygon(jigsaw1Data), new MultiPolygon(jigsaw2Data));
			totalIts++;	
//			Minkowski.generateMinkowskiNFP(new MultiPolygon(puzzle3Data), new MultiPolygon(block1Data));//correct
//			totalIts++;
//			Minkowski.generateMinkowskiNFP(new MultiPolygon(sawtooth1Data), new MultiPolygon(sawtooth2Data));//correct
//			totalIts++;
//			Minkowski.generateMinkowskiNFP(new MultiPolygon(triangleData), new MultiPolygon(interlockingConc2Data));//correct
//			totalIts++;
//			Minkowski.generateMinkowskiNFP(new MultiPolygon(triangleData), new MultiPolygon(interlockingConc3Data));//correct
//			totalIts++;
//			Minkowski.generateMinkowskiNFP(new MultiPolygon(interlockingConc3Data), new MultiPolygon(interlockingConc2Data));//correct
//			totalIts++;
//			Minkowski.generateMinkowskiNFP(new MultiPolygon(holes1Data), new MultiPolygon(block2Data));//correct
//			totalIts++;	
		}
				
		
//		Edge testEdge = new Edge(new Coordinate(124, -138), new Coordinate(398, -268));
//		Coordinate testCoordinate = new Coordinate(326, -234);
//		System.out.println(testCoordinate.dFunction(testEdge));
//		System.out.println(testCoordinate.shortestDistanceToEdge(testEdge));
				
		int j = 0;
		
		i=0;
		
		//polygonsT2.remove(2);
		if(testMass){
			
			
			for (MultiPolygon stat: polygonsT2) {
				if(i > findIDimensionPoly && testSpecific)break;
				j = 0;
				for (MultiPolygon orb: polygonsT2) {
							if(testSpecific){
								if(i == findIDimensionPoly && j == findJDimensionPoly){
					
									nfpList.add(Minkowski.generateMinkowskiNFP(new MultiPolygon(stat), new MultiPolygon(orb)));
									totalIts++;
									break;
								}
							}
							else{
								System.out.println("["+i+"]["+j+"]");
								nfpList.add(Minkowski.generateMinkowskiNFP(new MultiPolygon(stat), new MultiPolygon(orb)));
								totalIts++;
							}
					
					j++;
			    }
				i++;
				
			}
		}
		System.out.println("current total: " + totalIts);
		System.out.println("fails: " + Minkowski.numberOfFails);
		System.out.println("infinite stuck: " + Minkowski.numberStuckInfinite);
			
		endTime = System.currentTimeMillis();
		duration = (endTime - startTime);
		
		System.out.println("duration: " + duration);
		
		if(Minkowski.drawFigures || Minkowski.drawNFP){
			drawTool.launchDrawer(args);
		}
	}

}
