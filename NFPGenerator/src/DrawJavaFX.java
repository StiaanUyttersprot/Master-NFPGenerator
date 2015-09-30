import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;

/**
 * @author Stiaan
 * this class supports drawing of multipolygons in javaFX
 * in its current state it only allows the user to draw once
 * trying to call the draw method multiple times in the main method will result in errors
 */
public class DrawJavaFX extends Application{
    
    //polygons that will be drawn
    private static MultiPolygon[] multipolygons;
    private boolean launched = false;
    
    //these lines will form the x and y axis
    private Line xAxis = new Line(0,500,10000,500);
    private Line yAxis = new Line(750,0,750,10000);
    
    //starting the application
    @Override
    public void start(Stage primaryStage)
    {
        
        primaryStage.setTitle("Polygon");
        Group multiPolygonGroup = new Group();
        
        drawGroup(multiPolygonGroup, primaryStage);
        primaryStage.show();
    }
    
        
    //method is called when one mpolygon needs to be drawn
    public void draw(String[] args, MultiPolygon multiPoly)
    {
        multipolygons = new MultiPolygon[1];
        multipolygons[0] = multiPoly;
        
        //check if an application has already been launched
        if(!launched){
            launch(args);
            launched = true;
        }
        else{
            System.out.println("not possible to start another application");
        }
    }

    //method is called when multiple mpolygons need to be drawn
    public void draw(String[] args, MultiPolygon[] multiPoly)
    {
        //
        multipolygons = multiPoly;
        
        //check if an application has already been launched
        if(!launched){
            launch(args);
            launched = true;
        }
        else{
            System.out.println("not possible to start another application");
        }
    }
    

    
    private void makeScene(Group group, MultiPolygon mPolygon) {
        Polygon polygon = mPolygon.makeOuterPolygon();
        
        polygon.setFill(Color.SKYBLUE);
        polygon.setStrokeWidth(1);
        polygon.setStroke(Color.BLACK);
        
        group.getChildren().add(polygon);
        
        Polygon[] holes = mPolygon.makeHoles();
        
        for(Polygon hole: holes){
            hole.setFill(Color.GREY);
            hole.setStrokeWidth(1);
            hole.setStroke(Color.BLACK);
            group.getChildren().add(hole);
        }
    }

    private void drawGroup(Group multiPolygonGroup, Stage stage) {
        Scene scene = new Scene(multiPolygonGroup, 1500, 1000, Color.GREY);
        
        multiPolygonGroup.getChildren().add(xAxis);
        multiPolygonGroup.getChildren().add(yAxis);
        
        for(MultiPolygon mp: multipolygons){
            makeScene(multiPolygonGroup, mp);
        }
        
        stage.setScene(scene);
    }
}