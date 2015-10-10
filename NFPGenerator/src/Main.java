import java.io.File;
import java.io.FileNotFoundException;

/**
 *
 * @author Stiaan
 */
public class Main{

    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException {
        
    	DrawJavaFX drawTool = new DrawJavaFX();
    	
//        File polygonData = new File("Convex1.txt");
//        File polygonData2 = new File("Convex2.txt");
        
        File polygonData = new File("Polygon1.txt");
        File polygonData2 = new File("Polygon2.txt");
        
        MultiPolygon mPolygon = new MultiPolygon(polygonData);
        MultiPolygon mPolygon2 = new MultiPolygon(polygonData2);
        
        
        //mPolygon.printPolygonData();
        
        MultiPolygon[] mPolygons = new MultiPolygon[2];
        mPolygons[0] = mPolygon;
        mPolygons[1] = mPolygon2;
        
        //------------------------------------------------------------------------------
        
        //checking methods of calculations with coordinates
        /*
        Coordinate coord1 = new Coordinate(-2, -2);
        Coordinate coord2 = new Coordinate(4, 3);
        Coordinate coord3 = new Coordinate(5,7);
        double dist = coord1.distanceTo(coord2);
        double angle = coord2.calculateAngle(coord1, coord3);
        //angle += coord3.calculateAngle(coord1, coord2);
        //angle += coord1.calculateAngle(coord3, coord2);//sum 180° correct
        
        double dVal = coord2.dFunction(coord1, coord3);
        */
        
//        System.out.println(coord1.calculateVectorAngle());
        
//        System.out.println(dist);
//        System.out.println(Math.toDegrees(angle));
//        System.out.println(dVal);
        
        
        
        
        Orbiting.generateNFP(mPolygons[0], mPolygons[1]);
        
        PolygonPairStages.addPolygonPair(mPolygons);
        
        drawTool.launchDrawer(args);
    }
}
