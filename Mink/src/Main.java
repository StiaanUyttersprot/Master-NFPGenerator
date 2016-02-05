import java.io.File;
import java.io.FileNotFoundException;

public class Main {

	public static void main(String[] args) throws FileNotFoundException {
		
		DrawJavaFX drawTool = new DrawJavaFX();

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
		
//		Minkowski.generateMinkowskiNFP(new MultiPolygon(simple2Data), new MultiPolygon(simple1Data)); // correct
		
//		Minkowski.generateMinkowskiNFP(new MultiPolygon(simple1Data), new MultiPolygon(simple2Data)); //correct
		
		Minkowski.generateMinkowskiNFP(new MultiPolygon(mink1Data), new MultiPolygon(mink2Data)); // correct
		
//		Minkowski.generateMinkowskiNFP(new MultiPolygon(mink2Data), new MultiPolygon(mink1Data));
		
//		Minkowski.generateMinkowskiNFP(new MultiPolygon(convex1Data), new MultiPolygon(convex2Data)); //correct
		
//		Minkowski.generateMinkowskiNFP(new MultiPolygon(concave1Data), new MultiPolygon(concave2Data)); // correct
		
//		Minkowski.generateMinkowskiNFP(new MultiPolygon(concave2Data), new MultiPolygon(concave1Data));
		
//		Minkowski.generateMinkowskiNFP(new MultiPolygon(concave1Data), new MultiPolygon(convex2Data)); //correct
		
//		Minkowski.generateMinkowskiNFP(new MultiPolygon(convex1Data), new MultiPolygon(concave2Data));
		
//		Minkowski.generateMinkowskiNFP(new MultiPolygon(interlockingConc1Data), new MultiPolygon(triangleData));

//		Minkowski.generateMinkowskiNFP(new MultiPolygon(holes2Data), new MultiPolygon(holes1Data)); //correct 2richtingen (zonder gaten)
		
		drawTool.launchDrawer(args);
	}

}
