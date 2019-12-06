package mycontroller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tiles.MapTile;
import utilities.Coordinate;

public class GetNextToExplorerStrategy implements Strategy {

	@Override
	public Coordinate move(Coordinate current) {
		List<Coordinate> neighbourList = VisitRecordUtil.getNeighbourCoordinates(current);
        for (Coordinate neighbour : neighbourList) {
            if (!VisitRecordUtil.isVisited(neighbour) && !VisitRecordUtil.getWallMap().containsKey(neighbour)) {
                //System.out.println("to be neighbour  " + neighbour);
                return neighbour;
            }
        }
        
        Map<Coordinate, MapTile> unexplored = new HashMap<Coordinate, MapTile>();
        Map<Coordinate, MapTile> seen = VisitRecordUtil.getSeeMap();
        for (Coordinate coord : VisitRecordUtil.getSeeMap().keySet()) {
            if (!VisitRecordUtil.isVisited(coord) && !VisitRecordUtil.getWallMap().containsKey(coord)) {
                unexplored.put(coord, seen.get(coord));
            }
        }

        // the nearest coordinate
        int min = Integer.MAX_VALUE;
        Coordinate goal = null;
        for (Coordinate coordinate : unexplored.keySet()) {
            int dist = VisitRecordUtil.calcDistance(current, coordinate);
            if (dist < min) {
                Coordinate tmp = VisitRecordUtil.searchNextToTarget(current, coordinate);
                if (tmp != null) {
                    min = dist;
                    goal = tmp;
                }
            }
        }

        if (goal != null) {
            //System.out.println("to be explored " + goal);
        }
        return goal;
    }

}
