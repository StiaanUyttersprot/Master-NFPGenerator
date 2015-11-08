package tashimaReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

public class Main {
	
	private static String directory = "D:\\CloudStorage\\Google Drive\\_Master\\Terashima1Polygons\\";
	
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		
		File folder = new File("Terashima1");
		File[] listOfFiles = folder.listFiles();
		
		for (File file : listOfFiles) {
		    if (file.isFile()) {
		        parseFileToPolygons(file);
		    }
		}
		
		
	}

	private static void parseFileToPolygons(File file) throws FileNotFoundException, UnsupportedEncodingException {
		
		Scanner sc = new Scanner(file);
		
		int nPolys = sc.nextInt();
		//skip surface to place on
		sc.nextInt();
		sc.nextInt();
		
		int nPointsPoly;
		
		for(int i = 0; i< nPolys; i++){
			String part = file.getName().substring(0, file.getName().length() -4);
			String fileName = part + i + ".txt";
			System.out.println(fileName);
			String path  = directory + fileName;
			PrintWriter writer = new PrintWriter(path, "UTF-8");
			writer.println(0);
			nPointsPoly = sc.nextInt();
			writer.println(nPointsPoly);
			for(int j = 0; j< nPointsPoly; j++){
				writer.println(sc.nextInt() + " " + sc.nextInt());
			}
			writer.close();
			
		}
		
		
		
	}
}
