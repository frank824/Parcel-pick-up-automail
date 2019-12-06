package mycontroller;

import com.badlogic.gdx.utils.Queue;
import tiles.MapTile;
import tiles.TrapTile;
import utilities.Coordinate;
import world.World;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Endstart
 * Date: 2019-10-22
 * Time: 20:43
 */
public class VisitRecordUtil {
	
	
    private static Set<Coordinate> visitSet = new HashSet<>();   // walked cell
    public static Map<Coordinate, MapTile> seeMap = new HashMap<>();  // all cells being seen around
    public static Map<Coordinate, MapTile> wallMap = new HashMap<>(); // walls can be seen
    public static Map<Coordinate, MapTile> exitMap = new HashMap<>(); // exits can be seen
    public static Map<Coordinate, MapTile> parcelMap = new HashMap<>();  // parcels can be seen
    
    
    public static boolean visit(Coordinate coordinate) {
        if (!isVisited(coordinate)) {
            visitSet.add(coordinate);
            return true;
        }
        return false;
    }

    public static boolean isVisited(Coordinate coordinate) {
        return visitSet.contains(coordinate);
    }

    public static Coordinate searchNextToTarget(Coordinate from, Coordinate target) {
        Queue<Coordinate> queue = new Queue<Coordinate>();
        Map<Coordinate, Boolean> visited = new HashMap<>();
        Map<Coordinate, Coordinate> parentMap = new HashMap<>();
        for (Coordinate coordinate : seeMap.keySet()) {
            visited.put(coordinate, false);
            parentMap.put(coordinate, null);
        }

        visited.put(from, true);
        queue.addLast(from);

        while (queue.size != 0) {
            Coordinate current = queue.removeFirst();

            List<Coordinate> neighbourList = getNeighbourCoordinates(current);
            List<Coordinate> canThroughAdjacent = new ArrayList<>();
            for (Coordinate coordinate : neighbourList) {
                if (!wallMap.containsKey(coordinate) && seeMap.containsKey(coordinate)) {
                    canThroughAdjacent.add(coordinate);
                }
            }

            for (Coordinate adj : canThroughAdjacent) {
                if (!visited.get(adj)) {
                    visited.put(adj, true);
                    parentMap.put(adj, current);
                    queue.addLast(adj);
                    if (adj.equals(target)) {
                        while (parentMap.get(current) != null) {
                            Coordinate parent = parentMap.get(current);
                            if (parent.equals(from)) {
                                //System.out.println(current);
                                return current;
                            }
                            current = parent;
                        }
                        return null;
                    }
                }
            }
        }
        //System.out.println("cannot find valid way: from=" + from + ", to" + target);
        return null;
    }

    // recognize the map around car
    public static void recognizeAroundView(HashMap<Coordinate, MapTile> mapView) {
        for (Coordinate coordinate : mapView.keySet()) {
            MapTile tile = mapView.get(coordinate);
            if (coordinate.x < World.MAP_WIDTH && coordinate.x >= 0 && coordinate.y >= 0 && coordinate.y < World.MAP_HEIGHT) {
                if (tile.isType(MapTile.Type.WALL)) {
                    wallMap.put(coordinate, tile);
                    seeMap.put(coordinate, tile);
                } else if (tile.isType(MapTile.Type.ROAD)) {
                    if (parcelMap.containsKey(coordinate)) {
                        parcelMap.remove(coordinate);
                    }
                    seeMap.put(coordinate, tile);
                } else if (tile.isType(MapTile.Type.TRAP)) {
                    TrapTile trapTile = (TrapTile) tile;
                    if ("parcel".equalsIgnoreCase(trapTile.getTrap())) {
                        parcelMap.put(coordinate, tile);
                        seeMap.put(coordinate, tile);
                    }
                } else if (tile.isType(MapTile.Type.FINISH)) {
                    exitMap.put(coordinate, tile);
                    seeMap.put(coordinate, tile);
                }
            }
        }
    }

    public static List<Coordinate> getNeighbourCoordinates(Coordinate current) {
        List<Coordinate> neighbours = new ArrayList<>();
        neighbours.add(new Coordinate(current.x, current.y + 1));
        neighbours.add(new Coordinate(current.x + 1, current.y));
        neighbours.add(new Coordinate(current.x - 1, current.y));
        neighbours.add(new Coordinate(current.x, current.y - 1));
        return neighbours;
    }

    public static int calcDistance(Coordinate coordinateOne, Coordinate coordinateTwo) {
        double x2 = Math.pow(coordinateTwo.x - coordinateOne.x, 2);
        double y2 = Math.pow(coordinateTwo.y - coordinateOne.y, 2);
        return (int) Math.sqrt(x2 + y2);
    }

    public static Map<Coordinate, MapTile> getWallMap() {
        return Collections.unmodifiableMap(wallMap);
    }

    public static Map<Coordinate, MapTile> getExitMap() {
        return Collections.unmodifiableMap(exitMap);
    }

    public static Map<Coordinate, MapTile> getParcelMap() {
        return Collections.unmodifiableMap(parcelMap);
    }

    public static Map<Coordinate, MapTile> getSeeMap() {
        return Collections.unmodifiableMap(seeMap);
    }


}
