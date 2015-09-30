
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import javafx.scene.shape.Polygon;

/**
 *
 * @author Stiaan
 * 
 * this class contains a polygon to be used to generate the no-fit polygon
 * the polygon exists of coordinates and can have holes
 */
public class MultiPolygon{
    
    private int nHoles; //the number of holes
    private Coordinate[] outerPolygon; //the polygon that envelops the holes
    private Coordinate[][] holes;
    private Scanner input;
    
    private int xAxisOffset = 750;
    private int yAxisOffset = 500;
    private double sizeFactor = 0.5;
    
    //constructor reads file to create a polygon
    MultiPolygon(File file) throws FileNotFoundException{
        
        //TODO: catch wrong input file format error
        
        input = new Scanner(file);
        nHoles = input.nextInt();
        int nPoints = input.nextInt();//number of points of the polygon

        outerPolygon = new Coordinate[nPoints];
        holes = new Coordinate[nHoles][];
        
        for(int i=0; i < nPoints; i++){
            outerPolygon[i] = new Coordinate(input.nextDouble(), input.nextDouble());
        }
        
        
        for(int i = 0; i < nHoles; i++){
            nPoints = input.nextInt();
            holes[i] = new Coordinate[nPoints];
            for(int j = 0 ; j < nPoints; j++){
                holes[i][j] = new Coordinate(input.nextDouble(), input.nextDouble());
            }
        }
        input.close();
    }
    
    
    //converting outer polygon into polygon for UI
    public Polygon makeOuterPolygon(){
        
        Polygon polygon = new Polygon();
        for(Coordinate coord : outerPolygon){
            //add 300 to coord to move axis
            polygon.getPoints().add(sizeFactor*coord.getxCoord()+xAxisOffset);
            //yCoord*-1 to invert to normal axis and add 700 to move axis
            polygon.getPoints().add(-1*sizeFactor*coord.getyCoord()+yAxisOffset);
        }
        
        return polygon;
    }
    
    //converting holes into polygons for UI
    public Polygon[] makeHoles() {
        Polygon[] polyHoles = new Polygon[nHoles];
        for(int i = 0; i < nHoles; i++){
            polyHoles[i] = new Polygon();
            for(Coordinate coord : holes[i]){
                polyHoles[i].getPoints().add(sizeFactor*coord.getxCoord()+xAxisOffset);
                //yCoord*-1 to invert to normal axis
                polyHoles[i].getPoints().add(-1*sizeFactor*coord.getyCoord()+yAxisOffset);
            }
        }
        return polyHoles;
    }
    
    
    //print out the data of a polygon
    public void printPolygonData(){
        System.out.println("outer polygon number of points: " + outerPolygon.length);
        for (Coordinate coord : outerPolygon) {
            coord.printCoordinate();
        }
        for(int i = 0; i < nHoles; i++){
            System.out.println("hole "+(i+1) +" number of points: "+ holes[i].length);
            for (Coordinate holeCoord : holes[i]) {
                holeCoord.printCoordinate();
            }
        }
    }

    public Coordinate[] getOuterPolygon() {
        return outerPolygon;
    }

    public void setOuterPolygon(Coordinate[] outerPolygon) {
        this.outerPolygon = outerPolygon;
    }

    public Coordinate[][] getHoles() {
        return holes;
    }

    public void setHoles(Coordinate[][] holes) {
        this.holes = holes;
    }
    
    public void translate(double x, double y){
        for(Coordinate coord: outerPolygon){
            coord.move(x, y);
        }
        
        
        for(Coordinate[] hole: holes){
            for(Coordinate coord: hole){
                coord.move(x, y);
            }
        }
    }

    public Coordinate findBottomCoord() {
        Coordinate bottomCoord = outerPolygon[0];
        for(Coordinate coord: outerPolygon){
            //if the y-value of coord is lower then the current bottomCoord, replace bottomCoord
            if(coord.getyCoord()<bottomCoord.getyCoord())bottomCoord = coord;
        }
        return bottomCoord;
    }
    
    public Coordinate findTopCoord() {
        Coordinate topCoord = outerPolygon[0];
        for(Coordinate coord: outerPolygon){
            //if the y-value of coord is higer then the current topCoord, replace topCoord
            if(coord.getyCoord()>topCoord.getyCoord())topCoord = coord;
        }
        return topCoord;
    }

    public TouchingEdges[] findTouchingEdges(MultiPolygon orbPoly) {
        
        //check for every point of orb if it touches an edge of stat
        for(Coordinate coord: orbPoly.getOuterPolygon()){
            
            //first check for the edge connecting the last coordinate of the array with the first
            // to avoid need of if statement
            Coordinate lastCoord = outerPolygon[outerPolygon.length-1];
            if(coord.dFunction(outerPolygon[outerPolygon.length-1], outerPolygon[0])==0){
                
                
                
            }
            
            for(int i =1; i< outerPolygon.length-1; i++){
                
                
            }
        }
        
        return null;
    }

}

