import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class Main {

	private static final String directoryT1 = "Terashima1Polygons\\";
	private static final String directoryT2 = "Terashima2Polygons\\";
	
	public static void main(String[] args) throws FileNotFoundException {
		
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
		
		List<MultiPolygon> polygonsT1 = new ArrayList<>();
		List<MultiPolygon> polygonsT2 = new ArrayList<>();
		
		File folderT1 = new File(directoryT1);
		File[] listOfFilesT1 = folderT1.listFiles();
		
		File folderT2 = new File(directoryT2);
		File[] listOfFilesT2 = folderT2.listFiles();
		
		System.out.println("T1 to multipolys");
		int n = 0;
		int numberOfPolys = 1000;
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
		
		System.out.println("T2 done");
		
		long startTime;
		long endTime;

		long duration;

		List<Long> durations = new ArrayList<>();
		int totalIts = 0;

		

		startTime = System.currentTimeMillis();

		int i = 0;
		System.out.println("Minkowski tests");
		
//		Minkowski.generateMinkowskiNFP(new MultiPolygon(simple2Data), new MultiPolygon(simple1Data)); //correct
//		totalIts++;
//		Minkowski.generateMinkowskiNFP(new MultiPolygon(simple1Data), new MultiPolygon(simple2Data)); //correct
//		totalIts++;
//		Minkowski.generateMinkowskiNFP(new MultiPolygon(mink1Data), new MultiPolygon(mink2Data)); //correct
//		totalIts++;
//		Minkowski.generateMinkowskiNFP(new MultiPolygon(mink2Data), new MultiPolygon(mink1Data)); //correct
//		totalIts++;
//		Minkowski.generateMinkowskiNFP(new MultiPolygon(convex1Data), new MultiPolygon(convex2Data)); //correct
//		totalIts++;
//		Minkowski.generateMinkowskiNFP(new MultiPolygon(concave1Data), new MultiPolygon(concave2Data)); // correct
//		totalIts++;
//		Minkowski.generateMinkowskiNFP(new MultiPolygon(concave2Data), new MultiPolygon(concave1Data)); //correct
//		totalIts++;
//		Minkowski.generateMinkowskiNFP(new MultiPolygon(concave1Data), new MultiPolygon(convex2Data)); //correct
//		totalIts++;
//		Minkowski.generateMinkowskiNFP(new MultiPolygon(convex1Data), new MultiPolygon(concave2Data)); //correct
//		totalIts++;
//		Minkowski.generateMinkowskiNFP(new MultiPolygon(interlockingConc1Data), new MultiPolygon(triangleData));
//		totalIts++;
//		Minkowski.generateMinkowskiNFP(new MultiPolygon(interlockingConc2Data),new MultiPolygon(interlockingConc3Data));
//		totalIts++;
//		Minkowski.generateMinkowskiNFP(new MultiPolygon(holes2Data), new MultiPolygon(holes1Data)); //correct 2richtingen (zonder gaten)
//		totalIts++;
		
		int j = 0;
		
		i=0;
		
		int findIDimensionPoly = 2;
		int findJDimensionPoly = 232;
		
		boolean testSpecific = true;
		
		for (MultiPolygon stat: polygonsT2) {
			if(i > findIDimensionPoly && testSpecific)break;
			j = 0;
			for (MultiPolygon orb: polygonsT2) {
						if(testSpecific){
							if(i == findIDimensionPoly && j == findJDimensionPoly){
				
								nfpList.add(Minkowski.generateMinkowskiNFP(new MultiPolygon(stat), new MultiPolygon(orb)));
								break;
							}
						}
						else{
							System.out.println("["+i+"]["+j+"]");
							nfpList.add(Minkowski.generateMinkowskiNFP(new MultiPolygon(stat), new MultiPolygon(orb)));
						}
				totalIts++;
				j++;
		    }
			i++;
			
		}
		System.out.println("current total: " + totalIts);
		System.out.println("fails: " + Minkowski.numberOfFails);
		System.out.println("infinite stuck: " + Minkowski.numberStuckInfinite);
			
		endTime = System.currentTimeMillis();
		duration = (endTime - startTime);
		
		System.out.println("duration: " + duration);
		
		drawTool.launchDrawer(args);
	}

}
