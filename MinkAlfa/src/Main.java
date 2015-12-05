import java.io.File;
import java.io.FileNotFoundException;

public class Main {

	public static void main(String[] args) throws FileNotFoundException {
		
		DrawJavaFX drawTool = new DrawJavaFX();

		File mink1Data = new File("Mink1.txt");
		File mink2Data = new File("Mink2.txt");
		
		Minkowski.generateMinkowskiNFP(new MultiPolygon(mink1Data), new MultiPolygon(mink2Data));
		
		drawTool.launchDrawer(args);
	}

}
