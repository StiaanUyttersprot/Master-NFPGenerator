import javafx.application.Application;
import javafx.stage.Stage;

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

        PolygonPairStages.drawPolygonPairs();
        
    }
    
    public void launchDrawer(String[] args){
    	launch(args);
    }
      
}