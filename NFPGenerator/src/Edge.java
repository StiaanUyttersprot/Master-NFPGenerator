
/**
 *
 * @author Stiaan
 */
public class Edge {
    boolean stationary = true;
    Coordinate startPoint;
    Coordinate endPoint;
    
    Edge(Coordinate s, Coordinate e, boolean stat){
        startPoint = s;
        endPoint = e;
        stationary = stat;
    }
}
