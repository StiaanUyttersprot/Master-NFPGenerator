import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Orbiting {
    
    public static MultiPolygon generateNFP(MultiPolygon statPoly, MultiPolygon orbPoly){
        
        Coordinate bottomCoord = statPoly.findBottomCoord();
        Coordinate topCoord = orbPoly.findTopCoord();
        
        statPoly.isStationary();
        orbPoly.translate(bottomCoord.getxCoord()-topCoord.getxCoord(), bottomCoord.getyCoord()-topCoord.getyCoord());
        
		//---------------------------------------------------------------------------------------------------------------------
        //detecting touching edges
        List<TouchingEdgePair> touchingEdgeList = statPoly.findTouchingEdges(orbPoly);
        
        for(TouchingEdgePair tEP:touchingEdgeList){
        	tEP.calcFeasibleAngleRange();
        }
        
        System.out.println("touching edges: " + touchingEdgeList.size());
        for(TouchingEdgePair tEP:touchingEdgeList){
        	tEP.print();
        }
        
		//---------------------------------------------------------------------------------------------------------------------
        //create potential translation vectors
        //List<Coordinate> potentialVectorList = new ArrayList<>();
        
        Set<Coordinate> potentialVectorList = new HashSet<>();
        Coordinate potVector;
        
        for(TouchingEdgePair tEP: touchingEdgeList){
        	potVector = tEP.getPotentialVector();
        	if(potVector != null&&!potentialVectorList.contains(potVector)){
        		potVector.calculateVectorAngle();
        		potentialVectorList.add(potVector);
        	}
        }
        
        System.out.println();
        System.out.println("Potential vectors: " + potentialVectorList.size());
        for(Coordinate vect:potentialVectorList){
        	vect.printCoordinate();
        }
        
        
        //----------------------------------------------------------------------------------------------------------------------
        //find the feasible vectors
        boolean feasibleVector;
        List<Coordinate> feasibleVectorList = new ArrayList<>();

        for(Coordinate vector : potentialVectorList){
        	int i = 0; 
        	feasibleVector = true;
        	while(feasibleVector && i < touchingEdgeList.size()){
        		TouchingEdgePair tEP = touchingEdgeList.get(i);
        		if(!tEP.isFeasibleVector(vector))feasibleVector = false;
        		i++;
        		
        	}
        	if(feasibleVector){
        		feasibleVectorList.add(vector);
        	}
        	
        }
        
        
        
        System.out.println();
        System.out.println("Feasible vectors: " + feasibleVectorList.size());
        for(Coordinate vect:feasibleVectorList){
        	vect.printCoordinate();
        }
        
        

        
        return null;//TODO resultaat hier zetten
    }
}
