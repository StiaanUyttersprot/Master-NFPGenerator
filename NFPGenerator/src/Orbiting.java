
public class Orbiting {
    
    public static MultiPolygon generateNFP(MultiPolygon statPoly, MultiPolygon orbPoly){
        
        Coordinate bottomCoord = statPoly.findBottomCoord();
        Coordinate topCoord = orbPoly.findTopCoord();
        
        orbPoly.translate(bottomCoord.getxCoord()-topCoord.getxCoord(), bottomCoord.getyCoord()-topCoord.getyCoord());
        
        statPoly.findTouchingEdges(orbPoly);
        
        
        
        //TODO resultaat hier zetten
        return null;
    }
}
