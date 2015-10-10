
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
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
    private Edge[] outerPolygonEdges;
    
    private Coordinate[][] holes;
    private Edge[][] holeEdges;
    
    private Scanner input;
    
    private double biggestX = 0;
    private double biggestY = 0;
    
    //constructor reads file to create a polygon
    MultiPolygon(File file) throws FileNotFoundException{
        
        //TODO: catch wrong input file format error
        
        input = new Scanner(file);
        nHoles = input.nextInt();
        int nPoints = input.nextInt();//number of points of the polygon that is currently being read (this value changes when holes are read too)

        //used for autoscaling in drawtool
        double readX;
        double readY;
        
        
        outerPolygon = new Coordinate[nPoints];
        holes = new Coordinate[nHoles][];
        
        
        
        for(int i=0; i < nPoints; i++){
        	
        	readX = input.nextDouble();
        	readY = input.nextDouble();
            outerPolygon[i] = new Coordinate(readX, readY);
            if(Math.abs(readX) > biggestX)biggestX = Math.abs(readX);
            if(Math.abs(readY) > biggestY)biggestY = Math.abs(readY);
            
        }
        
        
        for(int i = 0; i < nHoles; i++){
        	
            nPoints = input.nextInt();
            holes[i] = new Coordinate[nPoints];
            
            for(int j = 0 ; j < nPoints; j++){
                holes[i][j] = new Coordinate(input.nextDouble(), input.nextDouble());
            }
        }
        input.close();
        
        //now we will make the edge arrays
      //this array contains the same information but in pairs of coordinates to allow an easier way to use edges
        outerPolygonEdges = new Edge[outerPolygon.length];
        holeEdges = new Edge[nHoles][];
        
        
        for(int i = 0; i< outerPolygon.length; i++){
        	if(i == outerPolygon.length-1){
        		outerPolygonEdges[i] = new Edge(outerPolygon[i], outerPolygon[0]);
        	}
        	else{
        		System.out.println(i);
        		outerPolygonEdges[i] = new Edge(outerPolygon[i], outerPolygon[i+1]);
        	}
        }
        
        for(int i = 0; i< nHoles; i++){
        	holeEdges[i] = new Edge[holes[i].length];
        	for(int j = 0; j < holes[i].length; j++){
        		
        		if(j == holes[i].length-1){
            		holeEdges[i][j] = new Edge(holes[i][j], holes[i][0]);
            	}
            	else{
            		holeEdges[i][j] = new Edge(holes[i][j], holes[i][j+1]);
            	}
        		
        	}
        	
        }
        
    }

    

	public double getBiggestX() {
		return biggestX;
	}

	public void setBiggestX(double biggestX) {
		this.biggestX = biggestX;
	}

	public double getBiggestY() {
		return biggestY;
	}

	public void setBiggestY(double biggestY) {
		this.biggestY = biggestY;
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
    
	public int getnHoles() {
		return nHoles;
	}

	public void setnHoles(int nHoles) {
		this.nHoles = nHoles;
	}

	public Edge[] getOuterPolygonEdges() {
		return outerPolygonEdges;
	}

	public void setOuterPolygonEdges(Edge[] outerPolygonEdges) {
		this.outerPolygonEdges = outerPolygonEdges;
	}

	public Edge[][] getHoleEdges() {
		return holeEdges;
	}

	public void setHoleEdges(Edge[][] holeEdges) {
		this.holeEdges = holeEdges;
	}
    //methods for GUI----------------------------------------------------------------------------------------------------------------
    //converting outer polygon into polygon for UI
    public Polygon makeOuterPolygon(double xSize, double ySize, double sizeFactor){
        
        Polygon polygon = new Polygon();
        for(Coordinate coord : outerPolygon){
            //add 300 to coord to move axis
        	
            polygon.getPoints().add(sizeFactor*coord.getxCoord()+xSize/2);
            //yCoord*-1 to invert to normal axis and add 700 to move axis
            polygon.getPoints().add(-1*sizeFactor*coord.getyCoord()+ySize/2);
        }
        
        return polygon;
    }
    
    //converting holes into polygons for UI
    public Polygon[] makeHoles(double xSize, double ySize, double sizeFactor) {
        Polygon[] polyHoles = new Polygon[nHoles];
        for(int i = 0; i < nHoles; i++){
            polyHoles[i] = new Polygon();
            for(Coordinate coord : holes[i]){
                polyHoles[i].getPoints().add(sizeFactor*coord.getxCoord()+xSize/2);
                //yCoord*-1 to invert to normal axis
                polyHoles[i].getPoints().add(-1*sizeFactor*coord.getyCoord()+ySize/2);
            }
        }
        return polyHoles;
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
    //---------------------------------------------------------------------------------------------------------------------------------------
  
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
            //if the y-value of coord is higher then the current topCoord, replace topCoord
            if(coord.getyCoord()>topCoord.getyCoord())topCoord = coord;
        }
        return topCoord;
    }
    
    public List<TouchingEdgePair> findTouchingEdges(MultiPolygon orbPoly) {
    	
    	List<TouchingEdgePair> touchingEdges = new ArrayList<>();
        TouchingEdgePair tEP;
        //the outer polygon of the orbiting multipolygon
        Edge[] orbOuterPolygonEdges = orbPoly.getOuterPolygonEdges();
        //check for every point of orb if it touches an edge of stat
        for(Edge orbEdge: orbOuterPolygonEdges){
        	
            for(Edge statEdge: outerPolygonEdges){
                
                tEP = statEdge.touching(orbEdge);
                if(tEP != null)touchingEdges.add(tEP);
            }
        }
        return touchingEdges;
        
    }

    public List<TouchingEdgePair> oudeVersiefindTouchingEdges(MultiPolygon orbPoly) {
    	
    	List<TouchingEdgePair> touchingEdges = new ArrayList<>();
        Edge statEdge;
        Edge orbEdge;
        TouchingEdgePair tEP;
        //the outer polygon of the orbiting multipolygon
        Coordinate[] orbOuterPolygon = orbPoly.getOuterPolygon();
        //check for every point of orb if it touches an edge of stat
        for(int k = 0; k < orbOuterPolygon.length; k++){
        	
        	//the edge of the orbiting Polygon we are going to use for testing if 2 edges touch
        	if(k == 0){
        		orbEdge = new Edge(orbOuterPolygon[orbOuterPolygon.length-1], orbOuterPolygon[k], false);	
        	}
        	else orbEdge = new Edge(orbOuterPolygon[k-1], orbOuterPolygon[k], false);
        	
            for(int i =0; i< outerPolygon.length-1; i++){
            	//the edge of the stationary Polygon we are going to use for testing if 2 edges touch
                if(i==0){
                	statEdge = new Edge(outerPolygon[orbOuterPolygon.length-1], outerPolygon[i], true);
                }
                else statEdge = new Edge(outerPolygon[i-1], outerPolygon[i], true);
                
                tEP = statEdge.touching(orbEdge);
                if(tEP != null)touchingEdges.add(tEP);
            }
        }
        return touchingEdges;
        
    }

	public void isStationary() {
		for(Edge e: outerPolygonEdges){
			e.setStationary(true);
		}
		for(Edge[] eA: holeEdges){
			for(Edge e: eA){
				e.setStationary(true);
			}
		}
		
	}

}

