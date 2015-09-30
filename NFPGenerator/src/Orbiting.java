import java.util.List;

public class Orbiting {
    
    public static MultiPolygon generateNFP(MultiPolygon statPoly, MultiPolygon orbPoly){
        
        Coordinate bottomCoord = statPoly.findBottomCoord();
        Coordinate topCoord = orbPoly.findTopCoord();
        
        orbPoly.translate(bottomCoord.getxCoord()-topCoord.getxCoord(), bottomCoord.getyCoord()-topCoord.getyCoord());
        
        List<TouchingEdgePair> touchingEdges = statPoly.findTouchingEdges(orbPoly);
        
        for(TouchingEdgePair tEP:touchingEdges){
        	tEP.print();
        }
        
        //TODO resultaat hier zetten
        return null;
    }
}
