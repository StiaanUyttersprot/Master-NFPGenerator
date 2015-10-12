import java.util.List;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * @author Stiaan
 * this class supports drawing of multipolygons in javaFX
 * in its current state it only allows the user to draw once
 * trying to call the draw method multiple times in the main method will result in errors
 */
public class DrawJavaFX extends Application{
	
    //starting the application
    @Override
    public void start(Stage primaryStage)
    {
    	double screenSizeX = java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth();
//    	List<Stage> polyPairStageList = PolygonPairStages.drawPolygonPairs();
    	double stageWidth = PolygonPairStages.getSceneSizeX();
    	double stageHeight = PolygonPairStages.getSceneSizeY();
    	int stageNumber = 0;
    	int heigthPlaceLine = 0;
    	int borderOffset = 30;
//    	for(Stage stage : polyPairStageList){
//    		if(stageWidth*(stageNumber+1)> screenSizeX){
//    			stageNumber = 0;
//    			heigthPlaceLine++;
//    		}
//    		stage.setX(stageWidth*(stageNumber));
//    		stage.setY((borderOffset+stageHeight)*heigthPlaceLine);
//    		stage.show();
//    		
//    		stageNumber++;
//    		
//    	}
    	
    	List<Stage> nfpStages = NoFitPolygonStages.drawNFPFigures();
    	stageWidth = NoFitPolygonStages.getSceneSizeX();
    	stageHeight = NoFitPolygonStages.getSceneSizeY();
    	for(Stage stage : nfpStages){
    		//stageNumber + 1 to make sure it is inside the screen
    		if(stageWidth*(stageNumber+1)> screenSizeX){
    			stageNumber = 0;
    			heigthPlaceLine++;
    		}
    		stage.setX(stageWidth*(stageNumber));
    		stage.setY((borderOffset+stageHeight)*heigthPlaceLine);
    		stage.show();
    		
    		stageNumber++;
    		
    	}
    }
    
    public void launchDrawer(String[] args){
    	launch(args);
    }
      
}