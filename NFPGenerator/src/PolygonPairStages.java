import java.util.ArrayList;
import java.util.List;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;

/**
 * @author Stiaan
 * this class is used to prepare the stages for polygon pairs for the GUI
 * the values and methods are static to be able to add polygon pairs from every location in the code
 * and draw them in the drawtool without having to give an object to the drawtool
 */

public class PolygonPairStages {

	static int aantalPolygonPairStages = 0;
	static List<MultiPolygon[]> MultiPolygonsToDraw = new ArrayList<>();
	
	static double sceneSizeX = 800;
	static double sceneSizeY = 800;
	
	private static Line xAxis = new Line(0,sceneSizeY/2,10000,sceneSizeY/2);
    private static Line yAxis = new Line(sceneSizeX/2,0,sceneSizeX/2,10000);
	
	public static void addPolygonPair(MultiPolygon[] multiPolyPair){
		aantalPolygonPairStages++;
		MultiPolygonsToDraw.add(multiPolyPair);
		
	}

	public static void drawPolygonPairs(){
		
		for(MultiPolygon[] multiPolys: MultiPolygonsToDraw){
			
			drawPair(multiPolys);
			
		}

	}

	private static void drawPair(MultiPolygon[] multiPolys) {
		
		Stage stage = new Stage();
		
		Group multiPolygonPairGroup = new Group();
		Scene scene = new Scene(multiPolygonPairGroup, sceneSizeX, sceneSizeY, Color.GREY);
        
		multiPolygonPairGroup.getChildren().add(xAxis);
		multiPolygonPairGroup.getChildren().add(yAxis);
        
		double biggestXCoordValue = multiPolys[0].getBiggestX() + multiPolys[1].getBiggestX();//biggest value of x and y coords of the polygons, used for autoscaling
		double biggestYCoordValue = multiPolys[0].getBiggestY() + multiPolys[1].getBiggestY();;
		double biggestValue = Math.max(biggestXCoordValue, biggestYCoordValue);
		
//		if(multiPolys[0].getBiggestX()>multiPolys[1].getBiggestX())biggestXCoordValue = multiPolys[0].getBiggestX();
//		else biggestXCoordValue = multiPolys[1].getBiggestX();
//        
//		if(multiPolys[0].getBiggestY()>multiPolys[1].getBiggestY())biggestYCoordValue = multiPolys[0].getBiggestY();
//		else biggestYCoordValue = multiPolys[1].getBiggestY();
		
//		if(biggestXCoordValue > biggestYCoordValue)biggestValue = biggestXCoordValue;
//		else biggestValue = biggestYCoordValue;
		
		
		makeScene(multiPolygonPairGroup, multiPolys[0], 0, biggestValue);
        makeScene(multiPolygonPairGroup, multiPolys[1],1, biggestValue);
        
        stage.setScene(scene);
        stage.show();
	}
	
	private static void makeScene(Group group, MultiPolygon mPolygon, int color, double biggestValue) {
		
		//sceneSize divided by 2 because x and y axis are in the middle
		double resizeFactor = sceneSizeY/biggestValue/2;
		
        Polygon polygon = mPolygon.makeOuterPolygon(sceneSizeX, sceneSizeY, resizeFactor);
        
        switch(color){
        case 0: polygon.setFill(Color.SKYBLUE);break;
        case 1: polygon.setFill(Color.RED);break;
        }
        polygon.setStrokeWidth(1);
        polygon.setStroke(Color.BLACK);
        
        group.getChildren().add(polygon);
        
        Polygon[] holes = mPolygon.makeHoles(sceneSizeX, sceneSizeY, resizeFactor);
        
        for(Polygon hole: holes){
            hole.setFill(Color.GREY);
            hole.setStrokeWidth(1);
            hole.setStroke(Color.BLACK);
            group.getChildren().add(hole);
        }   
    }
}
